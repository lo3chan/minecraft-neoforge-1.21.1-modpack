package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;

@EventBusSubscriber
public class EatingRottenFleshProcedure {
   @SubscribeEvent
   public static void onUseItemFinish(Finish event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity(), event.getItem());
      }
   }

   public static void execute(Entity entity, ItemStack itemstack) {
      execute(null, entity, itemstack);
   }

   private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (itemstack.copy().getItem() == Items.ROTTEN_FLESH && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.ROTTEN_SMELL, 2400, 0));
         }
      }
   }
}
