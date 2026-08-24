package net.mehvahdjukaar.amendments.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.integration.CompatObjects;
import net.mehvahdjukaar.moonlight.api.client.util.VertexUtil;
import net.mehvahdjukaar.moonlight.api.item.IFirstPersonSpecialItemRenderer;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonAnimationProvider;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonSpecialItemRenderer;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TorchRendererExtension implements IThirdPersonAnimationProvider, IThirdPersonSpecialItemRenderer, IFirstPersonSpecialItemRenderer {
   private static final ResourceLocation FLAME_TEXTURE = ResourceLocation.withDefaultNamespace("textures/particle/flame.png");
   private static final ResourceLocation SOUL_FLAME_TEXTURE = ResourceLocation.withDefaultNamespace("textures/particle/soul_fire_flame.png");
   private static final ResourceLocation REDSTONE_FLAME_TEXTURE = ResourceLocation.withDefaultNamespace("textures/particle/generic_6.png");
   private static final Vec3 TORCH_PARTICLE_OFFSET = new Vec3(0.0, 0.16, 0.0);

   public <T extends LivingEntity> boolean poseRightArm(ItemStack itemStack, HumanoidModel<T> model, T t, HumanoidArm arm) {
      if (ClientConfigs.HOLDING_ANIMATION_FIXED.get()) {
         model.rightArm.xRot = -1.3F;
      } else {
         model.rightArm.xRot = Mth.clamp(MthUtils.wrapRad(-1.4F + model.head.xRot), -2.4F, -0.2F);
      }

      return true;
   }

   public <T extends LivingEntity> boolean poseLeftArm(ItemStack itemStack, HumanoidModel<T> model, T t, HumanoidArm arm) {
      if (ClientConfigs.HOLDING_ANIMATION_FIXED.get()) {
         model.leftArm.xRot = -1.3F;
      } else {
         model.leftArm.xRot = Mth.clamp(MthUtils.wrapRad(-1.4F + model.head.xRot), -2.4F, -0.2F);
      }

      return true;
   }

   public <T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> void renderThirdPersonItem(
      M parentModel, LivingEntity entity, ItemStack stack, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int light
   ) {
      if (!stack.isEmpty()) {
         poseStack.pushPose();
         parentModel.translateToHand(humanoidArm, poseStack);
         poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean left = humanoidArm == HumanoidArm.LEFT;
         poseStack.translate((left ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         poseStack.scale(1.0F, 1.0F, 1.0F);
         poseStack.translate(0.0F, 0.1875F, 0.125F);
         renderTorchModel(entity, stack, poseStack, bufferSource, light, left);
         if (ClientConfigs.TORCH_HOLDING_FLAME.get() && !entity.isInWater()) {
            this.renderFlame(entity, poseStack, bufferSource, stack);
         }

         poseStack.popPose();
      }
   }

   private void renderFlame(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack) {
      VertexConsumer builder = bufferSource.getBuffer(RenderType.text(getFlameTexture(stack)));
      int lu = 240;
      int lv = 240;
      int b = 255;
      int g = 255;
      int r = 255;
      int a = 255;
      float period = 20.0F;
      float t = (entity.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)) % period / period;
      float ss = 1.0F - t * t * 0.4F;
      float scale = ss * 4.0F / 16.0F;
      Matrix4f mat = new Matrix4f();
      Quaternionf cameraRot = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
      poseStack.translate(TORCH_PARTICLE_OFFSET.x, TORCH_PARTICLE_OFFSET.y, TORCH_PARTICLE_OFFSET.z);
      mat.setTranslation(poseStack.last().pose().getTranslation(new Vector3f()));
      mat.rotate(cameraRot);
      poseStack.last().pose().set(mat);
      poseStack.scale(-scale, scale, -scale);
      VertexUtil.addQuad(builder, poseStack, -0.5F, -0.5F, 0.5F, 0.5F, r, g, b, a, lu, lv);
   }

   private static ResourceLocation getFlameTexture(ItemStack stack) {
      String path = Utils.getID(stack.getItem()).getPath();
      if (path.contains("soul")) {
         return SOUL_FLAME_TEXTURE;
      } else {
         return path.contains("redstone") ? REDSTONE_FLAME_TEXTURE : FLAME_TEXTURE;
      }
   }

   private static void renderTorchModel(LivingEntity entity, ItemStack itemStack, PoseStack poseStack, MultiBufferSource buffer, int light, boolean left) {
      Minecraft mc = Minecraft.getInstance();
      ItemRenderer itemRenderer = mc.getItemRenderer();
      Item item = itemStack.getItem();
      if (item == CompatObjects.SCONCE_LEVER.get()) {
         item = CompatObjects.SCONCE.get();
      }

      BlockState state = ((BlockItem)item).getBlock().defaultBlockState();
      BakedModel model = mc.getBlockRenderer().getBlockModel(state);
      itemRenderer.render(itemStack, ItemDisplayContext.NONE, left, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, model);
   }

   public boolean renderFirstPersonItem(
      AbstractClientPlayer player,
      ItemStack stack,
      InteractionHand hand,
      HumanoidArm arm,
      PoseStack poseStack,
      float partialTicks,
      float pitch,
      float attackAnim,
      float equipAnim,
      MultiBufferSource buffer,
      int light,
      ItemInHandRenderer renderer
   ) {
      boolean left = arm == HumanoidArm.LEFT;
      float f = left ? -1.0F : 1.0F;
      poseStack.pushPose();
      float n = -0.4F * Mth.sin(Mth.sqrt(attackAnim) * 3.1415927F);
      float m = 0.2F * Mth.sin(Mth.sqrt(attackAnim) * 6.2831855F);
      float h = -0.2F * Mth.sin(attackAnim * 3.1415927F);
      poseStack.translate(f * n, m, h);
      renderer.applyItemArmTransform(poseStack, arm, equipAnim);
      renderer.applyItemArmAttackTransform(poseStack, arm, attackAnim);
      ItemTransform transform = new ItemTransform(
         new Vector3f(0.0F, -90.0F, 25.0F), new Vector3f(0.0F, 2.0F, 1.25F).mul(0.0625F), new Vector3f(0.68F, 0.68F, 0.68F)
      );
      transform.apply(left, poseStack);
      poseStack.translate(f * 0.5 / 16.0, 0.103125, -0.0625);
      float scale = (float)ClientConfigs.TORCH_HOLDING_SIZE.get().doubleValue();
      poseStack.scale(scale, scale, scale);
      renderTorchModel(player, stack, poseStack, buffer, light, left);
      if (ClientConfigs.TORCH_HOLDING_FLAME.get() && !player.isInWater()) {
         this.renderFlame(player, poseStack, buffer, stack);
      }

      poseStack.popPose();
      return true;
   }
}
