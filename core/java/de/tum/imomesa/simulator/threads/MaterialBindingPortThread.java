package de.tum.imomesa.simulator.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.ComponentInterface;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.DynamicChannel;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.PortInterface.Direction;
import de.tum.imomesa.model.elements.ports.materials.MaterialBindingPort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class MaterialBindingPortThread extends AbstractThread<MaterialBindingPort> {
	
	public MaterialBindingPortThread(List<Element> context, MaterialBindingPort port, Memory memory, int step) {
		super(context, port, memory, step);
	}
	
	@Override
	protected void execute() {
		// Try working
		try {
			// Join writing executables
			ThreadManager.getInstance().joinThreads(this, Utils.getWritingExecutableThreads(context, element, memory, step));
			// Check value
			if (!memory.hasValue(element.append(context), step)) {
				// Set material binding port to null
				ObservationDispatcher.getInstance().dispatch(this, context, element, memory, step, null);
			}
			
			// get data
			Object value = memory.getValue(this, element.append(context), step);
			
			// if not bound
			if (value == null || (value instanceof Set && ((Set<?>) value).size() == 0)) {
				// Set nested output ports to null
				for (PortInterface nested : element.getPorts()) {
					if (nested.checkDirection(Direction.OUTPUT)) {
						ObservationDispatcher.getInstance().dispatch(this, element.append(context), nested, memory, step, null);
					}
				}
			}
			// if bound correctly
			else if (value instanceof Set) {
				Set<?> set = (Set<?>) value;
				
				// set bindings to current MaterialBindingPort in Components
				if(element.getBindsVolumes() == true) {
					if (set != null) {
						for(Object current : set) {
							if (current instanceof List) {
								List<?> list = (List<?>) current;
								// Convert to element list
								List<Element> next = new ArrayList<>();
								for (Object temp : list) {
									if (temp instanceof Element) {
										next.add((Element) temp);
									}
									else {
										MarkerManager.get().addMarker(new ErrorMarker(element.append(context), "List item is not an element: " + temp, step));
									}
								}
								// Check for equality of lists
								if (next.size() == list.size()) {
									memory.getBinding(next, step).add(context);
								}
							}
						}
					}
				}
				
				// make channels for all parts in set
				Iterator<?> it = set.iterator();
				
				while(it.hasNext()) {
					Object item = it.next();
					
					if (item instanceof List) {
						List<?> list = (List<?>) item;
						
						// Convert to element list
						List<Element> next_context = new ArrayList<>();
						for (Object temp : list) {
							if (temp instanceof Element) {
								next_context.add((Element) temp);
							}
							else {
								MarkerManager.get().addMarker(new ErrorMarker(element.append(context), "List item is not an element: " + temp, step));
							}
						}
						
						// Check for equality of lists
						if (next_context.size() == list.size()) {
						
							ComponentInterface next_component = null;
							
							if (next_context.size() > 1 && next_context.get(next_context.size() - 2) instanceof ComponentProxy) {
								next_component = (ComponentProxy) next_context.get(next_context.size() - 2);
								next_context = next_context.subList(0, next_context.size() - 1);
							}
							else {
								next_component = (Component) next_context.get(next_context.size() - 1);
							}
						
							// Iterate material binding ports
							for(PortInterface pPort : element.getPorts()) {
								// Interate component ports 
								for(PortInterface pPart : next_component.getPorts()) {
									// Check port name equality
									if(pPort.getReadType().equals(pPart.getReadType()) && pPort.getName().equals(pPart.getName())) {
										
										// Create channel
										DynamicChannel c = new DynamicChannel();
										// Check input port
										if(pPort.getDirection().equals(Direction.INPUT.ordinal())) {
											c.setSource(pPort);
											c.setTarget(pPart);
											c.setSourceContext(element.append(context));
											c.setTargetContext(next_context);
											
											synchronized (pPort.getOutgoing()) {
												pPort.getOutgoing().add(c);
												pPart.getIncoming().add(c);

												if(memory.hasValue(pPort.append(element.append(context)), step)) {
													// Obtain value
													Object temp = memory.getValue(this, pPort.append(element.append(context)), step);
													// Transfer value
													ObservationDispatcher.getInstance().dispatch(this, next_context, pPart, memory, step, temp);
												}
											}
											
											//System.out.println("Create Channel " + pPort.append(element.append(context)) + " -> " + pPart.append(next_context) + " in step " + step);
											
											if(Utils.getIncomingForContext(next_context, pPart).size() > 1) {
												MarkerManager.get().addMarker(new ErrorMarker(next_context, "Too many incoming channels.", step));
											}
										}
										// Check output port
										else if(pPort.getDirection().equals(Direction.OUTPUT.ordinal())) {
											c.setSource(pPart);
											c.setTarget(pPort);
											c.setSourceContext(next_context);
											c.setTargetContext(element.append(context));
											
											synchronized (pPart.getOutgoing()) {
												pPart.getOutgoing().add(c);
												pPort.getIncoming().add(c);
												
												// Check if value is available, but has not been forwarded yet (by port impl).
												if(memory.hasValue(pPart.append(next_context), step) && !memory.hasValue(pPort.append(element.append(context)), step)) {
													// Obtain value
													Object temp = memory.getValue(this, pPart.append(next_context), step);
													// Transfer value
													ObservationDispatcher.getInstance().dispatch(this, element.append(context), pPort, memory, step, temp);
												}
											}
											
											//System.out.println("Create Channel " + pPart.append(next_context) + " -> " + pPort.append(element.append(context)) + " in step " + step);
											
											if(Utils.getIncomingForContext(element.append(context), pPort).size() > 1) {
												MarkerManager.get().addMarker(new ErrorMarker(pPort.append(element.append(context)), "Too many incoming channels.", step));
											}
										}
										else {
											throw new IllegalStateException();
										}
									}
								}
							}
						}
					}
					else {
						MarkerManager.get().addMarker(new ErrorMarker(element.append(context), "Set item is not a list: " + item, step));
					}
				}
			}
		}
		catch (InterruptedException e) {
			MarkerManager.get().addMarker(new ErrorMarker(element.append(context), "Interrupted exception occurred!", step));
		}
	}

}
