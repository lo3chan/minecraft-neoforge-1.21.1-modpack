package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.procedures.WitherflameOnEffectActiveTickProcedure;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class WitherflameMobEffect extends MobEffect {
   public WitherflameMobEffect() {
      super(MobEffectCategory.NEUTRAL, -16777216);
      this.addAttributeModifier(
         UndeadRevamp2ModAttributes.CHEROATTACKSPEED, ResourceLocation.fromNamespaceAndPath("undead_revamp2", "effect.witherflame_0"), 0.0, Operation.ADD_VALUE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      WitherflameOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
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
         new MobEffect[]{(MobEffect)UndeadRevamp2ModMobEffects.WITHERFLAME.get()}
      );
   }
}
