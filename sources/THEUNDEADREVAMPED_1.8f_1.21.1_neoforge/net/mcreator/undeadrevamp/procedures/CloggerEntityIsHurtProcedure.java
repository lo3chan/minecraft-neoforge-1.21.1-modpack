package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CloggerEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) / 100.0F * 30.0F
            && entity instanceof CloggerEntity
            && world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerpain")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  -5.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerpain")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  -5.0F,
                  false
               );
            }
         }
      }
   }
}
