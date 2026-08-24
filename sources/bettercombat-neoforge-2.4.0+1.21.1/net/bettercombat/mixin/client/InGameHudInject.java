package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.BetterCombatClientMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public abstract class InGameHudInject {
   @Inject(
      method = {"renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
      )}
   )
   private void pre_renderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      if (BetterCombatClientMod.config.isHighlightCrosshairEnabled) {
         this.setShaderForHighlighting();
      }
   }

   @Inject(
      method = {"renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("TAIL")}
   )
   private void post_renderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Unique
   private void setShaderForHighlighting() {
      if (((MinecraftClient_BetterCombat)Minecraft.getInstance()).hasTargetsInReach()) {
         int color = BetterCombatClientMod.config.hudHighlightColor;
         float red = (color >> 16 & 0xFF) / 255.0F;
         float green = (color >> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         float alpha = 0.5F;
         RenderSystem.setShaderColor(red, green, blue, alpha);
      }
   }
}
