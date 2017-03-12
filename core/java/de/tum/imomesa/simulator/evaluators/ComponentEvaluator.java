package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Channel;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.ComponentInterface;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.executables.behaviors.Behavior;
import de.tum.imomesa.model.elements.executables.monitors.Monitor;
import de.tum.imomesa.model.elements.ports.Port;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.PortInterface.Direction;
import de.tum.imomesa.model.elements.ports.materials.MaterialBindingPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialEntryPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialExitPort;
import de.tum.imomesa.model.elements.properties.Property;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;

public class ComponentEvaluator extends ComponentInterfaceEvaluator<Component> {

	private List<PortEvaluator<?>> ports = new ArrayList<>();
	private List<PropertyEvaluator> properties = new ArrayList<>();
	private List<MonitorEvaluator> monitors = new ArrayList<>();
	private List<BehaviorEvaluator> behaviors = new ArrayList<>();
	private List<ComponentInterfaceEvaluator<?>> components = new ArrayList<>();
	
	public ComponentEvaluator(List<Element> context, Component component, Memory memory, int step) {
		super(context, component, memory, step);

		for (PortInterface port : component.getPorts()) {
			if (port instanceof MaterialBindingPort) {
				ports.add(new MaterialBindingPortEvaluator(component.append(context), (MaterialBindingPort) port, memory, step));
			}
			else {
				ports.add(new PortEvaluator<Port>(component.append(context), (Port) port, memory, step)); 
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
		for (ComponentInterface child : component.getComponents()) {
			if (child instanceof Component) {
				components.add(new ComponentEvaluator(component.append(context), (Component) child, memory, step));
			}
			else if (child instanceof ComponentProxy) {
				components.add(new ComponentProxyEvaluator(component.append(context), (ComponentProxy) child, memory, step));
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
		
		for (ComponentInterfaceEvaluator<?> evaluator : components) {
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
	public void initialize() throws InterruptedException {
		super.initialize();
		
		memory.setBinding(element.append(context), step, new CopyOnWriteArraySet<>());
		
		for (ComponentInterfaceEvaluator<?> evaluator : components) {
			evaluator.initialize();
		}
		for (MonitorEvaluator evaluator : monitors) {
			evaluator.initialize();
		}
		for (BehaviorEvaluator evaluator : behaviors) {
			evaluator.initialize();
		}
		for (PropertyEvaluator evaluator : properties) {
			evaluator.initialize();
		}
		for (PortEvaluator<?> evaluator : ports) {
			evaluator.initialize();
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
		for (ComponentInterfaceEvaluator<?> evaluator : components) {
			evaluator.createThread();
		}
	}
	
	@Override
	public void startThread() {
		for (ComponentInterfaceEvaluator<?> component : components) {
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
		for (ComponentInterfaceEvaluator<?> component : components) {
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
		for(ComponentInterfaceEvaluator<?> evaluator : components) {
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
		for(PortInterface p : element.getPorts()) {
			if(p instanceof MaterialBindingPort) {
				for(PortInterface pPort : ((MaterialBindingPort)p).getPorts()) {
					if(pPort.getDirection().equals(Direction.INPUT.ordinal())) {
						for(Channel c : pPort.getOutgoing()) {
							c.getTarget().getIncoming().remove(c);
							c.setSource(null);
							c.setTarget(null);
						}
						pPort.getOutgoing().clear();
					}
					else if(pPort.getDirection().equals(Direction.OUTPUT.ordinal())) {
						for(Channel c : pPort.getIncoming()) {
							c.getSource().getOutgoing().remove(c);
							c.setSource(null);
							c.setTarget(null);
						}
						pPort.getIncoming().clear();
					}
					else {
						throw new IllegalStateException();
					}
				}
			}
			else if(p instanceof MaterialEntryPort) {
				Utils.createComponent(element.append(context), (MaterialEntryPort) p, memory, step);
			}
			else if(p instanceof MaterialExitPort) {
				Utils.deleteComponent(element.append(context), (MaterialExitPort) p, memory, step);
			}
		}
	}

}
