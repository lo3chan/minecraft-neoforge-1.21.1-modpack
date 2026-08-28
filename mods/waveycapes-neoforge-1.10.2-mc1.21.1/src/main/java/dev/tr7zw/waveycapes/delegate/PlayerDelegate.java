/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.client.player.AbstractClientPlayer
 */
package dev.tr7zw.waveycapes.delegate;

import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;

public class PlayerDelegate
implements MinecraftPlayer {
    private AbstractClientPlayer player;

    @Override
    public double getXCloak() {
        return this.player.xCloak;
    }

    @Override
    public double getZCloak() {
        return this.player.zCloak;
    }

    @Override
    public float getYBodyRotO() {
        return this.player.yBodyRotO;
    }

    @Override
    public float getYBodyRot() {
        return this.player.yBodyRot;
    }

    @Override
    public double getYo() {
        return this.player.yo;
    }

    @Override
    public double getXo() {
        return this.player.xo;
    }

    @Override
    public double getZo() {
        return this.player.zo;
    }

    @Generated
    public PlayerDelegate(AbstractClientPlayer player) {
        this.player = player;
    }

    @Generated
    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    @Override
    @Generated
    public boolean isVisuallySwimming() {
        return this.getPlayer().isVisuallySwimming();
    }

    @Override
    @Generated
    public float getXRot() {
        return this.getPlayer().getXRot();
    }

    @Override
    @Generated
    public boolean isCrouching() {
        return this.getPlayer().isCrouching();
    }

    @Override
    @Generated
    public double getY() {
        return this.getPlayer().getY();
    }

    @Override
    @Generated
    public float getYRot() {
        return this.getPlayer().getYRot();
    }

    @Override
    @Generated
    public double getZ() {
        return this.getPlayer().getZ();
    }

    @Override
    @Generated
    public double getX() {
        return this.getPlayer().getX();
    }

    @Override
    @Generated
    public boolean isUnderWater() {
        return this.getPlayer().isUnderWater();
    }
}

