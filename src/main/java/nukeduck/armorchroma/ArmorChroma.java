package nukeduck.armorchroma;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import nukeduck.armorchroma.config.ArmorChromaConfig;
import nukeduck.armorchroma.config.ArmorChromaConfig.ArmorChromaAutoConfig;
import nukeduck.armorchroma.config.IconData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArmorChroma implements ClientModInitializer {

    public static final String MODID = "armorchroma";
    public static final GuiArmor GUI = new GuiArmor();
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean USE_AUTO_CONFIG = FabricLoader.getInstance().isModLoaded("cloth-config2");
    public static ArmorChromaConfig config;

    public static final IconData ICON_DATA = new IconData();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper manager = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
        manager.registerReloadListener(ICON_DATA);

        if (USE_AUTO_CONFIG) {
            AutoConfig.register(ArmorChromaAutoConfig.class, GsonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ArmorChromaAutoConfig.class).getConfig();
        } else {
            config = new ArmorChromaConfig();
        }
    }

}
