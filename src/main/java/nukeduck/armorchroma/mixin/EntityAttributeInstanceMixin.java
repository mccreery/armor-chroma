package nukeduck.armorchroma.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import nukeduck.armorchroma.UnclampedEntityAttribute;

@Mixin(EntityAttributeInstance.class)
public abstract class EntityAttributeInstanceMixin implements UnclampedEntityAttribute {

    @Unique private double unclampedValue;



    @Inject(method = "computeValue",
        at = @At(value = "INVOKE", target = "net/minecraft/entity/attribute/EntityAttribute.clamp(D)D"),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void onComputeValue(CallbackInfoReturnable<Double> info, double d, double value) {
        unclampedValue = value;
    }

    @Override
    public double getUnclampedValue() {
        getValue(); // Recalcule la valeur si nécessaire
        return unclampedValue;
    }



    @Shadow
    public double getValue() {
        return 0;
    }

}
