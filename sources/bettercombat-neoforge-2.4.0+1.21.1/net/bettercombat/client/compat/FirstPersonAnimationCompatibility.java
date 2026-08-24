package net.bettercombat.client.compat;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import net.bettercombat.Platform;
import net.bettercombat.client.BetterCombatClientMod;

public class FirstPersonAnimationCompatibility {
   private static boolean isCameraModPresent = false;

   static void setup() {
      String[] cameraMods = new String[]{"firstperson", "realcamera"};

      for (String mod : cameraMods) {
         if (Platform.isModLoaded(mod)) {
            isCameraModPresent = true;
            break;
         }
      }
   }

   public static FirstPersonMode firstPersonMode() {
      switch (BetterCombatClientMod.config.firstPersonAnimations) {
         case YES:
            return FirstPersonMode.THIRD_PERSON_MODEL;
         case NO:
            return FirstPersonMode.NONE;
         default:
            return isCameraModPresent ? FirstPersonMode.NONE : FirstPersonMode.THIRD_PERSON_MODEL;
      }
   }
}
