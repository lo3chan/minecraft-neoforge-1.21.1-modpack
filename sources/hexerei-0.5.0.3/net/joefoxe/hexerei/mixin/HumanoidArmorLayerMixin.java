package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.HexereiSupporterBenefits;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(
   value = {HumanoidArmorLayer.class},
   priority = 999
)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   @Final
   @Shadow
   private TextureAtlas armorTrimAtlas;

   @OnlyIn(Dist.CLIENT)
   public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer, A innerModel, A outerModel, ModelManager modelManager) {
      super(renderer);
   }

   @OnlyIn(Dist.CLIENT)
   @Inject(
      at = {@At("HEAD")},
      method = {"renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V"},
      cancellable = true
   )
   public void hexerei$renderArmorPiece(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T livingEntity,
      EquipmentSlot slot,
      int packedLight,
      A p_model,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      CallbackInfo ci
   ) {
      ItemStack itemstack = livingEntity.getItemBySlot(slot);
      if (itemstack.getItem() instanceof WitchArmorItem armoritem && armoritem.getEquipmentSlot() == slot) {
         ((HumanoidModel)this.getParentModel()).copyPropertiesTo(p_model);
         this.hexerei$setPartVisibility(p_model, slot);
         Model model = this.getArmorModelHook(livingEntity, itemstack, slot, p_model);
         boolean flag = false;
         ArmorMaterial armormaterial = (ArmorMaterial)armoritem.getMaterial().value();
         IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
         extensions.setupModelAnimations(livingEntity, itemstack, slot, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
         int fallbackColor = armoritem.getColor(itemstack);

         for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
            Layer armormaterial$layer = (Layer)armormaterial.layers().get(layerIdx);
            int j = extensions.getArmorLayerTintColor(itemstack, livingEntity, armormaterial$layer, layerIdx, fallbackColor);
            float[] colors = HexereiUtil.rgbIntToFloatArray(j);
            if (j != 0 && (layerIdx == 0 || j != -1)) {
               if (armormaterial$layer.dyeable() && livingEntity instanceof Player player && HexereiSupporterBenefits.matchesSupporterUUID(player.getUUID())) {
                  packedLight = 15728880;
               }

               ResourceLocation texture = ClientHooks.getArmorTexture(livingEntity, itemstack, armormaterial$layer, flag, slot);
               this.hexerei$renderModel(poseStack, bufferSource, packedLight, false, model, colors[0], colors[1], colors[2], texture);
            }
         }

         ArmorTrim armortrim = (ArmorTrim)itemstack.get(DataComponents.TRIM);
         if (armortrim != null) {
            this.hexerei$renderTrim(armoritem.getMaterial(), poseStack, bufferSource, packedLight, armortrim, model, flag);
         }

         if (itemstack.hasFoil()) {
            this.hexerei$renderGlint(poseStack, bufferSource, packedLight, model);
         }

         ci.cancel();
      }
   }

   @Unique
   private void hexerei$renderTrim(
      Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, Model p_289663_, boolean p_289651_
   ) {
      TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(p_289651_ ? p_289692_.innerTexture(p_323506_) : p_289692_.outerTexture(p_323506_));
      VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)p_289692_.pattern().value()).decal())));
      p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY);
   }

   @Unique
   private void hexerei$renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_) {
      p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
   }

   protected Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
      return ClientHooks.getArmorModel(entity, itemStack, slot, model);
   }

   @Unique
   private void hexerei$renderModel(
      PoseStack p_117107_,
      MultiBufferSource p_117108_,
      int p_117109_,
      boolean p_117111_,
      Model p_117112_,
      float p_117114_,
      float p_117115_,
      float p_117116_,
      ResourceLocation armorResource
   ) {
      VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), p_117111_);
      p_117112_.renderToBuffer(
         p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(p_117114_, p_117115_, p_117116_, 1.0F)
      );
   }

   @Unique
   protected void hexerei$setPartVisibility(A p_117126_, EquipmentSlot p_117127_) {
      p_117126_.setAllVisible(false);
      switch (p_117127_) {
         case HEAD:
            p_117126_.head.visible = true;
            p_117126_.hat.visible = true;
            break;
         case CHEST:
            p_117126_.body.visible = true;
            p_117126_.rightArm.visible = true;
            p_117126_.leftArm.visible = true;
            break;
         case LEGS:
            p_117126_.body.visible = true;
            p_117126_.rightLeg.visible = true;
            p_117126_.leftLeg.visible = true;
            break;
         case FEET:
            p_117126_.rightLeg.visible = true;
            p_117126_.leftLeg.visible = true;
      }
   }
}
