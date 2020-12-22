package nukeduck.armorchroma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import nukeduck.armorchroma.config.IconData;

public class ArmorChroma implements ClientModInitializer {
	public static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

	public static final String MODID = "armorchroma";
	public static final GuiArmor GUI = new GuiArmor();
	public static final Logger LOGGER = LogManager.getLogger();

	private static final IconData ICON_DATA = new IconData();
	public static IconData getIconData() { return ICON_DATA; }

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper manager = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
		manager.registerReloadListener(ICON_DATA);
	}
}
