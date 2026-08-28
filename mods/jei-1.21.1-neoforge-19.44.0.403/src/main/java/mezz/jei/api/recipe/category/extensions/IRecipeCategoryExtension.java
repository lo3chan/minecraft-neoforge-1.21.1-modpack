/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.api.recipe.category.extensions;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface IRecipeCategoryExtension<T> {
    default public void drawInfo(T recipe, int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.drawInfo(recipeWidth, recipeHeight, guiGraphics, mouseX, mouseY);
    }

    default public void getTooltip(ITooltipBuilder tooltip, T recipe, double mouseX, double mouseY) {
        List<Component> tooltipStrings = this.getTooltipStrings(recipe, mouseX, mouseY);
        tooltip.addAll(tooltipStrings);
    }

    @Deprecated(since="19.5.4", forRemoval=true)
    default public List<Component> getTooltipStrings(T recipe, double mouseX, double mouseY) {
        return this.getTooltipStrings(mouseX, mouseY);
    }

    @Deprecated(since="19.19.3", forRemoval=true)
    default public void createRecipeExtras(T recipe, IRecipeExtrasBuilder builder, IRecipeSlotsView recipeSlotsView, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    }

    default public void createRecipeExtras(T recipe, IRecipeExtrasBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        this.createRecipeExtras(recipe, builder, () -> Collections.unmodifiableList(builder.getRecipeSlots().getSlots()), craftingGridHelper, focuses);
    }

    @Deprecated(since="19.6.0", forRemoval=true)
    default public boolean handleInput(T recipe, double mouseX, double mouseY, InputConstants.Key input) {
        return this.handleInput(mouseX, mouseY, input);
    }

    default public boolean isHandled(T recipe) {
        return true;
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public List<Component> getTooltipStrings(double mouseX, double mouseY) {
        return Collections.emptyList();
    }

    @Deprecated(since="16.0.0", forRemoval=true)
    default public boolean handleInput(double mouseX, double mouseY, InputConstants.Key input) {
        return false;
    }
}

