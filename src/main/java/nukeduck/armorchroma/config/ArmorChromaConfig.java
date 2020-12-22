package nukeduck.armorchroma.config;

// @Config(modid = ArmorChroma.MODID)
// @EventBusSubscriber
public class ArmorChromaConfig {
	// @Comment("Display enchanted glint")
	// @LangKey("armorchroma.config.renderGlint")
	public static boolean renderGlint = true;

	// @Comment("Compress the bar into different colored borders when your armor exceeds 20")
	// @LangKey("armorchroma.config.compressBar")
	public static boolean compressBar;

	// @SubscribeEvent
	// public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
	// 	if(event.getModID().equals(ArmorChroma.MODID)) {
	// 		ConfigManager.sync(ArmorChroma.MODID, Config.Type.INSTANCE);
	// 	}
	// }
}
