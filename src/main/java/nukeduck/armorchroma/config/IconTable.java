package nukeduck.armorchroma.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
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

        if(item instanceof ArmorItem) {
            i = Util.getGlob(materials, ((ArmorItem)item).getMaterial().getName());
        }
        if(i == null) {
            i = Util.getGlob(items, Util.getModid(stack));
        }
        return i;
    }

    public Integer getSpecialIndex(String key) {
        return special.get(key);
    }
}
