/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge
 *  net.caffeinemc.mods.sodium.api.config.StorageEventHandler
 *  net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.PageBuilder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package com.leonardoinc22.shortgrass.client.sodium;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.PageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@ConfigEntryPointForge(value="grassiergrass")
public final class GrassConfigEntryPoint
implements ConfigEntryPoint {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"icon.png");
    private static final StorageEventHandler STORAGE = GrassConfigEntryPoint::apply;

    public void registerConfigLate(ConfigBuilder builder) {
        OptionGroupBuilder grassBladesGroup = builder.createOptionGroup().setName((Component)Component.translatable((String)"grassiergrass.configuration.category.grassBlades")).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blades_per_block")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladesPerBlock")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladesPerBlock.tooltip")).setDefaultValue(Integer.valueOf(48)).setRange(1, 64, 1).setValueFormatter(v -> Component.literal((String)Integer.toString(v))).setBinding(v -> {
            GrassConfig.bladesPerBlock = v;
        }, () -> GrassConfig.bladesPerBlock).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("grass_sparsity")).setName((Component)Component.translatable((String)"grassiergrass.configuration.grassSparsity")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.grassSparsity.tooltip")).setDefaultValue(Integer.valueOf(0)).setRange(0, 100, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2f", Float.valueOf((float)v / 100.0f)))).setBinding(v -> GrassConfig.setGrassSparsity((float)v.intValue() / 100.0f), () -> Math.round(GrassConfig.grassSparsity() * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_height")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeHeight")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeHeight.tooltip")).setDefaultValue(Integer.valueOf(35)).setRange(10, 130, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2f", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.bladeHeight = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.bladeHeight * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("height_variation")).setName((Component)Component.translatable((String)"grassiergrass.configuration.heightVariation")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.heightVariation.tooltip")).setDefaultValue(Integer.valueOf(100)).setRange(0, 500, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.heightVariation = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.heightVariation * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_width")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeWidth")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeWidth.tooltip")).setDefaultValue(Integer.valueOf(120)).setRange(25, 200, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.bladeWidth = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.bladeWidth * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("render_radius")).setName((Component)Component.translatable((String)"grassiergrass.configuration.renderRadius")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.renderRadius.tooltip")).setDefaultValue(Integer.valueOf(100)).setRange(16, 160, 8).setValueFormatter(v -> Component.literal((String)(v + " blocks"))).setBinding(v -> {
            GrassConfig.renderRadius = GrassConfig.clampRenderRadius(v);
        }, () -> GrassConfig.renderRadius).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("dynamic_wind_speed_limit")).setName((Component)Component.translatable((String)"grassiergrass.configuration.dynamicWindSpeedLimit")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.dynamicWindSpeedLimit.tooltip")).setDefaultValue(Integer.valueOf(100)).setRange(0, 100, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> GrassConfig.setDynamicWindSpeedLimit((float)v.intValue() / 100.0f), () -> Math.round(GrassConfig.dynamicWindSpeedLimit() * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createEnumOption(GrassConfigEntryPoint.id("grass_style"), GrassConfig.GrassStyle.class).setName((Component)Component.translatable((String)"grassiergrass.configuration.grassStyle")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.grassStyle.tooltip")).setDefaultValue((Enum)GrassConfig.GrassStyle.SEGMENTED).setElementNameProvider(v -> Component.literal((String)(v == GrassConfig.GrassStyle.TAPERED ? "Tapered" : "Segmented"))).setBinding(v -> {
            GrassConfig.grassStyle = v;
        }, () -> GrassConfig.grassStyle).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createBooleanOption(GrassConfigEntryPoint.id("grass_plants_as_blades")).setName((Component)Component.translatable((String)"grassiergrass.configuration.grassPlantsAsBlades")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.grassPlantsAsBlades.tooltip")).setDefaultValue(Boolean.valueOf(true)).setBinding(v -> {
            GrassConfig.grassPlantsAsBlades = v;
        }, () -> GrassConfig.grassPlantsAsBlades).setStorageHandler(STORAGE));
        OptionGroupBuilder worldEffectsGroup = builder.createOptionGroup().setName((Component)Component.translatable((String)"grassiergrass.configuration.category.worldEffects")).addOption((OptionBuilder)builder.createBooleanOption(GrassConfigEntryPoint.id("dense_flowers")).setName((Component)Component.translatable((String)"grassiergrass.configuration.denseFlowers")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.denseFlowers.tooltip")).setDefaultValue(Boolean.valueOf(true)).setBinding(v -> {
            GrassConfig.denseFlowers = v;
        }, () -> GrassConfig.denseFlowers).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createBooleanOption(GrassConfigEntryPoint.id("blade_particles")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeParticles")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeParticles.tooltip")).setDefaultValue(Boolean.valueOf(true)).setBinding(v -> {
            GrassConfig.bladeParticles = v;
        }, () -> GrassConfig.bladeParticles).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createBooleanOption(GrassConfigEntryPoint.id("grass_through_snow")).setName((Component)Component.translatable((String)"grassiergrass.configuration.grassThroughSnow")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.grassThroughSnow.tooltip")).setDefaultValue(Boolean.valueOf(false)).setBinding(v -> {
            GrassConfig.grassThroughSnow = v;
        }, () -> GrassConfig.grassThroughSnow).setStorageHandler(STORAGE));
        OptionGroupBuilder colorLightingGroup = builder.createOptionGroup().setName((Component)Component.translatable((String)"grassiergrass.configuration.category.colorLighting")).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("grass_brightness")).setName((Component)Component.translatable((String)"grassiergrass.configuration.grassBrightness")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.grassBrightness.tooltip")).setDefaultValue(Integer.valueOf(100)).setRange(25, 200, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.grassBrightness = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.grassBrightness * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_hue_jitter")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeHueJitter")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeHueJitter.tooltip")).setDefaultValue(Integer.valueOf(15)).setRange(0, Math.round(30.0f), 1).setValueFormatter(v -> Component.literal((String)(v + " degrees"))).setBinding(v -> GrassConfig.setBladeHueJitterDegrees(v.intValue()), () -> Math.round(GrassConfig.bladeHueJitterDegrees())).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_gradient_bottom")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientBottom")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientBottom.tooltip")).setDefaultValue(Integer.valueOf(100)).setRange(25, 200, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.bladeGradientBottom = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.bladeGradientBottom * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_gradient_top")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientTop")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientTop.tooltip")).setDefaultValue(Integer.valueOf(110)).setRange(25, 200, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2fx", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.bladeGradientTop = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.bladeGradientTop * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createIntegerOption(GrassConfigEntryPoint.id("blade_gradient_curve")).setName((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientCurve")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.bladeGradientCurve.tooltip")).setDefaultValue(Integer.valueOf(150)).setRange(10, 400, 5).setValueFormatter(v -> Component.literal((String)String.format("%.2f", Float.valueOf((float)v / 100.0f)))).setBinding(v -> {
            GrassConfig.bladeGradientCurve = (float)v.intValue() / 100.0f;
        }, () -> Math.round(GrassConfig.bladeGradientCurve * 100.0f)).setStorageHandler(STORAGE)).addOption((OptionBuilder)builder.createBooleanOption(GrassConfigEntryPoint.id("shader_pack_shadows")).setName((Component)Component.translatable((String)"grassiergrass.configuration.shaderPackShadows")).setTooltip((Component)Component.translatable((String)"grassiergrass.configuration.shaderPackShadows.tooltip")).setDefaultValue(Boolean.valueOf(false)).setBinding(v -> {
            GrassConfig.shaderPackShadows = v;
        }, () -> GrassConfig.shaderPackShadows).setStorageHandler(STORAGE));
        OptionPageBuilder page = builder.createOptionPage().setName((Component)Component.literal((String)"Grassier Grass")).addOptionGroup(grassBladesGroup).addOptionGroup(worldEffectsGroup).addOptionGroup(colorLightingGroup);
        builder.registerOwnModOptions().setName("Grassier Grass").setVersion("1.4.5").setNonTintedIcon(ICON).addPage((PageBuilder)page);
    }

    private static void apply() {
        GrassConfig.save();
        GrassRenderPass.flushCache();
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)path);
    }
}

