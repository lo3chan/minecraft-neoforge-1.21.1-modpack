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

public class BurningoutthesoulProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.LORD_PUMPKINHEADS_HAT_HELMET.get()
            || !world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SOULSTRATIFICATIONEFFECT)
            || entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION)) {
            if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SOULSTRATIFICATIONEFFECT)
               && !(entity instanceof LivingEntity _livEnt17 && _livEnt17.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION))
               && !(entity instanceof LordPumpkinheadEntity)
               && !(entity instanceof PumpkinheadEntity)
               && !(entity instanceof SirPumpkinheadEntity)
               && !(entity instanceof SirPumpkinheadWithoutHorseEntity)
               && !(entity instanceof SirTheHeadlessEntity)
               && !(entity instanceof FelsteedEntity)
               && !(entity instanceof LordPumpkinheadHeadEntity)
               && !(entity instanceof LordPumpkinheadWithoutaHorseEntity)
               && !(entity instanceof LordTheHeadlessEntity)
               && !(entity instanceof LordsFelsteedEntity)) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION, 180, 0));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 180, 0));
               }
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
            && !(entity instanceof LordsFelsteedEntity)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION, 140, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 140, 0));
            }
         }
      }
   }
}
