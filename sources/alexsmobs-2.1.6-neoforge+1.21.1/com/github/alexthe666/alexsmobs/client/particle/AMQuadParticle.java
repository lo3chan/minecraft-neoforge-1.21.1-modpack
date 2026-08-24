package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;

public abstract class AMQuadParticle extends TextureSheetParticle {
   protected AMQuadParticle(ClientLevel world, double x, double y, double z) {
      super(world, x, y, z);
   }

   protected AMQuadParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
      super(world, x, y, z, xd, yd, zd);
   }
}
