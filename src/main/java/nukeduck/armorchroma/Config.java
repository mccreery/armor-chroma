package nukeduck.armorchroma;

import java.io.File;
import java.util.HashMap;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.config.Configuration;

public class Config {
	private Configuration config;

	/** Default armor icon Y position */
	public int iconDefault;
	/** Uncolored leather armor icon Y position */
	public int iconLeather;
	/** Map containing links between armor material name and icon Y position */
	public HashMap<String, Integer> icons = new HashMap<String, Integer>();

	/** Flag to render glint on enchanted pieces */
	public boolean renderGlint;
	/** Flag to render colored leather armor */
	public boolean renderColor;
	/** Flag to break between each piece regardless of type */
	public boolean alwaysBreak;

	/** Default values for {@link #renderGlint}, {@link #renderColor} and {@link #alwaysBreak} */
	public static final boolean GLINT_DEFAULT = true, COLOR_DEFAULT = true, BREAK_DEFAULT = false;

	/** Separator for key-value combinations within the icon list */
	private static final String KEYVAL_SEPARATOR = "=";

	/** Initializes the configuration.
	 * @param file The configuration file path */
	public void init(File file) {
		this.config = new Configuration(file);
		this.config.load();

		String[] defaults = new String[ArmorMaterial.values().length];
		int i;
		for(i = 0; i < ArmorMaterial.values().length; i++) {
			defaults[i] = ArmorMaterial.values()[i].toString() + KEYVAL_SEPARATOR + String.valueOf(i);
		}

		String[] iconStrings = this.config.getStringList("icons", Configuration.CATEGORY_GENERAL, defaults, "Icon Y indices for armor bar icons");
		this.iconDefault = this.config.getInt("iconDefault", Configuration.CATEGORY_GENERAL, 2, 0, 28, "Default Y index for armor bar icons");
		this.iconLeather = this.config.getInt("leatherColor", Configuration.CATEGORY_GENERAL, 5, 0, 28, "Default Y index for uncolored leather armor");

		for(String s : iconStrings) {
			if(s.contains(KEYVAL_SEPARATOR)) {
				try {
					String[] split = s.split(KEYVAL_SEPARATOR);
					icons.put(split[0], Integer.parseInt(split[1]));
				} catch(NumberFormatException ex) {}
			}
		}

		this.renderGlint = this.config.getBoolean("renderGlint", Configuration.CATEGORY_GENERAL, GLINT_DEFAULT, "Whether or not to render enchanted glint");
		this.renderColor = this.config.getBoolean("renderColor", Configuration.CATEGORY_GENERAL, COLOR_DEFAULT, "Whether or not to render leather color");
		this.alwaysBreak = this.config.getBoolean("alwaysBreak", Configuration.CATEGORY_GENERAL, BREAK_DEFAULT, "Whether or not to always insert a breaking line between armor pieces, when necessary");

		if(this.config.hasChanged()) this.config.save();
	}

	/** Looks up the icon for a given armor material.
	 * @param material The armor material to look up
	 * @return The found icon, or {@link #iconDefault} if not found */
	public int getIconIndex(ArmorMaterial material) {
		if(material == null) return this.iconDefault;

		String s = material.toString();
		if(this.icons.containsKey(s)) {
			return this.icons.get(s);
		}
		return this.iconDefault;
	}
}
