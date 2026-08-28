/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.diebuddies.bridge;

import net.diebuddies.bridge.Event;
import net.diebuddies.bridge.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FabricAPIServer {
    public static final Event<StartWorldTick> START_WORLD_TICK = EventFactory.createArrayBacked(StartWorldTick.class, callbacks -> world -> {
        for (StartWorldTick callback : callbacks) {
            callback.onStartTick(world);
        }
    });
    public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, listeners -> (world, player, pos, state, entity) -> {
        for (After event : listeners) {
            event.afterBlockBreak(world, player, pos, state, entity);
        }
    });

    @FunctionalInterface
    public static interface After {
        public void afterBlockBreak(Level var1, Player var2, BlockPos var3, BlockState var4, BlockEntity var5);
    }

    @FunctionalInterface
    public static interface StartWorldTick {
        public void onStartTick(ServerLevel var1);
    }
}

