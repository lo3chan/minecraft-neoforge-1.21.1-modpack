package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nonnull;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

public class BookOfShadowsRecipe extends ShapedRecipe {
   public BookOfShadowsRecipe(ShapedRecipe shapedRecipe) {
      super(shapedRecipe.getGroup(), shapedRecipe.category(), shapedRecipe.pattern, shapedRecipe.result, shapedRecipe.showNotification());
   }

   public BookOfShadowsRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
      super(group, category, pattern, result, showNotification);
   }

   public boolean isSpecial() {
      return false;
   }

   @Nonnull
   public ItemStack assemble(CraftingInput input, Provider registries) {
      DyeColor color1 = null;
      DyeColor color2 = null;
      ItemStack book = null;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         Item item = stack.getItem();
         if (item instanceof DyeItem dye) {
            if (color1 == null) {
               color1 = dye.getDyeColor();
            } else {
               color2 = dye.getDyeColor();
            }
         } else if (item instanceof HexereiBookItem) {
            book = stack;
         }
      }

      ItemStack stack = new ItemStack((ItemLike)ModItems.BOOK_OF_SHADOWS.get());
      return HexereiBookItem.withColors(
         book == null ? stack : book, color1 == null ? 0 : color1.getTextureDiffuseColor(), color2 == null ? 0 : color2.getTextureDiffuseColor()
      );
   }

   public ItemStack getOutput() {
      return this.getResultItem(null);
   }

   public NonNullList<Ingredient> getInputs() {
      return this.getIngredients();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.BOOK_OF_SHADOWS_SERIALIZER.get();
   }

   public static class Serializer implements RecipeSerializer<BookOfShadowsRecipe> {
      public static final MapCodec<BookOfShadowsRecipe> CODEC = RecordCodecBuilder.mapCodec(
         p_340778_ -> p_340778_.group(net.minecraft.world.item.crafting.ShapedRecipe.Serializer.CODEC.forGetter(bookOfShadowsRecipe -> bookOfShadowsRecipe))
            .apply(p_340778_, BookOfShadowsRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, BookOfShadowsRecipe> STREAM_CODEC = StreamCodec.of(
         BookOfShadowsRecipe.Serializer::toNetwork, BookOfShadowsRecipe.Serializer::fromNetwork
      );

      public MapCodec<BookOfShadowsRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BookOfShadowsRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static BookOfShadowsRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new BookOfShadowsRecipe((ShapedRecipe)net.minecraft.world.item.crafting.ShapedRecipe.Serializer.STREAM_CODEC.decode(buffer));
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, BookOfShadowsRecipe recipe) {
         net.minecraft.world.item.crafting.ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
      }
   }
}
