package at.petrak.hexcasting.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Witch.class})
public class MixinWitch {
   @Redirect(
      method = {"aiStep"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/monster/Witch;isAlive()Z"
      )
   )
   private boolean isAliveForAiPurposes(Witch instance) {
      Witch self = (Witch)this;
      return self.isAlive() && !IXplatAbstractions.INSTANCE.isBrainswept(self);
   }
}
