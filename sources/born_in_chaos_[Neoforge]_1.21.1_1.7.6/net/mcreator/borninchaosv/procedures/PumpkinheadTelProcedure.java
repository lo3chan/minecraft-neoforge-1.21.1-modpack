package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PumpkinheadTelProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) >= 30.0F && Math.random() < 0.9) {
            if (!world.isClientSide() && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (Math.random() < 0.3 && !world.getBlockState(BlockPos.containing(x + 8.0, y + 1.0, z + 0.5)).canOcclude()) {
               entity.teleportTo(x + 8.0, y + 1.0, z + 0.5);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
               }
            } else if (Math.random() < 0.3 && !world.getBlockState(BlockPos.containing(x - 8.0, y + 1.0, z + 0.5)).canOcclude()) {
               entity.teleportTo(x - 8.0, y + 1.0, z + 0.5);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x - 8.0, y + 1.0, z + 0.5, entity.getYRot(), entity.getXRot());
               }
            } else if (Math.random() < 0.3 && !world.getBlockState(BlockPos.containing(x + 0.5, y + 1.0, z + 8.0)).canOcclude()) {
               entity.teleportTo(x - 0.5, y + 1.0, z + 8.0);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x - 0.5, y + 1.0, z + 8.0, entity.getYRot(), entity.getXRot());
               }
            } else {
               entity.teleportTo(x - 0.5, y + 1.0, z - 8.0);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x - 0.5, y + 1.0, z - 8.0, entity.getYRot(), entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), x, y, z, 9, 1.0, 1.0, 1.0, 1.0);
            }

            if (!world.isClientSide() && world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F,
                     false
                  );
               }
            }
         }
      }
   }
}
