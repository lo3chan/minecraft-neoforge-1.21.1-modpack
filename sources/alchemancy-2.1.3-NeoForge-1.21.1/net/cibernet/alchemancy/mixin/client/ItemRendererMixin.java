package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.DisguisedProperty;
import net.cibernet.alchemancy.properties.ResizedProperty;
import net.cibernet.alchemancy.properties.RotationDataProperty;
import net.cibernet.alchemancy.properties.WayfindingProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.WayfindingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererMixin {
   @Unique
   private LivingEntity alchemancy$livingEntity = null;

   @Shadow
   public abstract BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3, int var4);

   @Inject(
      method = {"renderQuadList"},
      at = {@At(
         value = "JUMP",
         shift = Shift.AFTER
      )}
   )
   public void modifyColorNoTint(
      PoseStack poseStack,
      VertexConsumer buffer,
      List<BakedQuad> quads,
      ItemStack itemStack,
      int combinedLight,
      int combinedOverlay,
      CallbackInfo ci,
      @Local(ordinal = 2) LocalIntRef localTint,
      @Local BakedQuad quad
   ) {
      if (!quad.isTinted()) {
         CommonUtils.modifyTint(itemStack, quad.getTintIndex(), localTint);
      }
   }

   @Inject(
      method = {"renderQuadList"},
      at = {@At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I",
         shift = Shift.AFTER
      )}
   )
   public void modifyColorWithTint(
      PoseStack poseStack,
      VertexConsumer buffer,
      List<BakedQuad> quads,
      ItemStack itemStack,
      int combinedLight,
      int combinedOverlay,
      CallbackInfo ci,
      @Local(ordinal = 2) LocalIntRef localTint,
      @Local BakedQuad quad
   ) {
      if (quad.isTinted()) {
         CommonUtils.modifyTint(itemStack, quad.getTintIndex(), localTint);
      }
   }

   @Inject(
      method = {"renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
      )}
   )
   public void renderStatic(
      LivingEntity entity,
      ItemStack itemStack,
      ItemDisplayContext diplayContext,
      boolean leftHand,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      Level level,
      int combinedLight,
      int combinedOverlay,
      int seed,
      CallbackInfo ci
   ) {
      this.alchemancy$livingEntity = entity;
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      boolean leftHand,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int combinedLight,
      int combinedOverlay,
      BakedModel p_model,
      CallbackInfo ci,
      @Local(argsOnly = true) LocalRef<BakedModel> localModel,
      @Local(ordinal = 0,argsOnly = true) LocalIntRef localLight,
      @Local(ordinal = 0,argsOnly = true) LocalRef<ItemStack> localStack
   ) {
      if (displayContext != ItemDisplayContext.GUI && InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.CONCEALED)) {
         ci.cancel();
      } else {
         if (InfusedPropertiesHelper.hasInfusedProperty(itemStack, AlchemancyProperties.DISGUISED)) {
            ItemStack disguise = ((DisguisedProperty)AlchemancyProperties.DISGUISED.get()).getData(itemStack);
            LivingEntity owner = (LivingEntity)(displayContext == ItemDisplayContext.GUI ? Minecraft.getInstance().player : this.alchemancy$livingEntity);
            if (!disguise.isEmpty()) {
               localStack.set(disguise);
               localModel.set(
                  this.getModel(disguise, (Level)(owner == null ? Minecraft.getInstance().level : owner.level()), owner, owner == null ? 0 : owner.getId())
               );
            }
         }

         if (InfusedPropertiesHelper.hasInfusedProperty(itemStack, AlchemancyProperties.GLOWING_AURA)) {
            localLight.set(15728880);
         }
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/client/ClientHooks;handleCameraTransforms(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemDisplayContext;Z)Lnet/minecraft/client/resources/model/BakedModel;"
      )}
   )
   public void renderScale(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      boolean leftHand,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int combinedLight,
      int combinedOverlay,
      BakedModel p_model,
      CallbackInfo ci
   ) {
      if (InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.FLATTENED)) {
         switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
               poseStack.scale(0.05F, 1.0F, 1.0F);
               break;
            default:
               poseStack.scale(1.0F, 1.0F, 0.05F);
         }
      }

      Level level = Minecraft.getInstance().level;
      Entity user = (Entity)(this.alchemancy$livingEntity == null ? Minecraft.getInstance().player : this.alchemancy$livingEntity);
      boolean canWayfind = true;
      if (InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.DEATH_TRACKER)
         && !InfusedPropertiesHelper.hasInnateProperty(itemStack, AlchemancyProperties.WAYWARD_WARP)
         && user instanceof Player player
         && level != null) {
         Optional<GlobalPos> lastDeath = player.getLastDeathLocation();
         if (lastDeath.isPresent()) {
            WayfindingProperty.RotationData data = ((RotationDataProperty)AlchemancyProperties.DEATH_TRACKER.value()).getData(itemStack);
            boolean updatePrev = data.shouldUpdate(user.level().getGameTime());
            float rotation = lastDeath.get().dimension().equals(level.dimension())
               ? WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), lastDeath.get().pos())
               : WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
            float prevRotation = data.previousRotaion();
            float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            if (updatePrev) {
               prevRotation = data.rotation();
               ((RotationDataProperty)AlchemancyProperties.DEATH_TRACKER.value())
                  .setData(
                     itemStack,
                     ((RotationDataProperty)AlchemancyProperties.DEATH_TRACKER.value()).getData(itemStack).step(rotation, user.level().getGameTime())
                  );
            }

            float angle = 360.0F * CommonUtils.lerpAngle(partialTick, prevRotation, rotation);
            switch (displayContext) {
               case FIRST_PERSON_RIGHT_HAND:
               case THIRD_PERSON_RIGHT_HAND:
               case FIRST_PERSON_LEFT_HAND:
               case THIRD_PERSON_LEFT_HAND:
                  poseStack.mulPose(Axis.YN.rotationDegrees(angle + 195.0F));
                  break;
               case HEAD:
                  poseStack.mulPose(Axis.YN.rotationDegrees(angle));
                  break;
               default:
                  poseStack.mulPose(Axis.ZN.rotationDegrees(angle - 45.0F));
            }

            canWayfind = false;
         }
      }

      if (canWayfind && InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.WAYFINDING) && user != null && level != null) {
         Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> data = ((WayfindingProperty)AlchemancyProperties.WAYFINDING.value())
            .getData(itemStack);
         boolean updatePrev = ((WayfindingProperty.RotationData)data.getB()).shouldUpdate(user.level().getGameTime());
         float rotation = ((WayfindingProperty.WayfindData)data.getA()).getRotation(user);
         float prevRotation = ((WayfindingProperty.RotationData)data.getB()).previousRotaion();
         float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         if (updatePrev) {
            prevRotation = ((WayfindingProperty.RotationData)data.getB()).rotation();
            ((WayfindingProperty)AlchemancyProperties.WAYFINDING.value())
               .setData(
                  itemStack,
                  ((WayfindingProperty.RotationData)((WayfindingProperty)AlchemancyProperties.WAYFINDING.value()).getData(itemStack).getB())
                     .step(rotation, user.level().getGameTime())
               );
         }

         float angle = 360.0F * CommonUtils.lerpAngle(partialTick, prevRotation, rotation);
         switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case THIRD_PERSON_LEFT_HAND:
               poseStack.mulPose(Axis.YN.rotationDegrees(angle + 195.0F));
               break;
            case HEAD:
               poseStack.mulPose(Axis.YN.rotationDegrees(angle));
               break;
            default:
               poseStack.mulPose(Axis.ZN.rotationDegrees(angle - 45.0F));
         }
      }

      if (InfusedPropertiesHelper.hasInfusedProperty(itemStack, AlchemancyProperties.ROTATING)) {
         float time = PropertyModifierComponent.getOrElse(itemStack, AlchemancyProperties.ROTATING, AlchemancyProperties.Modifiers.ROTATION, 5.0F);
         float partialSecond = (float)(System.currentTimeMillis() % (1000L * (long)time)) / 1000.0F;
         float angle = partialSecond / time * 360.0F;
         switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case THIRD_PERSON_LEFT_HAND:
            case HEAD:
               poseStack.mulPose(Axis.YN.rotationDegrees(angle));
               break;
            default:
               poseStack.mulPose(Axis.ZN.rotationDegrees(angle));
         }
      }

      if (InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.RESIZED)) {
         float size = ((ResizedProperty)AlchemancyProperties.RESIZED.get()).getData(itemStack);
         poseStack.scale(size, size, size);
      }

      this.alchemancy$livingEntity = null;
   }
}
