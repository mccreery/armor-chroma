package nukeduck.armorchroma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import nukeduck.armorchroma.parse.ComparatorIconGroup;
import nukeduck.armorchroma.parse.ComparatorItemEntry;
import nukeduck.armorchroma.parse.ComparatorMaterialEntry;
import nukeduck.armorchroma.parse.IconData;
import nukeduck.armorchroma.parse.IconData.IconGroup;
import nukeduck.armorchroma.parse.IconData.IconGroup.ItemEntry;
import nukeduck.armorchroma.parse.IconData.IconGroup.MaterialEntry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class Config {
	private Configuration config;
	public File directory;
	private File icons;

	public void reload() {
		this.init(this.config.getConfigFile(), this.icons);
	}

	/** Default armor icon Y position */
	public int iconDefault;
	/** Uncolored leather armor icon Y position */
	public int iconLeather;

	/** Information from icons.json */
	public IconData iconData;

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

	/** Returns the icon index of the given {@link ItemStack}.
	 * @param stack The item stack to look up
	 * @return The icon index found, or {@link #iconDefault} if none was found. */
	public int getIcon(ItemStack stack) {
		Item item = stack.getItem();
		String[] fullName = Item.itemRegistry.getNameForObject(item).split(":");

		IconGroup groupNeedle = new IconGroup();
		groupNeedle.modid = fullName[0];

		int i = Arrays.binarySearch(this.iconData.groupsA, groupNeedle, ComparatorIconGroup.INSTANCE);
		if(i >= 0) {
			IconGroup group = this.iconData.groupsA[i];

			if(group.itemsA != null) {
				ItemEntry itemNeedle = new ItemEntry();
				itemNeedle.id = fullName[1];

				i = Arrays.binarySearch(group.itemsA, itemNeedle, ComparatorItemEntry.INSTANCE);
				if(i >= 0) {
					return group.itemsA[i].index;
				}
			}
		}

		if(item instanceof ItemArmor) {
			MaterialEntry materialNeedle = new MaterialEntry();
			materialNeedle.name = ((ItemArmor) item).getArmorMaterial().name();
	
			i = Arrays.binarySearch(this.iconData.materialsA, materialNeedle, ComparatorMaterialEntry.INSTANCE);
			if(i >= 0) {
				return this.iconData.materialsA[i].index;
			}
		}
		return this.iconDefault;
	}

	/** Initializes the configuration.
	 * @param file The configuration file path */
	public void init(File file, File icons) {
		this.config = new Configuration(file);
		this.config.load();

		this.iconDefault = this.config.getInt("iconDefault", Configuration.CATEGORY_GENERAL, 2, 0, 28, "Default Y index for armor bar icons");
		this.iconLeather = this.config.getInt("leatherColor", Configuration.CATEGORY_GENERAL, 5, 0, 28, "Default Y index for uncolored leather armor");
		this.renderGlint = this.config.getBoolean("renderGlint", Configuration.CATEGORY_GENERAL, GLINT_DEFAULT, "Whether or not to render enchanted glint");
		this.renderColor = this.config.getBoolean("renderColor", Configuration.CATEGORY_GENERAL, COLOR_DEFAULT, "Whether or not to render leather color");
		this.alwaysBreak = this.config.getBoolean("alwaysBreak", Configuration.CATEGORY_GENERAL, BREAK_DEFAULT, "Whether or not to always insert a breaking line between armor pieces, when necessary");

		this.icons = icons;
		try {
			this.iconData = this.getIconData();
		} catch (IOException e) {
			ArmorChroma.INSTANCE.logger.error("Unable to load icon data");
			e.printStackTrace();
		}

		if(this.config.hasChanged()) this.config.save();
	}

	private IconData getIconData() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(this.icons));
			IconData data = new Gson().fromJson(reader, IconData.class);

			data.groupsA = data.groups.toArray(new IconGroup[data.groups.size()]);
			Arrays.sort(data.groupsA, ComparatorIconGroup.INSTANCE);

			List<MaterialEntry> materials = new ArrayList<MaterialEntry>();

			for(IconGroup group : data.groups) {
				if(group.items != null) {
					group.itemsA = group.items.toArray(new ItemEntry[group.items.size()]);
					Arrays.sort(group.itemsA, ComparatorItemEntry.INSTANCE);
				}
				if(group.materials != null) {
					materials.addAll(group.materials);
				}
			}

			data.materialsA = materials.toArray(new MaterialEntry[materials.size()]);
			Arrays.sort(data.materialsA, ComparatorMaterialEntry.INSTANCE);
			return data;
		} catch(FileNotFoundException e) {
			ArmorChroma.INSTANCE.logger.info("icons.json did not exist. Loading defaults");
			FileUtils.copyURLToFile(this.getClass().getResource("/assets/armorchroma/icons.json"), this.icons);
			return this.getIconData();
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}
}
