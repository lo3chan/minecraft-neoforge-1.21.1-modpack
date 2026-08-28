/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.tr7zw.waveycapes.mixin;

import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class PlayerMixin
extends Entity
implements CapeHolder {
    @Unique
    private BasicSimulation simulation;
    @Unique
    private Vector3 lastPlayerAnimatorPosition = new Vector3();
    @Unique
    private boolean dirty = false;

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Inject(method={"tick()V"}, at={@At(value="TAIL")})
    private void moveCloakUpdate(CallbackInfo info) {
        if (!(this instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer entity = (AbstractClientPlayer)this;
        this.updateSimulation(16);
        PlayerDelegate playerDelegate = new PlayerDelegate(entity);
        if (this.dirty) {
            this.dirty = false;
            this.simulation.applyMovement(new Vector3(1.0f, 1.0f, 0.0f));
            for (int i = 0; i < 5; ++i) {
                this.simulate(playerDelegate);
            }
        }
        this.simulate(playerDelegate);
    }

    @Override
    public UUID getWCUUID() {
        return this.getUUID();
    }

    @Override
    @Generated
    public BasicSimulation getSimulation() {
        return this.simulation;
    }

    @Override
    @Generated
    public void setSimulation(BasicSimulation simulation) {
        this.simulation = simulation;
    }

    @Override
    @Generated
    public Vector3 getLastPlayerAnimatorPosition() {
        return this.lastPlayerAnimatorPosition;
    }

    @Override
    @Generated
    public void setLastPlayerAnimatorPosition(Vector3 lastPlayerAnimatorPosition) {
        this.lastPlayerAnimatorPosition = lastPlayerAnimatorPosition;
    }
}

