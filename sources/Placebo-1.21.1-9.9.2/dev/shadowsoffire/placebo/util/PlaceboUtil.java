package dev.shadowsoffire.placebo.util;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ItemLike;

public class PlaceboUtil {
   @SafeVarargs
   public static <T> List<T> asList(T... objs) {
      ArrayList<T> list = new ArrayList<>();

      for (T t : objs) {
         list.add(t);
      }

      return list;
   }

   public static ItemStack makeStack(Object thing) {
      if (thing instanceof ItemStack stack) {
         return stack;
      } else if (thing instanceof ItemLike il) {
         return new ItemStack(il);
      } else if (thing instanceof Holder h) {
         return makeStack(h.value());
      } else {
         throw new IllegalArgumentException("Attempted to create an ItemStack from something that cannot be converted: " + thing);
      }
   }

   public static ItemStack[] toStackArray(Object... args) {
      ItemStack[] out = new ItemStack[args.length];

      for (int i = 0; i < args.length; i++) {
         out[i] = makeStack(args[i]);
      }

      return out;
   }

   public static <T> List<T> toMutable(List<T> list) {
      if (list instanceof ImmutableList) {
         list = new ArrayList<>(list);
      }

      return list;
   }

   @Deprecated(
      forRemoval = true,
      since = "9.9.1"
   )
   public static boolean tryHarvestBlock(ServerPlayer player, BlockPos pos) {
      return player.gameMode.destroyBlock(pos);
   }

   public static void addLore(ItemStack stack, Component lore) {
      ItemLore comp = (ItemLore)stack.get(DataComponents.LORE);
      stack.set(DataComponents.LORE, comp.withLineAdded(lore));
   }

   public static <T extends TextColor> void registerCustomColor(T color) {
      TextColor.NAMED_COLORS.put(color.serialize(), color);
   }
}
