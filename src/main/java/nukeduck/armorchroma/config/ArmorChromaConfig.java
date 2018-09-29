package nukeduck.armorchroma.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import nukeduck.armorchroma.ArmorChroma;

@Config(modid = ArmorChroma.MODID)
public class ArmorChromaConfig {
	@Comment("Display enchanted glint")
	public boolean renderGlint;

	@Comment("Compress the bar into different colored borders when your armor exceeds 20")
	public boolean compressBar;
}
