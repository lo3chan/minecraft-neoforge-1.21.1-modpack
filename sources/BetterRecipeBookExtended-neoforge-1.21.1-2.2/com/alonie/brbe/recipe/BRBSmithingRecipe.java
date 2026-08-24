package com.alonie.brbe.recipe;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.generic.GenericRecipe;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

public interface BRBSmithingRecipe extends SmithingRecipe, GenericRecipe {
   @Override
   ItemStack getResult(RegistryAccess var1, BRBBookCategories.Category var2);

   ItemStack getResult(ResourceKey<TrimMaterial> var1, RegistryAccess var2, BRBBookCategories.Category var3);

   Ingredient getTemplate();

   ItemStack getBase();

   Ingredient getAddition();

   default boolean hasMaterials(NonNullList<Slot> slots, RegistryAccess registryAccess) {
      return this.hasTemplate(slots) && this.hasBase(slots, registryAccess) && this.hasAddition(slots);
   }

   default boolean hasTemplate(List<Slot> slots) {
      for (Slot slot : slots) {
         if (this.getTemplate().test(slot.getItem())) {
            return true;
         }
      }

      return false;
   }

   default boolean hasBase(List<Slot> slots, RegistryAccess registryAccess) {
      for (Slot slot : slots) {
         if (!slot.getItem().has(DataComponents.TRIM) && this.getBase().getItem().equals(slot.getItem().getItem())) {
            return true;
         }
      }

      return false;
   }

   default boolean hasAddition(List<Slot> slots) {
      for (Slot slot : slots) {
         if (this.getAddition().test(slot.getItem())) {
            return true;
         }
      }

      return false;
   }

   default String getTemplateType() {
      TooltipContext tipCtx = TooltipContext.of(Minecraft.getInstance().player.level());
      return ((Component)this.getTemplate().getItems()[0].getTooltipLines(tipCtx, Minecraft.getInstance().player, TooltipFlag.NORMAL).get(1)).getString();
   }

   @Override
   default ResourceLocation id() {
      return BuiltInRegistries.ITEM.getKey(this.getTemplate().getItems()[0].getItem());
   }

   @Override
   default String getSearchString(BRBBookCategories.Category category) {
      return this.getTemplateType();
   }
}
