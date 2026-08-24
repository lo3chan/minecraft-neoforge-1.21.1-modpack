package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LifestealerPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(), x, y, z, 1, 0.3, 0.2, 0.3, 0.1);
         }

         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
            < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.LIFESTEALER_TRUE_FORM.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:lifestealer_scream_ap")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }
         }

         if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR
            && (
               world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS
            )) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).defaultBlockState(), 3);
         }
      }
   }
}
