package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThesomnolenceEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class SleepwalkingOnEffectActiveTicksProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.lookAt(Anchor.EYES, new Vec3(x, y - 2.0, z));
         if (entity instanceof Mob _entity) {
            _entity.getNavigation().moveTo(x + 10.0, y, z, 1.0);
         }

         if (Math.random() < 0.04 && !(entity instanceof ThesomnolenceEntity)) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:snoring")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:snoring")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.SNEEZE, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.0);
            }
         }

         if (entity instanceof ThesomnolenceEntity && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS);
         }

         if (Math.random() < 0.08 && world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.ZEESLEEP.get(), x, y + 1.0, z, 1, 1.0, 1.0, 1.0, 0.5);
         }

         if (entity instanceof ServerPlayer _plr11
            && _plr11.level() instanceof ServerLevel
            && _plr11.getAdvancements().getOrStartProgress(_plr11.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:nightnight"))).isDone()
            && entity instanceof Player
            && entity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:nightnight"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         if (!(entity instanceof Player)) {
            entity.setYRot(0.0F);
            entity.setXRot(-5.0F);
            entity.setYBodyRot(entity.getYRot());
            entity.setYHeadRot(entity.getYRot());
            entity.yRotO = entity.getYRot();
            entity.xRotO = entity.getXRot();
            if (entity instanceof LivingEntity _entity) {
               _entity.yBodyRotO = _entity.getYRot();
               _entity.yHeadRotO = _entity.getYRot();
            }
         }
      }
   }
}
