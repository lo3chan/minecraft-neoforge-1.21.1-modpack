/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import java.util.Set;
import net.diebuddies.physics.CubeExtension;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.Cube.class})
public class MixinCube
implements CubeExtension {
    @Unique
    private boolean physicsMirror;

    @Inject(at={@At(value="TAIL")}, method={"<init>"})
    public void constructor(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> set, CallbackInfo info) {
        this.physicsMirror = mirror;
    }

    @Override
    public boolean isMirrored() {
        return this.physicsMirror;
    }
}

