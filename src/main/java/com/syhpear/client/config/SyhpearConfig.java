package com.syhpear.client.config;

import com.google.gson.*;
import com.syhpear.client.util.Logger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Syhpear Client — Config Manager
 *
 * Features:
 * - JSON-based persistence
 * - Multiple profiles
 * - Import / Export
 * - Auto-save
 * - Backup on corruption
 */
public class SyhpearConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configDir;
    private final Path configFile;
    private final Path backupFile;

    private JsonObject root      = new JsonObject();
    private String currentProfile = "default";

    // Module states cache
    private final Map<String, Boolean>    moduleStates  = new HashMap<>();
    // Module settings cache
    private final Map<String, JsonObject> moduleSettings = new HashMap<>();

    public SyhpearConfig() {
        configDir   = FabricLoader.getInstance().getConfigDir().resolve("syhpear");
        configFile  = configDir.resolve("config.json");
        backupFile  = configDir.resolve("config.backup.json");
    }

    // ── Load ──────────────────────────────────────────────────────

    public void load() {
        try {
            if (!Files.exists(configDir)) Files.createDirectories(configDir);

            if (!Files.exists(configFile)) {
                Logger.info("[Config] No config found — using defaults");
                createDefaults();
                save();
                return;
            }

            String json = Files.readString(configFile);
            root = GSON.fromJson(json, JsonObject.class);
            if (root == null) throw new JsonParseException("Root is null");

            currentProfile = root.has("currentProfile")
                ? root.get("currentProfile").getAsString()
                : "default";

            parseModuleStates();
            Logger.info("[Config] Loaded successfully — Profile: " + currentProfile);

        } catch (Exception e) {
            Logger.warn("[Config] Failed to load config: " + e.getMessage());
            attemptRestore();
        }
    }

    private void parseModuleStates() {
        if (!root.has("modules")) return;
        JsonObject mods = root.getAsJsonObject("modules");
        for (var entry : mods.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                JsonObject mod = entry.getValue().getAsJsonObject();
                moduleStates.put(entry.getKey(),
                    mod.has("enabled") && mod.get("enabled").getAsBoolean());
                moduleSettings.put(entry.getKey(), mod);
            }
        }
    }

    // ── Save ──────────────────────────────────────────────────────

    public void save() {
        try {
            if (!Files.exists(configDir)) Files.createDirectories(configDir);

            // Backup before overwrite
            if (Files.exists(configFile)) {
                Files.copy(configFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            }

            root.addProperty("currentProfile", currentProfile);
            root.addProperty("version", "1.0.0");
            root.addProperty("lastSaved", System.currentTimeMillis());

            Files.writeString(configFile, GSON.toJson(root));
        } catch (IOException e) {
            Logger.error("[Config] Save failed: " + e.getMessage());
        }
    }

    // ── Module state ──────────────────────────────────────────────

    public boolean getModuleState(String moduleName) {
        return moduleStates.getOrDefault(moduleName.toLowerCase(), false);
    }

    public void setModuleState(String moduleName, boolean state) {
        moduleStates.put(moduleName.toLowerCase(), state);
        getOrCreateModuleObject(moduleName).addProperty("enabled", state);
        save();
    }

    // ── Module settings ───────────────────────────────────────────

    public JsonObject getModuleSettings(String moduleName) {
        return moduleSettings.getOrDefault(moduleName.toLowerCase(), new JsonObject());
    }

    public void setModuleSetting(String moduleName, String key, Object value) {
        JsonObject obj = getOrCreateModuleObject(moduleName);
        if (value instanceof Boolean b) obj.addProperty(key, b);
        else if (value instanceof Number n) obj.addProperty(key, n);
        else if (value instanceof String s) obj.addProperty(key, s);
        save();
    }

    private JsonObject getOrCreateModuleObject(String moduleName) {
        if (!root.has("modules")) root.add("modules", new JsonObject());
        JsonObject mods = root.getAsJsonObject("modules");
        String key = moduleName.toLowerCase();
        if (!mods.has(key)) mods.add(key, new JsonObject());
        JsonObject obj = mods.getAsJsonObject(key);
        moduleSettings.put(key, obj);
        return obj;
    }

    // ── Profiles ──────────────────────────────────────────────────

    public void switchProfile(String profile) {
        save(); // Save current before switching
        currentProfile = profile;
        load();
    }

    public List<String> getProfiles() {
        List<String> profiles = new ArrayList<>();
        try {
            Files.list(configDir)
                .filter(p -> p.toString().endsWith(".json"))
                .filter(p -> !p.equals(configFile) && !p.equals(backupFile))
                .forEach(p -> profiles.add(p.getFileName().toString().replace(".json", "")));
        } catch (IOException e) { /* ignore */ }
        profiles.add(0, "default");
        return profiles;
    }

    // ── Import / Export ───────────────────────────────────────────

    public boolean exportTo(Path destination) {
        try {
            Files.copy(configFile, destination, StandardCopyOption.REPLACE_EXISTING);
            Logger.info("[Config] Exported to: " + destination);
            return true;
        } catch (IOException e) {
            Logger.error("[Config] Export failed: " + e.getMessage());
            return false;
        }
    }

    public boolean importFrom(Path source) {
        try {
            String json = Files.readString(source);
            GSON.fromJson(json, JsonObject.class); // Validate
            Files.copy(source, configFile, StandardCopyOption.REPLACE_EXISTING);
            load();
            Logger.info("[Config] Imported from: " + source);
            return true;
        } catch (Exception e) {
            Logger.error("[Config] Import failed: " + e.getMessage());
            return false;
        }
    }

    // ── Backup restore ────────────────────────────────────────────

    private void attemptRestore() {
        try {
            if (Files.exists(backupFile)) {
                Files.copy(backupFile, configFile, StandardCopyOption.REPLACE_EXISTING);
                Logger.warn("[Config] Restored from backup");
                load();
            } else {
                Logger.warn("[Config] No backup found — using defaults");
                createDefaults();
            }
        } catch (Exception e) {
            Logger.error("[Config] Restore failed: " + e.getMessage());
            createDefaults();
        }
    }

    private void createDefaults() {
        root = new JsonObject();
        root.addProperty("currentProfile", "default");
        root.addProperty("version", "1.0.0");

        // Default enabled modules
        for (String defaultOn : new String[]{"FPS Boost", "Entity Culling", "FPS Counter", "Coordinates", "Crosshair"}) {
            getOrCreateModuleObject(defaultOn).addProperty("enabled", true);
        }
    }

    public String getCurrentProfile() { return currentProfile; }
}
