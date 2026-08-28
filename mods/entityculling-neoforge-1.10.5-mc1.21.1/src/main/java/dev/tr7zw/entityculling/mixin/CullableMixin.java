/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.tr7zw.entityculling.mixin;

import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Entity.class, BlockEntity.class})
public class CullableMixin
implements Cullable {
    private long lasttime = 0L;
    private boolean culled = false;
    private boolean outOfCamera = false;

    @Override
    public void setTimeout() {
        this.lasttime = System.currentTimeMillis() + 1000L;
    }

    @Override
    public boolean isForcedVisible() {
        return this.lasttime > System.currentTimeMillis();
    }

    @Override
    public void setCulled(boolean value) {
        this.culled = value;
        if (!value) {
            this.setTimeout();
        }
    }

    @Override
    public boolean isCulled() {
        if (!EntityCullingVersionlessBase.enabled) {
            return false;
        }
        return this.culled;
    }

    @Override
    public void setOutOfCamera(boolean value) {
        this.outOfCamera = value;
    }

    @Override
    public boolean isOutOfCamera() {
        if (!EntityCullingVersionlessBase.enabled) {
            return false;
        }
        return this.outOfCamera;
    }
}

