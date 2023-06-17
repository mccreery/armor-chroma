package nukeduck.armorchroma.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import nukeduck.armorchroma.ArmorChroma;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Replaces the vanilla armor rendering with the mod's */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    /* Removes the vanilla armor bar */
    @ModifyVariable(method = "renderStatusBars",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I", shift = At.Shift.BY, by = 2),
            ordinal = 11)
    private int modifyArmor(int armor) {
        return ArmorChroma.config.isEnabled() ? 0 : armor;
    }

    /** Renders the modded armor bar */
    @Inject(method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderArmor(DrawContext context, CallbackInfo info, PlayerEntity player, int i, boolean bl, long l, int j, HungerManager hungerManager, int k, int left, int n, int o, float f, int p, int q, int r, int top) {
        if (ArmorChroma.config.isEnabled()) {
            ArmorChroma.GUI.draw(context, left, top);
        }
    }

}
