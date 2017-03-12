package de.tum.imomesa.workbench.commons.converters;

import de.tum.imomesa.integrator.reports.AbstractReport;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.integrator.reports.overviews.ComponentsReport;
import de.tum.imomesa.integrator.reports.overviews.ProjectsReport;
import de.tum.imomesa.integrator.reports.overviews.ScenariosReport;
import de.tum.imomesa.integrator.reports.overviews.TemplatesReport;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.commons.nodes.PTreeTableColumn;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.util.Callback;

public class ReportConverter {

	public static TreeTableView<AbstractReport<?>> convert(TreeItem<AbstractReport<?>> report) {
		TreeTableView<AbstractReport<?>> view = new TreeTableView<>(report);

		// Columns

		PTreeTableColumn<AbstractReport<?>, String> nameColumn = new PTreeTableColumn<>("Name", 0.4);
		PTreeTableColumn<AbstractReport<?>, Double> successesColumn = new PTreeTableColumn<>("Successes (in %)", 0.2);
		PTreeTableColumn<AbstractReport<?>, Double> failuresColumn = new PTreeTableColumn<>("Failures (in %)", 0.2);
		PTreeTableColumn<AbstractReport<?>, Double> timeoutsColumn = new PTreeTableColumn<>("Timeouts (in %)", 0.2);

		// Cell factories

		successesColumn.setCellFactory(
				new Callback<TreeTableColumn<AbstractReport<?>, Double>, TreeTableCell<AbstractReport<?>, Double>>() {
					@Override
					public TreeTableCell<AbstractReport<?>, Double> call(
							TreeTableColumn<AbstractReport<?>, Double> param) {
						return new ProgressBarTreeTableCell<AbstractReport<?>>() {
							@Override
							public void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								if (!empty) {
									setStyle("-fx-accent: green");
									setText(Math.round(item * getTreeTableRow().getItem().getTotalCount()) + "");
								} else {
									setText(null);
									setGraphic(null);
									setStyle(null);
								}
							}
						};
					}
				});
		failuresColumn.setCellFactory(
				new Callback<TreeTableColumn<AbstractReport<?>, Double>, TreeTableCell<AbstractReport<?>, Double>>() {
					@Override
					public TreeTableCell<AbstractReport<?>, Double> call(
							TreeTableColumn<AbstractReport<?>, Double> param) {
						return new ProgressBarTreeTableCell<AbstractReport<?>>() {
							@Override
							public void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								if (!empty) {
									setStyle("-fx-accent: red");
									setText(Math.round(item * getTreeTableRow().getItem().getTotalCount()) + "");
								} else {
									setText(null);
									setGraphic(null);
									setStyle(null);
								}
							}
						};
					}
				});
		timeoutsColumn.setCellFactory(
				new Callback<TreeTableColumn<AbstractReport<?>, Double>, TreeTableCell<AbstractReport<?>, Double>>() {
					@Override
					public TreeTableCell<AbstractReport<?>, Double> call(
							TreeTableColumn<AbstractReport<?>, Double> param) {
						return new ProgressBarTreeTableCell<AbstractReport<?>>() {
							@Override
							public void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								if (!empty) {
									setStyle("-fx-accent: blue");
									setText(Math.round(item * getTreeTableRow().getItem().getTotalCount()) + "");
								} else {
									setText(null);
									setGraphic(null);
									setStyle(null);
								}
							}
						};
					}
				});

		// Cell value factories

		nameColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<AbstractReport<?>, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<AbstractReport<?>, String> param) {
						return new ReadOnlyStringWrapper(param.getValue().getValue().getName());
					}
				});
		successesColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<AbstractReport<?>, Double>, ObservableValue<Double>>() {
					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Double> call(CellDataFeatures<AbstractReport<?>, Double> param) {
						int success = param.getValue().getValue().getSuccessCount();
						int total = param.getValue().getValue().getTotalCount();
						if (total == 0) {
							total = 1;
						}
						return (ObservableValue<Double>) (ObservableValue<?>) new ReadOnlyDoubleWrapper(
								1.0 * success / total);
					}
				});
		failuresColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<AbstractReport<?>, Double>, ObservableValue<Double>>() {
					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Double> call(CellDataFeatures<AbstractReport<?>, Double> param) {
						int failure = param.getValue().getValue().getFailureCount();
						int total = param.getValue().getValue().getTotalCount();
						if (total == 0) {
							total = 1;
						}
						return (ObservableValue<Double>) (ObservableValue<?>) new ReadOnlyDoubleWrapper(
								1.0 * failure / total);
					}
				});
		timeoutsColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<AbstractReport<?>, Double>, ObservableValue<Double>>() {
					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Double> call(CellDataFeatures<AbstractReport<?>, Double> param) {
						int timeout = param.getValue().getValue().getTimeoutCount();
						int total = param.getValue().getValue().getTotalCount();
						if (total == 0) {
							total = 1;
						}
						return (ObservableValue<Double>) (ObservableValue<?>) new ReadOnlyDoubleWrapper(
								1.0 * timeout / total);
					}
				});

		// Add

		view.getColumns().add(nameColumn);
		view.getColumns().add(successesColumn);
		view.getColumns().add(failuresColumn);
		view.getColumns().add(timeoutsColumn);

		// Return

		return view;
	}

	public static TreeItem<AbstractReport<?>> convert(WorkspaceReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getIcon(report.getElement().getClass()));

		item.setExpanded(!report.getSuccessFlag());

		if (report.getProjectsReport().getReports().size() > 0) {
			item.getChildren().add(convert(report.getProjectsReport()));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ProjectReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getIcon(report.getElement().getClass()));

		item.setExpanded(!report.getSuccessFlag());

		if (report.getTemplatesReport().getReports().size() > 0) {
			item.getChildren().add(convert(report.getTemplatesReport()));
		}
		if (report.getComponentReport() != null) {
			item.getChildren().add(convert(report.getComponentReport()));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ComponentReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getIcon(report.getElement().getClass()));

		item.setExpanded(!report.getSuccessFlag());

		if (report.getComponentsReport().getReports().size() > 0) {
			item.getChildren().add(convert(report.getComponentsReport()));
		}
		if (report.getScenariosReport().getReports().size() > 0) {
			item.getChildren().add(convert(report.getScenariosReport()));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ScenarioReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getIcon(report.getElement().getClass()));

		item.setExpanded(!report.getSuccessFlag());

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ProjectsReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getFolderIcon(Project.class));

		item.setExpanded(!report.getSuccessFlag());

		for (ProjectReport nestedReport : report.getReports()) {
			item.getChildren().add(convert(nestedReport));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(TemplatesReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getFolderIcon(Component.class));

		item.setExpanded(!report.getSuccessFlag());

		for (ComponentReport nestedReport : report.getReports()) {
			item.getChildren().add(convert(nestedReport));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ComponentsReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getFolderIcon(Component.class));

		item.setExpanded(!report.getSuccessFlag());

		for (ComponentReport nestedReport : report.getReports()) {
			item.getChildren().add(convert(nestedReport));
		}

		return item;
	}

	public static TreeItem<AbstractReport<?>> convert(ScenariosReport report) {
		TreeItem<AbstractReport<?>> item = new TreeItem<>(report, ImageHelper.getFolderIcon(Scenario.class));

		item.setExpanded(!report.getSuccessFlag());

		for (ScenarioReport nestedReport : report.getReports()) {
			item.getChildren().add(convert(nestedReport));
		}

		return item;
	}

}
