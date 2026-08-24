package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public class MixinScreen {
   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void physicsmod$renderHead(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      PhysicsMod.hudRendering = true;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render"}
   )
   public void physicsmod$renderTail(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      PhysicsMod.hudRendering = false;
   }
}
