package at.petrak.hexcasting.client.model;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.player.AltioraAbility;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AltioraLayer<M extends EntityModel<AbstractClientPlayer>> extends RenderLayer<AbstractClientPlayer, M> {
   private static final ResourceLocation TEX_LOC = HexAPI.modLoc("textures/misc/altiora.png");
   private final ElytraModel<AbstractClientPlayer> elytraModel;

   public AltioraLayer(RenderLayerParent<AbstractClientPlayer, M> renderer, EntityModelSet ems) {
      super(renderer);
      this.elytraModel = new ElytraModel(ems.bakeLayer(HexModelLayers.ALTIORA));
   }

   public void render(
      PoseStack ps,
      MultiBufferSource buffer,
      int packedLight,
      AbstractClientPlayer player,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      AltioraAbility altiora = IXplatAbstractions.INSTANCE.getAltiora(player);
      ItemStack chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
      if (altiora != null && !chestSlot.is(Items.ELYTRA)) {
         ps.pushPose();
         ps.translate(0.0, 0.0, 0.125);
         this.getParentModel().copyPropertiesTo(this.elytraModel);
         this.elytraModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         VertexConsumer verts = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(TEX_LOC), false);
         this.elytraModel.renderToBuffer(ps, verts, packedLight, OverlayTexture.NO_OVERLAY);
         ps.popPose();
      }
   }
}
