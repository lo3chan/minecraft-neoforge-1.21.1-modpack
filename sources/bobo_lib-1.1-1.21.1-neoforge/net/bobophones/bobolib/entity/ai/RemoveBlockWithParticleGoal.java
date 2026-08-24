package net.bobophones.bobolib.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class RemoveBlockWithParticleGoal extends MoveToBlockGoal {
   private final Block block;
   private int ticks_since_reached_goal;

   public RemoveBlockWithParticleGoal(Block block, PathfinderMob mob, double speed, int range) {
      super(mob, speed, 24, range);
      this.block = block;
   }

   public boolean canUse() {
      if (!EventHooks.canEntityGrief(this.mob.level(), this.mob)) {
         return false;
      } else if (this.nextStartTick > 0) {
         this.nextStartTick--;
         return false;
      } else if (this.findNearestBlock()) {
         this.nextStartTick = reducedTickDelay(20);
         return true;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return false;
      }
   }

   public void start() {
      super.start();
      this.ticks_since_reached_goal = 0;
   }

   public void stop() {
      super.stop();
      this.mob.fallDistance = 1.0F;
   }

   public void tick() {
      super.tick();
      Level level = this.mob.level();
      BlockPos pos0 = this.mob.blockPosition();
      BlockPos pos1 = this.get_pos(pos0, level);
      RandomSource random = this.mob.getRandom();
      BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, this.block.defaultBlockState());
      if (this.isReachedTarget() && pos1 != null) {
         if (this.ticks_since_reached_goal > 0) {
            Vec3 vec = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(vec.x, 0.3, vec.z);
            if (level instanceof ServerLevel s_level) {
               s_level.sendParticles(
                  particle,
                  pos1.getX() + 0.5,
                  pos1.getY() + 0.7,
                  pos1.getZ() + 0.5,
                  3,
                  (random.nextDouble() - 0.5) * 0.08,
                  (random.nextDouble() - 0.5) * 0.08,
                  (random.nextDouble() - 0.5) * 0.08,
                  0.15
               );
            }
         }

         if (this.ticks_since_reached_goal % 2 == 0) {
            Vec3 vec = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(vec.x, -0.3, vec.z);
         }

         if (this.ticks_since_reached_goal > 60) {
            level.removeBlock(pos1, false);
            if (level instanceof ServerLevel s_level) {
               for (int i = 0; i < 20; i++) {
                  double d0 = random.nextGaussian() * 0.02;
                  double d1 = random.nextGaussian() * 0.02;
                  double d2 = random.nextGaussian() * 0.02;
                  s_level.sendParticles(particle, pos1.getX() + 0.5, pos1.getY(), pos1.getZ() + 0.5, 1, d0, d1, d2, 0.15);
               }
            }
         }

         this.ticks_since_reached_goal++;
      }
   }

   private BlockPos get_pos(BlockPos pos, BlockGetter level) {
      if (level.getBlockState(pos).is(this.block)) {
         return pos;
      } else {
         for (BlockPos i_pos : new BlockPos[]{pos.below(), pos.west(), pos.east(), pos.north(), pos.south(), pos.below().below()}) {
            if (level.getBlockState(i_pos).is(this.block)) {
               return i_pos;
            }
         }

         return null;
      }
   }

   protected boolean isValidTarget(LevelReader level, BlockPos pos) {
      ChunkAccess chunkaccess = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
      return chunkaccess != null
         && chunkaccess.getBlockState(pos).canEntityDestroy(level, pos, this.mob)
         && chunkaccess.getBlockState(pos).is(this.block)
         && chunkaccess.getBlockState(pos.above()).isAir()
         && chunkaccess.getBlockState(pos.above(2)).isAir();
   }
}
