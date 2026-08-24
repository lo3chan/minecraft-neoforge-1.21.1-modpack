package dev.worldgen.deeper.oceans;

import dev.worldgen.apollib.Apollib;
import dev.worldgen.apollib.config.ApollibConfigHolder;
import dev.worldgen.apollib.registry.ApollibRegistrar;
import dev.worldgen.deeper.oceans.config.ConfigState;
import dev.worldgen.deeper.oceans.worldgen.densityfunction.DepthMultiplier;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.LithostitchedPlacementConditions;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeeperOceans {
   public static final String MOD_ID = "deeper_oceans";
   public static final Logger LOGGER = LoggerFactory.getLogger("deeper_oceans");
   public static final ApollibConfigHolder<ConfigState> CONFIG = Apollib.createConfigHolder(
      id("deeper_oceans"), ApollibConfigHolder.CONFIG_DIRECTORY.resolve("deeper_oceans.json"), ConfigState.CODEC, ConfigState.DEFAULT_STATE
   );
   public static final ApollibRegistrar REGISTRAR = Apollib.createRegistrar("deeper_oceans");

   public static void init() {
      CONFIG.load();
      REGISTRAR.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, "depth_multiplier", DepthMultiplier.DATA_CODEC);
      REGISTRAR.registerAll();
      AddWorldgenModifiersEvent.EVENT
         .register(
            (AddWorldgenModifiersEvent)(registries, consumer) -> {
               if (((ConfigState)CONFIG.getState()).disableDeepOceanTrialChambers) {
                  Optional<Named<Biome>> deepOceans = Lithostitched.registry(registries, Registries.BIOME).getTag(BiomeTags.IS_DEEP_OCEAN);
                  Optional<Reference<Structure>> trialChambers = Lithostitched.registry(registries, Registries.STRUCTURE)
                     .getHolder(BuiltinStructures.TRIAL_CHAMBERS);
                  if (!deepOceans.isEmpty() && !trialChambers.isEmpty()) {
                     consumer.accept(
                        id("disable_deep_ocean_trial_chambers"),
                        WorldgenModifier.builder()
                           .setStructureSpawnCondition(
                              (Holder)trialChambers.get(),
                              LithostitchedPlacementConditions.not(LithostitchedPlacementConditions.inBiome((HolderSet)deepOceans.get())),
                              true
                           )
                     );
                  }
               }
            }
         );
   }

   public static ResourceLocation id(String name) {
      return ResourceLocation.fromNamespaceAndPath("deeper_oceans", name);
   }
}
