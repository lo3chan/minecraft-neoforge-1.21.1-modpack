/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.StringWidget
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import traben.entity_texture_features.ETF;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryText
extends TConfigEntry {
    protected final StringWidget widget;

    public TConfigEntryText(@Translatable String text, TextAlignment alignment) {
        super(text, null);
        this.widget = new StringWidget(this.getText(), Minecraft.getInstance().font);
        alignment.align(this.widget);
    }

    public TConfigEntryText(@Translatable String text) {
        this(text, TextAlignment.CENTER);
    }

    public static Collection<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width) {
        return TConfigEntryText.fromLongOrMultilineTranslation(translationKey, width, TextAlignment.CENTER);
    }

    public static List<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width, TextAlignment alignment) {
        Component translated = ETF.getTextFromTranslation(translationKey);
        List lines = Minecraft.getInstance().font.getSplitter().splitLines((FormattedText)translated, width, Style.EMPTY);
        ArrayList<TConfigEntry> list = new ArrayList<TConfigEntry>();
        String lastLine = null;
        for (FormattedText line : lines) {
            if (lastLine != null) {
                list.add(new TwoLines(lastLine, line.getString(), alignment));
                lastLine = null;
                continue;
            }
            lastLine = line.getString();
        }
        if (lastLine != null) {
            list.add(new TwoLines(lastLine, "", alignment));
        }
        return list;
    }

    @Override
    public AbstractWidget getWidget(int x, int y, int width, int height) {
        this.widget.setRectangle(width, height, x, y);
        return this.widget;
    }

    @Override
    boolean hasChangedFromInitial() {
        return false;
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

    @Deprecated
    public static enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT;


        private void align(StringWidget widget) {
            switch (this.ordinal()) {
                case 0: {
                    widget.alignLeft();
                    break;
                }
                case 1: {
                    widget.alignCenter();
                    break;
                }
                case 2: {
                    widget.alignRight();
                }
            }
        }
    }

    public static class TwoLines
    extends TConfigEntryText {
        protected final StringWidget widget2;

        public TwoLines(@Translatable String text1, @Translatable String text2) {
            this(text1, text2, TextAlignment.CENTER);
        }

        public TwoLines(@Translatable String text1, @Translatable String text2, TextAlignment alignment) {
            super(text1, alignment);
            this.widget2 = new StringWidget(ETF.getTextFromTranslation(text2), Minecraft.getInstance().font);
            alignment.align(this.widget2);
            if (!this.widget2.getMessage().getString().contains("\u00a7")) {
                this.widget2.setColor(0xCCCCCC);
            }
        }

        @Override
        public AbstractWidget getWidget(int x, int y, int width, int height) {
            this.widget.setRectangle(width, height / 2, x, y);
            this.widget2.setRectangle(width, height / 2, x, y + height / 2 + 2);
            return this.widget;
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.lastWidgetRendered = this.getWidget(x, y, entryWidth, entryHeight);
            this.widget.render(context, mouseX, mouseY, tickDelta);
            this.widget2.render(context, mouseX, mouseY, tickDelta);
        }
    }
}

