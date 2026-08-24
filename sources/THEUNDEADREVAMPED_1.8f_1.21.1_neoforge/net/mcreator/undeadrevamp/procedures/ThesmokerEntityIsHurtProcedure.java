package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThesmokerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThesmokerEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((entity instanceof ThesmokerEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(ThesmokerEntity.DATA_axe) : 0) != 1
            && (entity instanceof ThesmokerEntity _datEntI ? (Integer)_datEntI.getEntityData().get(ThesmokerEntity.DATA_bubblehp) : 0) > 0) {
            if (sourceentity instanceof LivingEntity) {
               if ((entity instanceof ThesmokerEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) > 0
                  && (entity instanceof ThesmokerEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) < 10) {
                  if (entity instanceof ThesmokerEntity _datEntSetI) {
                     _datEntSetI.getEntityData()
                        .set(
                           ThesmokerEntity.DATA_bubblehp,
                           (entity instanceof ThesmokerEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(ThesmokerEntity.DATA_bubblehp) : 0) - 15
                        );
                  }
               } else if (entity instanceof ThesmokerEntity _datEntSetI) {
                  _datEntSetI.getEntityData()
                     .set(
                        ThesmokerEntity.DATA_bubblehp,
                        (entity instanceof ThesmokerEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(ThesmokerEntity.DATA_bubblehp) : 0) - 5
                     );
               }
            }

            if ((entity instanceof ThesmokerEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(ThesmokerEntity.DATA_bubblehp) : 0) < 1) {
               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.TOXICFUMESPINK.get(), x, y, z, 30, 0.2, 2.0, 0.2, 1.0E-8);
               }

               if (entity instanceof ThesmokerEntity animatable) {
                  animatable.setTexture("decapitatedwheezer");
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:pop")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:pop")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
