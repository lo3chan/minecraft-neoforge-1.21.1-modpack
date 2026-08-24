package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

public class LuckysackrightclickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity.isShiftKeyDown() && (entity instanceof Player _plr ? _plr.experienceLevel : 0) >= 8) {
            if (entity instanceof Player _player) {
               _player.giveExperiencePoints(-((entity instanceof Player _plrx ? _plrx.experienceLevel : 0) - 8));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 750, 1));
            }

            UndeadRevamp2Mod.queueServerWork(
               3,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -5.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -5.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               6,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -4.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -4.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               9,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -3.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -3.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               12,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -2.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -2.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               15,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           -1.0F,
                           false
                        );
                     }
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               18,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.experience_orb.pickup")),
                           SoundSource.NEUTRAL,
                           2.0F,
                           1.0F,
                           false
                        );
                     }
                  }
               }
            );
            if (world instanceof ServerLevel _level) {
               itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
            }

            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown(itemstack.getItem(), 100);
            }
         }
      }
   }
}
