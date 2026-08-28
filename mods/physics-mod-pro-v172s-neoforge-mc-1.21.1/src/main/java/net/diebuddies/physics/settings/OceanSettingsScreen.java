/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.ocean.OceanLayer;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OceanSettingsScreen
extends LegacyOptionsSubScreen {
    private final CycleOption<Boolean> PHYSICS_OCEAN = CycleOption.createOnOff("physicsmod.menu.ocean.oceanphysics", gameOptions -> ConfigClient.oceanPhysics, (gameOptions, option, value) -> {
        ConfigClient.oceanPhysics = value;
        Minecraft.getInstance().levelRenderer.allChanged();
    });
    private final CycleOption<Boolean> PHYSICS_OCEAN_HITBOX = CycleOption.createOnOff("physicsmod.menu.ocean.hitbox", gameOptions -> ConfigClient.oceanAdjustHitbox, (gameOptions, option, value) -> {
        ConfigClient.oceanAdjustHitbox = value;
    }).setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.ocean.hitbox.info"));
    private final CycleOption<Boolean> PHYSICS_OCEAN_RIPPLES = CycleOption.createOnOff("physicsmod.menu.ocean.ripples", gameOptions -> ConfigClient.oceanRipples, (gameOptions, option, value) -> {
        ConfigClient.oceanRipples = value;
        Minecraft.getInstance().levelRenderer.allChanged();
    }).setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.ocean.ripples.info"));
    private final ProgressOption PHYSICS_OCEAN_DETAIL = new ProgressOption("physicsmod.menu.ocean.detail", 0.0, 1.0, 0.01f, gameOptions -> ConfigClient.oceanDetail, (gameOptions, value) -> {
        ConfigClient.oceanDetail = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.detail", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_RIPPLES_QUALITY = new ProgressOption("physicsmod.menu.ocean.ripplesquality", 1024.0, 4096.0, 1024.0f, gameOptions -> ConfigClient.oceanPuddleResolutionQuality, (gameOptions, value) -> {
        ConfigClient.oceanPuddleResolutionQuality = value.intValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.ripplesquality", Integer.toString((int)option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_RAIN_PUDDLES = new ProgressOption("physicsmod.menu.ocean.rainpuddles", 0.0, 1.0, 0.01f, gameOptions -> ConfigClient.oceanRainPuddleAmount, (gameOptions, value) -> {
        ConfigClient.oceanRainPuddleAmount = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.rainpuddles", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> {
        if (!ConfigClient.oceanRipples || !ConfigClient.weatherParticles) {
            return Component.translatable((String)"physicsmod.menu.ocean.rainpuddles.deactivated");
        }
        return Component.translatable((String)"physicsmod.menu.ocean.rainpuddles.info");
    });
    private final ProgressOption PHYSICS_OCEAN_FOAM_AMOUNT = new ProgressOption("physicsmod.menu.ocean.foamamount", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.oceanFoamAmount, (gameOptions, value) -> {
        ConfigClient.oceanFoamAmount = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.foamamount", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_FOAM_OPACITY = new ProgressOption("physicsmod.menu.ocean.foamopacity", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.oceanFoamOpacity, (gameOptions, value) -> {
        ConfigClient.oceanFoamOpacity = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.foamopacity", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_BLOCK_RANGE = new ProgressOption("physicsmod.menu.ocean.blockrange", 2.0, 96.0, 1.0f, gameOptions -> ConfigClient.oceanBlockRange, (gameOptions, value) -> {
        ConfigClient.oceanBlockRange = value.byteValue();
        OceanLayer.updateRange(ConfigClient.oceanBlockRange);
        Minecraft.getInstance().levelRenderer.allChanged();
        this.checkOceanWarning();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.blockrange", Integer.toString((int)option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.ocean.blockrange.info"));
    private final ProgressOption PHYSICS_OCEAN_WEATHER_CLEAR = new ProgressOption("physicsmod.menu.ocean.weather.clear", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.oceanWeatherClear, (gameOptions, value) -> {
        ConfigClient.oceanWeatherClear = value.floatValue();
        this.checkOceanWarning();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.weather.clear", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_WEATHER_RAIN = new ProgressOption("physicsmod.menu.ocean.weather.rain", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.oceanWeatherRain, (gameOptions, value) -> {
        ConfigClient.oceanWeatherRain = value.floatValue();
        this.checkOceanWarning();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.weather.rain", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_WEATHER_THUNDER = new ProgressOption("physicsmod.menu.ocean.weather.thunder", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.oceanWeatherThunder, (gameOptions, value) -> {
        ConfigClient.oceanWeatherThunder = value.floatValue();
        this.checkOceanWarning();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.weather.thunder", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_WAVE_HEIGHT = new ProgressOption("physicsmod.menu.ocean.waveheight", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.oceanWaveHeightMultiplier, (gameOptions, value) -> {
        ConfigClient.oceanWaveHeightMultiplier = value.floatValue();
        this.checkOceanWarning();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.waveheight", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_WAVE_SPEED = new ProgressOption("physicsmod.menu.ocean.wavespeed", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.oceanBaseSpeed, (gameOptions, value) -> {
        ConfigClient.oceanBaseSpeed = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.wavespeed", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_WAVE_HORIZONTAL_SCALE = new ProgressOption("physicsmod.menu.ocean.wavehorizontalscale", 0.1, 2.0, 0.01f, gameOptions -> ConfigClient.oceanHorizontalWaveScale, (gameOptions, value) -> {
        ConfigClient.oceanHorizontalWaveScale = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.wavehorizontalscale", String.format("%.2f", option.get((Options)gameOptions))));
    private final ProgressOption PHYSICS_OCEAN_PARTICLE_ALPHA = new ProgressOption("physicsmod.menu.ocean.particlealpha", 0.0, 1.0, 0.01f, gameOptions -> ConfigClient.oceanParticleAlpha, (gameOptions, value) -> {
        ConfigClient.oceanParticleAlpha = value.floatValue();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.ocean.particlealpha", String.format("%.2f", option.get((Options)gameOptions))));
    private final CycleOption<Boolean> PHYSICS_OCEAN_STICKY = CycleOption.createOnOff("physicsmod.menu.ocean.sticky", gameOptions -> ConfigClient.oceanStickyEntities, (gameOptions, option, value) -> {
        ConfigClient.oceanStickyEntities = value;
    });
    private final CycleOption<Boolean> PHYSICS_OCEAN_PARTICLES = CycleOption.createOnOff("physicsmod.menu.ocean.particles", gameOptions -> ConfigClient.oceanParticles, (gameOptions, option, value) -> {
        ConfigClient.oceanParticles = value;
    });
    private LegacyOptionsList list;
    private Component title;

    public OceanSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.ocean.title"));
        this.checkOceanWarning();
    }

    private void checkOceanWarning() {
        this.title = OceanWorld.getMaxOceanHeight() * 0.5f > (float)ConfigClient.oceanBlockRange ? Component.translatable((String)"physicsmod.menu.ocean.title.warning") : Component.translatable((String)"physicsmod.menu.ocean.title");
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.renderBackgroundWhenIngame = false;
        this.list.addSmall(this.PHYSICS_OCEAN, this.PHYSICS_OCEAN_HITBOX);
        this.list.addSmall(this.PHYSICS_OCEAN_FOAM_AMOUNT, this.PHYSICS_OCEAN_FOAM_OPACITY);
        this.list.addBig(this.PHYSICS_OCEAN_BLOCK_RANGE);
        this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.ocean.ripplestitle")));
        this.list.addBig(this.PHYSICS_OCEAN_RIPPLES);
        this.list.addBig(this.PHYSICS_OCEAN_RIPPLES_QUALITY);
        this.list.addBig(this.PHYSICS_OCEAN_RAIN_PUDDLES);
        this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.ocean.wavetitle")));
        this.list.addSmall(this.PHYSICS_OCEAN_DETAIL, this.PHYSICS_OCEAN_WAVE_HEIGHT);
        this.list.addSmall(this.PHYSICS_OCEAN_WAVE_SPEED, this.PHYSICS_OCEAN_WAVE_HORIZONTAL_SCALE);
        this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.ocean.entities")));
        this.list.addSmall(this.PHYSICS_OCEAN_PARTICLES, this.PHYSICS_OCEAN_PARTICLE_ALPHA);
        this.list.addBig(this.PHYSICS_OCEAN_STICKY);
        this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.ocean.weather")));
        this.list.addBig(this.PHYSICS_OCEAN_WEATHER_CLEAR);
        this.list.addBig(this.PHYSICS_OCEAN_WEATHER_RAIN);
        this.list.addBig(this.PHYSICS_OCEAN_WEATHER_THUNDER);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> {
            this.onClose();
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.reset"), button -> PopupWidget.create(Language.getInstance().getOrDefault("physicsmod.menu.ocean.reset"), this, widget -> this.addRenderableWidget((GuiEventListener)widget), widget -> this.removeWidget((GuiEventListener)widget), response -> {
            if (response == PopupWidget.PopupResponse.YES) {
                ConfigClient.resetOceanSettings();
                OceanLayer.updateRange(ConfigClient.oceanBlockRange);
                Minecraft.getInstance().levelRenderer.allChanged();
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new OceanSettingsScreen(this.lastScreen, this.options));
            } else {
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new OceanSettingsScreen(this.lastScreen, this.options));
            }
        })));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.PHYSICS_OCEAN_RIPPLES_QUALITY.setActive(ConfigClient.oceanRipples);
        this.PHYSICS_OCEAN_RAIN_PUDDLES.setActive(ConfigClient.oceanRipples && ConfigClient.weatherParticles);
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    @Override
    public void onClose() {
        ConfigClient.save();
        super.onClose();
    }
}

