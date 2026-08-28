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

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.SoundSearchScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.vines.ValueChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoundOption
extends LegacyOption {
    private String text;
    private String sound;
    private ValueChanged changed;
    private Screen parent;
    private Button button;

    public SoundOption(String text, String sound, Screen parent, ValueChanged changed) {
        super(text);
        this.text = text;
        this.sound = sound;
        this.changed = changed;
        this.parent = parent;
    }

    @Override
    public AbstractWidget createButton(Options options, int i, int j, int k) {
        this.button = ButtonSettings.builder(i, j, k, 20, (Component)Component.literal((String)(this.text + ": " + this.sound)), button -> Minecraft.getInstance().setScreen((Screen)new SoundSearchScreen(this.parent, this)));
        return this.button;
    }

    public void setSound(String sound) {
        this.sound = sound;
        if (this.button != null) {
            this.button.setMessage((Component)Component.literal((String)(this.text + ": " + (sound == null ? "null" : sound))));
        }
        this.changed.changed(sound);
    }
}

