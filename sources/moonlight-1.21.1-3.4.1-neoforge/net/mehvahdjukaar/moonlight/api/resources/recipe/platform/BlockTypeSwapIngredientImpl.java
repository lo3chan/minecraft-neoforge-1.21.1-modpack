package net.mehvahdjukaar.moonlight.api.resources.recipe.platform;

import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.resources.recipe.BlockTypeSwapIngredient;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public class BlockTypeSwapIngredientImpl<T extends BlockType> extends BlockTypeSwapIngredient<T> implements ICustomIngredient {
   protected BlockTypeSwapIngredientImpl(Ingredient inner, T fromType, T toType, BlockTypeRegistry<T> reg) {
      super(inner, fromType, toType, reg);
   }

   public Stream<ItemStack> getItems() {
      return this.getMatchingStacks().stream();
   }

   public boolean isSimple() {
      return false;
   }

   public IngredientType<?> getType() {
      return ModIngredientTypes.BLOCK_TYPE_SWAP.get();
   }

   public static <T extends BlockType> BlockTypeSwapIngredient<T> create(Ingredient original, T from, T to, BlockTypeRegistry<T> reg) {
      return new BlockTypeSwapIngredientImpl<>(original, from, to, reg);
   }

   public static <T extends BlockType> Ingredient create(Ingredient original, T from, T to) {
      return new BlockTypeSwapIngredientImpl(original, from, to, from.getRegistry()).toVanilla();
   }
}
