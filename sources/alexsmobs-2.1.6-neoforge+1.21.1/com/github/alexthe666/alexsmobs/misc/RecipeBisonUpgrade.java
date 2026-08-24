package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeBisonUpgrade extends CustomRecipe {
   public RecipeBisonUpgrade(CraftingBookCategory category) {
      super(category);
   }

   private ItemStack createBoots(CraftingInput container) {
      int size = container.size();
      ItemStack boots = ItemStack.EMPTY;
      int fur = 0;

      for (int j = 0; j < size; j++) {
         ItemStack itemstack1 = container.getItem(j);
         if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.get().asItem())) {
            fur++;
         }
      }

      if (fur == 1) {
         for (int jx = 0; jx < size; jx++) {
            ItemStack itemstack1 = container.getItem(jx);
            boolean notFurred = !AMCompat.hasTag(itemstack1)
               || AMCompat.getTag(itemstack1) != null && !AMCompat.getBoolean(AMCompat.getTag(itemstack1), "BisonFur");
            if (!itemstack1.isEmpty() && notFurred && AMCompat.equipmentSlotFor(itemstack1) == EquipmentSlot.FEET) {
               boots = itemstack1;
            }
         }

         if (!boots.isEmpty()) {
            ItemStack stack = boots.copy();
            CompoundTag tag = AMCompat.getOrCreateTag(stack);
            tag.putBoolean("BisonFur", true);
            AMCompat.setTag(stack, tag);
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean matches(CraftingInput inv, Level worldIn) {
      return !this.createBoots(inv).isEmpty();
   }

   public ItemStack assemble(CraftingInput container, Provider provider) {
      return this.createBoots(container);
   }

   public boolean canCraftInDimensions(int x, int y) {
      return x * y >= 2;
   }

   public RecipeSerializer<? extends CustomRecipe> getSerializer() {
      return (RecipeSerializer<? extends CustomRecipe>)AMRecipeRegistry.BISON_UPGRADE.get();
   }
}
