/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import traben.tconfig.gui.TConfigScreenList;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryCategory
extends TConfigEntry {
    private final LinkedHashMap<String, TConfigEntry> options = new LinkedHashMap();
    private final String translationKey;
    protected boolean fullWidthBackgroundEvenIfSmaller = false;
    private TConfigScreenList screen = null;
    private Tooltip emptyTooltip = Tooltip.create((Component)Component.translatable((String)"config.entity_features.empty"));
    private TConfigScreenList.Align align = TConfigScreenList.Align.CENTER;
    private TConfigScreenList.Renderable renderFeature = null;

    public TConfigEntryCategory(@Translatable String text, @Translatable String tooltip) {
        super(text, tooltip);
        this.translationKey = text;
    }

    public TConfigEntryCategory(@Translatable String text) {
        super(text, null);
        this.translationKey = text;
    }

    public void setAlign(TConfigScreenList.Align align) {
        this.align = align;
    }

    public LinkedHashMap<String, TConfigEntry> getOptions() {
        return this.options;
    }

    public TConfigScreenList getScreen() {
        if (this.screen == null) {
            this.screen = new TConfigScreenList(this.translationKey, Minecraft.getInstance().screen, this.options.values().toArray(new TConfigEntry[0]), this::setValuesToDefault, this::resetValuesToInitial, this.align);
            this.screen.setRenderFeature(this.renderFeature);
            if (this.fullWidthBackgroundEvenIfSmaller) {
                this.screen.setWidgetBackgroundToFullWidth();
            }
        }
        return this.screen;
    }

    @Override
    public AbstractWidget getWidget(int x, int y, int width, int height) {
        return new CategoryButton(x, y, width, height, this.getText(), button -> Minecraft.getInstance().setScreen((Screen)this.getScreen()));
    }

    @Override
    public boolean saveValuesToConfig() {
        boolean found = false;
        for (TConfigEntry option : this.options.values()) {
            found |= option.saveValuesToConfig();
        }
        return found;
    }

    public void setWidgetBackgroundToFullWidth() {
        this.fullWidthBackgroundEvenIfSmaller = true;
    }

    @Override
    public void setValuesToDefault() {
        for (TConfigEntry option : this.options.values()) {
            option.setValuesToDefault();
        }
    }

    @Override
    public void resetValuesToInitial() {
        for (TConfigEntry option : this.options.values()) {
            option.resetValuesToInitial();
        }
    }

    public TConfigEntryCategory add(TConfigEntry ... option) {
        for (TConfigEntry tConfigEntry : option) {
            this.add(tConfigEntry);
        }
        return this;
    }

    public TConfigEntryCategory addAll(Collection<TConfigEntry> option) {
        if (option != null) {
            option.forEach(this::add);
        }
        return this;
    }

    public TConfigEntryCategory add(TConfigEntry option) {
        if (option == null) {
            return this;
        }
        if (option instanceof TConfigEntryCategory) {
            TConfigEntryCategory category = (TConfigEntryCategory)option;
            return this.addOrMerge(category);
        }
        this.options.put(option.getText().getString(), option);
        return this;
    }

    private TConfigEntryCategory addOrMerge(TConfigEntryCategory category) {
        TConfigEntry tConfigEntry;
        String categoryKey = category.getText().getString();
        if (this.options.containsKey(categoryKey) && (tConfigEntry = this.options.get(categoryKey)) instanceof TConfigEntryCategory) {
            TConfigEntryCategory existingCategory = (TConfigEntryCategory)tConfigEntry;
            category.options.values().forEach(existingCategory::add);
        } else {
            this.options.put(categoryKey, category);
        }
        return this;
    }

    @Override
    boolean hasChangedFromInitial() {
        return this.options.values().stream().anyMatch(TConfigEntry::hasChangedFromInitial);
    }

    public TConfigEntryCategory setEmptyTooltip(@NotNull @Translatable String emptyTooltipKey) {
        this.emptyTooltip = Tooltip.create((Component)Component.translatable((String)emptyTooltipKey));
        return this;
    }

    public void setRenderFeature(TConfigScreenList.Renderable renderFeature) {
        this.renderFeature = renderFeature;
    }

    private class CategoryButton
    extends Button {
        protected CategoryButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
            super(x, y, width, height, message, onPress, Supplier::get);
            boolean bl = this.active = !TConfigEntryCategory.this.options.isEmpty();
            if (!this.active) {
                this.setTooltip(TConfigEntryCategory.this.emptyTooltip);
            }
        }

        @NotNull
        public Component getMessage() {
            return TConfigEntryCategory.this.hasChangedFromInitial() ? Component.nullToEmpty((String)("\u00a7a" + super.getMessage().getString())) : super.getMessage();
        }
    }

    public static class Empty
    extends TConfigEntryCategory {
        public Empty() {
            super("", null);
        }

        @Override
        public AbstractWidget getWidget(int x, int y, int width, int height) {
            return null;
        }
    }
}

