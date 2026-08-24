package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.nio.ByteBuffer;
import net.diebuddies.physics.PhysicsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {BufferBuilder.class},
   priority = 2010
)
public class MixinBufferBuilder {
   @Redirect(
      method = {"fillExtendedData"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/nio/ByteBuffer;putInt(II)Ljava/nio/ByteBuffer;",
         ordinal = 0
      ),
      remap = false
   )
   private ByteBuffer physicsmod$disableFlatShadingForCloth(ByteBuffer buffer, int index, int value) {
      if (!PhysicsMod.clothSmootShadingIrisFix) {
         buffer.putInt(index, value);
      }

      return buffer;
   }
}
