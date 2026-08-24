package dev.corgitaco.enhancedcelestials2defaultlunarevents.core;

import dev.corgitaco.enhancedcelestials2core.platform.services.RegistrationService;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.EnhancedCelestialsDefaultLunarEvents;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ECSounds {
   public static final Supplier<SoundEvent> BLOOD_MOON = createSound("blood_moon");
   public static final Supplier<SoundEvent> BLUE_MOON = createSound("blue_moon");
   public static final Supplier<SoundEvent> HARVEST_MOON = createSound("harvest_moon");

   public static Supplier<SoundEvent> createSound(String location) {
      ResourceLocation soundLocation = EnhancedCelestialsDefaultLunarEvents.createLocation(location);
      return RegistrationService.INSTANCE
         .register(BuiltInRegistries.SOUND_EVENT, "enhancedcelestials2defaultlunarevents", location, () -> SoundEvent.createVariableRangeEvent(soundLocation));
   }

   public static void loadClass() {
   }
}
