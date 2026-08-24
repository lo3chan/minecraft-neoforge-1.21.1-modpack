package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SleepwalkingOnEffectActiveTicknewProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.lookAt(Anchor.EYES, new Vec3(x, y - 10.0, z));
         if (entity instanceof Mob _entity) {
            _entity.getNavigation().moveTo(x + 1.0, y, z, 1.0);
         }

         if (entity instanceof LivingEntity _entity) {
            AttributeInstance _attrInst = _entity.getAttribute(Attributes.STEP_HEIGHT);
            if (_attrInst != null) {
               _attrInst.setBaseValue(-2.0);
            }
         }

         if (Math.random() < 0.02 && world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.ZEESLEEP.get(), x, y, z, 1, 1.0, 2.0, 1.0, 0.15);
         }

         if (Math.random() < 0.015 && world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:snoring")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:snoring")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (Math.random() < 0.2 && entity instanceof Mob _mobEnt5 && _mobEnt5.isAggressive()) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entity != entityiterator && entity instanceof Mob _entityx && entityiterator instanceof LivingEntity _ent) {
                  _entityx.setTarget(_ent);
               }
            }
         }
      }
   }
}
