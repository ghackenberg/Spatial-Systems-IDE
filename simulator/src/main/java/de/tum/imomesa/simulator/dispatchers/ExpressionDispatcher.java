package de.tum.imomesa.simulator.dispatchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.IfClauseExpression;
import de.tum.imomesa.model.expressions.NullExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.booleans.AndExpression;
import de.tum.imomesa.model.expressions.booleans.ConstantExpressionBoolean;
import de.tum.imomesa.model.expressions.booleans.EqualExpression;
import de.tum.imomesa.model.expressions.booleans.GreaterExpression;
import de.tum.imomesa.model.expressions.booleans.NotExpression;
import de.tum.imomesa.model.expressions.booleans.OrExpression;
import de.tum.imomesa.model.expressions.booleans.SmallerExpression;
import de.tum.imomesa.model.expressions.numbers.AverageExpression;
import de.tum.imomesa.model.expressions.numbers.CardinalityExpression;
import de.tum.imomesa.model.expressions.numbers.ConstantExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.DifferenceExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.DivisionExpression;
import de.tum.imomesa.model.expressions.numbers.DurationExpression;
import de.tum.imomesa.model.expressions.numbers.MaximumExpression;
import de.tum.imomesa.model.expressions.numbers.MinimumExpression;
import de.tum.imomesa.model.expressions.numbers.ProductExpression;
import de.tum.imomesa.model.expressions.numbers.RandomExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.StepExpression;
import de.tum.imomesa.model.expressions.numbers.SumExpression;
import de.tum.imomesa.model.expressions.sets.DifferenceExpressionSet;
import de.tum.imomesa.model.expressions.sets.IntersectionExpression;
import de.tum.imomesa.model.expressions.sets.SetConstructorExpression;
import de.tum.imomesa.model.expressions.sets.UnionExpression;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.errors.WrongUseOfDurationExpressionError;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class ExpressionDispatcher
{
	
	private static ExpressionDispatcher instance = new ExpressionDispatcher();
	
	public static ExpressionDispatcher getInstance() {
		return instance;
	}
	
	private ExpressionDispatcher() {
		
	}
	
	public Object dispatch(AbstractThread<?> thread, List<Element> context, Expression expression, Memory memory, Integer step) {
		try {
			if (expression != null) {
				// Obtain method
				Method method = getClass().getMethod("evaluate", AbstractThread.class, List.class, expression.getClass(), Memory.class, Integer.class);
				// Invoke method
				return method.invoke(this, thread, context, expression, memory, step);
			}
			else {
				return null;
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, IfClauseExpression expression, Memory memory, Integer step) {
		if ((Boolean) dispatch(thread, context, expression.getArgumentOne(), memory, step)) {
			return dispatch(thread, context, expression.getArgumentTwo(), memory, step);
		}
		else {
			return dispatch(thread, context, expression.getArgumentThree(), memory, step);
		}
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, NullExpression expression, Memory memory, Integer step) {
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, ObservationExpression expression, Memory memory, Integer step) {
		try {
			// Obtain context
			List<Element> temp = expression.getObservation().resolve(context);
			// Obtain value
			return memory.getValue(thread, expression.getObservation().append(temp), step - expression.getDelay());
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, AndExpression expression, Memory memory, Integer step) {
		for (Expression argument : expression.getArguments()) {
			Object value = dispatch(thread, context, argument, memory, step);
			if (value == null) {
				return null;
			}
			if ((Boolean) value == false) {
				return false;
			}
		}
		return true;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, ConstantExpressionBoolean expression, Memory memory, Integer step) {
		return expression.getValue();
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, EqualExpression expression, Memory memory, Integer step) {
		// Check arguments
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object previous = dispatch(thread, context, iterator.next(), memory, step);
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (previous == null && next != null || previous != null && next == null || previous != null && next != null && !previous.equals(next)) {
					return false;
				}
				previous = next;
			}
		}
		// Return true
		return true;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, GreaterExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object previous = dispatch(thread, context, iterator.next(), memory, step);
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (previous == null) {
					return null;
				}
				if (next == null) {
					return null;
				}
				if (previous instanceof Double && next instanceof Double) {
					if ((Double) previous <= (Double) next) {
						return false;
					}
				}
				else {
					return null;
				}
				previous = next;
			}
		}
		return true;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, NotExpression expression, Memory memory, Integer step) {
		Object value = dispatch(thread, context, expression.getArgument(), memory, step);
		if (value == null) {
			return null;
		}
		return !(Boolean) value;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, OrExpression expression, Memory memory, Integer step) {
		for (Expression argument : expression.getArguments()) {
			Object value = dispatch(thread, context, argument, memory, step);
			if (value != null && (Boolean) value == true) {
				return true;
			}
		}
		return false;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, SmallerExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object previous = dispatch(thread, context, iterator.next(), memory, step);
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (previous == null) {
					return null;
				}
				if (next == null) {
					return null;
				}
				if (previous instanceof Double && next instanceof Double) {
					if ((Double) previous >= (Double) next) {
						return false;
					}
				}
				else {
					return null;
				}
				previous = next;
			}
		}
		return true;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, AverageExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			double result = 0;
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result += (Double) value / expression.getArguments().size();
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, CardinalityExpression expression, Memory memory, Integer step) {
		Object value = dispatch(thread, context, expression.getArgument(), memory, step);
		if (value == null) {
			return null;
		}
		return (double) ((Set<?>) value).size();
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, ConstantExpressionNumber expression, Memory memory, Integer step) {
		return expression.getDoubleValue();
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, DifferenceExpressionNumber expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object first = dispatch(thread, context, iterator.next(), memory, step);
			if (first == null) {
				return null;
			}
			double result = (Double) first;
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (next == null) {
					return null;
				}
				result = result - (Double) next;
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, DivisionExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object first = dispatch(thread, context, iterator.next(), memory, step);
			if (first == null) {
				return null;
			}
			double result = (Double) first;
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (next == null) {
					return null;
				}
				result = result / (Double) next;
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, DurationExpression expression, Memory memory, Integer step) {
		
		try {
			// find first Executable in context
			Executable<?, ?> executable = expression.getFirstAncestorByType(Executable.class);
			
			if (executable == null) {
				MarkerManager.get().addMarker(new WrongUseOfDurationExpressionError(context, step));
				return null;
			}
			
			// get active label of executable out of memory
			Label current_label = null;
			if(executable instanceof Monitor) {
				// current label can be used as the properties depend on this
				current_label = memory.getLabel(context, step);
			}
			else {
				current_label = memory.getLabel(context, step - 1);
			}
			
			
			// check for how long this label has been active
			double counter = 0.;
			
			for (int index = step - 1; index >= 0; index--) {
				if(current_label.equals(memory.getLabel(context, index))) {
					counter++;
				}
				else {
					break;
				}
			}
			
			return counter;
			
		} catch (InterruptedException e) {
			// Should never happen
			throw new IllegalStateException(e);
		}
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, MaximumExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			double result = -Double.MAX_VALUE;
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result = Math.max(result, (Double) value);
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, MinimumExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			double result = Double.MAX_VALUE;
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result = Math.min(result, (Double) value);
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, ProductExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			double result = 1;
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result = result * (Double) value;
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, RandomExpressionNumber expression, Memory memory, Integer step) {
		return Math.random();
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, StepExpression expression, Memory memory, Integer step) {
		return (double) step;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, SumExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			double result = 0;
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result = result + (Double) value;
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, DifferenceExpressionSet expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object first = dispatch(thread, context, iterator.next(), memory, step);
			if (first == null) {
				return null;
			}
			Set<?> result = new HashSet<>((Set<?>) first);
			while (iterator.hasNext()) {
				Object next = dispatch(thread, context, iterator.next(), memory, step);
				if (next == null) {
					return null;
				}
				result.removeAll((Set<?>) next);
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, IntersectionExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Iterator<Expression> iterator = expression.getArguments().iterator();
			Object first = dispatch(thread, context, iterator.next(), memory, step);
			if (first == null) {
				return null;
			}
			Set<Object> result = new HashSet<>((Set<?>) first);
			while (iterator.hasNext()) {
				Object value = dispatch(thread, context, iterator.next(), memory, step);
				if (value == null) {
					return null;
				}
				Set<?> previous = result;
				Set<?> next = (Set<?>) value;
				result = new HashSet<>();
				for (Object element : previous) {
					if (next.contains(element)) {
						result.add(element);
					}
				}
			}
			return result;
		}
		return null;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, SetConstructorExpression expression, Memory memory, Integer step) {
		Set<Object> result = new HashSet<>();
		if (expression.getArguments().size() > 0) {
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result.addAll((Set<?>) value);
			}
		}
		return result;
	}
	
	public Object evaluate(AbstractThread<?> thread, List<Element> context, UnionExpression expression, Memory memory, Integer step) {
		if (expression.getArguments().size() > 0) {
			Set<Object> result = new HashSet<>();
			for (Expression argument : expression.getArguments()) {
				Object value = dispatch(thread, context, argument, memory, step);
				if (value == null) {
					return null;
				}
				result.addAll((Set<?>) value);
			}
			return result;
		}
		return null;
	}
	
}
