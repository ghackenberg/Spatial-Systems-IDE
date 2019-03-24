package de.tum.imomesa.workbench.attributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.ObservedElement;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Guard;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.expressions.CompositeExpression;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.TerniaryExpression;
import de.tum.imomesa.model.expressions.booleans.ConstantExpressionBoolean;
import de.tum.imomesa.model.expressions.numbers.ConstantExpressionNumber;
import de.tum.imomesa.model.ports.DataPort;
import de.tum.imomesa.model.ports.GenericPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.TranslationTransform;
import de.tum.imomesa.model.volumes.AtomicVolume;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.attributes.listeners.ChangeListenerComponentProxyTemplate;
import de.tum.imomesa.workbench.attributes.listeners.ChangeListenerObservationType;
import de.tum.imomesa.workbench.attributes.listeners.ChangeListenerPortDirection;
import de.tum.imomesa.workbench.attributes.listeners.ChangeListenerTemplate;
import de.tum.imomesa.workbench.attributes.managers.ListenerManager;
import de.tum.imomesa.workbench.attributes.nodes.AttributesGrid;
import de.tum.imomesa.workbench.attributes.nodes.NumberField;
import de.tum.imomesa.workbench.commons.helpers.ContextHelper;
import de.tum.imomesa.workbench.commons.helpers.ExecutableContextHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class AttributesEditorBuilder {

	public static Node getAttributes(Element element) {
		ListenerManager.getListenerManager().removeAllListener();

		AttributesGrid grid = new AttributesGrid();
		
		List<Class<?>> types = new ArrayList<>();

		Class<?> type = element.getClass();

		while (Element.class.isAssignableFrom(type)) {
			types.add(0, type);
			
			type = type.getSuperclass();
		}
		
		for (Class<?> typeIter : types) {
			try {
				grid.addType(typeIter);

				Method method = AttributesEditorBuilder.class.getMethod("addProperties", AttributesGrid.class, typeIter);

				method.invoke(null, grid, element);
			} catch (NoSuchMethodException e) {
				grid.addEmpty();
			} catch (Exception e) {
				grid.addException(e);
			}

		}

		return grid;
	}

	public static void addProperties(AttributesGrid grid, Element e) {
		TextField tfKey = new TextField();
		tfKey.setText("" + StorageManager.getInstance().getKey(e));
		tfKey.setDisable(true);
		grid.addProperty("Key:", tfKey);
		
		TextField tfPath = new TextField();
		tfPath.textProperty().bind(e.getElementPath());
		tfPath.setDisable(true);
		grid.addProperty("Path:", tfPath);
	}

	public static void addProperties(AttributesGrid grid, NamedElement e) {
		// Name
		TextField tfName = new TextField();
		Bindings.bindBidirectional(tfName.textProperty(), e.nameProperty());
		grid.addProperty("Name:", tfName);

		// Description
		TextArea tfDescription = new TextArea();
		tfDescription.setPrefRowCount(3);
		tfDescription.setWrapText(true);
		Bindings.bindBidirectional(tfDescription.textProperty(), e.descriptionProperty());
		grid.addProperty("Description:", tfDescription);
	}

	public static void addProperties(AttributesGrid grid, ObservedElement e) {
		// Write Type
		TextField lWriteType = new TextField();
		lWriteType.textProperty().bind(e.writeTypeProperty().asString());
		lWriteType.setDisable(true);
		grid.addProperty("Write types:", lWriteType);

		// Read Type
		TextField lReadType = new TextField();
		lReadType.textProperty().bind(e.readTypeProperty().asString());
		lReadType.setDisable(true);
		grid.addProperty("Read type:", lReadType);
	}

	public static void addProperties(AttributesGrid grid, Observation o) {
		// expression string
		TextField lExpressionString = new TextField();
		if (o.getDefault() != null) {
			lExpressionString.textProperty().bind(o.getDefault().toStringExpression());
		} else {
			lExpressionString.setText("No expression specified.");
			lExpressionString.setFont(Font.font("System", FontPosture.ITALIC, 12));
		}
		lExpressionString.setDisable(true);
		grid.addProperty("Default:", lExpressionString);
	}

	public static void addProperties(AttributesGrid grid, Project p) {
		// Component
		TextField lComponent = new TextField();
		if (p.getComponent() != null) {
			lComponent.textProperty().bind(p.getComponent().nameProperty());
		} else {
			lComponent.setText("No component set.");
			lComponent.setFont(Font.font("System", FontPosture.ITALIC, 12));
		}
		lComponent.setDisable(true);
		grid.addProperty("Component:", lComponent);
	}

	public static void addProperties(AttributesGrid grid, Component<?> component) {
		// x
		NumberField x = new NumberField();
		x.textProperty().bindBidirectional(component.xProperty(), new NumberConverter());
		grid.addProperty("X:", x);

		// y
		NumberField y = new NumberField();
		y.textProperty().bindBidirectional(component.yProperty(), new NumberConverter());
		grid.addProperty("Y:", y);

		// width
		NumberField width = new NumberField();
		width.textProperty().bindBidirectional(component.widthProperty(), new NumberConverter());
		grid.addProperty("Width:", width);

		// height
		NumberField height = new NumberField();
		height.textProperty().bindBidirectional(component.heightProperty(), new NumberConverter());
		grid.addProperty("Height:", height);

		// camera distance
		NumberField dtfCamDist = new NumberField();
		dtfCamDist.textProperty().bindBidirectional(component.cameraDistanceProperty(), new NumberConverter());
		grid.addProperty("Camera Distance:", dtfCamDist);

		// camera translate x
		NumberField dtfTransX = new NumberField();
		dtfTransX.textProperty().bindBidirectional(component.cameraTranslateXProperty(), new NumberConverter());
		grid.addProperty("Camera Translate X:", dtfTransX);

		// camera translate y
		NumberField dtfTransY = new NumberField();
		dtfTransY.textProperty().bindBidirectional(component.cameraTranslateYProperty(), new NumberConverter());
		grid.addProperty("Camera Translate Y:", dtfTransY);

		// camera translate z
		NumberField dtfTransZ = new NumberField();
		dtfTransZ.textProperty().bindBidirectional(component.cameraTranslateZProperty(), new NumberConverter());
		grid.addProperty("Camera Translate Z:", dtfTransZ);

		// camera angle X
		NumberField dtfAngleX = new NumberField();
		dtfAngleX.textProperty().bindBidirectional(component.cameraAngleXProperty(), new NumberConverter());
		grid.addProperty("Camera Angle X", dtfAngleX);

		// camera angle y
		NumberField dtfAngleY = new NumberField();
		dtfAngleY.textProperty().bindBidirectional(component.cameraAngleYProperty(), new NumberConverter());
		grid.addProperty("Camera Angle Y:", dtfAngleY);
	}

	public static void addProperties(AttributesGrid grid, ReferenceComponent c) {
		// Template - ComboBox

		// get all templates of the current project
		Project proj = c.getFirstAncestorByType(Project.class);
		ObservableList<DefinitionComponent> optionsComponent = FXCollections.observableArrayList(proj.getTemplates());

		ComboBox<DefinitionComponent> cbTemplate = new ComboBox<>(optionsComponent);
		cbTemplate.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cbTemplate.requestFocus();
			}
		});
		// select item
		if (c.getTemplate() != null) {
			// select the component
			cbTemplate.getSelectionModel().select(c.getTemplate());
		}

		// add change listener on combobox
		ListenerManager.getListenerManager().addListener(cbTemplate.getSelectionModel().selectedItemProperty(),
				new ChangeListenerComponentProxyTemplate(c, cbTemplate));
		grid.addProperty("Template:", cbTemplate);
	}

	public static void addProperties(AttributesGrid grid, Port port) {
		// angle
		NumberField angle = new NumberField();
		angle.textProperty().bindBidirectional(port.angleProperty(), new NumberConverter());
		grid.addProperty("Angle:", angle);

		// Direction
		if (port instanceof MaterialPort) {
			// only display if MaterialPort as the direction is predetermined by
			// the type
			TextField lDirection = new TextField();
			lDirection.setText(Direction.values()[port.getDirection()].toString());
			lDirection.setDisable(true);
			grid.addProperty("Direction:", lDirection);
		} else {
			ObservableList<String> options = FXCollections.observableArrayList("INPUT", "OUTPUT");
			ComboBox<String> direction = new ComboBox<>(options);
			direction.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					direction.requestFocus();
				}
			});
			direction.getSelectionModel().select(port.getDirection());
			direction.valueProperty().addListener(new ChangeListenerPortDirection(port, options));
			grid.addProperty("Direction:", direction);
		}
	}

	public static void addProperties(AttributesGrid grid, DataPort p) {
		// Type selection menu
		List<Class<?>> contentList = FXCollections.observableArrayList(Object.class, Set.class, Number.class,
				Boolean.class);
		ComboBox<Class<?>> cbType = new ComboBox<>(FXCollections.observableArrayList(contentList));
		cbType.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cbType.requestFocus();
			}
		});
		cbType.getSelectionModel().select(p.getReadType());
		cbType.valueProperty().addListener(new ChangeListenerObservationType<Port>(p));
		grid.addProperty("Read / write type:", cbType);
	}

	public static void addProperties(AttributesGrid grid, GenericPort p) {
		// Type selection menu
		List<Class<?>> contentList = FXCollections.observableArrayList(Object.class, Set.class, Number.class,
				Boolean.class);
		ComboBox<Class<?>> cbType = new ComboBox<>(FXCollections.observableArrayList(contentList));
		cbType.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cbType.requestFocus();
			}
		});
		cbType.getSelectionModel().select(p.getReadType());
		cbType.valueProperty().addListener(new ChangeListenerObservationType<Port>(p));
		grid.addProperty("Read / write type:", cbType);
	}

	public static void addProperties(AttributesGrid grid, InteractionMaterialPort port) {
		// x
		NumberField x = new NumberField();
		x.textProperty().bindBidirectional(port.xProperty(), new NumberConverter());
		grid.addProperty("X:", x);

		// y
		NumberField y = new NumberField();
		y.textProperty().bindBidirectional(port.yProperty(), new NumberConverter());
		grid.addProperty("Y:", y);

		// width
		NumberField width = new NumberField();
		width.textProperty().bindBidirectional(port.widthProperty(), new NumberConverter());
		grid.addProperty("Width:", width);

		// height
		NumberField height = new NumberField();
		height.textProperty().bindBidirectional(port.heightProperty(), new NumberConverter());
		grid.addProperty("Height:", height);

		// check if binding
		CheckBox cbBinding = new CheckBox();
		cbBinding.selectedProperty().bindBidirectional(port.bindsVolumesProperty());
		grid.addProperty("Binds volumes:", cbBinding);

		// check if always active
		CheckBox cbActive = new CheckBox();
		cbActive.selectedProperty().bindBidirectional(port.alwaysActiveProperty());
		grid.addProperty("Always active:", cbActive);
	}

	public static void addProperties(AttributesGrid grid, LifeMaterialPort p) {
		// offer ComboBox to choose component
		Project proj = p.getFirstAncestorByType(Project.class);
		ObservableList<DefinitionComponent> optionsComponent = FXCollections.observableArrayList(proj.getTemplates());
		ComboBox<DefinitionComponent> cbComponent = new ComboBox<>(optionsComponent);
		cbComponent.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cbComponent.requestFocus();
			}
		});
		if (p.getComponent() != null) {
			// select the component
			cbComponent.getSelectionModel().select(p.getComponent());
		}
		// add listener
		cbComponent.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListenerTemplate(p.componentProperty()));
		grid.addProperty("Template:", cbComponent);
	}

	public static void addProperties(AttributesGrid grid, AtomicVolume v) {
		// color
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(Color.rgb(v.getRed(), v.getGreen(), v.getBlue()));
		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				v.setRed((int) (colorPicker.getValue().getRed() * 255));
				v.setGreen((int) (colorPicker.getValue().getGreen() * 255));
				v.setBlue((int) (colorPicker.getValue().getBlue() * 255));
			}
		});
		grid.addProperty("Color:", colorPicker);
	}
	
	public static void addProperties(AttributesGrid grid, BoxVolume v) {
		// width
		NumberField dtfWidth = new NumberField();
		dtfWidth.textProperty().bindBidirectional(v.widthProperty(), new NumberConverter());
		grid.addProperty("Width:", dtfWidth);

		// height
		NumberField dtfHeight = new NumberField();
		dtfHeight.textProperty().bindBidirectional(v.heightProperty(), new NumberConverter());
		grid.addProperty("Height:", dtfHeight);

		// depth
		NumberField dtfDepth = new NumberField();
		dtfDepth.textProperty().bindBidirectional(v.depthProperty(), new NumberConverter());
		grid.addProperty("Depth:", dtfDepth);
	}
	
	public static void addProperties(AttributesGrid grid, CylinderVolume v) {
		// radius
		NumberField dtfRadius = new NumberField();
		Bindings.bindBidirectional(dtfRadius.textProperty(), v.radiusProperty(), new NumberConverter());
		grid.addProperty("Radius:", dtfRadius);

		// height
		NumberField dtfHeight = new NumberField();
		Bindings.bindBidirectional(dtfHeight.textProperty(), v.heightProperty(), new NumberConverter());
		grid.addProperty("Height:", dtfHeight);
	}
	
	public static void addProperties(AttributesGrid grid, SphereVolume v) {
		// radius
		NumberField dtfRadius = new NumberField();
		Bindings.bindBidirectional(dtfRadius.textProperty(), v.radiusProperty(), new NumberConverter());
		grid.addProperty("Radius:", dtfRadius);
	}

	public static void addProperties(AttributesGrid grid, RotationTransform t) {
		// point
		addPoint(grid, "Axis ", t.getRotationAxe());

		// angle
		NumberField dtfAngle = new NumberField();
		dtfAngle.textProperty().bindBidirectional(t.angleProperty(), new NumberConverter());
		grid.addProperty("Angle:", dtfAngle);
	}

	public static void addProperties(AttributesGrid grid, TranslationTransform t) {
		// Vector
		addPoint(grid, "Vector ", t.getVector());
	}

	public static void addProperties(AttributesGrid grid, Property p) {
		// expression string
		TextField lExpressionString = new TextField();
		if (p.getExpression() != null) {
			lExpressionString.textProperty().bind(p.getExpression().toStringExpression());
		} else {
			lExpressionString.setText("No expression specified.");
			lExpressionString.setFont(Font.font("System", FontPosture.ITALIC, 12));
		}
		grid.addProperty("Expression:", lExpressionString);
	}

	public static void addProperties(AttributesGrid grid, Expression e) {
		// Type
		TextField lType = new TextField();
		// TODO Do not use read only wrapper here!
		lType.textProperty().bind(new ReadOnlyStringWrapper(e.getType().getName()));
		lType.setDisable(true);
		grid.addProperty("Type:", lType);

		// String
		TextField lExpressionString = new TextField();
		lExpressionString.textProperty().bind(e.toStringExpression());
		grid.addProperty("String:", lExpressionString);
	}

	public static void addProperties(AttributesGrid grid, ConstantExpressionBoolean e) {
		// create comb box
		ObservableList<Boolean> options = FXCollections.observableArrayList(true, false);
		ComboBox<Boolean> value = new ComboBox<>(options);
		value.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				value.requestFocus();
			}
		});
		value.valueProperty().bindBidirectional(e.valueProperty());
		grid.addProperty("Value:", value);
	}

	public static void addProperties(AttributesGrid grid, ConstantExpressionNumber e) {
		NumberField dtfValue = new NumberField();
		Bindings.bindBidirectional(dtfValue.textProperty(), e.doubleValueProperty(), new NumberConverter());
		grid.addProperty("Value:", dtfValue);
	}

	public static void addProperties(AttributesGrid grid, ObservationExpression e) {
		// Observation Value
		ComboBox<Observation> observations = new ComboBox<>(
				FXCollections.observableArrayList(ContextHelper.getObservations(e)));
		observations.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				observations.requestFocus();
			}
		});
		observations.valueProperty().bindBidirectional(e.observationProperty());
		grid.addProperty("Observation:", observations);

		// Delay TextField
		NumberField dtfDelay = new NumberField();
		Bindings.bindBidirectional(dtfDelay.textProperty(), e.delayProperty(), new NumberConverter());
		grid.addProperty("Delay:", dtfDelay);
	}

	public static void addProperties(AttributesGrid grid, CompositeExpression e) {
		// Attribute
		TextField lArgument = new TextField();
		// TODO Do not use read only wrapper here!
		lArgument.textProperty().bind(new ReadOnlyStringWrapper(e.getArgumentType().getName()));
		lArgument.setDisable(true);
		grid.addProperty("Argument type:", lArgument);
	}

	public static void addProperties(AttributesGrid grid, TerniaryExpression e) {
		// Attribute 1
		TextField lArgument1 = new TextField();
		// TODO Do not use read only wrapper here!
		lArgument1.textProperty().bind(new ReadOnlyStringWrapper(e.getArgumentOneType().getName()));
		lArgument1.setDisable(true);
		grid.addProperty("Argument type 1:", lArgument1);

		// Attribute 2
		TextField lArgument2 = new TextField();
		// TODO Do not use read only wrapper here!
		lArgument2.textProperty().bind(new ReadOnlyStringWrapper(e.getArgumentTwoType().getName()));
		lArgument2.setDisable(true);
		grid.addProperty("Argument type 2:", lArgument2);

		// Attribute 3
		TextField lArgument3 = new TextField();
		// TODO Do not use read only wrapper here!
		lArgument3.textProperty().bind(new ReadOnlyStringWrapper(e.getArgumentThreeType().getName()));
		lArgument3.setDisable(true);
		grid.addProperty("Argument type 3:", lArgument3);
	}

	public static void addProperties(AttributesGrid grid, Action a) {
		// Observation
		// create comb box
		ComboBox<Observation> observations = new ComboBox<>(
				FXCollections.observableArrayList(ExecutableContextHelper.getObservationsToSet(a)));
		// bind value
		observations.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				observations.requestFocus();
			}
		});
		observations.valueProperty().bindBidirectional(a.observationProperty());
		grid.addProperty("Observation:", observations);

		// expression string
		TextField lExpressionString = new TextField();
		if (a.getExpression() != null) {
			lExpressionString.textProperty().bind(a.getExpression().toStringExpression());
		} else {
			lExpressionString.setText("No expression specified.");
			lExpressionString.setFont(Font.font("System", FontPosture.ITALIC, 12));
		}
		grid.addProperty("Expression:", lExpressionString);
	}

	public static void addProperties(AttributesGrid grid, Variable v) {
		// Type selection menu
		List<Class<?>> contentList = FXCollections.observableArrayList(Object.class, Set.class, Number.class,
				Boolean.class);
		ComboBox<Class<?>> cbType = new ComboBox<>(FXCollections.observableArrayList(contentList));
		cbType.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cbType.requestFocus();
			}
		});
		cbType.getSelectionModel().select(v.getReadType());
		cbType.valueProperty().addListener(new ChangeListenerObservationType<Variable>(v));
		grid.addProperty("Read / write type:", cbType);
	}

	public static void addProperties(AttributesGrid grid, StaticChannel channel) {
		// Source
		TextField lSource = new TextField();
		lSource.textProperty().bind(channel.getSource().nameProperty());
		lSource.setDisable(true);
		grid.addProperty("Source:", lSource);

		// Target
		TextField lTarget = new TextField();
		lTarget.textProperty().bind(channel.getTarget().nameProperty());
		lTarget.setDisable(true);
		grid.addProperty("Target:", lTarget);
	}

	public static void addProperties(AttributesGrid grid, Transition<?> transition) {
		TextArea guard = new TextArea();
		guard.setDisable(true);
		guard.setWrapText(true);
		guard.setPrefRowCount(6);
		
		ChangeListener<Expression> exprListener = new ChangeListener<Expression>() {
			@Override
			public void changed(ObservableValue<? extends Expression> observable, Expression oldValue,
					Expression newValue) {
				guard.textProperty().unbind();
				if (newValue != null) {
					guard.textProperty().bind(newValue.toStringExpression());
				}
			}
		};
		ChangeListener<Guard> guardListener = new ChangeListener<Guard>() {
			@Override
			public void changed(ObservableValue<? extends Guard> observable, Guard oldValue, Guard newValue) {
				guard.textProperty().unbind();
				if (oldValue != null) {
					oldValue.expressionProperty().addListener(exprListener);
				}
				if (newValue != null) {
					newValue.expressionProperty().addListener(exprListener);
					if (newValue.getExpression() != null) {
						guard.textProperty().bind(transition.getGuard().getExpression().toStringExpression());
					}
				}
			}
		};
		
		transition.guardProperty().addListener(guardListener);
		if (transition.getGuard() != null) {
			transition.getGuard().expressionProperty().addListener(exprListener);
			if (transition.getGuard().getExpression() != null) {
				guard.textProperty().bind(transition.getGuard().getExpression().toStringExpression());
			}
		}
		grid.addProperty("Guard:", guard);
		
		// Angle source
		NumberField sourceAngle = new NumberField();
		Bindings.bindBidirectional(sourceAngle.textProperty(), transition.sourceAngleProperty(), new NumberConverter());
		grid.addProperty("Source angle:", sourceAngle);

		// Angle target
		NumberField targetAngle = new NumberField();
		Bindings.bindBidirectional(targetAngle.textProperty(), transition.targetAngleProperty(), new NumberConverter());
		grid.addProperty("Target angle:", targetAngle);
	}

	public static void addProperties(AttributesGrid grid, Guard guard) {
		// expression string
		TextField lExpressionString = new TextField();
		if (guard.getExpression() != null) {
			lExpressionString.textProperty().bind(guard.getExpression().toStringExpression());
		} else {
			lExpressionString.setText("No expression specified.");
			lExpressionString.setFont(Font.font("System", FontPosture.ITALIC, 12));
		}
		grid.addProperty("Expression:", lExpressionString);
	}
	
	public static void addProperties(AttributesGrid grid, Scenario scenario) {
		ComboBox<Step> initialComboBox = new ComboBox<>(scenario.labelsProperty());
		initialComboBox.valueProperty().bindBidirectional(scenario.initialLabelProperty());
		grid.addProperty("Intial step:", initialComboBox);
		
		ComboBox<Step> finalComboBox = new ComboBox<>(scenario.labelsProperty());
		finalComboBox.valueProperty().bindBidirectional(scenario.finalLabelProperty());
		grid.addProperty("Final step:", finalComboBox);
	}
	
	public static void addProperties(AttributesGrid grid, Monitor monitor) {
		ComboBox<Activity> comboBox = new ComboBox<>(monitor.labelsProperty());
		comboBox.valueProperty().bindBidirectional(monitor.initialLabelProperty());
		grid.addProperty("Intial activity:", comboBox);
	}
	
	public static void addProperties(AttributesGrid grid, Behavior behavior) {
		ComboBox<State> comboBox = new ComboBox<>(behavior.labelsProperty());
		comboBox.valueProperty().bindBidirectional(behavior.initialLabelProperty());
		grid.addProperty("Intial state:", comboBox);
	}

	public static void addProperties(AttributesGrid grid, de.tum.imomesa.model.executables.Label label) {
		// x
		NumberField x = new NumberField();
		x.textProperty().bindBidirectional(label.xProperty(), new NumberConverter());
		grid.addProperty("X:", x);

		// y
		NumberField y = new NumberField();
		y.textProperty().bindBidirectional(label.yProperty(), new NumberConverter());
		grid.addProperty("Y:", y);

		// width
		NumberField width = new NumberField();
		width.textProperty().bindBidirectional(label.widthProperty(), new NumberConverter());
		grid.addProperty("Width:", width);

		// height
		NumberField height = new NumberField();
		height.textProperty().bindBidirectional(label.heightProperty(), new NumberConverter());
		grid.addProperty("Height:", height);
	}
	
	private static void addPoint(AttributesGrid grid, String prefix, Point point) {
		NumberField x = new NumberField();
		x.textProperty().bindBidirectional(point.xProperty(), new NumberConverter());
		grid.addProperty(prefix + "x:", x);

		NumberField y = new NumberField();
		y.textProperty().bindBidirectional(point.yProperty(), new NumberConverter());
		grid.addProperty(prefix + "y:", y);

		NumberField z = new NumberField();
		z.textProperty().bindBidirectional(point.zProperty(), new NumberConverter());
		grid.addProperty(prefix + "z:", z);
	}
	
}
