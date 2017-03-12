package de.tum.imomesa.workbench.controllers.main.editors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.tum.imomesa.model.expressions.AtomicExpression;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.TerniaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ExpressionController implements AbstractElementController<Expression> {

	@FXML
	private BorderPane borderPane;

	@Override
	public void setElement(Expression element) {
		try {
			// Generate graph
			FileWriter writer = new FileWriter("temp.dot");

			writer.write("digraph G {\n");
			writer.write("size = \"8,8\";\n");
			writer.write("ratio = \"fill\";\n");
			writer.write(
					"node [shape = rectangle, style = filled, color = \"black\", fillcolor = \"gray\", fontcolor = \"black\", fontname = \"Calibri\"]\n");
			writer.write("edge [color = \"blue\", fontcolor = \"blue\", fontname = \"Calibri\"]\n");
			render(element, writer);
			writer.write("}");

			writer.close();

			// Render graph
			Runtime.getRuntime().exec("dot -Tpng -otemp.png temp.dot").waitFor();

			// Load graph
			ImageView image = new ImageView(new Image(new File("temp.png").toURI().toString()));

			// Create scroll
			ScrollPane scrollPane = new ScrollPane(image);

			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			scrollPane.setCenterShape(true);

			// Show graph
			borderPane.setCenter(scrollPane);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void render(Expression expression, Writer writer) {
		Class<?> iterator = expression.getClass();

		while (Expression.class.isAssignableFrom(iterator)) {
			try {
				Method method = getClass().getMethod("handle", iterator, Writer.class);
				method.invoke(this, expression, writer);
				return;
			} catch (NoSuchMethodException e) {
				iterator = iterator.getSuperclass();
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public void handle(AtomicExpression expr, Writer writer) throws IOException {
		writer.write("\"" + StorageManager.getInstance().getKey(expr) + "\" [label = \""
				+ expr.getClass().getSimpleName() + "\"]\n;");
	}

	public void handle(UnaryExpression expr, Writer writer) throws IOException {
		writer.write("\"" + StorageManager.getInstance().getKey(expr) + "\" [label = \""
				+ expr.getClass().getSimpleName() + "\"]\n;");

		argument(expr, expr.argumentProperty(), "", writer);
	}

	public void handle(TerniaryExpression expr, Writer writer) throws IOException {
		writer.write("\"" + StorageManager.getInstance().getKey(expr) + "\" [label = \""
				+ expr.getClass().getSimpleName() + "\"]\n;");

		argument(expr, expr.argumentOneProperty(), "first", writer);
		argument(expr, expr.argumentTwoProperty(), "second", writer);
		argument(expr, expr.argumentThreeProperty(), "third", writer);
	}

	public void handle(NaryExpression expr, Writer writer) throws IOException {
		writer.write("\"" + StorageManager.getInstance().getKey(expr) + "\" [label = \""
				+ expr.getClass().getSimpleName() + "\"]\n;");

		int index = 1;
		for (Expression argument : expr.getArguments()) {
			argument(expr, argument, "" + (index++), writer);
		}
	}

	private void argument(Expression parent, ObjectProperty<Expression> child, String label, Writer writer)
			throws IOException {
		if (child.get() != null) {
			argument(parent, child.get(), label, writer);
		}
	}

	private void argument(Expression parent, Expression child, String label, Writer writer) throws IOException {
		if (child != null) {
			render(child, writer);
			writer.write("\"" + StorageManager.getInstance().getKey(parent) + "\" -> \""
					+ StorageManager.getInstance().getKey(child) + "\" [label = \"" + label + "\"];\n");
		}
	}

}
