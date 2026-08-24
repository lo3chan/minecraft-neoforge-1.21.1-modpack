package traben.entity_model_features.mixin.mixins;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.IEMFCuboidDataSupplier;

@Mixin({Cube.class})
public class MixinModelPart$Cuboid implements IEMFCuboidDataSupplier {
   @Unique
   private int[] emf$textureUV = null;
   @Unique
   private float[] emf$sizeAdd = null;
   @Unique
   private int[] emf$textureXY = null;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void emf$injectAnnouncerCube(
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
      Set<?> set,
      CallbackInfo ci
   ) {
      this.emf$textureUV = new int[]{u, v};
      this.emf$sizeAdd = new float[]{extraX, extraY, extraZ};
      this.emf$textureXY = new int[]{(int)textureWidth, (int)textureHeight};
   }

   @Override
   public int[] emf$getTextureUV() {
      return this.emf$textureUV;
   }

   @Override
   public int[] emf$getTextureXY() {
      return this.emf$textureXY;
   }

   @Override
   public float[] emf$getSizeAdd() {
      return this.emf$sizeAdd;
   }
}
