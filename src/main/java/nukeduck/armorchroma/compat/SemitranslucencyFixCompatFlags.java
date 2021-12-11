package nukeduck.armorchroma.compat;

import nukeduck.armorchroma.mixin.compat.semitranslucencyfix.DrawableHelperMixin;
import nukeduck.armorchroma.mixin.compat.semitranslucencyfix.GuiArmorMixin;

/**
 * Contains flags used for Semitranslucency Fix compatibility
 * @see DrawableHelperMixin
 * @see GuiArmorMixin
 */
public class SemitranslucencyFixCompatFlags {

    private SemitranslucencyFixCompatFlags() {}

    public static boolean drawingMaskedIcon = false;
    public static boolean drawingGlint = false;

}
