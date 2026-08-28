/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.Team
 */
package traben.entity_texture_features.features.state;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ETFEntityRenderStateViaReference
implements ETFEntityRenderState {
    private final ETFEntity entity;

    public ETFEntityRenderStateViaReference(ETFEntity entity) {
        this.entity = entity;
    }

    @Override
    @Deprecated
    public ETFEntity entity() {
        return this.entity;
    }

    @Override
    public UUID uuid() {
        return this.entity.etf$getUuid();
    }

    @Override
    public boolean canRenderBright() {
        return this.entity.etf$canBeBright();
    }

    @Override
    public boolean isBlockEntity() {
        return this.entity.etf$isBlockEntity();
    }

    @Override
    public EntityType<?> entityType() {
        return this.entity.etf$getType();
    }

    @Override
    public Level world() {
        return this.entity.etf$getWorld();
    }

    @Override
    public BlockPos blockPos() {
        return this.entity.etf$getBlockPos();
    }

    @Override
    public int optifineId() {
        return this.entity.etf$getOptifineId();
    }

    @Override
    public int optifineVehicleId() {
        return this.entity.etf$getOptifineVehicleId();
    }

    @Override
    public int blockY() {
        return this.entity.etf$getBlockY();
    }

    @Override
    public CompoundTag nbt() {
        return this.entity.etf$getNbt();
    }

    @Override
    public boolean hasCustomName() {
        return this.entity.etf$hasCustomName();
    }

    @Override
    public Component customName() {
        return this.entity.etf$getCustomName();
    }

    @Override
    public Team scoreboardTeam() {
        return this.entity.etf$getScoreboardTeam();
    }

    @Override
    public Iterable<ItemStack> itemsEquipped() {
        return this.entity.etf$getItemsEquipped();
    }

    @Override
    public Iterable<ItemStack> handItems() {
        return this.entity.etf$getHandItems();
    }

    @Override
    public Iterable<ItemStack> armorItems() {
        return this.entity.etf$getArmorItems();
    }

    @Override
    public Vec3 velocity() {
        return this.entity.etf$getVelocity();
    }

    @Override
    @Deprecated
    public Pose pose() {
        return this.entity.etf$getPose();
    }

    @Override
    public String entityKey() {
        return this.entity.etf$getEntityKey();
    }

    @Override
    public float distanceTo(Entity other) {
        return this.entity.etf$distanceTo(other);
    }
}

