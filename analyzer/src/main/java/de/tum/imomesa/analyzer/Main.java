package de.tum.imomesa.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.tum.imomesa.analyzer.aggregations.elements.ElementComponentCountAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementComponentTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementComponentTransitionAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementCountAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementFeatureCountAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementFeatureTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.elements.ElementFeatureTransitionAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeComponentFeaturePlotAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeComponentScenarioPlotAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeElementComponentFeatureAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeElementFeatureTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeSimulationComponentResultAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeSimulationGraphAggregation;
import de.tum.imomesa.analyzer.aggregations.prototypes.PrototypeSimulationPlotAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsCategoryCountAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsCategoryDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsCategoryTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsClassCountAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsClassDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsClassTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsComponentCountAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsComponentDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsComponentTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.semantics.SemanticsCountAggregation;
import de.tum.imomesa.analyzer.aggregations.sessions.SessionCountAggregation;
import de.tum.imomesa.analyzer.aggregations.sessions.SessionDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.sessions.SessionTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationComponentCountAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationComponentTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationComponentTransitionAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationCountAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationResultCountAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationResultTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationResultTransitionAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationScenarioCountAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationScenarioTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.simulations.SimulationScenarioTransitionAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxCategoryCountAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxCategoryDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxCategoryTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxClassCountAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxClassDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxClassTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxComponentCountAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxComponentDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxComponentTimelineAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxCountAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxFeatureCountAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxFeatureDurationAggregation;
import de.tum.imomesa.analyzer.aggregations.syntax.SyntaxFeatureTimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.nodes.MainPane;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;
import de.tum.imomesa.database.changes.UpdateChange;
import de.tum.imomesa.database.client.Client;
import de.tum.imomesa.database.client.Converter;
import de.tum.imomesa.database.client.Socket;
import de.tum.imomesa.database.server.Database;
import de.tum.imomesa.database.server.Server;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.volumes.AtomicVolume;
import de.tum.imomesa.workbench.commons.events.ProgramEndEvent;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	private static final int PORT = 8080;
	private static final String HOST = "localhost";
	private static final int BINS_PER_HOUR = 1;
	private static final int SNAPSHOTS_RELATIVE = 1;
	private static final long SNAPSHOTS_ABSOLUTE = 1000 * 60 * 1;

	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}

	private List<Change> changes = new ArrayList<>();
	private List<Event> events = new ArrayList<>();

	@Override
	public void init() {
		try {
			boolean snapshots = getParameters().getUnnamed().contains("snapshots");

			File runFolder = new File("temp");

			if (runFolder.exists()) {
				runFolder.delete();
			}

			// Start database
			Database.createInstance(runFolder);

			Server dbServer = new Server(PORT);
			Client dbClient = new Client(HOST, PORT);
			Socket dbSocket = new Socket(dbClient);

			dbServer.start();
			dbClient.start();

			// Parse parameters
			String workspacePath = getParameters().getRaw().get(0);

			File workspaceFolder = new File(workspacePath);
			File databaseLogFile = new File(workspaceFolder, "database.log");
			File eventLogFile = new File(workspaceFolder, "event.log");

			// Parse database log json
			BufferedReader databaseLogReader = new BufferedReader(new FileReader(databaseLogFile));

			List<JSONObject> databaseLogEntries = new ArrayList<>();

			String databaseLogLine;
			while ((databaseLogLine = databaseLogReader.readLine()) != null) {
				databaseLogEntries.add(new JSONObject(databaseLogLine));
			}

			databaseLogReader.close();

			// Parse event log json
			BufferedReader eventLogReader = new BufferedReader(new FileReader(eventLogFile));

			List<JSONObject> eventLogEntries = new ArrayList<>();

			String eventLogLine;
			while ((eventLogLine = eventLogReader.readLine()) != null) {
				eventLogEntries.add(new JSONObject(eventLogLine));
			}

			eventLogReader.close();

			// Parse changes
			for (JSONObject json : databaseLogEntries) {
				Double client = json.getDouble("client");
				Double key = json.getDouble("key");
				Long timestamp = json.getLong("timestamp");
				if (json.has("type")) {
					String type = json.getString("type");
					changes.add(new ManageChange(client, key, timestamp, Class.forName(type)));
				} else {
					if (json.has("name")) {
						String name = json.getString("name");
						Object value = json.get("value");
						changes.add(new UpdateChange(client, key, timestamp, name, value));
					} else {
						changes.add(new ReleaseChange(client, key, timestamp));
					}
				}
			}

			// Load database and parse events
			Snapshot snapshot_relative = new Snapshot(dbClient, new File("change"));
			Snapshot snapshop_absolute = new Snapshot(dbClient, new File("time"));

			int sessions = 0;

			long last_start = 0;
			long dead_time = 0;

			int snap_count = 0;
			long last_snap = 0;

			List<Change> queueChanges = new ArrayList<>(changes);
			List<JSONObject> queueJson = new ArrayList<>(databaseLogEntries);

			for (JSONObject json : eventLogEntries) {
				// Obtain event timestamp
				Long timestamp = json.getJSONObject("fields").getLong("timestamp");

				// Apply database changes first!
				while (queueChanges.size() > 0 && queueChanges.get(0).getTimestamp() <= timestamp) {
					// Remove change
					Change curChange = queueChanges.remove(0);

					// Update timestamp
					curChange.setTimesamp(curChange.getTimestamp() - dead_time);

					// Optionally take snapshot
					if (snapshots && curChange.getTimestamp() - last_snap >= SNAPSHOTS_ABSOLUTE) {
						last_snap += SNAPSHOTS_ABSOLUTE;
						snapshop_absolute.create(last_snap);
					}

					// Remove json
					JSONObject curJson = queueJson.remove(0);

					if (curChange instanceof ReleaseChange) {
						curChange.setObject(dbClient.getObject(curChange.getKey()));
					}

					// Apply change
					dbSocket.onMessage(curJson.toString());

					if (curChange instanceof ManageChange || curChange instanceof UpdateChange) {
						curChange.setObject(dbClient.getObject(curChange.getKey()));
					}

					// Optionally take snapshot
					if (snapshots && curChange instanceof UpdateChange) {
						UpdateChange update = (UpdateChange) curChange;

						Element object = (Element) curChange.getObject();

						AtomicVolume volume = object.getFirstAncestorByType(AtomicVolume.class);
						Transform transform = object.getFirstAncestorByType(Transform.class);

						if (volume != null || transform != null) {
							if (update.nameProperty().get().equals("angle")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("radius")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("width")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("height")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("depth")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("x")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("y")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("z")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("red")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("green")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							} else if (update.nameProperty().get().equals("blue")) {
								if (++snap_count % SNAPSHOTS_RELATIVE == 0) {
									snapshot_relative.create(update.getTimestamp());
								}
							}
						}
					}
				}

				// Convert json to event
				Event event = (Event) Converter.convert(dbClient, json);

				// Handle program end events
				if (event instanceof ProgramStartEvent) {
					if (sessions == 0) {
						dead_time += event.getTimestamp() - last_start;
					}
					sessions++;
				}
				// Handle program start events
				if (event instanceof ProgramEndEvent) {
					sessions--;
					if (sessions == 0) {
						last_start = event.getTimestamp();
					}
				}

				// Update event timestamp
				event.setTimestamp(event.getTimestamp() - dead_time);

				// Optionally take snapshot
				if (snapshots && event.getTimestamp() - last_snap >= SNAPSHOTS_ABSOLUTE) {
					last_snap += SNAPSHOTS_ABSOLUTE;
					snapshop_absolute.create(last_snap);
				}

				// Remember event
				events.add(event);
			}

			// Take last snapshot
			if (snapshots) {
				snapshop_absolute.create(events.get(events.size() - 1).getTimestamp());

				snapshot_relative.finish();
				snapshop_absolute.finish();
			}

			// Clear json
			databaseLogEntries.clear();
			eventLogEntries.clear();

			// Wait for changes to be processed!
			Thread.sleep(5000);

			// Stop database
			dbClient.stop();
			dbServer.stop();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * // Normalize Normalizer.normalize(changes, events);
		 */

		// Discretize
		Discretizer discretizer = new Discretizer(changes, events, BINS_PER_HOUR);

		// create stage
		MainPane processorPane = new MainPane();
		processorPane.addProcessors("Session activities", new SessionCountAggregation(changes, events),
				new SessionTimelineAggregation(changes, events, discretizer),
				new SessionDurationAggregation(changes, events));
		processorPane.addProcessors("Modeling activities", new ElementCountAggregation(changes, events),
				new ElementComponentCountAggregation(changes, events),
				new ElementComponentTimelineAggregation(changes, events, discretizer),
				new ElementComponentTransitionAggregation(changes, events),
				new ElementFeatureCountAggregation(changes, events),
				new ElementFeatureTimelineAggregation(changes, events, discretizer),
				new ElementFeatureTransitionAggregation(changes, events));
		processorPane.addProcessors("Simulation activities", new SimulationCountAggregation(changes, events),
				new SimulationDurationAggregation(changes, events),
				new SimulationComponentCountAggregation(changes, events),
				new SimulationComponentTimelineAggregation(changes, events, discretizer),
				new SimulationComponentTransitionAggregation(changes, events),
				new SimulationScenarioCountAggregation(changes, events),
				new SimulationScenarioTimelineAggregation(changes, events, discretizer),
				new SimulationScenarioTransitionAggregation(changes, events),
				new SimulationResultCountAggregation(changes, events),
				new SimulationResultTimelineAggregation(changes, events, discretizer),
				new SimulationResultTransitionAggregation(changes, events));
		processorPane.addProcessors("Syntactic issues", new SyntaxCountAggregation(changes, events),
				new SyntaxComponentCountAggregation(changes, events),
				new SyntaxComponentTimelineAggregation(changes, events, discretizer),
				new SyntaxComponentDurationAggregation(changes, events),
				new SyntaxClassCountAggregation(changes, events),
				new SyntaxClassTimelineAggregation(changes, events, discretizer),
				new SyntaxClassDurationAggregation(changes, events),
				new SyntaxCategoryCountAggregation(changes, events),
				new SyntaxCategoryTimelineAggregation(changes, events, discretizer),
				new SyntaxCategoryDurationAggregation(changes, events),
				new SyntaxFeatureCountAggregation(changes, events),
				new SyntaxFeatureTimelineAggregation(changes, events, discretizer),
				new SyntaxFeatureDurationAggregation(changes, events));
		processorPane.addProcessors("Semantic issues", new SemanticsCountAggregation(changes, events),
				new SemanticsComponentCountAggregation(changes, events),
				new SemanticsComponentTimelineAggregation(changes, events, discretizer),
				new SemanticsComponentDurationAggregation(changes, events),
				new SemanticsClassCountAggregation(changes, events),
				new SemanticsClassTimelineAggregation(changes, events, discretizer),
				new SemanticsClassDurationAggregation(changes, events),
				new SemanticsCategoryCountAggregation(changes, events),
				new SemanticsCategoryTimelineAggregation(changes, events, discretizer),
				new SemanticsCategoryDurationAggregation(changes, events));
		processorPane.addProcessors("Prototype aggregations",
				new PrototypeComponentFeaturePlotAggregation(changes, events, discretizer),
				new PrototypeComponentScenarioPlotAggregation(changes, events, discretizer),
				new PrototypeElementFeatureTimelineAggregation(changes, events, discretizer),
				new PrototypeElementComponentFeatureAggregation(changes, events, discretizer),
				new PrototypeSimulationComponentResultAggregation(changes, events, discretizer),
				new PrototypeSimulationPlotAggregation(changes, events),
				new PrototypeSimulationGraphAggregation(changes, events));

		ToolBar topToolBar = new ToolBar();
		topToolBar.getItems().add(new Label("About"));
		topToolBar.getItems().add(new Button("Manual", new ImageView(new Image("manual.png"))));
		topToolBar.getItems().add(new Button("Credit", new ImageView(new Image("credit.png"))));

		ToolBar bottomToolBar = new ToolBar();
		bottomToolBar.getItems().add(new Label(
				"Copyright 2015, Georg Hackenberg and Thomas Stocker, Lehrstuhl für Software and Systems Engineering, Technische Universität München"));

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(topToolBar);
		borderPane.setCenter(processorPane);
		borderPane.setBottom(bottomToolBar);

		Scene scene = new Scene(borderPane);

		primaryStage = new Stage(StageStyle.DECORATED);
		primaryStage.setTitle("MACON Analyzer - Main");
		primaryStage.getIcons().add(new Image("icon.png"));
		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
