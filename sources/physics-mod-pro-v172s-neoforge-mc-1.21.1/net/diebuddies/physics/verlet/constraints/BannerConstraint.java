package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.nio.ByteBuffer;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.RenderedBufferAccessor;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Layer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;

public class BannerConstraint implements VerletConstraint {
   private ModelCube[] partsToCheck;
   private BannerBlockEntity bannerBlock;
   private Matrix4d transformation = new Matrix4d();
   private Matrix4d invTransformation = new Matrix4d();
   private VerletHelper helper = new VerletHelper();
   private Vector3d invPoint = new Vector3d();
   private Vector2f[] tmpUV;
   private Matrix4f[] textureMatrices;
   private BannerPatternLayers patterns;
   private DyeColor baseColor;
   private VertexBuffer vertexBuffer;

   public BannerConstraint(VerletSimulation simulation, BannerBlockEntity bannerBlock, ModelPart pole, ModelPart bar, float tickDelta) {
      this.bannerBlock = bannerBlock;
      this.patterns = new BannerPatternLayers(bannerBlock.getPatterns().layers());
      this.baseColor = bannerBlock.getBaseColor();
      List<VerletConstraint> constraints = simulation.getConstraints();

      for (int i = 0; i < constraints.size(); i++) {
         if (constraints.get(i) instanceof RenderConstraint) {
            constraints.remove(i--);
         }
      }

      if (bannerBlock.getBlockState().getBlock() instanceof BannerBlock) {
         this.partsToCheck = new ModelCube[]{new ModelCube(pole), new ModelCube(bar)};
      } else {
         this.partsToCheck = new ModelCube[]{new ModelCube(bar)};
      }

      this.calculateTransformation(simulation, tickDelta);
      int capeXPoints = 9;
      int capeYPoints = 17;
      double distance = 0.15000000001500002;
      VerletPoint[][] points = new VerletPoint[capeXPoints][capeYPoints];
      float uvXOff = 0.015625F;
      float uvYOff = 0.015625F;
      float uvXMod = 0.3125F;
      float uvYMod = 0.625F;

      for (int y = 0; y < points[0].length; y++) {
         for (int x = 0; x < points.length; x++) {
            Vector3d position = new Vector3d(x * distance - capeXPoints * 0.5 * distance + distance * 0.5, y * distance, -0.08928571428571429);
            this.transformation.transformPosition(position);
            VerletPoint point = new VerletPoint(position);
            point.uv.set((float)x / (points.length - 1) * uvXMod + uvXOff, (float)y / (points[0].length - 1) * uvYMod + uvYOff);
            if (y == 0) {
               point.locked = true;
            }

            points[x][y] = point;
            simulation.addPoint(points[x][y]);
         }
      }

      for (int x = 0; x < points.length; x++) {
         for (int y = 0; y < points[0].length; y++) {
            if (x < points.length - 1) {
               simulation.addStick(new VerletStick(points[x][y], points[x + 1][y]));
            }

            if (y < points[0].length - 1) {
               simulation.addStick(new VerletStick(points[x][y], points[x][y + 1]));
            }

            if (x < points.length - 1 && y < points[0].length - 1) {
               simulation.addQuad(new VerletQuad(points[x][y + 1], points[x + 1][y + 1], points[x + 1][y], points[x][y]));
               simulation.addStick(new VerletStick(points[x][y], points[x + 1][y + 1]));
               simulation.addStick(new VerletStick(points[x + 1][y], points[x][y + 1]));
            }
         }
      }

      simulation.calculateNormals();
      simulation.downloadData();
      this.calculateTransformation(simulation, tickDelta);
      List<VerletQuad> quads = simulation.getQuads();
      int drawCalls = Math.min(17, this.patterns.layers().size() + 1);
      int size = quads.size();
      this.tmpUV = new Vector2f[drawCalls * size * 4];
      this.textureMatrices = new Matrix4f[drawCalls];

      for (int ix = 0; ix < this.tmpUV.length; ix++) {
         this.tmpUV[ix] = new Vector2f();
      }

      for (int ix = 0; ix < 17 && ix < this.patterns.layers().size() + 1; ix++) {
         Material bannerMaterial = null;
         if (ix == 0) {
            bannerMaterial = Sheets.BANNER_BASE;
         } else {
            Layer layer = (Layer)this.patterns.layers().get(ix - 1);
            bannerMaterial = Sheets.getBannerMaterial(layer.pattern());
         }

         if (bannerMaterial == null) {
            this.textureMatrices[ix] = new Matrix4f();
         } else {
            TextureAtlasSprite sprite = bannerMaterial.sprite();
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float xScale = maxU - minU;
            float yScale = maxV - minV;
            this.textureMatrices[ix] = new Matrix4f().translate(minU, minV, 0.0F).scale(xScale, yScale, 0.0F);

            for (int j = 0; j < quads.size(); j++) {
               VerletQuad quad = quads.get(j);
               this.remap(quad.point1.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4]);
               this.remap(quad.point2.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 1]);
               this.remap(quad.point3.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 2]);
               this.remap(quad.point4.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 3]);
            }
         }
      }
   }

   private void calculateTransformation(VerletSimulation simulation, float tickDelta) {
      BlockState blockState = this.bannerBlock.getBlockState();
      BlockPos blockPos = this.bannerBlock.getBlockPos();
      Vector3d offset = simulation.getOffset();
      Matrix4d test = new Matrix4d();
      if (offset != null) {
         test.translate(blockPos.getX() - offset.x, blockPos.getY() - offset.y, blockPos.getZ() - offset.z);
      } else {
         test.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

      if (blockState.getBlock() instanceof BannerBlock) {
         test.translate(0.5, 0.5, 0.5);
         float blockRotation = -(Integer)blockState.getValue(BannerBlock.ROTATION) * 360 / 16.0F;
         test.rotate(Axis.YP.rotationDegrees(blockRotation));
      } else {
         test.translate(0.5, -0.1666666716337204, 0.5);
         float blockRotation = -((Direction)blockState.getValue(WallBannerBlock.FACING)).toYRot();
         test.rotate(Axis.YP.rotationDegrees(blockRotation));
         test.translate(0.0, -0.3125, -0.4375);
      }

      test.scale(0.6666667, -0.6666667, -0.6666667);
      if (simulation.getOffset() == null) {
         long gameTime = this.bannerBlock.getLevel().getGameTime();
         float n = ((float)Math.floorMod(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13 + gameTime, 100L) + tickDelta) / 100.0F;
         float xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * n)) * 3.1415927F;
         double yPos = -32.0;
         test.translate(0.0, yPos / 16.0, 0.0);
         if (xRot != 0.0F) {
            test.rotate(Axis.XP.rotation(xRot));
         }
      }

      this.transformation.set(test);
      this.transformation.invert(this.invTransformation);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      for (int i = 0; i < this.partsToCheck.length; i++) {
         this.partsToCheck[i].pose = this.partsToCheck[i].part.storePose();
         this.partsToCheck[i].updateHitbox();
      }

      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
      this.doCollisionCheck(percent, simulation);
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   private void doCollisionCheck(double percent, VerletSimulation simulation) {
      float enlarge = 0.075F;

      for (int i = 0; i < this.partsToCheck.length; i++) {
         ModelCube part = this.partsToCheck[i];
         float minX = part.minX - enlarge;
         float minY = part.minY - enlarge;
         float minZ = part.minZ - enlarge;
         float maxX = part.maxX + enlarge;
         float maxY = part.maxY + enlarge;
         float maxZ = part.maxZ + enlarge;

         for (VerletPoint point : simulation.getPoints()) {
            if (!point.locked) {
               this.invTransformation.transformPosition(this.invPoint.set(point.position));
               if (this.helper.movePointOutOfBox(this.invPoint, minX, minY, minZ, maxX, maxY, maxZ)) {
                  point.position.set(this.transformation.transformPosition(this.invPoint));
                  point.friction = 0.6;
               }
            }
         }
      }
   }

   public void translateAndRotate(PoseStack poseStack, PartPose pose) {
      poseStack.translate(pose.x / 16.0, pose.y / 16.0, pose.z / 16.0);
      if (pose.zRot != 0.0F) {
         poseStack.mulPose(Axis.ZP.rotation(pose.zRot));
      }

      if (pose.yRot != 0.0F) {
         poseStack.mulPose(Axis.YP.rotation(pose.yRot));
      }

      if (pose.xRot != 0.0F) {
         poseStack.mulPose(Axis.XP.rotation(pose.xRot));
      }
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   @Override
   public void render(Matrix4fStack matrixStack, double renderPercent, VerletSimulation simulation) {
      int brightness = simulation.brightness;
      List<VerletQuad> quads = simulation.getQuads();
      int size = quads.size();
      Matrix4f oldTextureMatrix = RenderSystem.getTextureMatrix();
      float[] color = new float[4];
      if (simulation.getQuads().size() > 0) {
         List<VerletPoint> points = simulation.getPoints();

         for (int i = 0; i < points.size(); i++) {
            points.get(i).updateRenderPosition(renderPercent);
         }

         MeshData bufferedRenderer = null;

         for (int i = 0; i < 17 && i < this.patterns.layers().size() + 1; i++) {
            Material bannerMaterial;
            int icolor;
            if (i == 0) {
               icolor = this.baseColor.getTextureDiffuseColor();
               bannerMaterial = Sheets.BANNER_BASE;
            } else {
               Layer layer = (Layer)this.patterns.layers().get(i - 1);
               icolor = layer.color().getTextureDiffuseColor();
               bannerMaterial = Sheets.getBannerMaterial(layer.pattern());
            }

            color[0] = ARGB32.red(icolor) / 255.0F;
            color[1] = ARGB32.green(icolor) / 255.0F;
            color[2] = ARGB32.blue(icolor) / 255.0F;
            color[3] = ARGB32.alpha(icolor) / 255.0F;
            if (bannerMaterial != null) {
               TextureAtlasSprite sprite = bannerMaterial.sprite();
               if (StarterClient.optifabric) {
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               } else {
                  RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0F);
               }

               int glID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
               RenderSystem.setShaderTexture(0, glID);
               RenderSystem.bindTexture(glID);
               boolean releaseBuffer = i == 16 || i == this.patterns.layers().size() || StarterClient.iris && Iris.isExtending();
               int drawCallIndex = i * size * 4;
               if (bufferedRenderer != null) {
                  if (StarterClient.iris && Iris.isExtending()) {
                     RenderSystem.setTextureMatrix(this.textureMatrices[i]);
                  } else {
                     ByteBuffer buffer = bufferedRenderer.vertexBuffer();
                     int count = 0;
                     int ccount = 0;
                     int vertexCount = quads.size() * 6;
                     int vertexSize = buffer.capacity() / vertexCount;
                     long pointer = MemoryUtil.memAddress(buffer);

                     for (int j = 0; j < quads.size(); j++) {
                        int multiple = j * 4;
                        int uvIndex = drawCallIndex + multiple;
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 3]);
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 2]);
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 1]);
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex]);
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 3]);
                        this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 1]);
                        if (StarterClient.optifabric) {
                           this.updateColor(pointer, vertexSize * ccount++, color);
                           this.updateColor(pointer, vertexSize * ccount++, color);
                           this.updateColor(pointer, vertexSize * ccount++, color);
                           this.updateColor(pointer, vertexSize * ccount++, color);
                           this.updateColor(pointer, vertexSize * ccount++, color);
                           this.updateColor(pointer, vertexSize * ccount++, color);
                        }
                     }
                  }

                  ((RenderedBufferAccessor)bufferedRenderer.vertexBuffer).setIgnoreRelease(!releaseBuffer);
                  if (bufferedRenderer.indexBuffer != null) {
                     ((RenderedBufferAccessor)bufferedRenderer.indexBuffer).setIgnoreRelease(!releaseBuffer);
                  }

                  this.drawWithShader(bufferedRenderer);
               } else {
                  BufferBuilder bufferbuilder;
                  if (StarterClient.iris && Iris.isExtending()) {
                     bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);

                     for (int jx = 0; jx < quads.size(); jx++) {
                        VerletQuad quad = quads.get(jx);
                        RenderSystem.setTextureMatrix(this.textureMatrices[i]);
                        if (ConfigClient.clothSmoothShading) {
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.point1.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.point2.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.point3.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.point4.bufferNormal, brightness, color
                           );
                        } else {
                           this.bufferVertex(bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.bufferNormal, brightness, color);
                           this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.bufferNormal, brightness, color);
                           this.bufferVertex(bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.bufferNormal, brightness, color);
                           this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.bufferNormal, brightness, color);
                        }
                     }
                  } else {
                     bufferbuilder = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);

                     for (int jxx = 0; jxx < quads.size(); jxx++) {
                        VerletQuad quad = quads.get(jxx);
                        int multiple = jxx * 4;
                        int uvIndex = drawCallIndex + multiple;
                        if (ConfigClient.clothSmoothShading) {
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.point4.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point3.renderPosition, this.tmpUV[uvIndex + 2], quad.point3.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.point2.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point1.renderPosition, this.tmpUV[uvIndex], quad.point1.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.point4.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.point2.bufferNormal, brightness, color
                           );
                        } else {
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point3.renderPosition, this.tmpUV[uvIndex + 2], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point1.renderPosition, this.tmpUV[uvIndex], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.bufferNormal, brightness, color
                           );
                        }
                     }
                  }

                  bufferedRenderer = bufferbuilder.build();
                  ((RenderedBufferAccessor)bufferedRenderer.vertexBuffer).setIgnoreRelease(!releaseBuffer);
                  if (bufferedRenderer.indexBuffer != null) {
                     ((RenderedBufferAccessor)bufferedRenderer.indexBuffer).setIgnoreRelease(!releaseBuffer);
                  }

                  this.drawWithShader(bufferedRenderer);
               }
            }
         }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setTextureMatrix(oldTextureMatrix);
      this.vertexBuffer = null;
   }

   public void drawWithShader(MeshData renderedBuffer) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> this._drawWithShader(renderedBuffer));
      } else {
         this._drawWithShader(renderedBuffer);
      }
   }

   private void _drawWithShader(MeshData renderedBuffer) {
      if (StarterClient.iris && Iris.isExtending() && this.vertexBuffer != null) {
         this.vertexBuffer.bind();
      } else {
         this.vertexBuffer = renderedBuffer.drawState().format().getImmediateDrawVertexBuffer();
         this.vertexBuffer.bind();
         this.vertexBuffer.upload(renderedBuffer);
      }

      if (this.vertexBuffer != null) {
         this.vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
      }
   }

   private void remap(Vector2f uv, float minU, float maxU, float minV, float maxV, Vector2f dst) {
      dst.set(net.diebuddies.math.Math.remap(uv.x, 0.0F, 1.0F, minU, maxU), net.diebuddies.math.Math.remap(uv.y, 0.0F, 1.0F, minV, maxV));
   }

   private void bufferVertex(BufferBuilder bufferbuilder, double renderPercent, Vector3d position, Vector2f uv, Vector3d normal, int brightness, float[] color) {
      if (StarterClient.optifabric) {
         bufferbuilder.addVertex((float)position.x, (float)position.y, (float)position.z)
            .setColor(color[0], color[1], color[2], 1.0F)
            .setUv(uv.x, uv.y)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(brightness)
            .setNormal((float)normal.x, (float)normal.y, (float)normal.z);
      } else {
         bufferbuilder.addVertex((float)position.x, (float)position.y, (float)position.z)
            .setColor(1.0F, 1.0F, 1.0F, 1.0F)
            .setUv(uv.x, uv.y)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(brightness)
            .setNormal((float)normal.x, (float)normal.y, (float)normal.z);
      }
   }

   private void updateUV(long pointer, int offset, Vector2f uv) {
      MemoryUtil.memPutFloat(pointer + offset + 16L, uv.x);
      MemoryUtil.memPutFloat(pointer + offset + 20L, uv.y);
   }

   private void updateColor(long pointer, int offset, float[] color) {
      MemoryUtil.memPutByte(pointer + offset + 12L, (byte)(color[0] * 255.0F));
      MemoryUtil.memPutByte(pointer + offset + 13L, (byte)(color[1] * 255.0F));
      MemoryUtil.memPutByte(pointer + offset + 14L, (byte)(color[2] * 255.0F));
   }
}
