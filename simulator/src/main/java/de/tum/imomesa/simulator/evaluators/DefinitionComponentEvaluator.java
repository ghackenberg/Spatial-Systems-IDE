package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.DynamicChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;

public class DefinitionComponentEvaluator extends ComponentEvaluator<DefinitionComponent> {

	private List<PortEvaluator<?>> ports = new ArrayList<>();
	private List<PropertyEvaluator> properties = new ArrayList<>();
	private List<MonitorEvaluator> monitors = new ArrayList<>();
	private List<BehaviorEvaluator> behaviors = new ArrayList<>();
	private List<ComponentEvaluator<?>> components = new ArrayList<>();
	
	public DefinitionComponentEvaluator(List<Element> context, DefinitionComponent component, Memory memory, int step) {
		super(context, component, memory, step);

		for (DefinitionPort port : component.getPorts()) {
			if (port instanceof InteractionMaterialPort) {
				ports.add(new InteractionMaterialPortEvaluator(component.append(context), (InteractionMaterialPort) port, memory, step));
			}
			else {
				ports.add(new PortEvaluator<DefinitionPort>(component.append(context), port, memory, step)); 
			}
		}
		for (Property property : component.getProperties()) {
			properties.add(new PropertyEvaluator(component.append(context), property, memory, step));
		}
		for (Monitor executable : component.getMonitors()) {
			monitors.add(new MonitorEvaluator(component.append(context), executable, memory, step));
		}
		for (Behavior executable : component.getBehaviors()) {
			behaviors.add(new BehaviorEvaluator(component.append(context), executable, memory, step));
		}
		for (Component<?> child : component.getComponents()) {
			if (child instanceof DefinitionComponent) {
				components.add(new DefinitionComponentEvaluator(component.append(context), (DefinitionComponent) child, memory, step));
			}
			else if (child instanceof ReferenceComponent) {
				components.add(new ReferenceComponentEvaluator(component.append(context), (ReferenceComponent) child, memory, step));
			}
			else {
				throw new IllegalStateException();
			}
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		
		memory.initBinding(element.append(context), step);
		
		for (ComponentEvaluator<?> evaluator : components) {
			evaluator.prepare();
		}
		for (MonitorEvaluator evaluator : monitors) {
			evaluator.prepare();
		}
		for (BehaviorEvaluator evaluator : behaviors) {
			evaluator.prepare();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.prepare();
		}
		for (PortEvaluator<?> evaluator : ports) {
			evaluator.prepare();
		}
	}
	
	@Override
	public void createThread() {
		memory.setBinding(element.append(context), step, new CopyOnWriteArraySet<>());
		
		for (PortEvaluator<?> evaluator : ports) {
			evaluator.createThread();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.createThread();
		}
		for (MonitorEvaluator evaluator : monitors) {
			evaluator.createThread();
		}
		for (BehaviorEvaluator evaluator : behaviors) {
			evaluator.createThread();
		}
		for (ComponentEvaluator<?> evaluator : components) {
			evaluator.createThread();
		}
	}
	
	@Override
	public void startThread() {
		for (ComponentEvaluator<?> component : components) {
			component.startThread();
		}
		for (BehaviorEvaluator behavior : behaviors) {
			behavior.startThread();
		}
		for (PortEvaluator<?> port : ports) {
			port.startThread();
		}
		for (MonitorEvaluator monitor : monitors) {
			monitor.startThread();
		}
		for (PropertyEvaluator property : properties) {
			property.startThread();
		}
	}
	
	
	@Override
	public void joinThread() throws InterruptedException {
		for (ComponentEvaluator<?> component : components) {
			component.joinThread();
		}
		for (BehaviorEvaluator behavior : behaviors) {
			behavior.joinThread();
		}
		for (PortEvaluator<?> port : ports) {
			port.joinThread();
		}
		for (MonitorEvaluator monitor : monitors) {
			monitor.joinThread();
		}
		for (PropertyEvaluator property : properties) {
			property.joinThread();
		}
	}
	
	@Override
	public void cleanup() throws InterruptedException {
		for(ComponentEvaluator<?> evaluator : components) {
			evaluator.cleanup();
		}
		for(BehaviorEvaluator evaluator : behaviors) {
			evaluator.cleanup();
		}
		for(MonitorEvaluator evaluator : monitors) {
			evaluator.cleanup();
		}
		for(PortEvaluator<?> evaluator : ports) {
			evaluator.cleanup();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.cleanup();
		}
		
		// update transformations for next step
		Utils.calcTransforms(element, element.append(context), memory, step);
		
		// delete all channels inside MaterialPorts
		for(DefinitionPort port : element.getPorts()) {
			if(port instanceof InteractionMaterialPort) {
				for(DefinitionPort nestedPort : ((InteractionMaterialPort) port).getPorts()) {
					if(nestedPort.getDirection().equals(Direction.INPUT.ordinal())) {
						for(DynamicChannel c : nestedPort.getOutgoingDynamicChannels()) {
							c.getTarget().getIncomingDynamicChannels().remove(c);
							c.setSource(null);
							c.setTarget(null);
						}
						nestedPort.getOutgoingDynamicChannels().clear();
					}
					else if(nestedPort.getDirection().equals(Direction.OUTPUT.ordinal())) {
						for(DynamicChannel c : nestedPort.getIncomingDynamicChannels()) {
							c.getSource().getOutgoingDynamicChannels().remove(c);
							c.setSource(null);
							c.setTarget(null);
						}
						nestedPort.getIncomingDynamicChannels().clear();
					}
					else {
						throw new IllegalStateException();
					}
				}
			}
			else if(port instanceof EntryLifeMaterialPort) {
				Utils.createComponent(element.append(context), (EntryLifeMaterialPort) port, memory, step);
			}
			else if(port instanceof ExitLifeMaterialPort) {
				Utils.deleteComponent(element.append(context), (ExitLifeMaterialPort) port, memory, step);
			}
		}
	}

}
