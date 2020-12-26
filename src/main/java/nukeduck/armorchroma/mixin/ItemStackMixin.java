package nukeduck.armorchroma.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /** Adds the item material to the tooltip
     * @see ItemStack#getTooltip */
    @Inject(method = "getTooltip", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetTooltip(
        @Nullable PlayerEntity player, TooltipContext context,
        CallbackInfoReturnable<List<Text>> info, List<Text> list
    ) {
        if (context.isAdvanced()) {
            final Item item = getItem();
            if (item instanceof ArmorItem) {
                final String material = ((ArmorItem) item).getMaterial().getName();
                list.add(
                    new TranslatableText("armorchroma.tooltip.material", material)
                    .formatted(Formatting.DARK_GRAY)
                );
            }
        }
    }

    @Shadow public Item getItem() {
        return null;
    }

}
