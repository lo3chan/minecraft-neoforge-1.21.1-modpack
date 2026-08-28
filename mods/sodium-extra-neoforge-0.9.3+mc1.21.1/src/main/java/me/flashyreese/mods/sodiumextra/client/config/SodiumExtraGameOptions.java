/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.FieldNamingPolicy
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonParseException
 *  com.google.gson.annotations.SerializedName
 *  it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.caffeinemc.mods.sodium.api.config.StorageEventHandler
 *  net.caffeinemc.mods.sodium.client.gui.options.TextProvider
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.lwjgl.glfw.GLFW
 */
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
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.ConfigFileIO;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import me.flashyreese.mods.sodiumextra.common.util.IdentifierSerializer;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.client.gui.options.TextProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class SodiumExtraGameOptions
implements StorageEventHandler {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, (Object)new IdentifierSerializer()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().excludeFieldsWithModifiers(new int[]{2}).create();
    public AnimationSettings animationSettings = new AnimationSettings();
    public ParticleSettings particleSettings = new ParticleSettings();
    public DetailSettings detailSettings = new DetailSettings();
    public RenderSettings renderSettings = new RenderSettings();
    @SerializedName(value="extra_settings")
    public ExtraSettings extraSettings = new ExtraSettings();
    private Path path;

    public static SodiumExtraGameOptions load(File file) {
        return SodiumExtraGameOptions.load(file.toPath());
    }

    public static SodiumExtraGameOptions load(Path path) {
        SodiumExtraGameOptions config;
        boolean shouldWriteChanges = true;
        if (Files.exists(path, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
                config = (SodiumExtraGameOptions)gson.fromJson((Reader)reader, SodiumExtraGameOptions.class);
                if (config == null) {
                    throw new JsonParseException("Root element must be a JSON object");
                }
            }
            catch (JsonParseException | IOException | IllegalStateException e) {
                SodiumExtraClientMod.logger().warn("Could not read config, falling back to defaults", e);
                config = new SodiumExtraGameOptions();
                shouldWriteChanges = SodiumExtraGameOptions.moveCorruptConfig(path);
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
            this.animationSettings = new AnimationSettings();
        }
        if (this.particleSettings == null) {
            this.particleSettings = new ParticleSettings();
        }
        this.particleSettings.sanitize();
        if (this.detailSettings == null) {
            this.detailSettings = new DetailSettings();
        }
        if (this.renderSettings == null) {
            this.renderSettings = new RenderSettings();
        }
        this.renderSettings.sanitize();
        if (this.extraSettings == null) {
            this.extraSettings = new ExtraSettings();
        }
        this.extraSettings.sanitize();
    }

    public void writeChanges() {
        if (this.path == null) {
            SodiumExtraClientMod.logger().warn("Could not save configuration file because no path was set");
            return;
        }
        try {
            this.sanitize();
            ConfigFileIO.writeStringAtomically(this.path, gson.toJson((Object)this) + System.lineSeparator());
        }
        catch (IOException e) {
            SodiumExtraClientMod.logger().warn("Could not save configuration file", (Throwable)e);
        }
    }

    private static boolean moveCorruptConfig(Path path) {
        try {
            Path corruptPath = ConfigFileIO.moveCorruptFile(path);
            SodiumExtraClientMod.logger().warn("Moved corrupt configuration file to {}", (Object)corruptPath);
            return true;
        }
        catch (IOException e) {
            SodiumExtraClientMod.logger().warn("Could not move corrupt configuration file", (Throwable)e);
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

    public static class ParticleSettings {
        public boolean particles = true;
        public boolean rainSplash = true;
        public boolean blockBreak = true;
        public boolean blockBreaking = true;
        @SerializedName(value="other")
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
            }
            if (particleTypeId == null || this.otherMap == null) {
                return true;
            }
            return this.otherMap.getOrDefault(particleTypeId, true);
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

    public static class RenderSettings {
        public FogSettings fogSettings = new FogSettings();
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
                this.fogSettings = new FogSettings();
            }
            this.fogSettings.sanitize();
        }
    }

    public static class ExtraSettings {
        public OverlayCorner overlayCorner = OverlayCorner.TOP_LEFT;
        public TextContrast textContrast = TextContrast.NONE;
        public boolean showFps = false;
        public boolean showFPSExtended = true;
        public boolean showCoords = false;
        public boolean reduceResolutionOnMac = false;
        @SerializedName(value="wayland_fullscreen_resolution")
        public boolean waylandFullscreenResolution = false;
        @SerializedName(value="wayland_fullscreen_resolution_recovery_pending")
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
                this.overlayCorner = OverlayCorner.TOP_LEFT;
            }
            if (this.textContrast == null) {
                this.textContrast = TextContrast.NONE;
            }
            this.paniniProjectionStrength = Math.max(0, Math.min(this.paniniProjectionStrength, 100));
            if (this.steadyDebugHudRefreshInterval < 1) {
                this.steadyDebugHudRefreshInterval = 1;
            }
        }
    }

    public static class ProtectedFogSettings {
        @SerializedName(value="enabled_when_allowed", alternate={"enabled_in_private_singleplayer"})
        public boolean enabledWhenAllowed = false;
        @SerializedName(value="blindness_distance_blocks", alternate={"blindness_distance_chunks"})
        public int blindnessDistanceBlocks = 0;
        @SerializedName(value="darkness_distance_blocks", alternate={"darkness_distance_chunks"})
        public int darknessDistanceBlocks = 0;
        @SerializedName(value="lava_distance_blocks", alternate={"lava_distance_chunks"})
        public int lavaDistanceBlocks = 0;
        @SerializedName(value="powder_snow_distance_blocks", alternate={"powder_snow_distance_chunks"})
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

    public static class AtmosphericFogSettings {
        public int distanceChunks = 0;
        public int startPercent = 100;
        public FogShapeMode shapeMode = FogShapeMode.VANILLA;
        public int cloudFogPercent = 100;

        public void sanitize() {
            this.distanceChunks = FogDistanceHelper.normalizeFogDistance(this.distanceChunks);
            this.startPercent = Math.clamp((long)this.startPercent, (int)0, (int)100);
            this.cloudFogPercent = Math.clamp((long)this.cloudFogPercent, (int)0, (int)100);
            if (this.shapeMode == null || !FogShapeMode.getAvailableOptions().contains((Object)this.shapeMode)) {
                this.shapeMode = FogShapeMode.VANILLA;
            }
        }
    }

    public static class FogSettings {
        public boolean advanced = false;
        public boolean multiDimensionFogControl = false;
        public AtmosphericFogSettings atmospheric = new AtmosphericFogSettings();
        public Map<ResourceLocation, AtmosphericFogSettings> dimensionOverrides = new Object2ObjectArrayMap();
        public ProtectedFogSettings protectedGameplay = new ProtectedFogSettings();

        public void sanitize() {
            if (this.atmospheric == null) {
                this.atmospheric = new AtmosphericFogSettings();
            }
            this.atmospheric.sanitize();
            if (this.dimensionOverrides == null) {
                this.dimensionOverrides = new Object2ObjectArrayMap();
            }
            Object2ObjectArrayMap sanitizedDimensionOverrides = new Object2ObjectArrayMap(this.dimensionOverrides.size());
            for (Map.Entry<ResourceLocation, AtmosphericFogSettings> entry : this.dimensionOverrides.entrySet()) {
                ResourceLocation identifier = entry.getKey();
                if (identifier == null) continue;
                AtmosphericFogSettings settings = entry.getValue();
                if (settings == null) {
                    settings = new AtmosphericFogSettings();
                }
                settings.sanitize();
                sanitizedDimensionOverrides.put(identifier, settings);
            }
            this.dimensionOverrides = sanitizedDimensionOverrides;
            if (this.protectedGameplay == null) {
                this.protectedGameplay = new ProtectedFogSettings();
            }
            this.protectedGameplay.sanitize();
        }

        public AtmosphericFogSettings getAtmospheric(ResourceLocation dimensionId) {
            if (!this.advanced || !this.multiDimensionFogControl) {
                return this.atmospheric;
            }
            return this.getOrCreateDimensionOverride(dimensionId);
        }

        public int getDimensionFogDistance(ResourceLocation dimensionId) {
            AtmosphericFogSettings settings = this.dimensionOverrides.get(dimensionId);
            return settings != null ? settings.distanceChunks : 0;
        }

        public int getDimensionFogStart(ResourceLocation dimensionId) {
            return this.getDimensionOrFallback((ResourceLocation)dimensionId).startPercent;
        }

        public FogShapeMode getDimensionFogShape(ResourceLocation dimensionId) {
            return this.getDimensionOrFallback((ResourceLocation)dimensionId).shapeMode;
        }

        public int getDimensionCloudFogPercent(ResourceLocation dimensionId) {
            return this.getDimensionOrFallback((ResourceLocation)dimensionId).cloudFogPercent;
        }

        private AtmosphericFogSettings getDimensionOrFallback(ResourceLocation dimensionId) {
            AtmosphericFogSettings settings = this.dimensionOverrides.get(dimensionId);
            return settings != null ? settings : this.getAtmosphericFallback();
        }

        public AtmosphericFogSettings getOrCreateDimensionOverride(ResourceLocation dimensionId) {
            AtmosphericFogSettings settings = this.dimensionOverrides.computeIfAbsent(dimensionId, ignored -> this.createInheritedAtmospheric());
            settings.sanitize();
            return settings;
        }

        private AtmosphericFogSettings createInheritedAtmospheric() {
            AtmosphericFogSettings base = this.getAtmosphericFallback();
            AtmosphericFogSettings settings = new AtmosphericFogSettings();
            settings.startPercent = base.startPercent;
            settings.shapeMode = base.shapeMode;
            settings.cloudFogPercent = base.cloudFogPercent;
            return settings;
        }

        private AtmosphericFogSettings getAtmosphericFallback() {
            return this.atmospheric != null ? this.atmospheric : new AtmosphericFogSettings();
        }
    }

    public static enum FogShapeMode implements TextProvider
    {
        VANILLA("sodium-extra.option.fog_shape.vanilla"),
        CYLINDRICAL("sodium-extra.option.fog_shape.cylindrical"),
        RADIAL("sodium-extra.option.fog_shape.radial"),
        PLANAR("sodium-extra.option.fog_shape.planar");

        private final Component text;

        private FogShapeMode(String text) {
            this.text = Component.translatable((String)text);
        }

        public static EnumSet<FogShapeMode> getAvailableOptions() {
            return EnumSet.of(VANILLA, CYLINDRICAL, RADIAL, PLANAR);
        }

        public Component getLocalizedName() {
            return this.text;
        }
    }

    public static enum VerticalSyncOption implements TextProvider
    {
        OFF("options.off"),
        ON("options.on"),
        ADAPTIVE("sodium-extra.option.use_adaptive_sync.name");

        private final Component name;

        private VerticalSyncOption(String name) {
            this.name = Component.translatable((String)name);
        }

        public static VerticalSyncOption[] getAvailableOptions() {
            return (VerticalSyncOption[])Arrays.stream(VerticalSyncOption.values()).filter(VerticalSyncOption::isSupported).toArray(VerticalSyncOption[]::new);
        }

        public static boolean isAdaptiveSyncSupported() {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null || minecraft.getWindow() == null) {
                return false;
            }
            return GLFW.glfwGetCurrentContext() != 0L && (GLFW.glfwExtensionSupported((CharSequence)"GLX_EXT_swap_control_tear") || GLFW.glfwExtensionSupported((CharSequence)"WGL_EXT_swap_control_tear"));
        }

        private boolean isSupported() {
            return this != ADAPTIVE || VerticalSyncOption.isAdaptiveSyncSupported();
        }

        public Component getLocalizedName() {
            return this.name;
        }
    }

    public static enum TextContrast implements TextProvider
    {
        NONE("sodium-extra.option.text_contrast.none"),
        BACKGROUND("sodium-extra.option.text_contrast.background"),
        SHADOW("sodium-extra.option.text_contrast.shadow");

        private final Component text;

        private TextContrast(String text) {
            this.text = Component.translatable((String)text);
        }

        public Component getLocalizedName() {
            return this.text;
        }
    }

    public static enum OverlayCorner implements TextProvider
    {
        TOP_LEFT("sodium-extra.option.overlay_corner.top_left"),
        TOP_RIGHT("sodium-extra.option.overlay_corner.top_right"),
        BOTTOM_LEFT("sodium-extra.option.overlay_corner.bottom_left"),
        BOTTOM_RIGHT("sodium-extra.option.overlay_corner.bottom_right");

        private final Component text;

        private OverlayCorner(String text) {
            this.text = Component.translatable((String)text);
        }

        public Component getLocalizedName() {
            return this.text;
        }
    }
}

