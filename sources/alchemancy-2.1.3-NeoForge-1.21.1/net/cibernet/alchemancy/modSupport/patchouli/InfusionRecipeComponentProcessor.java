package net.cibernet.alchemancy.modSupport.patchouli;

import java.util.List;
import java.util.stream.Collectors;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class InfusionRecipeComponentProcessor implements IComponentProcessor {
   private AbstractForgeRecipe<?> recipe;
   private boolean hasTitle;

   public void setup(Level level, IVariableProvider variables) {
      String key = variables.get("recipe", level.registryAccess()).asString();
      if (((RecipeHolder)level.getRecipeManager()
            .byKey(ResourceLocation.parse(key))
            .orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")))
         .value() instanceof AbstractForgeRecipe r) {
         this.recipe = r;
         this.hasTitle = variables.has("title");
      } else {
         throw new IllegalArgumentException(key + " is not a valid infusion recipe");
      }
   }

   public IVariable process(Level level, String key) {
      if (key.equals("catalyst")) {
         return IVariable.from(this.recipe.getCatalyst().orElse(Ingredient.EMPTY), level.registryAccess());
      } else if (key.equals("output")) {
         return IVariable.from(this.recipe.getResultItem(level.registryAccess()), level.registryAccess());
      } else if (!this.hasTitle && key.equals("title")) {
         if (this.recipe instanceof ForgePropertyRecipe recipe1) {
            List<Holder<Property>> result = recipe1.getResult();
            return result.isEmpty()
               ? IVariable.from(Component.translatable("alchemancy.entry.infusion_removal"), level.registryAccess())
               : IVariable.wrap(
                  result.stream().map(propertyHolder -> ((Property)propertyHolder.value()).getName().getString()).collect(Collectors.joining(" and ")),
                  level.registryAccess()
               );
         } else {
            return IVariable.from(this.recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());
         }
      } else {
         return null;
      }
   }
}
