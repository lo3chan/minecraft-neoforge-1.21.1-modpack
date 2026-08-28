/*
 * Decompiled with CFR 0.152.
 */
package dev.tr7zw.entityculling.versionless.access;

public interface Cullable {
    public void setTimeout();

    public boolean isForcedVisible();

    public void setCulled(boolean var1);

    public boolean isCulled();

    public void setOutOfCamera(boolean var1);

    public boolean isOutOfCamera();
}

