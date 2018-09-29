package nukeduck.armorchroma.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class IconData {
    private final Map<String, ModEntry> modEntries = new HashMap<String, ModEntry>();

    public static final String DEFAULT = "default";

    public Map<String, ModEntry> getModEntries() {
        return modEntries;
    }

    /** @return The armor icon corresponding to {@code stack} */
	public int getIcon(ItemStack stack) {
        ModEntry mod = getModData(stack);
        return mod != null ? mod.getIcon(this, stack) : getSpecial(DEFAULT);
	}

    public int getSpecial(String key) {
        ModEntry minecraft = modEntries.get("minecraft");
        return minecraft != null ? minecraft.getSpecial(null, key) : 0;
    }

	private ModEntry getModData(ItemStack stack) {
		String modid = null;

		if(stack != null) {
			Item item = stack.getItem();

			if(item != null) {
				ResourceLocation name = item.getRegistryName();
				if(name != null) modid = name.getResourceDomain();
			}
		}
		return modEntries.get(modid);
	}

    public static class ModEntry {
        public final Map<String, Integer> materials = new HashMap<>();
        public final Map<String, Integer> items = new HashMap<>();
        public final Map<String, Integer> special = new HashMap<>();

        public void putAll(ModEntry other) {
            materials.putAll(other.materials);
            items.putAll(other.items);
            special.putAll(other.special);
        }

        public int getIcon(IconData fallback, ItemStack stack) {
            Integer index = null;

            if(stack != null) {
                Item item = stack.getItem();

                // Test armor material
                if(item instanceof ItemArmor) {
                    ArmorMaterial material = ((ItemArmor)item).getArmorMaterial();
                    if(material != null) index = Util.getGlob(materials, material.getName());
                }
                // Test item ID
                if(index == null && item != null) {
                    ResourceLocation resource = item.getRegistryName();
                    if(resource != null) index = Util.getGlob(items, resource.getResourcePath());
                }
            }
            return index != null ? index : getSpecial(fallback, DEFAULT);
        }

        public int getSpecial(IconData fallback, String key) {
            return special.getOrDefault(key, fallback != null ? fallback.getSpecial(key) : 0);
        }
    }
}
