package mezz.jei.api.recipe.vanilla;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.jetbrains.annotations.Nullable;

public interface IVanillaRecipeFactory {
   IJeiAnvilRecipe createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3, @Nullable ResourceLocation var4);

   IJeiAnvilRecipe createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3, ResourceLocation var4);

   IJeiGrindstoneRecipe createGrindstoneRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3, int var4, int var5, ResourceLocation var6);

   IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3, ResourceLocation var4);

   IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3, ResourceLocation var4);

   IJeiShapedRecipeBuilder createShapedRecipeBuilder(CraftingBookCategory var1, List<ItemStack> var2);

   @Deprecated(
      since = "19.1.0"
   )
   IJeiAnvilRecipe createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);

   @Deprecated(
      since = "19.1.0"
   )
   IJeiAnvilRecipe createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3);

   @Deprecated(
      since = "19.1.0"
   )
   IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3);

   @Deprecated(
      since = "19.1.0"
   )
   IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3);
}
