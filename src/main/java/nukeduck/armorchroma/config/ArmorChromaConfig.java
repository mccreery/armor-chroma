package nukeduck.armorchroma.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nukeduck.armorchroma.ArmorChroma;

@Config(modid = ArmorChroma.MODID)
@EventBusSubscriber
public class ArmorChromaConfig {
	@Comment("Display enchanted glint")
	public static boolean renderGlint = true;

	@Comment("Compress the bar into different colored borders when your armor exceeds 20")
	public static boolean compressBar;

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(ArmorChroma.MODID)) {
			ConfigManager.sync(ArmorChroma.MODID, Config.Type.INSTANCE);
		}
	}
}
