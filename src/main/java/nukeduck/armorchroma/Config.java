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
	public HashMap<String, Integer> exceptions = new HashMap<String, Integer>();

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

	public static final String[] DEFAULT_ICONS = {
		"CLOTH=0", "CHAIN=1", "IRON=2", "GOLD=3", "DIAMOND=4", // Vanilla
		"MANASTEEL=28", "TERRASTEEL=29", "B_ELEMENTIUM=30", "MANAWEAVE=31", // Botania
		"TF:COPPER=56", "TF:TIN=57", "TF:SILVER=58",
		"TF:LEAD=59", "TF:NICKEL=60", "TF:ELECTRUM=61",
		"TF:INVAR=62", "TF:BRONZE=63", "TF:PLATINUM=64", // Thermal Foundation
		"IC2_BRONZE=86", "IC2_ALLOY=87" // IC2
	};

	public static final String[] DEFAULT_EXCEPTIONS = {
		"IC2:itemArmorHazmatHelmet=84", "IC2:itemArmorHazmatChestplate=84",
		"IC2:itemArmorHazmatLeggings=84", "IC2:itemArmorRubBoots=85",

		"IC2:itemArmorNanoHelmet=88", "IC2:itemArmorNanoChestplate=88",
		"IC2:itemArmorNanoLegs=88", "IC2:itemArmorNanoBoots=88",
		"IC2:itemArmorQuantumHelmet=89", "IC2:itemArmorQuantumChestplate",
		"IC2:itemArmorQuantumLegs=89", "IC2:itemArmorQuantumBoots=89",

		"IC2:itemSolarHelmet=2", "IC2:itemNightvisionGoggles=85",
		"IC2:itemStaticBoots=85", "IC2:itemArmorJetpack=2",
		"IC2:itemArmorJetpackElectric=2", "IC2:itemArmorCFPack=2",
		"IC2:itemArmorEnergypack=2", "IC2:itemArmorBatpack=90",
		"IC2:itemArmorAdvBatpack=91"
	};

	/** Initializes the configuration.
	 * @param file The configuration file path */
	public void init(File file) {
		this.config = new Configuration(file);
		this.config.load();

		this.iconDefault = this.config.getInt("iconDefault", Configuration.CATEGORY_GENERAL, 2, 0, 28, "Default Y index for armor bar icons");
		this.iconLeather = this.config.getInt("leatherColor", Configuration.CATEGORY_GENERAL, 5, 0, 28, "Default Y index for uncolored leather armor");

		String[] iconStrings = this.config.getStringList("icons", Configuration.CATEGORY_GENERAL, DEFAULT_ICONS, "Icon Y indices for armor bar icons");
		for(String s : iconStrings) {
			if(s.contains(KEYVAL_SEPARATOR)) {
				try {
					int separator = s.lastIndexOf(KEYVAL_SEPARATOR);
					icons.put(s.substring(0, separator), Integer.parseInt(s.substring(separator + 1, s.length())));
				} catch(NumberFormatException ex) {}
			}
		}

		String[] exceptionStrings = this.config.getStringList("iconExceptions", Configuration.CATEGORY_GENERAL, DEFAULT_EXCEPTIONS, "Custom icons for single pieces of armor");
		for(String s : exceptionStrings) {
			if(s.contains(KEYVAL_SEPARATOR)) {
				try {
					int separator = s.lastIndexOf(KEYVAL_SEPARATOR);
					exceptions.put(s.substring(0, separator), Integer.parseInt(s.substring(separator + 1, s.length())));
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
