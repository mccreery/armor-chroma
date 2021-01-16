package nukeduck.armorchroma.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import nukeduck.armorchroma.config.ArmorChromaConfig.ArmorChromaAutoConfig;

public class ModMenuIntegration implements ModMenuApi {

    private static final ConfigScreenFactory<?> FACTORY;

    static {
        final FabricLoader loader = FabricLoader.getInstance();
        FACTORY = loader.isModLoaded("autoconfig1u") && loader.isModLoaded("cloth-config2")
                ? parent -> AutoConfig.getConfigScreen(ArmorChromaAutoConfig.class, parent).get()
                : parent -> null;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FACTORY;
    }

}
