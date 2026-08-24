package dev.latvian.mods.kubejs.level;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public record WrappedSpawner(@Nullable Entity entity, @Nullable LevelBlock block) {
   public static WrappedSpawner of(Either<BlockEntity, Entity> spawner) {
      if (spawner == null) {
         return new WrappedSpawner(null, null);
      } else {
         Entity e = (Entity)spawner.right().orElse(null);
         if (e != null) {
            return new WrappedSpawner(e, null);
         } else {
            BlockEntity be = (BlockEntity)spawner.left().orElse(null);
            return be != null ? new WrappedSpawner(null, be.getLevel().kjs$getBlock(be)) : new WrappedSpawner(null, null);
         }
      }
   }

   public boolean isWorldgen() {
      return this.entity == null && this.block == null;
   }
}
