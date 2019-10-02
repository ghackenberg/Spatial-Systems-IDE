package de.tum.imomesa.workbench.commons.nodes;

import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class DefaultCellFactory<E> implements Callback<TableColumn<E, String>, TableCell<E, String>> {

	@Override
	public TableCell<E, String> call(TableColumn<E, String> param) {
		return new TableCell<E, String>() {
			@Override
			public void updateItem(String item, boolean empty) {
				if (empty) {
					setGraphic(null);
					setText(null);
				} else if (item != null) {
					setText(item);
					if (getTableRow() != null) {
						if (getTableRow().getItem() != null) {
							setGraphic(ImageHelper.getIcon(getTableRow().getItem().getClass()));
						} else {
							setGraphic(null);
						}
					} else {
						setGraphic(null);
					}
				}
			}
		};
	}

}
