package de.tum.imomesa.model.channels;

import java.util.List;

import de.tum.imomesa.model.Element;

public class DynamicChannel extends Channel {
	
	// ########################################################################
	// Attributes
	// ########################################################################

	private List<Element> sourceContext;
	private List<Element> targetContext;

	// ########################################################################
	// Constructors
	// ########################################################################

	// ########################################################################
	// Methods
	// ########################################################################

	// source context
	public List<Element> getSourceContext() {
		return sourceContext;
	}
	
	public void setSourceContext(List<Element> context) {
		this.sourceContext = context;
	}
	
	// target context
	public List<Element> getTargetContext() {
		return targetContext;
	}
	public void setTargetContext(List<Element> context) {
		targetContext = context;
	}

	// ########################################################################
	// Properties
	// ########################################################################

}
