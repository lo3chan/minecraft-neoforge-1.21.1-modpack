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
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.utils;

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
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public interface ETFEntity {
    default public ETFEntityRenderState etf$getETFRenderState() {
        return ETFEntityRenderState.forEntity(this);
    }

    public boolean etf$canBeBright();

    public boolean etf$isBlockEntity();

    @Nullable
    public EntityType<?> etf$getType();

    public UUID etf$getUuid();

    public Level etf$getWorld();

    public BlockPos etf$getBlockPos();

    public int etf$getOptifineId();

    public int etf$getOptifineVehicleId();

    public int etf$getBlockY();

    public CompoundTag etf$getNbt();

    public boolean etf$hasCustomName();

    public Component etf$getCustomName();

    public Team etf$getScoreboardTeam();

    public Iterable<ItemStack> etf$getItemsEquipped();

    public Iterable<ItemStack> etf$getHandItems();

    public Iterable<ItemStack> etf$getArmorItems();

    public float etf$distanceTo(Entity var1);

    public Vec3 etf$getVelocity();

    @Deprecated
    public Pose etf$getPose();

    @Nullable
    public String etf$getEntityKey();
}

