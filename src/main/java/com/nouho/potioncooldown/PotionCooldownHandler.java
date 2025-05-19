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
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private static void applyPotionCooldown(ServerPlayer player, int ticks) {
        player.getCooldowns().addCooldown(Items.POTION, ticks);
        player.getCooldowns().addCooldown(Items.SPLASH_POTION, ticks);
        player.getCooldowns().addCooldown(Items.LINGERING_POTION, ticks);
    }

    @SubscribeEvent
    public static void onPotionFinishUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        if (stack.getItem() instanceof PotionItem || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
            UUID playerId = player.getUUID();
            long now = System.currentTimeMillis();
            Long lastUse = cooldowns.get(playerId);
            long cooldownMillis = Config.cooldownSeconds * 1000L;
            int ticks = (int) (cooldownMillis / 50);
            if (lastUse != null && now - lastUse < cooldownMillis) {
                int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);
                applyPotionCooldown(player, ticksLeft);
                return;
            }
            cooldowns.put(playerId, now);
            applyPotionCooldown(player, ticks);
        }
    }

    @SubscribeEvent
    public static void onPotionThrow(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
            UUID playerId = player.getUUID();
            long now = System.currentTimeMillis();
            Long lastUse = cooldowns.get(playerId);
            long cooldownMillis = Config.cooldownSeconds * 1000L;
            int ticks = (int) (cooldownMillis / 50);
            if (lastUse != null && now - lastUse < cooldownMillis) {
                int ticksLeft = (int) ((cooldownMillis - (now - lastUse)) / 50);
                applyPotionCooldown(player, ticksLeft);
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            cooldowns.put(playerId, now);
            applyPotionCooldown(player, ticks);
        }
    }
}