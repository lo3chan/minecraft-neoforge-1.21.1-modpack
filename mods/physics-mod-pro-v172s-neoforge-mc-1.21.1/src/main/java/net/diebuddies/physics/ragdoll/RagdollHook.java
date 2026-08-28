/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.world.entity.Entity
 */
package net.diebuddies.physics.ragdoll;

import java.util.List;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public interface RagdollHook {
    public void map(Ragdoll var1, Entity var2, EntityModel var3);

    public void filterCuboidsFromEntities(List<PhysicsEntity> var1, Entity var2, EntityModel var3);
}

