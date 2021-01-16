package nukeduck.armorchroma.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import nukeduck.armorchroma.ArmorChroma;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replaces the vanilla armor rendering with the mod's */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique private static final String PROFILER_SWAP_DESCRIPTOR = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V";
    @Unique private int top = -32; // Renders outside of the screen before initialized

    @Shadow @Final private MinecraftClient client;



    /** Cancels the vanilla armor rendering and stores the bar location */
    @Redirect(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = "net/minecraft/client/gui/hud/InGameHud.drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"),
        slice = @Slice(to = @At(value = "INVOKE", target = PROFILER_SWAP_DESCRIPTOR))
    )
    private void drawTextureProxy(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (ArmorChroma.config.enabled) {
            top = y;
        } else {
            // Comportement vanilla
            hud.drawTexture(matrices, x, y, u, v, width, height);
        }
    }

    /** Renders the modded aarmor bar */
    @Inject(method = "renderStatusBars",
        at = @At(value = "INVOKE", target = PROFILER_SWAP_DESCRIPTOR, ordinal = 0))
    private void renderArmor(MatrixStack matrices, CallbackInfo info) {
        if (ArmorChroma.config.enabled) {
            ArmorChroma.GUI.draw(
                matrices,
                client.getWindow().getScaledWidth(),
                top
            );
        }
    }

}
