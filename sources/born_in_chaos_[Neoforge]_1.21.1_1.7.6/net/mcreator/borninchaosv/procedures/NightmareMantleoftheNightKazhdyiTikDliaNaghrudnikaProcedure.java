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

public class NightmareMantleoftheNightKazhdyiTikDliaNaghrudnikaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
            && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()) {
            if (entity instanceof LivingEntity _livEnt8
               && _livEnt8.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION)
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, 1, false, false));
            }

            if (!(
                  entity instanceof ServerPlayer _plr10
                     && _plr10.level() instanceof ServerLevel
                     && _plr10.getAdvancements()
                        .getOrStartProgress(_plr10.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:unitywith_darknessachievement")))
                        .isDone()
               )
               && entity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:unitywith_darknessachievement"));
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
