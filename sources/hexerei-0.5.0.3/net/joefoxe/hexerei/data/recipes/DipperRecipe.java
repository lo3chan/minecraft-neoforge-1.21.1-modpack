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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class DipperRecipe implements Recipe<CraftingInput> {
   private final ItemStack input;
   private final ItemStack output;
   private final FluidStack fluid;
   private final int dippingTime;
   private final int dryingTime;
   private final int numberOfDips;
   private final boolean useInputItemAsOutput;

   public boolean isSpecial() {
      return true;
   }

   public DipperRecipe(ItemStack input, ItemStack output, FluidStack fluid, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
      this.input = input;
      this.output = output;
      this.fluid = fluid;
      this.dippingTime = dippingTime;
      this.dryingTime = dryingTime;
      this.numberOfDips = numberOfDips;
      this.useInputItemAsOutput = useInputItemAsOutput;
   }

   public FluidStack getFluid() {
      return this.fluid;
   }

   public boolean matches(CraftingInput inv, Level level) {
      return inv.items().stream().anyMatch(stack -> stack.is(this.input.getItem()));
   }

   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.withSize(1, Ingredient.of(new ItemStack[]{this.input}));
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      return this.output;
   }

   public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.getOutput();
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   public ItemStack getInput() {
      return this.input.copy();
   }

   public FluidStack getLiquid() {
      return this.fluid;
   }

   public int getFluidLevelsConsumed() {
      return this.fluid.getAmount();
   }

   public int getDippingTime() {
      return this.dippingTime;
   }

   public int getDryingTime() {
      return this.dryingTime;
   }

   public int getNumberOfDips() {
      return this.numberOfDips;
   }

   public boolean getUseInputItemAsOutput() {
      return this.useInputItemAsOutput;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModBlocks.CANDLE_DIPPER.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.DIPPER_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return DipperRecipe.Type.INSTANCE;
   }

   public static class Serializer implements RecipeSerializer<DipperRecipe> {
      public static final DipperRecipe.Serializer INSTANCE = new DipperRecipe.Serializer();
      public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("hexerei", "dipper");
      private static final MapCodec<DipperRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ItemStack.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
               Codec.INT.fieldOf("dippingTime").forGetter(recipe -> recipe.dippingTime),
               Codec.INT.fieldOf("dryingTime").forGetter(recipe -> recipe.dryingTime),
               Codec.INT.fieldOf("numberOfDips").forGetter(recipe -> recipe.numberOfDips),
               Codec.BOOL.optionalFieldOf("useInputItemAsOutput", false).forGetter(recipe -> recipe.useInputItemAsOutput)
            )
            .apply(instance, DipperRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> STREAM_CODEC = StreamCodec.of(
         DipperRecipe.Serializer::toNetwork, DipperRecipe.Serializer::fromNetwork
      );

      public MapCodec<DipperRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      static <B extends ByteBuf> CodecOperation<B, Ingredient, NonNullList<Ingredient>> list() {
         return p_320272_ -> ByteBufCodecs.collection(s -> NonNullList.withSize(s, Ingredient.EMPTY), p_320272_);
      }

      private static DipperRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         ItemStack input = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         FluidStack fluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         int dippingTime = (Integer)ByteBufCodecs.INT.decode(buffer);
         int dryingTime = (Integer)ByteBufCodecs.INT.decode(buffer);
         int numberOfDips = (Integer)ByteBufCodecs.INT.decode(buffer);
         boolean useInputItemAsOutput = (Boolean)ByteBufCodecs.BOOL.decode(buffer);
         return new DipperRecipe(input, output, fluid, dippingTime, dryingTime, numberOfDips, useInputItemAsOutput);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, DipperRecipe recipe) {
         ItemStack.STREAM_CODEC.encode(buffer, recipe.input);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
         ByteBufCodecs.INT.encode(buffer, recipe.dippingTime);
         ByteBufCodecs.INT.encode(buffer, recipe.dryingTime);
         ByteBufCodecs.INT.encode(buffer, recipe.numberOfDips);
         ByteBufCodecs.BOOL.encode(buffer, recipe.useInputItemAsOutput);
      }
   }

   public static class Type implements RecipeType<DipperRecipe> {
      public static final DipperRecipe.Type INSTANCE = new DipperRecipe.Type();
      public static final String ID = "dipper";

      private Type() {
      }
   }
}
