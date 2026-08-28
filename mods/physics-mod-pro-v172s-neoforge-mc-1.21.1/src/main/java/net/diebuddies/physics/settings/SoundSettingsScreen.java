/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SoundSettingsScreen
extends LegacyOptionsSubScreen {
    private static final ProgressOption IMPACT_VOLUME = new ProgressOption("physicsmod.menu.sound.soundvolume", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.impactVolume, (gameOptions, value) -> {
        ConfigClient.impactVolume = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.soundvolume", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.sound.soundvolume.info"));
    private static final ProgressOption WIND_VOLUME = new ProgressOption("physicsmod.menu.sound.windvolume", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.windVolume, (gameOptions, value) -> {
        ConfigClient.windVolume = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.windvolume", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.sound.windvolume.info"));
    private static final ProgressOption OCEAN_SPLASH_VOLUME = new ProgressOption("physicsmod.menu.sound.oceansplashvolume", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.oceanSplashVolume, (gameOptions, value) -> {
        ConfigClient.oceanSplashVolume = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.oceansplashvolume", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.sound.oceansplashvolume.info"));
    private LegacyOptionsList list;

    public SoundSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.sound.title"));
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(IMPACT_VOLUME);
        this.list.addBig(WIND_VOLUME);
        this.list.addBig(OCEAN_SPLASH_VOLUME);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

