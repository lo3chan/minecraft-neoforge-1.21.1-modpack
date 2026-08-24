package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PumpkinheadKoghdaSushchnostUmiraietProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity sourceentity) {
      if (sourceentity != null) {
         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.death")),
                  SoundSource.NEUTRAL,
                  0.6F,
                  1.6F
               );
            } else {
               _level.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.death")), SoundSource.NEUTRAL, 0.6F, 1.6F, false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x, y + 1.8, z, 12, 0.3, 0.3, 0.3, 0.1);
         }

         if (world.getLevelData().isThundering() || world.getLevelData().isRaining()) {
            world.getLevelData().setRaining(false);
         }

         if (sourceentity instanceof Player && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:pumpkin_sir"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
