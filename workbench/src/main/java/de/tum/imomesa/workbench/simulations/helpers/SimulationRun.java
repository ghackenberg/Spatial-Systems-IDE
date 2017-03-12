package de.tum.imomesa.workbench.simulations.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.simulator.markers.SimulationMarker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

public class SimulationRun {

	// Constructor

	public SimulationRun() {
		markers.addListener(new ListChangeListener<SimulationMarker>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends SimulationMarker> c) {
				Map<String, Integer> types = new HashMap<>();
				for (SimulationMarker marker : getMarkers()) {
					String type = marker.getClass().getSimpleName();
					if (!types.containsKey(type)) {
						types.put(type, 0);
					}
					types.put(type, types.get(type) + 1);
				}
				setMarkerString(types.toString());
			}
		});
	}

	// Timestamp

	private LongProperty timestamp = new SimpleLongProperty();

	public long getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(long timestamp) {
		this.timestamp.set(timestamp);
	}

	public LongProperty timestampProperty() {
		return timestamp;
	}

	// Duration

	private LongProperty duration = new SimpleLongProperty();

	public long getDuration() {
		return duration.get();
	}

	public void setDuration(long duration) {
		this.duration.set(duration);
	}

	public LongProperty durationProperty() {
		return duration;
	}

	// Finished

	private BooleanProperty finished = new SimpleBooleanProperty(false);

	public boolean getFinished() {
		return finished.get();
	}

	public void setFinished(boolean finished) {
		this.finished.set(finished);
	}

	public BooleanProperty finishedProperty() {
		return finished;
	}

	// Markers

	private ListProperty<SimulationMarker> markers = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<SimulationMarker> getMarkers() {
		return markers.get();
	}

	public void setMarkers(List<SimulationMarker> markers) {
		this.markers.set(FXCollections.observableList(markers));
	}

	public ListProperty<SimulationMarker> markersProperty() {
		return markers;
	}

	// Errors

	private BooleanProperty errors = new SimpleBooleanProperty(false);

	public boolean getErrors() {
		return errors.get();
	}

	public void setErrors(boolean errors) {
		this.errors.set(errors);
	}

	public BooleanProperty errorsProperty() {
		return errors;
	}

	// Warnings

	private BooleanProperty warnings = new SimpleBooleanProperty(false);

	public boolean getWarnings() {
		return warnings.get();
	}

	public void setWarnings(boolean warnings) {
		this.warnings.set(warnings);
	}

	public BooleanProperty warningsProperty() {
		return warnings;
	}

	// Timeouts

	private BooleanProperty timeouts = new SimpleBooleanProperty(false);

	public boolean getTimeouts() {
		return timeouts.get();
	}

	public void setTimeouts(boolean timeouts) {
		this.timeouts.set(timeouts);
	}

	public BooleanProperty timeoutsProperty() {
		return timeouts;
	}

	// Message

	private StringProperty message = new SimpleStringProperty("Running ...");

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public StringProperty messageProperty() {
		return message;
	}

	// Duration string

	private StringProperty durationString = new SimpleStringProperty();

	public String getDurationString() {
		return durationString.get();
	}

	public void setDurationString(String durationString) {
		this.durationString.set(durationString);
	}

	public StringProperty durationStringProperty() {
		return durationString;
	}

	// Time string

	private StringProperty timeString = new SimpleStringProperty();

	public String getTimeString() {
		return timeString.get();
	}

	public void setTimeString(String timeString) {
		this.timeString.set(timeString);
	}

	public StringProperty timeStringProperty() {
		return timeString;
	}

	// Marker string

	private StringProperty markerString = new SimpleStringProperty("{}");

	public String getMarkerString() {
		return markerString.get();
	}

	public void setMarkerString(String markerString) {
		this.markerString.set(markerString);
	}

	public StringProperty markerStringProperty() {
		return markerString;
	}

}
