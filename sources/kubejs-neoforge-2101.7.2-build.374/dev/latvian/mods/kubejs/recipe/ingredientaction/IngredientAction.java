package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public interface IngredientAction {
   Codec<IngredientAction> CODEC = IngredientActionType.CODEC.dispatch("type", IngredientAction::getType, IngredientActionType::codec);
   StreamCodec<RegistryFriendlyByteBuf, IngredientAction> STREAM_CODEC = IngredientActionType.STREAM_CODEC
      .dispatch(IngredientAction::getType, IngredientActionType::streamCodec);

   static ItemStack getRemaining(CraftingInput input, int index, List<IngredientActionHolder> ingredientActions) {
      ItemStack stack = input.getItem(index);
      if (stack != null && !stack.isEmpty()) {
         for (IngredientActionHolder holder : ingredientActions) {
            if (holder.filter().checkFilter(index, stack)) {
               return holder.action().transform(stack.copy(), index, input);
            }
         }

         return stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
      } else {
         return ItemStack.EMPTY;
      }
   }

   IngredientActionType<?> getType();

   ItemStack transform(ItemStack old, int index, CraftingInput input);
}
