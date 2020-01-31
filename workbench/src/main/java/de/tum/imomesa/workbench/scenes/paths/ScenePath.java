package de.tum.imomesa.workbench.scenes.paths;

import javafx.scene.Group;

public class ScenePath {

	public final Group solidRoot, alphaRoot, solidParent, alphaParent;

	public ScenePath(Group solidRoot, Group alphaRoot) {
		this.solidRoot = solidRoot;
		this.alphaRoot = alphaRoot;

		this.solidParent = solidRoot;
		this.alphaParent = alphaRoot;
	}

	public ScenePath(ScenePath parent, Group solidChild, Group alphaChild) {
		this.solidRoot = parent.solidRoot;
		this.alphaRoot = parent.alphaRoot;

		this.solidParent = solidChild;
		this.alphaParent = alphaChild;

		parent.solidParent.getChildren().add(solidChild);
		parent.alphaParent.getChildren().add(alphaChild);
	}

}
