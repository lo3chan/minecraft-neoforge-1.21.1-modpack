package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.Arrays;
import java.util.List;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class ShulkerBoxColoringRecipeMaker {
   private static final String group = "jei.shulker.color";

   public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
      Ingredient baseShulkerIngredient = Ingredient.of(new ItemLike[]{Blocks.SHULKER_BOX});
      return Arrays.stream(DyeColor.values()).map(color -> createRecipe(color, baseShulkerIngredient)).toList();
   }

   private static RecipeHolder<CraftingRecipe> createRecipe(DyeColor color, Ingredient baseShulkerIngredient) {
      IPlatformIngredientHelper ingredientHelper = Services.PLATFORM.getIngredientHelper();
      Ingredient colorIngredient = ingredientHelper.createShulkerDyeIngredient(color);
      NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, new Ingredient[]{baseShulkerIngredient, colorIngredient});
      Block coloredShulkerBox = ShulkerBoxBlock.getBlockByColor(color);
      ItemStack output = new ItemStack(coloredShulkerBox);
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("minecraft", "jei.shulker.color." + output.getDescriptionId());
      CraftingRecipe recipe = new ShapelessRecipe("jei.shulker.color", CraftingBookCategory.MISC, output, inputs);
      return new RecipeHolder(id, recipe);
   }

   private ShulkerBoxColoringRecipeMaker() {
   }
}
