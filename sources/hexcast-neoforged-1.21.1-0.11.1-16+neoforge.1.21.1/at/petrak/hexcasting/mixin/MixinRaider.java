package at.petrak.hexcasting.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Raider.class})
public class MixinRaider {
   @Redirect(
      method = {"aiStep"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/raid/Raider;isAlive()Z"
      )
   )
   private boolean isAliveForAiPurposes(Raider instance) {
      Raider self = (Raider)this;
      return self.isAlive() && !IXplatAbstractions.INSTANCE.isBrainswept(self);
   }
}
