package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.integrator.Integrator;
import de.tum.imomesa.integrator.events.ProgressChangeEvent;
import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.workbench.commons.converters.ReportConverter;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public class WorkspaceController implements AbstractEditorChildController<Workspace>, EventHandler{
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Spinner<Number> spinner;
	
	private Workspace workspace;

	@Override
	public void setRootElement(Workspace e) {
		workspace = e;
	}
	
	@Override
	public void setSelectedElement(Element element) {
		
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
		WorkspaceController self = this;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				EventBus.getInstance().subscribe(self);
				
				// Create integrator
				Integrator integrator = new Integrator(spinner.getValue().intValue());
				// Process project
				WorkspaceReport report = integrator.process(workspace);
				
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
