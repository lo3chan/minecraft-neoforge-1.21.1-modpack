package me.flashyreese.mods.sodiumextra.common.util;

import com.mojang.blaze3d.platform.Monitor;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface ControlValueFormatterExtended extends ControlValueFormatter {
   static ControlValueFormatter resolution() {
      return v -> {
         Monitor monitor = Minecraft.getInstance().getWindow().findBestMonitor();
         if (monitor != null && monitor.getModeCount() > 0) {
            int modeIndex = Math.max(0, Math.min(v - 1, monitor.getModeCount() - 1));
            return v == 0
               ? Component.translatable("options.fullscreen.current")
               : Component.literal(monitor.getMode(modeIndex).toString().replace(" (24bit)", ""));
         } else {
            return Component.translatable("options.fullscreen.unavailable");
         }
      };
   }

   static ControlValueFormatter fogDistance() {
      return v -> {
         if (v == 0) {
            return Component.translatable("options.gamma.default");
         } else {
            return FogDistanceHelper.disablesFog(v) ? Component.translatable("options.off") : Component.translatable("options.chunks", new Object[]{v});
         }
      };
   }

   static ControlValueFormatter protectedFogDistance() {
      return v -> {
         if (v == 0) {
            return Component.translatable("options.gamma.default");
         } else {
            return FogDistanceHelper.disablesFog(v)
               ? Component.translatable("options.off")
               : Component.translatable("sodium-extra.units.blocks", new Object[]{v});
         }
      };
   }

   static ControlValueFormatter ticks() {
      return v -> Component.translatable("sodium-extra.units.ticks", new Object[]{v});
   }
}
