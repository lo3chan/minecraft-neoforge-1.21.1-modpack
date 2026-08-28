/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.recipes;

import java.util.Optional;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class OutputSlotTooltipCallback
implements IRecipeSlotRichTooltipCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation recipeName;
    private final boolean recipeFromSameModAsCategory;

    public OutputSlotTooltipCallback(ResourceLocation recipeName, RecipeType<?> recipeType) {
        this.recipeName = recipeName;
        this.recipeFromSameModAsCategory = recipeName.getNamespace().equals(recipeType.getUid().getNamespace());
    }

    @Override
    public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
        boolean showAdvanced;
        if (recipeSlotView.getRole() != RecipeIngredientRole.OUTPUT) {
            return;
        }
        Optional<ITypedIngredient<?>> displayedIngredient = recipeSlotView.getDisplayedIngredient();
        if (displayedIngredient.isEmpty()) {
            return;
        }
        this.addRecipeBy(tooltip, displayedIngredient.get());
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = showAdvanced = minecraft.options.advancedItemTooltips || Screen.hasShiftDown();
        if (showAdvanced) {
            MutableComponent recipeId = Component.translatable((String)"jei.tooltip.recipe.id", (Object[])new Object[]{Component.literal((String)this.recipeName.toString())});
            tooltip.add((FormattedText)recipeId.withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private void addRecipeBy(ITooltipBuilder tooltip, ITypedIngredient<?> displayedIngredient) {
        if (this.recipeFromSameModAsCategory) {
            return;
        }
        IModIdHelper modIdHelper = Internal.getJeiRuntime().getJeiHelpers().getModIdHelper();
        if (!modIdHelper.isDisplayingModNameEnabled()) {
            return;
        }
        String ingredientModId = this.getDisplayModId(displayedIngredient);
        if (ingredientModId == null) {
            return;
        }
        String recipeModId = this.recipeName.getNamespace();
        if (recipeModId.equals(ingredientModId)) {
            return;
        }
        Component modName = modIdHelper.getFormattedModNameComponentForModId(recipeModId);
        MutableComponent recipeBy = Component.translatable((String)"jei.tooltip.recipe.by", (Object[])new Object[]{modName});
        tooltip.add((FormattedText)recipeBy.withStyle(ChatFormatting.GRAY));
    }

    @Nullable
    private <T> String getDisplayModId(ITypedIngredient<T> typedIngredient) {
        IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
        IIngredientType<T> type = typedIngredient.getType();
        T ingredient = typedIngredient.getIngredient();
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(type);
        try {
            return ingredientHelper.getDisplayModId(ingredient);
        }
        catch (RuntimeException e) {
            String ingredientInfo = ErrorUtil.getIngredientInfo(ingredient, type, ingredientManager);
            LOGGER.error("Caught exception from ingredient without a resource location: {}", (Object)ingredientInfo, (Object)e);
            return null;
        }
    }
}

