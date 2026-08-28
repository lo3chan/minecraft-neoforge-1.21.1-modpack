/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4fStack
 */
package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import org.joml.Matrix4fStack;

public interface VerletConstraint {
    default public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        return false;
    }

    default public void updateBefore(double delta, VerletSimulation simulation) {
    }

    default public void updateAfter(double delta, VerletSimulation simulation) {
    }

    default public void preSubStep(double percent, VerletSimulation simulation) {
    }

    default public void subStep(double percent, VerletSimulation simulation) {
    }

    default public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    default public void render(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    default public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }
}

