package net.Pandarix.recipe;

import net.minecraft.world.item.crafting.SingleItemRecipe.Serializer;

public class IdentifyingRecipeSerializer extends Serializer<IdentifyingRecipe> {
   public IdentifyingRecipeSerializer() {
      super(IdentifyingRecipe::new);
   }
}
