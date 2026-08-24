package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.codec.KubeJSStreamCodecs;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {
   private final boolean mirror;
   private final List<IngredientActionHolder> ingredientActions;
   private final String modifyResult;

   public ShapedKubeJSRecipe(
      String group,
      CraftingBookCategory category,
      ShapedRecipePattern pattern,
      ItemStack result,
      boolean showNotification,
      boolean mirror,
      List<IngredientActionHolder> ingredientActions,
      String modifyResult
   ) {
      super(group, category, pattern, result, showNotification);
      this.mirror = mirror;
      this.ingredientActions = ingredientActions;
      this.modifyResult = modifyResult;
      if (!mirror) {
         this.pattern.symmetrical = true;
      }
   }

   public RecipeSerializer<?> getSerializer() {
      return KubeJSRecipeSerializers.SHAPED.get();
   }

   @Override
   public List<IngredientActionHolder> kjs$getIngredientActions() {
      return this.ingredientActions;
   }

   @Override
   public String kjs$getModifyResult() {
      return this.modifyResult;
   }

   public boolean kjs$getMirror() {
      return this.mirror;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
      return this.kjs$getRemainingItems(input);
   }

   public ItemStack assemble(CraftingInput input, Provider registryAccess) {
      return this.kjs$assemble(input, registryAccess);
   }

   private ShapedRecipePattern pattern() {
      return this.pattern;
   }

   private ItemStack result() {
      return this.result;
   }

   public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {
      public static final MapCodec<ShapedKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
               CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
               ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
               Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
               Codec.BOOL.optionalFieldOf("kubejs:mirror", true).forGetter(ShapedKubeJSRecipe::kjs$getMirror),
               IngredientActionHolder.LIST_CODEC
                  .optionalFieldOf("kubejs:ingredient_actions", List.of())
                  .forGetter(ShapedKubeJSRecipe::kjs$getIngredientActions),
               Codec.STRING.optionalFieldOf("kubejs:modify_result", "").forGetter(ShapedKubeJSRecipe::kjs$getModifyResult)
            )
            .apply(instance, ShapedKubeJSRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> STREAM_CODEC = KubeJSStreamCodecs.composite(
         ByteBufCodecs.STRING_UTF8,
         ShapedRecipe::getGroup,
         CraftingBookCategory.STREAM_CODEC,
         ShapedRecipe::category,
         ShapedRecipePattern.STREAM_CODEC,
         ShapedKubeJSRecipe::pattern,
         ItemStack.STREAM_CODEC,
         ShapedKubeJSRecipe::result,
         ByteBufCodecs.BOOL,
         ShapedRecipe::showNotification,
         ByteBufCodecs.BOOL,
         ShapedKubeJSRecipe::kjs$getMirror,
         IngredientActionHolder.LIST_STREAM_CODEC,
         ShapedKubeJSRecipe::kjs$getIngredientActions,
         ByteBufCodecs.STRING_UTF8,
         ShapedKubeJSRecipe::kjs$getModifyResult,
         ShapedKubeJSRecipe::new
      );

      public MapCodec<ShapedKubeJSRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
