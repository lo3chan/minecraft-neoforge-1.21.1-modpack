package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.api.AetherAdvancementSoundOverrides;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AdvancementToast.class})
public class AdvancementToastMixin {
   @Final
   @Shadow
   private AdvancementHolder advancement;
   @Shadow
   private boolean playedSound;

   @Inject(
      at = {@At(
         value = "FIELD",
         target = "net/minecraft/client/gui/components/toasts/AdvancementToast.playedSound:Z"
      )},
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/components/toasts/ToastComponent;J)Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;"}
   )
   private void render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible, CallbackInfoReturnable<Visibility> cir) {
      if (!this.playedSound && this.advancement != null) {
         SoundEvent soundOverride = AetherAdvancementSoundOverrides.retrieveOverride(this.advancement);
         if (soundOverride != null) {
            if (soundOverride != SoundEvents.EMPTY) {
               toastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forMusic(soundOverride));
            }

            this.playedSound = true;
         }
      }
   }
}
