package at.petrak.hexcasting.api.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DiscoveryHandlers {
   private static final List<BiFunction<Player, String, ItemStack>> DEBUG_DISCOVERER = new ArrayList<>();

   public static ItemStack findDebugItem(Player player, String type) {
      for (BiFunction<Player, String, ItemStack> discoverer : DEBUG_DISCOVERER) {
         ItemStack stack = discoverer.apply(player, type);
         if (!stack.isEmpty()) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static void addDebugItemDiscoverer(BiFunction<Player, String, ItemStack> discoverer) {
      DEBUG_DISCOVERER.add(discoverer);
   }
}
