package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.InfernalSpiritEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class InfernalSpiritAtackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof InfernalSpiritEntity
            && (entity instanceof Mob || entity instanceof Monster || entity instanceof Player)
            && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.isBlocking())
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 240, 0));
         }
      }
   }
}
