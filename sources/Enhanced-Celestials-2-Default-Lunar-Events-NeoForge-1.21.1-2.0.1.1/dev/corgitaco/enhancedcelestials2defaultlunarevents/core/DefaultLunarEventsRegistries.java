package dev.corgitaco.enhancedcelestials2defaultlunarevents.core;

import dev.corgitaco.enhancedcelestials2defaultlunarevents.api.ECItemTags;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.api.ECLunarEventTags;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEventModifiers;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEvents;

public record DefaultLunarEventsRegistries() {
   public static void loadClasses() {
      ECSounds.loadClass();
      StandardLunarEventModifiers.loadClass();
      StandardLunarEvents.loadClass();
      StandardLunarEventProbabilities.loadClass();
      ECLunarEventTags.loadClass();
      ECItemTags.loadClass();
   }
}
