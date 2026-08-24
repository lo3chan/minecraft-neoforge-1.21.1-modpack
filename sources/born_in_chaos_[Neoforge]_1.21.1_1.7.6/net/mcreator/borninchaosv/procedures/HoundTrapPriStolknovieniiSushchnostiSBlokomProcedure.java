package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.BloodyGadflyEntity;
import net.mcreator.borninchaosv.entity.CorpseFlyEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class HoundTrapPriStolknovieniiSushchnostiSBlokomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof CorpseFlyEntity) && !(entity instanceof BloodyGadflyEntity)) {
            if (entity instanceof Player) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 10.0F);
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_FRACTURE, 300, 0));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 5, false, false));
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.CRIT, x + 0.5, y + 0.5, z + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
               }

               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bs = ((Block)BornInChaosV1ModBlocks.CLOSED_HOUND_TRAP.get()).defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);

               for (Property<?> _propertyOld : _bso.getProperties()) {
                  Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
                  if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                     try {
                        _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                     } catch (Exception var17) {
                     }
                  }
               }

               world.setBlock(_bp, _bs, 3);
               if (entity instanceof ServerPlayer _player) {
                  AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:unlucky_hunter"));
                  if (_adv != null) {
                     AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                     if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria()) {
                           _player.getAdvancements().award(_adv, criteria);
                        }
                     }
                  }
               }
            } else if (!(entity instanceof Player) && (entity.getType().is(EntityTypeTags.UNDEAD) || entity instanceof Animal)) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 25.0F);
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_FRACTURE, 500, 0));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 60, 0, false, false));
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.CRIT, x + 0.5, y + 0.5, z + 0.5, 8, 0.2, 0.2, 0.2, 0.1);
               }

               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bs = ((Block)BornInChaosV1ModBlocks.CLOSED_HOUND_TRAP.get()).defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);

               for (Property<?> _propertyOldx : _bso.getProperties()) {
                  Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
                  if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                     try {
                        _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
                     } catch (Exception var16) {
                     }
                  }
               }

               world.setBlock(_bp, _bs, 3);
            } else if (!(entity instanceof Player)) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 20.0F);
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_FRACTURE, 400, 0));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5, false, false));
               }

               if (world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F
                     );
                  } else {
                     _levelxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:houndtrap")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.9F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.CRIT, x + 0.5, y + 0.5, z + 0.5, 8, 0.2, 0.2, 0.2, 0.1);
               }

               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bs = ((Block)BornInChaosV1ModBlocks.CLOSED_HOUND_TRAP.get()).defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);

               for (Property<?> _propertyOldxx : _bso.getProperties()) {
                  Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxx.getName());
                  if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                     try {
                        _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxx));
                     } catch (Exception var15) {
                     }
                  }
               }

               world.setBlock(_bp, _bs, 3);
            }
         }
      }
   }
}
