package net.diebuddies.mixins;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Shadow
   private Overlay overlay;
   @Unique
   private boolean firstVerification = true;
   @Unique
   private volatile boolean failedVerification;

   @Inject(
      at = {@At("HEAD")},
      method = {"clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V"}
   )
   public void clearLevel(Screen screen, CallbackInfo info) {
      ObjectIterator var3 = PhysicsMod.getInstances().values().iterator();

      while (var3.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var3.next();
         mod.getPhysicsWorld().destroy();
      }

      PhysicsMod.getInstances().clear();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getFramerateLimit"},
      cancellable = true
   )
   private void getFramerateLimit(CallbackInfoReturnable<Integer> info) {
      Minecraft mc = (Minecraft)this;
      if (mc.level == null && (mc.screen != null || this.overlay != null) && mc.screen instanceof PhysicsSettingsScreen) {
         info.setReturnValue(120);
      }
   }
}
