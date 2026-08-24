package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class AddToCandleRecipe extends CustomRecipe {
   Ingredient input;
   ItemStack output;

   public AddToCandleRecipe(Ingredient input, ItemStack output) {
      super(CraftingBookCategory.MISC);
      this.input = input;
      this.output = output;
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(CraftingInput pInv, Level pLevel) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is((Item)ModItems.CANDLE.get())) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (this.input.getItems().length == 0) {
                  return false;
               }

               CompoundTag tag = ((CustomData)itemstack1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               CompoundTag tag2 = ((CustomData)this.input.getItems()[0].getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               boolean compare = NbtUtils.compareNbt(tag2, tag, true);
               if (itemstack1.is(this.input.getItems()[0].getItem()) && compare) {
                  i++;
               }
            }
         }
      }

      return !itemstack.isEmpty() && i == 1;
   }

   public ItemStack assemble(CraftingInput pInv, Provider registryAccess) {
      int i = 0;
      ItemStack candle = ItemStack.EMPTY;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is((Item)ModItems.CANDLE.get())) {
               if (!candle.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               candle = itemstack1;
            } else {
               CompoundTag tag = ((CustomData)itemstack1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               CompoundTag tag2 = ((CustomData)this.input.getItems()[0].getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               boolean compare = NbtUtils.compareNbt(tag2, tag, true);
               if (!itemstack1.is(this.input.getItems()[0].getItem()) && compare) {
                  return ItemStack.EMPTY;
               }

               i++;
            }
         }
      }

      if (!candle.isEmpty() && i >= 1) {
         ItemStack itemstack2 = candle.copy();
         itemstack2.setCount(1);
         CompoundTag itemstack2tag = ((CustomData)itemstack2.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CompoundTag outputtag = ((CustomData)this.output.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CandleData data = new CandleData();
         data.load(itemstack2tag, registryAccess);
         data.load(outputtag, registryAccess);
         data.save(itemstack2tag, registryAccess, true);
         itemstack2.set(DataComponents.CUSTOM_DATA, CustomData.of(itemstack2tag));
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getResultItem(Provider registries) {
      return this.getOutput();
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.of(Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)ModItems.CANDLE.get())}), new Ingredient[]{this.input});
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.ADD_TO_CANDLE_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return RecipeType.CRAFTING;
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth * pHeight >= 2;
   }

   public static class Serializer implements RecipeSerializer<AddToCandleRecipe> {
      public static final AddToCandleRecipe.Serializer INSTANCE = new AddToCandleRecipe.Serializer();
      private static final MapCodec<AddToCandleRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input), ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output)
            )
            .apply(instance, AddToCandleRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, AddToCandleRecipe> STREAM_CODEC = StreamCodec.of(
         AddToCandleRecipe.Serializer::toNetwork, AddToCandleRecipe.Serializer::fromNetwork
      );

      public MapCodec<AddToCandleRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, AddToCandleRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static AddToCandleRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         return new AddToCandleRecipe(input, output);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, AddToCandleRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
         ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
      }
   }

   public static class Type implements RecipeType<AddToCandleRecipe> {
      public static final AddToCandleRecipe.Type INSTANCE = new AddToCandleRecipe.Type();

      private Type() {
      }
   }
}
