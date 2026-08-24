package net.bettercombat.logic;

public enum AnimatedHand {
   MAIN_HAND,
   OFF_HAND,
   TWO_HANDED;

   public static AnimatedHand from(boolean isOffHand, boolean isTwoHanded) {
      if (isTwoHanded) {
         return TWO_HANDED;
      } else {
         return isOffHand ? OFF_HAND : MAIN_HAND;
      }
   }

   public boolean isOffHand() {
      return this == OFF_HAND;
   }
}
