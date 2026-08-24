package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SomnolencespawnsOnTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.DRIPSOMNOLENCE.get(), x, y, z, 1, 1.0, -0.1, 1.0, 0.5);
      }

      UndeadRevamp2Mod.queueServerWork(5, () -> {
         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.DRIPSOMNOLENCE.get(), x, y, z, 1, 1.0, -0.1, 1.0, 0.5);
         }
      });
      UndeadRevamp2Mod.queueServerWork(10, () -> {
         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.DRIPSOMNOLENCE.get(), x, y, z, 1, 1.0, -0.1, 1.0, 0.5);
         }
      });
      UndeadRevamp2Mod.queueServerWork(15, () -> {
         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.DRIPSOMNOLENCE.get(), x, y, z, 1, 1.0, -0.1, 1.0, 0.5);
         }
      });
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.drip")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.drip")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
            );
         }
      }
   }
}
