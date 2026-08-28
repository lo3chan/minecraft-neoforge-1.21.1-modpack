/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.library.gui.recipes.supplier.builder;

import java.util.EnumMap;
import java.util.Map;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.gui.recipes.RecipeLayoutIngredientSupplier;
import mezz.jei.library.gui.recipes.supplier.builder.IngredientSlotBuilder;

public class IngredientSupplierBuilder
implements IRecipeLayoutBuilder {
    private final IIngredientManager ingredientManager;
    private final Map<RecipeIngredientRole, IngredientSlotBuilder> ingredientSlotBuilders;

    public IngredientSupplierBuilder(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
        this.ingredientSlotBuilders = new EnumMap<RecipeIngredientRole, IngredientSlotBuilder>(RecipeIngredientRole.class);
    }

    @Override
    public IRecipeSlotBuilder addSlot(RecipeIngredientRole role, int x, int y) {
        return this.addSlot(role);
    }

    @Override
    public IRecipeSlotBuilder addSlot(RecipeIngredientRole role) {
        IngredientSlotBuilder slot = this.ingredientSlotBuilders.get((Object)role);
        if (slot == null) {
            slot = new IngredientSlotBuilder(this.ingredientManager);
            this.ingredientSlotBuilders.put(role, slot);
        }
        return slot;
    }

    @Override
    public IRecipeSlotBuilder addSlotToWidget(RecipeIngredientRole role, ISlottedWidgetFactory<?> widgetFactory) {
        return this.addSlot(role);
    }

    @Override
    public IIngredientAcceptor<?> addInvisibleIngredients(RecipeIngredientRole role) {
        return this.addSlot(role);
    }

    @Override
    public void moveRecipeTransferButton(int posX, int posY) {
    }

    @Override
    public void setShapeless() {
    }

    @Override
    public void setShapeless(int posX, int posY) {
    }

    @Override
    public void createFocusLink(IIngredientAcceptor<?> ... slots) {
    }

    public IIngredientSupplier buildIngredientSupplier() {
        return new RecipeLayoutIngredientSupplier(this.ingredientSlotBuilders);
    }
}

