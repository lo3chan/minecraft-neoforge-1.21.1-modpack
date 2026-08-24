package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;

public class AddBaseToCandleRecipe extends CustomRecipe {
   public AddBaseToCandleRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(CraftingInput pInv, Level pLevel) {
      ItemStack itemstack = ItemStack.EMPTY;
      BlockItem block = null;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is((Item)ModItems.CANDLE.get())) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
               if (block != null) {
                  return false;
               }

               block = blockItem;
            }
         }
      }

      return !itemstack.isEmpty() && block != null;
   }

   public ItemStack assemble(CraftingInput pInv, Provider registries) {
      int i = 0;
      ItemStack candle = ItemStack.EMPTY;
      BlockItem block = null;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is((Item)ModItems.CANDLE.get())) {
               if (!candle.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               candle = itemstack1;
            } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
               try {
                  if (block != null || !blockItem.getBlock().defaultBlockState().getShape(null, null).equals(Shapes.block())) {
                     return ItemStack.EMPTY;
                  }

                  block = blockItem;
               } catch (Exception var10) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (!candle.isEmpty() && block != null) {
         ItemStack itemstack2 = candle.copy();
         itemstack2.setCount(1);
         CandleData data = new CandleData();
         data.load(((CustomData)itemstack2.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag(), registries);
         if (BuiltInRegistries.BLOCK.containsValue(block.getBlock())) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("layerFromBlockLocation", true);
            tag.putString("layer", BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
            data.base.load(tag);
         }

         CompoundTag tag = ((CustomData)itemstack2.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         data.save(tag, registries, true);
         itemstack2.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getResultItem(Provider registries) {
      return this.getOutput();
   }

   public ItemStack getOutput() {
      return ((Item)ModItems.CANDLE.get()).getDefaultInstance();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.ADD_BASE_TO_CANDLE_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return RecipeType.CRAFTING;
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth * pHeight >= 2;
   }

   public static class Type implements RecipeType<AddBaseToCandleRecipe> {
      public static final AddBaseToCandleRecipe.Type INSTANCE = new AddBaseToCandleRecipe.Type();

      private Type() {
      }
   }
}
