/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.blocks;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.blocks.BlockCustomizeScreen;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.gui.AnimationOption;
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

public class BlockSettingsScreen
extends LegacyOptionsSubScreen {
    CycleOption<TypeWrapper> PHYSICS_BLOCK = CycleOption.create("physicsmod.menu.block.blockphysics", (Object[])TypeWrapper.values(), model -> Component.translatable((String)BlockPhysicsType.values()[TypeWrapper.unwrapMainRuleValue(((TypeWrapper)((Object)((Object)model))).ordinal())].toString()), gameOptions -> TypeWrapper.values()[TypeWrapper.wrapMainRuleValue(ConfigClient.blockSetting.type.ordinal())], (gameOptions, option, model) -> {
        TypeWrapper type = (TypeWrapper)((Object)((Object)model));
        ConfigClient.blockSetting.type = BlockPhysicsType.values()[TypeWrapper.unwrapMainRuleValue(type.ordinal())];
        ConfigClient.save();
    });
    private static final ProgressOption PHYSICS_LIFETIME_BLOCKS = new ProgressOption("physicsmod.menu.block.lifetime", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.blockSetting.lifetime, (gameOptions, value) -> {
        ConfigClient.blockSetting.lifetime = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.block.lifetime", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_BLOCKS = new ProgressOption("physicsmod.menu.block.lifetimevariance", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.blockSetting.lifetimeVariance, (gameOptions, value) -> {
        ConfigClient.blockSetting.lifetimeVariance = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.block.lifetimevariance", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_BLOCKS_RANGE = new ProgressOption("physicsmod.menu.block.blockphysicsrange", 10.0, 320.0, 0.1f, gameOptions -> ConfigClient.blockPhysicsRange, (gameOptions, value) -> {
        ConfigClient.blockPhysicsRange = value;
        ConfigClient.save();
    }, (gameOptions, option) -> {
        double val = option.get((Options)gameOptions);
        return option.customFormat("physicsmod.menu.block.blockphysicsrange", val >= 319.999 ? Language.getInstance().getOrDefault("physicsmod.menu.block.blockphysicsrange.max") : String.format("%.2f", option.get((Options)gameOptions)));
    }, minecraft -> Component.translatable((String)"physicsmod.menu.block.blockphysicsrange.info"));
    private static final ProgressOption PHYSICS_BLOCK_SCALE = new ProgressOption("physicsmod.menu.block.scale", 0.05, 4.0, 0.01f, gameOptions -> ConfigClient.blockSetting.scale, (gameOptions, value) -> {
        ConfigClient.blockSetting.scale = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.block.scale", String.format("%.2f", option.get((Options)gameOptions))));
    private LegacyOptionsList list;

    public BlockSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.blocks.title"));
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        Animation animation = ConfigClient.blockSetting.animation;
        long id = -1L;
        for (Long2ObjectMap.Entry entry : ConfigAnimations.animations.long2ObjectEntrySet()) {
            if (!((Animation)entry.getValue()).equals(animation)) continue;
            id = entry.getLongKey();
            break;
        }
        AnimationOption option = new AnimationOption(Language.getInstance().getOrDefault("physicsmod.animation"), id, this, particleChange -> {
            ConfigClient.blockSetting.animation = (Animation)ConfigAnimations.animations.get(Long.parseLong((String)particleChange));
            ConfigClient.save();
        }, "default", false);
        this.list.addSmall(this.PHYSICS_BLOCK, PHYSICS_BLOCKS_RANGE);
        this.list.addSmall(PHYSICS_LIFETIME_BLOCKS, PHYSICS_BLOCK_SCALE);
        this.list.addBig(PHYSICS_LIFETIME_VARIANCE_BLOCKS);
        this.list.addBig(option);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.customize"), button -> this.minecraft.setScreen((Screen)new BlockCustomizeScreen(this, this.minecraft.options))));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (ConfigClient.areDynamicBlockPhysicsEnabled() && Minecraft.getInstance().levelRenderer != null) {
            Minecraft.getInstance().levelRenderer.allChanged();
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    public static enum TypeWrapper {
        FRACTURED,
        BLOCKY,
        PARTICLES,
        OFF,
        FRACTURED_VOXEL;


        private static int wrapMainRuleValue(int ordinal) {
            if (ordinal > 3) {
                return ordinal - 1;
            }
            return ordinal;
        }

        private static int unwrapMainRuleValue(int ordinal) {
            if (ordinal > 3) {
                return ordinal + 1;
            }
            return ordinal;
        }
    }
}

