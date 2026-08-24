package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class LifestealerTFAtackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof LifestealerTrueFormEntity && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.isBlocking())) {
            if (entity instanceof LivingEntity _livEnt2
               && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.LIFESTEAL)
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STRANGLEHOLD, 360, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIFESTEAL, 260, 0));
            }

            if (sourceentity instanceof LivingEntity _entity) {
               _entity.setHealth((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) + 5.0F);
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(),
                  sourceentity.getX(),
                  sourceentity.getY() + 2.0,
                  sourceentity.getZ(),
                  10,
                  0.5,
                  1.0,
                  0.5,
                  0.0
               );
            }
         } else if (sourceentity instanceof LifestealerTrueFormEntity
            && entity instanceof LivingEntity _livEnt12
            && _livEnt12.isBlocking()
            && (entity instanceof LivingEntity _entUseItem13 ? _entUseItem13.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
            if (world instanceof ServerLevel _level) {
               (entity instanceof LivingEntity _entUseItem15 ? _entUseItem15.getUseItem() : ItemStack.EMPTY).hurtAndBreak(20, _level, null, _stkprov -> {});
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 1, false, false));
            }
         }
      }
   }
}
