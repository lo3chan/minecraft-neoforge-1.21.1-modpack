package net.cibernet.alchemancy.mixin;

import javax.annotation.Nullable;
import net.cibernet.alchemancy.events.handler.MobTemptHandler;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MobMixin {
   @Shadow
   @Final
   public GoalSelector goalSelector;

   @Shadow
   @Nullable
   public abstract LivingEntity getControllingPassenger();

   @Inject(
      method = {"getControllingPassenger"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
      if (this.alchemancy$self().getFirstPassenger() instanceof LivingEntity living) {
         MobTemptHandler.performIfTempted(
            this.alchemancy$self(), living, EquipmentSlotGroup.HAND, () -> cir.setReturnValue((LivingEntity)((Mob)this).getFirstPassenger())
         );
      }
   }

   @Inject(
      method = {"updateControlFlags"},
      at = {@At("RETURN")}
   )
   public void updateControlFlags(CallbackInfo ci) {
      if (this.alchemancy$self().getFirstPassenger() instanceof LivingEntity living) {
         MobTemptHandler.performIfTempted(this.alchemancy$self(), living, EquipmentSlotGroup.HAND, () -> {
            this.goalSelector.setControlFlag(Flag.MOVE, true);
            this.goalSelector.setControlFlag(Flag.LOOK, true);
         });
      }
   }

   @Unique
   private Mob alchemancy$self() {
      return (Mob)this;
   }
}
