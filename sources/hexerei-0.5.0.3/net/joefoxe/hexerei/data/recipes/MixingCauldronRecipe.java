package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class MixingCauldronRecipe implements Recipe<MixingCauldronRecipe.MixingCauldronRecipeInput> {
   private final ItemStack output;
   private final NonNullList<Ingredient> recipeItems;
   private final FluidStack fluid;
   private final FluidStack fluidOutput;
   protected static final List<Boolean> itemMatchesSlot = new ArrayList<>();
   private final FluidMixingRecipe.HeatCondition heatCondition;
   private final MoonPhases.MoonCondition moonCondition;

   public boolean isSpecial() {
      return true;
   }

   public MixingCauldronRecipe(
      ItemStack output,
      NonNullList<Ingredient> recipeItems,
      FluidStack fluid,
      FluidStack fluidOutput,
      FluidMixingRecipe.HeatCondition heatCondition,
      MoonPhases.MoonCondition moonCondition
   ) {
      this.output = output;
      this.recipeItems = recipeItems;
      this.fluid = fluid;
      this.fluidOutput = fluidOutput;
      this.heatCondition = heatCondition;
      this.moonCondition = moonCondition;
   }

   public static MixingCauldronRecipe.MixingCauldronRecipeInput createInput(List<ItemStack> items) {
      return new MixingCauldronRecipe.MixingCauldronRecipeInput(items);
   }

   public List<FluidIngredient> getFluidIngredients() {
      return new ArrayList<>(List.of(FluidIngredient.of(new FluidStack[]{this.fluid})));
   }

   public FluidIngredient getFluidIngredient() {
      return FluidIngredient.of(new FluidStack[]{this.fluid});
   }

   public boolean matches(MixingCauldronRecipe.MixingCauldronRecipeInput inv, Level worldIn) {
      List<Boolean> itemMatchesSlot = Stream.<Boolean>generate(() -> false).limit(inv.size()).collect(Collectors.toList());
      boolean flag = false;

      for (Ingredient recipeItem : this.recipeItems) {
         for (int i = 0; i < inv.size(); i++) {
            if (recipeItem.test(inv.getItem(i)) && !itemMatchesSlot.get(i)) {
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

      for (int ix = 0; ix < inv.size(); ix++) {
         if (!itemMatchesSlot.get(ix)) {
            return false;
         }
      }

      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public ItemStack assemble(MixingCauldronRecipe.MixingCauldronRecipeInput inv, Provider registryAccess) {
      ItemStack outputCopy = this.output.copy();
      if (outputCopy.is(ModItems.ENTANGLED_COFFER)) {
         ItemStack coffer = null;

         for (int i = 0; i < inv.size(); i++) {
            if (inv.getItem(i).is(ModItems.COFFER)) {
               coffer = inv.getItem(i);
               break;
            }
         }

         if (coffer != null) {
            CompoundTag tag = ((CustomData)coffer.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            CompoundTag tag2 = ((CustomData)outputCopy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            if (!tag.contains("CofferId")) {
               tag.putUUID("CofferId", UUID.randomUUID());
            }

            outputCopy.set(DataComponents.CUSTOM_DATA, CustomData.of(tag.merge(tag2)));
            if (coffer.has(DataComponents.DYED_COLOR)
               && ((DyedItemColor)coffer.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(4337438, true))).rgb() != 4337438) {
               outputCopy.set(DataComponents.DYED_COLOR, (DyedItemColor)coffer.get(DataComponents.DYED_COLOR));
            }
         }
      }

      return outputCopy;
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

   public FluidMixingRecipe.HeatCondition getHeatCondition() {
      return this.heatCondition;
   }

   public MoonPhases.MoonCondition getMoonCondition() {
      return this.moonCondition;
   }

   public FluidStack getLiquid() {
      return this.fluid.copy();
   }

   public FluidStack getLiquidOutput() {
      return this.fluidOutput.isEmpty() ? this.fluid.copy() : this.fluidOutput.copy();
   }

   public int getFluidLevelsConsumed() {
      return this.getLiquidOutput().getAmount();
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.MIXING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return MixingCauldronRecipe.Type.INSTANCE;
   }

   public static class MixingCauldronRecipeInput implements RecipeInput {
      private final List<ItemStack> items;
      private final StackedContents stackedContents = new StackedContents();

      public MixingCauldronRecipeInput(List<ItemStack> item) {
         this.items = item;

         for (ItemStack itemstack : item) {
            if (!itemstack.isEmpty()) {
               this.stackedContents.accountStack(itemstack, 1);
            }
         }
      }

      public ItemStack getItem(int i) {
         return this.items.get(i);
      }

      public int size() {
         return this.items.size();
      }
   }

   public static class Serializer implements RecipeSerializer<MixingCauldronRecipe> {
      public static final MixingCauldronRecipe.Serializer INSTANCE = new MixingCauldronRecipe.Serializer();
      public static final MapCodec<MixingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
               FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
               FluidStack.CODEC.optionalFieldOf("fluidOutput", FluidStack.EMPTY).forGetter(recipe -> recipe.fluidOutput),
               FluidMixingRecipe.HeatCondition.CODEC
                  .optionalFieldOf("heatRequirement", FluidMixingRecipe.HeatCondition.NONE)
                  .forGetter(recipe -> recipe.heatCondition),
               MoonPhases.MoonCondition.CODEC.optionalFieldOf("moonRequirement", MoonPhases.MoonCondition.NONE).forGetter(recipe -> recipe.moonCondition)
            )
            .apply(instance, MixingCauldronRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, MixingCauldronRecipe> STREAM_CODEC = StreamCodec.of(
         MixingCauldronRecipe.Serializer::toNetwork, MixingCauldronRecipe.Serializer::fromNetwork
      );

      public MapCodec<MixingCauldronRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, MixingCauldronRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static MixingCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
         inputs.replaceAll(ignored -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         FluidStack inputFluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         FluidStack outputFluid = FluidStack.EMPTY;
         if (buffer.readBoolean()) {
            outputFluid = (FluidStack)FluidStack.STREAM_CODEC.decode(buffer);
         }

         FluidMixingRecipe.HeatCondition heatCondition = (FluidMixingRecipe.HeatCondition)NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class)
            .decode(buffer);
         MoonPhases.MoonCondition moonCondition = (MoonPhases.MoonCondition)NeoForgeStreamCodecs.enumCodec(MoonPhases.MoonCondition.class).decode(buffer);
         return new MixingCauldronRecipe(output, inputs, inputFluid, outputFluid, heatCondition, moonCondition);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, MixingCauldronRecipe recipe) {
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
         buffer.writeInt(recipe.recipeItems.size());

         for (Ingredient ingredient : recipe.recipeItems) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
         buffer.writeBoolean(!recipe.fluidOutput.isEmpty());
         if (!recipe.fluidOutput.isEmpty()) {
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluidOutput);
         }

         NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class).encode(buffer, recipe.heatCondition);
         NeoForgeStreamCodecs.enumCodec(MoonPhases.MoonCondition.class).encode(buffer, recipe.moonCondition);
      }
   }

   public static class Type implements RecipeType<MixingCauldronRecipe> {
      public static final MixingCauldronRecipe.Type INSTANCE = new MixingCauldronRecipe.Type();

      private Type() {
      }
   }
}
