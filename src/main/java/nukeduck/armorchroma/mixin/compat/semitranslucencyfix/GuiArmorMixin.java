package nukeduck.armorchroma.mixin.compat.semitranslucencyfix;

import nukeduck.armorchroma.GuiArmor;
import nukeduck.armorchroma.compat.SemitranslucencyFixCompatFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sets the flags required for {@link DrawableHelperMixin} to know when to
 * revert Semitranslucency Fix's changes
 * @see SemitranslucencyFixCompatFlags
 */
@SuppressWarnings("UnresolvedMixinReference") // MCDev plugin not supporting mixins targeting the mod they are from?
@Mixin(GuiArmor.class)
public abstract class GuiArmorMixin {

    @Inject(method = "drawMaskedIcon",
            at = @At(value = "INVOKE", target = "Lnukeduck/armorchroma/config/ArmorIcon;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/DrawableHelper;II)V", ordinal = 1))
    private void beforeDrawMaskedIcon(CallbackInfo ci) {
        SemitranslucencyFixCompatFlags.drawingMaskedIcon = true;
    }

    @Inject(method = "drawMaskedIcon", at = @At("RETURN"))
    private void afterDrawMaskedIcon(CallbackInfo ci) {
        SemitranslucencyFixCompatFlags.drawingMaskedIcon = false;
    }

    @Inject(method = "drawTexturedGlintRect", at = @At("HEAD"))
    private void beforeDrawTexturedGlintRect(CallbackInfo ci) {
        SemitranslucencyFixCompatFlags.drawingGlint = true;
    }

    @Inject(method = "drawTexturedGlintRect", at = @At("RETURN"))
    private void afterDrawTexturedGlintRect(CallbackInfo ci) {
        SemitranslucencyFixCompatFlags.drawingGlint = false;
    }

}
