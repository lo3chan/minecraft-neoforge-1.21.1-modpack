/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.geom.ModelPart
 */
package net.diebuddies.physics.verlet;

import net.minecraft.client.model.geom.ModelPart;

public interface ModelPartParent {
    public void physicsmod$setParent(ModelPart var1);

    public ModelPart physicsmod$getParent();

    public void physicsmod$setName(String var1);

    public String physicsmod$getName();
}

