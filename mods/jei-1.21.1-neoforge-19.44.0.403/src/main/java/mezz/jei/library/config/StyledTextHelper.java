/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 */
package mezz.jei.library.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class StyledTextHelper {
    private StyledTextHelper() {
    }

    public static Optional<Component> replaceFirst(Component text, String target, Component replacement) {
        List<StyledText> styledTexts = StyledTextHelper.getStyledTexts(text);
        String lineString = StyledTextHelper.getString(styledTexts);
        int targetStart = lineString.indexOf(target);
        if (targetStart < 0) {
            return Optional.empty();
        }
        int targetEnd = targetStart + target.length();
        MutableComponent result = Component.empty();
        StyledTextHelper.appendRange(result, styledTexts, 0, targetStart);
        Style targetStyle = StyledTextHelper.getCommonStyle(styledTexts, targetStart, targetEnd);
        result.append((Component)replacement.copy().withStyle(targetStyle));
        StyledTextHelper.appendRange(result, styledTexts, targetEnd, lineString.length());
        return Optional.of(result);
    }

    public static String toLegacyString(Component component) {
        StringBuilder result = new StringBuilder();
        Style previousStyle = Style.EMPTY;
        for (StyledText styledText : StyledTextHelper.getStyledTexts(component)) {
            if (styledText.text().isEmpty()) continue;
            Style style = styledText.style();
            if (!Objects.equals(previousStyle, style)) {
                if (!previousStyle.isEmpty() && style.isEmpty()) {
                    result.append(ChatFormatting.RESET);
                } else {
                    result.append(StyledTextHelper.toLegacyFormattingString(style));
                }
            }
            result.append(styledText.text());
            previousStyle = style;
        }
        return result.toString();
    }

    private static List<StyledText> getStyledTexts(Component component) {
        ArrayList<StyledText> styledTexts = new ArrayList<StyledText>();
        component.visit((style, text) -> {
            StyledTextHelper.appendLegacyFormattedText(styledTexts, text, style);
            return Optional.empty();
        }, Style.EMPTY);
        return styledTexts;
    }

    private static void appendLegacyFormattedText(List<StyledText> styledTexts, String text, Style baseStyle) {
        Style style = baseStyle;
        StringBuilder currentText = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            ChatFormatting formatting;
            char c = text.charAt(i);
            if (c == '\u00a7' && i + 1 < text.length() && (formatting = ChatFormatting.getByCode((char)text.charAt(i + 1))) != null) {
                StyledTextHelper.append(styledTexts, currentText.toString(), style);
                currentText.setLength(0);
                style = formatting == ChatFormatting.RESET ? Style.EMPTY : style.applyFormat(formatting);
                ++i;
                continue;
            }
            currentText.append(c);
        }
        StyledTextHelper.append(styledTexts, currentText.toString(), style);
    }

    private static void append(List<StyledText> styledTexts, String text, Style style) {
        if (!text.isEmpty()) {
            styledTexts.add(new StyledText(text, style));
        }
    }

    private static String getString(List<StyledText> styledTexts) {
        StringBuilder result = new StringBuilder();
        for (StyledText styledText : styledTexts) {
            result.append(styledText.text());
        }
        return result.toString();
    }

    private static void appendRange(MutableComponent result, List<StyledText> styledTexts, int start, int end) {
        int index = 0;
        for (StyledText styledText : styledTexts) {
            int overlapEnd;
            int styledTextStart = index;
            int styledTextEnd = index + styledText.text().length();
            int overlapStart = Math.max(start, styledTextStart);
            if (overlapStart < (overlapEnd = Math.min(end, styledTextEnd))) {
                String text = styledText.text().substring(overlapStart - styledTextStart, overlapEnd - styledTextStart);
                result.append((Component)Component.literal((String)text).setStyle(styledText.style()));
            }
            index = styledTextEnd;
        }
    }

    private static Style getCommonStyle(List<StyledText> styledTexts, int start, int end) {
        Style commonStyle = null;
        int index = 0;
        for (StyledText styledText : styledTexts) {
            int styledTextStart = index;
            int styledTextEnd = index + styledText.text().length();
            if (Math.max(start, styledTextStart) < Math.min(end, styledTextEnd)) {
                Style style = styledText.style();
                if (commonStyle == null) {
                    commonStyle = style;
                } else if (!Objects.equals(commonStyle, style)) {
                    return Style.EMPTY;
                }
            }
            index = styledTextEnd;
        }
        return commonStyle == null ? Style.EMPTY : commonStyle;
    }

    private static String toLegacyFormattingString(Style style) {
        StringBuilder result = new StringBuilder();
        TextColor color = style.getColor();
        if (color != null) {
            for (ChatFormatting chatFormatting : ChatFormatting.values()) {
                if (!Objects.equals(TextColor.fromLegacyFormat((ChatFormatting)chatFormatting), color)) continue;
                result.append(chatFormatting);
                break;
            }
        }
        if (style.isObfuscated()) {
            result.append(ChatFormatting.OBFUSCATED);
        }
        if (style.isBold()) {
            result.append(ChatFormatting.BOLD);
        }
        if (style.isStrikethrough()) {
            result.append(ChatFormatting.STRIKETHROUGH);
        }
        if (style.isUnderlined()) {
            result.append(ChatFormatting.UNDERLINE);
        }
        if (style.isItalic()) {
            result.append(ChatFormatting.ITALIC);
        }
        return result.toString();
    }

    private record StyledText(String text, Style style) {
    }
}

