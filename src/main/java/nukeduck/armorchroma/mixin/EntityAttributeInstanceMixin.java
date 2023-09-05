package nukeduck.armorchroma.mixin;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import nukeduck.armorchroma.EntityAttributeInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Exposes the unclamped value of the attribute */
@Mixin(EntityAttributeInstance.class)
public abstract class EntityAttributeInstanceMixin implements EntityAttributeInstanceAccess {

    @Shadow public abstract double getValue();

    @Unique private double unclampedValue;



    /** Stores the unclamped value */
    @Inject(method = "computeValue",
        at = @At(value = "INVOKE", target = "net/minecraft/entity/attribute/EntityAttribute.clamp(D)D"),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void onComputeValue(CallbackInfoReturnable<Double> info, double d, double value) {
        unclampedValue = value;
    }

    @Override
    public double getUnclampedValue() {
        getValue(); // Compute the unclamped value again if needed
        return unclampedValue;
    }

}
