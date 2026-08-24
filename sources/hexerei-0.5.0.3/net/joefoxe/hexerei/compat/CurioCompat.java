package net.joefoxe.hexerei.compat;

import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioCompat {
   public static boolean hasGlasses(Player player) {
      return CuriosApi.getCuriosInventory(player).map(inv -> inv.isEquipped((Item)ModItems.READING_GLASSES.get())).orElse(false);
   }

   public static void sendIMC() {
   }
}
