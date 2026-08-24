package vazkii.psi.client.patchouli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;
import vazkii.psi.api.spell.SpellPiece;

public class PatchouliUtils {
   public static IVariable interweaveIngredients(List<Ingredient> ingredients, int longestIngredientSize, Provider registries) {
      if (ingredients.size() == 1) {
         return IVariable.wrapList(
            Arrays.stream(((Ingredient)ingredients.getFirst()).getItems()).map(d -> IVariable.from(d, registries)).collect(Collectors.toList()), registries
         );
      } else {
         ItemStack[] empty = new ItemStack[]{ItemStack.EMPTY};
         List<ItemStack[]> stacks = new ArrayList<>();

         for (Ingredient ingredient : ingredients) {
            if (ingredient != null && !ingredient.isEmpty()) {
               stacks.add(ingredient.getItems());
            } else {
               stacks.add(empty);
            }
         }

         List<IVariable> list = new ArrayList<>(stacks.size() * longestIngredientSize);

         for (int i = 0; i < longestIngredientSize; i++) {
            for (ItemStack[] stack : stacks) {
               list.add(IVariable.from(stack[i % stack.length], registries));
            }
         }

         return IVariable.wrapList(list, registries);
      }
   }

   public static void setPieceTooltip(IComponentRenderContext context, SpellPiece piece) {
      List<Component> tooltip = new ArrayList<>();
      piece.getTooltip(tooltip);
      context.setHoverTooltipComponents(tooltip);
   }
}
