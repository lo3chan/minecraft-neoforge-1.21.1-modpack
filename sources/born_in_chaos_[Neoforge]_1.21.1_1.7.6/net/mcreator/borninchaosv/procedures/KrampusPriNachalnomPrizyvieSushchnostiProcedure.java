package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class KrampusPriNachalnomPrizyvieSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 7, false, false));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 35, 4, false, false));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 40, 0, false, false));
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.warden.emerge")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.warden.emerge")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.SNOWCLOUD.get(), entity.getX(), entity.getY() + 0.2, entity.getZ(), 20, 0.9, 0.1, 0.9, 0.1
            );
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               20,
               0.8,
               0.8,
               0.9,
               0.1
            );
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               10,
               0.8,
               0.8,
               0.9,
               0.1
            );
         }

         if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR) {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.AIR) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.AIR) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.AIR) {
            world.setBlock(BlockPos.containing(x, y, z - 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.AIR) {
            world.setBlock(BlockPos.containing(x, y, z + 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.AIR && Math.random() < 0.45) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z + 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.AIR && Math.random() < 0.45) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z - 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.AIR && Math.random() < 0.45) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z - 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.AIR && Math.random() < 0.45) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z + 1.0), Blocks.SNOW.defaultBlockState(), 3);
         }
      }
   }
}
