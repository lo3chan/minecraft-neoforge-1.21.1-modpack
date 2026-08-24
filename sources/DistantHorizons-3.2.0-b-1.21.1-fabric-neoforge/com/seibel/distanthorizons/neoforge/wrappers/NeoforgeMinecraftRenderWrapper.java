package com.seibel.distanthorizons.neoforge.wrappers;

import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import net.minecraft.client.Minecraft;

public class NeoforgeMinecraftRenderWrapper extends MinecraftRenderWrapper_neoforge {
   public static final NeoforgeMinecraftRenderWrapper INSTANCE = new NeoforgeMinecraftRenderWrapper();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Minecraft MC = Minecraft.getInstance();

   @Override
   public int getGlDepthTextureId() {
      return super.getGlDepthTextureId();
   }

   @Override
   public int getGlColorTextureId() {
      return super.getGlColorTextureId();
   }
}
