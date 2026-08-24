package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PuddleofStimulationticProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.STIMULATINGBUBBLES.get(), x + 0.5, y, z + 0.5, 1, 0.2, 0.2, 0.2, 0.2);
      }

      if (!world.isClientSide()) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockEntity _blockEntity = world.getBlockEntity(_bp);
         BlockState _bs = world.getBlockState(_bp);
         if (_blockEntity != null) {
            _blockEntity.getPersistentData().putDouble("puddle", (new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "puddle") - 1.0);
         }

         if (world instanceof Level _level) {
            _level.sendBlockUpdated(_bp, _bs, _bs, 3);
         }
      }

      if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "puddle") == 0.0) {
         world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x + 0.5, y + 0.5, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                  SoundSource.NEUTRAL,
                  0.3F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                  SoundSource.NEUTRAL,
                  0.3F,
                  1.0F,
                  false
               );
            }
         }
      }

      if (!world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()) {
         world.destroyBlock(BlockPos.containing(x, y, z), false);
      }
   }
}
