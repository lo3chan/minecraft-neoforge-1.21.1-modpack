/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package mezz.jei.common.gui.elements;

import java.util.List;
import java.util.Objects;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.util.Pair;
import mezz.jei.common.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class DrawableWrappedText
implements IDrawable {
    private static final int lineSpacing = 2;
    private final List<FormattedText> descriptionLines;
    private final int lineHeight;
    private final int width;
    private final int height;

    public DrawableWrappedText(List<FormattedText> text, int maxWidth) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Objects.requireNonNull(font);
        this.lineHeight = 9 + 2;
        Pair<List<FormattedText>, Boolean> result = StringUtil.splitLines(font, text, maxWidth, Integer.MAX_VALUE);
        this.descriptionLines = result.first();
        this.width = maxWidth;
        this.height = this.lineHeight * this.descriptionLines.size() - 2;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        Language language = Language.getInstance();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int yPos = 0;
        for (FormattedText descriptionLine : this.descriptionLines) {
            FormattedCharSequence charSequence = language.getVisualOrder(descriptionLine);
            guiGraphics.drawString(font, charSequence, xOffset, yPos + yOffset, -16777216, false);
            yPos += this.lineHeight;
        }
    }
}

