package com.nouho.potioncooldown.mixin;

import com.nouho.potioncooldown.PotionCooldownHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LingeringPotionItem.class)
public class LingeringPotionItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onLingeringPotionUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && PotionCooldownHandler.shouldCancelPotionUse(user, stack)) {
            cir.setReturnValue(TypedActionResult.fail(stack));
            return;
        }
        
        // If not cancelled, record the usage
        if (!world.isClient) {
            PotionCooldownHandler.onPotionUsed(user, stack);
        }
    }
}
