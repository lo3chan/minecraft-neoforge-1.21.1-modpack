package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record NaturesRitualRecipe(NonNullList<Ingredient> ingredients, ItemStack output) implements Recipe<NaturesRitualRecipeInput> {
   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public boolean matches(NaturesRitualRecipeInput input, Level level) {
      return !level.isClientSide() && !this.ingredients.isEmpty() && ((Ingredient)this.ingredients.get(0)).test(input.getItem(0));
   }

   public ItemStack assemble(NaturesRitualRecipeInput input, Provider registries) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.ingredients.size();
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.output;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.NATURES_RITUAL_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipeTypes.NATURES_RITUAL.get();
   }

   public static final class Serializer implements RecipeSerializer<NaturesRitualRecipe> {
      public static final MapCodec<NaturesRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(list -> {
                  NonNullList<Ingredient> ingredients = NonNullList.create();
                  ingredients.addAll(list);
                  return ingredients;
               }, list -> list).forGetter(NaturesRitualRecipe::ingredients),
               BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").xmap(ItemStack::new, ItemStack::getItem).forGetter(NaturesRitualRecipe::output)
            )
            .apply(instance, NaturesRitualRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, NaturesRitualRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
         buffer.writeVarInt(recipe.ingredients.size());

         for (Ingredient ingredient : recipe.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
      }, buffer -> {
         int count = buffer.readVarInt();
         NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);

         for (int index = 0; index < count; index++) {
            ingredients.set(index, (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         }

         return new NaturesRitualRecipe(ingredients, (ItemStack)ItemStack.STREAM_CODEC.decode(buffer));
      });

      public MapCodec<NaturesRitualRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, NaturesRitualRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
