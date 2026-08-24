package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.FelsteedEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadHeadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadWithoutaHorseEntity;
import net.mcreator.borninchaosv.entity.LordTheHeadlessEntity;
import net.mcreator.borninchaosv.entity.LordsFelsteedEntity;
import net.mcreator.borninchaosv.entity.PumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadWithoutHorseEntity;
import net.mcreator.borninchaosv.entity.SirTheHeadlessEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class SoulSaberKoghdaZhivaiaSushchnostPopadaietSPomoshchiuInstrumientaProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
            || (sourceentity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
            || (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
            || (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()
            || !world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SOULSTRATIFICATIONEFFECT)
            || entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION)) {
            if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.GENERATIONOFINFECTEDDIAMONDS)
               && !(entity instanceof LivingEntity _livEnt22 && _livEnt22.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION))
               && !(entity instanceof LordPumpkinheadEntity)
               && !(entity instanceof PumpkinheadEntity)
               && !(entity instanceof SirPumpkinheadEntity)
               && !(entity instanceof SirPumpkinheadWithoutHorseEntity)
               && !(entity instanceof SirTheHeadlessEntity)
               && !(entity instanceof FelsteedEntity)
               && !(entity instanceof LordPumpkinheadHeadEntity)
               && !(entity instanceof LordPumpkinheadWithoutaHorseEntity)
               && !(entity instanceof LordTheHeadlessEntity)
               && !(entity instanceof LordsFelsteedEntity)
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION, 160, 0));
            }
         } else if (!(entity instanceof LordPumpkinheadEntity)
            && !(entity instanceof PumpkinheadEntity)
            && !(entity instanceof SirPumpkinheadEntity)
            && !(entity instanceof SirPumpkinheadWithoutHorseEntity)
            && !(entity instanceof SirTheHeadlessEntity)
            && !(entity instanceof FelsteedEntity)
            && !(entity instanceof LordPumpkinheadHeadEntity)
            && !(entity instanceof LordPumpkinheadWithoutaHorseEntity)
            && !(entity instanceof LordTheHeadlessEntity)
            && !(entity instanceof LordsFelsteedEntity)
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION, 120, 0));
         }
      }
   }
}
