package nukeduck.armorchroma.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class IconTable {
    public final Map<String, Integer> materials = new HashMap<>();
    public final Map<String, Integer> items = new HashMap<>();
    public final Map<String, Integer> special = new HashMap<>();

    public void putAll(IconTable other) {
        materials.putAll(other.materials);
        items.putAll(other.items);
        special.putAll(other.special);
    }

    public Integer getIconIndex(ItemStack stack) {
        if(stack == null) return null;

        Integer i = null;
        Item item = stack.getItem();

        if(item instanceof ItemArmor) {
            i = Util.getGlob(materials, ((ItemArmor)item).getArmorMaterial().getName());
        }
        if(i == null) {
            i = Util.getGlob(items, item.getRegistryName().getResourcePath());
        }
        return i;
    }

    public Integer getSpecialIndex(String key) {
        return special.get(key);
    }
}
