package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SoulStratificationKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SOULSTRATIFICATIONEFFECT)
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != BornInChaosV1ModItems.LORD_PUMPKINHEADS_HAT_HELMET.get()
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION).getDuration()
                     : 0
               )
               == 40) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SOUL_STRATIFICATION);
            }

            if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) >= 4.0F) {
               entity.hurt(
                  new DamageSource(world.holderOrThrow(DamageTypes.MAGIC)),
                  (float)((entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) * 0.2 + 1.0)
               );
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.death")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.6F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.death")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.6F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x, y, z, 9, 0.8, 1.0, 0.8, 0.3);
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SOUL_SLASH.get(), x, y + 1.0, z, 1, 0.1, 0.1, 0.1, 0.1);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.REGENERATION);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.MOVEMENT_SPEED);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.FIRE_RESISTANCE);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.ABSORPTION);
               }
            }
         }
      }
   }
}
