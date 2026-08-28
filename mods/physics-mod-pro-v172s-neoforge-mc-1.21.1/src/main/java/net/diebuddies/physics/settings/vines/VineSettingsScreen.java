/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.vines;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.diebuddies.physics.settings.vines.VineCustomizationScreen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VineSettingsScreen
extends LegacyOptionsSubScreen {
    private static final CycleOption<Boolean> PHYSICS_VINES = CycleOption.createOnOff("physicsmod.menu.dynamicblocks.dynamicblockphysics", gameOptions -> ConfigClient.vinePhysics, (gameOptions, option, value) -> {
        ConfigClient.vinePhysics = value;
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    });
    private static final ProgressOption PHYSICS_VINE_RANGE = new ProgressOption("physicsmod.menu.dynamicblocks.range", 20.0, 400.0, 1.0f, gameOptions -> ConfigClient.vineRange, (gameOptions, value) -> {
        ConfigClient.vineRange = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.range", String.format("%.0f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_MAX_LOADED_BLOCKS = new ProgressOption("physicsmod.menu.dynamicblocks.maxloadedblocks", 1.0, 400.0, 1.0f, gameOptions -> ConfigClient.maxLoadedDynamicBlocks, (gameOptions, value) -> {
        ConfigClient.maxLoadedDynamicBlocks = value.intValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.maxloadedblocks", String.format("%.0f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.dynamicblocks.maxloadedblocks.info"));
    private static final ProgressOption PHYSICS_LIFETIME_VINES = new ProgressOption("physicsmod.menu.dynamicblocks.lifetime", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeVines, (gameOptions, value) -> {
        ConfigClient.particleLifetimeVines = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.lifetime", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_VINES = new ProgressOption("physicsmod.menu.dynamicblocks.lifetimevariance", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeVarianceVines, (gameOptions, value) -> {
        ConfigClient.particleLifetimeVarianceVines = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.lifetimevariance", String.format("%.2f", option.get((Options)gameOptions))));
    private LegacyOptionsList list;

    public VineSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.dynamicblocks.title.pro"));
    }

    protected void init() {
        ConfigVines.init();
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(PHYSICS_VINES, PHYSICS_VINE_RANGE);
        this.list.addSmall(PHYSICS_LIFETIME_VINES, PHYSICS_LIFETIME_VARIANCE_VINES);
        this.list.addBig(PHYSICS_MAX_LOADED_BLOCKS);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.customize"), button -> this.minecraft.setScreen((Screen)new VineCustomizationScreen(this, this.options))));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    private /* synthetic */ void lambda$init$16(Button button) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private static /* synthetic */ void lambda$init$15(Button button) {
        Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro");
    }
}

