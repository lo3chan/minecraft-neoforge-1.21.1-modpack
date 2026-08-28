/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Coerce
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class}, priority=1900)
public class MixinItemRenderer {
    @Inject(at={@At(value="HEAD")}, method={"renderModelLists"})
    private void physicsmod$grabItemBreakTransformation(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer, CallbackInfo info) {
        if (PhysicsMod.itemBreakTransformation != null) {
            PhysicsMod.itemBreakTransformation.set((Matrix4fc)poseStack.last().pose());
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"renderBakedItemQuads"}, remap=false)
    private void physicsmod$grabSodiumItemBreakTransformation(PoseStack.Pose matrices, @Coerce Object writer, List<BakedQuad> quads, ItemStack itemStack, ItemColor colorProvider, int light, int overlay, CallbackInfo info) {
        if (PhysicsMod.itemBreakTransformation != null) {
            PhysicsMod.itemBreakTransformation.set((Matrix4fc)matrices.pose());
        }
    }
}

