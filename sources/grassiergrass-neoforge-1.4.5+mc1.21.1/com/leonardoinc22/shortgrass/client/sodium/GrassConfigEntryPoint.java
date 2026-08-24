package com.leonardoinc22.shortgrass.client.sodium;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@ConfigEntryPointForge("grassiergrass")
public final class GrassConfigEntryPoint implements ConfigEntryPoint {
   private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath("grassiergrass", "icon.png");
   private static final StorageEventHandler STORAGE = GrassConfigEntryPoint::apply;

   public void registerConfigLate(ConfigBuilder builder) {
      OptionGroupBuilder grassBladesGroup = builder.createOptionGroup()
         .setName(Component.translatable("grassiergrass.configuration.category.grassBlades"))
         .addOption(
            builder.createIntegerOption(id("blades_per_block"))
               .setName(Component.translatable("grassiergrass.configuration.bladesPerBlock"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladesPerBlock.tooltip"))
               .setDefaultValue(48)
               .setRange(1, 64, 1)
               .setValueFormatter(v -> Component.literal(Integer.toString(v)))
               .setBinding(v -> GrassConfig.bladesPerBlock = v, () -> GrassConfig.bladesPerBlock)
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("grass_sparsity"))
               .setName(Component.translatable("grassiergrass.configuration.grassSparsity"))
               .setTooltip(Component.translatable("grassiergrass.configuration.grassSparsity.tooltip"))
               .setDefaultValue(0)
               .setRange(0, 100, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2f", v / 100.0F)))
               .setBinding(v -> GrassConfig.setGrassSparsity(v.intValue() / 100.0F), () -> Math.round(GrassConfig.grassSparsity() * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_height"))
               .setName(Component.translatable("grassiergrass.configuration.bladeHeight"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeHeight.tooltip"))
               .setDefaultValue(35)
               .setRange(10, 130, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2f", v / 100.0F)))
               .setBinding(v -> GrassConfig.bladeHeight = v.intValue() / 100.0F, () -> Math.round(GrassConfig.bladeHeight * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("height_variation"))
               .setName(Component.translatable("grassiergrass.configuration.heightVariation"))
               .setTooltip(Component.translatable("grassiergrass.configuration.heightVariation.tooltip"))
               .setDefaultValue(100)
               .setRange(0, 500, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.heightVariation = v.intValue() / 100.0F, () -> Math.round(GrassConfig.heightVariation * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_width"))
               .setName(Component.translatable("grassiergrass.configuration.bladeWidth"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeWidth.tooltip"))
               .setDefaultValue(120)
               .setRange(25, 200, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.bladeWidth = v.intValue() / 100.0F, () -> Math.round(GrassConfig.bladeWidth * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("render_radius"))
               .setName(Component.translatable("grassiergrass.configuration.renderRadius"))
               .setTooltip(Component.translatable("grassiergrass.configuration.renderRadius.tooltip"))
               .setDefaultValue(100)
               .setRange(16, 160, 8)
               .setValueFormatter(v -> Component.literal(v + " blocks"))
               .setBinding(v -> GrassConfig.renderRadius = GrassConfig.clampRenderRadius(v), () -> GrassConfig.renderRadius)
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("dynamic_wind_speed_limit"))
               .setName(Component.translatable("grassiergrass.configuration.dynamicWindSpeedLimit"))
               .setTooltip(Component.translatable("grassiergrass.configuration.dynamicWindSpeedLimit.tooltip"))
               .setDefaultValue(100)
               .setRange(0, 100, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.setDynamicWindSpeedLimit(v.intValue() / 100.0F), () -> Math.round(GrassConfig.dynamicWindSpeedLimit() * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createEnumOption(id("grass_style"), GrassConfig.GrassStyle.class)
               .setName(Component.translatable("grassiergrass.configuration.grassStyle"))
               .setTooltip(Component.translatable("grassiergrass.configuration.grassStyle.tooltip"))
               .setDefaultValue(GrassConfig.GrassStyle.SEGMENTED)
               .setElementNameProvider(v -> Component.literal(v == GrassConfig.GrassStyle.TAPERED ? "Tapered" : "Segmented"))
               .setBinding(v -> GrassConfig.grassStyle = v, () -> GrassConfig.grassStyle)
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createBooleanOption(id("grass_plants_as_blades"))
               .setName(Component.translatable("grassiergrass.configuration.grassPlantsAsBlades"))
               .setTooltip(Component.translatable("grassiergrass.configuration.grassPlantsAsBlades.tooltip"))
               .setDefaultValue(true)
               .setBinding(v -> GrassConfig.grassPlantsAsBlades = v, () -> GrassConfig.grassPlantsAsBlades)
               .setStorageHandler(STORAGE)
         );
      OptionGroupBuilder worldEffectsGroup = builder.createOptionGroup()
         .setName(Component.translatable("grassiergrass.configuration.category.worldEffects"))
         .addOption(
            builder.createBooleanOption(id("dense_flowers"))
               .setName(Component.translatable("grassiergrass.configuration.denseFlowers"))
               .setTooltip(Component.translatable("grassiergrass.configuration.denseFlowers.tooltip"))
               .setDefaultValue(true)
               .setBinding(v -> GrassConfig.denseFlowers = v, () -> GrassConfig.denseFlowers)
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createBooleanOption(id("blade_particles"))
               .setName(Component.translatable("grassiergrass.configuration.bladeParticles"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeParticles.tooltip"))
               .setDefaultValue(true)
               .setBinding(v -> GrassConfig.bladeParticles = v, () -> GrassConfig.bladeParticles)
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createBooleanOption(id("grass_through_snow"))
               .setName(Component.translatable("grassiergrass.configuration.grassThroughSnow"))
               .setTooltip(Component.translatable("grassiergrass.configuration.grassThroughSnow.tooltip"))
               .setDefaultValue(false)
               .setBinding(v -> GrassConfig.grassThroughSnow = v, () -> GrassConfig.grassThroughSnow)
               .setStorageHandler(STORAGE)
         );
      OptionGroupBuilder colorLightingGroup = builder.createOptionGroup()
         .setName(Component.translatable("grassiergrass.configuration.category.colorLighting"))
         .addOption(
            builder.createIntegerOption(id("grass_brightness"))
               .setName(Component.translatable("grassiergrass.configuration.grassBrightness"))
               .setTooltip(Component.translatable("grassiergrass.configuration.grassBrightness.tooltip"))
               .setDefaultValue(100)
               .setRange(25, 200, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.grassBrightness = v.intValue() / 100.0F, () -> Math.round(GrassConfig.grassBrightness * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_hue_jitter"))
               .setName(Component.translatable("grassiergrass.configuration.bladeHueJitter"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeHueJitter.tooltip"))
               .setDefaultValue(15)
               .setRange(0, Math.round(30.0F), 1)
               .setValueFormatter(v -> Component.literal(v + " degrees"))
               .setBinding(v -> GrassConfig.setBladeHueJitterDegrees(v.intValue()), () -> Math.round(GrassConfig.bladeHueJitterDegrees()))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_gradient_bottom"))
               .setName(Component.translatable("grassiergrass.configuration.bladeGradientBottom"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeGradientBottom.tooltip"))
               .setDefaultValue(100)
               .setRange(25, 200, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.bladeGradientBottom = v.intValue() / 100.0F, () -> Math.round(GrassConfig.bladeGradientBottom * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_gradient_top"))
               .setName(Component.translatable("grassiergrass.configuration.bladeGradientTop"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeGradientTop.tooltip"))
               .setDefaultValue(110)
               .setRange(25, 200, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2fx", v / 100.0F)))
               .setBinding(v -> GrassConfig.bladeGradientTop = v.intValue() / 100.0F, () -> Math.round(GrassConfig.bladeGradientTop * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createIntegerOption(id("blade_gradient_curve"))
               .setName(Component.translatable("grassiergrass.configuration.bladeGradientCurve"))
               .setTooltip(Component.translatable("grassiergrass.configuration.bladeGradientCurve.tooltip"))
               .setDefaultValue(150)
               .setRange(10, 400, 5)
               .setValueFormatter(v -> Component.literal(String.format("%.2f", v / 100.0F)))
               .setBinding(v -> GrassConfig.bladeGradientCurve = v.intValue() / 100.0F, () -> Math.round(GrassConfig.bladeGradientCurve * 100.0F))
               .setStorageHandler(STORAGE)
         )
         .addOption(
            builder.createBooleanOption(id("shader_pack_shadows"))
               .setName(Component.translatable("grassiergrass.configuration.shaderPackShadows"))
               .setTooltip(Component.translatable("grassiergrass.configuration.shaderPackShadows.tooltip"))
               .setDefaultValue(false)
               .setBinding(v -> GrassConfig.shaderPackShadows = v, () -> GrassConfig.shaderPackShadows)
               .setStorageHandler(STORAGE)
         );
      OptionPageBuilder page = builder.createOptionPage()
         .setName(Component.literal("Grassier Grass"))
         .addOptionGroup(grassBladesGroup)
         .addOptionGroup(worldEffectsGroup)
         .addOptionGroup(colorLightingGroup);
      builder.registerOwnModOptions().setName("Grassier Grass").setVersion("1.4.5").setNonTintedIcon(ICON).addPage(page);
   }

   private static void apply() {
      GrassConfig.save();
      GrassRenderPass.flushCache();
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("grassiergrass", path);
   }
}
