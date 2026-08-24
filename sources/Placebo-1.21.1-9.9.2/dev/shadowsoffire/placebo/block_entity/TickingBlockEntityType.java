package dev.shadowsoffire.placebo.block_entity;

import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import org.jetbrains.annotations.Nullable;

public class TickingBlockEntityType<T extends BlockEntity & TickingBlockEntity> extends BlockEntityType<T> {
   protected final TickingBlockEntityType.TickSide side;

   public TickingBlockEntityType(BlockEntitySupplier<? extends T> pFactory, Set<Block> pValidBlocks, TickingBlockEntityType.TickSide side) {
      super(pFactory, pValidBlocks, null);
      this.side = side;
   }

   @Nullable
   public BlockEntityTicker<T> getTicker(boolean client) {
      if (client && this.side.ticksOnClient()) {
         return (level, pos, state, entity) -> ((TickingBlockEntity)entity).clientTick(level, pos, state);
      } else {
         return !client && this.side.ticksOnServer() ? (level, pos, state, entity) -> ((TickingBlockEntity)entity).serverTick(level, pos, state) : null;
      }
   }

   public static enum TickSide {
      CLIENT,
      SERVER,
      CLIENT_AND_SERVER;

      public boolean ticksOnClient() {
         return this == CLIENT || this == CLIENT_AND_SERVER;
      }

      public boolean ticksOnServer() {
         return this == SERVER || this == CLIENT_AND_SERVER;
      }
   }
}
