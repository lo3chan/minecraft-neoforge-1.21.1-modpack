package net.mehvahdjukaar.moonlight.api.resources.recipe.platform;

import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.recipe.BlockTypeSwapIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModIngredientTypes {
   public static final Supplier<IngredientType<?>> BLOCK_TYPE_SWAP = RegHelper.register(
      BlockTypeSwapIngredient.ID,
      () -> new IngredientType(BlockTypeSwapIngredient.CODEC, BlockTypeSwapIngredient.STREAM_CODEC),
      NeoForgeRegistries.INGREDIENT_TYPES.key()
   );

   public static void register() {
   }
}
