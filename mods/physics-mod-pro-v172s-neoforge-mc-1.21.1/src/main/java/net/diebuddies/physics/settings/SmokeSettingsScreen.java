/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package net.diebuddies.physics.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.EnumOption;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.diebuddies.physics.smoke.SmokeShadowTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class SmokeSettingsScreen
extends LegacyOptionsSubScreen {
    private static final CycleOption<Boolean> PHYSICS_SMOKE = CycleOption.createOnOff("physicsmod.menu.smoke.smokephysics", gameOptions -> ConfigClient.smokePhysics, (gameOptions, option, value) -> {
        ConfigClient.smokePhysics = value;
        ConfigClient.save();
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            mod.getPhysicsWorld().getSmokeDomain().clearParticles();
        }
    });
    private static final ProgressOption PHYSICS_SMOKE_DISTANCE = new ProgressOption("physicsmod.menu.smoke.smokephysicsrange", 1.0, 400.0, 0.1f, gameOptions -> ConfigClient.smokePhysicsRange, (gameOptions, value) -> {
        ConfigClient.smokePhysicsRange = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokephysicsrange", String.format("%.0f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_BRIGHTNESS = new ProgressOption("physicsmod.menu.smoke.smokedensity", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeDensity, (gameOptions, value) -> {
        ConfigClient.smokeDensity = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokedensity", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_PARTICLE_LIMIT = new ProgressOption("physicsmod.menu.smoke.smokeparticlelimit", 1.0, 40000.0, 0.1f, gameOptions -> ConfigClient.smokeParticleLimit, (gameOptions, value) -> {
        ConfigClient.smokeParticleLimit = value.intValue();
        ConfigClient.save();
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            mod.getPhysicsWorld().getSmokeDomain().killExcessParticles();
        }
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokeparticlelimit", String.format("%.0f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_SMOKE = new ProgressOption("physicsmod.menu.smoke.particlelifetimesmoke", 0.0, 300.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeSmoke, (gameOptions, value) -> {
        ConfigClient.particleLifetimeSmoke = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particlelifetimesmoke", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_SMOKE = new ProgressOption("physicsmod.menu.smoke.particlelifetimevariancesmoke", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeVarianceSmoke, (gameOptions, value) -> {
        ConfigClient.particleLifetimeVarianceSmoke = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particlelifetimevariancesmoke", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_DESPAWN_TIME_SMOKE = new ProgressOption("physicsmod.menu.smoke.particledespawntimesmoke", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.particleDespawnTimeSmoke, (gameOptions, value) -> {
        ConfigClient.particleDespawnTimeSmoke = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particledespawntimesmoke", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.smoke.particledespawntimesmoke.info"));
    private static final ProgressOption PHYSICS_DESPAWN_TIME_VARIANCE_SMOKE = new ProgressOption("physicsmod.menu.smoke.particledespawntimevariancesmoke", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.particleDespawnTimeVarianceSmoke, (gameOptions, value) -> {
        ConfigClient.particleDespawnTimeVarianceSmoke = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particledespawntimevariancesmoke", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_COLOR_RED = new ProgressOption("physicsmod.menu.smoke.red", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeColorRed, (gameOptions, value) -> {
        ConfigClient.smokeColorRed = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.red", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_COLOR_GREEN = new ProgressOption("physicsmod.menu.smoke.green", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeColorGreen, (gameOptions, value) -> {
        ConfigClient.smokeColorGreen = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.green", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_COLOR_BLUE = new ProgressOption("physicsmod.menu.smoke.blue", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeColorBlue, (gameOptions, value) -> {
        ConfigClient.smokeColorBlue = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blue", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_DENSE_COLOR_RED = new ProgressOption("physicsmod.menu.smoke.red", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeDenseColorRed, (gameOptions, value) -> {
        ConfigClient.smokeDenseColorRed = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.red", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_DENSE_COLOR_GREEN = new ProgressOption("physicsmod.menu.smoke.green", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeDenseColorGreen, (gameOptions, value) -> {
        ConfigClient.smokeDenseColorGreen = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.green", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_DENSE_COLOR_BLUE = new ProgressOption("physicsmod.menu.smoke.blue", 0.0, 2.0, 0.01f, gameOptions -> ConfigClient.smokeDenseColorBlue, (gameOptions, value) -> {
        ConfigClient.smokeDenseColorBlue = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blue", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_FIRE = new ProgressOption("physicsmod.menu.smoke.fire", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.smokeFire, (gameOptions, value) -> {
        ConfigClient.smokeFire = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.fire", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_CAMPFIRE = new ProgressOption("physicsmod.menu.smoke.campfire", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.smokeCampfire, (gameOptions, value) -> {
        ConfigClient.smokeCampfire = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.campfire", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_BLAZE = new ProgressOption("physicsmod.menu.smoke.blaze", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.smokeBlaze, (gameOptions, value) -> {
        ConfigClient.smokeBlaze = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blaze", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_SMOKE_OTHER = new ProgressOption("physicsmod.menu.smoke.other", 0.0, 3.0, 0.01f, gameOptions -> ConfigClient.smokeOther, (gameOptions, value) -> {
        ConfigClient.smokeOther = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.other", String.format("%.2f", option.get((Options)gameOptions))));
    private static final int MAX_INFO_WIDTH = 350;
    private LegacyOptionsList list;
    private List<FormattedCharSequence> info;
    private LabelOption denseColor;
    private LabelOption color;

    public SmokeSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)""));
        this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.smoke.warning"), 350);
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        EnumOption physicsSmokeShadow = new EnumOption(Language.getInstance().getOrDefault("physicsmod.menu.smoke.shadow"), this, shadowTransformer -> {
            ConfigClient.smokeShadowTransformer = (SmokeShadowTransformer)((Object)((Object)shadowTransformer));
            ConfigClient.save();
        }, "physicsmod.menu.smoke.shadow.title", ConfigClient.smokeShadowTransformer);
        this.list.addSmall(PHYSICS_SMOKE, PHYSICS_SMOKE_DISTANCE);
        this.list.addBig(PHYSICS_SMOKE_PARTICLE_LIMIT);
        this.list.addSmall(physicsSmokeShadow, PHYSICS_SMOKE_BRIGHTNESS);
        this.list.addSmall(PHYSICS_LIFETIME_SMOKE, PHYSICS_LIFETIME_VARIANCE_SMOKE);
        this.list.addSmall(PHYSICS_DESPAWN_TIME_SMOKE, PHYSICS_DESPAWN_TIME_VARIANCE_SMOKE);
        this.list.addBig(new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.smoke.emitter")));
        this.list.addSmall(PHYSICS_SMOKE_FIRE, PHYSICS_SMOKE_CAMPFIRE);
        this.list.addSmall(PHYSICS_SMOKE_BLAZE, PHYSICS_SMOKE_OTHER);
        this.denseColor = new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.smoke.densecolor"));
        this.color = new LabelOption(Language.getInstance().getOrDefault("physicsmod.menu.smoke.color"));
        this.list.addSmall(this.denseColor, this.color);
        this.list.addSmall(PHYSICS_DENSE_COLOR_RED, PHYSICS_COLOR_RED);
        this.list.addSmall(PHYSICS_DENSE_COLOR_GREEN, PHYSICS_COLOR_GREEN);
        this.list.addSmall(PHYSICS_DENSE_COLOR_BLUE, PHYSICS_COLOR_BLUE);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> {
            this.onClose();
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.reset"), button -> PopupWidget.create(Language.getInstance().getOrDefault("physicsmod.menu.smoke.reset"), this, widget -> this.addRenderableWidget((GuiEventListener)widget), widget -> this.removeWidget((GuiEventListener)widget), response -> {
            if (response == PopupWidget.PopupResponse.YES) {
                ConfigClient.resetSmokeSettings();
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new SmokeSettingsScreen(this.lastScreen, this.options));
                for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
                    mod.getPhysicsWorld().getSmokeDomain().clearParticles();
                }
            } else {
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new SmokeSettingsScreen(this.lastScreen, this.options));
            }
        })));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, -100.0f);
        int lineY = 0;
        for (FormattedCharSequence sequence : this.info) {
            guiGraphics.drawString(this.font, sequence, (this.width - 350) / 2, 8 + lineY, 0xFFFF55);
            lineY += 10;
        }
        matrices.popPose();
        super.render(guiGraphics, mouseX, mouseY, delta);
        if (this.denseColor != null && this.color != null && this.denseColor.label != null && this.color.label != null) {
            if (SmokeSettingsScreen.PHYSICS_COLOR_RED.widget != null && SmokeSettingsScreen.PHYSICS_COLOR_RED.widget.isHoveredOrFocused() || SmokeSettingsScreen.PHYSICS_COLOR_GREEN.widget != null && SmokeSettingsScreen.PHYSICS_COLOR_GREEN.widget.isHoveredOrFocused() || SmokeSettingsScreen.PHYSICS_COLOR_BLUE.widget != null && SmokeSettingsScreen.PHYSICS_COLOR_BLUE.widget.isHoveredOrFocused() || SmokeSettingsScreen.PHYSICS_DENSE_COLOR_RED.widget != null && SmokeSettingsScreen.PHYSICS_DENSE_COLOR_RED.widget.isHoveredOrFocused() || SmokeSettingsScreen.PHYSICS_DENSE_COLOR_GREEN.widget != null && SmokeSettingsScreen.PHYSICS_DENSE_COLOR_GREEN.widget.isHoveredOrFocused() || SmokeSettingsScreen.PHYSICS_DENSE_COLOR_BLUE.widget != null && SmokeSettingsScreen.PHYSICS_DENSE_COLOR_BLUE.widget.isHoveredOrFocused()) {
                this.denseColor.label.active = false;
                this.color.label.active = false;
                int r = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_COLOR_RED.widget.getValue() * 255.0), 255));
                int g = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_COLOR_GREEN.widget.getValue() * 255.0), 255));
                int b = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_COLOR_BLUE.widget.getValue() * 255.0), 255));
                int rd = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_DENSE_COLOR_RED.widget.getValue() * 255.0), 255));
                int gd = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_DENSE_COLOR_GREEN.widget.getValue() * 255.0), 255));
                int bd = Math.max(0, Math.min((int)(SmokeSettingsScreen.PHYSICS_DENSE_COLOR_BLUE.widget.getValue() * 255.0), 255));
                int hexColor = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
                int hexDenseColor = (rd & 0xFF) << 16 | (gd & 0xFF) << 8 | (bd & 0xFF) << 0;
                this.color.setInactiveColor(hexColor);
                this.denseColor.setInactiveColor(hexDenseColor);
            } else {
                this.denseColor.label.active = true;
                this.color.label.active = true;
            }
        }
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

