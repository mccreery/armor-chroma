package nukeduck.armorchroma.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
import nukeduck.armorchroma.ArmorChroma;

@Config(name = ArmorChroma.MODID)
public class ArmorChromaConfig implements ConfigData {

	public boolean enabled = true;

	@Tooltip
	public boolean renderGlint = true;

	@Tooltip
	public boolean compressBar = false;
}
