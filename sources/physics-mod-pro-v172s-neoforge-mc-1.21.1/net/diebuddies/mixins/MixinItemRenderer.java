package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.DummyMultiBufferSource;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"}
   )
   private void renderStaticHead(
      @Nullable LivingEntity livingEntity,
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      boolean bl,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      @Nullable Level level,
      int i,
      int j,
      int k,
      CallbackInfo ci
   ) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify) {
         PhysicsMod.getCurrentInstance().itemStackEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
         PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh = new Mesh();
         if (multiBufferSource instanceof DummyMultiBufferSource) {
            ((DummyMultiBufferSource)multiBufferSource).trackVertices(true);
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"}
   )
   private void renderStaticTail(
      @Nullable LivingEntity livingEntity,
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      boolean bl,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      @Nullable Level level,
      int i,
      int j,
      int k,
      CallbackInfo ci
   ) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify) {
         if (multiBufferSource instanceof DummyMultiBufferSource) {
            ((DummyMultiBufferSource)multiBufferSource).trackVertices(false);
         }

         if (PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh.indices.size() < 9) {
            return;
         }

         PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh.calculateOffset();
         PhysicsMod.getCurrentInstance().itemStackEntity.feature = PhysicsMod.getCurrentInstance().blockifyFeature;
         PhysicsMod.getCurrentInstance()
            .itemStackEntity
            .getTransformation()
            .translate(PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh.offset);
         PhysicsMod.getCurrentInstance()
            .itemStackEntity
            .getOldTransformation()
            .translate(PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh.offset);
         PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh.offset.zero();
         PhysicsMod.getCurrentInstance().blockifiedEntity.add(PhysicsMod.getCurrentInstance().itemStackEntity);
      }
   }
}
