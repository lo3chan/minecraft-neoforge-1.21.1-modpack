package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherNotDespawnEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class WoodSplitterAxePriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof DoorKnightEntity
            || entity instanceof DoorKnightNotDespawnEntity
            || entity instanceof SkeletonThrasherEntity
            || entity instanceof SkeletonThrasherNotDespawnEntity) {
            if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.CRIT, x, y, z, 5, 0.3, 0.5, 0.3, 0.1);
               }
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 600, 0, false, false));
            }
         }

         if ((entity instanceof Player || entity instanceof Mob)
            && (entity instanceof LivingEntity _entUseItem10 ? _entUseItem10.getUseItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem
            && entity instanceof LivingEntity _livEnt12
            && _livEnt12.isBlocking()) {
            if (world instanceof ServerLevel _levelx) {
               (entity instanceof LivingEntity _entUseItem13 ? _entUseItem13.getUseItem() : ItemStack.EMPTY).hurtAndBreak(500, _levelx, null, _stkprov -> {});
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(ParticleTypes.CRIT, x, y, z, 5, 0.3, 0.5, 0.3, 0.1);
            }
         }
      }
   }
}
