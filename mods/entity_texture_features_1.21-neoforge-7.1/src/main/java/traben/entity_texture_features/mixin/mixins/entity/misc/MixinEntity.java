/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.NbtPredicate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Team
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package traben.entity_texture_features.mixin.mixins.entity.misc;

import java.util.UUID;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value={Entity.class})
public abstract class MixinEntity
implements ETFEntity {
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract int getBlockY();

    @Shadow
    public abstract boolean hasCustomName();

    @Shadow
    @Nullable
    public abstract Component getCustomName();

    @Shadow
    public abstract float distanceTo(Entity var1);

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    @Nullable
    public abstract PlayerTeam getTeam();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    @Nullable
    public abstract Entity getVehicle();

    @Override
    public EntityType<?> etf$getType() {
        return this.getType();
    }

    @Override
    public UUID etf$getUuid() {
        return this.getUUID();
    }

    @Override
    public int etf$getOptifineId() {
        return (int)(this.etf$getUuid().getLeastSignificantBits() & Integer.MAX_VALUE);
    }

    @Override
    public int etf$getOptifineVehicleId() {
        return this.getVehicle() == null ? this.etf$getOptifineId() : ((ETFEntity)this.getVehicle()).etf$getOptifineId();
    }

    @Override
    public Level etf$getWorld() {
        return this.level();
    }

    @Override
    public BlockPos etf$getBlockPos() {
        return this.blockPosition();
    }

    @Override
    public int etf$getBlockY() {
        return this.getBlockY();
    }

    @Override
    public CompoundTag etf$getNbt() {
        return ETFRenderContext.cacheEntityNBTForFrame(this.etf$getUuid(), () -> NbtPredicate.getEntityTagToCompare((Entity)((Entity)this)));
    }

    @Override
    public boolean etf$hasCustomName() {
        return this.hasCustomName();
    }

    @Override
    public Component etf$getCustomName() {
        return this.getCustomName();
    }

    @Override
    public Team etf$getScoreboardTeam() {
        return this.getTeam();
    }

    @Override
    public Iterable<ItemStack> etf$getItemsEquipped() {
        LivingEntity alive = this.etf$getLivingOrNull();
        if (alive != null) {
            return alive.getAllSlots();
        }
        return null;
    }

    @Override
    public Iterable<ItemStack> etf$getHandItems() {
        LivingEntity alive = this.etf$getLivingOrNull();
        if (alive != null) {
            return alive.getHandSlots();
        }
        return null;
    }

    @Override
    public Iterable<ItemStack> etf$getArmorItems() {
        LivingEntity alive = this.etf$getLivingOrNull();
        if (alive != null) {
            return alive.getArmorSlots();
        }
        return null;
    }

    @Unique
    private LivingEntity etf$getLivingOrNull() {
        MixinEntity self = this;
        if (self instanceof LivingEntity) {
            LivingEntity alive = (LivingEntity)self;
            return alive;
        }
        return null;
    }

    @Override
    public float etf$distanceTo(Entity entity) {
        return this.distanceTo(entity);
    }

    @Override
    public Vec3 etf$getVelocity() {
        return this.getDeltaMovement();
    }

    @Override
    public Pose etf$getPose() {
        return this.getPose();
    }

    @Override
    public boolean etf$canBeBright() {
        MixinEntity self = this;
        return self instanceof Player;
    }

    @Override
    public boolean etf$isBlockEntity() {
        return false;
    }

    @Override
    public String etf$getEntityKey() {
        return this.getType().getDescriptionId();
    }
}

