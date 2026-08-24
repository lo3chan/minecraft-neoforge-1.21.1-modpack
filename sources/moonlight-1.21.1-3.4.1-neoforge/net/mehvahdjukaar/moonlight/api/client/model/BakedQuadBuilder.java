package net.mehvahdjukaar.moonlight.api.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Transformation;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.model.platform.BakedQuadBuilderImpl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface BakedQuadBuilder extends VertexConsumer, AutoCloseable {
   static BakedQuadBuilder create(TextureAtlasSprite sprite, Consumer<BakedQuad> quadConsumer) {
      return create(sprite, (Matrix4f)null, quadConsumer);
   }

   static BakedQuadBuilder create(TextureAtlasSprite sprite, @Nullable Transformation transformation, Consumer<BakedQuad> quadConsumer) {
      return create(
         sprite,
         transformation == null ? null : new Matrix4f().translate(0.5F, 0.5F, 0.5F).mul(transformation.getMatrix()).translate(-0.5F, -0.5F, -0.5F),
         quadConsumer
      );
   }

   BakedQuadBuilder setAutoDirection();

   BakedQuadBuilder setDirection(Direction var1);

   BakedQuadBuilder setAmbientOcclusion(boolean var1);

   BakedQuadBuilder setShade(boolean var1);

   BakedQuadBuilder lightEmission(int var1);

   BakedQuadBuilder setTint(int var1);

   default BakedQuadBuilder addVertex(Matrix4f matrix, float x, float y, float z) {
      super.addVertex(matrix, x, y, z);
      return this;
   }

   default VertexConsumer setNormal(Pose pose, float f, float g, float h) {
      super.setNormal(pose, f, g, h);
      return this;
   }

   static BakedQuadBuilder create(TextureAtlasSprite var0, Matrix4f var1, Consumer<BakedQuad> var2) {
      return BakedQuadBuilderImpl.create(var0, var1, var2);
   }
}
