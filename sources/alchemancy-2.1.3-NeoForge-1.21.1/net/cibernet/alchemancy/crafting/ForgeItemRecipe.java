package net.cibernet.alchemancy.crafting;

import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

public class ForgeItemRecipe extends AbstractForgeRecipe<ItemStack> {
   final ItemStack result;

   @Override
   public boolean matches(ForgeRecipeGrid input, Level level) {
      return this.checkParadoxical(input.getCurrentOutput()) && super.matches(input, level);
   }

   public ForgeItemRecipe(
      Optional<Ingredient> catalyst, Optional<String> catalystName, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, ItemStack result
   ) {
      super(catalyst.isPresent() ? catalyst : Optional.of(Ingredient.EMPTY), catalystName, infusables, infusedProperties);
      this.result = result;
   }

   @Override
   public int getPriority() {
      return 0;
   }

   public ItemStack getResult() {
      return this.result;
   }

   @Override
   public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid) {
      return forgeRecipePredicate.outputItem().isEmpty()
         ? TriState.DEFAULT
         : (forgeRecipePredicate.outputItem().get().test(this.result) ? TriState.TRUE : TriState.FALSE);
   }

   @Override
   public TriFunction<ForgeRecipeGrid, Provider, ItemStack, ItemStack> processResult() {
      return (input, registries, output) -> {
         ItemStack result = this.result.copy();
         if (ItemStack.isSameItem(result, input.getCurrentOutput())) {
            result.setCount(result.getCount() + input.getCurrentOutput().getCount() - 1);
         }

         result.set(AlchemancyItems.Components.INFUSED_PROPERTIES, (InfusedPropertiesComponent)output.get(AlchemancyItems.Components.INFUSED_PROPERTIES));
         PropertyDataComponent.mergeData(result, output);
         return result;
      };
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_ITEM.get();
   }
}
