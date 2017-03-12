package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.Observation;
import de.tum.imomesa.model.elements.executables.Label;
import de.tum.imomesa.model.elements.ports.PortProxy;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class Memory
{
	
	private CopyOnWriteArrayList<CopyOnWriteArraySet<ComponentProxy>> proxies = new CopyOnWriteArrayList<>();
	
	public void setProxy(int step, CopyOnWriteArraySet<ComponentProxy> set) {
		if (proxies.size() == step) {
			proxies.add(set);
		}
		else if (proxies.size() > step) {
			proxies.add(step, set);
		}
		else {
			throw new IllegalStateException("Step is in the future!");
		}
	}
	public CopyOnWriteArraySet<ComponentProxy> getProxy(int step) {
		if(step >= proxies.size()) {
			throw new IllegalStateException("Step is in the future!");
		}
		
		return proxies.get(step);
	}
	
	// Values
	
	private ConcurrentMap<String, CopyOnWriteArrayList<java.lang.Object>> values = new ConcurrentHashMap<>();
	
	private CopyOnWriteArrayList<java.lang.Object> getValues(List<Element> context)
	{
		CopyOnWriteArrayList<java.lang.Object> result = values.get(getUniquePath(context));
		
		if (result == null)
		{
			result = new CopyOnWriteArrayList<>();
			values.put(getUniquePath(context), result);
		}
		
		return result;
	}
	public void initValue(List<Element> context, int step) {
		CopyOnWriteArrayList<java.lang.Object> values = getValues(context);
		
		while (values.size() < step) {
			values.add(null);
		}
	}
	public synchronized java.lang.Object getValue(AbstractThread<?> thread, List<Element> context, int step) throws InterruptedException
	{
		if (!(context.get(context.size() - 1) instanceof Observation)) {
			throw new IllegalStateException();
		}
		if (context.get(context.size() - 1) instanceof PortProxy) {
			PortProxy proxy = (PortProxy) context.get(context.size() - 1); 
			context = context.subList(0, context.size() - 1);
			context.add(proxy.getPortImplementation().getParent());
			context.add(proxy.getPortImplementation());
		}
		
		CopyOnWriteArrayList<java.lang.Object> values = getValues(context);
		
		while (values.size() <= step && ThreadManager.getInstance().getRunning())
		{
			if (thread == null) {
				throw new InterruptedException("Main thread is blocked!");
			}
			else {
				ThreadManager.getInstance().blockObservation(thread, context);
			}
			//System.out.println("getValue(" + thread + ", " + getPathReadable(context) + ", " + step + ") : start wait");
			wait();
			//System.out.println("getValue(" + thread + ", " + getPathReadable(context) + ", " + step + ") : end wait");
			if (thread != null && ThreadManager.getInstance().blocksObservation(thread, context)) {
				ThreadManager.getInstance().unblockObservation(thread, context);
			}
		}
		
		//System.out.println("getValue(" + thread + ", " + getPathReadable(context) + ", " + step + ") = " + values.get(step));
		
		if (hasValue(context, step))
		{
			return values.get(step);
		}
		else {
			return null;
		}
	}
	public synchronized void setValue(AbstractThread<?> thread, List<Element> context, int step, java.lang.Object value)
	{
		if (!(context.get(context.size() - 1) instanceof Observation)) {
			throw new IllegalStateException();
		}
		if (context.get(context.size() - 1) instanceof PortProxy) {
			PortProxy proxy = (PortProxy) context.get(context.size() - 1); 
			context = context.subList(0, context.size() - 1);
			context.add(proxy.getPortImplementation().getParent());
			context.add(proxy.getPortImplementation());
		}
		
		//System.out.println("setValue(" + thread + ":" + (thread == null ? "null" : thread.getClass().getName()) + ", " + getPathReadable(context) + ", " + step + ", " + value + ")");
		
		CopyOnWriteArrayList<java.lang.Object> values = getValues(context);
		
		if (values.size() == step)
		{
			values.add(value);
			
			//System.out.println(thread + " writing value " + value + " on observation " + context + " in step " + step);
			
			ThreadManager.getInstance().unblockObservation(context);
		}
		else if (values.size() > step)
		{
			MarkerManager.get().addMarker(new ErrorMarker(thread == null ? new ArrayList<>() : thread.getContext(), "Writing value more than once: " + context, step));

			//System.out.println("setValue(" + thread + ":" + (thread == null ? "null" : thread.getClass().getName()) + ", " + getPathReadable(context) + ", " + step + ", " + value + ") : Writing value more than once!");
		}
		else
		{
			throw new IllegalStateException("Step is in the future!");
		}
		
		notifyAll();
	}
	public boolean hasValue(List<Element> context, int step)
	{
		if (!(context.get(context.size() - 1) instanceof Observation)) {
			throw new IllegalStateException();
		}
		if (context.get(context.size() - 1) instanceof PortProxy) {
			PortProxy proxy = (PortProxy) context.get(context.size() - 1); 
			context = context.subList(0, context.size() - 1);
			context.add(proxy.getPortImplementation().getParent());
			context.add(proxy.getPortImplementation());
		}
		
		CopyOnWriteArrayList<java.lang.Object> values = getValues(context);
		
		//System.out.println("hasValue(" + getPathReadable(context) + ", " + step + ") = " + (values.size() > step));
		
		return values.size() > step;
	}
	
	
	// Collisions
	
	private ConcurrentMap<String, CopyOnWriteArrayList<Set<List<Element>>>> collisions = new ConcurrentHashMap<>();
	
	private CopyOnWriteArrayList<Set<List<Element>>> getCollisions(List<Element> context)
	{
		CopyOnWriteArrayList<Set<List<Element>>> result = collisions.get(getUniquePath(context));
		
		if (result == null)
		{
			result = new CopyOnWriteArrayList<>();
			collisions.put(getUniquePath(context), result);
		}
		
		return result;
	}
	
	public void initCollision(List<Element> context, int step) {
		CopyOnWriteArrayList<Set<List<Element>>> values = getCollisions(context);
		
		while (values.size() < step) {
			values.add(null);
		}
	}
	
	public synchronized Set<List<Element>> getCollision(List<Element> context, int step) throws InterruptedException
	{
		CopyOnWriteArrayList<Set<List<Element>>> values = getCollisions(context);
		
		while (values.size() <= step && ThreadManager.getInstance().getRunning())
		{
//			System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") : start wait");
			wait();
//			System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") : end wait");
		}

//		System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") = " + values.get(step));
		
		return values.get(step);
	}
	public synchronized void setCollision(List<Element> context, int step, Set<List<Element>> value)
	{
//		System.out.println("setCollision(" + getPathReadable(context) + ", " + step + ", " + value + ")");

		CopyOnWriteArrayList<Set<List<Element>>> values = getCollisions(context);
		
		if (values.size() == step)
		{
			values.add(value);
		}
		else if (values.size() > step)
		{
			values.set(step, value);
		}
		else
		{
			throw new IllegalStateException("Step is in the future!");
		}
		
		notifyAll();
	}
	public boolean hasCollision(List<Element> context, int step)
	{
		CopyOnWriteArrayList<Set<List<Element>>> values = getCollisions(context);
//		System.out.println("hasCollision(" + getPathReadable(context) + ", " + step + ") = " + (values.size() > step));
		return values.size() > step;
	}
	
	
	// Bindings
	
	private ConcurrentMap<String, CopyOnWriteArrayList<Set<List<Element>>>> bindings = new ConcurrentHashMap<>();
	
	private CopyOnWriteArrayList<Set<List<Element>>> getBindings(List<Element> context)
	{
		CopyOnWriteArrayList<Set<List<Element>>> result = bindings.get(getUniquePath(context));
		
		if (result == null)
		{
			result = new CopyOnWriteArrayList<>();
			bindings.put(getUniquePath(context), result);
		}
		
		return result;
	}
	
	public void initBinding(List<Element> context, int step) {
		CopyOnWriteArrayList<Set<List<Element>>> values = getBindings(context);
		
		while (values.size() < step) {
			values.add(null);
		}
	}
	
	public synchronized Set<List<Element>> getBinding(List<Element> context, int step) throws InterruptedException
	{
		CopyOnWriteArrayList<Set<List<Element>>> values = getBindings(context);
		
		while (values.size() <= step && ThreadManager.getInstance().getRunning())
		{
//			System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") : start wait");
			wait();
//			System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") : end wait");
		}

//		System.out.println("getCollision(" + getPathReadable(context) + ", " + step + ") = " + values.get(step));

		return values.get(step);
	}
	public synchronized void setBinding(List<Element> context, int step, Set<List<Element>> value)
	{
//		System.out.println("setCollision(" + getPathReadable(context) + ", " + step + ", " + value + ")");

		CopyOnWriteArrayList<Set<List<Element>>> values = getBindings(context);
		
		if (values.size() == step)
		{
			values.add(value);
		}
		else if (values.size() > step)
		{
			values.set(step, value);
		}
		else
		{
			throw new IllegalStateException("Step is in the future!");
		}
		
		notifyAll();
	}
	public boolean hasBinding(List<Element> context, int step)
	{
		CopyOnWriteArrayList<Set<List<Element>>> values = getBindings(context);
//		System.out.println("hasCollision(" + getPathReadable(context) + ", " + step + ") = " + (values.size() > step));
		return values.size() > step;
	}
	
	// Transformations
	
	private ConcurrentMap<String, CopyOnWriteArrayList<RealMatrix>> transforms = new ConcurrentHashMap<>();

	private CopyOnWriteArrayList<RealMatrix> getTransforms(List<Element> context)
	{
		CopyOnWriteArrayList<RealMatrix> result = transforms.get(getUniquePath(context));
		
		if (result == null)
		{
			result = new CopyOnWriteArrayList<>();
			transforms.put(getUniquePath(context), result);
		}
		
		return result;
	}
	
	public void initTransform(List<Element> context, int step) {
		CopyOnWriteArrayList<RealMatrix> values = getTransforms(context);
		
		while (values.size() < step) {
			values.add(null);
		}
	}
	
	public synchronized RealMatrix getTransform(List<Element> context, int step) throws InterruptedException
	{
		CopyOnWriteArrayList<RealMatrix> transforms = getTransforms(context);
		
		while (transforms.size() <= step && ThreadManager.getInstance().getRunning())
		{
//			System.out.println("getTransform(" + getPathReadable(context) + ", " + step + ") : start wait");
			wait();
//			System.out.println("getTransform(" + getPathReadable(context) + ", " + step + ") : end wait");
		}

//		System.out.println("getTransform(" + getPathReadable(context) + ", " + step + ") = " + transforms.get(step));

		return transforms.get(step);
	}
	public synchronized void setTransform(List<Element> context, int step, RealMatrix value)
	{
//		System.out.println("setTransform(" + getPathReadable(context) + ", " + step + ", " + value + ")");

		CopyOnWriteArrayList<RealMatrix> transforms = getTransforms(context);
		
		if (transforms.size() == step)
		{
			transforms.add(value);
		}
		else if (transforms.size() > step)
		{
			transforms.set(step, value);
		}
		else
		{
			throw new IllegalStateException("Step is in the future!");
		}
		
		notifyAll();
	}
	public boolean hasTransform(List<Element> context, int step)
	{
		CopyOnWriteArrayList<RealMatrix> transforms = getTransforms(context);
		
//		System.out.println("hasTransform(" + getPathReadable(context) + ", " + step + ") = " + (transforms.size() > step));
		
		return transforms.size() > step;
	}
	
	
	// Labels
	
	private ConcurrentMap<String, CopyOnWriteArrayList<Label>> labels = new ConcurrentHashMap<>();
	
	private CopyOnWriteArrayList<Label> getLabels(List<Element> context)
	{
		CopyOnWriteArrayList<Label> result = labels.get(getUniquePath(context));
		
		if (result == null)
		{
			result = new CopyOnWriteArrayList<>();
			labels.put(getUniquePath(context), result);
		}
		
		return result;
	}
	
	public void initLabel(List<Element> context, int step) {
		CopyOnWriteArrayList<Label> values = getLabels(context);
		
		while (values.size() < step) {
			values.add(null);
		}
	}
	
	public synchronized Label getLabel(List<Element> context, int step) throws InterruptedException
	{
		CopyOnWriteArrayList<Label> labels = getLabels(context);
		
		while (labels.size() <= step && ThreadManager.getInstance().getRunning())
		{
//			System.out.println("getLabel(" + getPathReadable(context) + ", " + step + ") : start wait");
			wait();
//			System.out.println("getLabel(" + getPathReadable(context) + ", " + step + ") : end wait");
		}
		
		return labels.get(step);
	}
	
	public synchronized void setLabel(List<Element> context, int step, Label label)
	{
		CopyOnWriteArrayList<Label> labels = getLabels(context);
		
		if (labels.size() == step)
		{
			labels.add(label);
		}
		else if (labels.size() > step)
		{
			labels.set(step, label);
		}
		else
		{
			throw new IllegalStateException("Step is in the future!");
		}
		
		notifyAll();
	}
	
	public boolean hasLabel(List<Element> context, int step)
	{
		CopyOnWriteArrayList<Label> labels = getLabels(context);
//		System.out.println("hasLabel(" + getPathReadable(context) + ", " + step + ") = " + (labels.size() > step));
		
		return labels.size() > step;
	}
	
	// path creator
	private String getUniquePath(List<Element> context) {
		String result = "";

		for(Element e : context) {
			result += e.getId() + "|";
		}

		return result;
	}
	
	/*
	private String getPathReadable(List<Element> context) {
		String result = "";
		
		for(Element e : context) {
			result += "/" + e.toString();
		}
		
		return result;
	}
	*/
	
}
