/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.vertex.format.common.GlyphVertex
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.sodium;

import net.caffeinemc.mods.sodium.api.vertex.format.common.GlyphVertex;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value={GlyphVertex.class})
public class MixinGlyph {
    @Unique
    private static Vector3f physicsmod$tmp1 = new Vector3f();
    @Unique
    private static Vector3f physicsmod$tmp2 = new Vector3f();

    @Inject(at={@At(value="HEAD")}, method={"put"}, remap=false)
    private static void physicsmod$catchRenderingForBlockEntities(long ptr, float x, float y, float z, int color, float u, float v, int light, CallbackInfo info) {
        if (PhysicsMod.sodiumCatch) {
            Model model = BlockEntityVertexConsumerProvider.currentConsumer.getModel();
            if (model == null) {
                return;
            }
            model.textureID = TextureHelper.getLoadedTextures();
            Mesh mesh = model.mesh;
            mesh.positions.add(new Vector3f(x, y, z));
            mesh.colors.add(color);
            mesh.uvs.add(new Vector2f(u, v));
            if ((mesh.positions.size() & 3) == 0) {
                int posSize = mesh.positions.size();
                if (posSize != mesh.normals.size()) {
                    Vector3f tmp1;
                    Vector3f pos0 = mesh.positions.get(posSize - 3);
                    Vector3f pos1 = mesh.positions.get(posSize - 2);
                    Vector3f pos2 = mesh.positions.get(posSize - 1);
                    Vector3f tmp0 = pos1.sub((Vector3fc)pos0, physicsmod$tmp1);
                    Vector3f normal = tmp0.cross((Vector3fc)(tmp1 = pos2.sub((Vector3fc)pos0, physicsmod$tmp2)));
                    float length = normal.lengthSquared();
                    if ((double)length != 0.0) {
                        normal.mul(1.0f / length);
                    } else {
                        normal.set(0.0, 1.0, 0.0);
                    }
                    for (int i = 0; i < 4; ++i) {
                        mesh.normals.add(new Vector3f(normal.x, normal.y, normal.z));
                    }
                }
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
}

