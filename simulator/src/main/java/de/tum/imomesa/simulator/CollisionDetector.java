package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;
import org.eclipse.jetty.util.BlockingArrayQueue;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.errors.PartCollisionError;
import eu.mihosoft.jcsg.CSG;

public class CollisionDetector {

	private static List<List<Element>> combinationPart1 = new ArrayList<List<Element>>();
	private static List<List<Element>> combinationPart2 = new ArrayList<List<Element>>();

	public static void process(DefinitionComponent mainComp, Scenario scenario, Memory memory, int step) throws InterruptedException {
		// reset values
		combinationPart1 = new ArrayList<List<Element>>();
		combinationPart2 = new ArrayList<List<Element>>();

		// Adding
		System.out.println("Adding");

		Map<CSG, List<Element>> map = new HashMap<>();

		// add main component
		System.out.println("Adding main component");
		add(new ArrayList<>(), map, mainComp, memory, step);

		// add material ports of scenario
		System.out.println("Adding scenario");
		add(mainComp.append(new ArrayList<>()), map, scenario, memory, step);

		// add component proxies
		System.out.println("Adding generated components");
		for (ReferenceComponent proxy : memory.getProxy(step)) {
			add(new ArrayList<>(), map, proxy, memory, step);
		}

		memory.setIntersections(step, new HashSet<>());

		// Calculating
		System.out.println("Calculating");

		// Convert to ordered list
		List<Entry<CSG, List<Element>>> entries = new ArrayList<>(map.entrySet());

		// Queue calculations
		class Pair {
			Pair(int first, int second) {
				this.first = first;
				this.second = second;
			}

			int first;
			int second;
		}
		;

		Queue<Pair> queue = new BlockingArrayQueue<>();

		// Outer iteration
		for (int outerIndex = 0; outerIndex < entries.size(); outerIndex++) {
			// Inner iteration
			for (int innerIndex = outerIndex + 1; innerIndex < entries.size(); innerIndex++) {
				queue.add(new Pair(outerIndex, innerIndex));
			}
		}

		long start = System.currentTimeMillis();

		List<Thread> threads = new ArrayList<>();

		for (int processor = 0; processor < Runtime.getRuntime().availableProcessors() - 1; processor++) {
			Thread thread = new Thread(() -> {
				while (true) {
					Pair pair = queue.poll();

					if (pair == null) {
						return;
					}

					Entry<CSG, List<Element>> outerEntry = entries.get(pair.first);
					Entry<CSG, List<Element>> innerEntry = entries.get(pair.second);

					List<Element> listA = outerEntry.getValue();
					List<Element> listB = innerEntry.getValue();

					/*
					 * synchronized (threads) { System.out.println("Comparing " + listA + " and " + listB); }
					 */

					boolean sameComponent = true;

					for (int index = 0; index < Math.min(listA.size(), listB.size()); index++) {
						Element elementA = listA.get(index);
						Element elementB = listB.get(index);

						if (elementA instanceof Component || elementB instanceof Component) {
							if (!elementA.equals(elementB)) {
								sameComponent = false;
								break;
							}
						} else {
							break;
						}
					}

					if (!sameComponent) {
						/*
						 * synchronized (threads) { System.out.println("Not same component!"); }
						 */

						CSG outerCSG = outerEntry.getKey();
						CSG innerCSG = innerEntry.getKey();

						CSG intersection = outerCSG.intersect(innerCSG);

						if (!intersection.getPolygons().isEmpty()) {
							/*
							 * synchronized (threads) { System.out.println("Not empty intersection!"); }
							 */

							// Memorize intersection
							Element first = listA.get(listA.size() - 1);
							Element second = listB.get(listB.size() - 1);

							synchronized (threads) {
								System.out.println("Step " + step + ": collision between " + first.getElementPath().get() + " and " + second.getElementPath().get());
								// System.out.println(first.getElementPath().get() + ": " + (first instanceof Component ? memory.getTransform(listA, step) : memory.getTransform(listA.subList(0, listA.size() - 1), step)));
								// System.out.println(second.getElementPath().get() + ": " + (second instanceof Component ? memory.getTransform(listB, step) : memory.getTransform(listB.subList(0, listB.size() - 1), step)));
							}

							try {
								evaluateCollision(outerEntry, innerEntry, memory, step, intersection);
								evaluateCollision(innerEntry, outerEntry, memory, step, intersection);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});

			thread.start();

			threads.add(thread);
		}

		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		long end = System.currentTimeMillis();

		System.out.println((end - start) + "ms used!");

		System.out.println("Intersections: " + memory.getIntersections(step).size());
	}

	private static synchronized void evaluateCollision(Entry<CSG, List<Element>> entryA, Entry<CSG, List<Element>> entryB, Memory memory, int step, CSG intersection) throws InterruptedException {
		List<Element> contextA = entryA.getValue();
		List<Element> contextB = entryB.getValue();

		// get objects
		Element first = contextA.get(contextA.size() - 1);
		Element second = contextB.get(contextB.size() - 1);

		// check if the combination of port / component has already been handled (with another part of the component)
		for (int i = 0; i < combinationPart1.size(); i++) {
			if (combinationPart1.get(i).equals(contextA) && combinationPart2.get(i).equals(contextB.subList(0, contextB.size() - 2))) {
				// combination already handled
				return;
			} else if ((combinationPart1.get(i).equals(contextA) && combinationPart2.get(i).equals(contextB)) || (combinationPart1.get(i).equals(contextB) && combinationPart2.get(i).equals(contextA))) {
				// combination handled as part collision
				return;
			}
		}

		if (!first.getFirstAncestorByType(Component.class).equals(second.getFirstAncestorByType(Component.class))) {

			if (first instanceof Part && second instanceof Part) {
				MarkerManager.get().addMarker(new PartCollisionError(contextA, contextB, step));

				memory.getIntersections(step).add(intersection);

				CSG bodyA = entryA.getKey();
				CSG bodyB = entryB.getKey();

				CSG differenceA = bodyA;
				CSG differenceB = bodyB;

				if (memory.hasDifference(contextA, step) && memory.getDifference(contextA, step) != null) {
					differenceA = memory.getDifference(contextA, step);
				}
				if (memory.hasDifference(contextB, step) && memory.getDifference(contextB, step) != null) {
					differenceB = memory.getDifference(contextB, step);
				}

				differenceA = differenceA.difference(bodyB);
				differenceB = differenceB.difference(bodyA);

				memory.setDifference(contextA, step, differenceA);
				memory.setDifference(contextB, step, differenceB);

				memory.getCollisions(contextA, step).add(contextB);
				memory.getCollisions(contextB, step).add(contextA);

				combinationPart1.add(contextA);
				combinationPart2.add(contextB);
			}

			// MaterialBindingPort
			else if (first instanceof InteractionMaterialPort && second instanceof Part) {
				InteractionMaterialPort port = (InteractionMaterialPort) contextA.get(contextA.size() - 1);
				evaluateCollisionMaterialPort(contextA, port.getPorts(), contextB.subList(0, contextB.size() - 1), memory, step);
			}

			else if (first instanceof ExitLifeMaterialPort && second instanceof Part) {
				ExitLifeMaterialPort port = (ExitLifeMaterialPort) contextA.get(contextA.size() - 1);
				evaluateCollisionMaterialPort(contextA, port.getComponent().getPorts(), contextB.subList(0, contextB.size() - 1), memory, step);
			}
		}
	}

	private static void evaluateCollisionMaterialPort(List<Element> contextPort, List<? extends Port> portsPort, List<Element> contextCollidingObject, Memory memory, int step) throws InterruptedException {

		// get data
		MaterialPort port = (MaterialPort) contextPort.get(contextPort.size() - 1);
		Component<?> comp = (Component<?>) contextCollidingObject.get(contextCollidingObject.size() - 1);

		// check signature
		if (checkSignature(portsPort, comp.getPorts()) == true) {

			// check if colliding object is ancestor of port
			for (Element e : contextPort) {
				if (e.equals(comp)) {
					return;
				}
			}

			combinationPart1.add(contextPort);
			combinationPart2.add(contextCollidingObject);

			// show information
			// System.out.println("MaterialBinding ObjA = " + contextPort);
			// System.out.println("MaterialBinding ObjB = " + contextCollidingObject);

			// add to port data
			Set<List<Element>> components = memory.getCollisions(port.append(contextPort.subList(0, contextPort.size() - 1)), step);
			components.add(contextCollidingObject);
		} else {
			if (contextCollidingObject.size() > 1) {
				// call recursive
				evaluateCollisionMaterialPort(contextPort, portsPort, contextCollidingObject.subList(0, contextCollidingObject.size() - 1), memory, step);
			}
		}
	}

	private static boolean checkSignature(List<? extends Port> portsMaterialPort, List<? extends Port> portsPart) {
		// TODO: problem wenn ich zwei ports gleich benenne

		boolean result = true;

		// signature is equal if for every port of the material port a corresponding port of the part exists
		for (Port pPort : portsMaterialPort) {

			boolean portResult = false;

			for (Port pPart : portsPart) {
				if (pPort.getClass().equals(pPart.getClass()) && pPort.getName().equals(pPart.getName())) {
					portResult = true;
				}
			}

			result = result & portResult;
		}

		return result;
	}

	private static void add(List<Element> context, Map<CSG, List<Element>> map, Scenario scenario, Memory memory, int step) throws InterruptedException {
		for (LifeMaterialPort port : scenario.getPorts()) {
			if (port instanceof ExitLifeMaterialPort) {
				memory.setCollisions(port.append(scenario.append(context)), step, new HashSet<List<Element>>());

				CSG object = port.getVolume().getCSG();
				RealMatrix matrix = memory.getTransform(context, step);

				map.put(object.transformed(Transform.toVVecMathTransform(matrix)), port.append(scenario.append(context)));
			}
		}
	}

	private static void add(List<Element> context, Map<CSG, List<Element>> map, Component<?> component, Memory memory, int step) throws InterruptedException {
		// System.out.println("Adding component: " + component.getElementPath().get());

		if (component instanceof DefinitionComponent) {
			DefinitionComponent c = (DefinitionComponent) component;

			// Components
			for (Component<?> child : c.getComponents()) {
				add(component.append(context), map, child, memory, step);
			}

			// Ports
			for (Port port : c.getPorts()) {
				if (port instanceof InteractionMaterialPort) {
					memory.setCollisions(port.append(component.append(context)), step, new HashSet<List<Element>>());

					CSG object = ((InteractionMaterialPort) port).getVolume().getCSG();
					RealMatrix matrix = memory.getTransform(component.append(context), step);

					map.put(object.transformed(Transform.toVVecMathTransform(matrix)), port.append(component.append(context)));
				}
				if (port instanceof ExitLifeMaterialPort) {
					memory.setCollisions(port.append(component.append(context)), step, new HashSet<List<Element>>());

					CSG object = ((ExitLifeMaterialPort) port).getVolume().getCSG();
					RealMatrix matrix = memory.getTransform(component.append(context), step);

					map.put(object.transformed(Transform.toVVecMathTransform(matrix)), port.append(component.append(context)));
				}
			}

			// Parts of active label in behavior
			for (Behavior b : c.getBehaviors()) {
				State s = (State) memory.getLabel(b.append(component.append(context)), step);

				if (s != null && s.getParts() != null) {
					for (Part part : s.getParts()) {
						memory.setCollisions(part.append(s.append(b.append(component.append(context)))), step, new HashSet<List<Element>>());

						CSG object = part.toCSG();
						RealMatrix matrix = memory.getTransform(component.append(context), step);

						map.put(object.transformed(Transform.toVVecMathTransform(matrix)), part.append(component.append(context)));
					}
				}
			}

			// Parts
			for (Part part : c.getParts()) {
				memory.setCollisions(part.append(component.append(context)), step, new HashSet<List<Element>>());

				CSG object = part.toCSG();
				RealMatrix matrix = memory.getTransform(component.append(context), step);

				map.put(object.transformed(Transform.toVVecMathTransform(matrix)), part.append(component.append(context)));
			}

		} else if (component instanceof ReferenceComponent) {
			ReferenceComponent c = (ReferenceComponent) component;

			add(component.append(context), map, c.getTemplate(), memory, step);
		} else {
			throw new IllegalStateException();
		}
	}

}
