package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.Blaze3D;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.events.GlassesZoomKeyPressEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({MouseHandler.class})
public class MouseHandlerMixin {
   @Shadow
   Minecraft minecraft;
   @Shadow
   private double lastHandleMovementTime;
   @Shadow
   private double accumulatedDX;
   @Shadow
   private double accumulatedDY;
   @Shadow
   private double xpos;
   @Shadow
   private double ypos;
   @Shadow
   private int activeButton;
   @Shadow
   private double mousePressedTime;
   private final SmoothDouble smoothTurnX = new SmoothDouble();
   private final SmoothDouble smoothTurnY = new SmoothDouble();

   @Shadow
   private boolean isMouseGrabbed() {
      throw new IllegalStateException();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"turnPlayer"},
      cancellable = true
   )
   public void turnPlayer(CallbackInfo callback) {
      if (Hexerei.entityClicked) {
         this.smoothTurnX.reset();
         this.smoothTurnY.reset();
         this.accumulatedDX = 0.0;
         this.accumulatedDY = 0.0;
         callback.cancel();
      }

      if (this.minecraft.options.getCameraType().isFirstPerson() && GlassesZoomKeyPressEvent.zoomToggled) {
         double d0 = Blaze3D.getTime();
         this.lastHandleMovementTime = d0;
         if (this.isMouseGrabbed() && this.minecraft.isWindowActive() && this.minecraft.player != null && !this.minecraft.player.isScoping()) {
            double d4 = (Double)this.minecraft.options.sensitivity().get() * 0.6000000238418579 + 0.20000000298023224;
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            double d2 = this.accumulatedDX * d4 * d4 * (d4 * 4.0);
            double d3 = this.accumulatedDY * d4 * d4 * (d4 * 4.0);
            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
            int i = 1;
            if ((Boolean)this.minecraft.options.invertYMouse().get()) {
               i = -1;
            }

            this.minecraft.getTutorial().onMouse(d2, d3);
            if (this.minecraft.player != null) {
               this.minecraft.player.turn(d2, d3 * i);
            }

            callback.cancel();
         }
      }
   }
}
