/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.EnumSearchScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.vines.ValueChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class EnumOption
extends LegacyOption {
    private String text;
    private String translatableTitle;
    private ValueChanged changed;
    private Screen parent;
    private Button button;
    public Enum<?> selectedEnum;

    public EnumOption(String text, Screen parent, ValueChanged changed, String translatableTitle, Enum<?> selectedEnum) {
        super(text);
        this.translatableTitle = translatableTitle;
        this.selectedEnum = selectedEnum;
        this.text = text;
        this.changed = changed;
        this.parent = parent;
    }

    @Override
    public AbstractWidget createButton(Options options, int i, int j, int k) {
        this.button = ButtonSettings.builder(i, j, k, 20, (Component)Component.literal((String)(this.text + ": " + Language.getInstance().getOrDefault(this.selectedEnum.toString()))), button -> Minecraft.getInstance().setScreen((Screen)new EnumSearchScreen(this.parent, this, this.translatableTitle)));
        return this.button;
    }

    public void setEnum(Object value) {
        this.selectedEnum = (Enum)value;
        if (this.button != null) {
            this.button.setMessage((Component)Component.literal((String)(this.text + ": " + Language.getInstance().getOrDefault(this.selectedEnum.toString()))));
        }
        this.changed.changed(this.selectedEnum);
    }
}

