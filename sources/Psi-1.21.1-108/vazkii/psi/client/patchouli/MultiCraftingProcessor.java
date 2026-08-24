package vazkii.psi.client.patchouli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.psi.common.Psi;

public class MultiCraftingProcessor implements IComponentProcessor {
   private List<CraftingRecipe> recipes;
   private boolean shapeless = true;
   private int longestIngredientSize = 0;
   private boolean hasCustomHeading;

   public void setup(Level level, IVariableProvider variables) {
      if (Minecraft.getInstance().level != null) {
         List<RecipeHolder<CraftingRecipe>> recipeMap = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
         List<String> names = variables.get("recipes", level.registryAccess()).asStream(level.registryAccess()).<String>map(IVariable::asString).toList();
         this.recipes = new ArrayList<>();

         for (String name : names) {
            Optional<RecipeHolder<CraftingRecipe>> recipe = recipeMap.stream().filter(x -> x.id() == ResourceLocation.parse(name)).findFirst();
            if (recipe.isPresent()) {
               this.recipes.add((CraftingRecipe)recipe.get().value());
               if (this.shapeless) {
                  this.shapeless = !(recipe.get().value() instanceof ShapedRecipe);
               }

               for (Ingredient ingredient : ((CraftingRecipe)recipe.get().value()).getIngredients()) {
                  int size = ingredient.getItems().length;
                  if (this.longestIngredientSize < size) {
                     this.longestIngredientSize = size;
                  }
               }
            } else {
               Psi.logger.warn("Missing crafting recipe {}", name);
            }
         }

         this.hasCustomHeading = variables.has("heading");
      }
   }

   @NotNull
   public IVariable process(Level level, String key) {
      if (this.recipes.isEmpty()) {
         return null;
      } else if (key.equals("heading")) {
         return !this.hasCustomHeading
            ? IVariable.from(((CraftingRecipe)this.recipes.getFirst()).getResultItem(RegistryAccess.EMPTY).getHoverName(), level.registryAccess())
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
               .map(recipex -> recipex.getResultItem(RegistryAccess.EMPTY))
               .map(d -> IVariable.from(d, level.registryAccess()))
               .collect(Collectors.toList()),
            level.registryAccess()
         );
      } else {
         return key.equals("shapeless") ? IVariable.wrap(this.shapeless, level.registryAccess()) : null;
      }
   }
}
