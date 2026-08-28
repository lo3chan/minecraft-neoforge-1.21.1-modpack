/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.anvil;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.library.plugins.vanilla.anvil.AnvilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class AnvilRecipeCategory
extends AbstractRecipeCategory<IJeiAnvilRecipe> {
    private static final String leftSlotName = "leftSlot";
    private static final String rightSlotName = "rightSlot";

    public AnvilRecipeCategory(IGuiHelper guiHelper) {
        super(RecipeTypes.ANVIL, (Component)Blocks.ANVIL.getName(), guiHelper.createDrawableItemLike((ItemLike)Blocks.ANVIL), 125, 38);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiAnvilRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> leftInputs = recipe.getLeftInputs();
        List<ItemStack> rightInputs = recipe.getRightInputs();
        List<ItemStack> outputs = recipe.getOutputs();
        IRecipeSlotBuilder leftInputSlot = ((IRecipeSlotBuilder)builder.addInputSlot(1, 1).addItemStacks((List)leftInputs)).setStandardSlotBackground().setSlotName(leftSlotName);
        IRecipeSlotBuilder rightInputSlot = ((IRecipeSlotBuilder)builder.addInputSlot(50, 1).addItemStacks((List)rightInputs)).setStandardSlotBackground().setSlotName(rightSlotName);
        IRecipeSlotBuilder outputSlot = (IRecipeSlotBuilder)builder.addOutputSlot(108, 1).setStandardSlotBackground().addItemStacks((List)outputs);
        if (leftInputs.size() == rightInputs.size()) {
            if (leftInputs.size() == outputs.size()) {
                builder.createFocusLink(leftInputSlot, rightInputSlot, outputSlot);
            }
        } else if (leftInputs.size() == outputs.size() && rightInputs.size() == 1) {
            builder.createFocusLink(leftInputSlot, outputSlot);
        } else if (rightInputs.size() == outputs.size() && leftInputs.size() == 1) {
            builder.createFocusLink(rightInputSlot, outputSlot);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiAnvilRecipe recipe, IFocusGroup focuses) {
        builder.addRecipePlusSign().setPosition(27, 3);
        builder.addRecipeArrow().setPosition(76, 1);
        Integer cost = this.getCost(builder.getRecipeSlots());
        if (cost != null) {
            String costText = cost < 0 ? "err" : Integer.toString(cost);
            MutableComponent text = Component.translatable((String)"container.repair.cost", (Object[])new Object[]{costText});
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            int textColor = AnvilRecipeCategory.playerHasEnoughLevels(player, cost) ? -8323296 : -40864;
            ((ITextWidget)builder.addText((FormattedText)text, this.getWidth() - 4, 10).setPosition(2, 27)).setColor(textColor).setShadow(true).setTextAlignment(HorizontalAlignment.RIGHT);
        }
    }

    @Nullable
    private Integer getCost(IRecipeSlotDrawablesView recipeSlotsView) {
        Optional leftStack = recipeSlotsView.findSlotByName(leftSlotName).flatMap(IRecipeSlotView::getDisplayedItemStack);
        Optional rightStack = recipeSlotsView.findSlotByName(rightSlotName).flatMap(IRecipeSlotView::getDisplayedItemStack);
        if (leftStack.isEmpty() || rightStack.isEmpty()) {
            return null;
        }
        return AnvilHelper.findLevelsCost((ItemStack)leftStack.get(), (ItemStack)rightStack.get());
    }

    @Override
    @Nullable
    public ResourceLocation getRegistryName(IJeiAnvilRecipe recipe) {
        return recipe.getUid();
    }

    private static boolean playerHasEnoughLevels(@Nullable LocalPlayer player, int cost) {
        if (player == null) {
            return true;
        }
        if (player.isCreative()) {
            return true;
        }
        return cost < 40 && cost <= player.experienceLevel;
    }
}

