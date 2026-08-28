/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.geom.ModelPart
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.optifine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.class})
public class MixinModelPart {
    @Inject(at={@At(value="HEAD")}, method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFFZ)V"}, remap=false)
    public void physicsmod$optifineHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, boolean updateModel, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
            PhysicsMod.getCurrentInstance().localPivotMatrix.pushPose();
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFFZ)V"}, remap=false)
    public void physicsmod$optifineTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, boolean updateModel, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
            PhysicsMod.getCurrentInstance().localPivotMatrix.popPose();
        }
    }
}

