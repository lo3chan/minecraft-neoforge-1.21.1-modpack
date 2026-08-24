package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class StormcallersHornPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.SNOW_STORM))) {
            if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
               && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
               && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
               && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()) {
               if (entity instanceof Player _player) {
                  _player.getCooldowns().addCooldown(itemstack.getItem(), 1000);
               }

               if (world instanceof ServerLevel _level) {
                  itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SNOW_STORM, 900, 0));
               }
            } else {
               if (entity instanceof Player _player) {
                  _player.getCooldowns().addCooldown(itemstack.getItem(), 1500);
               }

               if (world instanceof ServerLevel _level) {
                  itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SNOW_STORM, 800, 0));
               }
            }
         }
      }
   }
}
