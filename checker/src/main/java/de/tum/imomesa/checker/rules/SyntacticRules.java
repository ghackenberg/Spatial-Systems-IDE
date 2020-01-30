package de.tum.imomesa.checker.rules;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SyntacticRules {
	
	public static <T> boolean notNull(T value) {
		return value != null;
	}
	
	public static boolean notEmpty(String value) {
		return notNull(value) && !value.contentEquals("");
	}
	
	public static <T> boolean notEmpty(ObservableList<? extends T> list) {
		return list.size() > 0;
	}
	
	public static boolean compatible(Class<?> a, Class<?> b) {
		return a.isAssignableFrom(b);
	}
	
	public static boolean compatible(ObservableList<? extends Class<?>> types, Class<?> source) {
		return types.stream().map(type -> compatible(type, source)).reduce(false, (first, second) -> first || second);
	}
	
	public static boolean compatible(Observation observation, Class<?> source) {
		return observation.getWriteType().stream().map(type -> compatible(type, source)).reduce(false, (first, second) -> first || second);
	}
	
	public static boolean compatible(Observation observation, Observation source) {
		return compatible(observation, source.getReadType());
	}
	
	public static boolean compatible(Observation observation, Expression expression) {
		return observation == null || expression == null || compatible(observation.writeTypeProperty(), expression.getType());
	}
	
	public static boolean compatible(Class<?> type, Observation observation) {
		return observation == null || compatible(type, observation.getReadType());
	}
	
	public static boolean compatible(Class<?> type, Expression expression) {
		return expression == null || compatible(FXCollections.<Class<?>>observableArrayList(Boolean.class), expression.getType());
	}
	
	public static boolean compatible(Class<?> type, ObservableList<? extends Expression> list)  {
		return list.stream().map(expression -> compatible(type, expression)).reduce(true, (first, second) -> first && second);
	}
	
	public static boolean input(Port port) {
		return port.getDirection() == Direction.INPUT.ordinal() || port.getDirection() == Direction.INPUT_OUTPUT.ordinal();
	}
	
	public static boolean output(Port port) {
		return port.getDirection() == Direction.OUTPUT.ordinal() || port.getDirection() == Direction.INPUT_OUTPUT.ordinal();
	}
	
	public static boolean inToOut(StaticChannel channel) {
		Port source = channel.getSource();
		Port target = channel.getTarget();
		
		if (source == null || target == null) {
			return false;
		} else {
			DefinitionComponent sourceComponent = source.getFirstAncestorByType(DefinitionComponent.class);
			DefinitionComponent targetComponent = target.getFirstAncestorByType(DefinitionComponent.class);
			
			return sourceComponent == targetComponent;
		}
	}
	
	public static boolean inToIn(StaticChannel channel) {
		Port source = channel.getSource();
		Port target = channel.getTarget();
		
		if (source == null || target == null) {
			return false;
		} else {
			DefinitionComponent sourceComponent = source.getFirstAncestorByType(DefinitionComponent.class);
			DefinitionComponent targetComponent = target.getFirstAncestorByType(DefinitionComponent.class);
			
			return sourceComponent == targetComponent.getFirstAncestorByType(DefinitionComponent.class);
		}
	}
	
	public static boolean outToOut(StaticChannel channel) {
		Port source = channel.getSource();
		Port target = channel.getTarget();
		
		if (source == null || target == null) {
			return false;
		} else {
			DefinitionComponent sourceComponent = source.getFirstAncestorByType(DefinitionComponent.class);
			DefinitionComponent targetComponent = target.getFirstAncestorByType(DefinitionComponent.class);
			
			return sourceComponent.getFirstAncestorByType(DefinitionComponent.class) == targetComponent;
		}
	}
	
	public static boolean outToIn(StaticChannel channel) {
		Port source = channel.getSource();
		Port target = channel.getTarget();
		
		if (source == null || target == null) {
			return false;
		} else {
			DefinitionComponent sourceComponent = source.getFirstAncestorByType(DefinitionComponent.class);
			DefinitionComponent targetComponent = target.getFirstAncestorByType(DefinitionComponent.class);
			
			return sourceComponent.getFirstAncestorByType(DefinitionComponent.class) == targetComponent.getFirstAncestorByType(DefinitionComponent.class);
		}
	}
	
	public static boolean valid(StaticChannel channel) {
		return inToIn(channel) || outToOut(channel) || inToOut(channel) || outToIn(channel);
	}
	
	public static boolean readable(Element context, Observation observation) {
		if (observation == null) {
			return true;
		}
		
		Observation property = context.getFirstAncestorByType(Observation.class);
		
		if (property != null) {
			return readable(property, observation);
		}
		
		Executable<?, ?> executable = context.getFirstAncestorByType(Executable.class);
		
		if (executable != null) {
			if (executable instanceof Scenario) {
				return readable((Scenario) executable, observation);
			} else if (executable instanceof Monitor) {
				return readable((Monitor) executable, observation);
			} else if (executable instanceof Behavior) {
				return readable((Behavior) executable, observation);
			} else {
				throw new IllegalStateException();
			}
		}
		
		throw new IllegalStateException();
	}
	
	public static boolean writable(Action context, Observation observation) {
		if (observation == null) {
			return true;
		}
		
		Executable<?, ?> executable = context.getFirstAncestorByType(Executable.class);
		
		if (executable instanceof Scenario) {
			return writable((Scenario) executable, observation);
		} else if (executable instanceof Monitor) {
			return writable((Monitor) executable, observation);
		} else if (executable instanceof Behavior) {
			return writable((Behavior) executable, observation);
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean readable(Observation context, Observation observation) {
		return true;
	}
	
	private static boolean readable(Scenario context, Observation observation) {
		if (observation.getFirstAncestorByType(Scenario.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else if (observation instanceof Port) {
				return output((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return output((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean readable(Monitor context, Observation observation) {
		if (observation.getFirstAncestorByType(Monitor.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean readable(Behavior context, Observation observation) {
		if (observation.getFirstAncestorByType(Behavior.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return input((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean writable(Scenario context, Observation observation) {
		if (observation.getFirstAncestorByType(Scenario.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else if (observation instanceof Port) {
				return input((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return input((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean writable(Monitor context, Observation observation) {
		if (observation.getFirstAncestorByType(Monitor.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return false;
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static boolean writable(Behavior context, Observation observation) {
		if (observation.getFirstAncestorByType(Behavior.class) == context) {
			if (observation instanceof Variable) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else if (observation.getFirstAncestorByType(DefinitionComponent.class) == context.getParent()) {
			if (observation instanceof Port) {
				return output((Port) observation);
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
}
