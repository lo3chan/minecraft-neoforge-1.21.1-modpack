package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class RecipeMimicreamRepair extends CustomRecipe {
   public RecipeMimicreamRepair(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level worldIn) {
      int size = inv.size();
      if (!AMConfig.mimicreamRepair) {
         return false;
      } else {
         ItemStack damageableStack = ItemStack.EMPTY;
         int mimicreamCount = 0;

         for (int j = 0; j < size; j++) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
               if (itemstack1.isDamageableItem() && !this.isBlacklisted(itemstack1)) {
                  damageableStack = itemstack1;
               } else if (itemstack1.getItem() == AMItemRegistry.MIMICREAM.get()) {
                  mimicreamCount++;
               }
            }
         }

         return !damageableStack.isEmpty() && mimicreamCount >= 8;
      }
   }

   public boolean isBlacklisted(ItemStack stack) {
      ResourceLocation name = BuiltInRegistries.ITEM.getKey(stack.getItem());
      return name != null && AMConfig.mimicreamBlacklist.contains(name.toString());
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      int size = inv.size();
      ItemStack damageableStack = ItemStack.EMPTY;
      int mimicreamCount = 0;

      for (int j = 0; j < size; j++) {
         ItemStack itemstack1 = inv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.isDamageableItem() && !this.isBlacklisted(itemstack1)) {
               damageableStack = itemstack1;
            } else if (itemstack1.getItem() == AMItemRegistry.MIMICREAM.get()) {
               mimicreamCount++;
            }
         }
      }

      if (!damageableStack.isEmpty() && mimicreamCount >= 8) {
         ItemStack itemstack2 = damageableStack.copy();
         ResourceLocation mendingName = Enchantments.MENDING.location();
         CompoundTag customData = AMCompat.getTag(itemstack2);
         if (customData != null && itemstack2.is(AMItemRegistry.GHOSTLY_PICKAXE.get()) && customData.contains("Items")) {
            customData.remove("Items");
            AMCompat.setTag(itemstack2, customData);
         }

         EnchantmentHelper.updateEnchantments(
            itemstack2, mutable -> mutable.removeIf(holder -> holder.unwrapKey().map(resourceKey -> resourceKey.location().equals(mendingName)).orElse(false))
         );
         itemstack2.setDamageValue(itemstack2.getMaxDamage());
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack itemstack = inv.getItem(i);
         if (AMCompat.hasCraftingRemainder(itemstack)) {
            nonnulllist.set(i, AMCompat.craftingRemainder(itemstack));
         } else if (itemstack.isDamageableItem()) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<? extends CustomRecipe> getSerializer() {
      return (RecipeSerializer<? extends CustomRecipe>)AMRecipeRegistry.MIMICREAM_RECIPE.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= 3 && height >= 3;
   }
}
