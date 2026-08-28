/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryCustomButton
extends TConfigEntry {
    private final Button button;

    public TConfigEntryCustomButton(@Translatable String text, @Translatable String tooltip, Button.OnPress action) {
        super(text, tooltip);
        this.button = Button.builder((Component)this.getText(), (Button.OnPress)action).bounds(0, 0, 0, 0).tooltip(this.getTooltip()).build();
    }

    public TConfigEntryCustomButton(@Translatable String text, Button.OnPress button) {
        this(text, null, button);
    }

    @Override
    public AbstractWidget getWidget(int x, int y, int width, int height) {
        this.button.setRectangle(width, height, x, y);
        return this.button;
    }

    @Override
    boolean saveValuesToConfig() {
        return false;
    }

    @Override
    void setValuesToDefault() {
    }

    @Override
    void resetValuesToInitial() {
    }

    @Override
    boolean hasChangedFromInitial() {
        return false;
    }
}

