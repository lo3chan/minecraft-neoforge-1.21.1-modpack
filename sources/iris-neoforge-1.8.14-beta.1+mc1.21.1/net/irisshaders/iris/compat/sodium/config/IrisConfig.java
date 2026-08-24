package net.irisshaders.iris.compat.sodium.config;

import java.io.IOException;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.pathways.colorspace.ColorSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IrisConfig implements ConfigEntryPoint {
   public static final ResourceLocation MONO = ResourceLocation.fromNamespaceAndPath("iris", "textures/gui/config-icon-mono.png");
   public static final ResourceLocation COLOR = ResourceLocation.fromNamespaceAndPath("iris", "textures/gui/config-icon.png");

   public void registerConfigLate(ConfigBuilder builder) {
      builder.registerOwnModOptions()
         .setName("Iris")
         .setIcon(MONO)
         .setColorTheme(builder.createColorTheme().setBaseThemeRGB(-698654))
         .setVersion(Iris.getVersionSimple())
         .addPage(
            builder.createExternalPage()
               .setName(Component.translatable("options.iris.shaderPackSelection.title"))
               .setScreenConsumer(i -> Minecraft.getInstance().setScreen(new ShaderPackScreen(i)))
         )
         .addPage(
            builder.createOptionPage()
               .setName(Component.literal("Settings"))
               .addOptionGroup(
                  builder.createOptionGroup()
                     .addOption(
                        builder.createExternalButtonOption(ResourceLocation.fromNamespaceAndPath("iris", "settings"))
                           .setTooltip(Component.literal("Packs"))
                           .setName(Component.translatable("options.iris.shaderPackList"))
                           .setScreenConsumer(i -> Minecraft.getInstance().setScreen(new ShaderPackScreen(i)))
                     )
               )
               .addOptionGroup(
                  builder.createOptionGroup()
                     .addOption(
                        builder.createEnumOption(ResourceLocation.fromNamespaceAndPath("iris", "color_space"), ColorSpace.class)
                           .setBinding(i -> IrisVideoSettings.colorSpace = i, () -> IrisVideoSettings.colorSpace)
                           .setName(Component.translatable("options.iris.colorSpace"))
                           .setDefaultValue(ColorSpace.SRGB)
                           .setTooltip(Component.translatable("options.iris.colorSpace.sodium_tooltip"))
                           .setStorageHandler(() -> {
                              try {
                                 Iris.getIrisConfig().save();
                              } catch (IOException var1x) {
                                 throw new RuntimeException(var1x);
                              }
                           })
                           .setElementNameProvider(i -> Component.literal(i.name()))
                     )
                     .addOption(
                        builder.createIntegerOption(ResourceLocation.fromNamespaceAndPath("iris", "shadow_distance"))
                           .setDefaultValue(32)
                           .setBinding(
                              value -> IrisVideoSettings.shadowDistance = value,
                              () -> IrisVideoSettings.getOverriddenShadowDistance(IrisVideoSettings.shadowDistance)
                           )
                           .setName(Component.translatable("options.iris.shadowDistance"))
                           .setTooltip(
                              i -> !IrisVideoSettings.isShadowDistanceSliderEnabled()
                                 ? Component.translatable("options.iris.shadowDistance.disabled")
                                 : Component.translatable("options.iris.shadowDistance.sodium_tooltip")
                           )
                           .setValueFormatter(
                              ControlValueFormatterImpls.quantityOrDisabled(
                                 i -> Component.translatable("options.chunks", new Object[]{i}), Component.literal("None")
                              )
                           )
                           .setEnabledProvider(i -> IrisVideoSettings.isShadowDistanceSliderEnabled(), new ResourceLocation[]{ConfigState.UPDATE_ON_REBUILD})
                           .setStorageHandler(() -> {
                              try {
                                 Iris.getIrisConfig().save();
                              } catch (IOException var1x) {
                                 throw new RuntimeException(var1x);
                              }
                           })
                           .setRange(new Range(0, 32, 1))
                           .setImpact(OptionImpact.HIGH)
                     )
               )
         );
   }
}
