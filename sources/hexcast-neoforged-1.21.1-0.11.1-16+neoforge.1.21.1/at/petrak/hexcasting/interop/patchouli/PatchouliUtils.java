package at.petrak.hexcasting.interop.patchouli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IVariable;

public class PatchouliUtils {
   public static <T extends Recipe<C>, C extends RecipeInput> T getRecipe(RecipeType<T> type, ResourceLocation id) {
      if (Minecraft.getInstance().level == null) {
         return null;
      } else {
         RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
         return manager.byKey(id).filter(recipe -> recipe.value().getType() == type).map(recipe -> (T)recipe.value()).orElse(null);
      }
   }

   public static IVariable interweaveIngredients(List<Ingredient> ingredients, int longestIngredientSize, Provider registries) {
      if (ingredients.size() == 1) {
         return IVariable.wrapList(
            Arrays.stream(ingredients.get(0).getItems()).map(stackx -> IVariable.from(stackx, registries)).collect(Collectors.toList()), registries
         );
      } else {
         ItemStack[] empty = new ItemStack[]{ItemStack.EMPTY};
         List<ItemStack[]> stacks = new ArrayList<>();

         for (Ingredient ingredient : ingredients) {
            if (ingredient != null && !ingredient.isEmpty()) {
               stacks.add(ingredient.getItems());
            } else {
               stacks.add(empty);
            }
         }

         List<IVariable> list = new ArrayList<>(stacks.size() * longestIngredientSize);

         for (int i = 0; i < longestIngredientSize; i++) {
            for (ItemStack[] stack : stacks) {
               list.add(IVariable.from(stack[i % stack.length], registries));
            }
         }

         return IVariable.wrapList(list, registries);
      }
   }

   public static IVariable interweaveIngredients(List<Ingredient> ingredients, Provider registries) {
      return interweaveIngredients(ingredients, ingredients.stream().mapToInt(ingr -> ingr.getItems().length).max().orElse(1), registries);
   }
}
