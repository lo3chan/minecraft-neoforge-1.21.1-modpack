/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  org.apache.commons.lang3.Validate
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.apache.commons.lang3.Validate;

public class BasicFrame
extends AbstractFrame {
    protected List<Supplier<GuiEventListener>> functions;

    public BasicFrame(LayoutBounds dim, Screen screen, boolean renderOutline, List<Supplier<GuiEventListener>> functions) {
        super(dim, screen, renderOutline, null);
        this.functions = functions;
        this.buildFrame();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void buildFrame() {
        this.children.clear();
        this.optionRows.clear();
        this.functions.forEach(function -> this.children.add((GuiEventListener)function.get()));
        super.buildFrame();
    }

    @Override
    public void updateFrameDim(LayoutBounds dim) {
        this.setDim(dim);
        this.buildFrame();
    }

    public static class Builder {
        private final List<Supplier<GuiEventListener>> functions = new ArrayList<Supplier<GuiEventListener>>();
        private LayoutBounds dim;
        private boolean renderOutline;
        private Screen screen;

        public Builder withDimension(LayoutBounds dim) {
            this.dim = dim;
            return this;
        }

        public Builder withRenderOutline(boolean renderOutline) {
            this.renderOutline = renderOutline;
            return this;
        }

        public Builder withScreen(Screen screen) {
            this.screen = screen;
            return this;
        }

        public Builder addChild(Supplier<GuiEventListener> function) {
            this.functions.add(function);
            return this;
        }

        public BasicFrame build() {
            Validate.notNull((Object)this.dim, (String)"Dimension must be specified", (Object[])new Object[0]);
            Validate.notNull((Object)this.screen, (String)"Screen must be specified", (Object[])new Object[0]);
            return new BasicFrame(this.dim, this.screen, this.renderOutline, this.functions);
        }
    }
}

