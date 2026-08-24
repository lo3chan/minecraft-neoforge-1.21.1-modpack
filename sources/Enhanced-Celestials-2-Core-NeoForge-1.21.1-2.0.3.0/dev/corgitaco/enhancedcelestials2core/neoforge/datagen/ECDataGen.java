package dev.corgitaco.enhancedcelestials2core.neoforge.datagen;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarEvents;
import dev.corgitaco.enhancedcelestials2core.neoforge.datagen.providers.ECLunarEventTagsProvider;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Cloner.Factory;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "enhancedcelestials2core"
)
public class ECDataGen {
   private static RegistrySetBuilder makeBuilder(boolean useMinecraftNameSpace) {
      ResourceKey<Registry<LunarEvent>> lunarEventKey = useMinecraftNameSpace
         ? ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("lunar/event"))
         : EnhancedCelestialsRegistry.LUNAR_EVENT_KEY;
      ResourceKey<Registry<LunarDimensionSettings>> dimensionSettingsKey = useMinecraftNameSpace
         ? ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("lunar/dimension_settings"))
         : EnhancedCelestialsRegistry.LUNAR_DIMENSION_SETTINGS_KEY;
      return new RegistrySetBuilder()
         .add(
            lunarEventKey,
            pContext -> DefaultLunarEvents.LUNAR_EVENT_FACTORIES
               .forEach(
                  (lunarEventResourceKey, factory) -> pContext.register(
                     ResourceKey.create(lunarEventKey, lunarEventResourceKey.location()), factory.generate(pContext)
                  )
               )
         )
         .add(
            dimensionSettingsKey,
            pContext -> DefaultLunarDimensionSettings.LUNAR_DIMENSION_SETTINGS_FACTORIES
               .forEach(
                  (lunarEventResourceKey, factory) -> pContext.register(
                     ResourceKey.create(dimensionSettingsKey, lunarEventResourceKey.location()), factory.generate(pContext)
                  )
               )
         );
   }

   @SubscribeEvent
   static void onDatagen(GatherDataEvent event) {
      EnhancedCelestials.commonSetup();
      Factory factory = new Factory();
      RegistryDataLoader.WORLDGEN_REGISTRIES.forEach(registryData -> registryData.runWithArguments(factory::addCodec));
      DataGenerator gen = event.getGenerator();
      DatapackBuiltinEntriesProvider datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(
         event.getGenerator().getPackOutput(), event.getLookupProvider(), makeBuilder(false), Set.of("minecraft", "enhancedcelestials2core")
      );
      gen.addProvider(event.includeServer(), datapackBuiltinEntriesProvider);
      CompletableFuture<Provider> lookupProvider = datapackBuiltinEntriesProvider.getRegistryProvider();
      gen.addProvider(
         event.includeServer(),
         new ECLunarEventTagsProvider(
            gen.getPackOutput(), false, EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, lookupProvider, "enhancedcelestials2core", event.getExistingFileHelper()
         )
      );
   }
}
