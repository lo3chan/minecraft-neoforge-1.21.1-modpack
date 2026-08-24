package tallestegg.guardvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.fml.ModList;
import tallestegg.guardvillagers.ModCompat;
import tallestegg.guardvillagers.client.GuardClientEvents;
import tallestegg.guardvillagers.client.models.GuardArmorModel;
import tallestegg.guardvillagers.client.models.GuardModel;
import tallestegg.guardvillagers.client.models.GuardSteveModel;
import tallestegg.guardvillagers.common.entities.Guard;
import tallestegg.guardvillagers.configuration.GuardConfig;

public class GuardRenderer extends HumanoidMobRenderer<Guard, HumanoidModel<Guard>> {
   private final HumanoidModel<Guard> steve;
   private final HumanoidModel<Guard> normal = (HumanoidModel<Guard>)this.getModel();
   private static final ResourceLocation guardTextures = ResourceLocation.fromNamespaceAndPath(
      "guardvillagers", "textures/entity/guard/guard" + (GuardConfig.CLIENT.GuardSteve.get() ? "_steve" : "") + ".png"
   );

   public GuardRenderer(Context context) {
      super(context, new GuardModel(context.bakeLayer(GuardClientEvents.GUARD)), 0.5F);
      this.steve = new GuardSteveModel(context.bakeLayer(GuardClientEvents.GUARD_STEVE));
      if ((Boolean)GuardConfig.CLIENT.GuardSteve.get()) {
         this.model = this.steve;
      } else {
         this.model = this.normal;
      }

      this.addLayer(new GuardRenderer.GuardVariantLayer(this));
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            (HumanoidModel)(!GuardConfig.CLIENT.GuardSteve.get()
               ? new GuardArmorModel(context.bakeLayer(GuardClientEvents.GUARD_ARMOR_INNER))
               : new HumanoidArmorModel(context.bakeLayer(GuardClientEvents.GUARD_PLAYER_ARMOR_INNER))),
            (HumanoidModel)(!GuardConfig.CLIENT.GuardSteve.get()
               ? new GuardArmorModel(context.bakeLayer(GuardClientEvents.GUARD_ARMOR_OUTER))
               : new HumanoidArmorModel(context.bakeLayer(GuardClientEvents.GUARD_PLAYER_ARMOR_OUTER))),
            context.getModelManager()
         )
      );
   }

   public void render(Guard entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      this.setModelVisibilities(entityIn);
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private void setModelVisibilities(Guard entityIn) {
      HumanoidModel<Guard> guardmodel = (HumanoidModel<Guard>)this.getModel();
      ItemStack itemstack = entityIn.getMainHandItem();
      ItemStack itemstack1 = entityIn.getOffhandItem();
      guardmodel.setAllVisible(true);
      ArmPose bipedmodel$armpose = this.getArmPose(entityIn, itemstack, itemstack1, InteractionHand.MAIN_HAND);
      ArmPose bipedmodel$armpose1 = this.getArmPose(entityIn, itemstack, itemstack1, InteractionHand.OFF_HAND);
      guardmodel.crouching = entityIn.isCrouching();
      if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
         guardmodel.rightArmPose = bipedmodel$armpose;
         guardmodel.leftArmPose = bipedmodel$armpose1;
      } else {
         guardmodel.rightArmPose = bipedmodel$armpose1;
         guardmodel.leftArmPose = bipedmodel$armpose;
      }
   }

   private ArmPose getArmPose(Guard entityIn, ItemStack itemStackMain, ItemStack itemStackOff, InteractionHand handIn) {
      ArmPose bipedmodel$armpose = ArmPose.EMPTY;
      ItemStack itemstack = handIn == InteractionHand.MAIN_HAND ? itemStackMain : itemStackOff;
      if (!itemstack.isEmpty()) {
         bipedmodel$armpose = ArmPose.ITEM;
         if (entityIn.getUseItemRemainingTicks() > 0) {
            UseAnim useaction = itemstack.getUseAnimation();

            bipedmodel$armpose = switch (useaction) {
               case BLOCK -> ArmPose.BLOCK;
               case BOW -> ArmPose.BOW_AND_ARROW;
               case SPEAR -> ArmPose.THROW_SPEAR;
               case CROSSBOW -> {
                  if (handIn == entityIn.getUsedItemHand()) {
                     yield ArmPose.CROSSBOW_CHARGE;
                  }
               }
               default -> ArmPose.EMPTY;
            };

            if (ModList.get().isLoaded("musketmod")) {
               bipedmodel$armpose = ModCompat.reloadMusketAnim(itemstack, handIn, entityIn, bipedmodel$armpose);
            }
         } else {
            if (ModList.get().isLoaded("musketmod")) {
               bipedmodel$armpose = ModCompat.holdMusketAnim(itemstack, entityIn);
            }

            boolean flag1 = itemStackMain.getItem() instanceof CrossbowItem;
            boolean flag2 = itemStackOff.getItem() instanceof CrossbowItem;
            if (flag1 && entityIn.isAggressive()) {
               bipedmodel$armpose = ArmPose.CROSSBOW_HOLD;
            }

            if (flag2 && itemStackMain.getItem().getUseAnimation(itemStackMain) == UseAnim.NONE && entityIn.isAggressive()) {
               bipedmodel$armpose = ArmPose.CROSSBOW_HOLD;
            }
         }
      }

      return bipedmodel$armpose;
   }

   protected void scale(Guard entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
   }

   @Nullable
   public ResourceLocation getTextureLocation(Guard entity) {
      return guardTextures;
   }

   public static class GuardVariantLayer extends RenderLayer<Guard, HumanoidModel<Guard>> {
      public GuardVariantLayer(RenderLayerParent renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferSource,
         int packedLight,
         Guard livingEntity,
         float limbSwing,
         float limbSwingAmount,
         float partialTick,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (!livingEntity.isInvisible()) {
            EntityModel m = this.getParentModel();
            String guardSteve = GuardConfig.CLIENT.GuardSteve.get() ? "_steve" : "";
            ResourceLocation resourcelocation = ResourceLocation.fromNamespaceAndPath(
               "guardvillagers", "textures/entity/guard/guard_variants/guard" + guardSteve + "_" + livingEntity.getVariant() + ".png"
            );
            AbstractTexture abstracttexture = Minecraft.getInstance().getTextureManager().getTexture(resourcelocation);
            if (abstracttexture == MissingTextureAtlasSprite.getTexture()) {
               resourcelocation = ResourceLocation.fromNamespaceAndPath(
                  "guardvillagers", "textures/entity/guard/guard_variants/guard" + guardSteve + "_plains.png"
               );
            }

            renderColoredCutoutModel(m, resourcelocation, poseStack, bufferSource, packedLight, livingEntity, -1);
         }
      }
   }
}
