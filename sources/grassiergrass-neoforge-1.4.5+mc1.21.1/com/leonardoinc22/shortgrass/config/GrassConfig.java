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
   private static volatile float grassSparsity = 0.1F;
   public static volatile float bladeHeight = 0.3F;
   public static volatile float heightVariation = 1.35F;
   public static volatile float bladeWidth = 0.95F;
   public static volatile int renderRadius = 160;
   public static final int MAX_RENDER_RADIUS = 160;
   public static volatile GrassConfig.GrassStyle grassStyle = GrassConfig.GrassStyle.TAPERED;
   public static volatile boolean grassPlantsAsBlades = true;
   public static volatile boolean denseFlowers = true;
   public static volatile boolean bladeParticles = true;
   public static volatile boolean grassThroughSnow = false;
   public static volatile boolean shaderPackShadows = false;
   public static volatile float grassBrightness = 1.0F;
   public static final float MAX_BLADE_HUE_JITTER_DEGREES = 30.0F;
   private static volatile float bladeHueJitterDegrees = 8.0F;
   public static volatile float bladeGradientBottom = 0.8F;
   public static volatile float bladeGradientTop = 1.0F;
   public static volatile float bladeGradientCurve = 1.9F;
   public static final int MAX_WIND_SPEED = 500;
   public static final int DYNAMIC_WIND_MIN_SPEED = 70;
   public static final int DYNAMIC_WIND_MAX_SPEED = 110;
   private static final float DYNAMIC_WIND_DIRECTION_DRIFT_DEGREES = 80.0F;
   private static final float DYNAMIC_WIND_DIRECTION_DRIFT_SPEED = 0.0025F;
   private static final float DYNAMIC_WIND_INTENSITY_DRIFT_SPEED = 0.0035F;
   public static volatile int windSpeed = 100;
   public static volatile float windDirectionDegrees = 0.0F;
   public static volatile boolean dynamicWind = true;
   private static volatile float dynamicWindSpeedLimit = 0.8F;
   private static volatile float effectiveWindSpeed = windSpeed;
   private static volatile int manualWindSpeed = windSpeed;
   private static volatile float dynamicWindBaseDirectionDegrees = windDirectionDegrees;
   private static final List<String> DEFAULT_PLANT_BLACKLIST = List.of("minecraft:seagrass", "minecraft:tall_seagrass", "minecraft:sea_pickle");
   private static volatile Set<ResourceLocation> plantBlacklist = parseIds(DEFAULT_PLANT_BLACKLIST);
   private static final List<String> DEFAULT_PLANT_WHITELIST = List.of();
   private static volatile Set<ResourceLocation> plantWhitelist = parseIds(DEFAULT_PLANT_WHITELIST);
   private static volatile Runnable persistHook = () -> {};

   public static void setPersistHook(Runnable hook) {
      persistHook = hook;
   }

   public static void setGrassSparsity(float sparsity) {
      grassSparsity = Mth.clamp(sparsity, 0.0F, 1.0F);
   }

   public static float grassSparsity() {
      return grassSparsity;
   }

   public static void setBladeHueJitterDegrees(float degrees) {
      bladeHueJitterDegrees = Mth.clamp(degrees, 0.0F, 30.0F);
   }

   public static float bladeHueJitterDegrees() {
      return bladeHueJitterDegrees;
   }

   public static void setWindSpeed(int speed) {
      manualWindSpeed = Math.max(0, Math.min(500, speed));
      windSpeed = manualWindSpeed;
      effectiveWindSpeed = manualWindSpeed;
   }

   public static void setWindDirectionDegrees(float degrees) {
      dynamicWindBaseDirectionDegrees = normalizeDegrees(degrees);
      windDirectionDegrees = dynamicWindBaseDirectionDegrees;
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
      dynamicWindSpeedLimit = Mth.clamp(limit, 0.0F, 1.0F);
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
      return plantBlacklist.stream().<String>map(ResourceLocation::toString).sorted().toList();
   }

   public static void setPlantBlacklistIds(Collection<? extends String> ids) {
      plantBlacklist = parseIds(ids);
   }

   public static boolean addPlantBlacklist(ResourceLocation id) {
      Set<ResourceLocation> updated = new LinkedHashSet<>(plantBlacklist);
      boolean changed = updated.add(id);
      if (changed) {
         plantBlacklist = Set.copyOf(updated);
      }

      return changed;
   }

   public static boolean removePlantBlacklist(ResourceLocation id) {
      Set<ResourceLocation> updated = new LinkedHashSet<>(plantBlacklist);
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
      Set<ResourceLocation> defaults = parseIds(DEFAULT_PLANT_BLACKLIST);
      boolean changed = !plantBlacklist.equals(defaults);
      plantBlacklist = defaults;
      return changed;
   }

   public static boolean isPlantBlacklisted(Block block) {
      return plantBlacklist.contains(BuiltInRegistries.BLOCK.getKey(block));
   }

   public static boolean isPlantBlacklisted(ResourceLocation id) {
      return plantBlacklist.contains(id);
   }

   public static List<String> defaultPlantWhitelistIds() {
      return DEFAULT_PLANT_WHITELIST;
   }

   public static List<String> plantWhitelistIds() {
      return plantWhitelist.stream().<String>map(ResourceLocation::toString).sorted().toList();
   }

   public static void setPlantWhitelistIds(Collection<? extends String> ids) {
      plantWhitelist = parseIds(ids);
   }

   public static boolean addPlantWhitelist(ResourceLocation id) {
      Set<ResourceLocation> updated = new LinkedHashSet<>(plantWhitelist);
      boolean changed = updated.add(id);
      if (changed) {
         plantWhitelist = Set.copyOf(updated);
      }

      return changed;
   }

   public static boolean removePlantWhitelist(ResourceLocation id) {
      Set<ResourceLocation> updated = new LinkedHashSet<>(plantWhitelist);
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
      Set<ResourceLocation> defaults = parseIds(DEFAULT_PLANT_WHITELIST);
      boolean changed = !plantWhitelist.equals(defaults);
      plantWhitelist = defaults;
      return changed;
   }

   public static boolean isPlantWhitelisted(Block block) {
      return plantWhitelist.contains(BuiltInRegistries.BLOCK.getKey(block));
   }

   public static boolean isPlantWhitelisted(ResourceLocation id) {
      return plantWhitelist.contains(id);
   }

   public static boolean isValidPlantId(Object value) {
      return value instanceof String string && parseIdentifier(string) != null;
   }

   public static ResourceLocation parseKnownBlockIdentifier(String value) {
      ResourceLocation id = parseIdentifier(value);
      return id != null && BuiltInRegistries.BLOCK.containsKey(id) ? id : null;
   }

   public static List<String> knownBlockIds() {
      return BuiltInRegistries.BLOCK.keySet().stream().<String>map(ResourceLocation::toString).sorted(Comparator.naturalOrder()).toList();
   }

   public static float effectiveWindSpeed() {
      return effectiveWindSpeed;
   }

   public static void updateDynamicWind(float windTime) {
      if (dynamicWind) {
         windDirectionDegrees = normalizeDegrees(dynamicWindBaseDirectionDegrees + smoothWindDirectionOffset(windTime));
         effectiveWindSpeed = smoothWindSpeed(windTime);
         windSpeed = Math.round(effectiveWindSpeed);
      }
   }

   private static float smoothWindDirectionOffset(float windTime) {
      float broadWander = Mth.sin(windTime * 0.0025F);
      float smallWander = Mth.sin(windTime * 0.0025F * 2.37F + 1.6F) * 0.35F;
      return (broadWander + smallWander) / 1.35F * 80.0F;
   }

   private static float smoothWindSpeed(float windTime) {
      float windPulse = Mth.sin(windTime * 0.0035F);
      float smallPulse = Mth.sin(windTime * 0.0035F * 2.11F + 2.4F) * 0.25F;
      float blend = (windPulse + smallPulse) / 1.25F * 0.5F + 0.5F;
      return Mth.lerp(blend, 70.0F, 110.0F) * dynamicWindSpeedLimit;
   }

   private static float normalizeDegrees(float degrees) {
      float normalized = degrees % 360.0F;
      return normalized < 0.0F ? normalized + 360.0F : normalized;
   }

   private static Set<ResourceLocation> parseIds(Collection<? extends String> ids) {
      Set<ResourceLocation> parsed = new LinkedHashSet<>();

      for (String value : ids) {
         if (value != null) {
            ResourceLocation id = parseIdentifier(value);
            if (id != null) {
               parsed.add(id);
            }
         }
      }

      return Set.copyOf(parsed);
   }

   private static ResourceLocation parseIdentifier(String value) {
      String trimmed = value.trim();
      return trimmed.isEmpty() ? null : ResourceLocation.tryParse(trimmed.indexOf(58) >= 0 ? trimmed : "minecraft:" + trimmed);
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
