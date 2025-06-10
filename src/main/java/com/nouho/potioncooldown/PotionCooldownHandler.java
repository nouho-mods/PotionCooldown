package com.nouho.potioncooldown;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PotionCooldownHandler {
    private static final Map<UUID, Long> normalPotionCooldowns = new HashMap<>();
    private static final Map<UUID, Long> throwablePotionCooldowns = new HashMap<>();

    public static boolean shouldCancelPotionUse(PlayerEntity player, ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity)) return false;
          boolean isNormal = stack.getItem() instanceof PotionItem && !(stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION);
        boolean isSplash = stack.getItem() == Items.SPLASH_POTION;
        boolean isLingering = stack.getItem() == Items.LINGERING_POTION;
        
        if (!isNormal && !isSplash && !isLingering) return false;
        
        UUID playerId = player.getUuid();
        long now = System.currentTimeMillis();        if (Config.syncPotionCooldowns) {
            // When sync is enabled, both timers start together but check only the specific type
            if (isNormal) {
                Long lastNormalUse = normalPotionCooldowns.get(playerId);
                long normalCooldownMillis = Config.normalCooldownSeconds * 1000L;
                if (lastNormalUse != null && now - lastNormalUse < normalCooldownMillis) {
                    long cooldownLeft = normalCooldownMillis - (now - lastNormalUse);
                    int ticksLeft = (int) (cooldownLeft / 50);
                    player.getItemCooldownManager().set(Items.POTION, ticksLeft);
                    return true;
                }
            } else if (isSplash || isLingering) {
                Long lastThrowableUse = throwablePotionCooldowns.get(playerId);
                long throwableCooldownMillis = Config.throwableCooldownSeconds * 1000L;
                if (lastThrowableUse != null && now - lastThrowableUse < throwableCooldownMillis) {
                    long cooldownLeft = throwableCooldownMillis - (now - lastThrowableUse);
                    int ticksLeft = (int) (cooldownLeft / 50);
                    player.getItemCooldownManager().set(Items.SPLASH_POTION, ticksLeft);
                    player.getItemCooldownManager().set(Items.LINGERING_POTION, ticksLeft);
                    return true;
                }
            }
        } else {
            if (isNormal) {
                long cooldownMillis = Config.normalCooldownSeconds * 1000L;
                Long lastUse = normalPotionCooldowns.get(playerId);
                if (lastUse != null && now - lastUse < cooldownMillis) {
                    int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);
                    player.getItemCooldownManager().set(Items.POTION, ticksLeft);
                    return true;
                }
            } else if (isSplash || isLingering) {
                long cooldownMillis = Config.throwableCooldownSeconds * 1000L;
                Long lastUse = throwablePotionCooldowns.get(playerId);
                if (lastUse != null && now - lastUse < cooldownMillis) {
                    int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);                    player.getItemCooldownManager().set(Items.SPLASH_POTION, ticksLeft);
                    player.getItemCooldownManager().set(Items.LINGERING_POTION, ticksLeft);
                    return true;
                }
            }
        }
        
        return false;
    }

    public static void onPotionUsed(PlayerEntity player, ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity)) return;
        
        boolean isNormal = stack.getItem() instanceof PotionItem && !(stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION);
        boolean isSplash = stack.getItem() == Items.SPLASH_POTION;
        boolean isLingering = stack.getItem() == Items.LINGERING_POTION;
          if (!isNormal && !isSplash && !isLingering) return;
        
        UUID playerId = player.getUuid();
        long now = System.currentTimeMillis();
        
        if (Config.syncPotionCooldowns) {
            // Always apply both cooldowns
            normalPotionCooldowns.put(playerId, now);
            throwablePotionCooldowns.put(playerId, now);
            int normalTicks = Config.normalCooldownSeconds * 20; // 20 ticks per second
            int throwableTicks = Config.throwableCooldownSeconds * 20;
            player.getItemCooldownManager().set(Items.POTION, normalTicks);
            player.getItemCooldownManager().set(Items.SPLASH_POTION, throwableTicks);
            player.getItemCooldownManager().set(Items.LINGERING_POTION, throwableTicks);
        } else if (isNormal) {
            normalPotionCooldowns.put(playerId, now);
            int ticks = Config.normalCooldownSeconds * 20;
            player.getItemCooldownManager().set(Items.POTION, ticks);
        } else if (isSplash || isLingering) {
            throwablePotionCooldowns.put(playerId, now);
            int ticks = Config.throwableCooldownSeconds * 20;
            player.getItemCooldownManager().set(Items.SPLASH_POTION, ticks);
            player.getItemCooldownManager().set(Items.LINGERING_POTION, ticks);
        }
    }
}