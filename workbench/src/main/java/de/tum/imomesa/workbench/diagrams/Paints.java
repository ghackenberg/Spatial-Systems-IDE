package de.tum.imomesa.workbench.diagrams;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Paints {

	public static final Color RED_ONE = Color.ORANGE;
	public static final Color RED_TWO = multiply(RED_ONE, 0.75);
	public static final Color RED_THREE = Color.ORANGERED;
	public static final Color RED_FOUR = multiply(RED_THREE, 0.75);

	public static final Color GREEN_ONE = Color.LIGHTGREEN;
	public static final Color GREEN_TWO = multiply(GREEN_ONE, 0.75);
	public static final Color GREEN_THREE = Color.LIMEGREEN;
	public static final Color GREEN_FOUR = multiply(GREEN_THREE, 0.75);

	public static final Color BLUE_ONE = Color.LIGHTBLUE;
	public static final Color BLUE_TWO = multiply(BLUE_ONE, 0.75);
	public static final Color BLUE_THREE = Color.CORNFLOWERBLUE;
	public static final Color BLUE_FOUR = multiply(BLUE_THREE, 0.75);

	public static final Color GRAY_ONE = Color.WHITE;
	public static final Color GRAY_TWO = multiply(GRAY_ONE, 0.75);
	public static final Color GRAY_THREE = Color.DARKGRAY;
	public static final Color GRAY_FOUR = multiply(GRAY_THREE, 0.75);

	public static final LinearGradient GRADIENT_RED_ONE = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, RED_ONE), new Stop(1, RED_TWO));
	public static final LinearGradient GRADIENT_RED_TWO = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, RED_THREE), new Stop(1, RED_FOUR));

	public static final LinearGradient GRADIENT_GREEN_ONE = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, GREEN_ONE), new Stop(1, GREEN_TWO));
	public static final LinearGradient GRADIENT_GREEN_TWO = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, GREEN_THREE), new Stop(1, GREEN_FOUR));

	public static final LinearGradient GRADIENT_BLUE_ONE = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, BLUE_ONE), new Stop(1, BLUE_TWO));
	public static final LinearGradient GRADIENT_BLUE_TWO = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, BLUE_THREE), new Stop(1, BLUE_FOUR));

	public static final LinearGradient GRADIENT_GRAY_ONE = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, GRAY_ONE), new Stop(1, GRAY_TWO));
	public static final LinearGradient GRADIENT_GRAY_TWO = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
			new Stop(0, GRAY_THREE), new Stop(1, GRAY_FOUR));

	private static Color multiply(Color color, double factor) {
		return new Color(color.getRed() * factor, color.getGreen() * factor, color.getBlue() * factor,
				color.getOpacity());
	}

}
