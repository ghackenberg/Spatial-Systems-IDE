package de.tum.imomesa.analyzer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.transforms.TranslationTransform;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.model.volumes.Volume;

public class Povray {

	private static final NumberFormat FORMAT = DecimalFormat.getInstance(Locale.ENGLISH);

	private static final double WIRE_LINE_RADIUS = 0.15;

	private static final int WIRE_CYLINDER_LINES = 3;
	private static final int WIRE_CYLINDER_CIRCLES = 2;

	private static final double VECTOR_LINE_RADIUS = WIRE_LINE_RADIUS * 2;

	private static final double VECTOR_ARROW_RADIUS = VECTOR_LINE_RADIUS * 3;
	private static final double VECTOR_ARROW_LENGTH = VECTOR_ARROW_RADIUS * 2;

	private static final int VECTOR_RED = 255;
	private static final int VECTOR_GREEN = 165;
	private static final int VECTOR_BLUE = 0;

	// RENDER

	public static String render(DefinitionComponent component, long timestamp, double cx, double cy, double cz) {
		String result = "";

		long seconds = timestamp / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		seconds %= 60;
		minutes %= 60;

		String seconds_text = (seconds < 10 ? "0" : "") + seconds;
		String minutes_text = (minutes < 10 ? "0" : "") + minutes;
		String hours_text = (hours < 10 ? "0" : "") + hours;

		result += "#include \"transforms.inc\"\n";
		// Background
		result += "background {\n";
		result += "\tcolor " + color(255, 255, 255) + "\n";
		result += "}\n";
		// Camera
		result += "camera {\n";
		result += "\tlocation " + vector(30 + cx, 0 + cy, 37.5 + cz) + "\n";
		result += "\tlook_at " + vector(30, 0, 37.5) + "\n";
		result += "}\n";
		// Light
		result += "light_source {\n";
		result += "\t" + vector(30, 100, 37.5) + ",\n";
		result += "\t" + color(255, 255, 255) + "\n";
		// result += "\tarea_light " + vector(100, 0, 0) + "," + vector(0, 0,
		// 100) + ",100,100\n";
		// result += "\tadaptive 3\n";
		// result += "\tjitter\n";
		result += "}\n";
		// Center
		result += sphere(30, 0, 37.5, 1, 0, 0, 0, 0);
		// Time
		result += "text {\n";
		result += "\tttf\"Calibri.ttf\"\n";
		result += "\t\"Time = " + hours_text + ":" + minutes_text + ":" + seconds_text + "\"\n";
		result += "\t" + 0 + ",\n";
		result += "\t" + 0 + "\n";
		result += "\tpigment { color " + color(0, 0, 0) + " }\n";
		result += "\tscale " + 9.5 + "\n";
		result += "\trotate " + vector(90, 0, 0) + "\n";
		result += "\ttranslate " + vector(0, 0, -30) + "\n";
		result += "}\n";
		// Plane
		result += "plane {\n";
		result += "\t" + vector(0,1,0) + ",\n";
		result += "\t" + -30 + "\n";
		result += "\tpigment { color " + color(255,255,255) + " }\n";
		result += "}\n";
		// Dispatch component
		result += dispatch(component, 0);

		return result;
	}

	// DISPATCH

	protected static String dispatch(Element element, int indent) {
		if (element != null) {
			Class<?> iterator = element.getClass();

			while (Element.class.isAssignableFrom(iterator)) {
				try {
					// Obtain method
					Method method = Povray.class.getDeclaredMethod("convert", iterator, Integer.class);
					// Invoke method
					return (String) method.invoke(null, element, indent);
				} catch (SecurityException e) {
					throw new IllegalStateException(e);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				} catch (IllegalArgumentException e) {
					throw new IllegalStateException(e);
				} catch (InvocationTargetException e) {
					throw new IllegalStateException(e);
				} catch (NoSuchMethodException e) {
					iterator = iterator.getSuperclass();
				}
			}

			throw new IllegalStateException("Element type not supported: " + element.getClass().getName());
		} else {
			System.out.println("Povray: Element is not defined!");

			return "";
		}
	}

	// CONVERT

	protected static String convert(DefinitionComponent component, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + component.getName() + "\n";
		// Ports
		result += indent(indent + 1) + "// Ports\n";
		for (DefinitionPort port : component.getPorts()) {
			result += dispatch(port, indent + 1);
		}
		// Components
		result += indent(indent + 1) + "// Components\n";
		for (Component<?> child : component.getComponents()) {
			result += dispatch(child, indent + 1);
		}
		// Behaviors
		result += indent(indent + 1) + "// Behaviors\n";
		for (Behavior behavior : component.getBehaviors()) {
			result += dispatch(behavior, indent + 1);
		}
		// Parts
		result += indent(indent + 1) + "// Parts\n";
		for (Part part : component.getParts()) {
			result += dispatch(part, indent + 1);
		}
		// Transforms
		result += transform(component.getTransforms(), indent + 1);
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(ReferenceComponent component, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + component.getName() + "\n";
		// Template
		result += indent(indent + 1) + "// Template\n";
		result += dispatch(component.getTemplate(), indent + 1);
		// Transforms
		result += transform(component.getTransforms(), indent + 1);
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(Port port, Integer indent) {
		return "";
	}

	protected static String convert(MaterialPort port, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + port.getName() + "\n";
		// Volume
		result += indent(indent + 1) + "// Volume\n";
		result += dispatch(port.getVolume(), indent + 1);
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(KinematicEnergyPort port, Integer indent) {
		Transform transform = port.getTransform();

		String result = "";

		// Start
		result += indent(indent) + "union { // " + port.getName() + "\n";
		// Transform
		if (transform != null) {
			if (transform instanceof TranslationTransform) {
				TranslationTransform translation = (TranslationTransform) transform;

				double x = translation.getVector().getX();
				double y = translation.getVector().getY();
				double z = translation.getVector().getZ();

				double l = Math.sqrt(x * x + y * y + z * z);

				double ex = x / l;
				double ey = y / l;
				double ez = z / l;

				result += cylinder(0, 0, 0, x, y, z, VECTOR_LINE_RADIUS, VECTOR_RED, VECTOR_GREEN, VECTOR_BLUE,
						indent + 1);
				result += cone(x, y, z, VECTOR_ARROW_RADIUS, x + VECTOR_ARROW_LENGTH * ex, y + VECTOR_ARROW_LENGTH * ey,
						z + VECTOR_ARROW_LENGTH * ez, 0, VECTOR_RED, VECTOR_GREEN, VECTOR_BLUE, indent + 1);
			} else if (transform instanceof RotationTransform) {
				RotationTransform rotation = (RotationTransform) transform;

				double x = rotation.getRotationAxe().getX();
				double y = rotation.getRotationAxe().getY();
				double z = rotation.getRotationAxe().getZ();

				double l = Math.sqrt(x * x + y * y + z * z);

				double ex = x / l;
				double ey = y / l;
				double ez = z / l;

				result += cylinder(0, 0, 0, x, y, z, VECTOR_LINE_RADIUS, VECTOR_RED, VECTOR_GREEN, VECTOR_BLUE,
						indent + 1);
				result += cone(x, y, z, VECTOR_ARROW_RADIUS, x + VECTOR_ARROW_LENGTH * ex, y + VECTOR_ARROW_LENGTH * ey,
						z + VECTOR_ARROW_LENGTH * ez, 0, VECTOR_RED, VECTOR_GREEN, VECTOR_BLUE, indent + 1);
			} else {
				throw new IllegalStateException("Transform type not supported: " + transform);
			}
		}
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(Behavior behavior, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + behavior.getName() + "\n";
		// Initial label
		result += indent(indent + 1) + "// Initial label\n";
		if (behavior.getInitialLabel() != null) {
			result += dispatch(behavior.getInitialLabel(), indent + 1);
		}
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(State state, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + state.getName() + "\n";
		// Parts
		result += indent(indent + 1) + "// Parts\n";
		for (Part part : state.getParts()) {
			result += dispatch(part, indent + 1);
		}
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(Part part, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union { // " + part.getName() + "\n";
		// Volume
		result += indent(indent + 1) + "// Volume\n";
		result += dispatch(part.getVolume(), indent + 1);
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(CompositeVolume volume, Integer indent) {
		String result = "";

		// Start
		result += indent(indent) + "union {\n";
		// Volumes
		result += indent(indent + 1) + "// Volumes\n";
		for (Volume child : volume.getVolumes()) {
			result += dispatch(child, indent + 1);
		}
		// Transforms
		result += transform(volume.getTransforms(), indent + 1);
		// End
		result += indent(indent) + "}\n";

		return result;
	}

	protected static String convert(BoxVolume volume, Integer indent) {
		int red = volume.getRed();
		int green = volume.getGreen();
		int blue = volume.getBlue();

		double width = volume.getWidth();
		double height = volume.getHeight();
		double depth = volume.getDepth();

		String result = "";

		if (width > 0 && height > 0 && depth > 0) {
			if (volume.getFirstAncestorByType(MaterialPort.class) == null) {
				// Start
				result += indent(indent) + "box {\n";
				result += indent(indent + 1) + vector(-width / 2, -height / 2, -depth / 2) + ",\n";
				result += indent(indent + 1) + vector(width / 2, height / 2, depth / 2) + "\n";
				// Color
				result += indent(indent + 1) + "// Color\n";
				result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
				// Transforms
				result += transform(volume.getTransforms(), indent + 1);
				// End
				result += indent(indent) + "}\n";

				// Set the wire colors
				red = 0;
				green = 0;
				blue = 0;
			}

			// Start
			result += indent(indent) + "union {\n";
			// Edges (unten)
			result += cylinder(-width / 2, -height / 2, -depth / 2, width / 2, -height / 2, -depth / 2,
					WIRE_LINE_RADIUS, red, green, blue, indent + 1);
			result += cylinder(-width / 2, -height / 2, depth / 2, width / 2, -height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(-width / 2, -height / 2, -depth / 2, -width / 2, -height / 2, depth / 2,
					WIRE_LINE_RADIUS, red, green, blue, indent + 1);
			result += cylinder(width / 2, -height / 2, -depth / 2, width / 2, -height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			// Edges (oben)
			result += cylinder(-width / 2, height / 2, -depth / 2, width / 2, height / 2, -depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(-width / 2, height / 2, depth / 2, width / 2, height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(-width / 2, height / 2, -depth / 2, -width / 2, height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(width / 2, height / 2, -depth / 2, width / 2, height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			// Edges (mitte)
			result += cylinder(-width / 2, -height / 2, -depth / 2, -width / 2, height / 2, -depth / 2,
					WIRE_LINE_RADIUS, red, green, blue, indent + 1);
			result += cylinder(-width / 2, -height / 2, depth / 2, -width / 2, height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(width / 2, -height / 2, -depth / 2, width / 2, height / 2, -depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			result += cylinder(width / 2, -height / 2, depth / 2, width / 2, height / 2, depth / 2, WIRE_LINE_RADIUS,
					red, green, blue, indent + 1);
			// Corners (unten)
			for (int x = -1; x <= 1; x += 2) {
				for (int y = -1; y <= 1; y += 2) {
					for (int z = -1; z <= 1; z += 2) {
						result += sphere(x * width / 2, y * height / 2, z * depth / 2, WIRE_LINE_RADIUS, red, green,
								blue, indent + 1);
					}
				}
			}
			// Transforms
			result += transform(volume.getTransforms(), indent + 1);
			// End
			result += indent(indent) + "}\n";
		}

		return result;
	}

	protected static String convert(CylinderVolume volume, Integer indent) {
		int red = volume.getRed();
		int green = volume.getGreen();
		int blue = volume.getBlue();

		double height = volume.getHeight();
		double radius = volume.getRadius();

		String result = "";

		if (height > 0 && radius > 0) {
			if (volume.getFirstAncestorByType(MaterialPort.class) == null) {
				// Start
				result += indent(indent) + "cylinder {\n";
				result += indent(indent + 1) + vector(0, -height / 2, 0) + ",\n";
				result += indent(indent + 1) + vector(0, height / 2, 0) + ",\n";
				result += indent(indent + 1) + FORMAT.format(radius) + "\n";
				// Color
				result += indent(indent + 1) + "// Color\n";
				result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
				// Transforms
				result += transform(volume.getTransforms(), indent + 1);
				// End
				result += indent(indent) + "}\n";

				// Set the wire colors
				red = 0;
				green = 0;
				blue = 0;
			}

			// Start
			result += indent(indent) + "union {\n";
			// Circles
			for (int i = 0; i < WIRE_CYLINDER_CIRCLES; i++) {
				double y = -height / 2 + height * i / (WIRE_CYLINDER_CIRCLES - 1.);

				result += torus(radius, WIRE_LINE_RADIUS, y, red, green, blue, indent + 1);
			}
			// Lines
			for (int i = 0; i < WIRE_CYLINDER_LINES; i++) {
				double x = Math.sin(Math.PI * 2 * i / WIRE_CYLINDER_LINES) * radius;
				double z = Math.cos(Math.PI * 2 * i / WIRE_CYLINDER_LINES) * radius;

				result += cylinder(x, -height / 2, z, x, height / 2, z, WIRE_LINE_RADIUS, red, green, blue, indent + 1);
			}
			// Transforms
			result += transform(volume.getTransforms(), indent + 1);
			// End
			result += indent(indent) + "}\n";
		}

		return result;
	}

	protected static String convert(SphereVolume volume, Integer indent) {
		int red = volume.getRed();
		int green = volume.getGreen();
		int blue = volume.getBlue();

		double radius = volume.getRadius();

		String result = "";

		if (radius > 0) {
			if (volume.getFirstAncestorByType(MaterialPort.class) == null) {
				// Start
				result += indent(indent) + "sphere {\n";
				result += indent(indent + 1) + vector(0, 0, 0) + ",\n";
				result += indent(indent + 1) + FORMAT.format(radius) + "\n";
				// Color
				result += indent(indent + 1) + "// Color\n";
				result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
				// Transforms
				result += transform(volume.getTransforms(), indent + 1);
				// End
				result += indent(indent) + "}\n";

				// Set the wire colors
				red = 0;
				green = 0;
				blue = 0;
			}
		}

		return result;
	}

	protected static String convert(TranslationTransform transform, Integer indent) {
		Point vector = transform.getVector();

		String result = "";

		result += indent(indent) + "translate " + vector(vector) + "\n";

		return result;
	}

	protected static String convert(RotationTransform transform, Integer indent) {
		double angle = transform.getAngle();
		Point axis = transform.getRotationAxe();

		String result = "";

		if (!equal(transform.getRotationAxe(), 0, 0, 0)) {
			result += indent(indent) + "Axis_Rotate_Trans(" + vector(axis) + "," + FORMAT.format(angle) + ")\n";
		}

		return result;
	}

	// Transform

	private static String transform(List<Transform> transforms, int indent) {
		String result = "";

		result += indent(indent) + "// Transforms\n";
		for (int i = transforms.size() - 1; i >= 0; i--) {
			result += dispatch(transforms.get(i), indent);
		}

		return result;
	}

	// CYLINDER

	private static String cylinder(double bx, double by, double bz, double cx, double cy, double cz, double r, int red,
			int green, int blue, int indent) {
		String result = "";

		if (!equal(bx, by, bz, cx, cy, cz) && r > 0) {
			result += indent(indent) + "cylinder {\n";
			result += indent(indent + 1) + vector(bx, by, bz) + ",\n";
			result += indent(indent + 1) + vector(cx, cy, cz) + ",\n";
			result += indent(indent + 1) + FORMAT.format(r) + "\n";
			result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
			result += indent(indent) + "}\n";
		}

		return result;
	}

	private static String sphere(double cx, double cy, double cz, double r, int red, int green, int blue, int indent) {
		String result = "";

		if (r > 0) {
			result += indent(indent) + "sphere {\n";
			result += indent(indent + 1) + vector(cx, cy, cz) + ",\n";
			result += indent(indent + 1) + FORMAT.format(r) + "\n";
			result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
			result += indent(indent) + "}\n";
		}

		return result;
	}

	private static String torus(double r1, double r2, double y, int red, int green, int blue, int indent) {
		String result = "";

		if (r1 > 0 && r2 > 0 && r1 >= r2) {
			result += indent(indent) + "torus {\n";
			result += indent(indent + 1) + FORMAT.format(r1) + ",\n";
			result += indent(indent + 1) + FORMAT.format(r2) + "\n";
			result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
			result += indent(indent + 1) + "translate " + vector(0, y, 0) + "\n";
			result += indent(indent) + "}\n";
		}

		return result;
	}

	private static String cone(double x1, double y1, double z1, double r1, double x2, double y2, double z2, double r2,
			int red, int green, int blue, int indent) {
		String result = "";

		if (!equal(x1, y1, z1, x2, y2, z2) && r1 >= 0 && r2 >= 0) {
			result += indent(indent) + "cone {\n";
			result += indent(indent + 1) + vector(x1, y1, z1) + ",\n";
			result += indent(indent + 1) + FORMAT.format(r1) + "\n";
			result += indent(indent + 1) + vector(x2, y2, z2) + ",\n";
			result += indent(indent + 1) + FORMAT.format(r2) + "\n";
			result += indent(indent + 1) + "pigment { color " + color(red, green, blue) + " }\n";
			result += indent(indent) + "}\n";
		}

		return result;
	}

	// COLOR

	private static String color(int red, int green, int blue) {
		return color(red / 255.0, green / 255.0, blue / 255.0);
	}

	private static String color(double red, double green, double blue) {
		return "rgb<" + FORMAT.format(red) + "," + FORMAT.format(green) + "," + FORMAT.format(blue) + ">";
	}

	// VECTOR

	private static String vector(Point point) {
		return vector(point.getX(), point.getY(), point.getZ());
	}

	private static String vector(double x, double y, double z) {
		return "<" + FORMAT.format(x) + "," + FORMAT.format(y) + "," + FORMAT.format(z) + ">";
	}

	// INDENT

	private static String indent(int indent) {
		String result = "";

		for (int i = 0; i < indent; i++) {
			result += "\t";
		}

		return result;
	}

	// EQUAL

	private static boolean equal(Point point, double x, double y, double z) {
		return equal(point.getX(), point.getY(), point.getZ(), x, y, z);
	}

	private static boolean equal(double x1, double y1, double z1, double x2, double y2, double z2) {
		return x1 == x2 && y1 == y2 && z1 == z2;
	}

}
