package com.nouho.potioncooldown;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = PotionCooldown.MODID)
public class PotionCooldownHandler {
    private static final Map<UUID, Long> normalPotionCooldowns = new HashMap<>();
    private static final Map<UUID, Long> throwablePotionCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPotionFinishUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        boolean isNormal = stack.getItem() instanceof PotionItem && !(stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION);
        boolean isSplash = stack.getItem() == Items.SPLASH_POTION;
        boolean isLingering = stack.getItem() == Items.LINGERING_POTION;
        if (!isNormal && !isSplash && !isLingering) return;
        UUID playerId = player.getUUID();
        long now = System.currentTimeMillis();
        if (Config.syncPotionCooldowns) {
            // Always apply both cooldowns
            normalPotionCooldowns.put(playerId, now);
            throwablePotionCooldowns.put(playerId, now);
            int normalTicks = (int) (Config.normalCooldownSeconds * 1000L / 50);
            int throwableTicks = (int) (Config.throwableCooldownSeconds * 1000L / 50);
            player.getCooldowns().addCooldown(Items.POTION, normalTicks);
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, throwableTicks);
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, throwableTicks);
        } else if (isNormal) {
            long cooldownMillis = Config.normalCooldownSeconds * 1000L;
            int ticks = (int) (cooldownMillis / 50);
            Long lastUse = normalPotionCooldowns.get(playerId);
            if (lastUse != null && now - lastUse < cooldownMillis) {
                int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);
                player.getCooldowns().addCooldown(Items.POTION, ticksLeft);
                return;
            }
            normalPotionCooldowns.put(playerId, now);
            player.getCooldowns().addCooldown(Items.POTION, ticks);
        }
        // Splash/Lingering handled in onPotionThrow
    }

    @SubscribeEvent
    public static void onPotionThrow(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItemStack();
        boolean isSplash = stack.getItem() == Items.SPLASH_POTION;
        boolean isLingering = stack.getItem() == Items.LINGERING_POTION;
        if (!isSplash && !isLingering) return;
        UUID playerId = player.getUUID();
        long now = System.currentTimeMillis();
        if (Config.syncPotionCooldowns) {
            // Always apply both cooldowns
            normalPotionCooldowns.put(playerId, now);
            throwablePotionCooldowns.put(playerId, now);
            int normalTicks = (int) (Config.normalCooldownSeconds * 1000L / 50);
            int throwableTicks = (int) (Config.throwableCooldownSeconds * 1000L / 50);
            player.getCooldowns().addCooldown(Items.POTION, normalTicks);
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, throwableTicks);
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, throwableTicks);
        } else {
            long cooldownMillis = Config.throwableCooldownSeconds * 1000L;
            int ticks = (int) (cooldownMillis / 50);
            Long lastUse = throwablePotionCooldowns.get(playerId);
            if (lastUse != null && now - lastUse < cooldownMillis) {
                int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);
                // Always apply cooldown to both splash and lingering potions for visual feedback
                player.getCooldowns().addCooldown(Items.SPLASH_POTION, ticksLeft);
                player.getCooldowns().addCooldown(Items.LINGERING_POTION, ticksLeft);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            throwablePotionCooldowns.put(playerId, now);
            // Always apply cooldown to both splash and lingering potions for visual feedback
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, ticks);
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, ticks);
        }
    }
}