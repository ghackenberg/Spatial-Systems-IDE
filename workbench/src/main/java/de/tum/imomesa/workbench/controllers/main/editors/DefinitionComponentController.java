package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.integrator.Integrator;
import de.tum.imomesa.integrator.events.ProgressChangeEvent;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.converters.ReportConverter;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public class DefinitionComponentController implements AbstractElementController<DefinitionComponent>, EventHandler {
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Spinner<Number> spinner;
	
	private DefinitionComponent component;

	@Override
	public void setElement(DefinitionComponent e) {
		component = e;
	}
	
	public void handle(ProgressChangeEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setProgress(event.getProgress());
			}
		});
	}
	
	public void startIntegration() {
		DefinitionComponentController self = this;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				EventBus.getInstance().subscribe(self);
				
				// Create integrator
				Integrator integrator = new Integrator(spinner.getValue().intValue());
				// Process project
				ComponentReport report = integrator.process(component);
				
				EventBus.getInstance().unsubscribe(self);
				
				Platform.runLater(new Runnable() {
					public void run() {
						borderPane.setCenter(ReportConverter.convert(ReportConverter.convert(report)));
					}
				});
			}
		});
		
		thread.start();
	}
	
}
