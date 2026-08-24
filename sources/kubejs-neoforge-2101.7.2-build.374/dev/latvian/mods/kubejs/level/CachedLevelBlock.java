package dev.latvian.mods.kubejs.level;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CachedLevelBlock implements LevelBlock {
   public final Level minecraftLevel;
   private final BlockPos pos;
   public transient BlockState cachedState;
   public transient BlockEntity cachedEntity;

   public CachedLevelBlock(Level w, BlockPos p) {
      this.minecraftLevel = w;
      this.pos = p;
   }

   @Override
   public Level getLevel() {
      return this.minecraftLevel;
   }

   @Override
   public BlockPos getPos() {
      return this.pos;
   }

   @Override
   public LevelBlock cache(BlockState state) {
      this.cachedState = state;
      return this;
   }

   @Override
   public LevelBlock cache(BlockEntity entity) {
      this.cachedEntity = entity;
      return this;
   }

   public void clearCache() {
      this.cachedState = null;
      this.cachedEntity = null;
   }

   @Override
   public BlockState getBlockState() {
      if (this.cachedState == null) {
         this.cachedState = this.minecraftLevel.getBlockState(this.getPos());
      }

      return this.cachedState;
   }

   @Override
   public void setBlockState(BlockState state, int flags) {
      this.minecraftLevel.setBlock(this.getPos(), state, flags);
      this.clearCache();
      this.cachedState = state;
   }

   @Nullable
   @Override
   public BlockEntity getEntity() {
      if (this.cachedEntity == null || this.cachedEntity.isRemoved()) {
         this.cachedEntity = this.minecraftLevel.getBlockEntity(this.pos);
      }

      return this.cachedEntity;
   }

   @Override
   public String toString() {
      return this.toBlockStateString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof CharSequence) {
         return this.kjs$getId().equals(obj.toString());
      } else {
         return obj instanceof ResourceLocation ? this.kjs$getIdLocation().equals(obj) : super.equals(obj);
      }
   }
}
