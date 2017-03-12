package de.tum.imomesa.analyzer.serializations;

import java.io.IOException;
import java.io.Writer;

public abstract class Serialization<T> {

	private T data;
	private Writer writer;

	public Serialization(T data, Writer writer) {
		this.data = data;
		this.writer = writer;
	}

	protected T getData() {
		return data;
	}

	protected Writer getWriter() {
		return writer;
	}

	public abstract void generateResult() throws IOException;

}
