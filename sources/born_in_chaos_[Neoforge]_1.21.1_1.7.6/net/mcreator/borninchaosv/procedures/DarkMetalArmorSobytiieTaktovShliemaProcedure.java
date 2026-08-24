package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DarkMetalArmorSobytiieTaktovShliemaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.DARK_METAL_ARMOR_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.DARK_METAL_ARMOR_LEGGINGS.get()
            && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.DARK_METAL_ARMOR_CHESTPLATE.get()
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.DARK_METAL_ARMOR_BOOTS.get()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.WITHER);
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 6.0F) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE, 100, 0));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false));
               }
            }

            if (!(
                  entity instanceof ServerPlayer _plr12
                     && _plr12.level() instanceof ServerLevel
                     && _plr12.getAdvancements()
                        .getOrStartProgress(_plr12.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:chaos_knight")))
                        .isDone()
               )
               && entity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:chaos_knight"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }
         }
      }
   }
}
