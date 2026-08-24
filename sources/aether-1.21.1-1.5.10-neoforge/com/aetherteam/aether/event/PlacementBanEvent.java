package com.aetherteam.aether.event;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class PlacementBanEvent extends Event {
   public static class CheckBlock extends PlacementBanEvent {
      private boolean banned = true;
      private final LevelAccessor level;
      private final BlockPos pos;
      private final BlockState blockState;

      public CheckBlock(LevelAccessor level, BlockPos pos, BlockState blockState) {
         this.level = (LevelAccessor)Preconditions.checkNotNull(level, "Null world in PlacementBanEvent");
         this.pos = (BlockPos)Preconditions.checkNotNull(pos, "Null position in PlacementBanEvent");
         this.blockState = (BlockState)Preconditions.checkNotNull(blockState, "Null blockState in PlacementBanEvent");
      }

      public LevelAccessor getLevel() {
         return this.level;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }

      public boolean isBanned() {
         return this.banned;
      }

      public void setBanned(boolean banned) {
         this.banned = banned;
      }
   }

   public static class CheckItem extends PlacementBanEvent {
      private boolean banned = true;
      private final LevelAccessor level;
      private final BlockPos pos;
      private final ItemStack itemStack;

      public CheckItem(LevelAccessor level, BlockPos pos, ItemStack itemStack) {
         this.level = (LevelAccessor)Preconditions.checkNotNull(level, "Null world in PlacementBanEvent");
         this.pos = (BlockPos)Preconditions.checkNotNull(pos, "Null position in PlacementBanEvent");
         this.itemStack = (ItemStack)Preconditions.checkNotNull(itemStack, "Null itemStack in PlacementBanEvent");
      }

      public LevelAccessor getLevel() {
         return this.level;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public boolean isBanned() {
         return this.banned;
      }

      public void setBanned(boolean banned) {
         this.banned = banned;
      }
   }

   public static class SpawnParticles extends PlacementBanEvent implements ICancellableEvent {
      private final LevelAccessor level;
      private final BlockPos pos;
      @Nullable
      private final Direction face;
      @Nullable
      private final ItemStack itemStack;
      @Nullable
      private final BlockState blockState;

      public SpawnParticles(LevelAccessor level, BlockPos pos, @Nullable Direction face, @Nullable ItemStack stack, @Nullable BlockState state) {
         this.level = (LevelAccessor)Preconditions.checkNotNull(level, "Null world in PlacementBanEvent");
         this.pos = (BlockPos)Preconditions.checkNotNull(pos, "Null position in PlacementBanEvent");
         this.face = face;
         this.itemStack = stack;
         this.blockState = state;
      }

      public LevelAccessor getLevel() {
         return this.level;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      @Nullable
      public Direction getFace() {
         return this.face;
      }

      @Nullable
      public ItemStack getItemStack() {
         return this.itemStack;
      }

      @Nullable
      public BlockState getBlockState() {
         return this.blockState;
      }
   }
}
