package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.AetherGameEvents;
import com.aetherteam.aether.block.FreezingBlock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;

public class IcestoneBlockEntity extends BlockEntity implements FreezingBlock {
   private final IcestoneBlockEntity.FreezingListener listener;
   private final Map<BlockPos, Integer> lastBrokenPositions = new HashMap<>();

   public IcestoneBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)AetherBlockEntityTypes.ICESTONE.get(), pos, state);
      PositionSource positionSource = new BlockPositionSource(this.getBlockPos());
      this.listener = new IcestoneBlockEntity.FreezingListener(positionSource, 4);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, IcestoneBlockEntity blockEntity) {
      if (!blockEntity.lastBrokenPositions.isEmpty()) {
         Iterator<Entry<BlockPos, Integer>> it = blockEntity.lastBrokenPositions.entrySet().iterator();

         while (it.hasNext()) {
            Entry<BlockPos, Integer> entry = it.next();
            int nextDelay = entry.setValue(entry.getValue() - 1);
            if (nextDelay == 0) {
               blockEntity.freezeBlocks(level, blockEntity.getBlockPos(), blockEntity.getBlockState(), FreezingBlock.SQRT_8);
               it.remove();
            }
         }
      }
   }

   public IcestoneBlockEntity.FreezingListener getListener() {
      return this.listener;
   }

   public Map<BlockPos, Integer> getLastBrokenPositions() {
      return this.lastBrokenPositions;
   }

   class FreezingListener implements GameEventListener {
      private final PositionSource listenerSource;
      private final int listenerRadius;

      public FreezingListener(PositionSource source, int radius) {
         this.listenerSource = source;
         this.listenerRadius = radius;
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRadius;
      }

      public boolean handleGameEvent(ServerLevel level, Holder<GameEvent> event, Context context, Vec3 pos) {
         if (!event.is(AetherGameEvents.ICESTONE_FREEZABLE_UPDATE)
            && !event.is(GameEvent.BLOCK_PLACE)
            && !event.is(GameEvent.FLUID_PLACE)
            && !event.is(GameEvent.ENTITY_PLACE)) {
            if (event == GameEvent.BLOCK_DESTROY) {
               BlockState state = context.affectedState();
               if (state != null && FreezingBlock.cachedResults.contains(state.getBlock())) {
                  BlockPos blockPos = BlockPos.containing(pos);
                  IcestoneBlockEntity.this.lastBrokenPositions.put(blockPos, (int)(state.getBlock().defaultDestroyTime() * 200.0F));
                  return true;
               }
            }

            return false;
         } else {
            IcestoneBlockEntity.this.freezeBlocks(level, IcestoneBlockEntity.this.getBlockPos(), IcestoneBlockEntity.this.getBlockState(), FreezingBlock.SQRT_8);
            return true;
         }
      }
   }
}
