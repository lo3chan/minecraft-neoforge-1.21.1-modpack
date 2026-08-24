package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class FluidMixingRecipe implements Recipe<RecipeInput> {
   private final NonNullList<Ingredient> recipeItems;
   private final FluidStack fluid;
   private final FluidStack fluidOutput;
   private final FluidMixingRecipe.HeatCondition heatCondition;

   public boolean isSpecial() {
      return true;
   }

   public FluidMixingRecipe(NonNullList<Ingredient> recipeItems, FluidStack fluid, FluidStack fluidOutput) {
      this(recipeItems, fluid, fluidOutput, FluidMixingRecipe.HeatCondition.NONE);
   }

   public FluidMixingRecipe(NonNullList<Ingredient> recipeItems, FluidStack fluid, FluidStack fluidOutput, FluidMixingRecipe.HeatCondition heatCondition) {
      this.recipeItems = recipeItems;
      this.fluid = fluid;
      this.fluidOutput = fluidOutput;
      this.heatCondition = heatCondition;
   }

   public boolean matches(RecipeInput input, Level worldIn) {
      List<Boolean> itemMatchesSlot = Stream.<Boolean>generate(() -> false).limit(8L).collect(Collectors.toList());
      boolean flag = false;

      for (Ingredient recipeItem : this.recipeItems) {
         for (int i = 0; i < 8; i++) {
            if (recipeItem.test(input.getItem(i)) && !itemMatchesSlot.get(i)) {
               itemMatchesSlot.set(i, true);
               flag = true;
               break;
            }
         }

         if (!flag) {
            break;
         }

         flag = false;
      }

      for (int ix = 0; ix < 8; ix++) {
         if (!itemMatchesSlot.get(ix)) {
            return false;
         }
      }

      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public ItemStack assemble(RecipeInput input, Provider registries) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   public FluidMixingRecipe.HeatCondition getHeatCondition() {
      return this.heatCondition;
   }

   public FluidStack getLiquid() {
      return this.fluid;
   }

   public FluidStack getLiquidOutput() {
      return this.fluidOutput;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.FLUID_MIXING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return FluidMixingRecipe.Type.INSTANCE;
   }

   public static enum HeatCondition implements StringRepresentable {
      NONE,
      HEATED,
      SUPERHEATED;

      public static final EnumCodec<FluidMixingRecipe.HeatCondition> CODEC = StringRepresentable.fromEnum(FluidMixingRecipe.HeatCondition::values);

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public static FluidMixingRecipe.HeatCondition getHeated(String str) {
         return switch (str) {
            case "heated" -> HEATED;
            case "superheated" -> SUPERHEATED;
            default -> NONE;
         };
      }

      public String getSerializedName() {
         return switch (this) {
            case NONE -> "none";
            case HEATED -> "heated";
            case SUPERHEATED -> "superheated";
         };
      }
   }

   public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
      public static final FluidMixingRecipe.Serializer INSTANCE = new FluidMixingRecipe.Serializer();
      private static final MapCodec<FluidMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
               FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
               FluidStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.fluidOutput),
               FluidMixingRecipe.HeatCondition.CODEC.fieldOf("heatRequirement").forGetter(recipe -> recipe.heatCondition)
            )
            .apply(instance, FluidMixingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> STREAM_CODEC = StreamCodec.of(
         FluidMixingRecipe.Serializer::toNetwork, FluidMixingRecipe.Serializer::fromNetwork
      );

      public MapCodec<FluidMixingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, FluidMixingRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static FluidMixingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
         inputs.replaceAll(ignored -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         FluidStack inputFluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         FluidStack outputFluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         FluidMixingRecipe.HeatCondition heatCondition = (FluidMixingRecipe.HeatCondition)NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class)
            .decode(buffer);
         return new FluidMixingRecipe(inputs, inputFluid, outputFluid, heatCondition);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, FluidMixingRecipe recipe) {
         buffer.writeInt(recipe.recipeItems.size());

         for (Ingredient ingredient : recipe.recipeItems) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluidOutput);
         NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class).encode(buffer, recipe.heatCondition);
      }
   }

   public static class Type implements RecipeType<FluidMixingRecipe> {
      public static final FluidMixingRecipe.Type INSTANCE = new FluidMixingRecipe.Type();

      private Type() {
      }
   }
}
