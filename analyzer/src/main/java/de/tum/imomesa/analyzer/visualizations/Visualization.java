package de.tum.imomesa.analyzer.visualizations;

import javafx.scene.Node;

public abstract class Visualization<T, N extends Node> {

	private T data;
	private N result;

	public Visualization(T data) {
		this.data = data;
	}

	protected T getData() {
		return data;
	}

	public N getResult() {
		if (result == null) {
			result = generateResult();
		}
		return result;
	}

	protected abstract N generateResult();

}
