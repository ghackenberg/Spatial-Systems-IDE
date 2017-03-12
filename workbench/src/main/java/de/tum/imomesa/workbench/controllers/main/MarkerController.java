package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.manager.SyntacticMarkerManager;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.commons.nodes.PTableColumn;
import de.tum.imomesa.workbench.controllers.AbstractMarkerController;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class MarkerController extends AbstractMarkerController implements EventHandler {

	// UI elements
	@FXML
	private TableView<SyntacticMarker> table;
	@FXML
	private PTableColumn<SyntacticMarker, String> columnType;
	@FXML
	private PTableColumn<SyntacticMarker, String> columnPath;

	public void initialize() {
		// set up sorting
		columnPath.setSortType(TableColumn.SortType.ASCENDING);
		// bind data of table
		table.setItems(SyntacticMarkerManager.getInstance().getMarkers());
		table.sort();
		// sort table by column
		table.getSortOrder().add(columnPath);
		// add cell factory to display icon
		columnType.setCellFactory(
				new Callback<TableColumn<SyntacticMarker, String>, TableCell<SyntacticMarker, String>>() {
					@Override
					public TableCell<SyntacticMarker, String> call(TableColumn<SyntacticMarker, String> param) {
						TableCell<SyntacticMarker, String> cell = new TableCell<SyntacticMarker, String>() {
							@Override
							public void updateItem(String item, boolean empty) {
								// if empty, delete all data in cell
								if (empty == true) {
									setGraphic(null);
									setText(null);
									return;
								}
								// set item text and graphic
								if (item != null) {
									setText(item);
									SyntacticMarker p = (SyntacticMarker) getTableRow().getItem();
									if (p != null) {
										setGraphic(ImageHelper.getIcon(p.getClass()));
									}
								}
							}
						};
						return cell;
					}
				});
		// listen to event bus
		EventBus.getInstance().subscribe(this);
	}

	public void handle(MarkerAddEvent event) {
		table.sort();
	}
}
