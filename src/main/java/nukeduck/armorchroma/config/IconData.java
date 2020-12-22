package nukeduck.armorchroma.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nukeduck.armorchroma.ArmorChroma;

public class IconData implements SimpleResourceReloadListener<Void> {
    private final Map<String, IconTable> mods = new HashMap<>();

    private static final ArmorIcon FALLBACK_ICON = new ArmorIcon(0);
    private static final String DEFAULT = "default";
    private static final String MINECRAFT = "minecraft";
    private static final Identifier ID = new Identifier(ArmorChroma.MODID, "icondata");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    /** @return The armor icon corresponding to {@code stack} */
    public ArmorIcon getIcon(ItemStack stack) {
        String modid = Util.getModid(stack);
        IconTable mod = mods.get(modid);

        Integer i = null;

        if (mod != null) {
            i = mod.getIconIndex(stack);

            if (i != null) {
                return new ArmorIcon(modid, i, Util.getColor(stack));
            } else {
                return getSpecial(modid, DEFAULT);
            }
        }
        return getSpecial(MINECRAFT, DEFAULT);
    }

    public ArmorIcon getSpecial(String modid, String key) {
        IconTable mod = mods.get(modid);

        if (mod == null) {
            modid = MINECRAFT;
            mod = mods.get(modid);

            if (mod == null)
                return FALLBACK_ICON;
        }

        Integer i = mod.getSpecialIndex(key);
        return i != null ? new ArmorIcon(modid, i) : getSpecial(MINECRAFT, key);
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            mods.clear();

            for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
                String modid = modContainer.getMetadata().getId();
                try {
                    for (Resource resource : manager.getAllResources(new Identifier(modid, "textures/gui/armor_chroma.json"))) {
                        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                            IconTable mod = new Gson().fromJson(reader, IconTable.class);

                            mods.merge(modid, mod, (a, b) -> {
                                a.putAll(b);
                                return a;
                            });
                        } catch (JsonSyntaxException | JsonIOException | IOException e) {
                            // If an error is caught here, continue to read the other files
                            ArmorChroma.LOGGER.error("Loading modid {}", modid, e);
                        }
                    }
                } catch (IOException e) {
                    // Mod is either not present (FileNotFoundException) or failed
                    if (MINECRAFT.equals(modid)) {
                        // As below, but with extra information
                        throw new RuntimeException("Missing fallback icons. The mod is damaged", e);
                    } else if (!(e instanceof FileNotFoundException)) {
                        ArmorChroma.LOGGER.error("Loading modid {}", modid, e);
                    }
                }
            }

            IconTable minecraft = mods.get(MINECRAFT);
            if (minecraft == null || minecraft.getSpecialIndex(DEFAULT) == null
                    || minecraft.getSpecialIndex("leadingMask") == null
                    || minecraft.getSpecialIndex("trailingMask") == null) {
                // This should never happen unless the mod has been edited
                throw new RuntimeException("Missing fallback icons. The mod is damaged");
            }

            // Ignore modid minecraft
            ArmorChroma.LOGGER.info("Loaded {} mods", mods.size() - 1);

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

}
