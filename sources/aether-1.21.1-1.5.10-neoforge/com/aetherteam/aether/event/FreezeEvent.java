package com.aetherteam.aether.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class FreezeEvent extends Event implements ICancellableEvent {
   private final LevelAccessor level;
   private final BlockPos pos;
   private final BlockState priorBlock;
   private BlockState frozenBlock;

   public FreezeEvent(LevelAccessor level, BlockPos pos, BlockState priorBlock, BlockState frozenBlock) {
      this.level = level;
      this.pos = pos;
      this.priorBlock = priorBlock;
      this.frozenBlock = frozenBlock;
   }

   public LevelAccessor getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getPriorBlock() {
      return this.priorBlock;
   }

   public BlockState getFrozenBlock() {
      return this.frozenBlock;
   }

   public void setFrozenBlock(BlockState frozenBlock) {
      this.frozenBlock = frozenBlock;
   }

   public static class FreezeFromBlock extends FreezeEvent implements ICancellableEvent {
      private final BlockPos sourcePos;
      private final BlockState sourceBlock;

      public FreezeFromBlock(LevelAccessor level, BlockPos pos, BlockPos sourcePos, BlockState priorBlock, BlockState frozenBlock, BlockState sourceBlock) {
         super(level, pos, priorBlock, frozenBlock);
         this.sourcePos = sourcePos;
         this.sourceBlock = sourceBlock;
      }

      public BlockState getSourceBlock() {
         return this.sourceBlock;
      }

      public BlockPos getSourcePos() {
         return this.sourcePos;
      }
   }

   public static class FreezeFromItem extends FreezeEvent implements ICancellableEvent {
      private final ItemStack sourceStack;

      public FreezeFromItem(LevelAccessor level, BlockPos pos, BlockState priorBlock, BlockState frozenBlock, ItemStack sourceStack) {
         super(level, pos, priorBlock, frozenBlock);
         this.sourceStack = sourceStack;
      }

      public ItemStack getSourceStack() {
         return this.sourceStack;
      }
   }
}
