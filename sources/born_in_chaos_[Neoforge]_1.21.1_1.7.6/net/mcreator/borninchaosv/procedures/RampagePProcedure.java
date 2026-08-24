package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class RampagePProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity sourceentity) {
      execute(null, world, x, y, z, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity sourceentity) {
      if (sourceentity != null) {
         if (sourceentity instanceof Player) {
            if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
               || sourceentity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE)
               || sourceentity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
               || sourceentity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
               || sourceentity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
               || sourceentity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
               if (!(
                     (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                           == BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
                        && sourceentity instanceof LivingEntity _livEnt12
                  )
                  || !_livEnt12.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE)
                  || sourceentity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)
                  || sourceentity instanceof LivingEntity _livEnt14 && _livEnt14.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)
                  || sourceentity instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)
                  || sourceentity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
                  if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                        == BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
                     && !(sourceentity instanceof LivingEntity _livEnt22 && _livEnt22.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt24 && _livEnt24.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt25 && _livEnt25.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE))
                     && sourceentity instanceof LivingEntity _livEnt26
                     && _livEnt26.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE)) {
                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STRONG_RAMPAGE, 100, 0));
                     }

                     if (sourceentity instanceof LivingEntity _entity) {
                        _entity.removeEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE);
                     }

                     if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                           _level.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F
                           );
                        } else {
                           _level.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F,
                              false
                           );
                        }
                     }
                  } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                        == BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
                     && !(sourceentity instanceof LivingEntity _livEnt32 && _livEnt32.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt34 && _livEnt34.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt35 && _livEnt35.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE))
                     && sourceentity instanceof LivingEntity _livEnt36
                     && _livEnt36.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE)) {
                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE, 100, 0));
                     }

                     if (sourceentity instanceof LivingEntity _entity) {
                        _entity.removeEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE);
                     }

                     if (world instanceof Level _levelx) {
                        if (!_levelx.isClientSide()) {
                           _levelx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F
                           );
                        } else {
                           _levelx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F,
                              false
                           );
                        }
                     }
                  } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                        == BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
                     && !(sourceentity instanceof LivingEntity _livEnt42 && _livEnt42.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt43 && _livEnt43.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt44 && _livEnt44.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt45 && _livEnt45.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE))
                     && sourceentity instanceof LivingEntity _livEnt46
                     && _livEnt46.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE)) {
                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE, 200, 0));
                     }

                     if (sourceentity instanceof LivingEntity _entity) {
                        _entity.removeEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE);
                     }

                     if (world instanceof Level _levelxx) {
                        if (!_levelxx.isClientSide()) {
                           _levelxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.2F,
                              0.6F
                           );
                        } else {
                           _levelxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.2F,
                              0.6F,
                              false
                           );
                        }
                     }

                     if (world instanceof Level _levelxxx) {
                        if (!_levelxxx.isClientSide()) {
                           _levelxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.ravager.roar")),
                              SoundSource.NEUTRAL,
                              1.2F,
                              0.9F
                           );
                        } else {
                           _levelxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.ravager.roar")),
                              SoundSource.NEUTRAL,
                              1.2F,
                              0.9F,
                              false
                           );
                        }
                     }

                     if (world instanceof ServerLevel _levelxxxx) {
                        _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPLASHOFFLESH.get(), x, y, z, 9, 0.3, 0.3, 0.3, 0.2);
                     }

                     if (sourceentity instanceof ServerPlayer _player) {
                        AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:rampage"));
                        if (_adv != null) {
                           AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                           if (!_ap.isDone()) {
                              for (String criteria : _ap.getRemainingCriteria()) {
                                 _player.getAdvancements().award(_adv, criteria);
                              }
                           }
                        }
                     }
                  } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                        == BornInChaosV1ModItems.GREAT_REAPER_AXE.get()
                     && !(sourceentity instanceof LivingEntity _livEnt55 && _livEnt55.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt56 && _livEnt56.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt57 && _livEnt57.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
                     && !(sourceentity instanceof LivingEntity _livEnt58 && _livEnt58.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE))
                     && sourceentity instanceof LivingEntity _livEnt59
                     && _livEnt59.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE)) {
                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE, 200, 0));
                     }

                     if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 2, false, false));
                     }

                     if (world instanceof Level _levelxxxx) {
                        if (!_levelxxxx.isClientSide()) {
                           _levelxxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F
                           );
                        } else {
                           _levelxxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              0.6F,
                              false
                           );
                        }
                     }
                  }
               } else {
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE, 100, 0));
                  }

                  if (sourceentity instanceof LivingEntity _entity) {
                     _entity.removeEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE);
                  }

                  if (world instanceof Level _levelxxxxx) {
                     if (!_levelxxxxx.isClientSide()) {
                        _levelxxxxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           0.6F
                        );
                     } else {
                        _levelxxxxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           0.6F,
                           false
                        );
                     }
                  }
               }
            } else {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE, 100, 0));
               }

               if (world instanceof Level _levelxxxxxx) {
                  if (!_levelxxxxxx.isClientSide()) {
                     _levelxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.6F
                     );
                  } else {
                     _levelxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.iron_golem.damage")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        0.6F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
