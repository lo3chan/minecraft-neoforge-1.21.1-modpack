package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class DogtruceMobEffect extends MobEffect {
   public DogtruceMobEffect() {
      super(MobEffectCategory.NEUTRAL, -15395559);
   }

   @SubscribeEvent
   public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
      event.registerMobEffect(new IClientMobEffectExtensions() {
         public boolean isVisibleInGui(MobEffectInstance effect) {
            return false;
         }
      }, new MobEffect[]{(MobEffect)BornInChaosV1ModMobEffects.DOGTRUCE.get()});
   }
}
