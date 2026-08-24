package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Info("Invoked when a falling block finishes falling.\n")
public class BlockStoppedFallingKubeEvent implements KubeEntityEvent {
   public final LevelBlock block;
   private final FallingBlockEntity entity;
   public final double fallSpeed;
   public final LevelBlock replacedBlock;

   public BlockStoppedFallingKubeEvent(Level level, BlockPos pos, BlockState state, FallingBlockEntity entity, double fallSpeed, BlockState replacedState) {
      this.block = level.kjs$getBlock(pos).cache(state);
      this.entity = entity;
      this.fallSpeed = fallSpeed;
      this.replacedBlock = level.kjs$getBlock(pos).cache(replacedState);
   }

   @Override
   public Level getLevel() {
      return this.block.getLevel();
   }

   @Override
   public Entity getEntity() {
      return this.entity;
   }
}
