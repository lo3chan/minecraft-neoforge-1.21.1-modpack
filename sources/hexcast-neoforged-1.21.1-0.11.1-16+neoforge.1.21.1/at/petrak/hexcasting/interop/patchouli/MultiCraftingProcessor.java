package at.petrak.hexcasting.interop.patchouli;

import at.petrak.hexcasting.api.HexAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class MultiCraftingProcessor implements IComponentProcessor {
   private List<CraftingRecipe> recipes;
   private boolean shapeless = true;
   private int longestIngredientSize = 0;
   private boolean hasCustomHeading;

   public void setup(Level level, IVariableProvider vars) {
      List<String> names = vars.get("recipes", level.registryAccess())
         .asStream(level.registryAccess())
         .<String>map(IVariable::asString)
         .collect(Collectors.toList());
      this.recipes = new ArrayList<>();

      for (String name : names) {
         CraftingRecipe recipe = PatchouliUtils.getRecipe(RecipeType.CRAFTING, ResourceLocation.parse(name));
         if (recipe != null) {
            this.recipes.add(recipe);
            if (this.shapeless) {
               this.shapeless = !(recipe instanceof ShapedRecipe);
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
               int size = ingredient.getItems().length;
               if (this.longestIngredientSize < size) {
                  this.longestIngredientSize = size;
               }
            }
         } else {
            HexAPI.LOGGER.warn("Missing crafting recipe " + name);
         }
      }

      this.hasCustomHeading = vars.has("heading");
   }

   public IVariable process(Level level, String key) {
      if (this.recipes.isEmpty()) {
         return null;
      } else if (key.equals("heading")) {
         return !this.hasCustomHeading
            ? IVariable.from(this.recipes.get(0).getResultItem(level.registryAccess()).getHoverName(), level.registryAccess())
            : null;
      } else if (key.startsWith("input")) {
         int index = Integer.parseInt(key.substring(5)) - 1;
         int shapedX = index % 3;
         int shapedY = index / 3;
         List<Ingredient> ingredients = new ArrayList<>();

         for (CraftingRecipe recipe : this.recipes) {
            if (recipe instanceof ShapedRecipe shaped) {
               if (shaped.getWidth() < shapedX + 1) {
                  ingredients.add(Ingredient.EMPTY);
               } else {
                  int realIndex = index - shapedY * (3 - shaped.getWidth());
                  NonNullList<Ingredient> list = recipe.getIngredients();
                  ingredients.add(list.size() > realIndex ? (Ingredient)list.get(realIndex) : Ingredient.EMPTY);
               }
            } else {
               NonNullList<Ingredient> list = recipe.getIngredients();
               ingredients.add(list.size() > index ? (Ingredient)list.get(index) : Ingredient.EMPTY);
            }
         }

         return PatchouliUtils.interweaveIngredients(ingredients, this.longestIngredientSize, level.registryAccess());
      } else if (key.equals("output")) {
         return IVariable.wrapList(
            this.recipes
               .stream()
               .map(recipex -> recipex.getResultItem(level.registryAccess()))
               .map(stack -> IVariable.from(stack, level.registryAccess()))
               .collect(Collectors.toList()),
            level.registryAccess()
         );
      } else {
         return key.equals("shapeless") ? IVariable.wrap(this.shapeless, level.registryAccess()) : null;
      }
   }
}
