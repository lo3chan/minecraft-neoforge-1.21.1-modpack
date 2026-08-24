package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BannerRenderer.class})
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

   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      cancellable = true
   )
   public void render(
      BannerBlockEntity bannerBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int brightness, int j, CallbackInfo info
   ) {
      if (ConfigClient.bannerPhysics) {
         BannerPatternLayers list = bannerBlockEntity.getPatterns();
         boolean guiRendering = bannerBlockEntity.getLevel() == null;
         if (list != null && !guiRendering && bannerBlockEntity.getLevel() instanceof ClientLevel) {
            BlockPos blockPos = bannerBlockEntity.getBlockPos();
            boolean isPlayerInSight = false;
            if (Minecraft.getInstance().player != null) {
               isPlayerInSight = Minecraft.getInstance().player.blockPosition().distSqr(blockPos)
                  < ConfigClient.bannerPhysicsRange * ConfigClient.bannerPhysicsRange;
            }

            if (isPlayerInSight) {
               poseStack.pushPose();
               long gameTime;
               if (guiRendering) {
                  gameTime = 0L;
                  poseStack.translate(0.5, 0.5, 0.5);
                  this.pole.visible = true;
               } else {
                  gameTime = bannerBlockEntity.getLevel().getGameTime();
                  BlockState blockState = bannerBlockEntity.getBlockState();
                  if (blockState.getBlock() instanceof BannerBlock) {
                     poseStack.translate(0.5, 0.5, 0.5);
                     float blockRotation = -(Integer)blockState.getValue(BannerBlock.ROTATION) * 360 / 16.0F;
                     poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation));
                     this.pole.visible = true;
                  } else {
                     poseStack.translate(0.5, -0.1666666716337204, 0.5);
                     float blockRotation = -((Direction)blockState.getValue(WallBannerBlock.FACING)).toYRot();
                     poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation));
                     poseStack.translate(0.0, -0.3125, -0.4375);
                     this.pole.visible = false;
                  }
               }

               poseStack.pushPose();
               poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
               VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid);
               this.pole.render(poseStack, vertexConsumer, brightness, j);
               this.bar.render(poseStack, vertexConsumer, brightness, j);
               float n = ((float)Math.floorMod(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13 + gameTime, 100L) + tickDelta) / 100.0F;
               this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * n)) * 3.1415927F;
               this.flag.y = -32.0F;
               Level level = bannerBlockEntity.getLevel();
               Iterator<Entry<BannerBlockEntity, VerletSimulation>> it = this.simulations.entrySet().iterator();

               while (it.hasNext()) {
                  if (it.next().getValue().destroyed) {
                     it.remove();
                  }
               }

               VerletSimulation simulation = this.simulations.get(bannerBlockEntity);
               PhysicsMod mod = PhysicsMod.getInstance(level);
               if (simulation == null) {
                  simulation = new VerletSimulation(new Vector3d(ConfigClient.getGravity(bannerBlockEntity.getLevel().dimension().location())), 25, 0.93);
                  simulation.addConstraint(new WorldConstraint(bannerBlockEntity.getLevel(), 1.0F));
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
