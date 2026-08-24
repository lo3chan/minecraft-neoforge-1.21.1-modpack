package com.finndog.moogs_structures.client;

import com.finndog.moogs_structures.config.MslConfig;
import com.finndog.moogs_structures.mixins.client.ScreenInvoker;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class SupportLinks {
   private static final int H = 20;
   private static final int DISCORD_W = 20;
   private static final int KOFI_W = 20;
   private static final int GAP = 4;
   private static final int PAD = 3;
   private static final int TOP = 2;
   private static final String DISCORD_ID = "discord";
   private static final String KOFI_ID = "kofi";
   private static final String DISCORD_URL = "https://discord.gg/S5nffJbuvA";
   private static final String KOFI_URL = "https://ko-fi.com/finndog";
   private static final ResourceLocation DISCORD_SPRITE = ResourceLocation.fromNamespaceAndPath("moogs_structures", "discord");
   private static final ResourceLocation KOFI_SPRITE = ResourceLocation.fromNamespaceAndPath("moogs_structures", "kofi");

   private SupportLinks() {
   }

   public static void addTo(Screen screen) {
      MslConfig cfg = MslConfig.get();
      int right = screen.width - 3;
      right = maybeAdd(screen, cfg, right, 20, "discord", DISCORD_SPRITE, "https://discord.gg/S5nffJbuvA", "Join the Discord");
      maybeAdd(screen, cfg, right, 20, "kofi", KOFI_SPRITE, "https://ko-fi.com/finndog", "Support on Ko-fi");
   }

   private static int maybeAdd(Screen screen, MslConfig cfg, int right, int w, String id, ResourceLocation sprite, String url, String tooltip) {
      if (cfg.isButtonHidden(id)) {
         return right;
      } else {
         int x = right - w;
         ((ScreenInvoker)screen).msl$addRenderableWidget(new SupportButton(x, 2, w, 20, sprite, url, Component.literal(tooltip), id));
         return x - 4;
      }
   }
}
