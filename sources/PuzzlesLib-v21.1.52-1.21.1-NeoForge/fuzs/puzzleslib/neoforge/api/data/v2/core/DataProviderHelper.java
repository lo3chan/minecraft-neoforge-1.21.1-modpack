package fuzs.puzzleslib.neoforge.api.data.v2.core;

import com.google.common.base.Function;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.ModPackMetadataProvider;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.ArrayUtils;

public final class DataProviderHelper {
   private DataProviderHelper() {
   }

   public static void registerDataProviders(String modId, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
      registerDataProviders(modId, new RegistrySetBuilder(), dataProviderFactories);
   }

   public static void registerDataProviders(ResourceLocation identifier, PackType packType, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
      registerDataProviders(identifier, packType, new RegistrySetBuilder(), dataProviderFactories);
   }

   public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
      registerDataProviders(
         modId,
         registrySetBuilder,
         dataProviderFactories,
         factory -> (event, packOutput, lookupProvider) -> factory.apply(NeoForgeDataProviderContext.fromEvent(event, packOutput, lookupProvider))
      );
   }

   public static void registerDataProviders(
      ResourceLocation identifier, PackType packType, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... dataProviderFactories
   ) {
      registerDataProviders(
         identifier,
         packType,
         registrySetBuilder,
         (NeoForgeDataProviderContext.Factory[])ArrayUtils.add(
            dataProviderFactories, (NeoForgeDataProviderContext.Factory)context -> new ModPackMetadataProvider(packType, context)
         ),
         factory -> (event, packOutput, lookupProvider) -> factory.apply(NeoForgeDataProviderContext.fromEvent(event, packOutput, lookupProvider))
      );
   }

   private static <T> void registerDataProviders(
      String modId, RegistrySetBuilder registrySetBuilder, T[] dataProviderFactories, Function<T, DataProviderHelper.Factory> factoryConverter
   ) {
      if (ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
         NeoForgeModContainerHelper.getOptionalModEventBus(modId)
            .ifPresent(
               eventBus -> eventBus.addListener(
                  event -> addDataProviders(
                     event, registrySetBuilder, dataProviderFactories, factoryConverter, event.getGenerator().getPackOutput(), event::addProvider
                  )
               )
            );
      }
   }

   private static <T> void registerDataProviders(
      ResourceLocation identifier,
      PackType packType,
      RegistrySetBuilder registrySetBuilder,
      T[] dataProviderFactories,
      Function<T, DataProviderHelper.Factory> factoryConverter
   ) {
      if (ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
         NeoForgeModContainerHelper.getOptionalModEventBus(identifier.getNamespace())
            .ifPresent(
               eventBus -> eventBus.addListener(
                  event -> {
                     Path path = event.getGenerator().getPackOutput().getOutputFolder();
                     PackOutput packOutput = new PackOutput(
                        event.getGenerator()
                           .getPackOutput()
                           .getOutputFolder()
                           .resolve(packType.getDirectory())
                           .resolve(identifier.getNamespace())
                           .resolve(packType == PackType.CLIENT_RESOURCES ? "resourcepacks" : "datapacks")
                           .resolve(identifier.getPath())
                     );
                     PackGenerator packGenerator = event.getGenerator()
                        .getPackGenerator(true, identifier.toString(), path.relativize(packOutput.getOutputFolder()).toString());
                     addDataProviders(
                        event,
                        registrySetBuilder,
                        dataProviderFactories,
                        factoryConverter,
                        packOutput,
                        dataProvider -> packGenerator.addProvider(packOutputX -> dataProvider)
                     );
                  }
               )
            );
      }
   }

   private static <T> void addDataProviders(
      GatherDataEvent event,
      RegistrySetBuilder registrySetBuilder,
      T[] dataProviderFactories,
      Function<T, DataProviderHelper.Factory> factoryConverter,
      PackOutput packOutput,
      Consumer<DataProvider> dataProviderConsumer
   ) {
      if (!registrySetBuilder.getEntryKeys().isEmpty()) {
         event.createDatapackRegistryObjects(registrySetBuilder, (Set)null);
      }

      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();

      for (T dataProviderFactory : dataProviderFactories) {
         DataProvider dataProvider = ((DataProviderHelper.Factory)factoryConverter.apply(dataProviderFactory)).apply(event, packOutput, lookupProvider);
         if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
            lookupProvider = registriesDataProvider.getRegistries();
         }

         dataProviderConsumer.accept(dataProvider);
      }
   }

   @FunctionalInterface
   private interface Factory {
      DataProvider apply(GatherDataEvent var1, PackOutput var2, CompletableFuture<Provider> var3);
   }
}
