/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.BannerBlock
 *  net.minecraft.world.level.block.WallBannerBlock
 *  net.minecraft.world.level.block.entity.BannerBlockEntity
 *  net.minecraft.world.level.block.entity.BannerPatternLayers
 *  net.minecraft.world.level.block.entity.BannerPatternLayers$Layer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.nio.ByteBuffer;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.RenderedBufferAccessor;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.physics.verlet.constraints.ModelCube;
import net.diebuddies.physics.verlet.constraints.RenderConstraint;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.system.MemoryUtil;

public class BannerConstraint
implements VerletConstraint {
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
        int i;
        this.bannerBlock = bannerBlock;
        this.patterns = new BannerPatternLayers(bannerBlock.getPatterns().layers());
        this.baseColor = bannerBlock.getBaseColor();
        List<VerletConstraint> constraints = simulation.getConstraints();
        for (int i2 = 0; i2 < constraints.size(); ++i2) {
            if (!(constraints.get(i2) instanceof RenderConstraint)) continue;
            constraints.remove(i2--);
        }
        this.partsToCheck = bannerBlock.getBlockState().getBlock() instanceof BannerBlock ? new ModelCube[]{new ModelCube(pole), new ModelCube(bar)} : new ModelCube[]{new ModelCube(bar)};
        this.calculateTransformation(simulation, tickDelta);
        int capeXPoints = 9;
        int capeYPoints = 17;
        double distance = 0.15000000001500002;
        VerletPoint[][] points = new VerletPoint[capeXPoints][capeYPoints];
        float uvXOff = 0.015625f;
        float uvYOff = 0.015625f;
        float uvXMod = 0.3125f;
        float uvYMod = 0.625f;
        for (int y = 0; y < points[0].length; ++y) {
            for (int x = 0; x < points.length; ++x) {
                Vector3d position = new Vector3d((double)x * distance - (double)capeXPoints * 0.5 * distance + distance * 0.5, (double)y * distance, -0.08928571428571429);
                this.transformation.transformPosition(position);
                VerletPoint point = new VerletPoint(position);
                point.uv.set((float)x / (float)(points.length - 1) * uvXMod + uvXOff, (float)y / (float)(points[0].length - 1) * uvYMod + uvYOff);
                if (y == 0) {
                    point.locked = true;
                }
                points[x][y] = point;
                simulation.addPoint(points[x][y]);
            }
        }
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[0].length; ++y) {
                if (x < points.length - 1) {
                    simulation.addStick(new VerletStick(points[x][y], points[x + 1][y]));
                }
                if (y < points[0].length - 1) {
                    simulation.addStick(new VerletStick(points[x][y], points[x][y + 1]));
                }
                if (x >= points.length - 1 || y >= points[0].length - 1) continue;
                simulation.addQuad(new VerletQuad(points[x][y + 1], points[x + 1][y + 1], points[x + 1][y], points[x][y]));
                simulation.addStick(new VerletStick(points[x][y], points[x + 1][y + 1]));
                simulation.addStick(new VerletStick(points[x + 1][y], points[x][y + 1]));
            }
        }
        simulation.calculateNormals();
        simulation.downloadData();
        this.calculateTransformation(simulation, tickDelta);
        List<VerletQuad> quads = simulation.getQuads();
        int drawCalls = java.lang.Math.min(17, this.patterns.layers().size() + 1);
        int size = quads.size();
        this.tmpUV = new Vector2f[drawCalls * size * 4];
        this.textureMatrices = new Matrix4f[drawCalls];
        for (i = 0; i < this.tmpUV.length; ++i) {
            this.tmpUV[i] = new Vector2f();
        }
        for (i = 0; i < 17 && i < this.patterns.layers().size() + 1; ++i) {
            Material bannerMaterial = null;
            if (i == 0) {
                bannerMaterial = Sheets.BANNER_BASE;
            } else {
                BannerPatternLayers.Layer layer = (BannerPatternLayers.Layer)this.patterns.layers().get(i - 1);
                bannerMaterial = Sheets.getBannerMaterial((Holder)layer.pattern());
            }
            if (bannerMaterial == null) {
                this.textureMatrices[i] = new Matrix4f();
                continue;
            }
            TextureAtlasSprite sprite = bannerMaterial.sprite();
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float xScale = maxU - minU;
            float yScale = maxV - minV;
            this.textureMatrices[i] = new Matrix4f().translate(minU, minV, 0.0f).scale(xScale, yScale, 0.0f);
            for (int j = 0; j < quads.size(); ++j) {
                VerletQuad quad = quads.get(j);
                this.remap(quad.point1.uv, minU, maxU, minV, maxV, this.tmpUV[size * i * 4 + j * 4]);
                this.remap(quad.point2.uv, minU, maxU, minV, maxV, this.tmpUV[size * i * 4 + j * 4 + 1]);
                this.remap(quad.point3.uv, minU, maxU, minV, maxV, this.tmpUV[size * i * 4 + j * 4 + 2]);
                this.remap(quad.point4.uv, minU, maxU, minV, maxV, this.tmpUV[size * i * 4 + j * 4 + 3]);
            }
        }
    }

    private void calculateTransformation(VerletSimulation simulation, float tickDelta) {
        BlockState blockState = this.bannerBlock.getBlockState();
        BlockPos blockPos = this.bannerBlock.getBlockPos();
        Vector3d offset = simulation.getOffset();
        Matrix4d test = new Matrix4d();
        if (offset != null) {
            test.translate((double)blockPos.getX() - offset.x, (double)blockPos.getY() - offset.y, (double)blockPos.getZ() - offset.z);
        } else {
            test.translate((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
        }
        if (blockState.getBlock() instanceof BannerBlock) {
            test.translate(0.5, 0.5, 0.5);
            blockRotation = (float)(-((Integer)blockState.getValue((Property)BannerBlock.ROTATION)).intValue() * 360) / 16.0f;
            test.rotate((Quaternionfc)Axis.YP.rotationDegrees(blockRotation));
        } else {
            test.translate(0.5, -0.1666666716337204, 0.5);
            blockRotation = -((Direction)blockState.getValue((Property)WallBannerBlock.FACING)).toYRot();
            test.rotate((Quaternionfc)Axis.YP.rotationDegrees(blockRotation));
            test.translate(0.0, -0.3125, -0.4375);
        }
        test.scale(0.6666667, -0.6666667, -0.6666667);
        if (simulation.getOffset() == null) {
            long gameTime = this.bannerBlock.getLevel().getGameTime();
            float n = ((float)java.lang.Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + gameTime, 100L) + tickDelta) / 100.0f;
            float xRot = (-0.0125f + 0.01f * Mth.cos((float)((float)java.lang.Math.PI * 2 * n))) * (float)java.lang.Math.PI;
            double yPos = -32.0;
            test.translate(0.0, yPos / 16.0, 0.0);
            if (xRot != 0.0f) {
                test.rotate((Quaternionfc)Axis.XP.rotation(xRot));
            }
        }
        this.transformation.set((Matrix4dc)test);
        this.transformation.invert(this.invTransformation);
    }

    @Override
    public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        for (int i = 0; i < this.partsToCheck.length; ++i) {
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
        float enlarge = 0.075f;
        for (int i = 0; i < this.partsToCheck.length; ++i) {
            ModelCube part = this.partsToCheck[i];
            float minX = part.minX - enlarge;
            float minY = part.minY - enlarge;
            float minZ = part.minZ - enlarge;
            float maxX = part.maxX + enlarge;
            float maxY = part.maxY + enlarge;
            float maxZ = part.maxZ + enlarge;
            for (VerletPoint point : simulation.getPoints()) {
                if (point.locked) continue;
                this.invTransformation.transformPosition(this.invPoint.set((Vector3dc)point.position));
                if (!this.helper.movePointOutOfBox(this.invPoint, minX, minY, minZ, maxX, maxY, maxZ)) continue;
                point.position.set((Vector3dc)this.transformation.transformPosition(this.invPoint));
                point.friction = 0.6;
            }
        }
    }

    public void translateAndRotate(PoseStack poseStack, PartPose pose) {
        poseStack.translate((double)pose.x / 16.0, (double)pose.y / 16.0, (double)pose.z / 16.0);
        if (pose.zRot != 0.0f) {
            poseStack.mulPose(Axis.ZP.rotation(pose.zRot));
        }
        if (pose.yRot != 0.0f) {
            poseStack.mulPose(Axis.YP.rotation(pose.yRot));
        }
        if (pose.xRot != 0.0f) {
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
            for (int i = 0; i < points.size(); ++i) {
                points.get(i).updateRenderPosition(renderPercent);
            }
            MeshData bufferedRenderer = null;
            for (int i = 0; i < 17 && i < this.patterns.layers().size() + 1; ++i) {
                BufferBuilder bufferbuilder;
                Material bannerMaterial;
                int icolor;
                if (i == 0) {
                    icolor = this.baseColor.getTextureDiffuseColor();
                    bannerMaterial = Sheets.BANNER_BASE;
                } else {
                    BannerPatternLayers.Layer layer = (BannerPatternLayers.Layer)this.patterns.layers().get(i - 1);
                    icolor = layer.color().getTextureDiffuseColor();
                    bannerMaterial = Sheets.getBannerMaterial((Holder)layer.pattern());
                }
                color[0] = (float)FastColor.ARGB32.red((int)icolor) / 255.0f;
                color[1] = (float)FastColor.ARGB32.green((int)icolor) / 255.0f;
                color[2] = (float)FastColor.ARGB32.blue((int)icolor) / 255.0f;
                color[3] = (float)FastColor.ARGB32.alpha((int)icolor) / 255.0f;
                if (bannerMaterial == null) continue;
                TextureAtlasSprite sprite = bannerMaterial.sprite();
                if (StarterClient.optifabric) {
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                } else {
                    RenderSystem.setShaderColor((float)color[0], (float)color[1], (float)color[2], (float)1.0f);
                }
                int glID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
                RenderSystem.setShaderTexture((int)0, (int)glID);
                RenderSystem.bindTexture((int)glID);
                boolean releaseBuffer = i == 16 || i == this.patterns.layers().size() || StarterClient.iris && Iris.isExtending();
                int drawCallIndex = i * size * 4;
                if (bufferedRenderer != null) {
                    if (StarterClient.iris && Iris.isExtending()) {
                        RenderSystem.setTextureMatrix((Matrix4f)this.textureMatrices[i]);
                    } else {
                        ByteBuffer buffer = bufferedRenderer.vertexBuffer();
                        int count = 0;
                        int ccount = 0;
                        int vertexCount = quads.size() * 6;
                        int vertexSize = buffer.capacity() / vertexCount;
                        long pointer = MemoryUtil.memAddress((ByteBuffer)buffer);
                        for (int j = 0; j < quads.size(); ++j) {
                            int multiple = j * 4;
                            int uvIndex = drawCallIndex + multiple;
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 3]);
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 2]);
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 1]);
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex]);
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 3]);
                            this.updateUV(pointer, vertexSize * count++, this.tmpUV[uvIndex + 1]);
                            if (!StarterClient.optifabric) continue;
                            this.updateColor(pointer, vertexSize * ccount++, color);
                            this.updateColor(pointer, vertexSize * ccount++, color);
                            this.updateColor(pointer, vertexSize * ccount++, color);
                            this.updateColor(pointer, vertexSize * ccount++, color);
                            this.updateColor(pointer, vertexSize * ccount++, color);
                            this.updateColor(pointer, vertexSize * ccount++, color);
                        }
                    }
                    ((RenderedBufferAccessor)bufferedRenderer.vertexBuffer).setIgnoreRelease(!releaseBuffer);
                    if (bufferedRenderer.indexBuffer != null) {
                        ((RenderedBufferAccessor)bufferedRenderer.indexBuffer).setIgnoreRelease(!releaseBuffer);
                    }
                    this.drawWithShader(bufferedRenderer);
                    continue;
                }
                if (StarterClient.iris && Iris.isExtending()) {
                    bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
                    for (j = 0; j < quads.size(); ++j) {
                        VerletQuad quad = quads.get(j);
                        RenderSystem.setTextureMatrix((Matrix4f)this.textureMatrices[i]);
                        if (ConfigClient.clothSmoothShading) {
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.point1.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.point2.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.point3.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.point4.bufferNormal, brightness, color);
                            continue;
                        }
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.bufferNormal, brightness, color);
                    }
                } else {
                    bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
                    for (j = 0; j < quads.size(); ++j) {
                        VerletQuad quad = quads.get(j);
                        int multiple = j * 4;
                        int uvIndex = drawCallIndex + multiple;
                        if (ConfigClient.clothSmoothShading) {
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.point4.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point3.renderPosition, this.tmpUV[uvIndex + 2], quad.point3.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.point2.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point1.renderPosition, this.tmpUV[uvIndex], quad.point1.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.point4.bufferNormal, brightness, color);
                            this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.point2.bufferNormal, brightness, color);
                            continue;
                        }
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point3.renderPosition, this.tmpUV[uvIndex + 2], quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point1.renderPosition, this.tmpUV[uvIndex], quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point4.renderPosition, this.tmpUV[uvIndex + 3], quad.bufferNormal, brightness, color);
                        this.bufferVertex(bufferbuilder, renderPercent, quad.point2.renderPosition, this.tmpUV[uvIndex + 1], quad.bufferNormal, brightness, color);
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
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setTextureMatrix((Matrix4f)oldTextureMatrix);
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
        dst.set(Math.remap(uv.x, 0.0f, 1.0f, minU, maxU), Math.remap(uv.y, 0.0f, 1.0f, minV, maxV));
    }

    private void bufferVertex(BufferBuilder bufferbuilder, double renderPercent, Vector3d position, Vector2f uv, Vector3d normal, int brightness, float[] color) {
        if (StarterClient.optifabric) {
            bufferbuilder.addVertex((float)position.x, (float)position.y, (float)position.z).setColor(color[0], color[1], color[2], 1.0f).setUv(uv.x, uv.y).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal((float)normal.x, (float)normal.y, (float)normal.z);
        } else {
            bufferbuilder.addVertex((float)position.x, (float)position.y, (float)position.z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.x, uv.y).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal((float)normal.x, (float)normal.y, (float)normal.z);
        }
    }

    private void updateUV(long pointer, int offset, Vector2f uv) {
        MemoryUtil.memPutFloat((long)(pointer + (long)offset + 16L), (float)uv.x);
        MemoryUtil.memPutFloat((long)(pointer + (long)offset + 20L), (float)uv.y);
    }

    private void updateColor(long pointer, int offset, float[] color) {
        MemoryUtil.memPutByte((long)(pointer + (long)offset + 12L), (byte)((byte)(color[0] * 255.0f)));
        MemoryUtil.memPutByte((long)(pointer + (long)offset + 13L), (byte)((byte)(color[1] * 255.0f)));
        MemoryUtil.memPutByte((long)(pointer + (long)offset + 14L), (byte)((byte)(color[2] * 255.0f)));
    }
}

