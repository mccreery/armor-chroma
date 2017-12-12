package nukeduck.armorchroma;

import java.io.File;
import java.net.URL;

public class Constants {
	public static final String RENDER_GLINT = "renderGlint";
	public static final String RENDER_GLINT_DESC = "Display enchanted shine";
	public static final String RENDER_COLOR = "renderColor";
	public static final String RENDER_COLOR_DESC = "Display colored leather armor";
	public static final String ALWAYS_BREAK = "alwaysBreak";
	public static final String ALWAYS_BREAK_DESC = "Always add a vertical line between armor pieces";

	/** Default value for {@link Config#renderGlint} */
	public static final boolean GLINT_DEFAULT = true;
	/** Default value for {@link Config#renderColor} */
	public static final boolean COLOR_DEFAULT = true;
	/** Default value for {@link Config#alwaysBreak} */
	public static final boolean BREAK_DEFAULT = false;

	public static final String ICON_DEFAULT = "iconDefault";
	public static final String ICON_DEFAULT_DESC = "Default icon index";
	public static final String ICON_LEATHER = "iconLeather";
	public static final String ICON_LEATHER_DESC = "Leather icon when " + RENDER_COLOR + " is disabled";

	public static final String NOT_FOUND = "%s did not exist. Loading defaults";
	public static final String SUCCESS = "%s loaded successfully";
	public static final String ERROR = "An unknown error occurred loading %s";
	public static final String ERROR_GENERIC = "An unknown error occurred";
	public static final String RELOAD = "Reloading config";

	/** Same as {@link #getMessage(String, Object...) but assumes the path of {@code file}
	 * @param message The message to format
	 * @param file The file to grab a path from */
	public static final String getMessage(String message, File file) {
		return getMessage(message, file.getAbsolutePath());
	}
	/** Same as {@link #getMessage(String, Object...) but assumes the path of {@code url}
	 * @param message The message to format
	 * @param url The URL to grab a path from */
	public static final String getMessage(URL url, String message) {
		return getMessage(message, url.getPath());
	}

	/** @param message The message to format
	 * @param paths Objects to format the message with
	 * @return A formatted message with the given strings */
	public static final String getMessage(String message, Object... paths) {
		return String.format(message, paths);
	}
}
