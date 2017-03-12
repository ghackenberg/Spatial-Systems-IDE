package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.simulator.Memory;

public abstract class ExecutableEvaluator<L extends Label, T extends Transition<L>, E extends Executable<L, T>> extends AbstractEvaluator<E> {
	
	private List<PropertyEvaluator> properties = new ArrayList<>();
	private List<VariableEvaluator> variables = new ArrayList<>();
	
	public ExecutableEvaluator(List<Element> context, E executable, Memory memory, int step) { 
		super(context, executable, memory, step);
		
		for (Label label : element.getLabels()) {
			for (Property property : label.getProperties()) {
				properties.add(new PropertyEvaluator(label.append(element.append(context)), property, memory, step));
			}
		}
		for (Variable variable : element.getVariables()) {
			variables.add(new VariableEvaluator(element.append(context), variable, memory, step));
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		for (PropertyEvaluator evaluator : properties) {
			evaluator.prepare();
		}
		for (VariableEvaluator evaluator : variables) {
			evaluator.prepare();
		}
	}
	
	@Override
	public void initialize() throws InterruptedException {
		for (VariableEvaluator evaluator : variables) {
			evaluator.initialize();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.initialize();
		}
	}
	
	@Override
	public void createThread() {
		for (VariableEvaluator evaluator : variables) {
			evaluator.createThread();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.createThread();
		}
	}
	
	@Override
	public void startThread() {
		for (VariableEvaluator evaluator : variables) {
			evaluator.startThread();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.startThread();
		}
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		for (VariableEvaluator evaluator : variables) {
			evaluator.joinThread();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.joinThread();
		}
	}

}
