package de.tum.imomesa.analyzer.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.errors.BlockedObservationError;
import de.tum.imomesa.simulator.markers.errors.BlockedThreadError;
import de.tum.imomesa.simulator.markers.errors.ConstraintViolatedError;
import de.tum.imomesa.simulator.markers.errors.DeadlockError;
import de.tum.imomesa.simulator.markers.errors.InterruptedExceptionError;
import de.tum.imomesa.simulator.markers.errors.ItemNoElementError;
import de.tum.imomesa.simulator.markers.errors.ItemNoListError;
import de.tum.imomesa.simulator.markers.errors.MaterialPortNotSetError;
import de.tum.imomesa.simulator.markers.errors.PartCollisionError;
import de.tum.imomesa.simulator.markers.errors.TooManyIncomingChannelsError;
import de.tum.imomesa.simulator.markers.errors.WritingValueMoreThanOnceError;
import de.tum.imomesa.simulator.markers.errors.WrongInputKinematicPortError;
import de.tum.imomesa.simulator.markers.errors.WrongUseOfDurationExpressionError;
import de.tum.imomesa.simulator.markers.warnings.NonDeterministicChoiceWarning;

public class Namer {

	public static String convertSpace(Class<?> type) {
		String[] parts = type.getSimpleName().split("(?=\\p{Upper})");

		String name = parts[0];

		for (int i = 1; i < parts.length - 1; i++) {
			name += " " + parts[i].toLowerCase();
		}

		return name;
	}

	public static String convertUnderscore(Class<?> type) {
		String[] parts = type.getSimpleName().split("(?=\\p{Upper})");

		String name = parts[0].toLowerCase();

		for (int i = 1; i < parts.length - 1; i++) {
			name += "_" + parts[i].toLowerCase();
		}

		return name;
	}

	public static String dispatch(Object object) {
		Class<?> iterator = object.getClass();

		while (iterator != null) {
			try {
				Method method = Namer.class.getDeclaredMethod("map", iterator);
				return (String) method.invoke(null, object);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalStateException(e);
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			} catch (NoSuchMethodException e) {
				iterator = iterator.getSuperclass();
			}
		}

		throw new IllegalStateException("Cannot dispatch object: " + object.getClass().getName());
	}

	protected static String map(String string) {
		return string;
	}

	protected static String map(Class<?> feature) {
		return feature.getSimpleName();
	}

	protected static String map(DefinitionComponent component) {
		if (component == null) {
			return "None";
		} else if (component.getName().equals("Pick and place unit")) {
			return "PPU";
		} else if (component.getName().equals("Material sensor")) {
			return "Workpiece sensor";
		} else if (component.getName().equals("Generic sensor")) {
			return "Component sensor";
		} else if (component.getName().equals("White material")) {
			return "White workpiece";
		} else if (component.getName().equals("Gray material")) {
			return "Gray workpiece";
		} else if (component.getName().equals("Black material")) {
			return "Black workpiece";
		} else if (component.getName().equals("Generic cylinder (v1)")) {
			return "Concrete static cylinder";
		} else if (component.getName().equals("Generic cylinder (v2)")) {
			return "Concrete dynamic cylinder (v2)";
		} else if (component.getName().equals("Material actuator")) {
			return "Concrete dynamic cylinder (v1)";
		} else if (component.getName().equals("Generic actuator")) {
			return "Abstract static cylinder";
		} else {
			return component.getName();
		}
	}

	protected static String map(SyntacticMarker marker) {
		Element element = marker.getElement();

		String name = "";

		/*
		 * if (marker instanceof SyntacticError) { name += "Defect"; } else if
		 * (marker instanceof SyntacticWarning) { name += "Deficiency"; } else {
		 * throw new IllegalStateException(
		 * "Syntactic issue type not supported: " +
		 * marker.getClass().getName()); }
		 */

		if (marker.getClass().getSimpleName().contains("Wrong")) {
			name += "Inconsistency";
		} else if (marker.getClass().getSimpleName().contains("NotEnoughArguments")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoVolumes")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("TooMany")) {
			name += "Inconsistency";
		} else if (marker.getClass().getSimpleName().contains("NoInitialLabel")) {
			name += "Fixed-type reference missing";
		} else if (marker.getClass().getSimpleName().contains("NoFinalStep")) {
			name += "Fixed-type reference missing";
		} else if (marker.getClass().getSimpleName().contains("NoObservation")) {
			name += "Variable-type reference missing";
		} else if (marker.getClass().getSimpleName().contains("NoTemplate")) {
			name += "Fixed-type reference missing";
		} else if (marker.getClass().getSimpleName().contains("NoComponent") && element instanceof Project) {
			name += "Fixed-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoComponent")) {
			name += "Fixed-type reference missing";
		} else if (marker.getClass().getSimpleName().contains("NoName")) {
			name += "Attribute missing";
		} else if (marker.getClass().getSimpleName().contains("NoDescription")) {
			name += "Attribute missing";
		} else if (marker.getClass().getSimpleName().contains("NoDefaultExpression")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoExpression")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoGuard")) {
			name += "Fixed-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoLine")) {
			name += "Fixed-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoParent")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoPortImpl")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoTransform")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoVolume")) {
			name += "Variable-type child missing";
		} else if (marker.getClass().getSimpleName().contains("NoPoint")) {
			name += "Fixed-type child missing";
		} else {
			name += marker.getClass().getSimpleName();
		}

		return name;
	}

	protected static String map(SimulationMarker marker) {
		if (marker instanceof ConstraintViolatedError) {
			return "Constraint violation";
		} else if (marker instanceof DeadlockError) {
			return "Generic fixed point";
		} else if (marker instanceof InterruptedExceptionError) {
			return "Interruption";
		} else if (marker instanceof ItemNoElementError) {
			return "Item no element";
		} else if (marker instanceof ItemNoListError) {
			return "Item no list";
		} else if (marker instanceof MaterialPortNotSetError) {
			return "Material port not set";
		} else if (marker instanceof PartCollisionError) {
			return "Part collision";
		} else if (marker instanceof TooManyIncomingChannelsError) {
			return "Non-determinism (multiple channels)";
		} else if (marker instanceof WritingValueMoreThanOnceError) {
			return "Non-determinism (multiple behaviors)";
		} else if (marker instanceof WrongInputKinematicPortError) {
			return "Wrong input kinematic port";
		} else if (marker instanceof WrongUseOfDurationExpressionError) {
			return "Wrong duration expression";
		} else if (marker instanceof NonDeterministicChoiceWarning) {
			return "Non-determinism (multiple transitions)";
		} else if (marker instanceof TimeoutMarker) {
			return "Timeout";
		} else if (marker instanceof BlockedObservationError) {
			return null;
		} else if (marker instanceof BlockedThreadError) {
			return null;
		} else {
			throw new IllegalStateException("Marker type not supported: " + marker.getClass().getName());
		}
	}

	protected static String map(Scenario scenario) {
		// return
		// dispatch(scenario.getFirstAncestorByType(DefinitionComponent.class))
		// + " - " + scenario.getName();

		if (scenario.getName().equals("White material")) {
			return "White workpiece";
		} else if (scenario.getName().equals("Gray material")) {
			return "Gray workpiece";
		} else if (scenario.getName().equals("Black material")) {
			return "Black workpiece";
		} else if (scenario.getName().equals("Random material")) {
			return "Ten random workpieces";
		} else if (scenario.getName().equals("White material at stack")) {
			return "White workpiece @ stack";
		} else if (scenario.getName().equals("White material at stamp")) {
			return "White workpiece @ stamp";
		} else if (scenario.getName().equals("Gray material at stack")) {
			return "Gray workpiece @ stack";
		} else if (scenario.getName().equals("Black material at stack")) {
			return "Gray workpiece @ stack";
		} else if (scenario.getName().equals("Material white")) {
			return "White workpiece";
		} else if (scenario.getName().equals("Material gray")) {
			return "Gray workpiece";
		} else if (scenario.getName().equals("Material black")) {
			return "Black workpiece";
		} else {
			return scenario.getName();
		}
	}

	protected static String map(NamedElement element) {
		return element.toString();
	}

	protected static String map(Element element) {
		return element.getClass().getName().substring("de.tum.imomesa.model.".length());
	}

	public static String map(List<?> list) {
		String result = "";

		for (Object element : list) {
			result += "/" + Namer.dispatch(element);
		}

		return result;
	}

}
