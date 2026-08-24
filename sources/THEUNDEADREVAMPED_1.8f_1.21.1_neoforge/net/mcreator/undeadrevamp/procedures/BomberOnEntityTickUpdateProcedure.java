package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class BomberOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            entity.getPersistentData().putDouble("gaszz_spead", entity.getPersistentData().getDouble("gaszz_spead") - 1.0);
         }

         if (entity.getPersistentData().getDouble("gaszz_spead") <= 0.0) {
            entity.getPersistentData().putDouble("gaszz_spead", 125.0);
            entity.setDeltaMovement(new Vec3(0.0, 0.2, 0.0));
            UndeadRevamp2Mod.queueServerWork(20, () -> {
               entity.setDeltaMovement(new Vec3(0.0, 0.2, 0.0));
               entity.getPersistentData().putDouble("gaszz_sped", 1.0);
            });
         }

         if (world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl12
               && _lvl12.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if ((world.getLevelData().isRaining() || world.getLevelData().isThundering()) && !world.isClientSide()) {
               entity.clearFire();
            }
         }

         if (entity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(UndeadRevamp2ModMobEffects.BOMBEREXPLODING)) {
            entity.getPersistentData().putBoolean("inflat_anim", true);
         } else {
            entity.getPersistentData().putBoolean("inflat_anim", false);
         }
      }
   }
}
