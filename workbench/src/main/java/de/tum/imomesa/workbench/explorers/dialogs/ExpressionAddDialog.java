package de.tum.imomesa.workbench.explorers.dialogs;

import java.util.List;

import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.IfClauseExpression;
import de.tum.imomesa.model.expressions.NullExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.booleans.AndExpression;
import de.tum.imomesa.model.expressions.booleans.ConstantExpressionBoolean;
import de.tum.imomesa.model.expressions.booleans.EqualExpression;
import de.tum.imomesa.model.expressions.booleans.GreaterExpression;
import de.tum.imomesa.model.expressions.booleans.NotExpression;
import de.tum.imomesa.model.expressions.booleans.OrExpression;
import de.tum.imomesa.model.expressions.booleans.SmallerExpression;
import de.tum.imomesa.model.expressions.numbers.AverageExpression;
import de.tum.imomesa.model.expressions.numbers.CardinalityExpression;
import de.tum.imomesa.model.expressions.numbers.ConstantExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.DifferenceExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.DivisionExpression;
import de.tum.imomesa.model.expressions.numbers.DurationExpression;
import de.tum.imomesa.model.expressions.numbers.MaximumExpression;
import de.tum.imomesa.model.expressions.numbers.MinimumExpression;
import de.tum.imomesa.model.expressions.numbers.ProductExpression;
import de.tum.imomesa.model.expressions.numbers.RandomExpressionNumber;
import de.tum.imomesa.model.expressions.numbers.StepExpression;
import de.tum.imomesa.model.expressions.numbers.SumExpression;
import de.tum.imomesa.model.expressions.sets.DifferenceExpressionSet;
import de.tum.imomesa.model.expressions.sets.IntersectionExpression;
import de.tum.imomesa.model.expressions.sets.SetConstructorExpression;
import de.tum.imomesa.model.expressions.sets.UnionExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ExpressionAddDialog extends Dialog<Expression> {
    
    private static ObservableList<String> options = FXCollections.observableArrayList(
        "Atomic expression",
        "Unary expression",
        "Nary expression"
    );

    private ObservableList<Expression> optionsAtomic = FXCollections.observableArrayList(
		new ConstantExpressionBoolean(),
		new ConstantExpressionNumber(),
		new RandomExpressionNumber(),
		new DurationExpression(),
		new StepExpression(),
		new NullExpression(),
		new ObservationExpression()
	);
    
    private ObservableList<Expression> optionsUnary = FXCollections.observableArrayList(
    	new CardinalityExpression(),
    	new NotExpression()
    );

    private ObservableList<Expression> optionsNary = FXCollections.observableArrayList(
    	new AndExpression(),
    	new AverageExpression(),
    	new DifferenceExpressionSet(),
    	new DifferenceExpressionNumber(),
    	new DivisionExpression(),
    	new EqualExpression(),
    	new GreaterExpression(),
    	new IntersectionExpression(),
    	new MaximumExpression(),
    	new MinimumExpression(),
    	new OrExpression(),
    	new ProductExpression(),
    	new SmallerExpression(),
    	new SumExpression(),
    	new UnionExpression(),
    	new SetConstructorExpression(),
    	new IfClauseExpression()
    );
    
    private ToggleGroup group = new ToggleGroup();
    
    private ObservableList<Expression> selectedList;
    
	public ExpressionAddDialog(List<Class<?>> writeTypes) {
		// set title and header
		setTitle("Add expression");
		setHeaderText(null);
		
		// button types

		// layout a custom GridPane containing the input fields and labels
	    GridPane content = new GridPane();
	    content.setHgap(10);
	    content.setVgap(10);
	    
	    // ComboBox
	    ComboBox<String> comboBox = new ComboBox<>(options);
	    // action on change of combo box
	    comboBox.setOnMousePressed(new EventHandler<MouseEvent>(){
	        @Override
	        public void handle(MouseEvent event) {
	            comboBox.requestFocus();
	        }
	    });
	    comboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// atomic expression
				if(comboBox.getValue().equals(options.get(0))) {
					addRadioButtons(content, selectAppropriateItems(optionsAtomic, writeTypes));
				}
				// unary composite expression
				if(comboBox.getValue().equals(options.get(1))) {
					addRadioButtons(content, selectAppropriateItems(optionsUnary, writeTypes));
				}
				// nary composite expression
				if(comboBox.getValue().equals(options.get(2))) {
					addRadioButtons(content, selectAppropriateItems(optionsNary, writeTypes));
				}
			}
		});
	    // set options
	    comboBox.setValue(options.get(0));
	    
	    // Label
	    content.add(new Label("Choose the type of expression"), 0, 0);
	    // Add combobox
	    content.add(comboBox, 1, 0);
	    // separator over two lines
	    content.add(new Separator(), 0, 1, 2, 1);
	    
	    // Add radio buttons
	    addRadioButtons(content, selectAppropriateItems(optionsAtomic, writeTypes));

	    // add content
	    getDialogPane().setContent(content);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	    
	    setResizable(true);

	    // result converter
	    setResultConverter(dialogButton -> {
	        if (dialogButton == ButtonType.OK) {
	    		// get selected item
	    		int selectedRadioButton = group.getToggles().indexOf((RadioButton)(group.getSelectedToggle()));
	    		// if no item was selected, return NULL-Pointer
	    		if(selectedRadioButton < 0 || selectedRadioButton >= selectedList.size() ) {
	    			return null;
	    		}
	    		// get list object
	    		return selectedList.get(selectedRadioButton);
	        }
	        return null;
	    });

	}
	
	private void addRadioButtons(GridPane content, ObservableList<Expression> items) {
		// save selected list
		selectedList = items;
		
		ObservableList<Toggle> l = group.getToggles();
		for(Toggle rb : l) {
			// remove
			content.getChildren().remove((RadioButton)rb);
		}
		
		// create toggle group
		group = new ToggleGroup();
		
		// indicator for first run of loop to set selected item
		boolean firstRun = true;
		int index = 2;
		
		for(Expression e : items) {
			// create radiobutton and add to group
			RadioButton rb = new RadioButton(e.toString());
			rb.setToggleGroup(group);
			if(firstRun == true) {
				// if first run, set item as selected
				rb.setSelected(true);
				firstRun = false;
			}
			// add to content
			content.add(rb, 0, index, 2, 1);
			index++;
		}
	}
	
	private ObservableList<Expression> selectAppropriateItems(ObservableList<Expression> fullList, List<Class<?>> typeObjects) {
		// create list
		ObservableList<Expression> list = FXCollections.observableArrayList();
		
		if(typeObjects.size() == 0) {
			return list;
		}
		
		// go through input list and choose all corresponding elements
		for(Expression e : fullList) {
			
			// if expression is ObservationExpression, it always can be added
			// but the type has to be defined
			if(e instanceof ObservationExpression || e instanceof NullExpression || e instanceof IfClauseExpression){
				
				// special case for those three as the type has to be set (to support Markers properly)
				// create one object for each write type
				for(Class<?> type : typeObjects) {
					// Observation Expression
					if(e instanceof ObservationExpression) {
						ObservationExpression expression = new ObservationExpression();
						expression.setType(type);
						list.add(expression);							
					}
					// NullExpression
					else if(e instanceof NullExpression) {
						NullExpression expression = new NullExpression();
						expression.setType(type);
						list.add(expression);							
					}
					// IfClauseExpression
					else if(e instanceof IfClauseExpression) {
						IfClauseExpression expression = new IfClauseExpression();
						expression.setType(type);
						expression.setArgumentTwoType(type);
						expression.setArgumentThreeType(type);
						list.add(expression);			
					}
					else {
						throw new IllegalStateException();
					}
				}
			}
			else {
				for(Class<?> typeObject : typeObjects) {
					if(typeObject.isAssignableFrom(e.getType())) {
						list.add(e);
					}
				}
			}
		}

		// return list
		return list;
	}
	
}
