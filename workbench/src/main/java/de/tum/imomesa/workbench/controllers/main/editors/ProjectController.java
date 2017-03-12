package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.integrator.Integrator;
import de.tum.imomesa.integrator.events.ProgressChangeEvent;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.workbench.commons.converters.ReportConverter;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public class ProjectController implements AbstractElementController<Project>, EventHandler {
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Spinner<Number> spinner;
	
	private Project project;

	@Override
	public void setElement(Project e) {
		project = e;
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
		ProjectController self = this;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				EventBus.getInstance().subscribe(self);
				
				// Create integrator
				Integrator integrator = new Integrator(spinner.getValue().intValue());
				// Process project
				ProjectReport report = integrator.process(project);
				
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
