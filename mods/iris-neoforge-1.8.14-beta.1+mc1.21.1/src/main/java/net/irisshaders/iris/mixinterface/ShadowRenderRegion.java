/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.mixinterface;

public interface ShadowRenderRegion {
    public void swapToRegularRenderList();

    public void swapToShadowRenderList();

    public void iris$forceClearAllBatches();
}

