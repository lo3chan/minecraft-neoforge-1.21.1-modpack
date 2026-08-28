/*
 * Decompiled with CFR 0.152.
 */
package dev.tr7zw.waveycapes.versionless.sim;

import dev.tr7zw.waveycapes.versionless.util.CapePoint;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.List;

public interface BasicSimulation {
    public void simulate();

    public void setGravityDirection(Vector3 var1);

    public float getGravity();

    public void setGravity(float var1);

    public boolean isSneaking();

    public void setSneaking(boolean var1);

    public boolean init(int var1);

    public boolean empty();

    public void applyMovement(Vector3 var1);

    public List<CapePoint> getPoints();
}

