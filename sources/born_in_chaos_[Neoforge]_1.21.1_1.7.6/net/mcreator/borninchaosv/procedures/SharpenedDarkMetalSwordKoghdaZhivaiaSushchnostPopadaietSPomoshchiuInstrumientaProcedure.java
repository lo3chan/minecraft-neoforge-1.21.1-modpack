package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;

public class SharpenedDarkMetalSwordKoghdaZhivaiaSushchnostPopadaietSPomoshchiuInstrumientaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, ItemStack itemstack) {
      if (entity != null && sourceentity != null) {
         if (entity.getType().is(EntityTypeTags.UNDEAD)
            && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK))
            && itemstack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE)) != 0) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 20.0F);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BARBEDATTACK, 10, 0, false, false));
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 10, 0.4, 0.7, 0.4, 0.3);
            }
         } else if (entity.getType().is(EntityTypeTags.UNDEAD)
            && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK))) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC), sourceentity), 14.0F);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BARBEDATTACK, 10, 0, false, false));
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 10, 0.4, 0.7, 0.4, 0.3);
            }
         }
      }
   }
}
