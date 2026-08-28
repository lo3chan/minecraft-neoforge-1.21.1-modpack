/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
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
package net.diebuddies.physics.settings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
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

public class LiquidSettingsScreen
extends LegacyOptionsSubScreen {
    private LegacyOptionsList list;
    private static final CycleOption<Boolean> PHYSICS_LIQUIDS = CycleOption.createOnOff("physicsmod.menu.liquid.liquidphysics", gameOptions -> ConfigClient.liquidPhysics, (gameOptions, option, value) -> {
        ConfigClient.liquidPhysics = value;
        ConfigClient.save();
        if (!ConfigClient.liquidPhysics) {
            for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
                ObjectArrayList liquids = new ObjectArrayList(mod.getPhysicsWorld().getLiquids());
                for (Liquid liquid : liquids) {
                    mod.getPhysicsWorld().removeLiquid(liquid);
                }
            }
        }
    });
    private final CycleOption<Boolean> PHYSICS_CUDA_LIQUIDS = CycleOption.createOnOff("physicsmod.menu.liquid.cudaliquids", gameOptions -> ConfigClient.cudaLiquids, (gameOptions, option, value) -> {
        if (((CycleOption)option).active) {
            ConfigClient.cudaLiquids = value;
            ConfigClient.save();
            for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
                mod.getPhysicsWorld().destroy();
            }
            PhysicsMod.getInstances().clear();
            StarterClient.createPhysicsCooking(ConfigClient.cudaLiquids());
            this.list.children().clear();
            Minecraft.getInstance().setScreen((Screen)new LiquidSettingsScreen(this.lastScreen, this.options));
        }
    }).setTooltip(minecraft -> graphicsStatus -> {
        if (!StarterClient.cudaAvailable) {
            return Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.error");
        }
        return Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.info");
    });
    private static final ProgressOption PHYSICS_CUDA_LIQUIDS_PARTICLE_SIZE = new ProgressOption("physicsmod.menu.liquid.cudaliquids.size", 0.05, 0.5, 0.01f, gameOptions -> ConfigClient.cudaLiquidsParticleSize, (gameOptions, value) -> {
        ConfigClient.cudaLiquidsParticleSize = value.floatValue();
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            mod.getPhysicsWorld().destroy();
        }
        PhysicsMod.getInstances().clear();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.cudaliquids.size", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> {
        if (Minecraft.getInstance().level != null || !StarterClient.cudaAvailable) {
            return Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.size.error");
        }
        return Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.size.info");
    });
    private static final ProgressOption PHYSICS_CUDA_LIQUIDS_BLUR_PASSES = new ProgressOption("physicsmod.menu.liquid.cudaliquids.blur", 1.0, 10.0, 1.0f, gameOptions -> ConfigClient.cudaLiquidsBlurPasses, (gameOptions, value) -> {
        ConfigClient.cudaLiquidsBlurPasses = Math.max(value.intValue(), 1);
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.cudaliquids.blur", Integer.toString((int)option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.blur.info"));
    private static final ProgressOption PHYSICS_LIQUID_CPU_THREADS = new ProgressOption("physicsmod.menu.liquid.liquidthreads", 1.0, Runtime.getRuntime().availableProcessors(), 1.0f, gameOptions -> ConfigClient.liquidThreads, (gameOptions, value) -> {
        ConfigClient.liquidThreads = Math.max(value.intValue(), 1);
        ConfigClient.save();
        if (Liquid.threads != null) {
            for (int i = 0; i < Liquid.threads.length; ++i) {
                Liquid.threads[i].cancel();
            }
        }
        Liquid.threads = null;
        Liquid.initThreads();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.liquidthreads", Integer.toString((int)option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.liquid.liquidthreads.info"));
    private static final ProgressOption PHYSICS_LIQUID_DENSITY = new ProgressOption("physicsmod.menu.liquid.waterdensity", 3.0, 8.0, 1.0f, gameOptions -> ConfigClient.waterDensity, (gameOptions, value) -> {
        ConfigClient.waterDensity = Math.max(value.intValue(), 1);
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.waterdensity", Integer.toString((int)option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.liquid.waterdensity.info"));
    private static final ProgressOption PHYSICS_LIQUID_SOURCE_DISTANCE = new ProgressOption("physicsmod.menu.liquid.liquidsourcedistance", 1.0, 40.0, 0.1f, gameOptions -> ConfigClient.liquidSourceDistance, (gameOptions, value) -> {
        ConfigClient.liquidSourceDistance = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.liquidsourcedistance", String.format("%.0f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_LIQUID = new ProgressOption("physicsmod.menu.liquid.particlelifetimeliquids", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeLiquids, (gameOptions, value) -> {
        ConfigClient.particleLifetimeLiquids = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimeliquids", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_LIQUID = new ProgressOption("physicsmod.menu.liquid.particlelifetimevarianceliquids", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeVarianceLiquids, (gameOptions, value) -> {
        ConfigClient.particleLifetimeVarianceLiquids = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimevarianceliquids", String.format("%.2f", option.get((Options)gameOptions))));
    private static final int MAX_INFO_WIDTH = 300;
    private List<FormattedCharSequence> info;

    public LiquidSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.liquid.title.pro"));
        this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.liquid.warning"), 300);
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        if (Minecraft.getInstance().level != null || !StarterClient.cudaAvailable) {
            this.PHYSICS_CUDA_LIQUIDS.active = false;
            LiquidSettingsScreen.PHYSICS_CUDA_LIQUIDS_PARTICLE_SIZE.active = false;
            this.list.renderBackgroundWhenIngame = false;
        } else {
            this.PHYSICS_CUDA_LIQUIDS.active = true;
            LiquidSettingsScreen.PHYSICS_CUDA_LIQUIDS_PARTICLE_SIZE.active = true;
        }
        if (ConfigClient.cudaLiquids()) {
            this.list.addBig(this.PHYSICS_CUDA_LIQUIDS);
            this.list.addBig(PHYSICS_CUDA_LIQUIDS_PARTICLE_SIZE);
            this.list.addBig(PHYSICS_CUDA_LIQUIDS_BLUR_PASSES);
            this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.liquid.cudaliquids.commands"), 300);
        } else {
            this.list.addSmall(PHYSICS_LIQUIDS, this.PHYSICS_CUDA_LIQUIDS);
            this.list.addSmall(PHYSICS_LIQUID_DENSITY, PHYSICS_LIQUID_CPU_THREADS);
            this.list.addBig(PHYSICS_LIQUID_SOURCE_DISTANCE);
            this.list.addSmall(PHYSICS_LIFETIME_LIQUID, PHYSICS_LIFETIME_VARIANCE_LIQUID);
            this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.liquid.warning"), 300);
        }
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        int yOffset = 137;
        if (ConfigClient.cudaLiquids()) {
            yOffset = 112;
        }
        int lineY = 0;
        for (FormattedCharSequence sequence : this.info) {
            guiGraphics.drawString(this.font, sequence, (this.width - 300) / 2, yOffset + lineY, 0xFFFF55);
            lineY += 10;
        }
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    @Override
    public void onClose() {
        ConfigClient.save();
        super.onClose();
    }

    private /* synthetic */ void lambda$init$32(Button button) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private static /* synthetic */ void lambda$init$31(Button button) {
        Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro");
    }
}

