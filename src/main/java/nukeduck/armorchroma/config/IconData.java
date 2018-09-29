package nukeduck.armorchroma.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import nukeduck.armorchroma.ArmorChroma;

public class IconData {
    private final Map<String, IconTable> mods = new HashMap<>();

    private static final ArmorIcon FALLBACK_ICON = new ArmorIcon(0);
    public static final String DEFAULT = "default";

    public static final String MINECRAFT = "minecraft";

    public void reload(IResourceManager manager) {
        mods.clear();

        for(String modid : Loader.instance().getIndexedModList().keySet()) {
            try {
                for(IResource resource : manager.getAllResources(new ResourceLocation(modid, "textures/gui/armor_chroma.json"))) {
                    try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                        IconTable mod = new Gson().fromJson(reader, IconTable.class);

                        mods.merge(modid, mod, (a, b) -> {
                            a.putAll(b);
                            return a;
                        });
                    }
                }
            } catch(IOException e) {
                ArmorChroma.getLogger().error("Unable to load icons for modid " + modid, e);
            }
        }
    }

    /** @return The armor icon corresponding to {@code stack} */
	public ArmorIcon getIcon(ItemStack stack) {
        String modid = Util.getModid(stack);
        IconTable mod = mods.get(modid);

        Integer i = null;

        if(mod != null) {
            i = mod.getIconIndex(stack);

            if(i != null) {
                return new ArmorIcon(modid, i, Util.getColor(stack));
            } else {
                return getSpecial(modid, DEFAULT);
            }
        }
        return getSpecial(MINECRAFT, DEFAULT);
	}

    public ArmorIcon getSpecial(String modid, String key) {
        IconTable mod = mods.get(modid);

        if(mod == null) {
            modid = MINECRAFT;
            mod = mods.get(modid);

            if(mod == null) return FALLBACK_ICON;
        }

        Integer i = mod.getSpecialIndex(key);
        return i != null ? new ArmorIcon(modid, i) : getSpecial(MINECRAFT, key);
    }
}
