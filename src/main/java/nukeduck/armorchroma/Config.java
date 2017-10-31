package nukeduck.armorchroma;

import static nukeduck.armorchroma.Constants.ALWAYS_BREAK;
import static nukeduck.armorchroma.Constants.ALWAYS_BREAK_DESC;
import static nukeduck.armorchroma.Constants.BREAK_DEFAULT;
import static nukeduck.armorchroma.Constants.COLOR_DEFAULT;
import static nukeduck.armorchroma.Constants.GLINT_DEFAULT;
import static nukeduck.armorchroma.Constants.ICON_DEFAULT;
import static nukeduck.armorchroma.Constants.ICON_DEFAULT_DESC;
import static nukeduck.armorchroma.Constants.ICON_LEATHER;
import static nukeduck.armorchroma.Constants.ICON_LEATHER_DESC;
import static nukeduck.armorchroma.Constants.RENDER_COLOR;
import static nukeduck.armorchroma.Constants.RENDER_COLOR_DESC;
import static nukeduck.armorchroma.Constants.RENDER_GLINT;
import static nukeduck.armorchroma.Constants.RENDER_GLINT_DESC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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

	/** Reloads the config from the existing file. */
	public void reload() {
		ArmorChroma.INSTANCE.logger.info(Constants.RELOAD);
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

	/** Returns the icon index of the given {@link ItemStack}.
	 * @param stack The item stack to look up
	 * @return The icon index found, or {@link #iconDefault} if none was found. */
	public int getIcon(ItemStack stack) {
		Item item = stack.getItem();
		String[] fullName = Item.itemRegistry.getNameForObject(item).toString().split(":");

		IconGroup groupNeedle = new IconGroup();
		groupNeedle.modid = fullName[0];

		MaterialEntry materialNeedle = null;

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

			if(group.materialsA != null && item instanceof ItemArmor) {
				materialNeedle = new MaterialEntry();
				materialNeedle.name = ((ItemArmor) item).getArmorMaterial().name();

				i = Arrays.binarySearch(group.materialsA, materialNeedle, ComparatorMaterialEntry.INSTANCE);
				if(i >= 0) {
					return group.materialsA[i].index;
				}
			}
		}

		if(materialNeedle != null) {
			i = Arrays.binarySearch(this.iconData.materialsA, materialNeedle, ComparatorMaterialEntry.INSTANCE);
			if(i >= 0) {
				return this.iconData.materialsA[i].index;
			}
		} else if(item instanceof ItemArmor) {
			materialNeedle = new MaterialEntry();
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

		this.iconDefault = this.config.getInt(ICON_DEFAULT, Configuration.CATEGORY_GENERAL, 2, 0, 28, ICON_DEFAULT_DESC);
		this.iconLeather = this.config.getInt(ICON_LEATHER, Configuration.CATEGORY_GENERAL, 0, 0, 28, ICON_LEATHER_DESC);
		this.renderGlint = this.config.getBoolean(RENDER_GLINT, Configuration.CATEGORY_GENERAL, GLINT_DEFAULT, RENDER_GLINT_DESC);
		this.renderColor = this.config.getBoolean(RENDER_COLOR, Configuration.CATEGORY_GENERAL, COLOR_DEFAULT, RENDER_COLOR_DESC);
		this.alwaysBreak = this.config.getBoolean(ALWAYS_BREAK, Configuration.CATEGORY_GENERAL, BREAK_DEFAULT, ALWAYS_BREAK_DESC);

		this.icons = icons;
		try {
			this.iconData = this.getIconData();
		} catch (IOException e) {
			ArmorChroma.INSTANCE.logger.error(Constants.getMessage(Constants.ERROR, this.icons));
			e.printStackTrace();
		}

		if(this.config.hasChanged()) this.config.save();
	}

	/** Reads (and copies, if necessary) icons.json to load icon information.
	 * @return The resultant icon data from file */
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
					group.materialsA = group.materials.toArray(new MaterialEntry[group.materials.size()]);
					Arrays.sort(group.materialsA, ComparatorMaterialEntry.INSTANCE);
					materials.addAll(group.materials);
				}
			}

			data.materialsA = materials.toArray(new MaterialEntry[materials.size()]);
			Arrays.sort(data.materialsA, ComparatorMaterialEntry.INSTANCE);

			ArmorChroma.INSTANCE.logger.info(Constants.getMessage(Constants.SUCCESS, this.icons));
			return data;
		} catch(FileNotFoundException e) {
			ArmorChroma.INSTANCE.logger.warn(Constants.getMessage(Constants.NOT_FOUND, this.icons));
			URL iconsInternal = this.getClass().getResource("/assets/armorchroma/icons.json");
			try {
				FileUtils.copyURLToFile(iconsInternal, this.icons);
			} catch(FileNotFoundException f) {
				ArmorChroma.INSTANCE.logger.error(Constants.getMessage(Constants.NOT_FOUND, iconsInternal));
			}
			return this.getIconData();
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}
}
