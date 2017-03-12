package de.tum.imomesa.workbench.explorers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Guard;
import de.tum.imomesa.model.executables.Label;
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
import de.tum.imomesa.model.expressions.TerniaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.explorers.dialogs.ComponentAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.DefinitionPortAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.ExpressionAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.LifeMaterialPortAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.PropertyAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.TransformAddDialog;
import de.tum.imomesa.workbench.explorers.dialogs.VolumeAddDialog;
import de.tum.imomesa.workbench.explorers.handlers.ListAddElementHandler;
import de.tum.imomesa.workbench.explorers.handlers.ObjectAddElementHandler;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;

public class ContextMenuBuilder {
	
	public static ContextMenu getContextMenu(TreeItem<Element> treeItem) {

		ContextMenu menu = new ContextMenu();
		Element element = treeItem.getValue();
		
		if (element != null) {
			if(element instanceof OverviewElement) {
				OverviewElement<?> overview = (OverviewElement<?>) element;
				Element parent = overview.getParent();
				String name = overview.getName();
				ListProperty<?> list = overview.getProperty();
				
				Class<?> iterator = parent.getClass();
				
				while (iterator != null) {
					try {
						Method method = ContextMenuBuilder.class.getMethod("getContextMenu" + name, iterator, ListProperty.class);
		    			
		    			String[] parts = iterator.getSimpleName().split("(?=\\p{Upper})");
		    			String folder = "";
		    			for (String part : parts) {
		    				folder += " " + (folder.length() == 0 ? part : part.toLowerCase());
		    			}
						
						MenuItem item = new MenuItem();
						item.setText(folder + " - " + name);
						item.setGraphic(ImageHelper.getFolderIcon(iterator));
						item.setDisable(true);
						
						MenuItem[] items = (MenuItem[]) method.invoke(null, parent, list);
						menu.getItems().add(0, new SeparatorMenuItem());
						menu.getItems().addAll(0, Arrays.asList(items));
						menu.getItems().add(0, item);
					}
					catch (Exception e) {
						// ignore
					}
					iterator = iterator.getSuperclass();
				}
				
				// Remove last separator
				if (menu.getItems().size() > 0) {
					menu.getItems().remove(menu.getItems().size() - 1);
				}
			}
			else {
				Class<?> iterator = element.getClass();
				
		    	while(Element.class.isAssignableFrom(iterator) )
		    	{
		    		try {
		    			Method method = ContextMenuBuilder.class.getMethod("getContextMenu", iterator);
		    			
		    			String[] parts = iterator.getSimpleName().split("(?=\\p{Upper})");
		    			String name = "";
		    			for (String part : parts) {
		    				name += " " + (name.length() == 0 ? part : part.toLowerCase());
		    			}
						
						MenuItem item = new MenuItem();
						item.setText(name);
						item.setGraphic(ImageHelper.getFolderIcon(iterator));
						item.setDisable(true);
						
		    	    	MenuItem[] items = (MenuItem[]) method.invoke(null, element);
		    	    	menu.getItems().add(0, new SeparatorMenuItem());
		    	    	menu.getItems().addAll(0, Arrays.asList(items));
		    	    	menu.getItems().add(0, item);
					}
		    		catch (Exception exp) {
						// ignore
					}
					iterator = iterator.getSuperclass();
		    	}
				
				// Remove last separator
				if (menu.getItems().size() > 0) {
					menu.getItems().remove(menu.getItems().size() - 1);
				}
			}
		}

		return menu;
	}
	
	// Element
	
	public static MenuItem[] getContextMenu(Element element) {
		String[] parts = element.getClass().getSimpleName().split("(?=\\p{Upper})");
		String name = "";
		for (String part : parts) {
			name += " " + part.toLowerCase();
		}
		
    	MenuItem deleteItem = new MenuItem("Delete" + name);
        deleteItem.setOnAction(new RemoveElementHandler(element));
		deleteItem.setGraphic(ImageHelper.getIconDeleted(element.getClass()));
		
		return new MenuItem[] { deleteItem };
	}
	
	public static MenuItem[] getContextMenu(Project p) {
		MenuItem addComponent = new MenuItem("Add component");
		addComponent.setOnAction(new ObjectAddElementHandler<>(p, p.componentProperty(), DefinitionComponent.class));
		addComponent.setGraphic(ImageHelper.getIcon(Component.class));
		addComponent.disableProperty().bind(p.componentProperty().isNotNull());
		
		return new MenuItem[] { addComponent };
	}
	
	public static MenuItem[] getContextMenu(Observation o) {
		MenuItem addExpression = new MenuItem("Add default expression");
		addExpression.setOnAction(new ObjectAddElementHandler<>(o, o.defaultProperty(), new ExpressionAddDialog(o.getWriteType())));
		addExpression.setGraphic(ImageHelper.getIcon(Expression.class));
		addExpression.disableProperty().bind(o.defaultProperty().isNotNull());
		
		return new MenuItem[] { addExpression };
	}
	
	public static MenuItem[] getContextMenu(Property p) {
		MenuItem addExpression = new MenuItem("Add expression");
		addExpression.setOnAction(new ObjectAddElementHandler<>(p, p.expressionProperty(), new ExpressionAddDialog(p.getWriteType())));
		addExpression.setGraphic(ImageHelper.getIcon(Expression.class));
		addExpression.disableProperty().bind(p.expressionProperty().isNotNull());
		
		return new MenuItem[] { addExpression };
	}
	
	public static MenuItem[] getContextMenu(Transition<?> t) {
		MenuItem addGuard = new MenuItem("Add guard");
		addGuard.setOnAction(new ObjectAddElementHandler<>(t, t.guardProperty(), Guard.class));
		addGuard.setGraphic(ImageHelper.getIcon(Guard.class));
		addGuard.disableProperty().bind(t.guardProperty().isNotNull());
		
		return new MenuItem[] { addGuard };
	}
	
	public static MenuItem[] getContextMenu(Guard g) {
		List<Class<?>> writeTypes = new ArrayList<>();
		writeTypes.add(Boolean.class);
		
		MenuItem addExpression = new MenuItem("Add expression");
		addExpression.setOnAction(new ObjectAddElementHandler<>(g, g.expressionProperty(), new ExpressionAddDialog(writeTypes)));
		addExpression.setGraphic(ImageHelper.getIcon(Expression.class));
		addExpression.disableProperty().bind(g.expressionProperty().isNotNull());
		
		return new MenuItem[] { addExpression };
	}
	
	public static MenuItem[] getContextMenu(Action a) {
		// TODO Use binding here!
		List<Class<?>> writeTypes = null;
		
		if (a.getObservation() != null) {
			writeTypes = a.getObservation().getWriteType();
		}
		else {
			writeTypes = new ArrayList<>();
			writeTypes.add(Object.class);
		}
		
		MenuItem addExpression = new MenuItem("Add expression");
		addExpression.setOnAction(new ObjectAddElementHandler<>(a, a.expressionProperty(), new ExpressionAddDialog(writeTypes)));
		addExpression.setGraphic(ImageHelper.getIcon(Expression.class));
		addExpression.disableProperty().bind(Bindings.or(a.observationProperty().isNull(), a.expressionProperty().isNotNull()));
		
		return new MenuItem[] { addExpression };
	}
	
	public static MenuItem[] getContextMenu(MaterialPort p) {
		MenuItem addVolume = new MenuItem("Add volume");
		addVolume.setOnAction(new ObjectAddElementHandler<>(p, p.volumeProperty(), new VolumeAddDialog()));
		addVolume.setGraphic(ImageHelper.getIcon(Volume.class));
		addVolume.disableProperty().bind(p.volumeProperty().isNotNull());
		
		return new MenuItem[] { addVolume };
	}
	
	public static MenuItem[] getContextMenu(KinematicEnergyPort p) {
		MenuItem addTransform = new MenuItem("Add transform");
		addTransform.setOnAction(new ObjectAddElementHandler<>(p, p.transformProperty(), new TransformAddDialog()));
		addTransform.setGraphic(ImageHelper.getIcon(Transform.class));
		addTransform.disableProperty().bind(Bindings.or(Bindings.equal(p.directionProperty(), Direction.INPUT.ordinal()), p.transformProperty().isNotNull()));
		
		return new MenuItem[] { addTransform };
	}
	
	public static MenuItem[] getContextMenu(Part p) {
		MenuItem addVolume = new MenuItem("Add volume");
		addVolume.setOnAction(new ObjectAddElementHandler<>(p, p.volumeProperty(), new VolumeAddDialog()));
		addVolume.setGraphic(ImageHelper.getIcon(Volume.class));
		addVolume.disableProperty().bind(p.volumeProperty().isNotNull());
		
		return new MenuItem[] { addVolume };
	}
	
	public static MenuItem[] getContextMenu(UnaryExpression e) {
		// TODO Use binding here!
		List<Class<?>> writeType = new ArrayList<>();
		writeType.add(e.getArgumentType());
		
		MenuItem addExpression = new MenuItem("Add argument");
		addExpression.setOnAction(new ObjectAddElementHandler<>(e, e.argumentProperty(), new ExpressionAddDialog(writeType)));
		addExpression.setGraphic(ImageHelper.getIcon(Expression.class));
		addExpression.disableProperty().bind(e.argumentProperty().isNotNull());
		
		return new MenuItem[] { addExpression };
	}
	
	public static MenuItem[] getContextMenu(TerniaryExpression e) {
		// TODO Use binding here!
		List<Class<?>> writeTypeOne = new ArrayList<>();
		writeTypeOne.add(e.getArgumentOneType());
		
		MenuItem addArgumentOne = new MenuItem("Add argument one");
		addArgumentOne.setOnAction(new ObjectAddElementHandler<>(e, e.argumentOneProperty(), new ExpressionAddDialog(writeTypeOne)));
		addArgumentOne.setGraphic(ImageHelper.getIcon(Expression.class));
		addArgumentOne.disableProperty().bind(e.argumentOneProperty().isNotNull());

		// TODO Use binding here!
		List<Class<?>> writeTypeTwo = new ArrayList<>();
		writeTypeTwo.add(e.getArgumentTwoType());
		
		MenuItem addArgumentTwo = new MenuItem("Add argument two");
		addArgumentOne.setOnAction(new ObjectAddElementHandler<>(e, e.argumentTwoProperty(), new ExpressionAddDialog(writeTypeTwo)));
		addArgumentOne.setGraphic(ImageHelper.getIcon(Expression.class));
		addArgumentOne.disableProperty().bind(e.argumentTwoProperty().isNotNull());

		// TODO Use binding here!
		List<Class<?>> writeTypeThree = new ArrayList<>();
		writeTypeTwo.add(e.getArgumentThreeType());
		
		MenuItem addArgumentThree = new MenuItem("Add argument three");
		addArgumentOne.setOnAction(new ObjectAddElementHandler<>(e, e.argumentThreeProperty(), new ExpressionAddDialog(writeTypeThree)));
		addArgumentOne.setGraphic(ImageHelper.getIcon(Expression.class));
		addArgumentOne.disableProperty().bind(e.argumentThreeProperty().isNotNull());
		
		return new MenuItem[] { addArgumentOne, addArgumentTwo, addArgumentThree };
	}
	
	// Overview
	
	public static MenuItem[] getContextMenuProjects(Workspace parent, ListProperty<Project> property) {
		MenuItem add = new MenuItem("Add project");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Project.class));
		add.setGraphic(ImageHelper.getIcon(Project.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuTemplates(Project parent, ListProperty<DefinitionComponent> property) {
		MenuItem add = new MenuItem("Add template");
		add.setOnAction(new ListAddElementHandler<>(parent, property, DefinitionComponent.class));
		add.setGraphic(ImageHelper.getIcon(DefinitionComponent.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuRequirements(DefinitionComponent parent, ListProperty<Requirement> property) {
		MenuItem add = new MenuItem("Add requirement");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Requirement.class));
		add.setGraphic(ImageHelper.getIcon(Requirement.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuPorts(DefinitionComponent parent, ListProperty<DefinitionPort> property) {
		MenuItem add = new MenuItem("Add port");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new DefinitionPortAddDialog()));
		add.setGraphic(ImageHelper.getIcon(DefinitionPort.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuProperties(DefinitionComponent parent, ListProperty<Property> property) {
		MenuItem add = new MenuItem("Add property");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new PropertyAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Property.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuScenarios(DefinitionComponent parent, ListProperty<Scenario> property) {
		MenuItem add = new MenuItem("Add scenario");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Scenario.class));
		add.setGraphic(ImageHelper.getIcon(Scenario.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuMonitors(DefinitionComponent parent, ListProperty<Monitor> property) {
		MenuItem add = new MenuItem("Add monitor");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Monitor.class));
		add.setGraphic(ImageHelper.getIcon(Monitor.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuComponents(DefinitionComponent parent, ListProperty<Component<?>> property) {
		MenuItem add = new MenuItem("Add component");
		add.setOnAction(new ListAddElementHandler<Component<?>>(parent, property, new ComponentAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Component.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuBehaviors(DefinitionComponent parent, ListProperty<Behavior> property) {
		MenuItem add = new MenuItem("Add behavior");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Behavior.class));
		add.setGraphic(ImageHelper.getIcon(Behavior.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuParts(DefinitionComponent parent, ListProperty<Part> property) {
		MenuItem add = new MenuItem("Add part");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Part.class));
		add.setGraphic(ImageHelper.getIcon(Part.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuTransforms(Component<?> parent, ListProperty<Transform> property) {
		MenuItem add = new MenuItem("Add transform");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new TransformAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Transform.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuRequirements(Requirement parent, ListProperty<Requirement> property) {
		MenuItem add = new MenuItem("Add requirement");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Requirement.class));
		add.setGraphic(ImageHelper.getIcon(Requirement.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuPorts(InteractionMaterialPort parent, ListProperty<DefinitionPort> property) {
		MenuItem add = new MenuItem("Add port");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new DefinitionPortAddDialog()));
		add.setGraphic(ImageHelper.getIcon(DefinitionPort.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuPorts(Scenario parent, ListProperty<LifeMaterialPort> property) {
		MenuItem add = new MenuItem("Add port");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new LifeMaterialPortAddDialog()));
		add.setGraphic(ImageHelper.getIcon(LifeMaterialPort.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuSteps(Scenario parent, ListProperty<Step> property) {
		MenuItem add = new MenuItem("Add step");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Step.class));
		add.setGraphic(ImageHelper.getIcon(Step.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuActivities(Monitor parent, ListProperty<Activity> property) {
		MenuItem add = new MenuItem("Add activity");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Activity.class));
		add.setGraphic(ImageHelper.getIcon(Activity.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuStates(Behavior parent, ListProperty<State> property) {
		MenuItem add = new MenuItem("Add state");
		add.setOnAction(new ListAddElementHandler<>(parent, property, State.class));
		add.setGraphic(ImageHelper.getIcon(State.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuActions(Transition<?> parent, ListProperty<Action> property) {
		MenuItem add = new MenuItem("Add action");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Action.class));
		add.setGraphic(ImageHelper.getIcon(Action.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuParts(State parent, ListProperty<Part> property) {
		MenuItem add = new MenuItem("Add part");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Part.class));
		add.setGraphic(ImageHelper.getIcon(Part.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuVariables(Executable<?, ?> parent, ListProperty<Variable> property) {
		MenuItem add = new MenuItem("Add variable");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Variable.class));
		add.setGraphic(ImageHelper.getIcon(Variable.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuActions(Label parent, ListProperty<Action> property) {
		MenuItem add = new MenuItem("Add action");
		add.setOnAction(new ListAddElementHandler<>(parent, property, Action.class));
		add.setGraphic(ImageHelper.getIcon(Action.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuProperties(Label parent, ListProperty<Property> property) {
		MenuItem add = new MenuItem("Add property");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new PropertyAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Property.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuTransforms(Part parent, ListProperty<Transform> property) {
		MenuItem add = new MenuItem("Add transform");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new TransformAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Transform.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuTransforms(Volume parent, ListProperty<Transform> property) {
		MenuItem add = new MenuItem("Add transform");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new TransformAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Transform.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuVolumes(CompositeVolume parent, ListProperty<Volume> property) {
		MenuItem add = new MenuItem("Add volume");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new VolumeAddDialog()));
		add.setGraphic(ImageHelper.getIcon(Volume.class));
		
		return new MenuItem[] { add };
	}
	
	public static MenuItem[] getContextMenuArguments(CompositeExpression parent, ListProperty<Expression> property) {
		List<Class<?>> writeTypes = new ArrayList<>();
		writeTypes.add(parent.getArgumentType());
		
		MenuItem add = new MenuItem("Add expression");
		add.setOnAction(new ListAddElementHandler<>(parent, property, new ExpressionAddDialog(writeTypes)));
		add.setGraphic(ImageHelper.getIcon(Expression.class));
		
		return new MenuItem[] { add };
	}
	
}