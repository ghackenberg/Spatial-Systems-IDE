package de.tum.imomesa.integrator;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.integrator.events.EndEvent;
import de.tum.imomesa.integrator.events.StartEvent;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.simulator.managers.MarkerManager;

public class Integrator {
	
	private long maxtime;
	private String path;
	
	public Integrator(long maxtime) {
		this(maxtime, "");
	}
	
	public Integrator(long maxtime, String path) {
		this.maxtime = maxtime;
		this.path = path;
	}
	
	private int countScenarios(Workspace element) {
		int result = 0;
		
		for (Project child : element.getProjects()) {
			result += countScenarios(child);
		}
		
		return result;
	}
	
	private int countScenarios(Project element) {
		int result = 0;
		
		for (DefinitionComponent child : element.getTemplates()) {
			result += countScenarios(child);
		}
		if (element.getComponent() != null) {
			result += countScenarios(element.getComponent());
		}
		
		return result;
	}
	
	private int countScenarios(DefinitionComponent element) {
		int result = 0;
		
		for (Component<?> child : element.getComponents()) {
			if (child instanceof DefinitionComponent) {
				result += countScenarios((DefinitionComponent) child);
			}
		}
		result += element.getScenarios().size();
		
		return result;
	}
	
	public WorkspaceReport process(Workspace element) {
		EventBus.getInstance().publish(new StartEvent());
		
		WorkspaceReport report = process(element, new Progress(countScenarios(element)));
		
		EventBus.getInstance().publish(new EndEvent());
		
		return report;
	}
	
	public ProjectReport process(Project element) {
		EventBus.getInstance().publish(new StartEvent());
		
		ProjectReport report = process(element, new Progress(countScenarios(element)));
		
		EventBus.getInstance().publish(new EndEvent());
		
		return report;
	}
	
	public ComponentReport process(DefinitionComponent element) {
		EventBus.getInstance().publish(new StartEvent());
		
		ComponentReport report = process(element, new Progress(countScenarios(element)));
		
		EventBus.getInstance().publish(new EndEvent());
		
		return report;
	}
	
	private WorkspaceReport process(Workspace workspace, Progress progress) {
		
		System.out.println("Processing workspace " + workspace);
		
		WorkspaceReport report = new WorkspaceReport(workspace);
		
		for (Project project : workspace.getProjects()) {
			report.getProjectsReport().addReport(process(project, progress));
		}
		
		report.getProjectsReport().stop();
		
		report.stop();
		
		return report;
		
	}
	
	private ProjectReport process(Project project, Progress progress) {
		
		System.out.println("Processing project " + project);
		
		ProjectReport report = new ProjectReport(project);
		
		for (DefinitionComponent component : project.getTemplates()) {	
			report.getTemplatesReport().addReport(process(component, progress));
		}

		report.getTemplatesReport().stop();
		
		if (project.getComponent() != null) {
			report.setComponentReport(process(project.getComponent(), progress));
		}
		
		report.stop();
		
		return report;
		
	}
	
	private ComponentReport process(DefinitionComponent component, Progress progress) {
		
		System.out.println("Processing component " + component);
		
		ComponentReport report = new ComponentReport(component);
		
		for (Scenario scenario : component.getScenarios()) {
			try {
				System.out.print("Simulating scenario " + scenario + ": ");
				
				ScenarioReport nested_report = new ScenarioReport(scenario);
				
				Simulator simulator = new Simulator(component, scenario, maxtime, path);
				simulator.start();
				simulator.join();
				
				if (MarkerManager.get().containsErrorMarker()) {
					System.out.println("failure");
				}
				else if (MarkerManager.get().containsTimeoutMarker()) {
					System.out.println("timeout");
				}
				else {
					System.out.println("success");
				}
				
				nested_report.getMarkers().addAll(MarkerManager.get().getMarkers());
				
				nested_report.stop();
				
				report.getScenariosReport().addReport(nested_report);
				
				progress.increment();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
		
		report.getScenariosReport().stop();
		
		for (Component<?> child : component.getComponents()) {
			if (child instanceof DefinitionComponent) {
				report.getComponentsReport().addReport(process((DefinitionComponent) child, progress));
			}
		}
		
		report.getComponentsReport().stop();
		
		report.stop();
		
		return report;
		
	}

}
