package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.procedures.ExperienceContainerKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class ExperienceContainerMobEffect extends MobEffect {
   public ExperienceContainerMobEffect() {
      super(MobEffectCategory.NEUTRAL, -6695368);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      ExperienceContainerKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity);
      return super.applyEffectTick(entity, amplifier);
   }

   @SubscribeEvent
   public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
      event.registerMobEffect(new IClientMobEffectExtensions() {
         public boolean isVisibleInGui(MobEffectInstance effect) {
            return false;
         }
      }, new MobEffect[]{(MobEffect)BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER.get()});
   }
}
