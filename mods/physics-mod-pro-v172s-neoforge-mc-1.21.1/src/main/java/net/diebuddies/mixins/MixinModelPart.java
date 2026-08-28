/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  net.minecraft.util.FastColor$ARGB32
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.class}, priority=500)
public class MixinModelPart {
    @Shadow
    @Final
    public List<ModelPart.Cube> cubes;

    @Inject(at={@At(value="HEAD")}, method={"compile"}, cancellable=true)
    private void renderCuboids(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
            ((ModelPart)this).translateAndRotate(PhysicsMod.getCurrentInstance().localPivotMatrix);
            PhysicsMod.createParticlesFromCuboids(matrices, PhysicsMod.getCurrentInstance().localPivotMatrix, this.cubes, PhysicsMod.getCurrentInstance().cubifyEntity, PhysicsMod.getCurrentInstance().cubifyEntityRenderer, PhysicsMod.getCurrentInstance().blockifyFeature, overlay, (float)FastColor.ARGB32.red((int)color) / 255.0f, (float)FastColor.ARGB32.green((int)color) / 255.0f, (float)FastColor.ARGB32.blue((int)color) / 255.0f);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"})
    public void renderHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
            PhysicsMod.getCurrentInstance().localPivotMatrix.pushPose();
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"})
    public void renderTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
            PhysicsMod.getCurrentInstance().localPivotMatrix.popPose();
        }
    }
}

