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
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package net.diebuddies.physics.settings.gui.legacy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.diebuddies.physics.settings.gui.legacy.LegacyContainerObjectSelectionList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.UXTooltipAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class LegacyOptionsList
extends LegacyContainerObjectSelectionList<LegacyEntry> {
    public LegacyOptionsList(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.centerListVertically = false;
    }

    public int addBig(LegacyOption option) {
        return this.addEntry(LegacyEntry.big(this.minecraft.options, this.width, option));
    }

    public void addSmall(LegacyOption option, @Nullable LegacyOption option2) {
        this.addEntry(LegacyEntry.small(this.minecraft.options, this.width, option, option2));
    }

    public void addSmall(LegacyOption[] options) {
        for (int i = 0; i < options.length; i += 2) {
            this.addSmall(options[i], i < options.length - 1 ? options[i + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    @Nullable
    public AbstractWidget findOption(LegacyOption option) {
        for (LegacyEntry entry : this.children()) {
            AbstractWidget abstractWidget = entry.options.get(option);
            if (abstractWidget == null) continue;
            return abstractWidget;
        }
        return null;
    }

    public Optional<AbstractWidget> getMouseOver(double d, double e) {
        for (LegacyEntry entry : this.children()) {
            for (AbstractWidget abstractWidget : entry.children) {
                if (!abstractWidget.isMouseOver(d, e)) continue;
                return Optional.of(abstractWidget);
            }
        }
        return Optional.empty();
    }

    public static Component tooltipAt(LegacyOptionsList optionsList, int i, int j) {
        Optional<AbstractWidget> optional = optionsList.getMouseOver(i, j);
        if (optional.isPresent() && optional.get() instanceof UXTooltipAccessor) {
            return ((UXTooltipAccessor)optional.get()).getTooltipLegacy();
        }
        return null;
    }

    public static AbstractWidget widgetAt(LegacyOptionsList optionsList, int i, int j) {
        Optional<AbstractWidget> optional = optionsList.getMouseOver(i, j);
        if (optional.isPresent() && optional.get() instanceof UXTooltipAccessor) {
            return optional.get();
        }
        return null;
    }

    protected static class LegacyEntry
    extends LegacyContainerObjectSelectionList.LegacyEntry<LegacyEntry> {
        final Map<LegacyOption, AbstractWidget> options;
        final List<AbstractWidget> children;

        private LegacyEntry(Map<LegacyOption, AbstractWidget> map) {
            this.options = map;
            this.children = ImmutableList.copyOf(map.values());
        }

        public static LegacyEntry big(Options options, int i, LegacyOption option) {
            return new LegacyEntry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)option.createButton(options, i / 2 - 155, 0, 310)));
        }

        public static LegacyEntry small(Options options, int i, LegacyOption option, @Nullable LegacyOption option2) {
            AbstractWidget abstractWidget = option.createButton(options, i / 2 - 155, 0, 150);
            if (option2 == null) {
                return new LegacyEntry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)abstractWidget));
            }
            return new LegacyEntry((Map<LegacyOption, AbstractWidget>)ImmutableMap.of((Object)option, (Object)abstractWidget, (Object)option2, (Object)option2.createButton(options, i / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.children.forEach(abstractWidget -> {
                abstractWidget.setY(j);
                abstractWidget.render(guiGraphics, n, o, f);
            });
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

