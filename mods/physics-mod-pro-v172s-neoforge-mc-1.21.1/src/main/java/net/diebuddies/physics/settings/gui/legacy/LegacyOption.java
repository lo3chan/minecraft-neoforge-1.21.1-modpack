/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package net.diebuddies.physics.settings.gui.legacy;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class LegacyOption {
    private final MutableComponent caption;

    public LegacyOption(String string) {
        this.caption = Component.translatable((String)string);
    }

    public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

    protected Component getCaption() {
        return this.caption;
    }

    public Component customFormat(String translatable, String literal) {
        return Component.translatable((String)"physicsmod.menu.options.format", (Object[])new Object[]{Component.translatable((String)translatable), Component.literal((String)literal)});
    }

    public Component pixelValueLabel(int i) {
        return Component.translatable((String)"options.pixel_value", (Object[])new Object[]{this.getCaption(), i});
    }

    public Component percentValueLabel(double d) {
        return Component.translatable((String)"options.percent_value", (Object[])new Object[]{this.getCaption(), (int)(d * 100.0)});
    }

    public Component percentAddValueLabel(int i) {
        return Component.translatable((String)"options.percent_add_value", (Object[])new Object[]{this.getCaption(), i});
    }

    public Component genericValueLabel(Component component) {
        return Component.translatable((String)"options.generic_value", (Object[])new Object[]{this.getCaption(), component});
    }

    public Component genericValueLabel(int i) {
        return this.genericValueLabel((Component)Component.literal((String)Integer.toString(i)));
    }
}

