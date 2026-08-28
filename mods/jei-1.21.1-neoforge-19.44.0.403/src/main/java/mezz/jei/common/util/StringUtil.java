/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.StringSplitter
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.FormattedText$StyledContentConsumer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package mezz.jei.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import mezz.jei.common.util.ExpandNewLineTextAcceptor;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class StringUtil {
    private StringUtil() {
    }

    public static Component stripStyling(Component textComponent) {
        MutableComponent text = textComponent.plainCopy();
        for (Component sibling : textComponent.getSiblings()) {
            text.append(StringUtil.stripStyling(sibling));
        }
        return text;
    }

    public static String removeChatFormatting(String string) {
        return ChatFormatting.stripFormatting((String)string);
    }

    public static FormattedText truncateStringToWidth(FormattedText text, int width, Font font) {
        int ellipsisWidth = font.width("...");
        StringSplitter splitter = font.getSplitter();
        FormattedText truncatedText = font.substrByWidth(text, width - ellipsisWidth);
        Style style = splitter.componentStyleAtWidth(text, width - ellipsisWidth);
        if (style == null) {
            style = Style.EMPTY;
        }
        return FormattedText.composite((FormattedText[])new FormattedText[]{truncatedText, Component.literal((String)"...").setStyle(style)});
    }

    public static Pair<List<FormattedText>, Boolean> splitLines(Font font, List<FormattedText> lines, int width, int maxLines) {
        if (lines.isEmpty()) {
            return new Pair<List<FormattedText>, Boolean>(List.of(), false);
        }
        if (maxLines <= 0) {
            return new Pair<List<FormattedText>, Boolean>(List.of(), true);
        }
        if (width <= 0) {
            return new Pair<List<FormattedText>, Boolean>(List.copyOf(lines), false);
        }
        StringSplitter splitter = font.getSplitter();
        ArrayList result = new ArrayList();
        for (FormattedText line : lines) {
            List splitLines = line.getString().isEmpty() ? List.of(line) : splitter.splitLines(line, width, Style.EMPTY);
            for (FormattedText splitLine : splitLines) {
                if (result.size() == maxLines) {
                    FormattedText last = (FormattedText)result.removeLast();
                    last = StringUtil.truncateStringToWidth(last, width, font);
                    result.add(last);
                    return new Pair<List<FormattedText>, Boolean>(result, true);
                }
                result.add(splitLine);
            }
        }
        return new Pair<List<FormattedText>, Boolean>(result, false);
    }

    public static List<FormattedText> expandNewlines(Component ... descriptionComponents) {
        ArrayList<FormattedText> descriptionLinesExpanded = new ArrayList<FormattedText>();
        for (Component descriptionLine : descriptionComponents) {
            ExpandNewLineTextAcceptor newLineTextAcceptor = new ExpandNewLineTextAcceptor();
            descriptionLine.visit((FormattedText.StyledContentConsumer)newLineTextAcceptor, Style.EMPTY);
            newLineTextAcceptor.addLinesTo(descriptionLinesExpanded);
        }
        return descriptionLinesExpanded;
    }

    public static String intsToString(Collection<Integer> indexes) {
        return indexes.stream().sorted().map(i -> Integer.toString(i)).collect(Collectors.joining(", "));
    }

    public static void drawCenteredStringWithShadow(GuiGraphics guiGraphics, Font font, String string, ImmutableRect2i area) {
        ImmutableRect2i textArea = MathUtil.centerTextArea(area, font, string);
        guiGraphics.drawString(font, string, textArea.getX(), textArea.getY(), -1);
    }

    public static void drawCenteredStringWithShadow(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, ImmutableRect2i area) {
        ImmutableRect2i textArea = MathUtil.centerTextArea(area, font, text);
        guiGraphics.drawString(font, text, textArea.getX(), textArea.getY(), -1);
    }
}

