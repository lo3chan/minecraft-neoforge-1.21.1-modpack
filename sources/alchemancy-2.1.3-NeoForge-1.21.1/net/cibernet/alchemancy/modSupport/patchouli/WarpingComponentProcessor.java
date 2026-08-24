package net.cibernet.alchemancy.modSupport.patchouli;

import java.util.stream.Collectors;
import net.cibernet.alchemancy.crafting.PropertyWarpRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class WarpingComponentProcessor implements IComponentProcessor {
   private PropertyWarpRecipe recipe;
   private boolean hasTitle;

   public void setup(Level level, IVariableProvider variables) {
      String key = variables.get("recipe", level.registryAccess()).asString();
      if (((RecipeHolder)level.getRecipeManager()
            .byKey(ResourceLocation.parse(key))
            .orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")))
         .value() instanceof PropertyWarpRecipe r) {
         this.recipe = r;
         this.hasTitle = variables.has("title");
      } else {
         throw new IllegalArgumentException(key + " is not a valid warping recipe");
      }
   }

   public IVariable process(Level level, String key) {
      if (key.equals("input")) {
         return IVariable.from(InfusedPropertiesHelper.createPropertyIngredient(this.recipe.getInfusedProperties()), level.registryAccess());
      } else if (key.equals("output")) {
         return IVariable.from(InfusedPropertiesHelper.createPropertyIngredient(this.recipe.getResult()), level.registryAccess());
      } else {
         return !this.hasTitle && key.equals("title")
            ? IVariable.wrap(
               this.recipe
                  .getResult()
                  .stream()
                  .map(propertyHolder -> ((Property)propertyHolder.value()).getDisplayText(ItemStack.EMPTY).getString())
                  .collect(Collectors.joining(" and ")),
               level.registryAccess()
            )
            : null;
      }
   }
}
