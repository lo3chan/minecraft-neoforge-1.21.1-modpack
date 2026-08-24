package io.wispforest.owo.mixin.ui;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.util.pond.OwoSlotExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractContainerScreen.class})
public abstract class HandledScreenMixin extends Screen {
   @Unique
   private static boolean owo$inOwoScreen = false;

   protected HandledScreenMixin(Component title) {
      super(title);
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"},
      at = {@At("HEAD")}
   )
   private void captureOwoState(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      owo$inOwoScreen = this instanceof BaseOwoHandledScreen;
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"},
      at = {@At("TAIL")}
   )
   private void resetOwoState(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      owo$inOwoScreen = false;
   }

   @Inject(
      method = {"renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V"},
      at = {@At("HEAD")}
   )
   private void injectSlotScissors(GuiGraphics context, Slot slot, CallbackInfo ci) {
      if (owo$inOwoScreen) {
         PositionedRectangle scissorArea = ((OwoSlotExtension)slot).owo$getScissorArea();
         if (scissorArea != null) {
            GlStateManager._enableScissorTest();
            GlStateManager._scissorBox(scissorArea.x(), scissorArea.y(), scissorArea.width(), scissorArea.height());
         }
      }
   }

   @Inject(
      method = {"renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V"},
      at = {@At("RETURN")}
   )
   private void clearSlotScissors(GuiGraphics context, Slot slot, CallbackInfo ci) {
      if (owo$inOwoScreen) {
         PositionedRectangle scissorArea = ((OwoSlotExtension)slot).owo$getScissorArea();
         if (scissorArea != null) {
            GlStateManager._disableScissorTest();
         }
      }
   }

   @Inject(
      method = {"renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"},
      at = {@At("HEAD")}
   )
   private static void enableSlotDepth(GuiGraphics context, int x, int y, int z, CallbackInfo ci) {
      if (owo$inOwoScreen) {
         RenderSystem.enableDepthTest();
         context.pose().translate(0.0F, 0.0F, 300.0F);
      }
   }

   @Inject(
      method = {"renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"},
      at = {@At("TAIL")}
   )
   private static void clearSlotDepth(GuiGraphics context, int x, int y, int z, CallbackInfo ci) {
      if (owo$inOwoScreen) {
         context.pose().translate(0.0F, 0.0F, -300.0F);
      }
   }

   @ModifyVariable(
      method = {"mouseClicked(DDI)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;",
         ordinal = 0
      ),
      ordinal = 3
   )
   private int doNoThrow(int slotId, @Local Slot slot) {
      return this instanceof BaseOwoHandledScreen && slot != null ? slot.index : slotId;
   }

   @Inject(
      method = {"keyPressed(III)Z"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(II)Z"
      )},
      cancellable = true
   )
   private void closeIt(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (this instanceof BaseOwoHandledScreen) {
         if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            cir.setReturnValue(true);
         }
      }
   }
}
