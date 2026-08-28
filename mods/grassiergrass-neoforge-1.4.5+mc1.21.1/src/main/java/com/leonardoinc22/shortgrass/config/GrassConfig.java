/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Block
 */
package com.leonardoinc22.shortgrass.config;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

public final class GrassConfig {
    public static volatile int bladesPerBlock = 64;
    private static volatile float grassSparsity = 0.1f;
    public static volatile float bladeHeight = 0.3f;
    public static volatile float heightVariation = 1.35f;
    public static volatile float bladeWidth = 0.95f;
    public static volatile int renderRadius = 160;
    public static final int MAX_RENDER_RADIUS = 160;
    public static volatile GrassStyle grassStyle = GrassStyle.TAPERED;
    public static volatile boolean grassPlantsAsBlades = true;
    public static volatile boolean denseFlowers = true;
    public static volatile boolean bladeParticles = true;
    public static volatile boolean grassThroughSnow = false;
    public static volatile boolean shaderPackShadows = false;
    public static volatile float grassBrightness = 1.0f;
    public static final float MAX_BLADE_HUE_JITTER_DEGREES = 30.0f;
    private static volatile float bladeHueJitterDegrees = 8.0f;
    public static volatile float bladeGradientBottom = 0.8f;
    public static volatile float bladeGradientTop = 1.0f;
    public static volatile float bladeGradientCurve = 1.9f;
    public static final int MAX_WIND_SPEED = 500;
    public static final int DYNAMIC_WIND_MIN_SPEED = 70;
    public static final int DYNAMIC_WIND_MAX_SPEED = 110;
    private static final float DYNAMIC_WIND_DIRECTION_DRIFT_DEGREES = 80.0f;
    private static final float DYNAMIC_WIND_DIRECTION_DRIFT_SPEED = 0.0025f;
    private static final float DYNAMIC_WIND_INTENSITY_DRIFT_SPEED = 0.0035f;
    public static volatile int windSpeed = 100;
    public static volatile float windDirectionDegrees = 0.0f;
    public static volatile boolean dynamicWind = true;
    private static volatile float dynamicWindSpeedLimit = 0.8f;
    private static volatile float effectiveWindSpeed = windSpeed;
    private static volatile int manualWindSpeed = windSpeed;
    private static volatile float dynamicWindBaseDirectionDegrees = windDirectionDegrees;
    private static final List<String> DEFAULT_PLANT_BLACKLIST = List.of("minecraft:seagrass", "minecraft:tall_seagrass", "minecraft:sea_pickle");
    private static volatile Set<ResourceLocation> plantBlacklist = GrassConfig.parseIds(DEFAULT_PLANT_BLACKLIST);
    private static final List<String> DEFAULT_PLANT_WHITELIST = List.of();
    private static volatile Set<ResourceLocation> plantWhitelist = GrassConfig.parseIds(DEFAULT_PLANT_WHITELIST);
    private static volatile Runnable persistHook = () -> {};

    public static void setPersistHook(Runnable hook) {
        persistHook = hook;
    }

    public static void setGrassSparsity(float sparsity) {
        grassSparsity = Mth.clamp((float)sparsity, (float)0.0f, (float)1.0f);
    }

    public static float grassSparsity() {
        return grassSparsity;
    }

    public static void setBladeHueJitterDegrees(float degrees) {
        bladeHueJitterDegrees = Mth.clamp((float)degrees, (float)0.0f, (float)30.0f);
    }

    public static float bladeHueJitterDegrees() {
        return bladeHueJitterDegrees;
    }

    public static void setWindSpeed(int speed) {
        windSpeed = manualWindSpeed = Math.max(0, Math.min(500, speed));
        effectiveWindSpeed = manualWindSpeed;
    }

    public static void setWindDirectionDegrees(float degrees) {
        windDirectionDegrees = dynamicWindBaseDirectionDegrees = GrassConfig.normalizeDegrees(degrees);
    }

    public static void setDynamicWind(boolean enabled) {
        dynamicWind = enabled;
        if (!enabled) {
            windSpeed = manualWindSpeed;
            effectiveWindSpeed = manualWindSpeed;
            windDirectionDegrees = dynamicWindBaseDirectionDegrees;
        }
    }

    public static void setDynamicWindSpeedLimit(float limit) {
        dynamicWindSpeedLimit = Mth.clamp((float)limit, (float)0.0f, (float)1.0f);
    }

    public static float dynamicWindSpeedLimit() {
        return dynamicWindSpeedLimit;
    }

    public static int configuredWindSpeed() {
        return manualWindSpeed;
    }

    public static float configuredWindDirectionDegrees() {
        return dynamicWindBaseDirectionDegrees;
    }

    public static int clampRenderRadius(int radius) {
        return Math.max(16, Math.min(160, radius));
    }

    public static List<String> defaultPlantBlacklistIds() {
        return DEFAULT_PLANT_BLACKLIST;
    }

    public static List<String> plantBlacklistIds() {
        return plantBlacklist.stream().map(ResourceLocation::toString).sorted().toList();
    }

    public static void setPlantBlacklistIds(Collection<? extends String> ids) {
        plantBlacklist = GrassConfig.parseIds(ids);
    }

    public static boolean addPlantBlacklist(ResourceLocation id) {
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<ResourceLocation>(plantBlacklist);
        boolean changed = updated.add(id);
        if (changed) {
            plantBlacklist = Set.copyOf(updated);
        }
        return changed;
    }

    public static boolean removePlantBlacklist(ResourceLocation id) {
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<ResourceLocation>(plantBlacklist);
        boolean changed = updated.remove(id);
        if (changed) {
            plantBlacklist = Set.copyOf(updated);
        }
        return changed;
    }

    public static boolean clearPlantBlacklist() {
        boolean changed = !plantBlacklist.isEmpty();
        plantBlacklist = Set.of();
        return changed;
    }

    public static boolean resetPlantBlacklist() {
        Set<ResourceLocation> defaults = GrassConfig.parseIds(DEFAULT_PLANT_BLACKLIST);
        boolean changed = !plantBlacklist.equals(defaults);
        plantBlacklist = defaults;
        return changed;
    }

    public static boolean isPlantBlacklisted(Block block) {
        return plantBlacklist.contains(BuiltInRegistries.BLOCK.getKey((Object)block));
    }

    public static boolean isPlantBlacklisted(ResourceLocation id) {
        return plantBlacklist.contains(id);
    }

    public static List<String> defaultPlantWhitelistIds() {
        return DEFAULT_PLANT_WHITELIST;
    }

    public static List<String> plantWhitelistIds() {
        return plantWhitelist.stream().map(ResourceLocation::toString).sorted().toList();
    }

    public static void setPlantWhitelistIds(Collection<? extends String> ids) {
        plantWhitelist = GrassConfig.parseIds(ids);
    }

    public static boolean addPlantWhitelist(ResourceLocation id) {
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<ResourceLocation>(plantWhitelist);
        boolean changed = updated.add(id);
        if (changed) {
            plantWhitelist = Set.copyOf(updated);
        }
        return changed;
    }

    public static boolean removePlantWhitelist(ResourceLocation id) {
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<ResourceLocation>(plantWhitelist);
        boolean changed = updated.remove(id);
        if (changed) {
            plantWhitelist = Set.copyOf(updated);
        }
        return changed;
    }

    public static boolean clearPlantWhitelist() {
        boolean changed = !plantWhitelist.isEmpty();
        plantWhitelist = Set.of();
        return changed;
    }

    public static boolean resetPlantWhitelist() {
        Set<ResourceLocation> defaults = GrassConfig.parseIds(DEFAULT_PLANT_WHITELIST);
        boolean changed = !plantWhitelist.equals(defaults);
        plantWhitelist = defaults;
        return changed;
    }

    public static boolean isPlantWhitelisted(Block block) {
        return plantWhitelist.contains(BuiltInRegistries.BLOCK.getKey((Object)block));
    }

    public static boolean isPlantWhitelisted(ResourceLocation id) {
        return plantWhitelist.contains(id);
    }

    public static boolean isValidPlantId(Object value) {
        String string;
        return value instanceof String && GrassConfig.parseIdentifier(string = (String)value) != null;
    }

    public static ResourceLocation parseKnownBlockIdentifier(String value) {
        ResourceLocation id = GrassConfig.parseIdentifier(value);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) {
            return null;
        }
        return id;
    }

    public static List<String> knownBlockIds() {
        return BuiltInRegistries.BLOCK.keySet().stream().map(ResourceLocation::toString).sorted(Comparator.naturalOrder()).toList();
    }

    public static float effectiveWindSpeed() {
        return effectiveWindSpeed;
    }

    public static void updateDynamicWind(float windTime) {
        if (!dynamicWind) {
            return;
        }
        windDirectionDegrees = GrassConfig.normalizeDegrees(dynamicWindBaseDirectionDegrees + GrassConfig.smoothWindDirectionOffset(windTime));
        effectiveWindSpeed = GrassConfig.smoothWindSpeed(windTime);
        windSpeed = Math.round(effectiveWindSpeed);
    }

    private static float smoothWindDirectionOffset(float windTime) {
        float broadWander = Mth.sin((float)(windTime * 0.0025f));
        float smallWander = Mth.sin((float)(windTime * 0.0025f * 2.37f + 1.6f)) * 0.35f;
        return (broadWander + smallWander) / 1.35f * 80.0f;
    }

    private static float smoothWindSpeed(float windTime) {
        float windPulse = Mth.sin((float)(windTime * 0.0035f));
        float smallPulse = Mth.sin((float)(windTime * 0.0035f * 2.11f + 2.4f)) * 0.25f;
        float blend = (windPulse + smallPulse) / 1.25f * 0.5f + 0.5f;
        return Mth.lerp((float)blend, (float)70.0f, (float)110.0f) * dynamicWindSpeedLimit;
    }

    private static float normalizeDegrees(float degrees) {
        float normalized = degrees % 360.0f;
        return normalized < 0.0f ? normalized + 360.0f : normalized;
    }

    private static Set<ResourceLocation> parseIds(Collection<? extends String> ids) {
        LinkedHashSet<ResourceLocation> parsed = new LinkedHashSet<ResourceLocation>();
        for (String string : ids) {
            ResourceLocation id;
            if (string == null || (id = GrassConfig.parseIdentifier(string)) == null) continue;
            parsed.add(id);
        }
        return Set.copyOf(parsed);
    }

    private static ResourceLocation parseIdentifier(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return ResourceLocation.tryParse((String)(trimmed.indexOf(58) >= 0 ? trimmed : "minecraft:" + trimmed));
    }

    public static void save() {
        persistHook.run();
    }

    private GrassConfig() {
    }

    public static enum GrassStyle {
        TAPERED,
        SEGMENTED;

    }
}

