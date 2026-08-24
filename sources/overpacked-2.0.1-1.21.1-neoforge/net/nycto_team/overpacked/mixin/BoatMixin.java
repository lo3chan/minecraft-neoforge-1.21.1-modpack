package net.nycto_team.overpacked.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.nycto_team.overpacked.util.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Boat.class})
public abstract class BoatMixin {
   @Inject(
      method = {"canAddPassenger"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void can_add_passenger(Entity passenger, CallbackInfoReturnable<Boolean> cir) {
      Boat boat = (Boat)this;
      if (boat instanceof ChestBoat chest_boat) {
         if (chest_boat.getPassengers().isEmpty() && passenger instanceof LivingEntity entity && Utils.is_backpack_equipped(entity)) {
            cir.setReturnValue(false);
         }
      } else if (!boat.getPassengers().isEmpty()
         && passenger instanceof LivingEntity entity
         && (Utils.is_backpack_equipped(entity) || boat.getFirstPassenger() instanceof LivingEntity l_entity && Utils.is_backpack_equipped(l_entity))) {
         cir.setReturnValue(false);
      }
   }
}
