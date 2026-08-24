package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockExplodedCallback {
   protected final LevelBlock block;
   protected final Explosion explosion;

   public BlockExplodedCallback(Level level, BlockPos pos, Explosion explosion) {
      this.block = level.kjs$getBlock(pos);
      this.explosion = explosion;
   }

   public Level getLevel() {
      return this.block.getLevel();
   }

   public LevelBlock getBlock() {
      return this.block;
   }

   public BlockState getBlockState() {
      return this.block.getBlockState();
   }

   public Explosion getExplosion() {
      return this.explosion;
   }

   public Entity getCause() {
      return this.explosion.getDirectSourceEntity();
   }

   @Nullable
   public LivingEntity getIgniter() {
      return this.explosion.getIndirectSourceEntity();
   }

   public float getRadius() {
      return this.explosion.radius();
   }

   public List<Player> getAffectedPlayers() {
      return this.explosion.getHitPlayers().keySet().stream().toList();
   }
}
