package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WeatherSettingsScreen extends LegacyOptionsSubScreen {
   private final CycleOption<Boolean> PHYSICS_WIND = CycleOption.createOnOff(
      "physicsmod.menu.weather.windphysics", gameOptions -> ConfigClient.windPhysics, (gameOptions, option, value) -> {
         ConfigClient.windPhysics = value;
         ConfigClient.save();
      }
   );
   private final CycleOption<Boolean> PHYSICS_WEATHER_PARTICLES = CycleOption.createOnOff(
      "physicsmod.menu.weather.particles", gameOptions -> ConfigClient.weatherParticles, (gameOptions, option, value) -> {
         ConfigClient.weatherParticles = value;
         ConfigClient.save();
      }
   );
   private static final ProgressOption PHYSICS_WEATHER_PARTICLES_RAIN_AMOUNT = new ProgressOption(
      "physicsmod.menu.weather.particlesamountrain",
      1.0,
      30.0,
      0.01F,
      gameOptions -> (double)ConfigClient.weatherRainParticleAmount,
      (gameOptions, value) -> {
         ConfigClient.weatherRainParticleAmount = value.intValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.particlesamountrain", String.format("%.0f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.particlesamountrain.info")
   );
   private static final ProgressOption PHYSICS_WEATHER_PARTICLES_THUNDER_AMOUNT = new ProgressOption(
      "physicsmod.menu.weather.particlesamountthunder",
      1.0,
      30.0,
      0.01F,
      gameOptions -> (double)ConfigClient.weatherThunderParticleAmount,
      (gameOptions, value) -> {
         ConfigClient.weatherThunderParticleAmount = value.intValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.particlesamountthunder", String.format("%.0f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.particlesamountthunder.info")
   );
   private static final ProgressOption WEATHER_CLEAR_STRENGTH = new ProgressOption(
      "physicsmod.menu.weather.clearstrength",
      0.0,
      4.0,
      0.01F,
      gameOptions -> (double)ConfigClient.weatherClearStrength,
      (gameOptions, value) -> {
         ConfigClient.weatherClearStrength = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.clearstrength", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.rainstrength.info")
   );
   private static final ProgressOption WEATHER_RAIN_STRENGTH = new ProgressOption(
      "physicsmod.menu.weather.rainstrength",
      0.0,
      4.0,
      0.01F,
      gameOptions -> (double)ConfigClient.weatherRainStrength,
      (gameOptions, value) -> {
         ConfigClient.weatherRainStrength = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.rainstrength", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.rainstrength.info")
   );
   private static final ProgressOption WEATHER_THUNDER_STRENGTH = new ProgressOption(
      "physicsmod.menu.weather.thunderstrength",
      0.0,
      4.0,
      0.01F,
      gameOptions -> (double)ConfigClient.weatherThunderStrength,
      (gameOptions, value) -> {
         ConfigClient.weatherThunderStrength = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.thunderstrength", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.rainstrength.info")
   );
   private static final ProgressOption WEATHER_RAIN_OPACITY = new ProgressOption(
      "physicsmod.menu.weather.rainopacity",
      0.0,
      1.0,
      0.01F,
      gameOptions -> (double)ConfigClient.particleRainOpacity,
      (gameOptions, value) -> {
         ConfigClient.particleRainOpacity = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.rainopacity", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.opacity.info")
   );
   private static final ProgressOption WEATHER_SNOW_OPACITY = new ProgressOption(
      "physicsmod.menu.weather.snowopacity",
      0.0,
      1.0,
      0.01F,
      gameOptions -> (double)ConfigClient.particleSnowOpacity,
      (gameOptions, value) -> {
         ConfigClient.particleSnowOpacity = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.snowopacity", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.opacity.info")
   );
   private static final ProgressOption WEATHER_DUST_OPACITY = new ProgressOption(
      "physicsmod.menu.weather.dustopacity",
      0.0,
      1.0,
      0.01F,
      gameOptions -> (double)ConfigClient.particleDustOpacity,
      (gameOptions, value) -> {
         ConfigClient.particleDustOpacity = value.floatValue();
         ConfigClient.save();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.weather.dustopacity", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.weather.opacity.info")
   );
   private LegacyOptionsList list;

   public WeatherSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.weather.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addSmall(this.PHYSICS_WIND, this.PHYSICS_WEATHER_PARTICLES);
      this.list.addBig(PHYSICS_WEATHER_PARTICLES_RAIN_AMOUNT);
      this.list.addBig(PHYSICS_WEATHER_PARTICLES_THUNDER_AMOUNT);
      this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.weather.strength")));
      this.list.addBig(WEATHER_CLEAR_STRENGTH);
      this.list.addBig(WEATHER_RAIN_STRENGTH);
      this.list.addBig(WEATHER_THUNDER_STRENGTH);
      this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.weather.opacity")));
      this.list.addBig(WEATHER_RAIN_OPACITY);
      this.list.addBig(WEATHER_SNOW_OPACITY);
      this.list.addBig(WEATHER_DUST_OPACITY);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen))
      );
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
               Language.getInstance().getOrDefault("physicsmod.menu.weather.reset"),
               this,
               widget -> this.addRenderableWidget(widget),
               widget -> this.removeWidget(widget),
               response -> {
                  if (response == PopupWidget.PopupResponse.YES) {
                     ConfigClient.resetWeatherSettings();
                     this.list.children().clear();
                     this.minecraft.setScreen(new WeatherSettingsScreen(this.lastScreen, this.options));
                  }
               }
            )
         )
      );
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.render(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      super.render(guiGraphics, mouseX, mouseY, delta);
      BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
   }

   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }
}
