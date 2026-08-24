package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThehorrorsdecoysEntity;
import net.mcreator.undeadrevamp.entity.ThespectreEntity;
import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class AnimationtestEffectStartedappliedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity.getDeltaMovement().x() != 0.0 || entity.getDeltaMovement().z() != 0.0)
            && entity instanceof ThespectreEntity
            && entity instanceof ThespectreEntity) {
            ((ThespectreEntity)entity).setAnimation("sprint");
         }

         if (entity instanceof ThespitterEntity) {
            world.levelEvent(2001, BlockPos.containing(x, y - 1.0, z), Block.getId(Blocks.DIRT.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y - 1.0, z), Block.getId(Blocks.DIRT.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y - 1.0, z), Block.getId(Blocks.DIRT.defaultBlockState()));
            entity.setShiftKeyDown(false);
         }

         if (entity instanceof ThehorrorsdecoysEntity) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")),
                     SoundSource.NEUTRAL,
                     8.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")),
                     SoundSource.NEUTRAL,
                     8.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 50, 1.0, 2.0, 1.0, 0.0);
            }

            if (Math.random() < 0.35 && world instanceof ServerLevel _levelx) {
               _levelx.addFreshEntity(new ExperienceOrb(_levelx, x, y, z, 1));
            }
         }
      }
   }
}
