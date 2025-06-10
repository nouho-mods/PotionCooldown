package com.nouho.potioncooldown;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("potion-cooldown.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static int normalCooldownSeconds = 10;
    public static int throwableCooldownSeconds = 10;
    public static boolean syncPotionCooldowns = true;
    
    public static void loadConfig() {
        if (Files.exists(CONFIG_FILE)) {
            try (FileReader reader = new FileReader(CONFIG_FILE.toFile())) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    normalCooldownSeconds = data.normalCooldownSeconds;
                    throwableCooldownSeconds = data.throwableCooldownSeconds;
                    syncPotionCooldowns = data.syncPotionCooldowns;
                }
            } catch (Exception e) {
                PotionCooldown.LOGGER.error("Failed to load config", e);
            }
        } else {
            saveConfig(); // Create default config
        }
    }
    
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE.toFile())) {
            ConfigData data = new ConfigData();
            data.normalCooldownSeconds = normalCooldownSeconds;
            data.throwableCooldownSeconds = throwableCooldownSeconds;
            data.syncPotionCooldowns = syncPotionCooldowns;
            
            GSON.toJson(data, writer);
        } catch (Exception e) {
            PotionCooldown.LOGGER.error("Failed to save config", e);
        }
    }
    
    private static class ConfigData {
        public int normalCooldownSeconds = 10;
        public int throwableCooldownSeconds = 10;
        public boolean syncPotionCooldowns = true;
    }
}