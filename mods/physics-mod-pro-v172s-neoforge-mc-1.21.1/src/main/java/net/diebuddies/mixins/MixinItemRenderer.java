/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
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
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public class MixinItemRenderer {
    @Inject(at={@At(value="HEAD")}, method={"renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"})
    private void renderStaticHead(@Nullable LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, @Nullable Level level, int i, int j, int k, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify) {
            PhysicsMod.getCurrentInstance().itemStackEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
            PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh = new Mesh();
            if (multiBufferSource instanceof DummyMultiBufferSource) {
                ((DummyMultiBufferSource)multiBufferSource).trackVertices(true);
            }
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"})
    private void renderStaticTail(@Nullable LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, @Nullable Level level, int i, int j, int k, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify) {
            if (multiBufferSource instanceof DummyMultiBufferSource) {
                ((DummyMultiBufferSource)multiBufferSource).trackVertices(false);
            }
            if (PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh.indices.size() < 9) {
                return;
            }
            PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh.calculateOffset();
            PhysicsMod.getCurrentInstance().itemStackEntity.feature = PhysicsMod.getCurrentInstance().blockifyFeature;
            PhysicsMod.getCurrentInstance().itemStackEntity.getTransformation().translate((Vector3fc)PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh.offset);
            PhysicsMod.getCurrentInstance().itemStackEntity.getOldTransformation().translate((Vector3fc)PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh.offset);
            PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh.offset.zero();
            PhysicsMod.getCurrentInstance().blockifiedEntity.add(PhysicsMod.getCurrentInstance().itemStackEntity);
        }
    }
}

