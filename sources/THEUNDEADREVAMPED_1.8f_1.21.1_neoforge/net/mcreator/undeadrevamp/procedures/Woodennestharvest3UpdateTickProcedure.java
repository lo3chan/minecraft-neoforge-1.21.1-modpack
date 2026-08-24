package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Woodennestharvest3UpdateTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (Math.random() < 0.03 && world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
               SoundSource.NEUTRAL,
               0.1F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
               SoundSource.NEUTRAL,
               0.1F,
               1.0F,
               false
            );
         }
      }

      if (Math.random() < 0.2 && world instanceof ServerLevel _levelx) {
         _levelx.sendParticles(ParticleTypes.SNEEZE, x, y, z, 5, 1.0, 1.0, 1.0, 0.0);
      }
   }
}
