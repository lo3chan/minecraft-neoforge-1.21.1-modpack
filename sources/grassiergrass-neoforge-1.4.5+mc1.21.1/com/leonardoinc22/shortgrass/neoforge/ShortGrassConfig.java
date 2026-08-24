package com.leonardoinc22.shortgrass.neoforge;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.Collection;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class ShortGrassConfig {
   public static final ModConfigSpec SPEC;
   public static final IntValue BLADES_PER_BLOCK;
   public static final DoubleValue GRASS_SPARSITY;
   public static final DoubleValue BLADE_HEIGHT;
   public static final DoubleValue HEIGHT_VARIATION;
   public static final DoubleValue BLADE_WIDTH;
   public static final IntValue RENDER_RADIUS;
   public static final EnumValue<GrassConfig.GrassStyle> GRASS_STYLE;
   public static final BooleanValue GRASS_PLANTS_AS_BLADES;
   public static final BooleanValue DENSE_FLOWERS;
   public static final BooleanValue BLADE_PARTICLES;
   public static final BooleanValue GRASS_THROUGH_SNOW;
   public static final BooleanValue SHADER_PACK_SHADOWS;
   public static final DoubleValue GRASS_BRIGHTNESS;
   public static final DoubleValue BLADE_HUE_JITTER_DEGREES;
   public static final DoubleValue BLADE_GRADIENT_BOTTOM;
   public static final DoubleValue BLADE_GRADIENT_TOP;
   public static final DoubleValue BLADE_GRADIENT_CURVE;
   public static final IntValue WIND_SPEED;
   public static final DoubleValue WIND_DIRECTION_DEGREES;
   public static final BooleanValue DYNAMIC_WIND;
   public static final DoubleValue DYNAMIC_WIND_SPEED_LIMIT;
   public static final ConfigValue<List<? extends String>> PLANT_BLACKLIST;
   public static final ConfigValue<List<? extends String>> PLANT_WHITELIST;

   public static void sync() {
      GrassConfig.bladesPerBlock = (Integer)BLADES_PER_BLOCK.get();
      GrassConfig.setGrassSparsity(((Double)GRASS_SPARSITY.get()).floatValue());
      GrassConfig.bladeHeight = ((Double)BLADE_HEIGHT.get()).floatValue();
      GrassConfig.heightVariation = ((Double)HEIGHT_VARIATION.get()).floatValue();
      GrassConfig.bladeWidth = ((Double)BLADE_WIDTH.get()).floatValue();
      GrassConfig.renderRadius = GrassConfig.clampRenderRadius((Integer)RENDER_RADIUS.get());
      GrassConfig.grassStyle = (GrassConfig.GrassStyle)GRASS_STYLE.get();
      GrassConfig.grassPlantsAsBlades = (Boolean)GRASS_PLANTS_AS_BLADES.get();
      GrassConfig.denseFlowers = (Boolean)DENSE_FLOWERS.get();
      GrassConfig.bladeParticles = (Boolean)BLADE_PARTICLES.get();
      GrassConfig.grassThroughSnow = (Boolean)GRASS_THROUGH_SNOW.get();
      GrassConfig.shaderPackShadows = (Boolean)SHADER_PACK_SHADOWS.get();
      GrassConfig.grassBrightness = ((Double)GRASS_BRIGHTNESS.get()).floatValue();
      GrassConfig.setBladeHueJitterDegrees(((Double)BLADE_HUE_JITTER_DEGREES.get()).floatValue());
      GrassConfig.bladeGradientBottom = ((Double)BLADE_GRADIENT_BOTTOM.get()).floatValue();
      GrassConfig.bladeGradientTop = ((Double)BLADE_GRADIENT_TOP.get()).floatValue();
      GrassConfig.bladeGradientCurve = ((Double)BLADE_GRADIENT_CURVE.get()).floatValue();
      GrassConfig.setWindSpeed((Integer)WIND_SPEED.get());
      GrassConfig.setWindDirectionDegrees(((Double)WIND_DIRECTION_DEGREES.get()).floatValue());
      GrassConfig.setDynamicWindSpeedLimit(((Double)DYNAMIC_WIND_SPEED_LIMIT.get()).floatValue());
      GrassConfig.setDynamicWind((Boolean)DYNAMIC_WIND.get());
      GrassConfig.setPlantBlacklistIds((Collection<? extends String>)PLANT_BLACKLIST.get());
      GrassConfig.setPlantWhitelistIds((Collection<? extends String>)PLANT_WHITELIST.get());
   }

   public static void writeBack() {
      BLADES_PER_BLOCK.set(GrassConfig.bladesPerBlock);
      GRASS_SPARSITY.set((double)GrassConfig.grassSparsity());
      BLADE_HEIGHT.set((double)GrassConfig.bladeHeight);
      HEIGHT_VARIATION.set((double)GrassConfig.heightVariation);
      BLADE_WIDTH.set((double)GrassConfig.bladeWidth);
      RENDER_RADIUS.set(GrassConfig.clampRenderRadius(GrassConfig.renderRadius));
      GRASS_STYLE.set(GrassConfig.grassStyle);
      GRASS_PLANTS_AS_BLADES.set(GrassConfig.grassPlantsAsBlades);
      DENSE_FLOWERS.set(GrassConfig.denseFlowers);
      BLADE_PARTICLES.set(GrassConfig.bladeParticles);
      GRASS_THROUGH_SNOW.set(GrassConfig.grassThroughSnow);
      SHADER_PACK_SHADOWS.set(GrassConfig.shaderPackShadows);
      GRASS_BRIGHTNESS.set((double)GrassConfig.grassBrightness);
      BLADE_HUE_JITTER_DEGREES.set((double)GrassConfig.bladeHueJitterDegrees());
      BLADE_GRADIENT_BOTTOM.set((double)GrassConfig.bladeGradientBottom);
      BLADE_GRADIENT_TOP.set((double)GrassConfig.bladeGradientTop);
      BLADE_GRADIENT_CURVE.set((double)GrassConfig.bladeGradientCurve);
      WIND_SPEED.set(GrassConfig.configuredWindSpeed());
      WIND_DIRECTION_DEGREES.set((double)GrassConfig.configuredWindDirectionDegrees());
      DYNAMIC_WIND.set(GrassConfig.dynamicWind);
      DYNAMIC_WIND_SPEED_LIMIT.set((double)GrassConfig.dynamicWindSpeedLimit());
      PLANT_BLACKLIST.set(GrassConfig.plantBlacklistIds());
      PLANT_WHITELIST.set(GrassConfig.plantWhitelistIds());
      SPEC.save();
   }

   private ShortGrassConfig() {
   }

   static {
      Builder builder = new Builder();
      builder.push("rendering");
      BLADES_PER_BLOCK = builder.comment("Number of grass blades rendered on each grass block. Higher is denser but heavier.")
         .translation("grassiergrass.configuration.bladesPerBlock")
         .defineInRange("bladesPerBlock", 64, 1, 64);
      GRASS_SPARSITY = builder.comment("Fraction of blades removed from clump rims inward. 0 keeps all; 1 removes all.")
         .translation("grassiergrass.configuration.grassSparsity")
         .defineInRange("grassSparsity", 0.1, 0.0, 1.0);
      BLADE_HEIGHT = builder.comment("Height of the grass blades.")
         .translation("grassiergrass.configuration.bladeHeight")
         .defineInRange("bladeHeight", 0.3, 0.1, 1.3);
      HEIGHT_VARIATION = builder.comment("Scales the built-in blade height variation. 1.0 is the default look; 0 makes blades uniform.")
         .translation("grassiergrass.configuration.heightVariation")
         .defineInRange("heightVariation", 1.35, 0.0, 5.0);
      BLADE_WIDTH = builder.comment("Width multiplier for grass blades.")
         .translation("grassiergrass.configuration.bladeWidth")
         .defineInRange("bladeWidth", 0.95, 0.25, 2.0);
      RENDER_RADIUS = builder.comment("How far (in blocks) grass renders around you. Cached per chunk, so distance is cheap.")
         .translation("grassiergrass.configuration.renderRadius")
         .defineInRange("renderRadius", 160, 16, 160);
      GRASS_STYLE = builder.comment("Blade look: TAPERED (smooth gradient) or SEGMENTED (vanilla-like banded blades).")
         .translation("grassiergrass.configuration.grassStyle")
         .defineEnum("grassStyle", GrassConfig.GrassStyle.TAPERED);
      GRASS_PLANTS_AS_BLADES = builder.comment("Replace vanilla short and tall grass plants with generated custom blades.")
         .translation("grassiergrass.configuration.grassPlantsAsBlades")
         .define("grassPlantsAsBlades", true);
      DENSE_FLOWERS = builder.comment("Flowers surrounded by more flowers sprout extra companions, filling out patches.")
         .translation("grassiergrass.configuration.denseFlowers")
         .define("denseFlowers", true);
      BLADE_PARTICLES = builder.comment("Shed occasional grass-blade particles into sufficiently strong wind.")
         .translation("grassiergrass.configuration.bladeParticles")
         .define("bladeParticles", true);
      GRASS_THROUGH_SNOW = builder.comment("Render grass blades when a thin snow layer covers the grass block.")
         .translation("grassiergrass.configuration.grassThroughSnow")
         .define("grassThroughSnow", false);
      SHADER_PACK_SHADOWS = builder.comment("Render grass blades and re-rendered plants into Iris/Oculus shader-pack shadow maps.")
         .translation("grassiergrass.configuration.shaderPackShadows")
         .define("shaderPackShadows", false);
      GRASS_BRIGHTNESS = builder.comment("Brightness multiplier for the grass colour. Tune to match how your shaderpack lights it.")
         .translation("grassiergrass.configuration.grassBrightness")
         .defineInRange("grassBrightness", 1.0, 0.25, 2.0);
      BLADE_HUE_JITTER_DEGREES = builder.comment("Maximum stable per-blade hue offset in degrees.")
         .translation("grassiergrass.configuration.bladeHueJitter")
         .defineInRange("bladeHueJitterDegrees", 8.0, 0.0, 30.0);
      BLADE_GRADIENT_BOTTOM = builder.comment("Brightness multiplier at the base of each grass blade.")
         .translation("grassiergrass.configuration.bladeGradientBottom")
         .defineInRange("bladeGradientBottom", 0.8, 0.25, 2.0);
      BLADE_GRADIENT_TOP = builder.comment("Brightness multiplier at the tip of each grass blade.")
         .translation("grassiergrass.configuration.bladeGradientTop")
         .defineInRange("bladeGradientTop", 1.0, 0.25, 2.0);
      BLADE_GRADIENT_CURVE = builder.comment("Blade gradient response curve. 1.0 is linear.")
         .translation("grassiergrass.configuration.bladeGradientCurve")
         .defineInRange("bladeGradientCurve", 1.9, 0.1, 4.0);
      WIND_SPEED = builder.comment("Wind speed API value. 100 preserves the original wind behavior.").defineInRange("windSpeed", 100, 0, 500);
      WIND_DIRECTION_DEGREES = builder.comment("Wind direction in degrees on the XZ plane. 0 = +X, 90 = +Z.")
         .defineInRange("windDirectionDegrees", 0.0, 0.0, 360.0);
      DYNAMIC_WIND = builder.comment("If true, wind smoothly wanders in direction and varies speed between 70 and 110.").define("dynamicWind", true);
      DYNAMIC_WIND_SPEED_LIMIT = builder.comment("Multiplier limiting the dynamic-wind speed range. 1.0 preserves 70-110; 0.0 is calm.")
         .translation("grassiergrass.configuration.dynamicWindSpeedLimit")
         .defineInRange("dynamicWindSpeedLimit", 0.8, 0.0, 1.0);
      builder.pop();
      builder.push("plants");
      PLANT_BLACKLIST = builder.comment("Block ids Grassier Grass should leave to vanilla rendering instead of hiding and re-rendering.")
         .translation("grassiergrass.configuration.plantBlacklist")
         .defineListAllowEmpty("plantBlacklist", GrassConfig.defaultPlantBlacklistIds(), GrassConfig::isValidPlantId);
      PLANT_WHITELIST = builder.comment("Extra block ids Grassier Grass should hide and re-render as grass blades.")
         .translation("grassiergrass.configuration.plantWhitelist")
         .defineListAllowEmpty("plantWhitelist", GrassConfig.defaultPlantWhitelistIds(), GrassConfig::isValidPlantId);
      builder.pop();
      SPEC = builder.build();
   }
}
