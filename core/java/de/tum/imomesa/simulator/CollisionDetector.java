package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.math3.linear.RealMatrix;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.HashedOverlappingPairCache;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.ComponentInterface;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.Part;
import de.tum.imomesa.model.elements.executables.behaviors.Behavior;
import de.tum.imomesa.model.elements.executables.behaviors.State;
import de.tum.imomesa.model.elements.executables.scenarios.Scenario;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.materials.MaterialBindingPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialExitPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialLifePort;
import de.tum.imomesa.model.elements.ports.materials.MaterialPort;
import de.tum.imomesa.model.elements.volumes.AtomicVolume;
import de.tum.imomesa.model.elements.volumes.BoxVolume;
import de.tum.imomesa.model.elements.volumes.CompositeVolume;
import de.tum.imomesa.model.elements.volumes.CylinderVolume;
import de.tum.imomesa.model.elements.volumes.SphereVolume;
import de.tum.imomesa.model.elements.volumes.Volume;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class CollisionDetector {
	
	private static final float MULTIPLIER = 1;
	private static final float SIZE = MULTIPLIER * 1000;
	private static final int HANDLES = 16000;
	
	// tolerance to avoid numeric rounding errors
	private static final float TOLERANCE = 0.001f;
	
	private static List<List<Element>> combinationPart1 = new ArrayList<List<Element>>();
	private static List<List<Element>> combinationPart2 = new ArrayList<List<Element>>();
	
	public static void process(Component mainComp, Scenario scenario, Memory memory, int step) throws InterruptedException {
		// reset values
		combinationPart1 = new ArrayList<List<Element>>();
		combinationPart2 = new ArrayList<List<Element>>();
		
		// Collision world
		Vector3f worldAabbMin = new Vector3f(-SIZE, -SIZE, -SIZE);
		Vector3f worldAabbMax = new Vector3f(SIZE, SIZE, SIZE);
		OverlappingPairCache pairCache = new HashedOverlappingPairCache();

		CollisionConfiguration config = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(config);
		BroadphaseInterface broadphase = new AxisSweep3(worldAabbMin, worldAabbMax, HANDLES, pairCache);
		
		CollisionWorld world = new CollisionWorld(dispatcher, broadphase, config);
				
		Map<CollisionObject, List<Element>> map = new HashMap<>();
		add(world, new ArrayList<>(), map, mainComp, memory, step);
		
		// add material ports of scenario
		add(world, mainComp.append(new ArrayList<>()), map, scenario, memory, step);
		
		// add component proxies
		for (ComponentProxy proxy : memory.getProxy(step)) {
			add(world, new ArrayList<>(), map, proxy, memory, step);
		}
		
		// Detect collisions
		world.performDiscreteCollisionDetection();

		// Process collisions
		for(int i = 0; i < world.getDispatcher().getNumManifolds(); i++) {

			PersistentManifold manifold = world.getDispatcher().getManifoldByIndexInternal(i);

			// Check for penetration
			float distance = Float.MAX_VALUE;
			for (int j = 0; j < manifold.getNumContacts(); j++) {
				distance = Math.min(distance, manifold.getContactPoint(j).getDistance());
			}
			
			// If objects are penetrating
			if (distance < -TOLERANCE) {
				CollisionObject objA = (CollisionObject) manifold.getBody0();
				CollisionObject objB = (CollisionObject) manifold.getBody1();
	
				List<Element> listA = map.get(objA);
				List<Element> listB = map.get(objB);
	
				evaluateCollision(listA, listB, memory, step);
				evaluateCollision(listB, listA, memory, step);
			}
		}
	}
	
	private static void evaluateCollision(List<Element> contextA, List<Element> contextB, Memory memory, int step) throws InterruptedException {
		// get objects
		Element first = contextA.get(contextA.size() - 1);
		Element second = contextB.get(contextB.size() - 1);
		
		// check if the combination of port / component has already been handled (with another part of the component)
		for(int i = 0; i < combinationPart1.size(); i++) {
			if(combinationPart1.get(i).equals(contextA) && combinationPart2.get(i).equals(contextB.subList(0, contextB.size() - 2))) {
				// combination already handled
				return;
			}
			else if((combinationPart1.get(i).equals(contextA) && combinationPart2.get(i).equals(contextB)) ||
					(combinationPart1.get(i).equals(contextB) && combinationPart2.get(i).equals(contextA))) {
				// combination handled as part collision
				return;
			}
		}
		
		if(!first.getFirstAncestorByType(Component.class).equals(second.getFirstAncestorByType(Component.class))) {

			if(first instanceof Part && second instanceof Part) {
				MarkerManager.get().addMarker(new ErrorMarker(contextA, "Part collision detected with " + contextB, step));
				combinationPart1.add(contextA);
				combinationPart2.add(contextB);
			}
			
			// MaterialBindingPort
			else if(first instanceof MaterialBindingPort && second instanceof Part) {
				MaterialBindingPort port = (MaterialBindingPort) contextA.get(contextA.size() - 1);
				evaluateCollisionMaterialPort(contextA, port.getPorts(), contextB.subList(0, contextB.size() - 1), memory, step);
			}
		
			else if(first instanceof MaterialExitPort && second instanceof Part) {
				MaterialExitPort port = (MaterialExitPort) contextA.get(contextA.size() - 1);
				evaluateCollisionMaterialPort(contextA, port.getComponent().getPorts(), contextB.subList(0, contextB.size() - 1), memory, step);
			}
		}
	}

	private static void evaluateCollisionMaterialPort(List<Element> contextPort, List<PortInterface> portsPort, List<Element> contextCollidingObject, Memory memory, int step) throws InterruptedException {

		// get data
		MaterialPort port = (MaterialPort) contextPort.get(contextPort.size() - 1);
		ComponentInterface comp = (ComponentInterface) contextCollidingObject.get(contextCollidingObject.size() - 1);

		// check signature
		if(checkSignature(portsPort, comp.getPorts()) == true) {
			
			// check if colliding object is ancestor of port
			for(Element e : contextPort) {
				if(e.equals(comp)) {
					return;
				}
			}
			
			combinationPart1.add(contextPort);
			combinationPart2.add(contextCollidingObject);
			
			// show information
			//System.out.println("MaterialBinding ObjA = " + contextPort);
			//System.out.println("MaterialBinding ObjB = " + contextCollidingObject);
			
			// add to port data
			Set<List<Element>> components = memory.getCollision(port.append(contextPort.subList(0, contextPort.size() - 1)), step);
			components.add(contextCollidingObject);
		}
		else {
			if(contextCollidingObject.size() > 1) {
				// call recursive
				evaluateCollisionMaterialPort(contextPort, portsPort, contextCollidingObject.subList(0, contextCollidingObject.size() - 1), memory, step);
			}
		}
	}

	
	private static boolean checkSignature(List<PortInterface> portsMaterialPort, List<PortInterface> portsPart) {
		// TODO: problem wenn ich zwei ports gleich benenne
		
		boolean result = true;
		
		// signature is equal if for every port of the material port a corresponding port of the part exists
		for(PortInterface pPort : portsMaterialPort) {
			
			boolean portResult = false;
			
			for(PortInterface pPart : portsPart) {
				if(pPort.getClass().equals(pPart.getClass()) && pPort.getName().equals(pPart.getName())) {
					portResult = true;
				}
			}
			
			result = result & portResult;
		}
		
		
		return result;
	}
	
	private static void add(CollisionWorld world, List<Element> context, Map<CollisionObject, List<Element>> map, Scenario scenario, Memory memory, int step) throws InterruptedException {
		for(MaterialLifePort port : scenario.getPorts()) {
			if(port instanceof MaterialExitPort) {
				memory.setCollision(port.append(scenario.append(context)), step, new HashSet<List<Element>>());
				
				CollisionObject object = new CollisionObject();
				object.setCollisionShape(add((MaterialExitPort) port));
				RealMatrix matrix = memory.getTransform(context, step);
				object.setWorldTransform(toTransform(matrix));
				world.addCollisionObject(object);
				
				map.put(object, port.append(scenario.append(context)));
			}
		}
	}
	
	private static void add(CollisionWorld world, List<Element> context, Map<CollisionObject, List<Element>> map, ComponentInterface component, Memory memory, int step) throws InterruptedException {
		if (component instanceof Component) {
			Component c = (Component) component;
			
			// Components
			for (ComponentInterface child : c.getComponents()) {
				add(world, component.append(context), map, child, memory, step);
			}
			
			// Ports
			for (PortInterface port : c.getPorts()) {
				if (port instanceof MaterialBindingPort) {
					memory.setCollision(port.append(component.append(context)), step, new HashSet<List<Element>>());
					
					CollisionObject object = new CollisionObject();
					object.setCollisionShape(add((MaterialBindingPort) port));
					RealMatrix matrix = memory.getTransform(component.append(context), step);
					object.setWorldTransform(toTransform(matrix));
					world.addCollisionObject(object);
					
					map.put(object, port.append(component.append(context)));
				}
				if(port instanceof MaterialExitPort) {
					memory.setCollision(port.append(component.append(context)), step, new HashSet<List<Element>>());
					
					CollisionObject object = new CollisionObject();
					object.setCollisionShape(add((MaterialExitPort) port));
					RealMatrix matrix = memory.getTransform(component.append(context), step);
					object.setWorldTransform(toTransform(matrix));
					world.addCollisionObject(object);
					
					map.put(object, port.append(component.append(context)));
				}
			}
			
			// Parts of active label in behavior
			for(Behavior b : c.getBehaviors()) {
				State s = (State) memory.getLabel(b.append(component.append(context)), step);
				
				for(Part part : s.getParts()) {
					CollisionObject object = new CollisionObject();
					
					object.setCollisionShape(add(part));
					
					RealMatrix matrix = memory.getTransform(component.append(context), step);
					
					object.setWorldTransform(toTransform(matrix));

					world.addCollisionObject(object);
					
					map.put(object, part.append(component.append(context)));
				}
			}
			
			// Parts
			for (Part part : c.getParts()) {
				CollisionObject object = new CollisionObject();
				
				object.setCollisionShape(add(part));
				
				RealMatrix matrix = memory.getTransform(component.append(context), step);
				
				object.setWorldTransform(toTransform(matrix));

				world.addCollisionObject(object);
				
				map.put(object, part.append(component.append(context)));
			}
			
		}
		else if (component instanceof ComponentProxy) {
			ComponentProxy c = (ComponentProxy) component;
			
			add(world, component.append(context), map, c.getTemplate(), memory, step);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	private static Matrix4f toMatrix4f(RealMatrix matrix) {
		float[] data = new float[16];
		
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				data[r * 4 + c] = (((c + 1) % 4 == 0 && (r + 1) < 4) ? MULTIPLIER : 1f) * (float) matrix.getEntry(r, c);
			}
		}
		
		return new Matrix4f(data);
	}
	
	private static Transform toTransform(RealMatrix matrix) {
		return new Transform(toMatrix4f(matrix));
	}
	
	private static CollisionShape add(MaterialPort port) {
		return add(port.getVolume());
	}
	
	private static CollisionShape add(Part part) {
		CompoundShape shape = new CompoundShape();
		// Calculate transform
		shape.addChildShape(calcTransforms(part.getTransforms()), add(part.getVolume()));
		
		return shape;
	}
	
	private static CollisionShape add(Volume volume) {
		CompoundShape shape = new CompoundShape();
		
		CollisionShape child;
		
		if (volume instanceof CompositeVolume) {
			CompositeVolume v = (CompositeVolume) volume;
			
			CompoundShape compound = new CompoundShape();
			
			for (Volume temp : v.getVolumes()) {
				Transform t = new Transform();
				t.setIdentity();
				compound.addChildShape(t, add(temp));
			}
			
			child = compound;
		}
		else if (volume instanceof AtomicVolume) {
			if (volume instanceof BoxVolume) {
				BoxVolume v = (BoxVolume) volume;
				BoxShape box = new BoxShape(new Vector3f(MULTIPLIER * v.getWidth().floatValue() / 2, MULTIPLIER * v.getHeight().floatValue() / 2, MULTIPLIER * v.getDepth().floatValue() / 2));
				child = box;
			}
			else if (volume instanceof SphereVolume) {
				SphereVolume v = (SphereVolume) volume;
				SphereShape sphere = new SphereShape(MULTIPLIER * (float) v.getRadius());
				child = sphere;
			}
			else if (volume instanceof CylinderVolume) {
				CylinderVolume v = (CylinderVolume) volume;
				CylinderShape cylinder = new CylinderShape(new Vector3f(MULTIPLIER * (float) v.getRadius(), MULTIPLIER * (float) v.getHeight() / 2, MULTIPLIER * (float) v.getRadius()));
				child = cylinder;
			}
			else {
				throw new IllegalStateException();
			}
		}
		else {
			throw new IllegalStateException();
		}
		
		// Calculate transform
		shape.addChildShape(calcTransforms(volume.getTransforms()), child);
		
		return shape;
	}
	
	private static Transform calcTransforms(List<de.tum.imomesa.model.elements.transforms.Transform> transforms) {
		Transform transform = new Transform();
		transform.setIdentity();
		
		for (de.tum.imomesa.model.elements.transforms.Transform t : transforms) {
			Transform temp = transform;
			transform = new Transform();
			transform.mul(temp, toTransform(t.toRealMatrix()));
		}		
		
		return transform;
	}

}
