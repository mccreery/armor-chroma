package nukeduck.armorchroma.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import nukeduck.armorchroma.Mergeable;

public class IconData extends MergeMap<String, IconData.ModEntry> {
    public static final long serialVersionUID = 1L;
    public static final String DEFAULT = "default";

    private final ModEntry minecraftEntry;

    public IconData() {
        put("minecraft", minecraftEntry = new ModEntry() {
            @Override
            public int getSpecial(String key) {
                return special.getOrDefault(key, 0);
            }
        });
    }

    /** @return The armor icon corresponding to {@code stack} */
	public int getIcon(ItemStack stack) {
        ModEntry mod = get(stack);
        return mod != null ? mod.getIcon(stack) : getSpecial(DEFAULT);
	}

    public int getSpecial(String key) {
        return minecraftEntry.getSpecial(key);
    }

	/** @see #get(Object) */
	private ModEntry get(ItemStack stack) {
		String modid = null;

		if(stack != null) {
			Item item = stack.getItem();

			if(item != null) {
				ResourceLocation name = item.getRegistryName();
				if(name != null) modid = name.getResourceDomain();
			}
		}
		return get(modid);
	}

    protected class ModEntry implements Mergeable<ModEntry> {
        public final Map<String, Integer> materials = new HashMap<>();
        public final Map<String, Integer> items = new HashMap<>();
        public final Map<String, Integer> special = new HashMap<>();

        public ModEntry merge(ModEntry other) {
            materials.putAll(other.materials);
            items.putAll(other.items);
            special.putAll(other.special);
            return this;
        }

        public int getIcon(ItemStack stack) {
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
            return index != null ? index : getSpecial(DEFAULT);
        }

        public int getSpecial(String key) {
            return special.getOrDefault(key, IconData.this.getSpecial(key));
        }
    }
}
