package io.github.razordevs.deep_aether.recipe.poison;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.recipe.DARecipeSerializers;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class PoisonRecipe implements Recipe<PoisonRecipeInput> {
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;

   public PoisonRecipe(String group, Ingredient ingredient, ItemStack result) {
      this.group = group;
      this.ingredient = ingredient;
      this.result = result;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)DARecipeSerializers.POISON_RECIPE.get();
   }

   public boolean matches(PoisonRecipeInput input, Level level) {
      return this.ingredient.test(input.getItem(0));
   }

   public ItemStack assemble(PoisonRecipeInput input, Provider registries) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int p_43743_, int p_43744_) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public String getGroup() {
      return this.group;
   }

   public Ingredient getIngredient() {
      return this.ingredient;
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)DARecipeTypes.POISON_RECIPE.get();
   }

   public static class Serializer implements RecipeSerializer<PoisonRecipe> {
      private static final MapCodec<PoisonRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(poisonRecipe -> poisonRecipe.group),
               Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
               ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            )
            .apply(instance, PoisonRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PoisonRecipe> STREAM_CODEC = StreamCodec.of(
         PoisonRecipe.Serializer::toNetwork, PoisonRecipe.Serializer::fromNetwork
      );

      public MapCodec<PoisonRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, PoisonRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      public static PoisonRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf();
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack result = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         return new PoisonRecipe(group, ingredient, result);
      }

      public static void toNetwork(RegistryFriendlyByteBuf buffer, PoisonRecipe recipe) {
         buffer.writeUtf(recipe.group);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
      }
   }
}
