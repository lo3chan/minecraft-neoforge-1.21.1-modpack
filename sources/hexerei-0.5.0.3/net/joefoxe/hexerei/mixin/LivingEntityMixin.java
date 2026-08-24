package net.joefoxe.hexerei.mixin;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"dismountVehicle"},
      at = {@At("HEAD")}
   )
   public void dismount(Entity vehicle, CallbackInfo ci) {
      if (vehicle instanceof BroomEntity broom) {
         broom.getPassengers().forEach(entity -> {
            if (!(entity instanceof Player)) {
               entity.stopRiding();
               if (entity instanceof TamableAnimal animal) {
                  animal.setInSittingPose(false);
                  animal.setOrderedToSit(false);
                  if (animal instanceof Cat cat) {
                     cat.setLying(false);
                  }
               }
            }
         });
      }
   }

   @Inject(
      method = {"isInWall"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void inWall(CallbackInfoReturnable<Boolean> cir) {
      if (this.getRootVehicle() instanceof BroomEntity broom && this.getType().is(HexereiTags.Entity.CAN_RIDE_BROOM)) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
