package de.tum.imomesa.analyzer.nodes.elements;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.helpers.Namer;

public class AggregationElement extends GroupElement {

	public AggregationElement(Aggregation<?> aggregation) {
		super(Namer.convertSpace(aggregation.getClass()));
	}

}
