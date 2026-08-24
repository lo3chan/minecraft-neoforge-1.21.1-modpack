package net.cibernet.alchemancy.crafting;

import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

public class ForgeRemovePropertiesRecipe extends AbstractForgeRecipe<Optional<ItemStack>> {
   final Optional<ItemStack> result;

   public ForgeRemovePropertiesRecipe(
      Optional<Ingredient> catalyst,
      Optional<String> catalystName,
      List<Ingredient> infusables,
      List<Holder<Property>> infusedProperties,
      Optional<ItemStack> result
   ) {
      super(catalyst.isPresent() ? catalyst : Optional.of(Ingredient.EMPTY), catalystName, infusables, infusedProperties);
      this.result = result;
   }

   @Override
   public boolean matches(ForgeRecipeGrid input, Level level) {
      return (this.catalyst.isEmpty() || this.catalyst.get().isEmpty() || this.catalyst.get().test(input.getCurrentOutput()))
         && (this.catalystName.isEmpty() || input.getCurrentOutput().getDisplayName().getString().equalsIgnoreCase(this.catalystName.get()))
         && input.testInfusables(this.infusables, false)
         && input.testProperties(this.infusedProperties, false);
   }

   @Override
   public int getPriority() {
      return 60;
   }

   public Optional<ItemStack> getResult() {
      return this.result;
   }

   @Override
   public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid) {
      return forgeRecipePredicate.outputItem().isEmpty()
         ? TriState.DEFAULT
         : (!this.result.isEmpty() && !forgeRecipePredicate.outputItem().get().test(this.result.get()) ? TriState.FALSE : TriState.TRUE);
   }

   @Override
   public TriFunction<ForgeRecipeGrid, Provider, ItemStack, ItemStack> processResult() {
      return (input, registries, output) -> {
         ItemStack result = this.result.<ItemStack>map(ItemStack::copy).orElseGet(input::getCurrentOutput);
         if (ItemStack.isSameItem(result, input.getCurrentOutput())) {
            result.setCount(result.getCount() + input.getCurrentOutput().getCount() - 1);
         }

         return InfusedPropertiesHelper.clearAllInfusions(result);
      };
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result.orElse(ItemStack.EMPTY);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.REMOVE_PROPERTIES.get();
   }
}
