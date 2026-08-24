package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class CauldronEmptyingRecipe implements Recipe<CauldronEmptyingRecipe.Wrapper> {
   private final Ingredient input;
   private final FluidStack fluid;
   private final ItemStack output;

   public CauldronEmptyingRecipe(Ingredient input, FluidStack fluid, ItemStack output) {
      this.input = input;
      this.fluid = fluid;
      this.output = output;
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(CauldronEmptyingRecipe.Wrapper pContainer, Level pLevel) {
      return this.input.test(pContainer.getInput())
         && FluidStack.isSameFluidSameComponents(this.fluid, pContainer.getFluid())
         && pContainer.getFluid().getAmount() >= this.fluid.getAmount();
   }

   public ItemStack assemble(CauldronEmptyingRecipe.Wrapper input, Provider registries) {
      return this.getResultItem(registries);
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return true;
   }

   public Ingredient getInput() {
      return this.input;
   }

   public FluidStack getFluid() {
      return this.fluid;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.output.copy();
   }

   public ItemStack getResultItem() {
      return this.output.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.CAULDRON_EMPTYING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get();
   }

   public static class Serializer implements RecipeSerializer<CauldronEmptyingRecipe> {
      private static final MapCodec<CauldronEmptyingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
               FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output)
            )
            .apply(instance, CauldronEmptyingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CauldronEmptyingRecipe> STREAM_CODEC = StreamCodec.of(
         CauldronEmptyingRecipe.Serializer::toNetwork, CauldronEmptyingRecipe.Serializer::fromNetwork
      );

      public MapCodec<CauldronEmptyingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CauldronEmptyingRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CauldronEmptyingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         FluidStack fluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         return new CauldronEmptyingRecipe(input, fluid, output);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronEmptyingRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
      }
   }

   public static class Wrapper extends RecipeWrapper {
      private final FluidStack fluid;

      public Wrapper(ItemStack stack, FluidStack fluid) {
         super(new ItemStackHandler(1));
         this.fluid = fluid;
         this.inv.insertItem(0, stack, false);
      }

      public ItemStack getInput() {
         return this.getItem(0);
      }

      public FluidStack getFluid() {
         return this.fluid;
      }
   }
}
