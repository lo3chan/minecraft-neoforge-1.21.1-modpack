/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.OptionInstance$CaptionBasedToString
 *  net.minecraft.client.OptionInstance$TooltipSupplier
 *  net.minecraft.client.OptionInstance$ValueSet
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 */
package net.irisshaders.iris.gui.option;

import java.util.function.Consumer;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;

public class ShadowDistanceOption<T>
extends OptionInstance<T> {
    public ShadowDistanceOption(String string, OptionInstance.TooltipSupplier<T> arg, OptionInstance.CaptionBasedToString<T> arg2, OptionInstance.ValueSet<T> arg3, T object, Consumer<T> consumer) {
        super(string, arg, arg2, arg3, object, consumer);
    }

    public AbstractWidget createButton(Options options, int x, int y, int width) {
        AbstractWidget widget = super.createButton(options, x, y, width);
        widget.active = IrisVideoSettings.isShadowDistanceSliderEnabled();
        return widget;
    }
}

