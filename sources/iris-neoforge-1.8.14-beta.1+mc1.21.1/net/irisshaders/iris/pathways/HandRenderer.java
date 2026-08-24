package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.mixin.GameRendererAccessor;
import net.irisshaders.iris.mixinterface.ItemInHandInterface;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class HandRenderer {
   public static final HandRenderer INSTANCE = new HandRenderer();
   public static final float DEPTH = 0.125F;
   private final FullyBufferedMultiBufferSource bufferSource = new FullyBufferedMultiBufferSource();
   private boolean ACTIVE;
   private boolean renderingSolid;

   private PoseStack setupGlState(GameRenderer gameRenderer, Camera camera, Matrix4fc modelMatrix, float tickDelta) {
      PoseStack poseStack = new PoseStack();
      Matrix4f scaleMatrix = new Matrix4f().scale(1.0F, 1.0F, 0.125F);
      scaleMatrix.mul(gameRenderer.getProjectionMatrix(((GameRendererAccessor)gameRenderer).invokeGetFov(camera, tickDelta, false)));
      gameRenderer.resetProjectionMatrix(scaleMatrix);
      poseStack.setIdentity();
      ((GameRendererAccessor)gameRenderer).invokeBobHurt(poseStack, tickDelta);
      if ((Boolean)Minecraft.getInstance().options.bobView().get()) {
         ((GameRendererAccessor)gameRenderer).invokeBobView(poseStack, tickDelta);
      }

      return poseStack;
   }

   private boolean canRender(Camera camera, GameRenderer gameRenderer) {
      return ((GameRendererAccessor)gameRenderer).getRenderHand()
         && !camera.isDetached()
         && camera.getEntity() instanceof Player
         && !((GameRendererAccessor)gameRenderer).getPanoramicMode()
         && !Minecraft.getInstance().options.hideGui
         && (!(camera.getEntity() instanceof LivingEntity) || !((LivingEntity)camera.getEntity()).isSleeping())
         && Minecraft.getInstance().gameMode.getPlayerMode() != GameType.SPECTATOR;
   }

   public boolean isHandTranslucent(ItemStack itemStack) {
      Item item = itemStack.getItem();
      return item instanceof BlockItem
         ? ItemBlockRenderTypes.getChunkRenderType(((BlockItem)item).getBlock().defaultBlockState()) == RenderType.translucent()
         : false;
   }

   public void renderSolid(Matrix4fc modelMatrix, float tickDelta, Camera camera, GameRenderer gameRenderer, WorldRenderingPipeline pipeline) {
      if (this.canRender(camera, gameRenderer) && ((ItemInHandInterface)gameRenderer.itemInHandRenderer).iris$isAnyHandSolid() && Iris.isPackInUseQuick()) {
         this.ACTIVE = true;
         PoseStack poseStack = this.setupGlState(gameRenderer, camera, modelMatrix, tickDelta);
         pipeline.setPhase(WorldRenderingPhase.HAND_SOLID);
         poseStack.pushPose();
         Minecraft.getInstance().getProfiler().push("iris_hand");
         this.renderingSolid = true;
         RenderSystem.getModelViewStack().pushMatrix();
         RenderSystem.getModelViewStack().set(poseStack.last().pose());
         RenderSystem.applyModelViewMatrix();
         gameRenderer.itemInHandRenderer
            .renderHandsWithItems(
               tickDelta,
               new PoseStack(),
               this.bufferSource.getUnflushableWrapper(),
               Minecraft.getInstance().player,
               Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(camera.getEntity(), tickDelta)
            );
         Minecraft.getInstance().getProfiler().pop();
         this.bufferSource.readyUp();
         this.bufferSource.endBatch();
         gameRenderer.resetProjectionMatrix(new Matrix4f(CapturedRenderingState.INSTANCE.getGbufferProjection()));
         poseStack.popPose();
         RenderSystem.getModelViewStack().popMatrix();
         RenderSystem.applyModelViewMatrix();
         this.renderingSolid = false;
         pipeline.setPhase(WorldRenderingPhase.NONE);
         this.ACTIVE = false;
      }
   }

   public void renderTranslucent(Matrix4fc modelMatrix, float tickDelta, Camera camera, GameRenderer gameRenderer, WorldRenderingPipeline pipeline) {
      if (this.canRender(camera, gameRenderer) && ((ItemInHandInterface)gameRenderer.itemInHandRenderer).iris$isAnyHandTranslucent() && Iris.isPackInUseQuick()
         )
       {
         this.ACTIVE = true;
         pipeline.setPhase(WorldRenderingPhase.HAND_TRANSLUCENT);
         PoseStack poseStack = this.setupGlState(gameRenderer, camera, modelMatrix, tickDelta);
         poseStack.pushPose();
         Minecraft.getInstance().getProfiler().push("iris_hand_translucent");
         RenderSystem.getModelViewStack().pushMatrix();
         RenderSystem.getModelViewStack().set(poseStack.last().pose());
         RenderSystem.applyModelViewMatrix();
         gameRenderer.itemInHandRenderer
            .renderHandsWithItems(
               tickDelta,
               new PoseStack(),
               this.bufferSource,
               Minecraft.getInstance().player,
               Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(camera.getEntity(), tickDelta)
            );
         poseStack.popPose();
         Minecraft.getInstance().getProfiler().pop();
         gameRenderer.resetProjectionMatrix(new Matrix4f(CapturedRenderingState.INSTANCE.getGbufferProjection()));
         this.bufferSource.endBatch();
         RenderSystem.getModelViewStack().popMatrix();
         RenderSystem.applyModelViewMatrix();
         pipeline.setPhase(WorldRenderingPhase.NONE);
         this.ACTIVE = false;
      }
   }

   public boolean isActive() {
      return this.ACTIVE;
   }

   public boolean isRenderingSolid() {
      return this.renderingSolid;
   }

   public FullyBufferedMultiBufferSource getBufferSource() {
      return this.bufferSource;
   }
}
