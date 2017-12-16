package nukeduck.armorchroma.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nukeduck.armorchroma.ArmorChroma;

public class Config extends Configuration {
	public Config(File file) {
		super(file);
	}

	/** Information from icons.json */
	private IconData iconData;

	/** Default armor icon Y position */
	public int iconDefault;
	/** Flag to render glint on enchanted pieces */
	public boolean renderGlint;
	/** Flag to compress the bar into different colored borders */
	public boolean compressBar;

	/** Returns the icon index of the given {@link ItemStack}.
	 * @param stack The item stack to look up
	 * @return The icon index found, or {@link #iconDefault} if none was found. */
	public int getIcon(ItemStack stack) {
		return iconData.getIcon(stack);
	}

	@Override
	public void load() {
		ArmorChroma.logger.info("Reloading config");
		super.load(); // Load file as default

		// Custom properties
		iconDefault = getInt("iconDefault", CATEGORY_GENERAL, 2, "Default icon index");
		renderGlint = getBoolean("renderGlint", CATEGORY_GENERAL, true, "Display enchanted shine");
		compressBar = getBoolean("compressBar", CATEGORY_GENERAL, false, "Compress the bar into different colored borders when your armor exceeds 20");

		// Load icon data and overrides
		try {
			this.iconData = getIconData();
		} catch(IOException e) {
			ArmorChroma.logger.error("An error occurred loading icon data");
			e.printStackTrace();
		}
		if(hasChanged()) save();
	}

	/** Reads (and copies, if necessary) icons.json to load icon information.
	 * @return The resultant icon data from file */
	private IconData getIconData() throws IOException {
		// Default icons
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/assets/armorchroma/icons.json")));
		IconData data = new Gson().fromJson(reader, IconData.class);
		reader.close();

		// Combine default with overrides
		File overrides = new File(getConfigFile().getParentFile(), "icons.json");

		if(overrides.exists()) {
			reader = new BufferedReader(new FileReader(overrides));
			IconData dataOverrides = new Gson().fromJson(reader, IconData.class);
			reader.close();

			data.putAll(dataOverrides);
		} else {
			// First run, or file has been deleted
			ArmorChroma.logger.warn(overrides.getAbsolutePath() + " did not exist. Loading defaults");
			FileUtils.copyURLToFile(getClass().getResource("/assets/armorchroma/overrides.json"), overrides);
		}
		return data;
	}

	/** As {@link #getInt(String, String, int, int, int, String)} but with no limits */
	private int getInt(String name, String category, int defaultValue, String comment) {
		Property prop = get(category, name, defaultValue);

		prop.setLanguageKey(name);
		prop.setComment(comment + " [default: " + defaultValue + "]");

		return prop.getInt(defaultValue);
	}
}
