package dev.worldgen.deeper.oceans.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.apollib.codec.ApollibCodecs;
import dev.worldgen.apollib.config.ApollibCopyable;

public class ConfigState implements ApollibCopyable<ConfigState> {
   public static final ConfigState DEFAULT_STATE = new ConfigState(2.0, -20, true);
   public static final Codec<ConfigState> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ApollibCodecs.optionalCommented(Codec.DOUBLE, 2.0, "depth_multiplier", "How much deeper the oceans should be relative to vanilla.")
               .forGetter(state -> state.depthMultiplier),
            ApollibCodecs.optionalCommented(
                  Codec.INT, -20, "monument_offset", "How much vanilla Ocean Monuments should be offset to compensate for deeper oceans."
               )
               .forGetter(state -> state.monumentOffset),
            ApollibCodecs.optionalCommented(Codec.BOOL, true, "disable_deep_ocean_trial_chambers", "Disables Trial Chamber spawning in Deep Ocean biomes.")
               .forGetter(state -> state.disableDeepOceanTrialChambers)
         )
         .apply(instance, ConfigState::new)
   );
   public double depthMultiplier;
   public int monumentOffset;
   public boolean disableDeepOceanTrialChambers;

   public ConfigState(double depthMultiplier, int monumentOffset, boolean disableDeepOceanTrialChambers) {
      this.depthMultiplier = depthMultiplier;
      this.monumentOffset = monumentOffset;
      this.disableDeepOceanTrialChambers = disableDeepOceanTrialChambers;
   }

   public ConfigState copy() {
      return new ConfigState(this.depthMultiplier, this.monumentOffset, this.disableDeepOceanTrialChambers);
   }
}
