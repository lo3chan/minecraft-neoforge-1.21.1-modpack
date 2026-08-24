package me.flashyreese.mods.sodiumextra.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import me.flashyreese.mods.sodiumextra.client.FrameCounter;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class SodiumExtraHud {
   private final List<Component> textList = new ObjectArrayList();
   private final Minecraft client = Minecraft.getInstance();
   private final FrameCounter stats = FrameCounter.getInstance();

   public void onStartTick(Minecraft client) {
      this.textList.clear();
      if (SodiumExtraClientMod.options().extraSettings.showFps) {
         int currentFPS = FrameCounter.getInstance().getSmoothFps();
         Component text = Component.translatable("sodium-extra.overlay.fps", new Object[]{currentFPS});
         if (SodiumExtraClientMod.options().extraSettings.showFPSExtended) {
            text = Component.literal(
               String.format(
                  "%s %s",
                  text.getString(),
                  Component.translatable(
                        "sodium-extra.overlay.fps_extended",
                        new Object[]{this.stats.getAverageFps(), this.stats.getOnePercentLowFps(), this.stats.getPointOnePercentLowFps()}
                     )
                     .getString()
               )
            );
         }

         this.textList.add(text);
      }

      if (SodiumExtraClientMod.options().extraSettings.showCoords && this.client.player != null) {
         Vec3 pos = this.client.player.position();
         Component text = Component.translatable(
            "sodium-extra.overlay.coordinates", new Object[]{String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)}
         );
         if (this.client.showOnlyReducedInfo()) {
            text = Component.translatable("sodium-extra.overlay.coordinates_unavailable");
         }

         this.textList.add(text);
      }
   }

   public void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      if (!this.client.getDebugOverlay().showDebugScreen() && !this.client.options.hideGui) {
         SodiumExtraGameOptions.OverlayCorner overlayCorner = SodiumExtraClientMod.options().extraSettings.overlayCorner;
         int y = overlayCorner != SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT && overlayCorner != SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT
            ? 2
            : this.client.getWindow().getGuiScaledHeight() - 9 - 2;

         for (Component text : this.textList) {
            int x;
            if (overlayCorner != SodiumExtraGameOptions.OverlayCorner.TOP_RIGHT && overlayCorner != SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
               x = 2;
            } else {
               x = this.client.getWindow().getGuiScaledWidth() - this.client.font.width(text) - 2;
            }

            this.drawString(guiGraphics, text, x, y);
            if (overlayCorner != SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT && overlayCorner != SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
               y += 9 + 2;
            } else {
               y -= 9 + 2;
            }
         }
      }
   }

   private void drawString(GuiGraphics guiGraphics, Component text, int x, int y) {
      int textColor = -1;
      if (SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.BACKGROUND) {
         guiGraphics.fill(x - 1, y - 1, x + this.client.font.width(text) + 1, y + 9 + 1, -1873784752);
      }

      guiGraphics.drawString(
         this.client.font, text, x, y, textColor, SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.SHADOW
      );
   }
}
