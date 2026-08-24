package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record LunarEventProbabilities(
   ResourceKey<LunarEvent> lunarEvent, Map<ResourceKey<Level>, LunarEventProbabilities.DimensionProbability> dimensionProbabilities
) {
   public static final Codec<LunarEventProbabilities> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceKey.codec(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).fieldOf("lunar_event").forGetter(LunarEventProbabilities::lunarEvent),
            Codec.unboundedMap(ResourceKey.codec(Registries.DIMENSION), LunarEventProbabilities.DimensionProbability.CODEC)
               .fieldOf("dimensions")
               .forGetter(LunarEventProbabilities::dimensionProbabilities)
         )
         .apply(instance, LunarEventProbabilities::new)
   );

   public record DimensionProbability(int priority, LunarEvent.SpawnRequirements spawnRequirements) {
      public static final Codec<LunarEventProbabilities.DimensionProbability> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.INT.fieldOf("priority").forGetter(LunarEventProbabilities.DimensionProbability::priority),
               LunarEvent.SpawnRequirements.CODEC.fieldOf("spawn_requirements").forGetter(LunarEventProbabilities.DimensionProbability::spawnRequirements)
            )
            .apply(instance, LunarEventProbabilities.DimensionProbability::new)
      );
   }
}
