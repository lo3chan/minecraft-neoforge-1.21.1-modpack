package net.cibernet.alchemancy.modSupport.patchouli;

import net.cibernet.alchemancy.crafting.ItemTransmutationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class TransmutationComponentProcessor implements IComponentProcessor {
   private ItemTransmutationRecipe recipe;
   private boolean hasTitle;

   public void setup(Level level, IVariableProvider variables) {
      String key = variables.get("recipe", level.registryAccess()).asString();
      if (((RecipeHolder)level.getRecipeManager()
            .byKey(ResourceLocation.parse(key))
            .orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")))
         .value() instanceof ItemTransmutationRecipe r) {
         this.recipe = r;
         this.hasTitle = variables.has("title");
      } else {
         throw new IllegalArgumentException(key + " is not a valid transmutation recipe");
      }
   }

   public IVariable process(Level level, String key) {
      if (key.equals("input")) {
         return IVariable.from(this.recipe.getCatalyst().orElse(Ingredient.EMPTY), level.registryAccess());
      } else if (key.equals("output")) {
         return IVariable.from(this.recipe.getResultItem(level.registryAccess()), level.registryAccess());
      } else {
         return !this.hasTitle && key.equals("title")
            ? IVariable.from(this.recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess())
            : null;
      }
   }
}
