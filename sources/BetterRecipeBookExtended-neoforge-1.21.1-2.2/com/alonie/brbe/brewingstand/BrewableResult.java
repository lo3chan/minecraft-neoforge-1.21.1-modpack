package com.alonie.brbe.brewingstand;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.generic.GenericRecipe;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class BrewableResult implements GenericRecipe {
   public Object recipe;
   public ResourceLocation input;

   public BrewableResult(Object recipe) {
      this.recipe = recipe;
      this.input = BuiltInRegistries.POTION.getKey(PlatformPotionUtil.getFrom(recipe));
   }

   public boolean hasIngredient(List<Slot> slots) {
      for (ItemStack itemStack : PlatformPotionUtil.getIngredient(this.recipe).getItems()) {
         for (Slot slot : slots) {
            if (itemStack.getItem().equals(slot.getItem().getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack inputAsItemStack(BRBBookCategories.Category category) {
      Potion inputPotion = PlatformPotionUtil.getFrom(this.recipe);
      Item potionItem = ((ItemStack)category.getItemIcons().getFirst()).getItem();
      return potionStackFromPotion(potionItem, inputPotion);
   }

   public boolean hasInput(BRBBookCategories.Category category, List<Slot> slots) {
      ItemStack inputStack = this.inputAsItemStack(category);

      for (Slot slot : slots) {
         ItemStack itemStack = slot.getItem();
         if (ItemStack.isSameItemSameComponents(inputStack, itemStack)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMaterials(BRBBookCategories.Category category, List<Slot> slots) {
      boolean hasIngredient = this.hasIngredient(slots);
      boolean hasInput = this.hasInput(category, slots);
      return hasIngredient && hasInput;
   }

   @Override
   public ResourceLocation id() {
      return BuiltInRegistries.POTION.getKey(PlatformPotionUtil.getTo(this.recipe));
   }

   public Component getHoverName(BRBBookCategories.Category category) {
      Potion resultPotion = PlatformPotionUtil.getTo(this.recipe);
      Item potionItem = ((ItemStack)category.getItemIcons().getFirst()).getItem();
      return potionStackFromPotion(potionItem, resultPotion).getHoverName();
   }

   @Override
   public ItemStack getResult(RegistryAccess registryAccess, BRBBookCategories.Category category) {
      Potion resultPotion = PlatformPotionUtil.getTo(this.recipe);
      Item potionItem = ((ItemStack)category.getItemIcons().getFirst()).getItem();
      return potionStackFromPotion(potionItem, resultPotion);
   }

   @Override
   public String getSearchString(BRBBookCategories.Category category) {
      return this.getHoverName(category).getString();
   }

   public static ItemStack potionStackFromPotion(Item item, Potion pot) {
      return PotionContents.createItemStack(item, BuiltInRegistries.POTION.wrapAsHolder(pot));
   }
}
