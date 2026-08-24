package net.mehvahdjukaar.moonlight.api.block;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface IOneUserInteractable {
   void setCurrentUser(@Nullable UUID var1);

   @Nullable
   UUID getCurrentUser();

   default boolean canBeUsedBy(BlockPos myPos, Entity player) {
      if (player instanceof ServerPlayer sp) {
         this.validateClaimer(myPos, sp.serverLevel());
         UUID uuid = this.getCurrentUser();
         return uuid == null ? true : uuid.equals(player.getUUID());
      } else {
         return this.isCloseEnoughToUse(player, myPos);
      }
   }

   private void validateClaimer(BlockPos myPos, ServerLevel level) {
      if (level == null) {
         this.setCurrentUser(null);
      } else {
         UUID uuid = this.getCurrentUser();
         if (uuid != null) {
            Entity player = level.getEntity(uuid);
            if (player == null || !this.isCloseEnoughToUse(player, myPos)) {
               this.setCurrentUser(null);
            }
         }
      }
   }

   default boolean isCloseEnoughToUse(Entity e, BlockPos myPos) {
      double maxDistance = 8.0;
      if (e instanceof Player p) {
         return p.canInteractWithBlock(myPos, maxDistance);
      } else {
         double currentDist = new AABB(myPos).distanceToSqr(e.getEyePosition());
         return currentDist < maxDistance * maxDistance;
      }
   }
}
