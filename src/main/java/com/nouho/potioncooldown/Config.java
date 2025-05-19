package com.nouho.potioncooldown;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS = BUILDER
            .comment("Potion cooldown in seconds")
            .defineInRange("cooldownSeconds", 10, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int cooldownSeconds;

    public static void onLoad(final ModConfigEvent event)
    {
        cooldownSeconds = COOLDOWN_SECONDS.get();
    }
}