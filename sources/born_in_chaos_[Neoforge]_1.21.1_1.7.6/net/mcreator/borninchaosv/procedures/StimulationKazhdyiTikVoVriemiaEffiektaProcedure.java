package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class StimulationKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.STIMULATINGBUBBLES.get(), x, y + 1.0, z, 1, 0.4, 0.4, 0.4, 0.2);
         }

         if (entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.INTOXICATION)
            && entity instanceof Player
            && !(
               entity instanceof ServerPlayer _plr3
                  && _plr3.level() instanceof ServerLevel
                  && _plr3.getAdvancements()
                     .getOrStartProgress(_plr3.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:shakeand_mix")))
                     .isDone()
            )
            && entity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:shakeand_mix"));
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
