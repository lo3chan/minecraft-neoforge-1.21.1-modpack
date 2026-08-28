/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package mezz.jei.library.plugins.vanilla.compostable;

import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class CompostableRecipeCategory
extends AbstractRecipeCategory<IJeiCompostingRecipe> {
    public CompostableRecipeCategory(IGuiHelper guiHelper) {
        super(RecipeTypes.COMPOSTING, (Component)Component.translatable((String)"gui.jei.category.compostable"), guiHelper.createDrawableItemLike((ItemLike)Blocks.COMPOSTER), 120, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiCompostingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks((List)recipe.getInputs());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiCompostingRecipe recipe, IFocusGroup focuses) {
        float chance = recipe.getChance();
        int chancePercent = (int)Math.floor(chance * 100.0f);
        MutableComponent text = Component.translatable((String)"gui.jei.category.compostable.chance", (Object[])new Object[]{chancePercent});
        ((ITextWidget)builder.addText((FormattedText)text, this.getWidth() - 24, this.getHeight()).setPosition(24, 0)).setTextAlignment(HorizontalAlignment.CENTER).setTextAlignment(VerticalAlignment.CENTER).setColor(-8355712);
    }

    @Override
    public ResourceLocation getRegistryName(IJeiCompostingRecipe recipe) {
        return recipe.getUid();
    }
}

