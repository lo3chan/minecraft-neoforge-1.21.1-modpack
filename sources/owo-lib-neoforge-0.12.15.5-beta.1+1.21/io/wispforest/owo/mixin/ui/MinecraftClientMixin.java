package io.wispforest.owo.mixin.ui;

import com.mojang.blaze3d.platform.Window;
import io.wispforest.owo.ui.event.ClientRenderCallback;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import io.wispforest.owo.ui.util.DisposableScreen;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftClientMixin {
   @Unique
   private final Set<DisposableScreen> screensToDispose = new HashSet<>();
   @Shadow
   @Final
   private Window window;
   @Shadow
   @Nullable
   public Screen screen;

   @Inject(
      method = {"resizeDisplay()V"},
      at = {@At("TAIL")}
   )
   private void captureResize(CallbackInfo ci) {
      ((WindowResizeCallback)WindowResizeCallback.EVENT.invoker()).onResized((Minecraft)this, this.window);
   }

   @Inject(
      method = {"runTick(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/platform/Window;setErrorSection(Ljava/lang/String;)V",
         ordinal = 1
      )}
   )
   private void beforeRender(boolean tick, CallbackInfo ci) {
      ((ClientRenderCallback)ClientRenderCallback.BEFORE.invoker()).onRender((Minecraft)this);
   }

   @Inject(
      method = {"runTick(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/platform/Window;updateDisplay()V",
         shift = Shift.AFTER
      )}
   )
   private void afterRender(boolean tick, CallbackInfo ci) {
      ((ClientRenderCallback)ClientRenderCallback.AFTER.invoker()).onRender((Minecraft)this);
   }

   @Inject(
      method = {"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"
      )}
   )
   private void captureSetScreen(Screen screen, CallbackInfo ci) {
      if (screen != null && this.screen instanceof DisposableScreen disposable) {
         this.screensToDispose.add(disposable);
      } else if (screen == null) {
         if (this.screen instanceof DisposableScreen disposable) {
            this.screensToDispose.add(disposable);
         }

         for (DisposableScreen disposable : this.screensToDispose) {
            try {
               disposable.dispose();
            } catch (Throwable var8) {
               CrashReport report = new CrashReport("Failed to dispose screen", var8);
               report.addCategory("Screen being disposed: ")
                  .setDetail("Screen class", disposable.getClass())
                  .setDetail("Screen being closed", this.screen)
                  .setDetail("Total screens to dispose", this.screensToDispose.size());
               throw new ReportedException(report);
            }
         }

         this.screensToDispose.clear();
      }
   }
}
