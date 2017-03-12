package de.tum.imomesa.workbench.explorers.listeners;

import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.explorers.Configuration;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;

public class OverviewElementChangeListener implements ListChangeListener<Element> {

	private TreeItem<Element> item;
	private Configuration configuration;
	private boolean childrenExpanded;

	public OverviewElementChangeListener(TreeItem<Element> treeItem, Configuration configuration,
			boolean childrenExpanded) {
		this.item = treeItem;
		this.configuration = configuration;
		this.childrenExpanded = childrenExpanded;
	}

	@Override
	public void onChanged(ListChangeListener.Change<? extends Element> change) {
		while (change.next()) {
			if (change.wasPermutated()) {
				throw new IllegalStateException("Not implemented yet!");
			} else if (change.wasUpdated()) {
				throw new IllegalStateException("Not implemented yet!");
			} else {
				for (Element element : change.getRemoved()) {
					for (int index = 0; index < item.getChildren().size(); index++) {
						TreeItem<Element> child = item.getChildren().get(index);
						if (child.getValue() == element) {
							item.getChildren().remove(index--);
						}
					}
				}
				int index = change.getFrom();
				for (Element element : change.getAddedSubList()) {
					item.getChildren().add(index++,
							TreeItemBuilder.getInstance().transform(element, configuration, childrenExpanded));
				}
			}
		}
	}

}
