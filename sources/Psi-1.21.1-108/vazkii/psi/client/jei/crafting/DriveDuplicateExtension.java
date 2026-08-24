package vazkii.psi.client.jei.crafting;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import vazkii.psi.common.crafting.recipe.DriveDuplicateRecipe;
import vazkii.psi.common.item.ItemSpellDrive;
import vazkii.psi.common.item.base.ModItems;

public class DriveDuplicateExtension implements ICraftingCategoryExtension<DriveDuplicateRecipe> {
   public void setRecipe(RecipeHolder<DriveDuplicateRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focuses) {
      ItemStack drive = new ItemStack((ItemLike)ModItems.spellDrive.get());
      focuses.getFocuses(VanillaTypes.ITEM_STACK)
         .filter(focus -> ((ItemStack)focus.getTypedValue().getIngredient()).getItem() instanceof ItemSpellDrive)
         .findFirst()
         .map(focus -> (ItemStack)focus.getTypedValue().getIngredient())
         .filter(stack -> ItemSpellDrive.getSpell(stack) != null)
         .ifPresent(stack -> ItemSpellDrive.setSpell(drive, ItemSpellDrive.getSpell(stack)));
      helper.createAndSetInputs(builder, ImmutableList.of(ImmutableList.of(drive), ImmutableList.of(new ItemStack((ItemLike)ModItems.spellDrive.get()))), 0, 0);
      helper.createAndSetOutputs(builder, ImmutableList.of(drive));
   }

   public void drawInfo(RecipeHolder<DriveDuplicateRecipe> recipeHolder, int recipeWidth, int recipeHeight, GuiGraphics graphics, double mouseX, double mouseY) {
      graphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.psi.spell_copy"), 57, 46, 8421504);
   }
}
