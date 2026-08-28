/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nonnegative
 *  net.minecraft.client.gui.GuiGraphics
 */
package mezz.jei.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnegative;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.input.ClickableIngredientInternal;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.overlay.elements.IngredientElement;
import net.minecraft.client.gui.GuiGraphics;

public class RecipeCatalysts
implements IRecipeFocusSource {
    private static final int ingredientSize = 16;
    private static final int ingredientBorderSize = 1;
    private static final int borderSize = 5;
    private static final int overlapSize = 6;
    private final ScalableDrawable backgroundTab;
    private final List<IRecipeSlotDrawable> recipeSlots;
    private final ScalableDrawable slotBackground;
    private final IRecipeManager recipeManager;
    private int left = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;

    public RecipeCatalysts(IRecipeManager recipeManager) {
        this.recipeManager = recipeManager;
        this.recipeSlots = new ArrayList<IRecipeSlotDrawable>();
        Textures textures = Internal.getTextures();
        this.backgroundTab = textures.getCatalystTab();
        this.slotBackground = textures.getRecipeCatalystSlotBackground();
    }

    public boolean isEmpty() {
        return this.recipeSlots.isEmpty();
    }

    @Nonnegative
    public int getWidth() {
        return Math.max(0, this.width - 6);
    }

    public void updateLayout(List<ITypedIngredient<?>> ingredients, ImmutableRect2i recipeArea, ImmutableRect2i optionButtonsArea) {
        this.recipeSlots.clear();
        Layout layout = RecipeCatalysts.calculateLayout(ingredients.size(), recipeArea, optionButtonsArea);
        this.left = layout.left();
        this.top = layout.top();
        this.width = layout.width();
        this.height = layout.height();
        if (layout.hasSlots()) {
            for (int i = 0; i < ingredients.size(); ++i) {
                ITypedIngredient<?> ingredientForSlot = ingredients.get(i);
                IRecipeSlotDrawable recipeSlot = this.createSlot(ingredientForSlot, i, layout.maxIngredientsPerColumn());
                this.recipeSlots.add(recipeSlot);
            }
        }
    }

    static Layout calculateLayout(int ingredientCount, ImmutableRect2i recipeArea, ImmutableRect2i optionButtonsArea) {
        if (ingredientCount <= 0) {
            return Layout.EMPTY;
        }
        int availableHeight = recipeArea.getHeight() - optionButtonsArea.getHeight() - 8;
        int borderHeight = 12;
        int maxIngredientsPerColumn = Math.max(1, (availableHeight - borderHeight) / 16);
        int columnCount = MathUtil.divideCeil(ingredientCount, maxIngredientsPerColumn);
        maxIngredientsPerColumn = MathUtil.divideCeil(ingredientCount, columnCount);
        int width = 12 + columnCount * 16;
        int height = 12 + maxIngredientsPerColumn * 16;
        int top = recipeArea.getY();
        int left = recipeArea.getX() - width + 6;
        return new Layout(left, top, width, height, maxIngredientsPerColumn);
    }

    private <T> IRecipeSlotDrawable createSlot(ITypedIngredient<T> typedIngredient, int index, int maxIngredientsPerColumn) {
        int column = index / maxIngredientsPerColumn;
        int row = index % maxIngredientsPerColumn;
        IRecipeSlotDrawable recipeSlotDrawable = this.recipeManager.createRecipeSlotDrawable(RecipeIngredientRole.CATALYST, List.of(Optional.of(typedIngredient)), (Set<Integer>)IntSet.of((int)0), 0);
        recipeSlotDrawable.setPosition(this.left + 5 + column * 16 + 1, this.top + 5 + row * 16 + 1);
        return recipeSlotDrawable;
    }

    public Optional<IRecipeSlotDrawable> draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int ingredientCount = this.recipeSlots.size();
        if (ingredientCount > 0) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.disableDepthTest();
            int slotWidth = this.width - 10;
            int slotHeight = this.height - 10;
            this.backgroundTab.draw(guiGraphics, this.left, this.top, this.width, this.height);
            this.slotBackground.draw(guiGraphics, this.left + 5, this.top + 5, slotWidth, slotHeight);
            RenderSystem.enableDepthTest();
            IRecipeSlotDrawable hovered = null;
            for (IRecipeSlotDrawable recipeSlot : this.recipeSlots) {
                if (recipeSlot.isMouseOver(mouseX, mouseY)) {
                    hovered = recipeSlot;
                }
                recipeSlot.draw(guiGraphics, hovered == recipeSlot);
            }
            return Optional.ofNullable(hovered);
        }
        return Optional.empty();
    }

    private Stream<IRecipeSlotDrawable> getHovered(double mouseX, double mouseY) {
        return this.recipeSlots.stream().filter(recipeSlot -> recipeSlot.isMouseOver(mouseX, mouseY));
    }

    @Override
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        return this.getHovered(mouseX, mouseY).map(recipeSlot -> recipeSlot.getDisplayedIngredient().map(i -> {
            IngredientElement element = new IngredientElement(i);
            return new ClickableIngredientInternal(element, recipeSlot::isMouseOver, false, true);
        })).flatMap(Optional::stream);
    }

    @Override
    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
        return Stream.empty();
    }

    record Layout(int left, int top, int width, int height, int maxIngredientsPerColumn) {
        private static final Layout EMPTY = new Layout(0, 0, 0, 0, 0);

        private boolean hasSlots() {
            return this.maxIngredientsPerColumn > 0;
        }
    }
}

