package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.procedures.SweetMadnessKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.SweetMadnessPriNalozhieniiEffiektaProcedure;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
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
public class SweetMadnessMobEffect extends MobEffect {
   public SweetMadnessMobEffect() {
      super(MobEffectCategory.NEUTRAL, -3407668);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      SweetMadnessPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      SweetMadnessKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }

   @SubscribeEvent
   public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
      event.registerMobEffect(
         new IClientMobEffectExtensions() {
            public boolean isVisibleInInventory(MobEffectInstance effect) {
               return false;
            }

            public boolean renderInventoryText(
               MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset
            ) {
               return false;
            }

            public boolean isVisibleInGui(MobEffectInstance effect) {
               return false;
            }
         },
         new MobEffect[]{(MobEffect)BornInChaosV1ModMobEffects.SWEET_MADNESS.get()}
      );
   }
}
