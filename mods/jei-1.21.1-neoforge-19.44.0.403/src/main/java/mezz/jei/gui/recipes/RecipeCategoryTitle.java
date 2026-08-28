/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package mezz.jei.gui.recipes;

import javax.annotation.Nullable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.StringUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class RecipeCategoryTitle {
    private final FormattedCharSequence visibleString;
    @Nullable
    private final Component tooltipString;
    private final ImmutableRect2i area;

    public static RecipeCategoryTitle create(IRecipeCategory<?> recipeCategory, Font font, ImmutableRect2i availableArea) {
        Component tooltipString;
        FormattedCharSequence visibleString;
        Component fullString = StringUtil.stripStyling(recipeCategory.getTitle());
        int availableTitleWidth = availableArea.getWidth();
        if (font.width((FormattedText)fullString) > availableTitleWidth) {
            FormattedText formattedText = StringUtil.truncateStringToWidth((FormattedText)fullString, availableTitleWidth, font);
            visibleString = Language.getInstance().getVisualOrder(formattedText);
            tooltipString = fullString;
        } else {
            visibleString = fullString.getVisualOrderText();
            tooltipString = null;
        }
        ImmutableRect2i area = MathUtil.centerTextArea(availableArea, font, visibleString);
        return new RecipeCategoryTitle(visibleString, tooltipString, area);
    }

    public RecipeCategoryTitle() {
        this(FormattedCharSequence.EMPTY, (Component)Component.empty(), ImmutableRect2i.EMPTY);
    }

    public RecipeCategoryTitle(FormattedCharSequence visibleString, @Nullable Component tooltipString, ImmutableRect2i area) {
        this.visibleString = visibleString;
        this.tooltipString = tooltipString;
        this.area = area;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.area.contains(mouseX, mouseY);
    }

    public void getTooltip(JeiTooltip tooltip) {
        if (this.tooltipString != null) {
            tooltip.add((FormattedText)this.tooltipString);
        }
    }

    public void draw(GuiGraphics guiGraphics, Font font) {
        StringUtil.drawCenteredStringWithShadow(guiGraphics, font, this.visibleString, this.area);
    }
}

