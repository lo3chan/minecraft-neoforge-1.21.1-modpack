package dev.corgitaco.enhancedcelestials2core.util;

import net.minecraft.client.Minecraft;

public class ClientUtil {
   public static void scheduleClientAction(Runnable runnable) {
      Minecraft.getInstance().execute(runnable);
   }
}
