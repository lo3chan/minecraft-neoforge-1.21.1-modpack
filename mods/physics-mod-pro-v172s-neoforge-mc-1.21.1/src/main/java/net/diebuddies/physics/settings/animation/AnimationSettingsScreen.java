/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.animation;

import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.AnimationEditList;
import net.diebuddies.physics.settings.animation.AnimationEditScreen;
import net.diebuddies.physics.settings.animation.ParticleDisplayScreen;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AnimationSettingsScreen
extends ParticleDisplayScreen {
    private AnimationEditList animationList;

    public AnimationSettingsScreen(Screen parent) {
        super(parent, (Component)Component.translatable((String)"physicsmod.menu.animation.settings.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.animationList = new AnimationEditList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.children.add(this.animationList);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 130, this.height - 27, 80, 20, (Component)Component.translatable((String)"physicsmod.gui.reset"), button -> PopupWidget.create(Language.getInstance().getOrDefault("physicsmod.menu.animation.settings.reset"), this, widget -> this.addRenderableWidget((GuiEventListener)widget), widget -> this.removeWidget((GuiEventListener)widget), response -> {
            if (response == PopupWidget.PopupResponse.YES) {
                ConfigAnimations.loadDefaultConfigSettings();
                this.children.remove((Object)this.animationList);
                this.animationList = new AnimationEditList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
                this.children.add(this.animationList);
                ConfigAnimations.save();
            }
        })));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 40, this.height - 27, 80, 20, (Component)Component.translatable((String)"physicsmod.menu.animation.add"), button -> this.minecraft.setScreen((Screen)new AnimationEditScreen(this, null, 0L))));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 50, this.height - 27, 80, 20, CommonComponents.GUI_DONE, button -> {
            ConfigAnimations.save();
            this.onClose();
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.animationList.getSelected() != null && this.animation != ConfigAnimations.animations.get(((Long)((BaseEntry)this.animationList.getSelected()).getUserData()).longValue())) {
            this.animation = (Animation)ConfigAnimations.animations.get(((Long)((BaseEntry)this.animationList.getSelected()).getUserData()).longValue());
            this.startAnimation();
        }
        this.animationList.render(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.reloadAllAnimations();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    private void reloadAllAnimations() {
        ConfigClient.reload();
        ConfigBlocks.reload();
    }
}

