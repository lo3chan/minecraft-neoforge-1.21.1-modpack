/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.vines.Adjustable;
import net.diebuddies.physics.vines.BlockFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class DynamicSetting
implements BlockFilter {
    @Adjustable(id="Linked Physics", translationId="physicsmod.prop.dynamicsetting.linkedphysics")
    public boolean linkedPhysics;

    public abstract DynamicRagdoll createRagdoll(PhysicsMod var1, BlockState var2, BlockPos var3, Long2ObjectMap<BlockState> var4);

    @Override
    public abstract boolean isValid(BlockState var1);

    public abstract Block defaultBlock();

    protected PhysicsEntity createPart(PhysicsMod mod, DynamicRagdoll ragdoll, BlockState state, int baseY, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        PhysicsEntity entity = mod.renderBlockIntoEntity(mod.getPhysicsWorld().getLevel(), PhysicsEntity.Type.VINE, state, pos, true);
        if (entity == null) {
            entity = new PhysicsEntity(PhysicsEntity.Type.VINE, state);
            entity.getTransformation().set((Matrix4dc)new Matrix4d().translate((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
            entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
            entity.models.get((int)0).mesh = new Mesh();
            entity.models.get((int)0).mesh.offset = new Vector3f();
        }
        Vector3f offset = entity.models.get((int)0).mesh.offset;
        entity.enlargeHitbox.set((Vector3fc)ragdoll.hitboxScale);
        int baseOffset = y - baseY;
        offset.y += (float)baseOffset;
        entity.getTransformation().translate(0.0, (double)(-baseOffset), 0.0);
        entity.getOldTransformation().translate(0.0, (double)(-baseOffset), 0.0);
        entity.pivot.set((double)offset.x, (double)baseOffset + 1.0, (double)offset.z);
        ragdoll.bodies.add(entity);
        ragdoll.getBlockPositions().add(pos);
        ragdoll.getBlockStates().add(state);
        return entity;
    }
}

