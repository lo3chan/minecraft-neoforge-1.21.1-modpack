/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package net.diebuddies.physics.vines;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.minecraft.core.BlockPos;

public interface DynamicLoader {
    public void unloadAllRagdolls();

    public void loadAllRagdolls();

    public void unloadAllSnow();

    public void loadAllSnow();

    public void unloadAllOcean();

    public void loadAllOcean();

    public void addVineRagdoll(DynamicRagdoll var1, BlockPos var2);

    public void removeVineRagdoll(DynamicRagdoll var1);

    public void chunkPosChanged();

    public void setPhysicsMod(PhysicsMod var1);
}

