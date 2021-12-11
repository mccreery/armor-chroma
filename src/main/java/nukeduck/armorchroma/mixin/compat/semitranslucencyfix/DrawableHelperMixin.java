package nukeduck.armorchroma.mixin.compat.semitranslucencyfix;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import nukeduck.armorchroma.compat.SemitranslucencyFixCompatFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

/**
 * Adds compatibility for Semitranslucency Fix (https://modrinth.com/mod/semitranslucency)
 * by undoing what it does when needed
 * (https://github.com/ruvaldak/Semitranslucency/blob/a70656c2e1b504417abc75e00a6a8797ed21471e/src/main/java/net/ims/semitranslucency/mixin/MixinDrawableHelper.java#L16)
 * @see SemitranslucencyFixCompatFlags
 */
@Mixin(DrawableHelper.class)
public abstract class DrawableHelperMixin {

    @Inject(method = "drawTexturedQuad", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private static void drawTexturedQuad(CallbackInfo ci) {
        if (SemitranslucencyFixCompatFlags.drawingMaskedIcon) {
            RenderSystem.blendFunc(GL_DST_COLOR, GL_ZERO);
        } else if (SemitranslucencyFixCompatFlags.drawingGlint) {
            RenderSystem.blendFuncSeparate(GL_SRC_COLOR, GL_ONE, GL_ONE, GL_ZERO);
        }
    }

}
