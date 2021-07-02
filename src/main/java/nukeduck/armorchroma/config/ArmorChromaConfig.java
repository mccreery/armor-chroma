package nukeduck.armorchroma.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import nukeduck.armorchroma.ArmorChroma;

public class ArmorChromaConfig {

    public boolean isEnabled() { return true; }
    public boolean renderGlint() { return true; }
    public boolean renderBackground() { return true; }
    public boolean compressBar() { return false; }

    /** Config class requiring AutoConfig */
    @SuppressWarnings("FieldMayBeFinal")
    @Config(name = ArmorChroma.MODID)
    public static class ArmorChromaAutoConfig extends ArmorChromaConfig implements ConfigData {

        private boolean enabled = super.isEnabled();
        @Tooltip private boolean renderGlint = super.renderGlint();
        @Tooltip private boolean renderBackground = super.renderBackground();
        @Tooltip private boolean compressBar = super.compressBar();

        @Override public boolean isEnabled() { return enabled; }
        @Override public boolean renderGlint() { return renderGlint; }
        @Override public boolean renderBackground() { return renderBackground; }
        @Override public boolean compressBar() { return compressBar; }

    }

}
