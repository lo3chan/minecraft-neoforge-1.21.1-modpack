/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing
 */
package dev.tr7zw.waveycapes.support;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import dev.tr7zw.waveycapes.support.SupportManager;

public class ShoulderSurfingSupport {
    public static void init() {
        SupportManager.setAlphaSupplier(() -> Float.valueOf(ShoulderSurfing.getInstance().getCameraEntityRenderer().getCameraEntityAlpha()));
    }
}

