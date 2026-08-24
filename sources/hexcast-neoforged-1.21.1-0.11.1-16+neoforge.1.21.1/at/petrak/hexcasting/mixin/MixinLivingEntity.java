package at.petrak.hexcasting.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class MixinLivingEntity {
   @Inject(
      method = {"getBrain"},
      at = {@At("RETURN")}
   )
   private void removeBrain(CallbackInfoReturnable<Brain<?>> cir) {
      LivingEntity self = (LivingEntity)this;
      if (self instanceof Mob mob && IXplatAbstractions.INSTANCE.isBrainswept(mob)) {
         Brain<?> brain = (Brain<?>)cir.getReturnValue();
         brain.removeAllBehaviors();
         cir.setReturnValue(brain);
      }
   }
}
