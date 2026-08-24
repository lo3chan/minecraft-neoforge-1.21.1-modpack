package net.mehvahdjukaar.moonlight.api.client.model.platform;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.model.BakedQuadBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BakedQuadBuilderImpl implements BakedQuadBuilder {
   private final QuadBakingVertexConsumer inner;
   private final TextureAtlasSprite sprite;
   private final Consumer<BakedQuad> quadConsumer;
   private final Matrix4f globalTransform;
   private final Matrix3f normalTransf;
   private int emissivity = 0;
   private boolean autoDirection = false;
   private int vertexIndex = -1;

   public static BakedQuadBuilder create(TextureAtlasSprite sprite, @Nullable Matrix4f transformation, Consumer<BakedQuad> quadConsumer) {
      return new BakedQuadBuilderImpl(sprite, transformation, quadConsumer);
   }

   private BakedQuadBuilderImpl(TextureAtlasSprite sprite, @Nullable Matrix4f transformation, Consumer<BakedQuad> quadConsumer) {
      this.inner = new QuadBakingVertexConsumer();
      this.globalTransform = transformation;
      this.sprite = sprite;
      this.quadConsumer = quadConsumer;
      this.inner.setShade(true);
      this.inner.setHasAmbientOcclusion(true);
      this.inner.setSprite(sprite);
      this.normalTransf = transformation == null ? null : new Matrix3f(transformation).invert().transpose();
   }

   @Override
   public void close() {
      this.tryBaking();
   }

   private void tryBaking() {
      if (this.vertexIndex == 3) {
         this.vertexIndex = -1;
         BakedQuad quad = this.inner.bakeQuad();
         if (this.emissivity != 0) {
            QuadTransformers.settingEmissivity(this.emissivity).processInPlace(quad);
         }

         this.quadConsumer.accept(quad);
      }
   }

   public BakedQuadBuilderImpl addVertex(float x, float y, float z) {
      this.tryBaking();
      this.vertexIndex++;
      if (this.globalTransform != null) {
         this.inner.addVertex(new Matrix4f(this.globalTransform), x, y, z);
      } else {
         this.inner.addVertex(x, y, z);
      }

      return this;
   }

   public BakedQuadBuilderImpl setColor(int red, int green, int blue, int alpha) {
      this.inner.setColor(red, green, blue, alpha);
      return this;
   }

   public BakedQuadBuilderImpl setUv(float u, float v) {
      this.inner.setUv(this.sprite.getU(u * 16.0F), this.sprite.getV(v * 16.0F));
      return this;
   }

   public BakedQuadBuilderImpl setUv1(int u, int v) {
      this.inner.setUv1(u, v);
      return this;
   }

   public BakedQuadBuilderImpl setUv2(int u, int v) {
      this.inner.setUv2(u, v);
      return this;
   }

   public BakedQuadBuilderImpl setNormal(float x, float y, float z) {
      if (this.globalTransform != null) {
         Vector3f normal = this.normalTransf.transform(new Vector3f(x, y, z));
         normal.normalize();
         this.inner.setNormal(normal.x, normal.y, normal.z);
      } else {
         this.inner.setNormal(x, y, z);
      }

      if (this.autoDirection) {
         this.setDirection(Direction.getNearest(x, y, z));
      }

      return this;
   }

   @Override
   public BakedQuadBuilder setDirection(Direction direction) {
      if (this.globalTransform != null) {
         direction = Direction.rotate(this.globalTransform, direction);
      }

      this.inner.setDirection(direction);
      return this;
   }

   @Override
   public BakedQuadBuilder setAmbientOcclusion(boolean ambientOcclusion) {
      this.inner.setHasAmbientOcclusion(ambientOcclusion);
      return this;
   }

   @Override
   public BakedQuadBuilder setTint(int tintIndex) {
      this.inner.setTintIndex(tintIndex);
      return this;
   }

   @Override
   public BakedQuadBuilder setShade(boolean shade) {
      this.inner.setShade(shade);
      return this;
   }

   @Override
   public BakedQuadBuilder lightEmission(int light) {
      this.emissivity = light;
      return this;
   }

   @Override
   public BakedQuadBuilder setAutoDirection() {
      this.autoDirection = true;
      return this;
   }
}
