package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe.Serializer;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
   private final List<IngredientActionHolder> ingredientActions;
   private final String modifyResult;

   public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientActionHolder> ingredientActions, String modifyResult) {
      super(original.getGroup(), original.category(), original.result, original.getIngredients());
      this.ingredientActions = ingredientActions;
      this.modifyResult = modifyResult;
   }

   public RecipeSerializer<?> getSerializer() {
      return KubeJSRecipeSerializers.SHAPELESS.get();
   }

   @Override
   public List<IngredientActionHolder> kjs$getIngredientActions() {
      return this.ingredientActions;
   }

   @Override
   public String kjs$getModifyResult() {
      return this.modifyResult;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
      return this.kjs$getRemainingItems(input);
   }

   public ItemStack assemble(CraftingInput input, Provider registryAccess) {
      return this.kjs$assemble(input, registryAccess);
   }

   public static class SerializerKJS implements RecipeSerializer<ShapelessKubeJSRecipe> {
      public static final MapCodec<ShapelessKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Serializer.CODEC.forGetter(r -> r),
               IngredientActionHolder.LIST_CODEC
                  .optionalFieldOf("kubejs:ingredient_actions", List.of())
                  .forGetter(ShapelessKubeJSRecipe::kjs$getIngredientActions),
               Codec.STRING.optionalFieldOf("kubejs:modify_result", "").forGetter(ShapelessKubeJSRecipe::kjs$getModifyResult)
            )
            .apply(instance, (original, ingredientActions1, modifyResult1) -> new ShapelessKubeJSRecipe(original, ingredientActions1, modifyResult1))
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> STREAM_CODEC = StreamCodec.composite(
         Serializer.STREAM_CODEC,
         r -> r,
         IngredientActionHolder.LIST_STREAM_CODEC,
         ShapelessKubeJSRecipe::kjs$getIngredientActions,
         ByteBufCodecs.STRING_UTF8,
         ShapelessKubeJSRecipe::kjs$getModifyResult,
         ShapelessKubeJSRecipe::new
      );

      public MapCodec<ShapelessKubeJSRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
