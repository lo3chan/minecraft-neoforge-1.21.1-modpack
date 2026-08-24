package fuzs.puzzleslib.impl.item;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@Deprecated
public class CopyComponentsShapelessRecipe extends ShapelessRecipe implements CopyComponentsRecipe {
   private final RecipeSerializer<?> recipeSerializer;
   private final Ingredient copyFrom;

   public CopyComponentsShapelessRecipe(String modId, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
      this(CopyComponentsRecipe.getModSerializer(modId, "copy_components_shapeless_recipe"), shapelessRecipe, copyFrom);
   }

   public CopyComponentsShapelessRecipe(RecipeSerializer<?> recipeSerializer, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
      super(shapelessRecipe.getGroup(), shapelessRecipe.category(), shapelessRecipe.getResultItem(RegistryAccess.EMPTY), shapelessRecipe.getIngredients());
      this.recipeSerializer = recipeSerializer;
      this.copyFrom = copyFrom;
   }

   public ItemStack assemble(CraftingInput craftingInput, Provider registries) {
      ItemStack itemStack = super.assemble(craftingInput, registries);
      CopyComponentsRecipe.super.copyComponentsToResult(itemStack, craftingInput);
      return itemStack;
   }

   public RecipeSerializer<?> getSerializer() {
      return this.recipeSerializer;
   }

   @Override
   public Ingredient getComponentsSource() {
      return this.copyFrom;
   }
}
