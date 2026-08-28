/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.class})
public class MixinModelPart
implements ModelPartParent {
    @Unique
    private ModelPart physicsmod$parent;
    @Unique
    private String physicsmod$name;

    @Inject(at={@At(value="TAIL")}, method={"<init>"})
    private void physicsmod$setParentInConstructor(List<ModelPart.Cube> cubes, Map<String, ModelPart> parts, CallbackInfo info) {
        if (parts != null) {
            for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
                String name = entry.getKey();
                ModelPart part = entry.getValue();
                ((ModelPartParent)part).physicsmod$setParent((ModelPart)this);
                ((ModelPartParent)part).physicsmod$setName(name);
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"})
    public void physicsmod$checkActiveParts(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if (ClothConstants.trackParts) {
            ClothConstants.activeParts.add((Object)((ModelPart)this));
        }
    }

    @Override
    public void physicsmod$setParent(ModelPart part) {
        this.physicsmod$parent = part;
    }

    @Override
    public ModelPart physicsmod$getParent() {
        return this.physicsmod$parent;
    }

    @Override
    public void physicsmod$setName(String name) {
        this.physicsmod$name = name;
    }

    @Override
    public String physicsmod$getName() {
        return this.physicsmod$name;
    }
}

