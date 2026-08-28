/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.recipes;

import java.util.function.Supplier;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.IngredientsTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

public class IngredientsTooltipCallback
implements IRecipeSlotRichTooltipCallback {
    private final Supplier<@Nullable IRecipeLayoutDrawable<?>> recipeLayoutSupplier;

    public IngredientsTooltipCallback(Supplier<@Nullable IRecipeLayoutDrawable<?>> supplier) {
        this.recipeLayoutSupplier = supplier;
    }

    @Override
    public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
        IRecipeLayoutDrawable<?> recipeLayout;
        if (Internal.getJeiClientConfigs().getClientConfig().ingredientsSummaryEnabled().getValue().booleanValue() && (recipeLayout = this.recipeLayoutSupplier.get()) != null) {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.recipe.tooltips.craft.ingredients").withStyle(ChatFormatting.GRAY));
            tooltip.add(new IngredientsTooltipComponent(recipeLayout));
        }
    }
}

