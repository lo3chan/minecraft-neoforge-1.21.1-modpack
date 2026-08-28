/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ButtonOption
extends LegacyOption {
    private String value;
    private Button.OnPress consumer;

    public ButtonOption(String value, Button.OnPress consumer) {
        super(value);
        this.value = value;
        this.consumer = consumer;
    }

    @Override
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        return ButtonSettings.builder(x, y, width, 20, (Component)Component.literal((String)this.value), this.consumer);
    }
}

