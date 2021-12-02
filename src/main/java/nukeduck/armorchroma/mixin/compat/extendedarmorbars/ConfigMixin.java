package nukeduck.armorchroma.mixin.compat.extendedarmorbars;

import com.rebelkeithy.extendedarmorbars.config.Config;
import nukeduck.armorchroma.ArmorChroma;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Config.class, remap = false)
public abstract class ConfigMixin {

    @Inject(method = "isArmorEnable", at = @At("HEAD"), cancellable = true, require = 0)
    private void onIsArmorEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (ArmorChroma.config.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

}
