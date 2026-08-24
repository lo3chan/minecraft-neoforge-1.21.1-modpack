package net.cibernet.alchemancy.crafting;

import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ItemTransmutationRecipe extends ForgeItemRecipe {
   public ItemTransmutationRecipe(
      Optional<Ingredient> catalyst, Optional<String> catalystName, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, ItemStack result
   ) {
      super(catalyst, catalystName, List.of(), List.of(), result);
   }

   @Override
   public boolean matches(ForgeRecipeGrid input, Level level) {
      return input.canPerformTransmutation()
         && (this.catalyst.isEmpty() || this.catalyst.get().test(input.getCurrentOutput()))
         && (this.catalystName.isEmpty() || input.getCurrentOutput().getHoverName().getString().equalsIgnoreCase(this.catalystName.get()));
   }

   @Override
   public boolean isTransmutation() {
      return true;
   }

   @Override
   public int getPriority() {
      return 100;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.ITEM_TRANSMUTATION.get();
   }
}
