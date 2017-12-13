package nukeduck.armorchroma.config;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import nukeduck.armorchroma.ArmorChroma;
import nukeduck.armorchroma.config.IconData.ModEntry;
import nukeduck.armorchroma.config.IconMap.ItemIconMap;
import nukeduck.armorchroma.config.IconMap.MaterialIconMap;

@SuppressWarnings("serial")
public class IconData extends HashMap<String, ModEntry> {
	public int getIcon(ItemStack stack) {
		ResourceLocation id = (ResourceLocation)Item.itemRegistry.getNameForObject(stack.getItem());
		ModEntry mod = get(id.getResourceDomain());

		int iconIndex = mod != null ? mod.getIcon(stack) : ArmorChroma.config.iconDefault;

		return iconIndex;
	}

	@Override
	public ModEntry put(String key, ModEntry value) {
		if(containsKey(key)) {
			ModEntry existing = get(key);
			existing.combine(value);
			return existing;
		} else {
			return super.put(key, value);
		}
	}

	public static class ModEntry {
		MaterialIconMap materials = new MaterialIconMap();
		ItemIconMap items = new ItemIconMap();

		public int getIcon(ItemStack stack) {
			Integer iconIndex = items.get(stack);
			if(iconIndex != null) return iconIndex;

			iconIndex = materials.get(stack);
			return iconIndex != null ? iconIndex : ArmorChroma.config.iconDefault;
		}

		public void combine(ModEntry other) {
			materials.putAll(other.materials);
			items.putAll(other.items);
		}
	}
}
