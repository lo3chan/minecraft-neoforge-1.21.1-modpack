/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.library.gui.recipes.layout.builder;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;

public class LegacyTooltipCallbackAdapter
implements IRecipeSlotRichTooltipCallback {
    private final IRecipeSlotTooltipCallback callback;

    public LegacyTooltipCallbackAdapter(IRecipeSlotTooltipCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
        this.callback.onRichTooltip(recipeSlotView, tooltip);
    }
}

