/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BannerRenderer
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BannerBlock
 *  net.minecraft.world.level.block.WallBannerBlock
 *  net.minecraft.world.level.block.entity.BannerBlockEntity
 *  net.minecraft.world.level.block.entity.BannerPatternLayers
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3d
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.BannerConstraint;
import net.diebuddies.physics.verlet.constraints.ClosestPlayerConstraint;
import net.diebuddies.physics.verlet.constraints.WindConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3d;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BannerRenderer.class})
public abstract class MixinBannerRenderer {
    @Shadow
    @Final
    private ModelPart flag;
    @Shadow
    @Final
    private ModelPart pole;
    @Shadow
    @Final
    private ModelPart bar;
    @Unique
    private Map<BannerBlockEntity, VerletSimulation> simulations = new Object2ObjectOpenHashMap();

    @Inject(at={@At(value="HEAD")}, method={"render"}, cancellable=true)
    public void render(BannerBlockEntity bannerBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int brightness, int j, CallbackInfo info) {
        if (ConfigClient.bannerPhysics) {
            boolean guiRendering;
            BannerPatternLayers list = bannerBlockEntity.getPatterns();
            boolean bl = guiRendering = bannerBlockEntity.getLevel() == null;
            if (list != null && !guiRendering && bannerBlockEntity.getLevel() instanceof ClientLevel) {
                BlockPos blockPos = bannerBlockEntity.getBlockPos();
                boolean isPlayerInSight = false;
                if (Minecraft.getInstance().player != null) {
                    boolean bl2 = isPlayerInSight = Minecraft.getInstance().player.blockPosition().distSqr((Vec3i)blockPos) < ConfigClient.bannerPhysicsRange * ConfigClient.bannerPhysicsRange;
                }
                if (isPlayerInSight) {
                    long gameTime;
                    poseStack.pushPose();
                    if (guiRendering) {
                        gameTime = 0L;
                        poseStack.translate(0.5, 0.5, 0.5);
                        this.pole.visible = true;
                    } else {
                        gameTime = bannerBlockEntity.getLevel().getGameTime();
                        BlockState blockState = bannerBlockEntity.getBlockState();
                        if (blockState.getBlock() instanceof BannerBlock) {
                            poseStack.translate(0.5, 0.5, 0.5);
                            blockRotation = (float)(-((Integer)blockState.getValue((Property)BannerBlock.ROTATION)).intValue() * 360) / 16.0f;
                            poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation));
                            this.pole.visible = true;
                        } else {
                            poseStack.translate(0.5, -0.1666666716337204, 0.5);
                            blockRotation = -((Direction)blockState.getValue((Property)WallBannerBlock.FACING)).toYRot();
                            poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation));
                            poseStack.translate(0.0, -0.3125, -0.4375);
                            this.pole.visible = false;
                        }
                    }
                    poseStack.pushPose();
                    poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
                    VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid);
                    this.pole.render(poseStack, vertexConsumer, brightness, j);
                    this.bar.render(poseStack, vertexConsumer, brightness, j);
                    float n = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + gameTime, 100L) + tickDelta) / 100.0f;
                    this.flag.xRot = (-0.0125f + 0.01f * Mth.cos((float)((float)Math.PI * 2 * n))) * (float)Math.PI;
                    this.flag.y = -32.0f;
                    Level level = bannerBlockEntity.getLevel();
                    Iterator<Map.Entry<BannerBlockEntity, VerletSimulation>> it = this.simulations.entrySet().iterator();
                    while (it.hasNext()) {
                        if (!it.next().getValue().destroyed) continue;
                        it.remove();
                    }
                    VerletSimulation simulation = this.simulations.get(bannerBlockEntity);
                    PhysicsMod mod = PhysicsMod.getInstance(level);
                    if (simulation == null) {
                        simulation = new VerletSimulation(new Vector3d((Vector3fc)ConfigClient.getGravity(bannerBlockEntity.getLevel().dimension().location())), 25, 0.93);
                        simulation.addConstraint(new WorldConstraint(bannerBlockEntity.getLevel(), 1.0f));
                        simulation.addConstraint(new ClosestPlayerConstraint(bannerBlockEntity.getLevel()));
                        simulation.addConstraint(new BannerConstraint(simulation, bannerBlockEntity, this.pole, this.bar, tickDelta));
                        simulation.addConstraint(new WindConstraint());
                        simulation.brightness = brightness;
                        simulation.textureID = PhysicsMod.whiteTexture.getID();
                        simulation.alwaysFetchInstantly = false;
                        this.simulations.put(bannerBlockEntity, simulation);
                        mod.physicsWorld.addVerletSimulation(simulation);
                    } else if (!simulation.destroyed) {
                        simulation.active = true;
                        simulation.brightness = brightness;
                    }
                    if (StarterClient.optifabric) {
                        PhysicsMod.optifineClothCompat.add(simulation);
                    } else {
                        simulation.renderSlow(level);
                    }
                    poseStack.popPose();
                    poseStack.popPose();
                    info.cancel();
                }
            }
        }
    }
}

