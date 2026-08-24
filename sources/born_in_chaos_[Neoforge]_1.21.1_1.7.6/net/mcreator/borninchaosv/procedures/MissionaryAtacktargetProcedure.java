package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.MissionerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class MissionaryAtacktargetProcedure {
   @SubscribeEvent
   public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
      execute(event, event.getOriginalAboutToBeSetTarget(), event.getEntity());
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof MissionerEntity
            && entity instanceof Player
            && !(sourceentity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))
            && !(sourceentity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.SACRIFICE))) {
            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.UNDEAD_SUMMONUN, 60, 0, false, false));
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 300, 0, false, false));
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 2, false, false));
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SACRIFICE, 600, 0, false, false));
            }
         }
      }
   }
}
