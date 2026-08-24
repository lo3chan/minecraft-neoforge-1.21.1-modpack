package net.bettercombat.api.fx;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public class ItemConditions {
   private static final Map<String, Predicate<ItemStack>> CONDITIONS = new HashMap<>();

   public static void register(String id, Predicate<ItemStack> predicate) {
      CONDITIONS.put(id, predicate);
   }

   public static Predicate<ItemStack> get(String id) {
      return CONDITIONS.get(id);
   }

   public static boolean test(String id, ItemStack itemStack) {
      Predicate<ItemStack> predicate = CONDITIONS.get(id);
      return predicate == null ? false : predicate.test(itemStack);
   }

   public static boolean has(String id) {
      return CONDITIONS.containsKey(id);
   }

   static {
      register("is_enchanted", ItemStack::isEnchanted);
   }
}
