package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class EternalCandyPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.ETERNAL_CANDY.get(), 70);
         }

         entity.clearFire();
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.ROTTEN_SMELL);
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SWEET_MADNESS, 10, 0, false, false));
         }
      }
   }
}
