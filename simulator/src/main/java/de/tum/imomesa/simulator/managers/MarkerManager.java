package de.tum.imomesa.simulator.managers;

import java.util.Collections;
import java.util.List;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.simulator.events.ErrorMarkerEvent;
import de.tum.imomesa.simulator.events.TimeoutMarkerEvent;
import de.tum.imomesa.simulator.events.WarningMarkerEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;
import de.tum.imomesa.simulator.markers.errors.DeadlockError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MarkerManager {

	// single instance
	private static MarkerManager instance;

	// private constructor
	private MarkerManager() {
		// do nothing
	}

	// get instance
	public static MarkerManager get() {
		if (MarkerManager.instance == null) {
			MarkerManager.instance = new MarkerManager();
		}
		return MarkerManager.instance;
	}

	private ObservableList<SimulationMarker> markers = FXCollections.observableArrayList();

	public List<SimulationMarker> getMarkers() {
		return Collections.unmodifiableList(markers);
	}

	public ObservableList<SimulationMarker> getMarkersAsObservable() {
		return markers;
	}

	public void addMarker(SimulationMarker marker) {
		if (!markers.contains(marker)) {
			markers.add(marker);

			// put data on EventBus
			if (marker instanceof TimeoutMarker) {
				EventBus.getInstance().publish(new TimeoutMarkerEvent(marker));
			} else if (marker instanceof ErrorMarker) {
				EventBus.getInstance().publish(new ErrorMarkerEvent(marker));
			} else if (marker instanceof WarningMarker) {
				EventBus.getInstance().publish(new WarningMarkerEvent(marker));
			} else if (marker == null) {
				throw new IllegalArgumentException("Null marker not supported!");
			} else {
				throw new IllegalStateException("Marker type not supported: " + marker.getClass().getName());
			}
		}
	}

	public boolean containsErrorMarker() {
		for (SimulationMarker m : markers) {
			if (m instanceof ErrorMarker) {
				return true;
			}
		}
		return false;
	}

	public boolean containsTimeoutMarker() {
		for (SimulationMarker m : markers) {
			if (m instanceof TimeoutMarker) {
				return true;
			}
		}
		return false;
	}

	public boolean containsDeadlockErrorMarker() {
		for (SimulationMarker m : markers) {
			if (m instanceof DeadlockError) {
				return true;
			}
		}
		return false;
	}

	public DeadlockError getDeadlockErrorMarker() {
		for (SimulationMarker m : markers) {
			if (m instanceof DeadlockError) {
				return (DeadlockError) m;
			}
		}
		return null;
	}

	public Node getMessages() {
		// create table

		TableView<SimulationMarker> table = new TableView<>();

		// TODO: add type (as icon)
		// TableColumn<MarkerSimulation, String> type = new TableColumn<>();
		// type.setCellValueFactory(new PropertyValueFactory<MarkerSimulation,
		// String>(""));

		TableColumn<SimulationMarker, String> context = new TableColumn<>();
		context.setText("Context");
		context.setCellValueFactory(new PropertyValueFactory<SimulationMarker, String>("context"));

		TableColumn<SimulationMarker, String> step = new TableColumn<>();
		step.setText("Step");
		step.setCellValueFactory(new PropertyValueFactory<SimulationMarker, String>("step"));

		TableColumn<SimulationMarker, String> message = new TableColumn<>();
		message.setText("Message");
		message.setCellValueFactory(new PropertyValueFactory<SimulationMarker, String>("message"));

		table.setItems(markers);
		table.getColumns().add(context);
		table.getColumns().add(step);
		table.getColumns().add(message);

		return table;
	}

	public void init() {
		markers.clear();
	}
}
