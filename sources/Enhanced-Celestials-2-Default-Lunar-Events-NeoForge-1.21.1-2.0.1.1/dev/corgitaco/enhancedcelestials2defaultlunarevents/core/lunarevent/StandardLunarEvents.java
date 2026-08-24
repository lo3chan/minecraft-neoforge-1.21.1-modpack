package dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.EnhancedCelestialsDefaultLunarEvents;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class StandardLunarEvents {
   public static final Map<ResourceKey<LunarEvent>, StandardLunarEvents.LunarEventFactory> LUNAR_EVENT_FACTORIES = new Reference2ObjectOpenHashMap();
   public static final Collection<Integer> DEFAULT_PHASES = IntArraySet.of(new int[]{0, 1, 2, 3, 4, 5, 6, 7});
   public static final Collection<Integer> DEFAULT_SUPER_MOON_PHASES = IntArraySet.of(0);
   public static final ResourceKey<LunarEvent> SUPER_MOON = createEvent(
      "super_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.SUPER_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.SUPER_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.SUPER_MOON_MOON_SIZE,
            StandardLunarEventModifiers.SUPER_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.SUPER_MOON_SLIMES_SPAWN_EVERYWHERE
         )
      )
   );
   public static final ResourceKey<LunarEvent> BLOOD_MOON = createEvent(
      "blood_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.BLOOD_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.BLOOD_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.BLOOD_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.BLOOD_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.BLOOD_MOON_NAME_COLOR,
            StandardLunarEventModifiers.BLOOD_MOON_SPAWN_CATEGORY_MULTIPLIER,
            StandardLunarEventModifiers.BLOOD_MOON_FORCE_SURFACE_SPAWNING,
            StandardLunarEventModifiers.BLOOD_MOON_BLOCK_SLEEPING,
            StandardLunarEventModifiers.BLOOD_MOON_VILLAGE_SIEGE_PROBABILITY,
            StandardLunarEventModifiers.BLOOD_MOON_MOB_SPAWN_DISTANCES
         )
      )
   );
   public static final ResourceKey<LunarEvent> SUPER_BLOOD_MOON = createEvent(
      "super_blood_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_MOON_SIZE,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_NAME_COLOR,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_SPAWN_CATEGORY_MULTIPLIER,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_FORCE_SURFACE_SPAWNING,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_BLOCK_SLEEPING,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_VILLAGE_SIEGE_PROBABILITY,
            StandardLunarEventModifiers.SUPER_BLOOD_MOON_MOB_SPAWN_DISTANCES
         )
      )
   );
   public static final ResourceKey<LunarEvent> HARVEST_MOON = createEvent(
      "harvest_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.HARVEST_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.HARVEST_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.HARVEST_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.HARVEST_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.HARVEST_MOON_NAME_COLOR,
            StandardLunarEventModifiers.HARVEST_MOON_ITEM_DROP
         )
      )
   );
   public static final ResourceKey<LunarEvent> SUPER_HARVEST_MOON = createEvent(
      "super_harvest_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_MOON_SIZE,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_NAME_COLOR,
            StandardLunarEventModifiers.SUPER_HARVEST_MOON_ITEM_DROP
         )
      )
   );
   public static final ResourceKey<LunarEvent> BLUE_MOON = createEvent(
      "blue_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.BLUE_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.BLUE_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.BLUE_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.BLUE_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.BLUE_MOON_NAME_COLOR,
            StandardLunarEventModifiers.BLUE_MOON_MOB_EFFECTS,
            StandardLunarEventModifiers.BLUE_MOON_ANVIL_COST,
            StandardLunarEventModifiers.BLUE_MOON_ENCHANTMENT_COST,
            StandardLunarEventModifiers.BLUE_MOON_EXPERIENCE,
            StandardLunarEventModifiers.BLUE_MOON_BEACON_RADIUS
         )
      )
   );
   public static final ResourceKey<LunarEvent> SUPER_BLUE_MOON = createEvent(
      "super_blue_moon",
      context -> new LunarEvent(
         modifiers(
            context,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_SKY_LIGHT_COLOR,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_MOON_TEXTURE_COLOR,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_MOON_SIZE,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_SOUND_TRACK,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_TEXT_COMPONENTS,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_NAME_COLOR,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_MOB_EFFECTS,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_ANVIL_COST,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_ENCHANTMENT_COST,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_EXPERIENCE,
            StandardLunarEventModifiers.SUPER_BLUE_MOON_BEACON_RADIUS
         )
      )
   );

   public static ResourceKey<LunarEvent> createEvent(String id, Function<BootstrapContext<LunarEvent>, LunarEvent> event) {
      ResourceKey<LunarEvent> lunarEventResourceKey = ResourceKey.create(
         EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, EnhancedCelestialsDefaultLunarEvents.createLocation(id)
      );
      LUNAR_EVENT_FACTORIES.put(lunarEventResourceKey, event::apply);
      return lunarEventResourceKey;
   }

   @SafeVarargs
   private static List<Holder<LunarEventModifier>> modifiers(BootstrapContext<LunarEvent> context, ResourceKey<LunarEventModifier>... modifierKeys) {
      HolderGetter<LunarEventModifier> modifierLookup = context.lookup(EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY);
      return Arrays.stream(modifierKeys).<Holder<LunarEventModifier>>map(modifierLookup::getOrThrow).collect(Collectors.toUnmodifiableList());
   }

   public static void loadClass() {
   }

   @FunctionalInterface
   public interface LunarEventFactory {
      LunarEvent generate(BootstrapContext<LunarEvent> var1);
   }
}
