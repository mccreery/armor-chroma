package nukeduck.armorchroma.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import nukeduck.armorchroma.ArmorChroma;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique private static final String DRAW_TEXTURE_DESCRIPTOR = "net/minecraft/client/gui/hud/InGameHud.drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V";
    @Shadow private @Final MinecraftClient client;

    @Unique private int top;



    /**
     * Empêche le jeu d'afficher l'armure en faisant comme si le
     * joueur en avait pas
     * @see InGameHud#renderStatusBars
     */
    // @Redirect(method = "renderStatusBars",
    //     at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.getArmor()I"))
    // private int getArmorProxy(PlayerEntity player) {
    //     return 0;
    // }

    /**
     * Annule l'affichage vanilla de l'armure et stocke la position des icônes
     * @see InGameHud#renderStatusBars
     */
    @Redirect(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = DRAW_TEXTURE_DESCRIPTOR, ordinal = 0))
    private void drawTextureProxy0(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int w, int h) {
        top = y;
    }

    /** @see #drawTextureProxy0 */
    @Redirect(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = DRAW_TEXTURE_DESCRIPTOR, ordinal = 1))
    private void drawTextureProxy1(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int w, int h) {
        drawTextureProxy0(hud, matrices, x, y, u, v, w, h);
    }

    /** @see #drawTextureProxy0 */
    @Redirect(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = DRAW_TEXTURE_DESCRIPTOR, ordinal = 2))
    private void drawTextureProxy2(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int w, int h) {
        drawTextureProxy0(hud, matrices, x, y, u, v, w, h);
    }

    /** Affiche les trucs d'armure en couleur et tout */
    @Inject(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V", ordinal = 0))
    private void renderArmor(MatrixStack matrices, CallbackInfo info) {
        ArmorChroma.GUI.draw(
            matrices,
            client.getWindow().getScaledWidth(),
            top
        );
    }

}
