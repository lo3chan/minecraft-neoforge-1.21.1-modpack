/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.PlayerModelPart
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.SkullBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package traben.entity_texture_features.mixin.mixins.entity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.features.player.ETFPlayerEntity;

@Mixin(value={SkullBlockEntity.class})
public abstract class MixinSkullBlockEntity
extends BlockEntity
implements ETFPlayerEntity {
    public MixinSkullBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Entity etf$getEntity() {
        return null;
    }

    @Override
    public boolean etf$isTeammate(Player player) {
        return false;
    }

    @Override
    public Inventory etf$getInventory() {
        return null;
    }

    @Override
    public boolean etf$isPartVisible(PlayerModelPart part) {
        return false;
    }

    @Override
    public Component etf$getName() {
        return Component.nullToEmpty((String)("player_skull # " + this.etf$getUuidAsString()));
    }

    @Override
    public String etf$getUuidAsString() {
        return this.etf$getUuid().toString();
    }
}

