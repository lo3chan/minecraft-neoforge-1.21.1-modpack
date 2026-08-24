package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.cibernet.alchemancy.events.handler.PropertyEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MouseHandler.class})
public class MouseHandlerMixin {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   @Final
   private SmoothDouble smoothTurnY;
   @Shadow
   @Final
   private SmoothDouble smoothTurnX;
   @Shadow
   private double accumulatedDX;
   @Shadow
   private double accumulatedDY;

   @Inject(
      method = {"turnPlayer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/tutorial/Tutorial;onMouse(DD)V",
         shift = Shift.BEFORE
      )}
   )
   public void turnPlayer(
      double movementTime, CallbackInfo ci, @Local(ordinal = 2) double d3, @Local(ordinal = 4) LocalDoubleRef d0, @Local(ordinal = 5) LocalDoubleRef d1
   ) {
      if (PropertyEventHandler.isScoping(this.minecraft.player)) {
         this.smoothTurnX.reset();
         this.smoothTurnY.reset();
         d0.set(this.accumulatedDX * d3);
         d1.set(this.accumulatedDY * d3);
      }
   }
}
