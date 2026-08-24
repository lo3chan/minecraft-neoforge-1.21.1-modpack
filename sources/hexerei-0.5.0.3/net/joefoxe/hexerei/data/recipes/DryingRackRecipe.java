package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamCodec.CodecOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class DryingRackRecipe implements Recipe<CraftingInput> {
   private final ItemStack output;
   private final Ingredient input;
   private final int dryingTime;

   public boolean isSpecial() {
      return true;
   }

   public DryingRackRecipe(Ingredient input, ItemStack output, int dryingTime) {
      this.output = output;
      this.input = input;
      this.dryingTime = dryingTime;
   }

   public boolean matches(CraftingInput inv, Level worldIn) {
      return inv.items().stream().anyMatch(stack -> this.input.test(stack));
   }

   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.withSize(1, this.input);
   }

   public ItemStack assemble(CraftingInput inv, Provider registryAccess) {
      return this.output;
   }

   public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
      return true;
   }

   public ItemStack getResultItem(Provider registryAccess) {
      return this.getOutput();
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   public int getDryingTime() {
      return this.dryingTime;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModBlocks.HERB_DRYING_RACK.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.DRYING_RACK_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return DryingRackRecipe.Type.INSTANCE;
   }

   public static class Serializer implements RecipeSerializer<DryingRackRecipe> {
      public static final DryingRackRecipe.Serializer INSTANCE = new DryingRackRecipe.Serializer();
      private static final MapCodec<DryingRackRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               Codec.INT.fieldOf("dryingTime").forGetter(recipe -> recipe.dryingTime)
            )
            .apply(instance, DryingRackRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> STREAM_CODEC = StreamCodec.of(
         DryingRackRecipe.Serializer::toNetwork, DryingRackRecipe.Serializer::fromNetwork
      );

      public MapCodec<DryingRackRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      static <B extends ByteBuf> CodecOperation<B, Ingredient, NonNullList<Ingredient>> list() {
         return p_320272_ -> ByteBufCodecs.collection(s -> NonNullList.withSize(s, Ingredient.EMPTY), p_320272_);
      }

      private static DryingRackRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         int dryingTime = (Integer)ByteBufCodecs.INT.decode(buffer);
         return new DryingRackRecipe(input, output, dryingTime);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, DryingRackRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         ByteBufCodecs.INT.encode(buffer, recipe.dryingTime);
      }
   }

   public static class Type implements RecipeType<DryingRackRecipe> {
      public static final DryingRackRecipe.Type INSTANCE = new DryingRackRecipe.Type();
      public static final String ID = "drying_rack";

      private Type() {
      }
   }
}
