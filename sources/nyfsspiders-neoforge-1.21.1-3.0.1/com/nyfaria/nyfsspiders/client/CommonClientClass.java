package com.nyfaria.nyfsspiders.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class CommonClientClass {
   public static Level getLevel() {
      return Minecraft.getInstance().level;
   }
}
