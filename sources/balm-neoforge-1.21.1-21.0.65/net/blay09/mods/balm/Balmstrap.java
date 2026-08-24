package net.blay09.mods.balm;

import net.blay09.mods.balm.api.Balm;

public class Balmstrap {
   public static void onRuntimeAvailable(Runnable callback) {
      Balm.getRuntime().onRuntimeAvailable(callback);
   }
}
