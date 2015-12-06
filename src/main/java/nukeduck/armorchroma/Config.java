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
		"IC2_BRONZE=86", "IC2_ALLOY=87", // IC2
		"THAUMIUM=140", "VOID=141", "SPECIAL=142",
		"FORTRESS=143", // Thaumcraft
		"VOIDFORTRESS=141", "SHADOW2=142", // Tainted Magic
		"WG:PRIMORDIALARMOR=146", "WG:ADVANCEDCLOTH=2", // Witching Gadgets
		"ADAMINITEARMOR=147", "MASCOFCRUELTY=0", // Thaumic Additions
		"ATHAUMINITE=148", "TBBLOODY=149", // Thaumic Bases
		"VampireArmor=117", "GlassArmor=118", "ImbuedArmor=119", // Blood Arsenal
		"NAGA_SCALE=168", "FIERY=169", "IRONWOOD=170",
		"KNIGHTMETAL=172", "KNIGHTPHANTOM=173", "YETI=174",
		"ARCTIC=175", // Twilight Forest
		"MONACLE=0", "exosuit=198", "GILDEDGOLD=3", "BRASS=199" // Flaxbeard's Steam Power
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
		"IC2:itemArmorAdvBatpack=91", // IC2

		"AWWayofTime:boundHelmet=112", "AWWayofTime:boundPlate=112",
		"AWWayofTime:boundLeggings=112", "AWWayofTime:boundBoots=112",
		"AWWayofTime:boundHelmetFire=113", "AWWayofTime:boundPlateFire=113",
		"AWWayofTime:boundLeggingsFire=113", "AWWayofTime:boundBootsFire=113",
		"AWWayofTime:boundHelmetWater=114", "AWWayofTime:boundPlateWater=114",
		"AWWayofTime:boundLeggingsWater=114", "AWWayofTime:boundBootsWater=114",
		"AWWayofTime:boundHelmetWind=115", "AWWayofTime:boundPlateWind=115",
		"AWWayofTime:boundLeggingsWind=115", "AWWayofTime:boundBootsWind=115",
		"AWWayofTime:boundHelmetEarth=116", "AWWayofTime:boundPlateEarth=116",
		"AWWayofTime:boundLeggingsEarth=116", "AWWayofTime:boundBootsEarth=116", // Blood Magic

		"Thaumcraft:BootsTraveller=0", "Thaumcraft:HoverHarness=0",
		"Thaumcraft:ItemHelmetCultistRobe=144", "Thaumcraft:ItemChestplateCultistRobe=144",
		"Thaumcraft:ItemLeggingsCultistRobe=144", "Thaumcraft:ItemHelmetCultistPlate=144",
		"Thaumcraft:ItemChestplateCultistPlate=144", "Thaumcraft:ItemLeggingsCultistPlate=144",
		"Thaumcraft:ItemBootsCultist=144", "Thaumcraft:ItemChestplateCultistLeaderPlate=144",
		"Thaumcraft:ItemLeggingsCultistLeaderPlate=144", // Thaumcraft

		"TaintedMagic:BootsVoidwalker=0",
		"TaintedMagic:CrimsonVoidHelm=144", "TaintedMagic:CrimsonVoidChest=144",
		"TaintedMagic:CrimsonVoidLegs=144", "TaintedMagic:CrimsonVoidBoots=144",
		"TaintedMagic:CrimsonPlateVoidHelm=144", "TaintedMagic:CrimsonPlateVoidChest=144",
		"TaintedMagic:CrimsonPlateVoidLegs=144",
		"TaintedMagic:CrimsonLeaderVoidHelm=144", "TaintedMagic:CrimsonLeaderVoidChest=144",
		"TaintedMagic:CrimsonLeaderVoidLegs=144",
		"TaintedMagic:HelmetShadowFortress=145", "TaintedMagic:ChestShadowFortress=145",
		"TaintedMagic:LegsShadowFortress=145", // Tainted Magic

		"TwilightForest:item.steeleafHelm=171", "TwilightForest:item.steeleafPlate=171",
		"TwilightForest:item.steeleafLegs=171", "TwilightForest:item.steeleafBoots=171", // Twilight Forest

		"witchery:vampirehat=196", "witchery:vampirehelmet=196",
		"witchery:vampirecoat=196", "witchery:vampirecoat_female=196",
		"witchery:vampirechaincoat=196", "witchery:vampirechaincoat_female=196",
		"witchery:vampirelegs=196", "witchery:vampirelegs_kilt=196",
		"witchery:vampireboots=196", "witchery:deathscowl=197",
		"witchery:deathsrobe=197", "witchery:deathsfeet=197" // Witchery
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
