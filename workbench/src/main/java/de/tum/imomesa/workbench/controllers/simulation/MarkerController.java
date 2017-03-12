package de.tum.imomesa.workbench.controllers.simulation;

import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.commons.nodes.PTableColumn;
import de.tum.imomesa.workbench.controllers.AbstractMarkerController;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class MarkerController extends AbstractMarkerController {

	@FXML
	private TableView<SimulationMarker> table;
	@FXML
	private PTableColumn<SimulationMarker, String> message;

	@FXML
	public void initialize() {
		table.setItems(MarkerManager.get().getMarkersAsObservable());

		message.setCellFactory(
				new Callback<TableColumn<SimulationMarker, String>, TableCell<SimulationMarker, String>>() {
					@Override
					public TableCell<SimulationMarker, String> call(TableColumn<SimulationMarker, String> param) {
						TableCell<SimulationMarker, String> cell = new TableCell<SimulationMarker, String>() {
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
									SimulationMarker p = (SimulationMarker) getTableRow().getItem();
									if (p != null) {
										setGraphic(ImageHelper.getIcon(p.getClass()));
									}
								}
							}
						};
						return cell;
					}
				});
	}

	private Simulator simulator;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public Simulator getSimulator() {
		return simulator;
	}

}
