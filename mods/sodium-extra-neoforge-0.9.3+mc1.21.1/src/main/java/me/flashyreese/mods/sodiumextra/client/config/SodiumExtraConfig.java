/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Monitor
 *  com.mojang.blaze3d.platform.Window
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint
 *  net.caffeinemc.mods.sodium.api.config.ConfigState
 *  net.caffeinemc.mods.sodium.api.config.StorageEventHandler
 *  net.caffeinemc.mods.sodium.api.config.option.OptionFlag
 *  net.caffeinemc.mods.sodium.api.config.option.OptionImpact
 *  net.caffeinemc.mods.sodium.api.config.option.SteppedValidator
 *  net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.EnumOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.PageBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.StatefulOptionBuilder
 *  net.caffeinemc.mods.sodium.client.gui.FullscreenResolutionRange
 *  net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.levelgen.WorldDimensions
 *  org.lwjgl.glfw.GLFW
 */
package me.flashyreese.mods.sodiumextra.client.config;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import me.flashyreese.mods.sodiumextra.client.fog.FogShaderTransformer;
import me.flashyreese.mods.sodiumextra.client.gui.FullscreenResolutionConfirmation;
import me.flashyreese.mods.sodiumextra.common.util.ControlValueFormatterExtended;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.EnumOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.PageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.StatefulOptionBuilder;
import net.caffeinemc.mods.sodium.client.gui.FullscreenResolutionRange;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.lwjgl.glfw.GLFW;

public class SodiumExtraConfig
implements ConfigEntryPoint {
    private static final ResourceLocation ADVANCED_FOG_OPTION_ID = SodiumExtraConfig.id("advanced_fog_settings");
    private static final ResourceLocation MULTI_DIMENSION_FOG_OPTION_ID = SodiumExtraConfig.id("multi_dimension_fog");
    private static final ResourceLocation PROTECTED_GAMEPLAY_FOG_OPTION_ID = SodiumExtraConfig.id("protected_gameplay_fog");
    private static final ResourceLocation WAYLAND_FULLSCREEN_RESOLUTION_OPTION_ID = SodiumExtraConfig.id("wayland_fullscreen_resolution");
    private static final ResourceLocation CLOUD_HEIGHT_OVERRIDE_OPTION_ID = SodiumExtraConfig.id("cloud_height_override");
    private static final ResourceLocation PANINI_PROJECTION_OPTION_ID = SodiumExtraConfig.id("panini_projection");
    private static final ResourceLocation SODIUM_FULLSCREEN_OPTION_ID = ResourceLocation.parse((String)"sodium:general.fullscreen");
    private static final ResourceLocation SODIUM_FULLSCREEN_RESOLUTION_OPTION_ID = ResourceLocation.parse((String)"sodium:general.fullscreen_resolution");
    private static final ResourceLocation SODIUM_VSYNC_OPTION_ID = ResourceLocation.parse((String)"sodium:general.vsync");

    private static ResourceLocation id(String path) {
        return ResourceLocation.parse((String)("sodium-extra:" + path));
    }

    private static Boolean isFullscreenResolutionOptionEnabled(ConfigState state) {
        Monitor monitor = SodiumExtraConfig.getMonitor();
        if (monitor == null || monitor.getModeCount() <= 0) {
            return false;
        }
        return state.readBooleanOption(SODIUM_FULLSCREEN_OPTION_ID) && SodiumExtraConfig.canUseFullscreenResolution(state);
    }

    private static boolean canUseFullscreenResolution(ConfigState state) {
        return SodiumExtraConfig.canUseFullscreenResolution(state.readBooleanOption(WAYLAND_FULLSCREEN_RESOLUTION_OPTION_ID));
    }

    private static boolean canUseFullscreenResolution() {
        return SodiumExtraConfig.canUseFullscreenResolution(SodiumExtraClientMod.options().extraSettings.waylandFullscreenResolution);
    }

    private static boolean canUseFullscreenResolution(boolean waylandFullscreenResolution) {
        Util.OS os = Util.getPlatform();
        return os == Util.OS.WINDOWS || os == Util.OS.OSX || SodiumExtraConfig.isX11() || SodiumExtraConfig.isWaylandOrXWayland() && waylandFullscreenResolution;
    }

    private static boolean isX11() {
        return Util.getPlatform() == Util.OS.LINUX && GLFW.glfwGetPlatform() == 393220 && !SodiumExtraConfig.isWaylandSession();
    }

    private static boolean isWaylandOrXWayland() {
        return Util.getPlatform() == Util.OS.LINUX && (GLFW.glfwGetPlatform() == 393219 || SodiumExtraConfig.isWaylandSession());
    }

    private static boolean isWaylandSession() {
        String sessionType = System.getenv("XDG_SESSION_TYPE");
        return System.getenv("WAYLAND_DISPLAY") != null || "wayland".equalsIgnoreCase(sessionType);
    }

    private static Monitor getMonitor() {
        Window window = Minecraft.getInstance().getWindow();
        return window == null ? null : window.findBestMonitor();
    }

    private static Integer getFullscreenResolution() {
        Monitor monitor = SodiumExtraConfig.getMonitor();
        if (monitor == null) {
            return 0;
        }
        return Minecraft.getInstance().getWindow().getPreferredFullscreenVideoMode().map(arg_0 -> ((Monitor)monitor).getVideoModeIndex(arg_0)).map(value -> value + 1).orElse(0);
    }

    private static void setFullscreenResolution(Integer value) {
        Monitor monitor = SodiumExtraConfig.getMonitor();
        if (monitor == null || monitor.getModeCount() <= 0) {
            return;
        }
        Window window = Minecraft.getInstance().getWindow();
        Optional previousMode = window.getPreferredFullscreenVideoMode();
        if (!SodiumExtraConfig.canUseFullscreenResolution() || value == 0) {
            window.setPreferredFullscreenVideoMode(Optional.empty());
            SodiumExtraClientMod.disarmWaylandFullscreenResolutionRecovery();
            return;
        }
        if (SodiumExtraConfig.isWaylandOrXWayland()) {
            SodiumExtraClientMod.armWaylandFullscreenResolutionRecovery();
            FullscreenResolutionConfirmation.request(previousMode);
        } else {
            SodiumExtraClientMod.disarmWaylandFullscreenResolutionRecovery();
        }
        int modeIndex = Math.max(0, Math.min(value - 1, monitor.getModeCount() - 1));
        window.setPreferredFullscreenVideoMode(Optional.of(monitor.getMode(modeIndex)));
    }

    private static void clearPreferredFullscreenVideoMode() {
        Window window = Minecraft.getInstance().getWindow();
        if (window != null && window.getPreferredFullscreenVideoMode().isPresent()) {
            window.setPreferredFullscreenVideoMode(Optional.empty());
        }
    }

    private static int compareParticleNamespace(String a, String b) {
        if (a.equals("minecraft") && !b.equals("minecraft")) {
            return -1;
        }
        if (!a.equals("minecraft") && b.equals("minecraft")) {
            return 1;
        }
        int result = a.compareToIgnoreCase(b);
        return result != 0 ? result : a.compareTo(b);
    }

    private static Component particleNamespaceName(String namespace) {
        String name = Arrays.stream(namespace.split("[^A-Za-z0-9]+")).filter(part -> !part.isBlank()).map(SodiumExtraConfig::capitalizeNamespacePart).collect(Collectors.joining(" "));
        return Component.literal((String)(name.isBlank() ? namespace : name));
    }

    private static String capitalizeNamespacePart(String part) {
        return part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1).toLowerCase(Locale.ROOT);
    }

    private static boolean isFogMixinEnabled() {
        return SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.fog").isEnabled();
    }

    private static boolean isAdvancedFogOptionEnabled(ConfigState state) {
        return SodiumExtraConfig.isFogMixinEnabled() && state.readBooleanOption(ADVANCED_FOG_OPTION_ID);
    }

    private static boolean isFogShapeOptionEnabled(ConfigState state) {
        return SodiumExtraConfig.isSingleFogOptionEnabled(state) && FogShaderTransformer.isShapeSupported();
    }

    private static boolean isSingleFogOptionEnabled(ConfigState state) {
        boolean advanced = state.readBooleanOption(ADVANCED_FOG_OPTION_ID);
        return SodiumExtraConfig.isFogMixinEnabled() && (!advanced || !state.readBooleanOption(MULTI_DIMENSION_FOG_OPTION_ID));
    }

    private static boolean isDimensionFogOptionEnabled(ConfigState state) {
        return SodiumExtraConfig.isFogMixinEnabled() && state.readBooleanOption(ADVANCED_FOG_OPTION_ID) && state.readBooleanOption(MULTI_DIMENSION_FOG_OPTION_ID);
    }

    private static boolean isDimensionFogShapeOptionEnabled(ConfigState state) {
        return SodiumExtraConfig.isDimensionFogOptionEnabled(state) && FogShaderTransformer.isShapeSupported();
    }

    private static boolean isProtectedGameplayFogOptionEnabled(ConfigState state) {
        return SodiumExtraConfig.isFogMixinEnabled() && state.readBooleanOption(ADVANCED_FOG_OPTION_ID) && state.readBooleanOption(PROTECTED_GAMEPLAY_FOG_OPTION_ID);
    }

    private static boolean isCloudHeightOptionEnabled(ConfigState state) {
        return SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.cloud").isEnabled() && state.readBooleanOption(CLOUD_HEIGHT_OVERRIDE_OPTION_ID);
    }

    private static boolean isPaniniProjectionOptionEnabled(ConfigState state) {
        return SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.panini_projection").isEnabled() && state.readBooleanOption(PANINI_PROJECTION_OPTION_ID);
    }

    private static SodiumExtraGameOptions.FogSettings fogSettings() {
        SodiumExtraGameOptions.RenderSettings renderSettings = SodiumExtraClientMod.options().renderSettings;
        renderSettings.sanitize();
        return renderSettings.fogSettings;
    }

    private static List<ResourceLocation> getDimensionFogEffectIds(SodiumExtraGameOptions.FogSettings fogSettings) {
        LinkedHashSet<ResourceLocation> identifiers = new LinkedHashSet<ResourceLocation>();
        WorldDimensions.keysInOrder(Stream.empty()).map(dim -> dim.location()).forEach(identifiers::add);
        SodiumExtraConfig.addKnownWorldDimensionEffectIds(identifiers);
        identifiers.addAll(fogSettings.dimensionOverrides.keySet());
        return identifiers.stream().sorted(Comparator.comparing(ResourceLocation::toString)).toList();
    }

    private static void addKnownWorldDimensionEffectIds(Set<ResourceLocation> identifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        if (minecraft.level != null) {
            identifiers.add(minecraft.level.dimensionType().effectsLocation());
        }
        if (minecraft.getSingleplayerServer() != null) {
            minecraft.getSingleplayerServer().getAllLevels().forEach(level -> identifiers.add(level.dimensionType().effectsLocation()));
        }
    }

    private static void setAtmosphericFogStart(int value) {
        SodiumExtraGameOptions.FogSettings fogSettings = SodiumExtraConfig.fogSettings();
        fogSettings.atmospheric.startPercent = Math.clamp((long)value, (int)0, (int)100);
    }

    private static void setAtmosphericFogShape(SodiumExtraGameOptions.FogShapeMode value) {
        SodiumExtraGameOptions.FogSettings fogSettings = SodiumExtraConfig.fogSettings();
        fogSettings.atmospheric.shapeMode = value;
    }

    private static void setAtmosphericCloudFog(int value) {
        SodiumExtraGameOptions.FogSettings fogSettings = SodiumExtraConfig.fogSettings();
        fogSettings.atmospheric.cloudFogPercent = Math.clamp((long)value, (int)0, (int)100);
    }

    private static void setDimensionFogStart(ResourceLocation dimensionId, int value) {
        SodiumExtraConfig.fogSettings().getOrCreateDimensionOverride((ResourceLocation)dimensionId).startPercent = Math.clamp((long)value, (int)0, (int)100);
    }

    private static void setDimensionFogShape(ResourceLocation dimensionId, SodiumExtraGameOptions.FogShapeMode value) {
        SodiumExtraConfig.fogSettings().getOrCreateDimensionOverride((ResourceLocation)dimensionId).shapeMode = value;
    }

    private static void setDimensionCloudFog(ResourceLocation dimensionId, int value) {
        SodiumExtraConfig.fogSettings().getOrCreateDimensionOverride((ResourceLocation)dimensionId).cloudFogPercent = Math.clamp((long)value, (int)0, (int)100);
    }

    private static <B extends StatefulOptionBuilder<?>> B fogOption(B option, Function<ConfigState, Boolean> enabledProvider, String nameKey, String tooltipKey) {
        option.setEnabledProvider(enabledProvider, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, MULTI_DIMENSION_FOG_OPTION_ID});
        option.setControlHiddenWhenDisabled(false);
        option.setName((Component)Component.translatable((String)nameKey));
        option.setTooltip((Component)Component.translatable((String)tooltipKey));
        option.setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options());
        return option;
    }

    private static Component parseVanillaString(String key) {
        String name = Component.translatable((String)key).getString().replaceAll("\u00a7.", "");
        return Component.literal((String)(name.isBlank() ? SodiumExtraConfig.fallbackName(key) : name));
    }

    private static Component translatableName(ResourceLocation identifier, String category) {
        String key = identifier.toLanguageKey("options.".concat(category));
        MutableComponent translatable = Component.translatable((String)key);
        String name = translatable.getString().replaceAll("\u00a7.", "");
        if (!ComponentUtils.isTranslationResolvable((Component)translatable) || name.isBlank()) {
            return Component.literal((String)SodiumExtraConfig.fallbackName(key));
        }
        return Component.literal((String)name);
    }

    private static String fallbackName(String key) {
        return Arrays.stream(key.substring(key.lastIndexOf(46) + 1).split("_")).filter(s -> !s.isBlank()).map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1)).collect(Collectors.joining(" "));
    }

    private static Component translatableTooltip(ResourceLocation identifier, String category) {
        String key = identifier.toLanguageKey("options.".concat(category)).concat(".tooltip");
        MutableComponent translatable = Component.translatable((String)key);
        if (!ComponentUtils.isTranslationResolvable((Component)translatable)) {
            translatable = Component.translatable((String)"sodium-extra.option.".concat(category).concat(".tooltips"), (Object[])new Object[]{SodiumExtraConfig.translatableName(identifier, category)});
        }
        return translatable;
    }

    private OptionPageBuilder createAnimationsPage(ConfigBuilder builder) {
        return builder.createOptionPage().setName((Component)Component.translatable((String)"sodium-extra.option.animations")).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animations_all")).setName(SodiumExtraConfig.parseVanillaString("gui.socialInteractions.tab_all")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animations_all.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.animation = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.animation).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animate_water")).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.water")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animate_water.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.water = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.water).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animate_lava")).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.lava")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animate_lava.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.lava = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.lava).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animate_fire")).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.fire")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animate_fire.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.fire = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.fire).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animate_portal")).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.nether_portal")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animate_portal.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.portal = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.portal).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("block_animations")).setName((Component)Component.translatable((String)"sodium-extra.option.block_animations")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.block_animations.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.blockAnimations = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.blockAnimations).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("animate_sculk_sensor")).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.sculk_sensor")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.animate_sculk_sensor.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().animationSettings.sculkSensor = value;
        }, () -> SodiumExtraClientMod.options().animationSettings.sculkSensor).setDefaultValue(Boolean.valueOf(true)).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_ASSET_RELOAD}).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.animation").isEnabled())));
    }

    private OptionPageBuilder createParticlesPage(ConfigBuilder builder) {
        OptionPageBuilder page = builder.createOptionPage().setName(SodiumExtraConfig.parseVanillaString("options.particles")).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("particles_all")).setName(SodiumExtraConfig.parseVanillaString("gui.socialInteractions.tab_all")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.particles_all.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().particleSettings.particles = value;
        }, () -> SodiumExtraClientMod.options().particleSettings.particles).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("rain_splash_particles")).setName(SodiumExtraConfig.parseVanillaString("subtitles.entity.generic.splash")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.rain_splash.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().particleSettings.rainSplash = value;
        }, () -> SodiumExtraClientMod.options().particleSettings.rainSplash).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("block_break_particles")).setName(SodiumExtraConfig.parseVanillaString("subtitles.block.generic.break")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.block_break.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().particleSettings.blockBreak = value;
        }, () -> SodiumExtraClientMod.options().particleSettings.blockBreak).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("block_breaking_particles")).setName(SodiumExtraConfig.parseVanillaString("subtitles.block.generic.hit")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.block_breaking.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().particleSettings.blockBreaking = value;
        }, () -> SodiumExtraClientMod.options().particleSettings.blockBreaking).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled())));
        TreeMap<String, List> particlesByNamespace = new TreeMap<String, List>(SodiumExtraConfig::compareParticleNamespace);
        BuiltInRegistries.PARTICLE_TYPE.keySet().forEach(identifier -> particlesByNamespace.computeIfAbsent(identifier.getNamespace(), namespace -> new ArrayList()).add(identifier));
        particlesByNamespace.forEach((namespace, identifiers) -> {
            OptionGroupBuilder particleGroup = builder.createOptionGroup().setName(SodiumExtraConfig.particleNamespaceName(namespace));
            identifiers.stream().sorted((a, b) -> SodiumExtraConfig.translatableName(a, "particles").getString().compareToIgnoreCase(SodiumExtraConfig.translatableName(b, "particles").getString())).forEach(particleId -> particleGroup.addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("particle." + particleId.toLanguageKey("options.particles"))).setName(SodiumExtraConfig.translatableName(particleId, "particles")).setTooltip(SodiumExtraConfig.translatableTooltip(particleId, "particles")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> SodiumExtraClientMod.options().particleSettings.otherMap.put((ResourceLocation)particleId, (Boolean)value), () -> SodiumExtraClientMod.options().particleSettings.otherMap.getOrDefault(particleId, true)).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled())));
            page.addOptionGroup(particleGroup);
        });
        return page;
    }

    private OptionPageBuilder createDetailsPage(ConfigBuilder builder) {
        return builder.createOptionPage().setName((Component)Component.translatable((String)"sodium-extra.option.details")).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("sky")).setName((Component)Component.translatable((String)"sodium-extra.option.sky")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.sky.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.sky = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.sky).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.sky").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("stars")).setName((Component)Component.translatable((String)"sodium-extra.option.stars")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.stars.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.stars = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.stars).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.stars").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("sun")).setName((Component)Component.translatable((String)"sodium-extra.option.sun")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.sun.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.sun = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.sun).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.sun_moon").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("moon")).setName((Component)Component.translatable((String)"sodium-extra.option.moon")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.moon.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.moon = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.moon).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.sun_moon").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("rain_snow")).setName(SodiumExtraConfig.parseVanillaString("soundCategory.weather")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.rain_snow.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.rainSnow = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.rainSnow).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.particle").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("biome_colors")).setName((Component)Component.translatable((String)"sodium-extra.option.biome_colors")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.biome_colors.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.biomeColors = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.biomeColors).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.biome_colors").isEnabled())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("sky_colors")).setName((Component)Component.translatable((String)"sodium-extra.option.sky_colors")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.sky_colors.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().detailSettings.skyColors = value;
        }, () -> SodiumExtraClientMod.options().detailSettings.skyColors).setDefaultValue(Boolean.valueOf(true)).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.sky_colors").isEnabled())));
    }

    private OptionPageBuilder createRenderPage(ConfigBuilder builder) {
        OptionPageBuilder page = builder.createOptionPage().setName((Component)Component.translatable((String)"sodium-extra.option.render"));
        SodiumExtraGameOptions.FogSettings fogSettings = SodiumExtraConfig.fogSettings();
        List<ResourceLocation> dimensionFogEffectIds = SodiumExtraConfig.getDimensionFogEffectIds(fogSettings);
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(ADVANCED_FOG_OPTION_ID).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.fog").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.advanced_fog_settings")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.advanced_fog_settings.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.advanced = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.advanced).setDefaultValue(Boolean.valueOf(false))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("single_fog")), SodiumExtraConfig::isSingleFogOptionEnabled, "sodium-extra.option.fog_distance", "sodium-extra.option.fog_distance.tooltip").setRangeProvider(FogDistanceHelper::getFogDistanceRange, new ResourceLocation[]{FogDistanceHelper.SODIUM_RENDER_DISTANCE_OPTION_ID, ConfigState.UPDATE_ON_REBUILD}).setValueFormatter(ControlValueFormatterExtended.fogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.atmospheric.distanceChunks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.atmospheric.distanceChunks).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("fog_start")), SodiumExtraConfig::isSingleFogOptionEnabled, "sodium-extra.option.fog_start", "sodium-extra.option.fog_start.tooltip").setRange(0, 100, 1).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(SodiumExtraConfig::setAtmosphericFogStart, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.atmospheric.startPercent).setDefaultValue(Integer.valueOf(100))).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createEnumOption(SodiumExtraConfig.id("fog_shape"), SodiumExtraGameOptions.FogShapeMode.class), SodiumExtraConfig::isFogShapeOptionEnabled, "sodium-extra.option.fog_shape", "sodium-extra.option.fog_shape.tooltip").setAllowedValues(SodiumExtraGameOptions.FogShapeMode.getAvailableOptions()).setBinding(SodiumExtraConfig::setAtmosphericFogShape, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.atmospheric.shapeMode).setDefaultValue((Enum)SodiumExtraGameOptions.FogShapeMode.VANILLA)).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("cloud_fog")), SodiumExtraConfig::isSingleFogOptionEnabled, "sodium-extra.option.cloud_fog", "sodium-extra.option.cloud_fog.tooltip").setRange(0, 100, 1).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(SodiumExtraConfig::setAtmosphericCloudFog, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.atmospheric.cloudFogPercent).setDefaultValue(Integer.valueOf(100))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(MULTI_DIMENSION_FOG_OPTION_ID).setEnabledProvider(SodiumExtraConfig::isAdvancedFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.multi_dimension_fog")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.multi_dimension_fog.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.multiDimensionFogControl = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.multiDimensionFogControl).setDefaultValue(Boolean.valueOf(false))));
        dimensionFogEffectIds.forEach(identifier -> {
            String dimensionKey = identifier.toLanguageKey("options.dimensions");
            OptionGroupBuilder dimensionFogGroup = builder.createOptionGroup().setName(SodiumExtraConfig.translatableName(identifier, "dimensions")).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("fog." + dimensionKey)), SodiumExtraConfig::isDimensionFogOptionEnabled, "sodium-extra.option.fog_distance", "sodium-extra.option.fog.tooltip").setRangeProvider(FogDistanceHelper::getFogDistanceRange, new ResourceLocation[]{FogDistanceHelper.SODIUM_RENDER_DISTANCE_OPTION_ID, ConfigState.UPDATE_ON_REBUILD}).setValueFormatter(ControlValueFormatterExtended.fogDistance()).setBinding(value -> {
                SodiumExtraClientMod.options().renderSettings.fogSettings.getOrCreateDimensionOverride((ResourceLocation)identifier).distanceChunks = value;
            }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.getDimensionFogDistance((ResourceLocation)identifier)).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("fog_start." + dimensionKey)), SodiumExtraConfig::isDimensionFogOptionEnabled, "sodium-extra.option.fog_start", "sodium-extra.option.fog_start.tooltip").setRange(0, 100, 1).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(value -> SodiumExtraConfig.setDimensionFogStart(identifier, value), () -> SodiumExtraClientMod.options().renderSettings.fogSettings.getDimensionFogStart((ResourceLocation)identifier)).setDefaultValue(Integer.valueOf(100))).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createEnumOption(SodiumExtraConfig.id("fog_shape." + dimensionKey), SodiumExtraGameOptions.FogShapeMode.class), SodiumExtraConfig::isDimensionFogShapeOptionEnabled, "sodium-extra.option.fog_shape", "sodium-extra.option.fog_shape.tooltip").setAllowedValues(SodiumExtraGameOptions.FogShapeMode.getAvailableOptions()).setBinding(value -> SodiumExtraConfig.setDimensionFogShape(identifier, value), () -> SodiumExtraClientMod.options().renderSettings.fogSettings.getDimensionFogShape((ResourceLocation)identifier)).setDefaultValue((Enum)SodiumExtraGameOptions.FogShapeMode.VANILLA)).addOption((OptionBuilder)SodiumExtraConfig.fogOption(builder.createIntegerOption(SodiumExtraConfig.id("cloud_fog." + dimensionKey)), SodiumExtraConfig::isDimensionFogOptionEnabled, "sodium-extra.option.cloud_fog", "sodium-extra.option.cloud_fog.tooltip").setRange(0, 100, 1).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(value -> SodiumExtraConfig.setDimensionCloudFog(identifier, value), () -> SodiumExtraClientMod.options().renderSettings.fogSettings.getDimensionCloudFogPercent((ResourceLocation)identifier)).setDefaultValue(Integer.valueOf(100)));
            page.addOptionGroup(dimensionFogGroup);
        });
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(PROTECTED_GAMEPLAY_FOG_OPTION_ID).setEnabledProvider(SodiumExtraConfig::isAdvancedFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.enabledWhenAllowed = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.enabledWhenAllowed).setDefaultValue(Boolean.valueOf(false))).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("protected_gameplay_fog.blindness")).setEnabledProvider(SodiumExtraConfig::isProtectedGameplayFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, PROTECTED_GAMEPLAY_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.blindness")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.blindness.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setRange(FogDistanceHelper.getProtectedGameplayFogDistanceRange()).setValueFormatter(ControlValueFormatterExtended.protectedFogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.blindnessDistanceBlocks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.blindnessDistanceBlocks).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("protected_gameplay_fog.darkness")).setEnabledProvider(SodiumExtraConfig::isProtectedGameplayFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, PROTECTED_GAMEPLAY_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.darkness")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.darkness.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setRange(FogDistanceHelper.getProtectedGameplayFogDistanceRange()).setValueFormatter(ControlValueFormatterExtended.protectedFogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.darknessDistanceBlocks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.darknessDistanceBlocks).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("protected_gameplay_fog.lava")).setEnabledProvider(SodiumExtraConfig::isProtectedGameplayFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, PROTECTED_GAMEPLAY_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.lava")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.lava.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setRange(FogDistanceHelper.getProtectedGameplayFogDistanceRange()).setValueFormatter(ControlValueFormatterExtended.protectedFogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.lavaDistanceBlocks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.lavaDistanceBlocks).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("protected_gameplay_fog.powder_snow")).setEnabledProvider(SodiumExtraConfig::isProtectedGameplayFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, PROTECTED_GAMEPLAY_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.powder_snow")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.powder_snow.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setRange(FogDistanceHelper.getProtectedGameplayFogDistanceRange()).setValueFormatter(ControlValueFormatterExtended.protectedFogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.powderSnowDistanceBlocks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.powderSnowDistanceBlocks).setDefaultValue(Integer.valueOf(0))).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("protected_gameplay_fog.water")).setEnabledProvider(SodiumExtraConfig::isProtectedGameplayFogOptionEnabled, new ResourceLocation[]{ADVANCED_FOG_OPTION_ID, PROTECTED_GAMEPLAY_FOG_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.water")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.protected_gameplay_fog.water.tooltip")).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setRange(FogDistanceHelper.getProtectedGameplayFogDistanceRange()).setValueFormatter(ControlValueFormatterExtended.protectedFogDistance()).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.waterDistanceBlocks = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.fogSettings.protectedGameplay.waterDistanceBlocks).setDefaultValue(Integer.valueOf(0))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("light_updates")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.light_updates").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.light_updates")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.light_updates.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.lightUpdates = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.lightUpdates).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("item_frame")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.entity").isEnabled()).setName(SodiumExtraConfig.parseVanillaString("entity.minecraft.item_frame")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.item_frames.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.itemFrame = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.itemFrame).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("armor_stands")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.entity").isEnabled()).setName(SodiumExtraConfig.parseVanillaString("entity.minecraft.armor_stand")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.armor_stands.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.armorStand = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.armorStand).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("paintings")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.entity").isEnabled()).setName(SodiumExtraConfig.parseVanillaString("entity.minecraft.painting")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.paintings.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.painting = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.painting).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("beacon_beam")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.block.entity").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.beacon_beam")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.beacon_beam.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.beaconBeam = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.beaconBeam).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("limit_beacon_beam_height")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.block.entity").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.limit_beacon_beam_height")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.limit_beacon_beam_height.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.limitBeaconBeamHeight = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.limitBeaconBeamHeight).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(false))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("enchanting_table_book")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.block.entity").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.enchanting_table_book")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.enchanting_table_book.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.enchantingTableBook = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.enchantingTableBook).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("piston")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.block.entity").isEnabled()).setName(SodiumExtraConfig.parseVanillaString("block.minecraft.piston")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.piston.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.piston = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.piston).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))));
        page.addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("item_frame_name_tag")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.entity").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.item_frame_name_tag")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.item_frame_name_tag.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.itemFrameNameTag = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.itemFrameNameTag).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("player_name_tag")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.render.entity").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.player_name_tag")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.player_name_tag.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().renderSettings.playerNameTag = value;
        }, () -> SodiumExtraClientMod.options().renderSettings.playerNameTag).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(true))));
        return page;
    }

    private OptionPageBuilder createExtraPage(ConfigBuilder builder) {
        return builder.createOptionPage().setName((Component)Component.translatable((String)"sodium-extra.option.extras")).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("reduce_resolution_on_mac")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.reduce_resolution_on_mac").isEnabled() && System.getProperty("os.name").toLowerCase().contains("mac")).setName((Component)Component.translatable((String)"sodium-extra.option.reduce_resolution_on_mac")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.reduce_resolution_on_mac.tooltip")).setImpact(OptionImpact.HIGH).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.reduceResolutionOnMac = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.reduceResolutionOnMac).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(false))).addOption((OptionBuilder)builder.createBooleanOption(WAYLAND_FULLSCREEN_RESOLUTION_OPTION_ID).setEnabled(SodiumExtraConfig.isWaylandOrXWayland()).setName((Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution.tooltip")).setImpact(OptionImpact.MEDIUM).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.waylandFullscreenResolution = value;
            if (!value.booleanValue()) {
                SodiumExtraConfig.clearPreferredFullscreenVideoMode();
                SodiumExtraClientMod.disarmWaylandFullscreenResolutionRecovery();
            }
        }, () -> SodiumExtraClientMod.options().extraSettings.waylandFullscreenResolution).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setDefaultValue(Boolean.valueOf(false)))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createEnumOption(SodiumExtraConfig.id("overlay_corner"), SodiumExtraGameOptions.OverlayCorner.class).setName((Component)Component.translatable((String)"sodium-extra.option.overlay_corner")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.overlay_corner.tooltip")).setDefaultValue((Enum)SodiumExtraGameOptions.OverlayCorner.TOP_LEFT).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.overlayCorner = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.overlayCorner).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createEnumOption(SodiumExtraConfig.id("text_contrast"), SodiumExtraGameOptions.TextContrast.class).setName((Component)Component.translatable((String)"sodium-extra.option.text_contrast")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.text_contrast.tooltip")).setDefaultValue((Enum)SodiumExtraGameOptions.TextContrast.NONE).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.textContrast = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.textContrast).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("show_fps")).setName((Component)Component.translatable((String)"sodium-extra.option.show_fps")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.show_fps.tooltip")).setDefaultValue(Boolean.valueOf(false)).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.showFps = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.showFps).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("show_fps_extended")).setName((Component)Component.translatable((String)"sodium-extra.option.show_fps_extended")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.show_fps_extended.tooltip")).setDefaultValue(Boolean.valueOf(true)).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.showFPSExtended = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.showFPSExtended).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("show_coordinates")).setName((Component)Component.translatable((String)"sodium-extra.option.show_coordinates")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.show_coordinates.tooltip")).setDefaultValue(Boolean.valueOf(false)).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.showCoords = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.showCoords).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(CLOUD_HEIGHT_OVERRIDE_OPTION_ID).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.cloud").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.cloud_height_override")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.cloud_height_override.tooltip")).setDefaultValue(Boolean.valueOf(false)).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.cloudHeightOverride = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.cloudHeightOverride).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("cloud_height")).setEnabledProvider(SodiumExtraConfig::isCloudHeightOptionEnabled, new ResourceLocation[]{CLOUD_HEIGHT_OVERRIDE_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.cloud_height")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.cloud_height.tooltip")).setRange(-64, 319, 1).setDefaultValue(Integer.valueOf(192)).setValueFormatter(ControlValueFormatterImpls.number()).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.cloudHeight = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.cloudHeight).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("cloud_distance")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.sodium.cloud").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.cloud_distance")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.cloud_distance.tooltip")).setRange(100, 300, 10).setDefaultValue(Integer.valueOf(100)).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.cloudDistance = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.cloudDistance).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("advanced_item_tooltips")).setName((Component)Component.translatable((String)"sodium-extra.option.advanced_item_tooltips")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.advanced_item_tooltips.tooltip")).setStorageHandler(() -> Minecraft.getInstance().options.save()).setBinding(value -> {
            Minecraft.getInstance().options.advancedItemTooltips = value;
        }, () -> Minecraft.getInstance().options.advancedItemTooltips).setDefaultValue(Boolean.valueOf(false)))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("toasts")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.toasts").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.toasts")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.toasts.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.toasts = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.toasts).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("advancement_toast")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.toasts").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.advancement_toast")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.advancement_toast.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.advancementToast = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.advancementToast).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("recipe_toast")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.toasts").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.recipe_toast")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.recipe_toast.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.recipeToast = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.recipeToast).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("system_toast")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.toasts").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.system_toast")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.system_toast.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.systemToast = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.systemToast).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("tutorial_toast")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.toasts").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.tutorial_toast")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.tutorial_toast.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.tutorialToast = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.tutorialToast).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("instant_sneak")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.instant_sneak").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.instant_sneak")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.instant_sneak.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.instantSneak = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.instantSneak).setDefaultValue(Boolean.valueOf(false)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("prevent_shaders")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.prevent_shaders").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.prevent_shaders")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.prevent_shaders.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.preventShaders = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.preventShaders).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_RENDERER_RELOAD}).setDefaultValue(Boolean.valueOf(false)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(PANINI_PROJECTION_OPTION_ID).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.panini_projection").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.panini_projection")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.panini_projection.tooltip")).setImpact(OptionImpact.MEDIUM).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.paniniProjection = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.paniniProjection).setDefaultValue(Boolean.valueOf(false)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("panini_projection_strength")).setEnabledProvider(SodiumExtraConfig::isPaniniProjectionOptionEnabled, new ResourceLocation[]{PANINI_PROJECTION_OPTION_ID}).setControlHiddenWhenDisabled(false).setName((Component)Component.translatable((String)"sodium-extra.option.panini_projection_strength")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.panini_projection_strength.tooltip")).setRange(0, 100, 1).setValueFormatter(ControlValueFormatterImpls.percentage()).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.paniniProjectionStrength = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.paniniProjectionStrength).setDefaultValue(Integer.valueOf(25)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createBooleanOption(SodiumExtraConfig.id("steady_debug_hud")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.steady_debug_hud").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.steady_debug_hud")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.steady_debug_hud.tooltip")).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.steadyDebugHud = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.steadyDebugHud).setDefaultValue(Boolean.valueOf(true)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options())).addOption((OptionBuilder)builder.createIntegerOption(SodiumExtraConfig.id("steady_debug_hud_refresh_interval")).setEnabled(SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.steady_debug_hud").isEnabled()).setName((Component)Component.translatable((String)"sodium-extra.option.steady_debug_hud_refresh_interval")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.steady_debug_hud_refresh_interval.tooltip")).setRange(1, 20, 1).setValueFormatter(ControlValueFormatterExtended.ticks()).setDefaultValue(Integer.valueOf(1)).setStorageHandler((StorageEventHandler)SodiumExtraClientMod.options()).setBinding(value -> {
            SodiumExtraClientMod.options().extraSettings.steadyDebugHudRefreshInterval = value;
        }, () -> SodiumExtraClientMod.options().extraSettings.steadyDebugHudRefreshInterval)));
    }

    private IntegerOptionBuilder createFullscreenResolutionOption(ConfigBuilder builder) {
        return builder.createIntegerOption(SODIUM_FULLSCREEN_RESOLUTION_OPTION_ID).setStorageHandler(() -> Minecraft.getInstance().options.save()).setName((Component)Component.translatable((String)"options.fullscreen.resolution")).setTooltip((Component)Component.translatable((String)"sodium-extra.option.resolution.tooltip")).setValueFormatter(ControlValueFormatterExtended.resolution()).setValidator((SteppedValidator)new FullscreenResolutionRange()).setDefaultValue(Integer.valueOf(0)).setBinding(SodiumExtraConfig::setFullscreenResolution, SodiumExtraConfig::getFullscreenResolution).setFlags(new OptionFlag[]{OptionFlag.REQUIRES_VIDEOMODE_RELOAD}).setEnabledProvider(SodiumExtraConfig::isFullscreenResolutionOptionEnabled, new ResourceLocation[]{SODIUM_FULLSCREEN_OPTION_ID, WAYLAND_FULLSCREEN_RESOLUTION_OPTION_ID, ConfigState.UPDATE_ON_REBUILD});
    }

    private EnumOptionBuilder<SodiumExtraGameOptions.VerticalSyncOption> createVerticalSyncOption(ConfigBuilder builder) {
        EnumSet<SodiumExtraGameOptions.VerticalSyncOption> allowedValues = EnumSet.of(SodiumExtraGameOptions.VerticalSyncOption.OFF, SodiumExtraGameOptions.VerticalSyncOption.ON);
        if (SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.adaptive_sync").isEnabled() && SodiumExtraGameOptions.VerticalSyncOption.isAdaptiveSyncSupported()) {
            allowedValues.add(SodiumExtraGameOptions.VerticalSyncOption.ADAPTIVE);
        }
        return builder.createEnumOption(SODIUM_VSYNC_OPTION_ID, SodiumExtraGameOptions.VerticalSyncOption.class).setDefaultValue((Enum)SodiumExtraGameOptions.VerticalSyncOption.ON).setAllowedValues(allowedValues).setName((Component)Component.translatable((String)"options.vsync")).setTooltip((Component)Component.literal((String)(Component.translatable((String)"sodium.options.v_sync.tooltip").getString() + "\n- " + Component.translatable((String)"sodium-extra.option.use_adaptive_sync.name").getString() + ": " + Component.translatable((String)"sodium-extra.option.use_adaptive_sync.tooltip").getString()))).setBinding(value -> {
            switch (value) {
                case OFF: {
                    SodiumExtraClientMod.options().extraSettings.useAdaptiveSync = false;
                    Minecraft.getInstance().options.enableVsync().set((Object)false);
                    break;
                }
                case ON: {
                    SodiumExtraClientMod.options().extraSettings.useAdaptiveSync = false;
                    Minecraft.getInstance().options.enableVsync().set((Object)true);
                    break;
                }
                case ADAPTIVE: {
                    SodiumExtraClientMod.options().extraSettings.useAdaptiveSync = true;
                    Minecraft.getInstance().options.enableVsync().set((Object)true);
                }
            }
        }, () -> {
            boolean adaptive;
            boolean vsync = (Boolean)Minecraft.getInstance().options.enableVsync().get();
            boolean bl = adaptive = SodiumExtraClientMod.options().extraSettings.useAdaptiveSync && SodiumExtraClientMod.mixinConfig().getOptions().get("mixin.adaptive_sync").isEnabled() && SodiumExtraGameOptions.VerticalSyncOption.isAdaptiveSyncSupported();
            if (!vsync) {
                return SodiumExtraGameOptions.VerticalSyncOption.OFF;
            }
            return adaptive ? SodiumExtraGameOptions.VerticalSyncOption.ADAPTIVE : SodiumExtraGameOptions.VerticalSyncOption.ON;
        }).setStorageHandler(() -> {
            SodiumExtraClientMod.options().afterSave();
            Minecraft.getInstance().options.save();
        });
    }

    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions().setIcon(ResourceLocation.parse((String)"sodium-extra:textures/icon.png")).addPage((PageBuilder)this.createAnimationsPage(builder)).addPage((PageBuilder)this.createParticlesPage(builder)).addPage((PageBuilder)this.createDetailsPage(builder)).addPage((PageBuilder)this.createRenderPage(builder)).addPage((PageBuilder)this.createExtraPage(builder)).registerOptionReplacement(SODIUM_FULLSCREEN_RESOLUTION_OPTION_ID, (OptionBuilder)this.createFullscreenResolutionOption(builder)).registerOptionReplacement(SODIUM_VSYNC_OPTION_ID, this.createVerticalSyncOption(builder));
    }
}

