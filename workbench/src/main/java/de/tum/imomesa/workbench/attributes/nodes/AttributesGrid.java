	package de.tum.imomesa.workbench.attributes.nodes;

import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AttributesGrid extends GridPane {
	
	private static final double GRID_PADDING = 0;
	
	private static final double GRID_GAP = 1;
	
	private static final double GRID_HGAP = GRID_GAP;
	private static final double GRID_VGAP = GRID_GAP;
	
	private static final String CELL_TYPE_STYLE = "-fx-background-color: linear-gradient(rgba(0,0,0,0.0), rgba(0,0,0,0.1));";
	private static final String CELL_EMPTY_STYLE = "-fx-background-color: rgba(255,255,255,1.0);";
	private static final String CELL_EXCEPTION_STYLE = "-fx-background-color: rgba(255,255,255,1.0);";
	
	public static final String STATIC_CELL_LABEL_STYLE = "-fx-background-color: rgba(255,255,255,1.0);";
	public static final String STATIC_CELL_VALUE_STYLE = "-fx-background-color: rgba(255,255,255,1.0);";
	
	public static final String DYNAMIC_CELL_LABEL_STYLE = "-fx-background-color: rgba(255,255,0,1.0);";
	public static final String DYNAMIC_CELL_VALUE_STYLE = "-fx-background-color: rgba(255,255,0,1.0);";
	
	private static final double CELL_PADDING = 8;
	
	private static final double CELL_TYPE_PADDING = CELL_PADDING;
	private static final double CELL_EMPTY_PADDING = CELL_PADDING;
	private static final double CELL_EXCEPTION_PADDING = CELL_PADDING;
	private static final double CELL_LABEL_PADDING = CELL_PADDING;
	private static final double CELL_VALUE_PADDING = CELL_PADDING;
	
	private static final String CELL_TYPE_LABEL_STYLE = "-fx-font-weight: bold;";
	private static final String CELL_TYPE_IMAGE_STYLE = "";
	private static final String CELL_EMPTY_TEXT_STYLE = "-fx-font-style: italic; -fx-fill: gray;";
	private static final String CELL_EXCEPTION_TEXT_STYLE = "-fx-font-style: italic; -fx-fill: red;";
	private static final String CELL_LABEL_LABEL_STYLE = "";
	private static final String CELL_VALUE_NODE_STYLE = "";
	
	/**
	 * Row counter.
	 */
	private int rowIndex = 0;

	/**
	 * Constructor that sets the initial settings of this GridPane
	 */
	public AttributesGrid() {
		setPadding(new Insets(GRID_PADDING));
		
		setHgap(GRID_HGAP);
		setVgap(GRID_VGAP);
		
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		column1.setPercentWidth(100.0/3*1);
		
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHgrow(Priority.ALWAYS);
		column2.setPercentWidth(100.0/3*2);
		
		getColumnConstraints().addAll(column1, column2);
	}

	public void addType(Class<?> type) {
		String[] parts = type.getSimpleName().split("(?=\\p{Upper})");
		String name = parts[0];
		
		for (int i = 1; i < parts.length; i++) {
			name += " " + parts[i].toLowerCase();
		}
		
		Label typeLabel = new Label(name);
		typeLabel.setStyle(CELL_TYPE_LABEL_STYLE);
		
		ImageView typeImage = ImageHelper.getIcon(type);
		typeImage.setStyle(CELL_TYPE_IMAGE_STYLE);
		
		HBox box = new HBox();
		box.setStyle(CELL_TYPE_STYLE);
		box.setPadding(new Insets(CELL_TYPE_PADDING));
		box.setSpacing(CELL_TYPE_PADDING);
		box.setFillHeight(true);
		box.setAlignment(Pos.CENTER_LEFT);
		box.getChildren().add(typeImage);
		box.getChildren().add(typeLabel);

		add(box, 0, rowIndex, 2, 1);

		rowIndex++;
	}
	
	public void addSection(String name) {
		Label nameLabel = new Label(name);
		nameLabel.setStyle(CELL_TYPE_LABEL_STYLE);
		
		HBox box = new HBox();
		box.setStyle(CELL_TYPE_STYLE);
		box.setPadding(new Insets(CELL_TYPE_PADDING));
		box.setSpacing(CELL_TYPE_PADDING);
		box.setFillHeight(true);
		box.setAlignment(Pos.CENTER_LEFT);
		box.getChildren().add(nameLabel);

		add(box, 0, rowIndex, 2, 1);

		rowIndex++;
	}

	public void addEmpty() {
		Text messageText = new Text("Empty");
		messageText.setStyle(CELL_EMPTY_TEXT_STYLE);
		
		VBox box = new VBox();
		box.setStyle(CELL_EMPTY_STYLE);
		box.setPadding(new Insets(CELL_EMPTY_PADDING));
		box.setFillWidth(true);
		// box.setAlignment(Pos.CENTER);
		box.getChildren().add(messageText);

		add(box, 0, rowIndex, 2, 1);

		rowIndex++;
	}

	public void addException(Exception e) {
		Text messageText = new Text(e.getMessage());
		messageText.setStyle(CELL_EXCEPTION_TEXT_STYLE);
		
		VBox box = new VBox();
		box.setStyle(CELL_EXCEPTION_STYLE);
		box.setPadding(new Insets(CELL_EXCEPTION_PADDING));
		box.setFillWidth(true);
		// box.setAlignment(Pos.CENTER);
		box.getChildren().add(messageText);

		add(box, 0, rowIndex, 2, 1);

		rowIndex++;
	}
	
	public void addProperty(String labelText, Node valueNode) {
		this.addProperty(labelText, valueNode, STATIC_CELL_LABEL_STYLE, STATIC_CELL_VALUE_STYLE);
	}

	/**
	 * This method allows to add one row to the GridPane This GridPane is used
	 * to display all the properties One row consists of a label and a node
	 * 
	 * @param l
	 *            the description of the node to be added
	 * @param valueNode
	 *            the node to be added
	 */
	public void addProperty(String labelText, Node valueNode, String labelStyle, String valueStyle) {
		Label labelLabel = new Label(labelText);
		labelLabel.setStyle(CELL_LABEL_LABEL_STYLE);
		
		valueNode.setStyle(CELL_VALUE_NODE_STYLE);
		
		VBox boxLabel = new VBox();
		boxLabel.setStyle(labelStyle);
		boxLabel.setPadding(new Insets(CELL_LABEL_PADDING));
		boxLabel.setFillWidth(true);
		boxLabel.setAlignment(Pos.CENTER_LEFT);
		boxLabel.getChildren().add(labelLabel);
		
		VBox boxValue = new VBox();
		boxValue.setStyle(valueStyle);
		boxValue.setPadding(new Insets(CELL_VALUE_PADDING));
		boxValue.setFillWidth(true);
		boxValue.setAlignment(Pos.CENTER_LEFT);
		boxValue.getChildren().add(valueNode);
		
		add(boxLabel, 0, rowIndex);
		add(boxValue, 1, rowIndex);

		rowIndex++;
	}
	
	public void addProperty(String labelText, String valueText) {
		this.addProperty(labelText, valueText, STATIC_CELL_LABEL_STYLE, STATIC_CELL_VALUE_STYLE);
	}
	
	public void addProperty(String labelText, String valueText, String labelStyle, String valueStyle) {
		TextField valueField = new TextField(valueText);
		valueField.setDisable(true);
		
		addProperty(labelText, valueField, labelStyle, valueStyle);
	}
	
}