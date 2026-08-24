package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class LifestealPriNalozhieniiEffiektaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 2.0F) {
            if (world.getDifficulty() == Difficulty.HARD) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 2.0F);
               }
            } else if (world.getDifficulty() == Difficulty.NORMAL && entity instanceof LivingEntity _entity) {
               _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 1.0F);
            }
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(
               new MobEffectInstance(
                  MobEffects.WITHER,
                  entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.LIFESTEAL)
                     ? _livEntx.getEffect(BornInChaosV1ModMobEffects.LIFESTEAL).getDuration()
                     : 0,
                  0,
                  false,
                  false
               )
            );
         }

         if (entity instanceof Player && entity instanceof Player _player) {
            _player.causeFoodExhaustion(1.5F);
         }
      }
   }
}
