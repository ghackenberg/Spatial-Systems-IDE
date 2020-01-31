package de.tum.imomesa.workbench.scenes.paths;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;

public class ModelPath<T extends Element> {

	public final List<Element> path;
	public final T element;

	public ModelPath(T element) {
		this.path = new ArrayList<>();
		this.path.add(element);
		this.element = element;
	}

	public <S extends Element> ModelPath(ModelPath<S> parent, T element) {
		this.path = element.append(parent.path);
		this.element = element;
	}

	public ModelPath(List<Element> path, T element) {
		this.path = path;
		this.element = element;
	}

	@SuppressWarnings("unchecked")
	public <S extends T> ModelPath<S> cast(Class<S> type) {
		if (type.isAssignableFrom(element.getClass())) {
			return new ModelPath<>(path, (S) element);
		} else {
			return null;
		}
	}

}
