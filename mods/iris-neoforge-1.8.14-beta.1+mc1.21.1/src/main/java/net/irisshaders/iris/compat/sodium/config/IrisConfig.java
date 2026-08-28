/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint
 *  net.caffeinemc.mods.sodium.api.config.ConfigState
 *  net.caffeinemc.mods.sodium.api.config.option.OptionImpact
 *  net.caffeinemc.mods.sodium.api.config.option.Range
 *  net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.PageBuilder
 *  net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package net.irisshaders.iris.compat.sodium.config;

import java.io.IOException;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.PageBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IrisConfig
implements ConfigEntryPoint {
    public static final ResourceLocation MONO = ResourceLocation.fromNamespaceAndPath((String)"iris", (String)"textures/gui/config-icon-mono.png");
    public static final ResourceLocation COLOR = ResourceLocation.fromNamespaceAndPath((String)"iris", (String)"textures/gui/config-icon.png");

    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions().setName("Iris").setIcon(MONO).setColorTheme(builder.createColorTheme().setBaseThemeRGB(-698654)).setVersion(Iris.getVersionSimple()).addPage((PageBuilder)builder.createExternalPage().setName((Component)Component.translatable((String)"options.iris.shaderPackSelection.title")).setScreenConsumer(i -> Minecraft.getInstance().setScreen((Screen)new ShaderPackScreen((Screen)i)))).addPage((PageBuilder)builder.createOptionPage().setName((Component)Component.literal((String)"Settings")).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createExternalButtonOption(ResourceLocation.fromNamespaceAndPath((String)"iris", (String)"settings")).setTooltip((Component)Component.literal((String)"Packs")).setName((Component)Component.translatable((String)"options.iris.shaderPackList")).setScreenConsumer(i -> Minecraft.getInstance().setScreen((Screen)new ShaderPackScreen((Screen)i))))).addOptionGroup(builder.createOptionGroup().addOption((OptionBuilder)builder.createEnumOption(ResourceLocation.fromNamespaceAndPath((String)"iris", (String)"color_space"), ColorSpace.class).setBinding(i -> {
            IrisVideoSettings.colorSpace = i;
        }, () -> IrisVideoSettings.colorSpace).setName((Component)Component.translatable((String)"options.iris.colorSpace")).setDefaultValue((Enum)ColorSpace.SRGB).setTooltip((Component)Component.translatable((String)"options.iris.colorSpace.sodium_tooltip")).setStorageHandler(() -> {
            try {
                Iris.getIrisConfig().save();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).setElementNameProvider(i -> Component.literal((String)i.name()))).addOption((OptionBuilder)builder.createIntegerOption(ResourceLocation.fromNamespaceAndPath((String)"iris", (String)"shadow_distance")).setDefaultValue(Integer.valueOf(32)).setBinding(value -> {
            IrisVideoSettings.shadowDistance = value;
        }, () -> IrisVideoSettings.getOverriddenShadowDistance(IrisVideoSettings.shadowDistance)).setName((Component)Component.translatable((String)"options.iris.shadowDistance")).setTooltip(i -> {
            if (!IrisVideoSettings.isShadowDistanceSliderEnabled()) {
                return Component.translatable((String)"options.iris.shadowDistance.disabled");
            }
            return Component.translatable((String)"options.iris.shadowDistance.sodium_tooltip");
        }).setValueFormatter(ControlValueFormatterImpls.quantityOrDisabled(i -> Component.translatable((String)"options.chunks", (Object[])new Object[]{i}), (Component)Component.literal((String)"None"))).setEnabledProvider(i -> IrisVideoSettings.isShadowDistanceSliderEnabled(), new ResourceLocation[]{ConfigState.UPDATE_ON_REBUILD}).setStorageHandler(() -> {
            try {
                Iris.getIrisConfig().save();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).setRange(new Range(0, 32, 1)).setImpact(OptionImpact.HIGH))));
    }
}

