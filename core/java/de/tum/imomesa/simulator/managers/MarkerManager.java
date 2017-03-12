package de.tum.imomesa.simulator.managers;

import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import de.tum.imomesa.simulator.markers.Marker;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class MarkerManager {

	// ***********************************************************************************
	// Singleton implementation
	// ***********************************************************************************
	// single instance
	private static MarkerManager instance;
	
	// private constructor
	private MarkerManager() {
		// do nothing
	}
	
	// get instance
	public static MarkerManager get() {
	    if (MarkerManager.instance == null) {
	    	MarkerManager.instance = new MarkerManager ();
	      }
	    return MarkerManager.instance;
	}
	
	// ***********************************************************************************
	// Attributes
	// ***********************************************************************************
	private ObservableList<Marker> markers = FXCollections.observableArrayList();
	
	// ***********************************************************************************
	// Methods
	// ***********************************************************************************
	public void setTable(TableView<Marker> tableProblems) {
		tableProblems.setItems(markers);
	}
	
	public List<Marker> getMarkers() {
		return Collections.unmodifiableList(markers);
	}

	public void addMarker(Marker marker) {
		if(!markers.contains(marker)) {
			markers.add(marker);
		}
	}
	
	public void removeMarker(Marker marker) {
		if(markers.contains(marker)) {	
			markers.remove(marker);
		}
	}

	public boolean containsError() {
		for(Marker m : markers) {
			if(m instanceof ErrorMarker) {
				return true;
			}
		}
		
		return false;
	}
	
	public Node getMessages() {
		// create table
		
		TableView<Marker> table = new TableView<>();
		
		// TODO: add type (as icon)
//		TableColumn<MarkerSimulation, String> type = new TableColumn<>();
//		type.setCellValueFactory(new PropertyValueFactory<MarkerSimulation, String>(""));
		
		TableColumn<Marker, String> context = new TableColumn<>();
		context.setText("Context");
		context.setCellValueFactory(new PropertyValueFactory<Marker, String>("context"));

		TableColumn<Marker, String> step = new TableColumn<>();
		step.setText("Step");
		step.setCellValueFactory(new PropertyValueFactory<Marker, String>("step"));

		TableColumn<Marker, String> message = new TableColumn<>();
		message.setText("Message");
		message.setCellValueFactory(new PropertyValueFactory<Marker, String>("message"));

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
