package io.github.razordevs.deep_aether;

import io.github.razordevs.deep_aether.init.DAItems;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class DAEnumExtensions {
   public static Object combiningSearchIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack(Items.COMPASS)));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object combiningFoodIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)DAItems.MOA_FODDER.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object combiningMiscIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)DAItems.ANTIDOTE.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   private static String prefix(String id) {
      return "deep_aether:" + id;
   }
}
