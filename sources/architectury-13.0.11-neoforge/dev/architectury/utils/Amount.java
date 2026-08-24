package dev.architectury.utils;

public class Amount {
   public static int toInt(long amount) {
      if (amount >= 2147483647L) {
         return 2147483647;
      } else {
         return amount <= -2147483648L ? -2147483648 : (int)amount;
      }
   }
}
