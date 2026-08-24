package net.mehvahdjukaar.moonlight.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemRenderExtension {
   @Nullable
   default ItemStackRenderer getItemRenderer() {
      return null;
   }

   default void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
   }

   default boolean animateItemFirstPerson(
      Player entity,
      ItemStack stack,
      InteractionHand hand,
      HumanoidArm arm,
      PoseStack poseStack,
      float partialTicks,
      float pitch,
      float attackAnim,
      float handHeight
   ) {
      return false;
   }

   default boolean renderFirstPersonItem(
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
      return false;
   }

   default <T extends LivingEntity> boolean poseRightArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand) {
      return false;
   }

   default <T extends LivingEntity> boolean poseLeftArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand) {
      return false;
   }

   default ItemRenderExtension.HandMode getHandMode() {
      return ItemRenderExtension.HandMode.DEFAULT;
   }

   default <T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> boolean renderThirdPersonItem(
      M parentModel, LivingEntity entity, ItemStack stack, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int light
   ) {
      return false;
   }

   public static enum HandMode {
      DEFAULT,
      TWO_HANDED,
      SINGLE_HANDED;
   }
}
