package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Set;
import net.mehvahdjukaar.moonlight.core.commands.BackCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({TeleportCommand.class})
public abstract class TeleportCommandMixin {
   @WrapOperation(
      method = {"performTeleport"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"
      )}
   )
   private static boolean ml$captureOldTpPos(
      Entity instance,
      ServerLevel level,
      double x,
      double y,
      double z,
      Set<RelativeMovement> relativeMovements,
      float yRot,
      float xRot,
      Operation<Boolean> original
   ) {
      BlockPos oldPos = instance.blockPosition();
      ResourceKey<Level> oldDim = instance.level().dimension();
      boolean result = (Boolean)original.call(new Object[]{instance, level, x, y, z, relativeMovements, yRot, xRot});
      if (result) {
         BackCommand.onTeleported(instance, oldPos, oldDim);
      }

      return result;
   }
}
