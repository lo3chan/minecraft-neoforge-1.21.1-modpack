/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package net.diebuddies.physics.verlet.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.verlet.test.VerletSimulationTest;

public interface VerletTestConstraint {
    public boolean initAsyncData(VerletSimulationTest var1);

    public void updateBefore(double var1, VerletSimulationTest var3);

    public void updateAfter(double var1, VerletSimulationTest var3);

    public void subStep(double var1, VerletSimulationTest var3);

    public void renderBefore(PoseStack var1, double var2, VerletSimulationTest var4);

    public void render(PoseStack var1, double var2, VerletSimulationTest var4);

    public void renderAfter(PoseStack var1, double var2, VerletSimulationTest var4);
}

