package at.petrak.hexcasting.client;

import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;

public class ShiftScrollListener {
   private static double mainHandDelta = 0.0;
   private static double offHandDelta = 0.0;

   public static boolean onScrollInGameplay(double delta) {
      return Minecraft.getInstance().screen != null ? false : onScroll(delta, true);
   }

   public static boolean onScroll(double delta, boolean needsSneaking) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && (player.isShiftKeyDown() || !needsSneaking)) {
         if (player.isSpectator()) {
            return false;
         }

         if (IsScrollableItem(player.getMainHandItem().getItem())) {
            mainHandDelta += delta;
            return true;
         }

         if (IsScrollableItem(player.getOffhandItem().getItem())) {
            offHandDelta += delta;
            return true;
         }
      }

      return false;
   }

   public static void clientTickEnd() {
      if (mainHandDelta != 0.0 || offHandDelta != 0.0) {
         IClientXplatAbstractions.INSTANCE
            .sendPacketToServer(
               new MsgShiftScrollC2S(
                  mainHandDelta,
                  offHandDelta,
                  Screen.hasControlDown(),
                  HexConfig.client().invertSpellbookScrollDirection(),
                  HexConfig.client().invertAbacusScrollDirection()
               )
            );
         mainHandDelta = 0.0;
         offHandDelta = 0.0;
      }
   }

   private static boolean IsScrollableItem(Item item) {
      return item == HexItems.SPELLBOOK || item == HexItems.ABACUS;
   }
}
