/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import org.lwjgl.opengl.GL32C;

public class StateTracker {
    public static void bindVertexArray(int id) {
        GL32C.glBindVertexArray((int)id);
    }

    public static void unbindVertexArray() {
        StateTracker.bindVertexArray(0);
    }
}

