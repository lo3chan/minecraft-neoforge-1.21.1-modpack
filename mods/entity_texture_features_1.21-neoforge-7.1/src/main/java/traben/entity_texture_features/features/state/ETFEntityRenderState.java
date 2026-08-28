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
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

public interface ETFEntityRenderState {
    public UUID uuid();

    public boolean canRenderBright();

    public boolean isBlockEntity();

    public EntityType<?> entityType();

    public Level world();

    public BlockPos blockPos();

    public int optifineId();

    public int optifineVehicleId();

    public int blockY();

    public CompoundTag nbt();

    public boolean hasCustomName();

    public Component customName();

    public Team scoreboardTeam();

    public Iterable<ItemStack> itemsEquipped();

    public Iterable<ItemStack> handItems();

    public Iterable<ItemStack> armorItems();

    public Vec3 velocity();

    @Deprecated
    public Pose pose();

    public String entityKey();

    @Deprecated
    public ETFEntity entity();

    public float distanceTo(Entity var1);

    public static void setEtfRenderStateConstructor(String reason, ETFRenderStateInit init) {
        ETFUtils2.logMessage("Modifying ETF Render State constructor because: " + reason);
        ETF.etfRenderStateConstructor = init;
    }

    public static ETFEntityRenderState forEntity(ETFEntity entity) {
        return ETF.etfRenderStateConstructor.make(entity);
    }

    public static interface ETFRenderStateInit {
        public ETFEntityRenderState make(ETFEntity var1);
    }
}

