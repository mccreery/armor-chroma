package nukeduck.armorchroma.config;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import nukeduck.armorchroma.ArmorChroma;
import nukeduck.armorchroma.config.IconData.ModEntry;
import nukeduck.armorchroma.config.IconMap.ItemIconMap;
import nukeduck.armorchroma.config.IconMap.MaterialIconMap;

@SuppressWarnings("serial")
public class IconData extends HashMap<String, ModEntry> {
	/** @return The armor icon corresponding to {@code stack} */
	public int getIcon(ItemStack stack) {
		ModEntry mod = get(stack.getItem().getRegistryName().getResourceDomain());
		return mod != null ? mod.getIcon(stack) : ArmorChroma.config.iconDefault;
	}

	@Override
	public ModEntry put(String key, ModEntry value) {
		// Combine mod entries if needed
		if(containsKey(key)) {
			ModEntry existing = get(key);
			existing.combine(value);
			return existing;
		} else {
			return super.put(key, value);
		}
	}

	/** Stores icon entries for a single mod */
	public static class ModEntry {
		MaterialIconMap materials = new MaterialIconMap();
		ItemIconMap items = new ItemIconMap();

		/** @return The armor icon corresponding to {@code stack} limited to the mod */
		public int getIcon(ItemStack stack) {
			Integer iconIndex = items.get(stack);
			if(iconIndex != null) return iconIndex;

			iconIndex = materials.get(stack);
			return iconIndex != null ? iconIndex : ArmorChroma.config.iconDefault;
		}

		/** Adds all icons from {@code other} to the entry */
		public void combine(ModEntry other) {
			materials.putAll(other.materials);
			items.putAll(other.items);
		}
	}
}
