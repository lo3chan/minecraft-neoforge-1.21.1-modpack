package com.iafenvoy.origins.screen.badge;

import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public final class BadgeTooltipManager {
   private static final Map<MapCodec<? extends Badge>, BadgeTooltipManager.Provider<Badge>> PROVIDERS = new LinkedHashMap<>();

   public static <T extends Badge> void register(MapCodec<T> codec, BadgeTooltipManager.Provider<T> provider) {
      PROVIDERS.put(codec, provider);
   }

   public static List<ClientTooltipComponent> getTooltipComponents(Badge badge, Power power, Font font, int widthLimit, float delta) {
      for (Entry<MapCodec<? extends Badge>, BadgeTooltipManager.Provider<Badge>> entry : PROVIDERS.entrySet()) {
         if (Objects.equals(entry.getKey(), badge.codec())) {
            return entry.getValue().getTooltipComponents(badge, power, font, widthLimit, delta);
         }
      }

      return List.of();
   }

   public interface Provider<T extends Badge> {
      List<ClientTooltipComponent> getTooltipComponents(T var1, Power var2, Font var3, int var4, float var5);
   }
}
