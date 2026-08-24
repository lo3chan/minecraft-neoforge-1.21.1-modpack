package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class PestleAndMortarRecipe implements Recipe<CraftingInput> {
   private final ItemStack output;
   private final int grindingTime;
   private final NonNullList<Ingredient> input;

   public boolean isSpecial() {
      return true;
   }

   public PestleAndMortarRecipe(ItemStack output, NonNullList<Ingredient> input, int grindingTime) {
      this.output = output;
      this.input = input;
      this.grindingTime = grindingTime;
   }

   public boolean matches(CraftingInput input, Level level) {
      List<Boolean> itemMatchesSlot = Stream.<Boolean>generate(() -> false).limit(input.size()).collect(Collectors.toList());
      boolean flag = false;
      if (input.size() != this.input.size()) {
         return false;
      } else {
         for (Ingredient recipeItem : this.input) {
            for (int i = 0; i < input.size(); i++) {
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

         for (int ix = 0; ix < input.size(); ix++) {
            if (!itemMatchesSlot.get(ix)) {
               return false;
            }
         }

         return true;
      }
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      return this.output;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.input;
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

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModBlocks.PESTLE_AND_MORTAR.get());
   }

   public int getGrindingTime() {
      return this.grindingTime;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.PESTLE_AND_MORTAR_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return PestleAndMortarRecipe.Type.INSTANCE;
   }

   public static class Serializer implements RecipeSerializer<PestleAndMortarRecipe> {
      public static final PestleAndMortarRecipe.Serializer INSTANCE = new PestleAndMortarRecipe.Serializer();
      private static final MapCodec<PestleAndMortarRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(recipe -> recipe.input),
               Codec.INT.fieldOf("grindingTime").forGetter(recipe -> recipe.grindingTime)
            )
            .apply(instance, PestleAndMortarRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PestleAndMortarRecipe> STREAM_CODEC = StreamCodec.of(
         PestleAndMortarRecipe.Serializer::toNetwork, PestleAndMortarRecipe.Serializer::fromNetwork
      );

      public MapCodec<PestleAndMortarRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, PestleAndMortarRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static PestleAndMortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
         inputs.replaceAll(ignored -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         int grindingTime = (Integer)ByteBufCodecs.INT.decode(buffer);
         return new PestleAndMortarRecipe(output, inputs, grindingTime);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, PestleAndMortarRecipe recipe) {
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         buffer.writeInt(recipe.input.size());

         for (Ingredient ingredient : recipe.input) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         ByteBufCodecs.INT.encode(buffer, recipe.grindingTime);
      }
   }

   public static class Type implements RecipeType<PestleAndMortarRecipe> {
      public static final PestleAndMortarRecipe.Type INSTANCE = new PestleAndMortarRecipe.Type();
      public static final String ID = "pestle_and_mortar";

      private Type() {
      }
   }
}
