package net.diebuddies.mixins.sodium;

import java.nio.ByteOrder;
import net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin({EntityRenderer.class})
public class MixinEntityRenderer8 {
   @Shadow(
      remap = false
   )
   @Final
   private static long[] CUBE_VERTEX_XY;
   @Shadow(
      remap = false
   )
   @Final
   private static long[] CUBE_VERTEX_ZW;
   @Unique
   private static final boolean LITTLE = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

   @Inject(
      at = {@At("HEAD")},
      method = {"writeVertex"},
      remap = false
   )
   private static void physicsmod$catchRenderingForBlockEntities(
      long ptr, int vertexIndex, long packedUv, long packedOverlayLight, int normal, CallbackInfoReturnable<Long> info
   ) {
      if (PhysicsMod.sodiumCatchBoundingBox) {
         float x = Float.intBitsToFloat(unpackA(CUBE_VERTEX_XY[vertexIndex]));
         float y = Float.intBitsToFloat(unpackB(CUBE_VERTEX_XY[vertexIndex]));
         float z = Float.intBitsToFloat(unpackA(CUBE_VERTEX_ZW[vertexIndex]));
         Vector3d start = PhysicsMod.sodiumBoundingBox.start;
         Vector3d end = PhysicsMod.sodiumBoundingBox.end;
         if (x < start.x) {
            start.x = x;
         }

         if (y < start.y) {
            start.y = y;
         }

         if (z < start.z) {
            start.z = z;
         }

         if (x > end.x) {
            end.x = x;
         }

         if (y > end.y) {
            end.y = y;
         }

         if (z > end.z) {
            end.z = z;
         }
      }

      if (PhysicsMod.sodiumCatch) {
         float xx = Float.intBitsToFloat(unpackA(CUBE_VERTEX_XY[vertexIndex]));
         float yx = Float.intBitsToFloat(unpackB(CUBE_VERTEX_XY[vertexIndex]));
         float zx = Float.intBitsToFloat(unpackA(CUBE_VERTEX_ZW[vertexIndex]));
         int color = unpackB(CUBE_VERTEX_ZW[vertexIndex]);
         float u = Float.intBitsToFloat(unpackA(packedUv));
         float v = Float.intBitsToFloat(unpackB(packedUv));
         Model model = BlockEntityVertexConsumerProvider.currentConsumer.getModel();
         if (model == null) {
            return;
         }

         model.textureID = TextureHelper.getLoadedTextures();
         Mesh mesh = model.mesh;
         mesh.positions.add(new Vector3f(xx, yx, zx));
         float normRange = 0.007874016F;
         float normX = (byte)(normal & 0xFF) * normRange;
         float normY = (byte)(normal >> 8 & 0xFF) * normRange;
         float normZ = (byte)(normal >> 16 & 0xFF) * normRange;
         mesh.colors.add(color);
         mesh.normals.add(new Vector3f(normX, normY, normZ));
         mesh.uvs.add(new Vector2f(u, v));
         if ((mesh.positions.size() & 3) == 0) {
            int index = mesh.positions.size() - 4;
            mesh.indices.add(index);
            mesh.indices.add(index + 1);
            mesh.indices.add(index + 2);
            mesh.indices.add(index);
            mesh.indices.add(index + 2);
            mesh.indices.add(index + 3);
         }
      }
   }

   @Unique
   private static int unpackA(long packed) {
      return LITTLE ? (int)(packed & 4294967295L) : (int)(packed >>> 32 & 4294967295L);
   }

   @Unique
   private static int unpackB(long packed) {
      return LITTLE ? (int)(packed >>> 32 & 4294967295L) : (int)(packed & 4294967295L);
   }
}
