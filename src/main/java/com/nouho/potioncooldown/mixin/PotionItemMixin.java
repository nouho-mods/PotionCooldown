package com.nouho.potioncooldown.mixin;

import com.nouho.potioncooldown.PotionCooldownHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    // Check cooldown when the potion starts being used
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onPotionUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && PotionCooldownHandler.shouldCancelPotionUse(user, stack)) {
            cir.setReturnValue(TypedActionResult.fail(stack));
        }    }

    // Record the usage when the potion drinking is completed
    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = false)
    private void onPotionFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            PotionCooldownHandler.onPotionUsed(player, stack);
        }
    }
}
