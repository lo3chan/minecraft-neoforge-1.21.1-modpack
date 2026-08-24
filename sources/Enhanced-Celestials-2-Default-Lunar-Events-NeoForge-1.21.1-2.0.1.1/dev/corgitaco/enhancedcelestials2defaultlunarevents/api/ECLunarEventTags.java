package dev.corgitaco.enhancedcelestials2defaultlunarevents.api;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.EnhancedCelestialsDefaultLunarEvents;
import net.minecraft.tags.TagKey;

public class ECLunarEventTags {
   public static final TagKey<LunarEvent> BLOOD_MOON = createEventTag("blood_moon");
   public static final TagKey<LunarEvent> BLUE_MOON = createEventTag("blue_moon");
   public static final TagKey<LunarEvent> HARVEST_MOON = createEventTag("harvest_moon");
   public static final TagKey<LunarEvent> SUPER_MOON = createEventTag("super_moon");

   private static TagKey<LunarEvent> createEventTag(String id) {
      return TagKey.create(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, EnhancedCelestialsDefaultLunarEvents.createLocation(id));
   }

   public static void loadClass() {
   }
}
