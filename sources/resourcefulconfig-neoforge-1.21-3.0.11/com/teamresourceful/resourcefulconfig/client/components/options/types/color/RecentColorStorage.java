package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecentColorStorage {
   private static final Set<HsbColor> nonAlpha = new LinkedHashSet<>();
   private static final Map<HsbColor, HsbColor> alpha = new LinkedHashMap<>();

   public static void add(HsbColor color) {
      if (color.alpha() == 255) {
         nonAlpha.remove(color);
         nonAlpha.add(color);
      } else {
         nonAlpha.remove(color.withAlpha(255));
         alpha.put(color.withAlpha(255), color);
      }
   }

   public static Collection<HsbColor> getRecentColors(boolean withAlpha) {
      List<HsbColor> colors = new ArrayList<>(nonAlpha);
      if (withAlpha) {
         colors.addAll(alpha.values());
      }

      Collections.reverse(colors);
      return colors;
   }

   public static boolean hasValues() {
      return !nonAlpha.isEmpty() || !alpha.isEmpty();
   }
}
