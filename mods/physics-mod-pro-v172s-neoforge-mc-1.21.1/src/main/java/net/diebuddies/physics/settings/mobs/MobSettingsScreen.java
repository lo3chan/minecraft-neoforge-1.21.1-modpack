/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.mobs;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.AnimationOption;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.mobs.MobCustomizeScreen;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MobSettingsScreen
extends LegacyOptionsSubScreen {
    CycleOption<TypeWrapper> PHYSICS_MOB_CYCLING = CycleOption.create("physicsmod.menu.mob.mobphysics", (Object[])TypeWrapper.values(), model -> Component.translatable((String)MobPhysicsType.values()[TypeWrapper.unwrapMainRuleValue(((TypeWrapper)((Object)((Object)model))).ordinal())].toString()), gameOptions -> TypeWrapper.values()[TypeWrapper.wrapMainRuleValue(ConfigClient.mobSetting.type.ordinal())], (gameOptions, option, model) -> {
        TypeWrapper type = (TypeWrapper)((Object)((Object)model));
        ConfigClient.mobSetting.type = MobPhysicsType.values()[TypeWrapper.unwrapMainRuleValue(type.ordinal())];
        ConfigClient.save();
    });
    private static final ProgressOption PHYSICS_LIFETIME_MOBS = new ProgressOption("physicsmod.menu.mob.lifetime", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.mobSetting.lifetime, (gameOptions, value) -> {
        ConfigClient.mobSetting.lifetime = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.lifetime", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE = new ProgressOption("physicsmod.menu.mob.lifetimevariance", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.mobSetting.lifetimeVariance, (gameOptions, value) -> {
        ConfigClient.mobSetting.lifetimeVariance = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.lifetimevariance", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_RAGDOLL_BREAK_FORCE = new ProgressOption("physicsmod.menu.mob.ragdollbreakforce", 0.0, 4.0, 0.01f, gameOptions -> ConfigClient.jointBreakForce, (gameOptions, value) -> {
        ConfigClient.jointBreakForce = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdollbreakforce", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.mob.ragdollbreak.info"));
    private static final ProgressOption PHYSICS_RAGDOLL_BREAK_BLOOD = new ProgressOption("physicsmod.menu.mob.ragdollbreakblood", 0.0, 5.0, 0.01f, gameOptions -> ConfigClient.jointBlood, (gameOptions, value) -> {
        ConfigClient.jointBlood = value.floatValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdollbreakblood", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.mob.ragdollbreak.info"));
    private static final ProgressOption PHYSICS_RAGDOLL_LIMIT = new ProgressOption("physicsmod.menu.mob.ragdolllimit", 1.0, 256.0, 1.0f, gameOptions -> ConfigClient.mobRagdollLimit, (gameOptions, value) -> {
        ConfigClient.mobRagdollLimit = Math.max(value.intValue(), 1);
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdolllimit", Integer.toString((int)option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.mob.ragdolllimit.info"));
    private LegacyOptionsList list;

    public MobSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.mobs.title"));
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        Animation animation = ConfigClient.mobSetting.animation;
        long id = -1L;
        for (Long2ObjectMap.Entry entry : ConfigAnimations.animations.long2ObjectEntrySet()) {
            if (!((Animation)entry.getValue()).equals(animation)) continue;
            id = entry.getLongKey();
            break;
        }
        AnimationOption option = new AnimationOption(Language.getInstance().getOrDefault("physicsmod.animation"), id, this, particleChange -> {
            ConfigClient.mobSetting.animation = (Animation)ConfigAnimations.animations.get(Long.parseLong((String)particleChange));
            ConfigClient.save();
        }, "default", false);
        this.list.addSmall(this.PHYSICS_MOB_CYCLING, PHYSICS_LIFETIME_MOBS);
        this.list.addSmall(PHYSICS_LIFETIME_VARIANCE, option);
        this.list.addBig(PHYSICS_RAGDOLL_BREAK_FORCE);
        this.list.addBig(PHYSICS_RAGDOLL_BREAK_BLOOD);
        this.list.addBig(PHYSICS_RAGDOLL_LIMIT);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.customize"), button -> this.minecraft.setScreen((Screen)new MobCustomizeScreen(this, this.minecraft.options))));
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

    public static enum TypeWrapper {
        RAGDOLL,
        BLOCKY,
        FRACTURED,
        BLOODY,
        OFF,
        RAGDOLL_BREAKING,
        RAGDOLL_BREAKING_BLOOD;


        private static int wrapMainRuleValue(int ordinal) {
            if (ordinal > 4) {
                return ordinal - 1;
            }
            return ordinal;
        }

        private static int unwrapMainRuleValue(int ordinal) {
            if (ordinal > 4) {
                return ordinal + 1;
            }
            return ordinal;
        }
    }
}

