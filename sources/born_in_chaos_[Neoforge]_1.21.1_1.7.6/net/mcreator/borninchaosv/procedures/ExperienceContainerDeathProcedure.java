package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class ExperienceContainerDeathProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER)) {
            for (int index0 = 0;
               index0
                  < 1
                     + (
                        entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER)
                           ? _livEnt.getEffect(BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER).getAmplifier()
                           : 0
                     );
               index0++
            ) {
               if (world instanceof ServerLevel _level) {
                  _level.addFreshEntity(
                     new ExperienceOrb(
                        _level,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        (int)(3.0 + (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) * 0.35)
                     )
                  );
               }
            }

            for (int index1 = 0; index1 < 3; index1++) {
               if (world instanceof ServerLevel _level) {
                  _level.addFreshEntity(new ExperienceOrb(_level, entity.getX(), entity.getY() + 1.0, entity.getZ(), 1));
               }
            }
         }
      }
   }
}
