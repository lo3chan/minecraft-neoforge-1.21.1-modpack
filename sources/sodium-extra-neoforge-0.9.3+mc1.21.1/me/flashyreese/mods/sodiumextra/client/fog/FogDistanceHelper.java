package me.flashyreese.mods.sodiumextra.client.fog;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;
import me.flashyreese.mods.greenlight.feature.ClientFeature;
import me.flashyreese.mods.greenlight.feature.Greenlight;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.Config;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance.ClampingLazyMaxIntRange;
import net.minecraft.client.OptionInstance.IntRange;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.GsonHelper;

public final class FogDistanceHelper {
   public static final ResourceLocation SODIUM_RENDER_DISTANCE_OPTION_ID = ResourceLocation.parse("sodium:general.render_distance");
   public static final int FOG_DISTANCE_OFF = -1;
   public static final int FOG_DISTANCE_VANILLA = 0;
   private static final int LEGACY_FOG_DISTANCE_OFF = 33;
   public static final int VANILLA_CLOUD_FOG_PERCENT = 100;
   private static final int VANILLA_MAX_FOG_DISTANCE = 32;
   private static final int VANILLA_MAX_CLOUD_RENDER_DISTANCE = 128;
   private static final int PROTECTED_FOG_DISTANCE_MAX_BLOCKS = 256;
   private static final float PLANAR_RENDER_DISTANCE_OFFSET = 2097152.0F;
   private static final float CYLINDRICAL_RENDER_DISTANCE_OFFSET = 3145728.0F;
   private static final float CYLINDRICAL_CULL_DISTANCE_MARKER = 0.75F;
   private static final float CHUNK_SIZE = 16.0F;
   public static final float CYLINDRICAL_VERTICAL_SCALE = 16.0F;
   private static final ClientFeature<FogDistanceHelper.ProtectedGameplayFogPolicy> PROTECTED_GAMEPLAY_FOG = Greenlight.feature(
         ResourceLocation.fromNamespaceAndPath("sodium-extra", "protected_gameplay_fog")
      )
      .decoder(1, FogDistanceHelper.ProtectedGameplayFogPolicy::fromJson)
      .register();
   private static volatile FogDistanceHelper.ExpandedCylindricalCull activeExpandedCylindricalCull;

   private FogDistanceHelper() {
   }

   public static SodiumExtraGameOptions.AtmosphericFogSettings getAtmosphericSettings(ClientLevel level) {
      SodiumExtraGameOptions.FogSettings fogSettings = getFogSettings();
      ResourceLocation dimensionEffectsId = level.dimensionType().effectsLocation();
      return fogSettings.getAtmospheric(dimensionEffectsId);
   }

   public static int normalizeFogDistance(int fogDistance) {
      return fogDistance == 33 ? -1 : fogDistance;
   }

   public static Range getFogDistanceRange(ConfigState state) {
      return new Range(-1, getMaxFogDistance(state), 1);
   }

   public static Range getProtectedGameplayFogDistanceRange() {
      return new Range(-1, 256, 1);
   }

   public static int getMaxFogDistance(ConfigState state) {
      int maxFogDistance = 32;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.options != null) {
         maxFogDistance = Math.max(maxFogDistance, (Integer)minecraft.options.renderDistance().get());
         Object valueSet = minecraft.options.renderDistance().values();
         if (valueSet instanceof IntRange range) {
            maxFogDistance = Math.max(maxFogDistance, range.maxInclusive());
         } else if (valueSet instanceof ClampingLazyMaxIntRange range) {
            maxFogDistance = Math.max(maxFogDistance, range.maxInclusive());
         } else {
            maxFogDistance = Math.max(maxFogDistance, getIntAccessor(valueSet, "maxInclusive", maxFogDistance));
         }
      }

      maxFogDistance = Math.max(maxFogDistance, getSodiumRenderDistanceMax(state, maxFogDistance));
      SodiumExtraGameOptions.FogSettings fogSettings = getFogSettings();
      maxFogDistance = Math.max(maxFogDistance, normalizeFogDistance(fogSettings.atmospheric.distanceChunks));

      for (SodiumExtraGameOptions.AtmosphericFogSettings settings : fogSettings.dimensionOverrides.values()) {
         maxFogDistance = Math.max(maxFogDistance, normalizeFogDistance(settings.distanceChunks));
      }

      return maxFogDistance;
   }

   private static int getSodiumRenderDistanceMax(ConfigState state, int fallback) {
      Config config = getSodiumConfig(state);
      if (config == null) {
         return fallback;
      } else {
         try {
            if (config.getOption(SODIUM_RENDER_DISTANCE_OPTION_ID) instanceof IntegerOption integerOption) {
               SteppedValidator validator = integerOption.getSteppedValidator();
               return validator.max();
            }
         } catch (RuntimeException var6) {
         }

         return fallback;
      }
   }

   private static Config getSodiumConfig(ConfigState state) {
      if (state instanceof Config config) {
         return config;
      } else {
         Config reflectedConfig = getConfigFromState(state);
         return reflectedConfig != null ? reflectedConfig : ConfigManager.CONFIG;
      }
   }

   private static Config getConfigFromState(ConfigState state) {
      if (state == null) {
         return null;
      } else {
         Class<?> type = state.getClass();

         while (type != null) {
            try {
               Field field = type.getDeclaredField("state");
               field.setAccessible(true);
               return field.get(state) instanceof Config config ? config : null;
            } catch (NoSuchFieldException var5) {
               type = type.getSuperclass();
            } catch (RuntimeException | ReflectiveOperationException var6) {
               return null;
            }
         }

         return null;
      }
   }

   public static float getStart(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
      return settings.distanceChunks * 16.0F * (settings.startPercent / 100.0F);
   }

   public static float applyStartMultiplier(float start, SodiumExtraGameOptions.AtmosphericFogSettings settings) {
      return start * (settings.startPercent / 100.0F);
   }

   public static float getEnd(int fogDistance) {
      return (fogDistance + 1) * 16.0F;
   }

   public static float getCloudEnd(int cloudFogPercent) {
      return getCloudRenderDistance() * 16.0F * (Math.clamp(cloudFogPercent, 0, 100) / 100.0F);
   }

   private static int getCloudRenderDistance() {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft != null && minecraft.options != null ? Math.max(1, (Integer)minecraft.options.renderDistance().get()) : 128;
   }

   public static boolean disablesFog(int fogDistance) {
      return fogDistance == -1;
   }

   public static void applyRenderDistanceShape(float fogStart, float fogEnd, SodiumExtraGameOptions.AtmosphericFogSettings settings) {
      if (fogEnd != 3.4028235E38F) {
         switch (settings.shapeMode) {
            case CYLINDRICAL:
               if (FogShaderTransformer.isShapeSupported()) {
                  RenderSystem.setShaderFogStart(fogStart + 3145728.0F);
                  RenderSystem.setShaderFogEnd(fogEnd + 3145728.0F);
               } else {
                  RenderSystem.setShaderFogShape(FogShape.CYLINDER);
               }
               break;
            case RADIAL:
               RenderSystem.setShaderFogShape(FogShape.SPHERE);
               break;
            case PLANAR:
               if (FogShaderTransformer.isShapeSupported()) {
                  RenderSystem.setShaderFogStart(fogStart + 2097152.0F);
                  RenderSystem.setShaderFogEnd(fogEnd + 2097152.0F);
               }
            case VANILLA:
         }
      }
   }

   public static float expandCylindricalCullDistance(float currentDistance, float renderDistanceStart, float renderDistanceEnd, float renderDistance) {
      if (!isCylindricalRenderDistanceEncoded(renderDistanceStart, renderDistanceEnd)) {
         return currentDistance;
      } else {
         float decodedRenderDistanceEnd = renderDistanceEnd - 3145728.0F;
         if (Float.isFinite(decodedRenderDistanceEnd) && !(decodedRenderDistanceEnd <= 0.0F) && Float.isFinite(renderDistance) && !(renderDistance <= 0.0F)) {
            float verticalLimit = renderDistance * 16.0F;
            float expandedDistance = (float)Math.ceil(Math.max(renderDistance, verticalLimit)) + 0.75F;
            activeExpandedCylindricalCull = new FogDistanceHelper.ExpandedCylindricalCull(expandedDistance, renderDistance, verticalLimit);
            return expandedDistance;
         } else {
            return currentDistance;
         }
      }
   }

   public static boolean isExpandedCylindricalCullDistance(float distanceLimit) {
      FogDistanceHelper.ExpandedCylindricalCull active = activeExpandedCylindricalCull;
      return active != null && active.matches(distanceLimit);
   }

   public static boolean testExpandedCylindricalCullDistance(float horizontalDistanceSquared, float verticalDistance, float distanceLimit) {
      FogDistanceHelper.ExpandedCylindricalCull active = activeExpandedCylindricalCull;
      return active != null && active.matches(distanceLimit)
         ? horizontalDistanceSquared < active.horizontalLimit() * active.horizontalLimit() && Math.abs(verticalDistance) < active.verticalLimit()
         : horizontalDistanceSquared < distanceLimit * distanceLimit && Math.abs(verticalDistance) < distanceLimit;
   }

   private static boolean isCylindricalRenderDistanceEncoded(float renderDistanceStart, float renderDistanceEnd) {
      return FogShaderTransformer.isShapeSupported()
         && Float.isFinite(renderDistanceStart)
         && Float.isFinite(renderDistanceEnd)
         && renderDistanceStart >= 3145728.0F
         && renderDistanceEnd >= 3145728.0F;
   }

   public static boolean isBossFogActive() {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft != null && minecraft.gui != null && minecraft.gui.getBossOverlay().shouldCreateWorldFog();
   }

   public static boolean shouldModifyProtectedGameplayFog() {
      SodiumExtraGameOptions.FogSettings fogSettings = getFogSettings();
      return fogSettings.advanced
         && fogSettings.protectedGameplay.enabledWhenAllowed
         && (isLocalWorldAllowedForProtectedGameplayFog() || PROTECTED_GAMEPLAY_FOG.policy().isPresent());
   }

   public static int getProtectedGameplayFogDistance(FogDistanceHelper.ProtectedFogType type) {
      SodiumExtraGameOptions.FogSettings fogSettings = getFogSettings();
      if (fogSettings.advanced && fogSettings.protectedGameplay.enabledWhenAllowed) {
         int distanceBlocks = getConfiguredProtectedGameplayFogDistance(fogSettings.protectedGameplay, type);
         return isLocalWorldAllowedForProtectedGameplayFog()
            ? distanceBlocks
            : PROTECTED_GAMEPLAY_FOG.policy().map(policy -> policy.clamp(type, distanceBlocks)).orElse(0);
      } else {
         return 0;
      }
   }

   private static int getConfiguredProtectedGameplayFogDistance(SodiumExtraGameOptions.ProtectedFogSettings settings, FogDistanceHelper.ProtectedFogType type) {
      return switch (type) {
         case BLINDNESS -> settings.blindnessDistanceBlocks;
         case DARKNESS -> settings.darknessDistanceBlocks;
         case LAVA -> settings.lavaDistanceBlocks;
         case POWDER_SNOW -> settings.powderSnowDistanceBlocks;
         case WATER -> settings.waterDistanceBlocks;
      };
   }

   public static void applyProtectedGameplayFog(int distanceBlocks, float startMultiplier, float endMultiplier) {
      distanceBlocks = normalizeFogDistance(distanceBlocks);
      if (distanceBlocks != 0) {
         if (disablesFog(distanceBlocks)) {
            RenderSystem.setShaderFogStart(3.4028235E38F);
            RenderSystem.setShaderFogEnd(3.4028235E38F);
         } else {
            float end = distanceBlocks * endMultiplier;
            RenderSystem.setShaderFogStart(distanceBlocks * startMultiplier);
            RenderSystem.setShaderFogEnd(end);
         }
      }
   }

   public static SodiumExtraGameOptions.FogSettings getFogSettings() {
      SodiumExtraGameOptions.RenderSettings renderSettings = SodiumExtraClientMod.options().renderSettings;
      renderSettings.sanitize();
      return renderSettings.fogSettings;
   }

   private static boolean isLocalWorldAllowedForProtectedGameplayFog() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || !minecraft.hasSingleplayerServer()) {
         return false;
      } else if (minecraft.isSingleplayer()) {
         return true;
      } else {
         PlayerList playerList = minecraft.getSingleplayerServer().getPlayerList();
         return playerList != null && playerList.isAllowCommandsForAllPlayers();
      }
   }

   private static int getIntAccessor(Object object, String methodName, int fallback) {
      if (object == null) {
         return fallback;
      } else {
         try {
            Method method = object.getClass().getMethod(methodName);
            return method.invoke(object) instanceof Integer value ? value : fallback;
         } catch (RuntimeException | ReflectiveOperationException var6) {
            return fallback;
         }
      }
   }

   private record ExpandedCylindricalCull(float distanceLimit, float horizontalLimit, float verticalLimit) {
      private boolean matches(float candidate) {
         return Float.floatToRawIntBits(candidate) == Float.floatToRawIntBits(this.distanceLimit);
      }
   }

   private record ProtectedFogRule(boolean enabled, int maxDistanceBlocks, boolean allowOff) {
      private int clamp(int distanceBlocks) {
         if (!this.enabled) {
            return 0;
         } else if (distanceBlocks == 0) {
            return 0;
         } else if (FogDistanceHelper.disablesFog(distanceBlocks)) {
            return this.allowOff ? -1 : this.maxDistanceBlocks;
         } else {
            return Math.min(distanceBlocks, this.maxDistanceBlocks);
         }
      }
   }

   public static enum ProtectedFogType {
      BLINDNESS("blindness"),
      DARKNESS("darkness"),
      LAVA("lava"),
      POWDER_SNOW("powder_snow"),
      WATER("water");

      private final String policyKey;

      private ProtectedFogType(String policyKey) {
         this.policyKey = policyKey;
      }
   }

   private record ProtectedGameplayFogPolicy(Map<FogDistanceHelper.ProtectedFogType, FogDistanceHelper.ProtectedFogRule> rules) {
      private static FogDistanceHelper.ProtectedGameplayFogPolicy fromJson(JsonObject settings) {
         EnumMap<FogDistanceHelper.ProtectedFogType, FogDistanceHelper.ProtectedFogRule> rules = new EnumMap<>(FogDistanceHelper.ProtectedFogType.class);

         for (FogDistanceHelper.ProtectedFogType type : FogDistanceHelper.ProtectedFogType.values()) {
            JsonObject rule = GsonHelper.getAsJsonObject(settings, type.policyKey, new JsonObject());
            boolean enabled = GsonHelper.getAsBoolean(rule, "enabled", false);
            int maxDistanceBlocks = Math.max(0, Math.min(GsonHelper.getAsInt(rule, "max_distance_blocks", 0), 256));
            boolean allowOff = GsonHelper.getAsBoolean(rule, "allow_off", false);
            rules.put(type, new FogDistanceHelper.ProtectedFogRule(enabled, maxDistanceBlocks, allowOff));
         }

         return new FogDistanceHelper.ProtectedGameplayFogPolicy(Map.copyOf(rules));
      }

      private int clamp(FogDistanceHelper.ProtectedFogType type, int distanceBlocks) {
         FogDistanceHelper.ProtectedFogRule rule = this.rules.get(type);
         return rule != null ? rule.clamp(distanceBlocks) : 0;
      }
   }
}
