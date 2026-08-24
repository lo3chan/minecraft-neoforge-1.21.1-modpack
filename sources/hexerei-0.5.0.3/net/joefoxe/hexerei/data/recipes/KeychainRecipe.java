package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeychainRecipe extends CustomRecipe {
   public KeychainRecipe(CraftingBookCategory cBc) {
      super(cBc);
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(CraftingInput inventory, Level world) {
      int keychain = 0;
      int other = 0;

      for (int i = 0; i < inventory.size(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof KeychainItem) {
               CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               if (!tag.contains("Items")) {
                  keychain++;
               }
            } else {
               other++;
            }

            if (other > 1 || keychain > 1) {
               return false;
            }
         }
      }

      return keychain == 1 && other == 1;
   }

   public ItemStack assemble(CraftingInput inventory, Provider registryAccess) {
      ItemStack keychain = ItemStack.EMPTY;
      ItemStack other = ItemStack.EMPTY;

      for (int i = 0; i < inventory.size(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof KeychainItem) {
               keychain = stack.copy();
               keychain.setCount(1);
            } else {
               other = stack.copy();
               other.setCount(1);
            }
         }
      }

      if (keychain.getItem() instanceof KeychainItem && !other.isEmpty()) {
         CompoundTag tag = ((CustomData)keychain.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         ListTag listtag = new ListTag();
         if (!other.isEmpty()) {
            listtag.add(other.save(registryAccess));
         }

         tag.put("Items", listtag);
         keychain.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      return keychain;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= 2 && height >= 1;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.KEYCHAIN_APPLY_SERIALIZER.get();
   }
}
