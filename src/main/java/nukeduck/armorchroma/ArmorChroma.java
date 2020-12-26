package nukeduck.armorchroma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import nukeduck.armorchroma.config.ArmorChromaConfig;
import nukeduck.armorchroma.config.IconData;

public class ArmorChroma implements ClientModInitializer {

    public static final String MODID = "armorchroma";
    public static final GuiArmor GUI = new GuiArmor();
    public static final Logger LOGGER = LogManager.getLogger();
    public static ArmorChromaConfig config;

    private static final IconData ICON_DATA = new IconData();
    public static IconData getIconData() { return ICON_DATA; }

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper manager = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
        manager.registerReloadListener(ICON_DATA);

        AutoConfig.register(ArmorChromaConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ArmorChromaConfig.class).getConfig();
    }

}
