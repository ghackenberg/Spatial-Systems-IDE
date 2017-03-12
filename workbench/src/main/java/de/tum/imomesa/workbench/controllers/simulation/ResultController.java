package de.tum.imomesa.workbench.controllers.simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.commons.nodes.PTableColumn;
import de.tum.imomesa.workbench.simulations.helpers.SimulationRun;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class ResultController implements EventHandler {

	private static final Map<Scenario, ObservableList<SimulationRun>> RUNS = new HashMap<>();

	@FXML
	private TableView<SimulationRun> tableView;
	@FXML
	private PTableColumn<SimulationRun, String> message;

	private ObservableList<SimulationRun> runs;
	private SimulationRun run;

	@FXML
	public void initialize() {
		EventBus.getInstance().subscribe(this);

		message.setCellFactory(new Callback<TableColumn<SimulationRun, String>, TableCell<SimulationRun, String>>() {
			@Override
			public TableCell<SimulationRun, String> call(TableColumn<SimulationRun, String> param) {
				TableCell<SimulationRun, String> cell = new TableCell<SimulationRun, String>() {
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
							SimulationRun p = (SimulationRun) getTableRow().getItem();
							if (p != null) {
								if (!p.getFinished()) {
									setGraphic(ImageHelper.getImageAsIcon("icons/working.gif"));
								} else {
									if (p.getErrors()) {
										setGraphic(ImageHelper.getImageAsIcon("icons/decline.png"));
									} else if (p.getTimeouts()) {
										setGraphic(ImageHelper.getImageAsIcon("icons/timeout.png"));
									} else if (p.getWarnings()) {
										setGraphic(ImageHelper.getImageAsIcon("icons/warning.png"));
									} else {
										setGraphic(ImageHelper.getImageAsIcon("icons/accept.png"));
									}
								}
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

		if (!RUNS.containsKey(simulator.getScenario())) {
			RUNS.put(simulator.getScenario(), FXCollections.observableArrayList());
		}

		runs = RUNS.get(simulator.getScenario());
		
		long timestamp = System.currentTimeMillis();
		
		for (SimulationRun run : runs) {
			long duration = timestamp - run.getTimestamp();
			
			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
			long hours = TimeUnit.MILLISECONDS.toHours(duration);
			long days = TimeUnit.MILLISECONDS.toDays(duration);
			
			if (days > 0) {
				run.setTimeString(days + " day" + (days == 1 ? "" : "s") + " ago");
			} else if (hours > 0) {
				run.setTimeString(hours + " hour" + (hours == 1 ? "" : "s") + " ago");
			} else if (minutes > 0) {
				run.setTimeString(minutes + " minute" + (minutes == 1 ? "" : "s") + " ago");
			} else {
				run.setTimeString(seconds + " second" + (seconds == 1 ? "" : "s") + " ago");
			}
		}

		tableView.setItems(runs);
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public void handle(StartEvent event) {
		run = new SimulationRun();
		run.setTimestamp(System.currentTimeMillis());
		run.setTimeString("Now");

		runs.add(run);
	}

	public void handle(SimulationMarkerEvent event) {
		run.getMarkers().add(event.getMarker());

		if (event.getMarker() instanceof ErrorMarker) {
			run.setErrors(true);
		} else if (event.getMarker() instanceof TimeoutMarker) {
			run.setTimeouts(true);
		} else if (event.getMarker() instanceof WarningMarker) {
			run.setWarnings(true);
		} else {
			throw new IllegalStateException("Marker type not supported: " + event.getMarker().getClass().getName());
		}
	}

	public void handle(EndEvent event) {
		long duration = event.getTimestamp() - run.getTimestamp();
		
		run.setFinished(true);
		run.setDuration(duration);
		
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration);
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		
		if (days > 0) {
			run.setDurationString(days + " day" + (days == 1 ? "" : "s"));
		} else if (hours > 0) {
			run.setDurationString(hours + " hour" + (hours == 1 ? "" : "s"));
		} else if (minutes > 0) {
			run.setDurationString(minutes + " minute" + (minutes == 1 ? "" : "s"));
		} else {
			run.setDurationString(seconds + " second" + (seconds == 1 ? "" : "s"));
		}

		String message = "Finished";

		if (run.getErrors()) {
			message += " with errors";
			if (run.getWarnings()) {
				message += " and warnings";
			}
		} else if (run.getTimeouts()) {
			message += " with timeout";
			if (run.getWarnings()) {
				message += " and warnings";
			}
		} else if (run.getWarnings()) {
			message += " with warnings";
		} else {
			message += " without semantic issues";
		}

		run.setMessage(message);
	}

}
