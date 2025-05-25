package com.nouho.potioncooldown;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue NORMAL_COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown for normal potions (in seconds)")
            .defineInRange("normalPotionCD", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue THROWABLE_COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown for throwable potions (in seconds)")
            .defineInRange("throwablePotionCD", 10, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SYNC_POTION_COOLDOWNS = BUILDER
            .comment("If normal and throwable potion go on cooldown at the same time, or if they have separate cooldowns")
            .define("syncPotionCooldowns", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int normalCooldownSeconds;
    public static int throwableCooldownSeconds;
    public static boolean syncPotionCooldowns;

    public static void onLoad(final ModConfigEvent event)
    {
        // Only update values if the config is loaded, not unloading
        if (event instanceof net.neoforged.fml.event.config.ModConfigEvent.Loading) {
            normalCooldownSeconds = NORMAL_COOLDOWN_SECONDS.get();
            throwableCooldownSeconds = THROWABLE_COOLDOWN_SECONDS.get();
            syncPotionCooldowns = SYNC_POTION_COOLDOWNS.get();
        }
    }
}