/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package net.diebuddies.physics.settings.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.concurrent.Executors;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class ClothSettingsScreen
extends LegacyOptionsSubScreen {
    private static final CycleOption<Boolean> PHYSICS_ENTITY_CLOTH = CycleOption.createOnOff("physicsmod.menu.cloth.entityclothphysics", gameOptions -> ConfigClient.capePhysics, (gameOptions, option, value) -> {
        ConfigClient.capePhysics = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    });
    private static final ProgressOption PYHSICS_ENTITY_CLOTH_RANGE = new ProgressOption("physicsmod.menu.cloth.entityclothrange", 0.1, 120.0, 0.1f, gameOptions -> ConfigClient.clothEntityRange, (gameOptions, value) -> {
        ConfigClient.clothEntityRange = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.entityclothrange", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.cloth.entityclothrange.info"));
    private static final CycleOption<Boolean> PHYSICS_FISHING_LINE = CycleOption.createOnOff("physicsmod.menu.cloth.fishinglinephysics", gameOptions -> ConfigClient.fishingRodPhysics, (gameOptions, option, value) -> {
        ConfigClient.fishingRodPhysics = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    });
    private static final CycleOption<Boolean> PHYSICS_FORCE_ARMOR = CycleOption.createOnOff("physicsmod.menu.cloth.forcearmor", gameOptions -> ConfigClient.clothForceArmor, (gameOptions, option, value) -> {
        ConfigClient.clothForceArmor = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    }).setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.cloth.forcearmor.info"));
    private static final CycleOption<Boolean> PHYSICS_LEASH = CycleOption.createOnOff("physicsmod.menu.cloth.leashphysics", gameOptions -> ConfigClient.leashPhysics, (gameOptions, option, value) -> {
        ConfigClient.leashPhysics = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    });
    private static final CycleOption<Boolean> PHYSICS_BANNER = CycleOption.createOnOff("physicsmod.menu.cloth.bannerphysics", gameOptions -> ConfigClient.bannerPhysics, (gameOptions, option, value) -> {
        ConfigClient.bannerPhysics = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    });
    private static final ProgressOption PHYSICS_BANNER_DISTANCE = new ProgressOption("physicsmod.menu.cloth.bannerdistance", 5.0, 200.0, 0.1f, gameOptions -> ConfigClient.bannerPhysicsRange, (gameOptions, value) -> {
        ConfigClient.bannerPhysicsRange = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.bannerdistance", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.cloth.bannerdistance.info"));
    private static final CycleOption<Boolean> PHYSICS_CLOTH_SMOOTH_SHADING = CycleOption.createOnOff("physicsmod.menu.cloth.smoothshading", gameOptions -> ConfigClient.clothSmoothShading, (gameOptions, option, value) -> {
        ConfigClient.clothSmoothShading = value;
        ConfigClient.save();
        PhysicsMod.resetClothSimulations();
    });
    private static final ProgressOption CPU_THREADS = new ProgressOption("physicsmod.menu.cloth.clothcputhreads", 1.0, Runtime.getRuntime().availableProcessors(), 1.0f, gameOptions -> ConfigClient.clothThreads, (gameOptions, value) -> {
        ConfigClient.clothThreads = Math.max(value.intValue(), 1);
        ConfigClient.save();
        VerletSimulation.asynchronousWorker.shutdownNow();
        VerletSimulation.asynchronousWorker = Executors.newFixedThreadPool(ConfigClient.clothThreads);
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.clothcputhreads", Integer.toString((int)option.get((Options)gameOptions))));
    private static final ProgressOption LEASH_LENGTH = new ProgressOption("physicsmod.menu.cloth.leashlength", 0.1, 30.0, 0.1f, gameOptions -> ConfigClient.leashLength, (gameOptions, value) -> {
        ConfigClient.leashLength = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.leashlength", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption FISHING_LINE_LENGTH = new ProgressOption("physicsmod.menu.cloth.fishinglinelength", 0.1, 30.0, 0.1f, gameOptions -> ConfigClient.fishingLineLength, (gameOptions, value) -> {
        ConfigClient.fishingLineLength = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.fishinglinelength", String.format("%.2f", option.get((Options)gameOptions))));
    private LegacyOptionsList list;
    private static final int MAX_INFO_WIDTH = 300;
    private int infoOffset = 187;
    private List<FormattedCharSequence> info;

    public ClothSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.cloth.title.pro"));
        this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.cloth.changesinfo"), 300);
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 100, this.height - 27, 100, 20, (Component)Component.translatable((String)"physicsmod.gui.customizecloth"), button -> this.minecraft.setScreen((Screen)new ClothDisplayScreen(this))));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
        this.list.addSmall(PHYSICS_ENTITY_CLOTH, PYHSICS_ENTITY_CLOTH_RANGE);
        this.list.addBig(PHYSICS_FORCE_ARMOR);
        this.list.addSmall(PHYSICS_FISHING_LINE, FISHING_LINE_LENGTH);
        this.list.addSmall(PHYSICS_LEASH, LEASH_LENGTH);
        this.list.addSmall(PHYSICS_BANNER, PHYSICS_BANNER_DISTANCE);
        this.list.addSmall(PHYSICS_CLOTH_SMOOTH_SHADING, CPU_THREADS);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, -75.0f);
        int lineY = 0;
        for (FormattedCharSequence sequence : this.info) {
            guiGraphics.drawString(this.font, sequence, (this.width - 300) / 2, this.infoOffset + lineY, 0xFFFF55);
            lineY += 10;
        }
        matrices.popPose();
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    private /* synthetic */ void lambda$init$32(Button button) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private static /* synthetic */ void lambda$init$31(Button button) {
        Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro");
    }
}

