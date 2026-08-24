package dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent;

import com.google.common.collect.ImmutableMap;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent.SpawnRequirements;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventProbabilities.DimensionProbability;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.MinNightsBetweenRule;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.ValidMoonPhaseRule;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class StandardLunarEventProbabilities {
   public static final Map<ResourceKey<LunarEventProbabilities>, StandardLunarEventProbabilities.LunarEventProbabilitiesFactory> LUNAR_EVENT_PROBABILITIES_FACTORIES = new Reference2ObjectOpenHashMap();
   public static final ResourceKey<LunarEventProbabilities> SUPER_MOON = createProbabilities(
      StandardLunarEvents.SUPER_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.SUPER_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  5, List.of(Holder.direct(new MinNightsBetweenRule(20)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_SUPER_MOON_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> BLOOD_MOON = createProbabilities(
      StandardLunarEvents.BLOOD_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.BLOOD_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  10, List.of(Holder.direct(new MinNightsBetweenRule(4)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> SUPER_BLOOD_MOON = createProbabilities(
      StandardLunarEvents.SUPER_BLOOD_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.SUPER_BLOOD_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  5, List.of(Holder.direct(new MinNightsBetweenRule(20)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_SUPER_MOON_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> HARVEST_MOON = createProbabilities(
      StandardLunarEvents.HARVEST_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.HARVEST_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  10, List.of(Holder.direct(new MinNightsBetweenRule(4)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> SUPER_HARVEST_MOON = createProbabilities(
      StandardLunarEvents.SUPER_HARVEST_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.SUPER_HARVEST_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  5, List.of(Holder.direct(new MinNightsBetweenRule(20)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_SUPER_MOON_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> BLUE_MOON = createProbabilities(
      StandardLunarEvents.BLUE_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.BLUE_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  10, List.of(Holder.direct(new MinNightsBetweenRule(4)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_PHASES)))
               )
            )
         )
      )
   );
   public static final ResourceKey<LunarEventProbabilities> SUPER_BLUE_MOON = createProbabilities(
      StandardLunarEvents.SUPER_BLUE_MOON,
      () -> new LunarEventProbabilities(
         StandardLunarEvents.SUPER_BLUE_MOON,
         ImmutableMap.of(
            Level.OVERWORLD,
            new DimensionProbability(
               0,
               new SpawnRequirements(
                  5, List.of(Holder.direct(new MinNightsBetweenRule(20)), Holder.direct(new ValidMoonPhaseRule(StandardLunarEvents.DEFAULT_SUPER_MOON_PHASES)))
               )
            )
         )
      )
   );

   public static ResourceKey<LunarEventProbabilities> createProbabilities(ResourceKey<LunarEvent> lunarEvent, Supplier<LunarEventProbabilities> probabilities) {
      ResourceKey<LunarEventProbabilities> key = ResourceKey.create(EnhancedCelestialsRegistry.LUNAR_EVENT_PROBABILITIES_KEY, lunarEvent.location());
      LUNAR_EVENT_PROBABILITIES_FACTORIES.put(key, context -> probabilities.get());
      return key;
   }

   public static void loadClass() {
   }

   @FunctionalInterface
   public interface LunarEventProbabilitiesFactory {
      LunarEventProbabilities generate(BootstrapContext<LunarEventProbabilities> var1);
   }
}
