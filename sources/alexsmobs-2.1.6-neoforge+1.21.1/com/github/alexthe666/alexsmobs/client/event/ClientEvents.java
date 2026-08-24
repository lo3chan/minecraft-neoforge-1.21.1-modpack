package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetFluidRenderType;
import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.alexsmobs.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe666.alexsmobs.client.command.AMShieldPoseCommand;
import com.github.alexthe666.alexsmobs.client.model.ModelRockyChestplateRolling;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.effect.EffectPowerDown;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
   private static final ResourceLocation ROCKY_CHESTPLATE_TEXTURE = AMCompat.rl("alexsmobs:textures/armor/rocky_chestplate.png");
   private static final ModelRockyChestplateRolling ROCKY_CHESTPLATE_MODEL = new ModelRockyChestplateRolling();
   private boolean previousLavaVision = false;
   private LiquidBlockRenderer previousFluidRenderer;
   public static long lastStaticTick = -1L;
   public static int renderStaticScreenFor = 0;

   @SubscribeEvent
   public void onOutlineEntityColor(EventGetOutlineColor event) {
      if (event.getEntityIn() instanceof Enemy
         && AlexsMobs.PROXY.getSingingBlueJayId() != -1
         && event.getEntityIn().level().getEntity(AlexsMobs.PROXY.getSingingBlueJayId()) instanceof EntityBlueJay jay
         && jay.isAlive()
         && jay.isMakingMonstersBlue()) {
         event.setColor(4953598);
         event.setHandled(true);
      }

      if (event.getEntityIn() instanceof ItemEntity && ((ItemEntity)event.getEntityIn()).getItem().is(AMTagRegistry.VOID_WORM_DROPS)) {
         int fromColor = 0;
         int toColor = 2221567;
         float startR = (fromColor >> 16 & 0xFF) / 255.0F;
         float startG = (fromColor >> 8 & 0xFF) / 255.0F;
         float startB = (fromColor & 0xFF) / 255.0F;
         float endR = (toColor >> 16 & 0xFF) / 255.0F;
         float endG = (toColor >> 8 & 0xFF) / 255.0F;
         float endB = (toColor & 0xFF) / 255.0F;
         float f = (float)(Math.cos(0.4F * (event.getEntityIn().tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true))) + 1.0)
            * 0.5F;
         float r = (endR - startR) * f + startR;
         float g = (endG - startG) * f + startG;
         float b = (endB - startB) * f + startB;
         int j = ((int)(r * 255.0F) & 0xFF) << 16 | ((int)(g * 255.0F) & 0xFF) << 8 | ((int)(b * 255.0F) & 0xFF) << 0;
         event.setColor(j);
         event.setHandled(true);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onGetStarBrightness(EventGetStarBrightness event) {
      if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()))
         && Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get())) != null) {
         MobEffectInstance instance = Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()));
         EffectPowerDown powerDown = (EffectPowerDown)AMCompat.rawEffect(instance);
         int duration = instance.getDuration();
         float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         float f = (Math.min(powerDown.getActiveTime(), duration) + partialTicks) * 0.1F;
         event.setBrightness(0.0F);
         event.setHandled(true);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onFogColor(ComputeFogColor event) {
      if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()))
         && Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get())) != null) {
         event.setBlue(0.0F);
         event.setRed(0.0F);
         event.setGreen(0.0F);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   @OnlyIn(Dist.CLIENT)
   public void onFogDensity(RenderFog event) {
      FogType fogType = event.getCamera().getFluidInCamera();
      if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.LAVA_VISION.get())) && fogType == FogType.LAVA) {
         event.setNearPlaneDistance(-8.0F);
         event.setFarPlaneDistance(50.0F);
         event.setCanceled(true);
      }

      if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()))
         && fogType == FogType.NONE
         && Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get())) != null) {
         float initEnd = event.getFarPlaneDistance();
         MobEffectInstance instance = Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()));
         EffectPowerDown powerDown = (EffectPowerDown)AMCompat.rawEffect(instance);
         int duration = instance.getDuration();
         float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         float f = Math.min(20.0F, Math.min(powerDown.getActiveTime() + partialTicks, duration + partialTicks)) * 0.05F;
         event.setNearPlaneDistance(-8.0F);
         float f1 = 8.0F + (1.0F - f) * Math.max(0.0F, initEnd - 8.0F);
         event.setFarPlaneDistance(f1);
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPreRenderEntity(Pre event) {
      LivingEntity amEntity = renderedEntity(event);
      float amPartialTick = renderedPartialTick(event);
      if (amEntity != null) {
         if (RockyChestplateUtil.isRockyRolling(amEntity)) {
            event.setCanceled(true);
            event.getPoseStack().pushPose();
            float limbSwing = amEntity.walkAnimation.position() - amEntity.walkAnimation.speed() * (1.0F - amPartialTick);
            float limbSwingAmount = amEntity.walkAnimation.speed(renderedLight(event));
            float yRot = amEntity.yBodyRotO + (amEntity.yBodyRot - amEntity.yBodyRotO) * amPartialTick;
            float roll = amEntity.walkDistO + (amEntity.walkDist - amEntity.walkDistO) * amPartialTick;
            MultiBufferSource amBuffers = renderedBuffers(event);
            VertexConsumer vertexconsumer = AMRenderCompat.armorFoilBuffer(
               amBuffers, RenderType.armorCutoutNoCull(ROCKY_CHESTPLATE_TEXTURE), amEntity.getItemBySlot(EquipmentSlot.CHEST).hasFoil()
            );
            event.getPoseStack().translate(0.0, amEntity.getBbHeight() - amEntity.getBbHeight() * 0.5F, 0.0);
            event.getPoseStack().mulPose(Axis.YN.rotationDegrees(180.0F + yRot));
            event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(180.0F));
            event.getPoseStack().mulPose(Axis.XP.rotationDegrees(100.0F * roll));
            ROCKY_CHESTPLATE_MODEL.setupAnim(amEntity, limbSwing, limbSwingAmount, amEntity.tickCount + amPartialTick, 0.0F, 0.0F);
            ROCKY_CHESTPLATE_MODEL.renderToBuffer(event.getPoseStack(), vertexconsumer, renderedLight(event), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            event.getPoseStack().popPose();
            flushBuffers(amBuffers);
            NeoForge.EVENT_BUS
               .post(new Post(amEntity, event.getRenderer(), amPartialTick, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
         } else {
            if (amEntity instanceof WanderingTrader
               && amEntity.getType() == EntityType.WANDERING_TRADER
               && amEntity.getVehicle() instanceof EntityElephant
               && !(event.getRenderer().model instanceof ModelWanderingVillagerRider)) {
               event.getRenderer().model = new ModelWanderingVillagerRider(
                  Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SITTING_WANDERING_VILLAGER)
               );
            }

            if (EffectClinging.isFlippedUpsideDown(amEntity)
               || amEntity.hasEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()))
                  && AMCompat.isArthropod(amEntity)
                  && amEntity.getBbWidth() > amEntity.getBbHeight()) {
               event.getPoseStack().pushPose();
               event.getPoseStack().translate(0.0, amEntity.getBbHeight() + 0.1F, 0.0);
               event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(180.0F));
               flipUpsideDown(event, amEntity);
            }

            if (amEntity.hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()))) {
               event.getPoseStack().pushPose();
               event.getPoseStack().mulPose(Axis.YP.rotationDegrees((float)(Math.cos(amEntity.tickCount * 7.0) * 3.141592653589793 * 1.2000000476837158)));
               float vibrate = 0.05F;
               event.getPoseStack()
                  .translate(
                     (amEntity.getRandom().nextFloat() - 0.5F) * vibrate,
                     (amEntity.getRandom().nextFloat() - 0.5F) * vibrate,
                     (amEntity.getRandom().nextFloat() - 0.5F) * vibrate
                  );
            }
         }
      }
   }

   private static LivingEntity renderedEntity(RenderLivingEvent<?, ?> event) {
      return event.getEntity();
   }

   private static float renderedPartialTick(RenderLivingEvent<?, ?> event) {
      return event.getPartialTick();
   }

   private static MultiBufferSource renderedBuffers(RenderLivingEvent<?, ?> event) {
      return event.getMultiBufferSource();
   }

   private static MultiBufferSource handBuffers(RenderHandEvent event) {
      return event.getMultiBufferSource();
   }

   private static void flushBuffers(MultiBufferSource buffers) {
   }

   private static int renderedLight(RenderLivingEvent<?, ?> event) {
      return event.getPackedLight();
   }

   private static void flipUpsideDown(RenderLivingEvent<?, ?> event, LivingEntity entity) {
      entity.yBodyRotO = -entity.yBodyRotO;
      entity.yBodyRot = -entity.yBodyRot;
      entity.yHeadRotO = -entity.yHeadRotO;
      entity.yHeadRot = -entity.yHeadRot;
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPostRenderEntity(Post event) {
      LivingEntity amEntity = renderedEntity(event);
      float amPartialTick = renderedPartialTick(event);
      if (amEntity != null && !RockyChestplateUtil.isRockyRolling(amEntity)) {
         if (amEntity.hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()))) {
            event.getPoseStack().popPose();
         }

         if (EffectClinging.isFlippedUpsideDown(amEntity)
            || amEntity.hasEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()))
               && AMCompat.isArthropod(amEntity)
               && amEntity.getBbWidth() > amEntity.getBbHeight()) {
            event.getPoseStack().popPose();
            flipUpsideDown(event, amEntity);
         }

         if (VineLassoUtil.hasLassoData(amEntity) && !(amEntity instanceof Player)) {
            Entity lassoedOwner = VineLassoUtil.getLassoedTo(amEntity);
            if (lassoedOwner instanceof LivingEntity && lassoedOwner != amEntity) {
               double d0 = Mth.lerp(amPartialTick, amEntity.xOld, amEntity.getX());
               double d1 = Mth.lerp(amPartialTick, amEntity.yOld, amEntity.getY());
               double d2 = Mth.lerp(amPartialTick, amEntity.zOld, amEntity.getZ());
               event.getPoseStack().pushPose();
               event.getPoseStack().translate(-d0, -d1, -d2);
               MultiBufferSource amBuffers = renderedBuffers(event);
               RenderVineLasso.renderVine(
                  amEntity,
                  amPartialTick,
                  event.getPoseStack(),
                  amBuffers,
                  (LivingEntity)lassoedOwner,
                  ((LivingEntity)lassoedOwner).getMainArm() == HumanoidArm.LEFT,
                  0.1F
               );
               flushBuffers(amBuffers);
               event.getPoseStack().popPose();
            }
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPoseHand(EventPosePlayerHand event) {
      LivingEntity player = (LivingEntity)event.getEntityIn();
      float f = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
      boolean leftHand = false;
      boolean usingLasso = player.isUsingItem() && player.getUseItem().is(AMItemRegistry.VINE_LASSO.get());
      if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.VINE_LASSO.get()) {
         leftHand = player.getMainArm() == HumanoidArm.LEFT;
      } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.VINE_LASSO.get()) {
         leftHand = player.getMainArm() != HumanoidArm.LEFT;
      }

      if (leftHand && event.isLeftHand() && usingLasso) {
         event.setHandled(true);
         event.getModel().leftArm.xRot = Maths.rad(-120.0) + Mth.sin(player.tickCount + f) * 0.5F;
         event.getModel().leftArm.yRot = Maths.rad(-20.0) + Mth.cos(player.tickCount + f) * 0.5F;
      }

      if (!leftHand && !event.isLeftHand() && usingLasso) {
         event.setHandled(true);
         event.getModel().rightArm.xRot = Maths.rad(-120.0) + Mth.sin(player.tickCount + f) * 0.5F;
         event.getModel().rightArm.yRot = Maths.rad(20.0) - Mth.cos(player.tickCount + f) * 0.5F;
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderHand(RenderHandEvent event) {
      if (Minecraft.getInstance().getCameraEntity() instanceof IFalconry) {
         event.setCanceled(true);
      }

      if (!Minecraft.getInstance().player.getPassengers().isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
         Player player = Minecraft.getInstance().player;
         boolean leftHand = false;
         if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = player.getMainArm() == HumanoidArm.LEFT;
         } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = player.getMainArm() != HumanoidArm.LEFT;
         }

         for (Entity entity : player.getPassengers()) {
            if (entity instanceof IFalconry falconry) {
               float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * event.getPartialTick();
               ClientProxy.currentUnrenderedEntities.remove(entity.getUUID());
               PoseStack matrixStackIn = event.getPoseStack();
               matrixStackIn.pushPose();
               matrixStackIn.scale(0.5F, 0.5F, 0.5F);
               matrixStackIn.translate(leftHand ? -falconry.getHandOffset() : falconry.getHandOffset(), -0.6F, -1.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
               if (leftHand) {
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
               } else {
                  matrixStackIn.mulPose(Axis.YN.rotationDegrees(90.0F));
               }

               MultiBufferSource amBuffers = handBuffers(event);
               this.renderEntity(entity, 0.0, 0.0, 0.0, 0.0F, event.getPartialTick(), matrixStackIn, amBuffers, event.getPackedLight());
               flushBuffers(amBuffers);
               matrixStackIn.popPose();
               ClientProxy.currentUnrenderedEntities.add(entity.getUUID());
            }
         }
      }

      if (Minecraft.getInstance().player.getUseItem().getItem() instanceof ItemDimensionalCarver
         && event.getItemStack().getItem() instanceof ItemDimensionalCarver) {
         PoseStack matrixStackIn = event.getPoseStack();
         matrixStackIn.pushPose();
         ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
         InteractionHand hand = (InteractionHand)MoreObjects.firstNonNull(Minecraft.getInstance().player.swingingArm, InteractionHand.MAIN_HAND);
         float f = Minecraft.getInstance().player.getAttackAnim(event.getPartialTick());
         float f5 = -0.4F * Mth.sin(Mth.sqrt(f) * 3.1415927F);
         float f6 = 0.2F * Mth.sin(Mth.sqrt(f) * 6.2831855F);
         float f10 = -0.2F * Mth.sin(f * 3.1415927F);
         HumanoidArm handside = hand == InteractionHand.MAIN_HAND
            ? Minecraft.getInstance().player.getMainArm()
            : Minecraft.getInstance().player.getMainArm().getOpposite();
         boolean flag3 = handside == HumanoidArm.RIGHT;
         int l = flag3 ? 1 : -1;
         matrixStackIn.translate(l * f5, f6, f10);
      }
   }

   public <E extends Entity> void renderEntity(
      E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight
   ) {
      AMRenderCompat.renderEntity(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderNameplate(RenderNameTagEvent event) {
      Entity nameTagEntity = event.getEntity();
      if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle
         && nameTagEntity == Minecraft.getInstance().player
         && Minecraft.getInstance().hasSingleplayerServer()) {
         event.setCanRender(TriState.FALSE);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
      if (AMShieldPoseCommand.available()) {
         AMShieldPoseCommand.register(event.getDispatcher(), (source, message) -> source.sendSuccess(() -> message, false));
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderWorldLastEvent(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_SKY) {
         this.doWorldLastFrame();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void doWorldLastFrame() {
      if (!AMConfig.shadersCompat) {
         if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.LAVA_VISION.get()))) {
            if (!this.previousLavaVision) {
               this.previousFluidRenderer = Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer;
               Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer = new LavaVisionFluidRenderer();
               this.updateAllChunks();
            }
         } else if (this.previousLavaVision) {
            if (this.previousFluidRenderer != null) {
               Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer = this.previousFluidRenderer;
            }

            this.updateAllChunks();
         }

         this.previousLavaVision = Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.LAVA_VISION.get()));
         if (AMConfig.clingingFlipEffect) {
            if (EffectClinging.isFlippedUpsideDown(Minecraft.getInstance().player)) {
               Minecraft.getInstance().gameRenderer.loadEffect(AMCompat.rl("shaders/post/flip.json"));
            } else if (Minecraft.getInstance().gameRenderer.currentEffect() != null
               && Minecraft.getInstance().gameRenderer.currentEffect().getName().equals("minecraft:shaders/post/flip.json")) {
               Minecraft.getInstance().gameRenderer.shutdownEffect();
            }
         }
      }

      if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle) {
         EntityBaldEagle eagle = (EntityBaldEagle)Minecraft.getInstance().getCameraEntity();
         LocalPlayer playerEntity = Minecraft.getInstance().player;
         if (!((EntityBaldEagle)Minecraft.getInstance().getCameraEntity()).shouldHoodedReturn() && !eagle.isRemoved()) {
            float rotX = Mth.wrapDegrees(playerEntity.getYRot() + playerEntity.yHeadRot);
            float rotY = playerEntity.getXRot();
            Entity over = null;
            if (Minecraft.getInstance().hitResult instanceof EntityHitResult) {
               over = ((EntityHitResult)Minecraft.getInstance().hitResult).getEntity();
            } else {
               Minecraft.getInstance().hitResult = null;
            }

            boolean loadChunks = playerEntity.level().getDayTime() % 10L == 0L;
            ((EntityBaldEagle)Minecraft.getInstance().getCameraEntity()).directFromPlayer(rotX, rotY, false, over);
            AlexsMobs.sendMSGToServer(
               new MessageUpdateEagleControls(Minecraft.getInstance().getCameraEntity().getId(), rotX, rotY, loadChunks, over == null ? -1 : over.getId())
            );
         } else {
            Minecraft.getInstance().setCameraEntity(playerEntity);
            Minecraft.getInstance().options.setCameraType(CameraType.values()[AlexsMobs.PROXY.getPreviousPOV()]);
         }
      }
   }

   private void updateAllChunks() {
      if (Minecraft.getInstance().levelRenderer.viewArea != null) {
         int length = Minecraft.getInstance().levelRenderer.viewArea.sections.length;

         for (int i = 0; i < length; i++) {
            Minecraft.getInstance().levelRenderer.viewArea.sections[i].setDirty(false);
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onGetFluidRenderType(EventGetFluidRenderType event) {
      if (Minecraft.getInstance().player.hasEffect(AMCompat.effect(AMEffectRegistry.LAVA_VISION.get()))
         && (event.getFluidState().is(Fluids.LAVA) || event.getFluidState().is(Fluids.FLOWING_LAVA))) {
         event.setRenderType(RenderType.translucent());
         event.setHandled(true);
      }
   }

   @SubscribeEvent
   public void clientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
      AMItemstackRenderer.incrementTick();
      tickClinging();
   }

   private static void tickClinging() {
   }

   public static void applyEarthquakeShake(Camera camera) {
      if (Minecraft.getInstance().player != null) {
         if (Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.EARTHQUAKE.get())) != null && !Minecraft.getInstance().isPaused()) {
            int duration = Minecraft.getInstance().player.getEffect(AMCompat.effect(AMEffectRegistry.EARTHQUAKE.get())).getDuration();
            float f = (Math.min(10, duration) + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)) * 0.1F;
            float intensity = f * ((Double)Minecraft.getInstance().options.screenEffectScale().get()).floatValue();
            RandomSource rng = Minecraft.getInstance().player.getRandom();
            camera.move(rng.nextFloat() * 0.1F * intensity, rng.nextFloat() * 0.2F * intensity, rng.nextFloat() * 0.4F * intensity);
         }
      }
   }

   public static void renderStaticOverlay(GuiGraphics guiGraphics, float partialTick) {
      Minecraft mc = Minecraft.getInstance();
      if (renderStaticScreenFor > 0 && mc.player != null && mc.level != null) {
         if (mc.player.isAlive() && lastStaticTick != mc.level.getGameTime()) {
            renderStaticScreenFor--;
         }

         float staticLevel = renderStaticScreenFor / 60.0F;
         float screenWidth = mc.getWindow().getScreenWidth();
         float screenHeight = mc.getWindow().getScreenHeight();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         float ageInTicks = (float)mc.level.getGameTime() + partialTick;
         float staticIndexX = (float)Math.sin(ageInTicks * 0.2F) * 2.0F;
         float staticIndexY = (float)Math.cos(ageInTicks * 0.2F + 3.0F) * 2.0F;
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, staticLevel);
         RenderSystem.setShaderTexture(0, AMRenderTypes.STATIC_TEXTURE);
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         float minU = 10.0F * staticIndexX * 0.125F;
         float maxU = 10.0F * (0.5F + staticIndexX * 0.125F);
         float minV = 10.0F * staticIndexY * 0.125F;
         float maxV = 10.0F * (0.125F + staticIndexY * 0.125F);
         bufferbuilder.addVertex(0.0F, screenHeight, -190.0F).setUv(minU, maxV);
         bufferbuilder.addVertex(screenWidth, screenHeight, -190.0F).setUv(maxU, maxV);
         bufferbuilder.addVertex(screenWidth, 0.0F, -190.0F).setUv(maxU, minV);
         bufferbuilder.addVertex(0.0F, 0.0F, -190.0F).setUv(minU, minV);
         BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         lastStaticTick = mc.level.getGameTime();
      }
   }
}
