/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 */
package mezz.jei.common.gui;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.SafeIngredientUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class IngredientTooltipComponent<T>
implements ClientTooltipComponent,
TooltipComponent {
    private static final int INGREDIENT_PADDING = 1;
    private final ITypedIngredient<T> typedIngredient;
    private final IIngredientRenderer<T> ingredientRenderer;

    public IngredientTooltipComponent(ITypedIngredient<T> typedIngredient, IIngredientRenderer<T> ingredientRenderer) {
        this.typedIngredient = typedIngredient;
        this.ingredientRenderer = ingredientRenderer;
    }

    public int getHeight() {
        return this.ingredientRenderer.getHeight() + 2;
    }

    public int getWidth(Font font) {
        return this.ingredientRenderer.getWidth() + 2;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        SafeIngredientUtil.render(guiGraphics, this.ingredientRenderer, this.typedIngredient, x + 1, y + 1);
    }
}

