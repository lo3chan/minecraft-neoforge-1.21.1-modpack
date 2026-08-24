package com.finndog.moogs_structures.client;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigButtons {
   public static final int PREVIEW_WIDTH = 55;
   public static final int DISABLE_WIDTH = 62;

   private ConfigButtons() {
   }

   public static Button preview(String url) {
      boolean hasUrl = url != null && !url.isBlank();
      Builder builder = Button.builder(Component.literal("Preview"), b -> {
         if (hasUrl) {
            openLink(url);
         }
      }).bounds(0, 0, 55, 20);
      if (!hasUrl) {
         builder.tooltip(Tooltip.create(Component.literal("No preview: this mod hasn't set a preview link (mod_slug).")));
      }

      Button button = builder.build();
      button.active = hasUrl;
      return button;
   }

   public static Button disable(boolean initialDisabled, Consumer<Boolean> onChange) {
      boolean[] state = new boolean[]{initialDisabled};
      return Button.builder(label(state[0]), b -> {
         state[0] = !state[0];
         onChange.accept(state[0]);
         b.setMessage(label(state[0]));
      }).bounds(0, 0, 62, 20).build();
   }

   private static Component label(boolean disabled) {
      return disabled ? Component.literal("Disabled").withStyle(ChatFormatting.RED) : Component.literal("Enabled").withStyle(ChatFormatting.GREEN);
   }

   public static void openLink(String url) {
      Minecraft mc = Minecraft.getInstance();
      Screen previous = mc.screen;
      mc.setScreen(new ConfirmLinkScreen(open -> {
         if (open) {
            Util.getPlatform().openUri(url);
         }

         mc.setScreen(previous);
      }, url, true));
   }
}
