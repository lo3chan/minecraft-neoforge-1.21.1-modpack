package org.dimdev.limlib.api;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;

public interface ICreativeTabHandler {
   void appendStack(CreativeModeTab var1, ItemStack var2);

   void modify(CreativeModeTab var1, ICreativeTabHandler.ModifyTabCallback var2);

   public interface CreativeTabOutput extends Output {
      void acceptAfter(ItemStack var1, ItemStack var2, TabVisibility var3);

      default void acceptAfter(ItemStack after, ItemStack stack) {
         this.acceptAfter(after, stack, TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      void acceptBefore(ItemStack var1, ItemStack var2, TabVisibility var3);

      default void acceptBefore(ItemStack after, ItemStack stack) {
         this.acceptBefore(after, stack, TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void accept(ItemStack stack, TabVisibility visibility) {
         this.acceptAfter(ItemStack.EMPTY, stack, visibility);
      }
   }

   @FunctionalInterface
   public interface ModifyTabCallback {
      void accept(FeatureFlagSet var1, ICreativeTabHandler.CreativeTabOutput var2, boolean var3);
   }
}
