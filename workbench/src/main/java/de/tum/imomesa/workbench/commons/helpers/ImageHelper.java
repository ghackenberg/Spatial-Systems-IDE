package de.tum.imomesa.workbench.commons.helpers;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageHelper {
	
	// constants
	
	private static final int ICON_SIZE = 14;
	private static final String PATH_ICON_FOLDER = "icons/folder.png";
	private static final String PATH_ICON_DELETE = "icons/actions/remove.png";
	
	public static Node getFolderIcon(Class<?> type) {
		ImageView view = loadIconOfIcon(getIcon(type));
		return loadIconFolder(view);
	}
	
	public static ImageView getIcon(Class<?> type) {
		ImageView view = getImageIcons(type);
		return loadIcon(view);
	}
	
	public static Node getIconDeleted(Class<?> type) {
		ImageView view = loadIcon(getImageIcons(type));
		return loadIconDeleted(view);
	}
	
	public static Node getIconPort(Class<?> type, IntegerProperty property) {
		ImageView view = getImageIcons(type);
		return loadIconPort(view, property);
	}
	
	public static ImageView getImageAsIcon(String path) {
		return loadIcon(loadImage(path));
	}
	
	private static ImageView getImageIcons(Class<?> type)	{
		Class<?> iterator = type;
		
		while (iterator != null) {
			try {
				return loadImage("icons/elements/" + iterator.getSimpleName().toLowerCase() + ".png");
			}
			catch (Exception e) {
				// Ignore
			}
			iterator = iterator.getSuperclass();
		}
		
		throw new IllegalStateException("No icon found: " + type.getName());
	}
	
	private static Node loadIconDeleted(ImageView view) {
		ImageView viewIcon = loadIconOfIcon(loadImage(PATH_ICON_DELETE));
		return loadDoubledIcon(view, viewIcon);
	}
	
	private static Node loadIconFolder(ImageView view) {
		ImageView viewMain = loadIcon(loadImage(PATH_ICON_FOLDER));
		return loadDoubledIcon(viewMain, view);
	}

	private static Node loadIconPort(ImageView view, IntegerProperty direction) {
		view.opacityProperty().bind(Bindings.add(direction, 0.5));
		return view;
	}
	
	private static Node loadDoubledIcon(ImageView viewMain, ImageView viewIcon) {
		
		// blend images over each other
		viewIcon.setBlendMode(BlendMode.SRC_OVER);
        Group blend = new Group(viewMain, viewIcon);

		// return image
		return blend;
	}

	private static ImageView loadIconOfIcon(ImageView view) {

		// Scale the image to the preferred size
		view.setPreserveRatio(true);
		int sizeIcon = ICON_SIZE - 6;
		view.setFitHeight(ICON_SIZE - 6);
		// move the image
		view.setTranslateX(ICON_SIZE - sizeIcon - 1);
		view.setTranslateY(ICON_SIZE - sizeIcon - 1);
		view.setSmooth(true);
		
		return view;
	}
	
	private static ImageView loadIcon(ImageView view) {

		// Scale the image to the preferred size
		view.setPreserveRatio(true);
		view.setFitHeight(ICON_SIZE);
		view.setSmooth(true);
		
		// return image
		return view;
	}
	
	private static Map<String, Image> CACHE = new HashMap<>();
	
	private static ImageView loadImage(String path) {
		if (!CACHE.containsKey(path)) {
			CACHE.put(path, new Image(path));
		}
		return new ImageView(CACHE.get(path));
	}
}
