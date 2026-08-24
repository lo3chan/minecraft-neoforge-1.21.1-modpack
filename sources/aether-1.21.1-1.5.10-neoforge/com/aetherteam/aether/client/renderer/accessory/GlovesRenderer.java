package com.aetherteam.aether.client.renderer.accessory;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class GlovesRenderer implements AccessoryRenderer {
   private final GlovesModel glovesModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES));
   private final GlovesModel glovesTrimModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_TRIM));
   private final GlovesModel glovesModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_SLIM));
   private final GlovesModel glovesTrimModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_TRIM_SLIM));
   private final GlovesModel glovesFirstPerson = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_FIRST_PERSON));
   private final GlovesModel glovesTrimFirstPerson = new GlovesModel(
      Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_TRIM_FIRST_PERSON)
   );
   private final TextureAtlas armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);

   public <M extends LivingEntity> void render(
      ItemStack stack,
      SlotReference reference,
      PoseStack poseStack,
      EntityModel<M> entityModel,
      MultiBufferSource buffer,
      int packedLight,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      GlovesItem glovesItem = (GlovesItem)stack.getItem();
      GlovesModel model = this.glovesModel;
      GlovesModel trimModel = this.glovesTrimModel;
      ResourceLocation texture = glovesItem.getGlovesTexture();
      if (entityModel instanceof PlayerModel<?> playerModel) {
         PlayerModelAccessor playerModelAccessor = (PlayerModelAccessor)playerModel;
         model = playerModelAccessor.aether$getSlim() ? this.glovesModelSlim : this.glovesModel;
         trimModel = playerModelAccessor.aether$getSlim() ? this.glovesTrimModelSlim : this.glovesTrimModel;
      }

      AccessoryRenderer.followBodyRotations(reference.entity(), model);
      AccessoryRenderer.followBodyRotations(reference.entity(), trimModel);
      int color = IClientItemExtensions.of(stack).getDefaultDyeColor(stack);
      VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(texture));
      model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
      ArmorTrim trim = (ArmorTrim)stack.get(DataComponents.TRIM);
      if (trim != null) {
         TextureAtlasSprite textureAtlasSprite = this.armorTrimAtlas.getSprite(trim.outerTexture(glovesItem.getMaterial()));
         VertexConsumer trimConsumer = textureAtlasSprite.wrap(buffer.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)trim.pattern().value()).decal())));
         trimModel.renderToBuffer(poseStack, trimConsumer, packedLight, OverlayTexture.NO_OVERLAY);
      }

      if (stack.hasFoil()) {
         model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
      }
   }

   public boolean shouldRenderInFirstPerson(HumanoidArm arm, ItemStack stack, SlotReference reference) {
      return !(
         reference.entity() instanceof Player player
            && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isWearingInvisibilityCloak()
      );
   }

   public <M extends LivingEntity> void renderOnFirstPerson(
      HumanoidArm arm, ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light
   ) {
      if (reference.entity() instanceof AbstractClientPlayer player && model instanceof HumanoidModel<M> humanoidModel) {
         this.renderFirstPerson(stack, matrices, multiBufferSource, light, player, humanoidModel, arm);
      }
   }

   public void renderFirstPerson(
      ItemStack stack,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      AbstractClientPlayer player,
      HumanoidModel<?> humanoidModel,
      HumanoidArm arm
   ) {
      GlovesModel model = this.glovesFirstPerson;
      GlovesModel trimModel = this.glovesTrimFirstPerson;
      ModelPart playerArm = arm == HumanoidArm.RIGHT ? humanoidModel.rightArm : humanoidModel.leftArm;
      GlovesItem glovesItem = (GlovesItem)stack.getItem();
      VertexConsumer consumer = buffer.getBuffer(RenderType.armorCutoutNoCull(glovesItem.getGlovesTexture()));
      int color = IClientItemExtensions.of(stack).getDefaultDyeColor(stack);
      model.setAllVisible(false);
      model.attackTime = 0.0F;
      model.crouching = false;
      model.swimAmount = 0.0F;
      model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      ModelPart gloveArm = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
      gloveArm.copyFrom(playerArm);
      gloveArm.visible = true;
      boolean isSlim = player.getSkin().model() == Model.SLIM;
      boolean flag = arm != HumanoidArm.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float offset = isSlim ? 0.0425F : 0.0F;
      poseStack.translate(f * offset - 0.0025, 0.0025, -0.0025);
      gloveArm.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
      ArmorTrim trim = (ArmorTrim)stack.get(DataComponents.TRIM);
      if (trim != null) {
         trimModel.setAllVisible(false);
         trimModel.attackTime = 0.0F;
         trimModel.crouching = false;
         trimModel.swimAmount = 0.0F;
         trimModel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
         ModelPart gloveTrimArm = arm == HumanoidArm.RIGHT ? trimModel.rightArm : trimModel.leftArm;
         gloveTrimArm.copyFrom(playerArm);
         gloveTrimArm.visible = true;
         TextureAtlasSprite textureAtlasSprite = this.armorTrimAtlas.getSprite(trim.outerTexture(glovesItem.getMaterial()));
         VertexConsumer trimConsumer = textureAtlasSprite.wrap(buffer.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)trim.pattern().value()).decal())));
         gloveTrimArm.render(poseStack, trimConsumer, packedLight, OverlayTexture.NO_OVERLAY);
      }

      if (stack.hasFoil()) {
         gloveArm.render(poseStack, buffer.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
      }
   }
}
