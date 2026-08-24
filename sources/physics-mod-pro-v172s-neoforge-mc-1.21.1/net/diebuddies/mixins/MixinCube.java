package net.diebuddies.mixins;

import java.util.Set;
import net.diebuddies.physics.CubeExtension;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Cube.class})
public class MixinCube implements CubeExtension {
   @Unique
   private boolean physicsMirror;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void constructor(
      int u,
      int v,
      float x,
      float y,
      float z,
      float sizeX,
      float sizeY,
      float sizeZ,
      float extraX,
      float extraY,
      float extraZ,
      boolean mirror,
      float textureWidth,
      float textureHeight,
      Set<Direction> set,
      CallbackInfo info
   ) {
      this.physicsMirror = mirror;
   }

   @Override
   public boolean isMirrored() {
      return this.physicsMirror;
   }
}
