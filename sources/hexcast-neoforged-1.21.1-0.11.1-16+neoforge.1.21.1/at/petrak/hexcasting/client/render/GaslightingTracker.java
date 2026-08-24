package at.petrak.hexcasting.client.render;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.resources.ResourceLocation;

public class GaslightingTracker {
   private static int GASLIGHTING_AMOUNT = 0;
   private static int LOOKING_COOLDOWN_MAX = 40;
   private static int LOOKING_COOLDOWN = LOOKING_COOLDOWN_MAX;
   public static ResourceLocation GASLIGHTING_PRED = HexAPI.modLoc("variant");

   public static int getGaslightingAmount() {
      LOOKING_COOLDOWN = LOOKING_COOLDOWN_MAX;
      return GASLIGHTING_AMOUNT;
   }

   public static void postFrameCheckRendered() {
      if (LOOKING_COOLDOWN > 0) {
         LOOKING_COOLDOWN--;
      } else {
         GASLIGHTING_AMOUNT++;
      }
   }
}
