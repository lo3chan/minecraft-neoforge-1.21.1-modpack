package me.flashyreese.mods.sodiumextra.client.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import me.flashyreese.mods.sodiumextra.common.util.IdentifierSerializer;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.client.gui.options.TextProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class SodiumExtraGameOptions implements StorageEventHandler {
   private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(ResourceLocation.class, new IdentifierSerializer())
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .setPrettyPrinting()
      .excludeFieldsWithModifiers(new int[]{2})
      .create();
   public SodiumExtraGameOptions.AnimationSettings animationSettings = new SodiumExtraGameOptions.AnimationSettings();
   public SodiumExtraGameOptions.ParticleSettings particleSettings = new SodiumExtraGameOptions.ParticleSettings();
   public SodiumExtraGameOptions.DetailSettings detailSettings = new SodiumExtraGameOptions.DetailSettings();
   public SodiumExtraGameOptions.RenderSettings renderSettings = new SodiumExtraGameOptions.RenderSettings();
   @SerializedName("extra_settings")
   public SodiumExtraGameOptions.ExtraSettings extraSettings = new SodiumExtraGameOptions.ExtraSettings();
   private Path path;

   public static SodiumExtraGameOptions load(File file) {
      return load(file.toPath());
   }

   public static SodiumExtraGameOptions load(Path path) {
      boolean shouldWriteChanges = true;
      SodiumExtraGameOptions config;
      if (Files.exists(path)) {
         try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            config = (SodiumExtraGameOptions)gson.fromJson(reader, SodiumExtraGameOptions.class);
            if (config == null) {
               throw new JsonParseException("Root element must be a JSON object");
            }
         } catch (JsonParseException | IllegalStateException | IOException var8) {
            SodiumExtraClientMod.logger().warn("Could not read config, falling back to defaults", var8);
            config = new SodiumExtraGameOptions();
            shouldWriteChanges = moveCorruptConfig(path);
         }
      } else {
         config = new SodiumExtraGameOptions();
      }

      config.sanitize();
      config.path = path;
      if (shouldWriteChanges) {
         config.writeChanges();
      }

      return config;
   }

   private void sanitize() {
      if (this.animationSettings == null) {
         this.animationSettings = new SodiumExtraGameOptions.AnimationSettings();
      }

      if (this.particleSettings == null) {
         this.particleSettings = new SodiumExtraGameOptions.ParticleSettings();
      }

      this.particleSettings.sanitize();
      if (this.detailSettings == null) {
         this.detailSettings = new SodiumExtraGameOptions.DetailSettings();
      }

      if (this.renderSettings == null) {
         this.renderSettings = new SodiumExtraGameOptions.RenderSettings();
      }

      this.renderSettings.sanitize();
      if (this.extraSettings == null) {
         this.extraSettings = new SodiumExtraGameOptions.ExtraSettings();
      }

      this.extraSettings.sanitize();
   }

   public void writeChanges() {
      if (this.path == null) {
         SodiumExtraClientMod.logger().warn("Could not save configuration file because no path was set");
      } else {
         try {
            this.sanitize();
            ConfigFileIO.writeStringAtomically(this.path, gson.toJson(this) + System.lineSeparator());
         } catch (IOException var2) {
            SodiumExtraClientMod.logger().warn("Could not save configuration file", var2);
         }
      }
   }

   private static boolean moveCorruptConfig(Path path) {
      try {
         Path corruptPath = ConfigFileIO.moveCorruptFile(path);
         SodiumExtraClientMod.logger().warn("Moved corrupt configuration file to {}", corruptPath);
         return true;
      } catch (IOException var2) {
         SodiumExtraClientMod.logger().warn("Could not move corrupt configuration file", var2);
         return false;
      }
   }

   public void afterSave() {
      this.writeChanges();
   }

   public static class AnimationSettings {
      public boolean animation = true;
      public boolean water = true;
      public boolean lava = true;
      public boolean fire = true;
      public boolean portal = true;
      public boolean blockAnimations = true;
      public boolean sculkSensor = true;
   }

   public static class AtmosphericFogSettings {
      public int distanceChunks = 0;
      public int startPercent = 100;
      public SodiumExtraGameOptions.FogShapeMode shapeMode = SodiumExtraGameOptions.FogShapeMode.VANILLA;
      public int cloudFogPercent = 100;

      public void sanitize() {
         this.distanceChunks = FogDistanceHelper.normalizeFogDistance(this.distanceChunks);
         this.startPercent = Math.clamp(this.startPercent, 0, 100);
         this.cloudFogPercent = Math.clamp(this.cloudFogPercent, 0, 100);
         if (this.shapeMode == null || !SodiumExtraGameOptions.FogShapeMode.getAvailableOptions().contains(this.shapeMode)) {
            this.shapeMode = SodiumExtraGameOptions.FogShapeMode.VANILLA;
         }
      }
   }

   public static class DetailSettings {
      public boolean sky = true;
      public boolean sun = true;
      public boolean moon = true;
      public boolean stars = true;
      public boolean rainSnow = true;
      public boolean biomeColors = true;
      public boolean skyColors = true;
   }

   public static class ExtraSettings {
      public SodiumExtraGameOptions.OverlayCorner overlayCorner = SodiumExtraGameOptions.OverlayCorner.TOP_LEFT;
      public SodiumExtraGameOptions.TextContrast textContrast = SodiumExtraGameOptions.TextContrast.NONE;
      public boolean showFps = false;
      public boolean showFPSExtended = true;
      public boolean showCoords = false;
      public boolean reduceResolutionOnMac = false;
      @SerializedName("wayland_fullscreen_resolution")
      public boolean waylandFullscreenResolution = false;
      @SerializedName("wayland_fullscreen_resolution_recovery_pending")
      public boolean waylandFullscreenResolutionRecoveryPending = false;
      public boolean useAdaptiveSync = false;
      public boolean cloudHeightOverride = false;
      public int cloudHeight = 192;
      public int cloudDistance = 100;
      public boolean toasts = true;
      public boolean advancementToast = true;
      public boolean recipeToast = true;
      public boolean systemToast = true;
      public boolean tutorialToast = true;
      public boolean instantSneak = false;
      public boolean preventShaders = false;
      public boolean paniniProjection = false;
      public int paniniProjectionStrength = 25;
      public boolean steadyDebugHud = true;
      public int steadyDebugHudRefreshInterval = 1;

      public void sanitize() {
         if (this.overlayCorner == null) {
            this.overlayCorner = SodiumExtraGameOptions.OverlayCorner.TOP_LEFT;
         }

         if (this.textContrast == null) {
            this.textContrast = SodiumExtraGameOptions.TextContrast.NONE;
         }

         this.paniniProjectionStrength = Math.max(0, Math.min(this.paniniProjectionStrength, 100));
         if (this.steadyDebugHudRefreshInterval < 1) {
            this.steadyDebugHudRefreshInterval = 1;
         }
      }
   }

   public static class FogSettings {
      public boolean advanced = false;
      public boolean multiDimensionFogControl = false;
      public SodiumExtraGameOptions.AtmosphericFogSettings atmospheric = new SodiumExtraGameOptions.AtmosphericFogSettings();
      public Map<ResourceLocation, SodiumExtraGameOptions.AtmosphericFogSettings> dimensionOverrides = new Object2ObjectArrayMap();
      public SodiumExtraGameOptions.ProtectedFogSettings protectedGameplay = new SodiumExtraGameOptions.ProtectedFogSettings();

      public void sanitize() {
         if (this.atmospheric == null) {
            this.atmospheric = new SodiumExtraGameOptions.AtmosphericFogSettings();
         }

         this.atmospheric.sanitize();
         if (this.dimensionOverrides == null) {
            this.dimensionOverrides = new Object2ObjectArrayMap();
         }

         Map<ResourceLocation, SodiumExtraGameOptions.AtmosphericFogSettings> sanitizedDimensionOverrides = new Object2ObjectArrayMap(
            this.dimensionOverrides.size()
         );

         for (Entry<ResourceLocation, SodiumExtraGameOptions.AtmosphericFogSettings> entry : this.dimensionOverrides.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            if (identifier != null) {
               SodiumExtraGameOptions.AtmosphericFogSettings settings = entry.getValue();
               if (settings == null) {
                  settings = new SodiumExtraGameOptions.AtmosphericFogSettings();
               }

               settings.sanitize();
               sanitizedDimensionOverrides.put(identifier, settings);
            }
         }

         this.dimensionOverrides = sanitizedDimensionOverrides;
         if (this.protectedGameplay == null) {
            this.protectedGameplay = new SodiumExtraGameOptions.ProtectedFogSettings();
         }

         this.protectedGameplay.sanitize();
      }

      public SodiumExtraGameOptions.AtmosphericFogSettings getAtmospheric(ResourceLocation dimensionId) {
         return this.advanced && this.multiDimensionFogControl ? this.getOrCreateDimensionOverride(dimensionId) : this.atmospheric;
      }

      public int getDimensionFogDistance(ResourceLocation dimensionId) {
         SodiumExtraGameOptions.AtmosphericFogSettings settings = this.dimensionOverrides.get(dimensionId);
         return settings != null ? settings.distanceChunks : 0;
      }

      public int getDimensionFogStart(ResourceLocation dimensionId) {
         return this.getDimensionOrFallback(dimensionId).startPercent;
      }

      public SodiumExtraGameOptions.FogShapeMode getDimensionFogShape(ResourceLocation dimensionId) {
         return this.getDimensionOrFallback(dimensionId).shapeMode;
      }

      public int getDimensionCloudFogPercent(ResourceLocation dimensionId) {
         return this.getDimensionOrFallback(dimensionId).cloudFogPercent;
      }

      private SodiumExtraGameOptions.AtmosphericFogSettings getDimensionOrFallback(ResourceLocation dimensionId) {
         SodiumExtraGameOptions.AtmosphericFogSettings settings = this.dimensionOverrides.get(dimensionId);
         return settings != null ? settings : this.getAtmosphericFallback();
      }

      public SodiumExtraGameOptions.AtmosphericFogSettings getOrCreateDimensionOverride(ResourceLocation dimensionId) {
         SodiumExtraGameOptions.AtmosphericFogSettings settings = this.dimensionOverrides
            .computeIfAbsent(dimensionId, ignored -> this.createInheritedAtmospheric());
         settings.sanitize();
         return settings;
      }

      private SodiumExtraGameOptions.AtmosphericFogSettings createInheritedAtmospheric() {
         SodiumExtraGameOptions.AtmosphericFogSettings base = this.getAtmosphericFallback();
         SodiumExtraGameOptions.AtmosphericFogSettings settings = new SodiumExtraGameOptions.AtmosphericFogSettings();
         settings.startPercent = base.startPercent;
         settings.shapeMode = base.shapeMode;
         settings.cloudFogPercent = base.cloudFogPercent;
         return settings;
      }

      private SodiumExtraGameOptions.AtmosphericFogSettings getAtmosphericFallback() {
         return this.atmospheric != null ? this.atmospheric : new SodiumExtraGameOptions.AtmosphericFogSettings();
      }
   }

   public static enum FogShapeMode implements TextProvider {
      VANILLA("sodium-extra.option.fog_shape.vanilla"),
      CYLINDRICAL("sodium-extra.option.fog_shape.cylindrical"),
      RADIAL("sodium-extra.option.fog_shape.radial"),
      PLANAR("sodium-extra.option.fog_shape.planar");

      private final Component text;

      private FogShapeMode(String text) {
         this.text = Component.translatable(text);
      }

      public static EnumSet<SodiumExtraGameOptions.FogShapeMode> getAvailableOptions() {
         return EnumSet.of(VANILLA, CYLINDRICAL, RADIAL, PLANAR);
      }

      public Component getLocalizedName() {
         return this.text;
      }
   }

   public static enum OverlayCorner implements TextProvider {
      TOP_LEFT("sodium-extra.option.overlay_corner.top_left"),
      TOP_RIGHT("sodium-extra.option.overlay_corner.top_right"),
      BOTTOM_LEFT("sodium-extra.option.overlay_corner.bottom_left"),
      BOTTOM_RIGHT("sodium-extra.option.overlay_corner.bottom_right");

      private final Component text;

      private OverlayCorner(String text) {
         this.text = Component.translatable(text);
      }

      public Component getLocalizedName() {
         return this.text;
      }
   }

   public static class ParticleSettings {
      public boolean particles = true;
      public boolean rainSplash = true;
      public boolean blockBreak = true;
      public boolean blockBreaking = true;
      @SerializedName("other")
      public Map<ResourceLocation, Boolean> otherMap = new Object2BooleanLinkedOpenHashMap();

      public void sanitize() {
         if (this.otherMap == null) {
            this.otherMap = new Object2BooleanLinkedOpenHashMap();
         } else if (!(this.otherMap instanceof Object2BooleanLinkedOpenHashMap)) {
            this.otherMap = new Object2BooleanLinkedOpenHashMap(this.otherMap);
         }
      }

      public boolean isParticleEnabled(ResourceLocation particleTypeId) {
         if (!this.particles) {
            return false;
         } else {
            return particleTypeId != null && this.otherMap != null ? this.otherMap.getOrDefault(particleTypeId, true) : true;
         }
      }
   }

   public static class ProtectedFogSettings {
      @SerializedName(
         value = "enabled_when_allowed",
         alternate = {"enabled_in_private_singleplayer"}
      )
      public boolean enabledWhenAllowed = false;
      @SerializedName(
         value = "blindness_distance_blocks",
         alternate = {"blindness_distance_chunks"}
      )
      public int blindnessDistanceBlocks = 0;
      @SerializedName(
         value = "darkness_distance_blocks",
         alternate = {"darkness_distance_chunks"}
      )
      public int darknessDistanceBlocks = 0;
      @SerializedName(
         value = "lava_distance_blocks",
         alternate = {"lava_distance_chunks"}
      )
      public int lavaDistanceBlocks = 0;
      @SerializedName(
         value = "powder_snow_distance_blocks",
         alternate = {"powder_snow_distance_chunks"}
      )
      public int powderSnowDistanceBlocks = 0;
      public int waterDistanceBlocks = 0;

      public void sanitize() {
         this.blindnessDistanceBlocks = FogDistanceHelper.normalizeFogDistance(this.blindnessDistanceBlocks);
         this.darknessDistanceBlocks = FogDistanceHelper.normalizeFogDistance(this.darknessDistanceBlocks);
         this.lavaDistanceBlocks = FogDistanceHelper.normalizeFogDistance(this.lavaDistanceBlocks);
         this.powderSnowDistanceBlocks = FogDistanceHelper.normalizeFogDistance(this.powderSnowDistanceBlocks);
         this.waterDistanceBlocks = FogDistanceHelper.normalizeFogDistance(this.waterDistanceBlocks);
      }
   }

   public static class RenderSettings {
      public SodiumExtraGameOptions.FogSettings fogSettings = new SodiumExtraGameOptions.FogSettings();
      public boolean lightUpdates = true;
      public boolean itemFrame = true;
      public boolean armorStand = true;
      public boolean painting = true;
      public boolean piston = true;
      public boolean beaconBeam = true;
      public boolean limitBeaconBeamHeight = false;
      public boolean enchantingTableBook = true;
      public boolean itemFrameNameTag = true;
      public boolean playerNameTag = true;

      public void sanitize() {
         if (this.fogSettings == null) {
            this.fogSettings = new SodiumExtraGameOptions.FogSettings();
         }

         this.fogSettings.sanitize();
      }
   }

   public static enum TextContrast implements TextProvider {
      NONE("sodium-extra.option.text_contrast.none"),
      BACKGROUND("sodium-extra.option.text_contrast.background"),
      SHADOW("sodium-extra.option.text_contrast.shadow");

      private final Component text;

      private TextContrast(String text) {
         this.text = Component.translatable(text);
      }

      public Component getLocalizedName() {
         return this.text;
      }
   }

   public static enum VerticalSyncOption implements TextProvider {
      OFF("options.off"),
      ON("options.on"),
      ADAPTIVE("sodium-extra.option.use_adaptive_sync.name");

      private final Component name;

      private VerticalSyncOption(String name) {
         this.name = Component.translatable(name);
      }

      public static SodiumExtraGameOptions.VerticalSyncOption[] getAvailableOptions() {
         return Arrays.stream(values())
            .filter(SodiumExtraGameOptions.VerticalSyncOption::isSupported)
            .toArray(SodiumExtraGameOptions.VerticalSyncOption[]::new);
      }

      public static boolean isAdaptiveSyncSupported() {
         Minecraft minecraft = Minecraft.getInstance();
         return minecraft != null && minecraft.getWindow() != null
            ? GLFW.glfwGetCurrentContext() != 0L
               && (GLFW.glfwExtensionSupported("GLX_EXT_swap_control_tear") || GLFW.glfwExtensionSupported("WGL_EXT_swap_control_tear"))
            : false;
      }

      private boolean isSupported() {
         return this != ADAPTIVE || isAdaptiveSyncSupported();
      }

      public Component getLocalizedName() {
         return this.name;
      }
   }
}
