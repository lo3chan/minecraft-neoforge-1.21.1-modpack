/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.sodium;

import java.nio.ByteOrder;
import net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value={EntityRenderer.class})
public class MixinEntityRenderer8 {
    @Shadow(remap=false)
    @Final
    private static long[] CUBE_VERTEX_XY;
    @Shadow(remap=false)
    @Final
    private static long[] CUBE_VERTEX_ZW;
    @Unique
    private static final boolean LITTLE;

    @Inject(at={@At(value="HEAD")}, method={"writeVertex"}, remap=false)
    private static void physicsmod$catchRenderingForBlockEntities(long ptr, int vertexIndex, long packedUv, long packedOverlayLight, int normal, CallbackInfoReturnable<Long> info) {
        float z;
        float y;
        float x;
        if (PhysicsMod.sodiumCatchBoundingBox) {
            x = Float.intBitsToFloat(MixinEntityRenderer8.unpackA(CUBE_VERTEX_XY[vertexIndex]));
            y = Float.intBitsToFloat(MixinEntityRenderer8.unpackB(CUBE_VERTEX_XY[vertexIndex]));
            z = Float.intBitsToFloat(MixinEntityRenderer8.unpackA(CUBE_VERTEX_ZW[vertexIndex]));
            Vector3d start = PhysicsMod.sodiumBoundingBox.start;
            Vector3d end = PhysicsMod.sodiumBoundingBox.end;
            if ((double)x < start.x) {
                start.x = x;
            }
            if ((double)y < start.y) {
                start.y = y;
            }
            if ((double)z < start.z) {
                start.z = z;
            }
            if ((double)x > end.x) {
                end.x = x;
            }
            if ((double)y > end.y) {
                end.y = y;
            }
            if ((double)z > end.z) {
                end.z = z;
            }
        }
        if (PhysicsMod.sodiumCatch) {
            x = Float.intBitsToFloat(MixinEntityRenderer8.unpackA(CUBE_VERTEX_XY[vertexIndex]));
            y = Float.intBitsToFloat(MixinEntityRenderer8.unpackB(CUBE_VERTEX_XY[vertexIndex]));
            z = Float.intBitsToFloat(MixinEntityRenderer8.unpackA(CUBE_VERTEX_ZW[vertexIndex]));
            int color = MixinEntityRenderer8.unpackB(CUBE_VERTEX_ZW[vertexIndex]);
            float u = Float.intBitsToFloat(MixinEntityRenderer8.unpackA(packedUv));
            float v = Float.intBitsToFloat(MixinEntityRenderer8.unpackB(packedUv));
            Model model = BlockEntityVertexConsumerProvider.currentConsumer.getModel();
            if (model == null) {
                return;
            }
            model.textureID = TextureHelper.getLoadedTextures();
            Mesh mesh = model.mesh;
            mesh.positions.add(new Vector3f(x, y, z));
            float normRange = 0.007874016f;
            float normX = (float)((byte)(normal & 0xFF)) * normRange;
            float normY = (float)((byte)(normal >> 8 & 0xFF)) * normRange;
            float normZ = (float)((byte)(normal >> 16 & 0xFF)) * normRange;
            mesh.colors.add(color);
            mesh.normals.add(new Vector3f(normX, normY, normZ));
            mesh.uvs.add(new Vector2f(u, v));
            if ((mesh.positions.size() & 3) == 0) {
                int index = mesh.positions.size() - 4;
                mesh.indices.add(index);
                mesh.indices.add(index + 1);
                mesh.indices.add(index + 2);
                mesh.indices.add(index);
                mesh.indices.add(index + 2);
                mesh.indices.add(index + 3);
            }
        }
    }

    @Unique
    private static int unpackA(long packed) {
        return LITTLE ? (int)(packed & 0xFFFFFFFFL) : (int)(packed >>> 32 & 0xFFFFFFFFL);
    }

    @Unique
    private static int unpackB(long packed) {
        return LITTLE ? (int)(packed >>> 32 & 0xFFFFFFFFL) : (int)(packed & 0xFFFFFFFFL);
    }

    static {
        LITTLE = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    }
}

