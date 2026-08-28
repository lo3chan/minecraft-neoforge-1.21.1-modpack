/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.AnimationSearchScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.vines.ValueChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AnimationOption
extends LegacyOption {
    private String text;
    private long id;
    private ValueChanged changed;
    private Screen parent;
    private Button button;
    public String nullAnimation;
    public boolean addParent;

    public AnimationOption(String text, long id, Screen parent, ValueChanged changed, String nullAnimation, boolean addParent) {
        super(text);
        this.nullAnimation = nullAnimation;
        this.text = text;
        this.id = id;
        this.changed = changed;
        this.parent = parent;
        this.addParent = addParent;
    }

    @Override
    public AbstractWidget createButton(Options options, int i, int j, int k) {
        Animation animation = (Animation)ConfigAnimations.animations.get(this.id);
        this.button = ButtonSettings.builder(i, j, k, 20, (Component)Component.literal((String)(this.text + ": " + (animation == null ? this.nullAnimation : animation.name))), button -> Minecraft.getInstance().setScreen((Screen)new AnimationSearchScreen(this.parent, this)));
        return this.button;
    }

    public void setAnimation(long id) {
        this.id = id;
        Animation animation = (Animation)ConfigAnimations.animations.get(id);
        if (this.button != null) {
            this.button.setMessage((Component)Component.literal((String)(this.text + ": " + (animation == null ? this.nullAnimation : animation.name))));
        }
        this.changed.changed(Long.toString(id));
    }
}

