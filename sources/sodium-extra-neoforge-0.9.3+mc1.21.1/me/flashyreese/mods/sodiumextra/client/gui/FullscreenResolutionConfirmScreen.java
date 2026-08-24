package me.flashyreese.mods.sodiumextra.client.gui;

import com.mojang.blaze3d.platform.VideoMode;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class FullscreenResolutionConfirmScreen extends ConfirmScreen {
   private static final int TIMEOUT_TICKS = 300;
   private int ticksRemaining = 300;

   public FullscreenResolutionConfirmScreen(Optional<VideoMode> previousMode) {
      super(
         accepted -> {
            if (accepted) {
               FullscreenResolutionConfirmation.keep();
            } else {
               FullscreenResolutionConfirmation.revert(previousMode);
            }

            Minecraft.getInstance().setScreen(null);
         },
         Component.translatable("sodium-extra.option.wayland_fullscreen_resolution.confirm.title"),
         Component.translatable("sodium-extra.option.wayland_fullscreen_resolution.confirm.message"),
         Component.translatable("sodium-extra.option.wayland_fullscreen_resolution.confirm.keep"),
         Component.translatable("sodium-extra.option.wayland_fullscreen_resolution.confirm.revert")
      );
   }

   public void tick() {
      super.tick();
      this.ticksRemaining--;
      if (this.ticksRemaining <= 0) {
         this.callback.accept(false);
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
