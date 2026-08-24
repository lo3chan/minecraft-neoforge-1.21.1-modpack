package dev.corgitaco.enhancedcelestials2defaultlunarevents.neoforge.datagen;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEventModifiers;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEvents;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.neoforge.datagen.providers.ECItemTagsProvider;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.neoforge.datagen.providers.ECLunarEventTagsProvider;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Cloner.Factory;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "enhancedcelestials2defaultlunarevents"
)
public class DefaultLunarEventsDataGen {
   private static RegistrySetBuilder makeBuilder() {
      ResourceKey<Registry<LunarEvent>> lunarEventKey = EnhancedCelestialsRegistry.LUNAR_EVENT_KEY;
      ResourceKey<Registry<LunarEventProbabilities>> probabilitiesKey = EnhancedCelestialsRegistry.LUNAR_EVENT_PROBABILITIES_KEY;
      ResourceKey<Registry<LunarEventModifier>> modifierKey = EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY;
      return new RegistrySetBuilder()
         .add(
            modifierKey,
            pContext -> StandardLunarEventModifiers.LUNAR_EVENT_MODIFIER_FACTORIES
               .forEach(
                  (modifierResourceKey, factory) -> pContext.register(
                     ResourceKey.create(modifierKey, modifierResourceKey.location()), factory.generate(pContext)
                  )
               )
         )
         .add(
            lunarEventKey,
            pContext -> StandardLunarEvents.LUNAR_EVENT_FACTORIES
               .forEach(
                  (lunarEventResourceKey, factory) -> pContext.register(
                     ResourceKey.create(lunarEventKey, lunarEventResourceKey.location()), factory.generate(pContext)
                  )
               )
         )
         .add(
            probabilitiesKey,
            pContext -> StandardLunarEventProbabilities.LUNAR_EVENT_PROBABILITIES_FACTORIES
               .forEach(
                  (probabilitiesResourceKey, factory) -> pContext.register(
                     ResourceKey.create(probabilitiesKey, probabilitiesResourceKey.location()), factory.generate(pContext)
                  )
               )
         );
   }

   @SubscribeEvent
   static void onDatagen(GatherDataEvent event) {
      Factory factory = new Factory();
      RegistryDataLoader.WORLDGEN_REGISTRIES.forEach(registryData -> registryData.runWithArguments(factory::addCodec));
      DataGenerator gen = event.getGenerator();
      DatapackBuiltinEntriesProvider datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(
         event.getGenerator().getPackOutput(), event.getLookupProvider(), makeBuilder(), Set.of("enhancedcelestials2defaultlunarevents")
      );
      gen.addProvider(event.includeServer(), datapackBuiltinEntriesProvider);
      CompletableFuture<Provider> lookupProvider = datapackBuiltinEntriesProvider.getRegistryProvider();
      gen.addProvider(
         event.includeServer(),
         new ECLunarEventTagsProvider(
            gen.getPackOutput(),
            EnhancedCelestialsRegistry.LUNAR_EVENT_KEY,
            lookupProvider,
            "enhancedcelestials2defaultlunarevents",
            event.getExistingFileHelper()
         )
      );
      gen.addProvider(
         event.includeServer(),
         new ECItemTagsProvider(
            gen.getPackOutput(),
            lookupProvider,
            CompletableFuture.completedFuture(blockTagKey -> Optional.empty()),
            "enhancedcelestials2defaultlunarevents",
            event.getExistingFileHelper()
         )
      );
   }
}
