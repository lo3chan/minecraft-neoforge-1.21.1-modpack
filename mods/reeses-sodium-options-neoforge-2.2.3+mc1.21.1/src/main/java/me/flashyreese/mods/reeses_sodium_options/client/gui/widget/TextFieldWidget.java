/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringUtil
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.List;
import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextFieldWidget
extends BaseWidget
implements ControlGuideProvider {
    private static final long CURSOR_ANIMATION_DURATION = 750L;
    private final Font font;
    @Nullable
    private final Component placeholder;
    protected boolean selecting;
    protected String text;
    protected int maxLength;
    protected boolean visible;
    protected boolean editable;
    private int firstCharacterIndex;
    private int selectionStart;
    private int selectionEnd;
    private int lastCursorPosition;
    private long nextCursorUpdate;
    private boolean currentCursorState;
    private float currentCursorAlpha;

    public TextFieldWidget(LayoutBounds dim, @Nullable Component placeholder) {
        super(dim);
        this.font = Minecraft.getInstance().font;
        this.text = "";
        this.maxLength = 100;
        this.visible = true;
        this.editable = true;
        this.placeholder = placeholder;
    }

    @Override
    public List<ControlGuide> controlGuides() {
        return this.isVisible() && this.isEditable() && this.isFocused() ? List.of(ControlGuide.press((Component)Component.translatable((String)"rso.controller.guide.edit"))) : List.of();
    }

    protected void onTextChanged(String text) {
    }

    protected boolean onSubmit(boolean reverse) {
        return false;
    }

    protected void onInteraction() {
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) {
            return;
        }
        this.updateCursorAlpha();
        if (!this.isFocused() && this.text.isBlank() && this.placeholder != null) {
            this.drawString(guiGraphics, this.placeholder, this.getX() + 6, this.getY() + 6, GuiThemes.DEFAULT_BUTTON.themeDarker);
        }
        this.drawRect(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), this.isFocused() ? GuiThemes.DEFAULT_BUTTON.bgHighlight : GuiThemes.DEFAULT_BUTTON.bgDefault);
        int selectionStartOffset = this.selectionStart - this.firstCharacterIndex;
        int selectionEndOffset = this.selectionEnd - this.firstCharacterIndex;
        String displayedText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        boolean isCursorWithinDisplayedText = selectionStartOffset >= 0 && selectionStartOffset <= displayedText.length();
        int textStartX = this.getX() + 6;
        int textStartY = this.getY() + 6;
        int textEndX = textStartX;
        if (selectionEndOffset > displayedText.length()) {
            selectionEndOffset = displayedText.length();
        }
        if (!displayedText.isEmpty()) {
            String preCursorText = isCursorWithinDisplayedText ? displayedText.substring(0, selectionStartOffset) : displayedText;
            guiGraphics.drawString(this.font, TextFieldWidget.formatted(preCursorText), textEndX, textStartY, -1);
            textEndX += this.font.width(TextFieldWidget.formatted(preCursorText));
        }
        boolean isCursorAtEnd = this.selectionStart < this.text.length() || this.text.length() >= this.maxLength;
        int cursorX = textEndX;
        if (!isCursorWithinDisplayedText) {
            cursorX = selectionStartOffset > 0 ? textStartX + this.getWidth() - 12 : textStartX;
        } else if (isCursorAtEnd) {
            --cursorX;
            --textEndX;
        }
        if (!displayedText.isEmpty() && isCursorWithinDisplayedText && selectionStartOffset < displayedText.length()) {
            guiGraphics.drawString(this.font, TextFieldWidget.formatted(displayedText.substring(selectionStartOffset)), textEndX, textStartY, -1);
        }
        if (this.isFocused()) {
            int color = (int)(this.currentCursorAlpha * 255.0f) << 24 | 0xD0D0D0;
            Objects.requireNonNull(this.font);
            guiGraphics.fill(cursorX, textStartY - 1, cursorX + 1, textStartY + 1 + 9, color);
        }
        if (selectionEndOffset != selectionStartOffset) {
            int selectionEndX = textStartX + this.font.width(displayedText.substring(0, selectionEndOffset));
            Objects.requireNonNull(this.font);
            this.drawSelectionHighlight(guiGraphics, cursorX, textStartY - 1, selectionEndX - 1, textStartY + 1 + 9);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int clickX = Mth.floor((double)mouseX) - this.getX() - 6;
        String displayedText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        this.setCursor(this.font.plainSubstrByWidth(displayedText, clickX).length() + this.firstCharacterIndex);
        this.setFocused(this.isMouseOver(mouseX, mouseY));
        this.onInteraction();
        return this.isFocused();
    }

    private void drawSelectionHighlight(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY) {
        int temp;
        if (startX < endX) {
            temp = startX;
            startX = endX;
            endX = temp;
        }
        if (startY < endY) {
            temp = startY;
            startY = endY;
            endY = temp;
        }
        if (endX > this.getX() + this.getWidth()) {
            endX = this.getX() + this.getWidth();
        }
        if (startX > this.getX() + this.getWidth()) {
            startX = this.getX() + this.getWidth();
        }
        guiGraphics.fill(startX, startY, endX, endY, -7019309);
    }

    private static FormattedCharSequence formatted(String text) {
        return FormattedCharSequence.forward((String)text, (Style)Style.EMPTY);
    }

    public String getSelectedText() {
        int selectionStartIndex = Math.min(this.selectionStart, this.selectionEnd);
        int selectionEndIndex = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(selectionStartIndex, selectionEndIndex);
    }

    public String getText() {
        return this.text;
    }

    public boolean rso$acceptChar(char ch, int modifiers) {
        if (!this.isVisible() || !this.isEditable()) {
            return false;
        }
        this.setFocused(true);
        this.write(String.valueOf(ch));
        return true;
    }

    public boolean rso$acceptKeyCode(int keycode, int scancode, int modifiers) {
        if (!this.isVisible()) {
            return false;
        }
        this.setFocused(true);
        return this.keyPressed(keycode, scancode, modifiers);
    }

    public boolean rso$moveCursor(int amount) {
        if (!this.isVisible()) {
            return false;
        }
        this.moveCursor(amount);
        return true;
    }

    public boolean rso$copyText() {
        if (!this.isVisible()) {
            return false;
        }
        String selectedText = this.getSelectedText();
        Minecraft.getInstance().keyboardHandler.setClipboard(selectedText.isEmpty() ? this.text : selectedText);
        return true;
    }

    public void write(String text) {
        String filteredText;
        int filteredTextLength;
        int selectionStartIndex = Math.min(this.selectionStart, this.selectionEnd);
        int selectionEndIndex = Math.max(this.selectionStart, this.selectionEnd);
        int availableSpace = this.maxLength - this.text.length() - (selectionStartIndex - selectionEndIndex);
        if (availableSpace < (filteredTextLength = (filteredText = StringUtil.filterText((String)text)).length())) {
            filteredText = filteredText.substring(0, availableSpace);
            filteredTextLength = availableSpace;
        }
        this.currentCursorState = true;
        this.nextCursorUpdate = System.currentTimeMillis() + 750L;
        this.text = new StringBuilder(this.text).replace(selectionStartIndex, selectionEndIndex, filteredText).toString();
        this.setSelectionStart(selectionStartIndex + filteredTextLength);
        this.setSelectionEnd(this.selectionStart);
        this.onTextChanged(this.text);
    }

    public boolean hasText() {
        return !this.text.isEmpty();
    }

    public void clearText() {
        if (this.text.isEmpty()) {
            return;
        }
        this.text = "";
        this.firstCharacterIndex = 0;
        this.selectionStart = 0;
        this.selectionEnd = 0;
        this.lastCursorPosition = 0;
        this.onTextChanged(this.text);
    }

    public void selectAllText() {
        this.setCursorToEnd();
        this.setSelectionEnd(0);
    }

    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }
    }

    public void eraseWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
            }
        }
    }

    public void eraseCharacters(int characterOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                int endIndex;
                int cursorPosWithOffset = this.getCursorPosWithOffset(characterOffset);
                int startIndex = Math.min(cursorPosWithOffset, this.selectionStart);
                if (startIndex != (endIndex = Math.max(cursorPosWithOffset, this.selectionStart))) {
                    this.text = new StringBuilder(this.text).delete(startIndex, endIndex).toString();
                    this.setCursor(startIndex);
                    this.onTextChanged(this.text);
                }
            }
        }
    }

    public int getWordSkipPosition(int wordOffset) {
        return this.getWordSkipPosition(wordOffset, this.getCursor());
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition) {
        return this.getWordSkipPosition(wordOffset, cursorPosition, true);
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
        int newPosition = cursorPosition;
        boolean isNegativeOffset = wordOffset < 0;
        int absoluteOffset = Math.abs(wordOffset);
        for (int i = 0; i < absoluteOffset; ++i) {
            if (!isNegativeOffset) {
                int textLength = this.text.length();
                if ((newPosition = this.text.indexOf(32, newPosition)) == -1) {
                    newPosition = textLength;
                    continue;
                }
                while (skipOverSpaces && newPosition < textLength && this.text.charAt(newPosition) == ' ') {
                    ++newPosition;
                }
                continue;
            }
            while (skipOverSpaces && newPosition > 0 && this.text.charAt(newPosition - 1) == ' ') {
                --newPosition;
            }
            while (newPosition > 0 && this.text.charAt(newPosition - 1) != ' ') {
                --newPosition;
            }
        }
        return newPosition;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    public void setCursor(int cursor) {
        this.setSelectionStart(cursor);
        if (!this.selecting) {
            this.setSelectionEnd(this.selectionStart);
        }
    }

    public void moveCursor(int offset) {
        this.setCursor(this.getCursorPosWithOffset(offset));
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.offsetByCodepoints((String)this.text, (int)this.selectionStart, (int)offset);
    }

    public void setSelectionStart(int cursor) {
        this.selectionStart = Mth.clamp((int)cursor, (int)0, (int)this.text.length());
    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    public void setSelectionEnd(int index) {
        int textLength = this.text.length();
        this.selectionEnd = Mth.clamp((int)index, (int)0, (int)textLength);
        if (this.firstCharacterIndex > textLength) {
            this.firstCharacterIndex = textLength;
        }
        int innerWidth = this.getInnerWidth();
        String displayText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), innerWidth);
        int endIndex = displayText.length() + this.firstCharacterIndex;
        if (this.selectionEnd == this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.font.plainSubstrByWidth(this.text, innerWidth, true).length();
        }
        if (this.selectionEnd > endIndex) {
            this.firstCharacterIndex += this.selectionEnd - endIndex;
        } else if (this.selectionEnd <= this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
        }
        this.firstCharacterIndex = Mth.clamp((int)this.firstCharacterIndex, (int)0, (int)textLength);
    }

    public boolean isActive() {
        return this.isVisible();
    }

    private boolean canConsumeTextInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canConsumeTextInput()) {
            return false;
        }
        if (StringUtil.isAllowedChatCharacter((char)codePoint)) {
            if (this.editable) {
                this.write(Character.toString(codePoint));
            }
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.onInteraction();
        if (!this.canConsumeTextInput()) {
            return false;
        }
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll((int)keyCode)) {
            this.setCursorToEnd();
            this.setSelectionEnd(0);
            return true;
        }
        if (Screen.isCopy((int)keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste((int)keyCode)) {
            if (this.editable) {
                this.write(Minecraft.getInstance().keyboardHandler.getClipboard());
            }
            return true;
        }
        if (Screen.isCut((int)keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            if (this.editable) {
                this.write("");
            }
            return true;
        }
        switch (keyCode) {
            case 257: 
            case 335: {
                return this.onSubmit(Screen.hasShiftDown());
            }
            case 259: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(-1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 261: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1));
                } else {
                    this.moveCursor(1);
                }
                boolean state = this.getCursor() != this.lastCursorPosition && this.getCursor() != this.text.length() + 1;
                this.lastCursorPosition = this.getCursor();
                return state;
            }
            case 263: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                boolean state = this.getCursor() != this.lastCursorPosition && this.getCursor() != 0;
                this.lastCursorPosition = this.getCursor();
                return state;
            }
            case 268: {
                this.setCursorToStart();
                return true;
            }
            case 269: {
                this.setCursorToEnd();
                return true;
            }
        }
        return false;
    }

    private void updateCursorAlpha() {
        float cursorAlpha;
        if (ReeseSodiumOptionsConfig.config().isReducedMotion()) {
            this.currentCursorAlpha = 1.0f;
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= this.nextCursorUpdate) {
            this.currentCursorState = !this.currentCursorState;
            this.nextCursorUpdate = currentTimeMillis + 750L;
        }
        cursorAlpha = (cursorAlpha = (float)(this.nextCursorUpdate - currentTimeMillis) / 750.0f) <= 0.25f ? (cursorAlpha *= 4.0f) : (cursorAlpha >= 0.75f ? (1.0f - cursorAlpha) * 4.0f : 1.0f);
        cursorAlpha = Math.clamp((float)cursorAlpha, (float)0.0f, (float)1.0f);
        this.currentCursorAlpha = this.currentCursorState ? 1.0f : 1.0f - cursorAlpha;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public int getInnerWidth() {
        return this.getWidth() - 12;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        if (!this.visible) {
            return null;
        }
        return super.nextFocusPath(navigation);
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        MutableComponent label = this.placeholder == null ? Component.empty() : this.placeholder;
        builder.add(NarratedElementType.TITLE, (Component)Component.translatable((String)"gui.narrate.editBox", (Object[])new Object[]{label, this.text}));
    }
}

