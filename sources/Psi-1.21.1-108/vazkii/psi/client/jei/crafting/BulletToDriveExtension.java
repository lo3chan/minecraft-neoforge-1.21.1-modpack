package vazkii.psi.client.jei.crafting;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.common.crafting.recipe.BulletToDriveRecipe;
import vazkii.psi.common.item.ItemSpellBullet;
import vazkii.psi.common.item.ItemSpellDrive;
import vazkii.psi.common.item.base.ModItems;

public class BulletToDriveExtension implements ICraftingCategoryExtension<BulletToDriveRecipe> {
   private final List<List<ItemStack>> inputs = ImmutableList.of(
      ImmutableList.of(new ItemStack((ItemLike)ModItems.spellDrive.get())),
      (List)BuiltInRegistries.ITEM.stream().filter(item -> item instanceof ItemSpellBullet).map(ItemStack::new).collect(Collectors.toList())
   );

   public void setRecipe(RecipeHolder<BulletToDriveRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focuses) {
      ItemStack drive = new ItemStack((ItemLike)ModItems.spellDrive.get());
      focuses.getFocuses(VanillaTypes.ITEM_STACK)
         .filter(focus -> ((ItemStack)focus.getTypedValue().getIngredient()).getItem() instanceof ItemSpellBullet)
         .findFirst()
         .map(focus -> (ItemStack)focus.getTypedValue().getIngredient())
         .flatMap(stack -> ISpellAcceptor.hasSpell(stack) ? Optional.ofNullable(ISpellAcceptor.acceptor(stack).getSpell()) : Optional.empty())
         .ifPresent(spell -> ItemSpellDrive.setSpell(drive, spell));
      helper.createAndSetInputs(builder, this.inputs, 0, 0);
      helper.createAndSetOutputs(builder, ImmutableList.of(drive));
   }

   public void drawInfo(
      RecipeHolder<BulletToDriveRecipe> recipeHolder, int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY
   ) {
      guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("jei.psi.spell_copy").withStyle(ChatFormatting.GRAY), 57, 46);
   }
}
