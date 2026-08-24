package at.petrak.hexcasting.common.misc;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.mixin.accessor.AccessorAbstractArrow;
import at.petrak.hexcasting.mixin.accessor.AccessorVillager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public class RegisterMisc {
   public static void register() {
      HexAPI.instance().registerSpecialVelocityGetter(EntityType.PLAYER, player -> {
         if (player instanceof ServerPlayer splayer) {
            return PlayerPositionRecorder.getMotion(splayer);
         } else {
            throw new IllegalStateException("Call this only on the server side, silly");
         }
      });
      HexAPI.instance().registerSpecialVelocityGetter(EntityType.ARROW, RegisterMisc::arrowVelocitizer);
      HexAPI.instance().registerSpecialVelocityGetter(EntityType.SPECTRAL_ARROW, RegisterMisc::arrowVelocitizer);
      HexAPI.instance().registerSpecialVelocityGetter(EntityType.TRIDENT, RegisterMisc::arrowVelocitizer);
      HexAPI.instance().registerCustomBrainsweepingBehavior(EntityType.VILLAGER, villager -> {
         ((AccessorVillager)villager).hex$releaseAllPois();
         HexAPI.instance().defaultBrainsweepingBehavior().accept(villager);
      });
      HexAPI.instance().registerCustomBrainsweepingBehavior(EntityType.ALLAY, allay -> {
         allay.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
         HexAPI.instance().defaultBrainsweepingBehavior().accept(allay);
      });
   }

   private static Vec3 arrowVelocitizer(AbstractArrow arrow) {
      return ((AccessorAbstractArrow)arrow).hex$isInGround() ? Vec3.ZERO : arrow.getDeltaMovement();
   }
}
