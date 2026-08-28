/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.grindstone;

import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class GrindstoneRecipeCategory
extends AbstractRecipeCategory<IJeiGrindstoneRecipe> {
    private static final String topSlotName = "topSlot";
    private static final String bottomSlotName = "bottomSlot";

    public GrindstoneRecipeCategory(IGuiHelper guiHelper) {
        super(RecipeTypes.GRINDSTONE, (Component)Blocks.GRINDSTONE.getName(), guiHelper.createDrawableItemLike((ItemLike)Blocks.GRINDSTONE), 125, 52);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiGrindstoneRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> topInputs = recipe.getTopInputs();
        List<ItemStack> bottomInputs = recipe.getBottomInputs();
        List<ItemStack> outputs = recipe.getOutputs();
        IRecipeSlotBuilder topInputSlot = ((IRecipeSlotBuilder)builder.addInputSlot(1, 1).addItemStacks((List)topInputs)).setStandardSlotBackground().setSlotName(topSlotName);
        IRecipeSlotBuilder bottomInputSlot = ((IRecipeSlotBuilder)builder.addInputSlot(1, 24).addItemStacks((List)bottomInputs)).setStandardSlotBackground().setSlotName(bottomSlotName);
        int outputSlotXPosition = 52;
        int outputSlotYPosition = 13;
        IRecipeSlotBuilder outputSlot = recipe.isOutputRenderOnly() ? builder.addSlot(RecipeIngredientRole.RENDER_ONLY, outputSlotXPosition, outputSlotYPosition) : builder.addOutputSlot(outputSlotXPosition, outputSlotYPosition);
        outputSlot.setOutputSlotBackground().addItemStacks((List)outputs);
        if (topInputs.size() == bottomInputs.size()) {
            if (topInputs.size() == outputs.size()) {
                builder.createFocusLink(topInputSlot, bottomInputSlot, outputSlot);
            }
        } else if (topInputs.size() == outputs.size() && bottomInputs.size() == 1) {
            builder.createFocusLink(topInputSlot, outputSlot);
        } else if (bottomInputs.size() == outputs.size() && topInputs.size() == 1) {
            builder.createFocusLink(bottomInputSlot, outputSlot);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiGrindstoneRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(20, 12);
        int maxXpReward = recipe.getMaxXpReward();
        if (maxXpReward > 0) {
            int minXpReward = recipe.getMinXpReward();
            MutableComponent text = Component.translatable((String)"gui.jei.category.grindstone.experience", (Object[])new Object[]{minXpReward, maxXpReward});
            ((ITextWidget)builder.addText((FormattedText)text, this.getWidth(), 10).setPosition(0, 43)).setColor(-8323296).setShadow(true).setTextAlignment(HorizontalAlignment.RIGHT);
        }
    }

    @Override
    @Nullable
    public ResourceLocation getRegistryName(IJeiGrindstoneRecipe recipe) {
        return recipe.getUid();
    }
}

