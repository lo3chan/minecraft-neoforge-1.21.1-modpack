package net.diebuddies.mixins.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.PhysicsDebugOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public class MixinGui {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Unique
   private PhysicsDebugOverlay physicsmod$debugOverlay;

   @Inject(
      at = {@At("TAIL")},
      method = {"render"}
   )
   public void physicsmod$renderDebugInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
      if (this.physicsmod$debugOverlay == null) {
         this.physicsmod$debugOverlay = new PhysicsDebugOverlay(this.minecraft);
      }

      if (ConfigClient.renderPhysicsDebugOverlay) {
         this.physicsmod$debugOverlay.render(guiGraphics);
      }
   }
}
