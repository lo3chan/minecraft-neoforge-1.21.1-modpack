package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CauldronFillingRecipe implements Recipe<SingleRecipeInput> {
   private final Ingredient input;
   private final ItemStack output;
   private final FluidStack fluid;

   public CauldronFillingRecipe(Ingredient input, ItemStack output, FluidStack fluid) {
      this.input = input;
      this.output = output;
      this.fluid = fluid;
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(SingleRecipeInput recipeInput, Level pLevel) {
      return this.test(this.input, recipeInput.getItem(0));
   }

   public boolean test(Ingredient ingredient, @Nullable ItemStack pStack) {
      if (pStack == null) {
         return false;
      } else if (ingredient.isEmpty()) {
         return pStack.isEmpty();
      } else {
         for (ItemStack itemStack : ingredient.getItems()) {
            if (itemStack.is(pStack.getItem())) {
               PotionContents itemStackPotion = (PotionContents)itemStack.get(DataComponents.POTION_CONTENTS);
               PotionContents pStackPotion = (PotionContents)pStack.get(DataComponents.POTION_CONTENTS);
               return itemStackPotion == null
                  || pStackPotion == null
                  || pStackPotion.potion().isPresent() && itemStackPotion.is((Holder)pStackPotion.potion().get());
            }
         }

         return false;
      }
   }

   public ItemStack assemble(SingleRecipeInput input, Provider registries) {
      return this.getResultItem(registries);
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.output.copy();
   }

   public FluidStack getResultingFluid() {
      return this.fluid.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.CAULDRON_FILLING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipeTypes.CAULDRON_FILLING_TYPE.get();
   }

   public static class Serializer implements RecipeSerializer<CauldronFillingRecipe> {
      private static final MapCodec<CauldronFillingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid)
            )
            .apply(instance, CauldronFillingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CauldronFillingRecipe> STREAM_CODEC = StreamCodec.of(
         CauldronFillingRecipe.Serializer::toNetwork, CauldronFillingRecipe.Serializer::fromNetwork
      );

      public MapCodec<CauldronFillingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CauldronFillingRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CauldronFillingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         FluidStack fluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         return new CauldronFillingRecipe(input, output, fluid);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronFillingRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
      }
   }
}
