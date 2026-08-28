/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  org.jetbrains.annotations.Nullable
 */
package net.diebuddies.physics.settings.animation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.diebuddies.math.Math;
import net.diebuddies.physics.settings.gui.legacy.LegacyContainerObjectSelectionList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.Nullable;

public class CustomAnimationList
extends LegacyContainerObjectSelectionList<Entry> {
    private static final int X_OFFSET = 45;

    public CustomAnimationList(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.centerListVertically = false;
    }

    public int addBig(LegacyOption option) {
        return this.addEntry(Entry.big(this.minecraft.options, this.width, 45, option));
    }

    public void addSmall(LegacyOption option, @Nullable LegacyOption option2) {
        this.addEntry(Entry.small(this.minecraft.options, this.width, 45, option, option2));
    }

    public void addSmall(LegacyOption[] options) {
        for (int i = 0; i < options.length; i += 2) {
            this.addSmall(options[i], i < options.length - 1 ? options[i + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 420;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 20;
    }

    @Nullable
    public AbstractWidget findOption(LegacyOption option) {
        for (Entry entry : this.children()) {
            AbstractWidget abstractWidget = entry.options.get(option);
            if (abstractWidget == null) continue;
            return abstractWidget;
        }
        return null;
    }

    public Optional<AbstractWidget> getMouseOver(double d, double e) {
        for (Entry entry : this.children()) {
            for (AbstractWidget abstractWidget : entry.children) {
                if (!abstractWidget.isMouseOver(d, e)) continue;
                return Optional.of(abstractWidget);
            }
        }
        return Optional.empty();
    }

    protected static class Entry
    extends LegacyContainerObjectSelectionList.LegacyEntry<Entry> {
        final Map<LegacyOption, AbstractWidget> options;
        final List<AbstractWidget> children;
        static final int buttonWidth = 100;
        static final int halfGap = 5;

        private Entry(Map<LegacyOption, AbstractWidget> map) {
            this.options = map;
            this.children = ImmutableList.copyOf(map.values());
        }

        public static Entry big(Options options, int width, int xOffset, LegacyOption option) {
            int buttonWidth = (int)Math.remapClamp(width, 320.0f, 640.0f, 100.0f, 150.0f);
            return new Entry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)option.createButton(options, width / 2 - (buttonWidth + 5) + xOffset, 0, buttonWidth * 2 + 10)));
        }

        public static Entry small(Options options, int width, int xOffset, LegacyOption option, @Nullable LegacyOption option2) {
            int buttonWidth = (int)Math.remapClamp(width, 320.0f, 640.0f, 100.0f, 150.0f);
            AbstractWidget abstractWidget = option.createButton(options, width / 2 - (buttonWidth + 5) + xOffset, 0, buttonWidth);
            if (option2 == null) {
                return new Entry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)abstractWidget));
            }
            return new Entry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)abstractWidget, (Object)option2, (Object)option2.createButton(options, width / 2 - (buttonWidth + 5) + (buttonWidth + 10) + xOffset, 0, buttonWidth)));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.children.forEach(abstractWidget -> {
                abstractWidget.setY(j);
                abstractWidget.render(guiGraphics, n, o, f);
            });
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            this.children.forEach(abstractWidget -> abstractWidget.setFocused(focused));
        }

        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}

