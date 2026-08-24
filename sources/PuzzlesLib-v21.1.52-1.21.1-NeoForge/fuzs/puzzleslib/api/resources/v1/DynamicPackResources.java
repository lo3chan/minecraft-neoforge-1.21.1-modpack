package fuzs.puzzleslib.api.resources.v1;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.FileUtil;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class DynamicPackResources extends AbstractModPackResources {
   public static final Map<String, PackType> PATHS_FOR_TYPE = Arrays.stream(PackType.values())
      .collect(ImmutableMap.toImmutableMap(PackType::getDirectory, Function.identity()));
   protected final DataProviderContext.Factory[] factories;
   private Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths;

   protected DynamicPackResources(DataProviderContext.Factory... factories) {
      this.factories = factories;
   }

   public static Supplier<AbstractModPackResources> create(DataProviderContext.Factory... factories) {
      return () -> new DynamicPackResources(factories);
   }

   public static Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders(
      String modId, DataProviderContext.Factory... factories
   ) {
      try {
         Stopwatch stopwatch = Stopwatch.createStarted();
         Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths = Arrays.stream(PackType.values())
            .collect(Collectors.toMap(Function.identity(), $ -> Maps.newConcurrentMap()));
         DataProviderContext context = DataProviderContext.fromModId(modId);

         for (DataProviderContext.Factory factory : factories) {
            DataProvider dataProvider = factory.apply(context);
            if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
               context = context.withRegistries(registriesDataProvider.getRegistries());
            }

            dataProvider.run(
                  (filePath, data, hashCode) -> {
                     List<String> strings = FileUtil.decomposePath(filePath.normalize().toString().replace(File.separator, "/"))
                        .result()
                        .filter(list -> list.size() >= 2)
                        .orElse(null);
                     if (strings != null) {
                        PackType packType = PATHS_FOR_TYPE.get(strings.getFirst());
                        Objects.requireNonNull(packType, () -> "pack type for directory %s is null".formatted(strings.getFirst()));
                        String path = strings.stream().skip(2L).collect(Collectors.joining("/"));
                        ResourceLocation resourceLocation = ResourceLocation.tryBuild(strings.get(1), path);
                        if (resourceLocation != null) {
                           paths.get(packType).put(resourceLocation, () -> new ByteArrayInputStream(data));
                        }
                     }
                  }
               )
               .join();
         }

         Iterator<Entry<PackType, Map<ResourceLocation, IoSupplier<InputStream>>>> iterator = paths.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> entry = iterator.next();
            if (!entry.getValue().isEmpty()) {
               entry.setValue(ImmutableMap.copyOf(entry.getValue()));
            } else {
               iterator.remove();
            }
         }

         PuzzlesLib.LOGGER.info("Data generation for dynamic pack resources provided by '{}' took {}ms", modId, stopwatch.stop().elapsed().toMillis());
         return Maps.immutableEnumMap(paths);
      } catch (Throwable var11) {
         PuzzlesLib.LOGGER.warn("Unable to complete data generation for dynamic pack resources provided by '{}'", modId, var11);
         return Collections.emptyMap();
      }
   }

   protected Map<ResourceLocation, IoSupplier<InputStream>> getPathsForType(PackType packType) {
      Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths = this.paths;
      if (paths == null) {
         paths = this.paths = this.generatePathsFromProviders();
      }

      return paths.getOrDefault(packType, Collections.emptyMap());
   }

   protected Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders() {
      return generatePathsFromProviders(this.getNamespace(), this.factories);
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      return this.getPathsForType(packType).get(location);
   }

   @Override
   public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
      this.getPathsForType(packType)
         .entrySet()
         .stream()
         .filter(entry -> entry.getKey().getNamespace().equals(namespace) && entry.getKey().getPath().startsWith(path))
         .forEach(entry -> resourceOutput.accept(entry.getKey(), entry.getValue()));
   }

   @Override
   public Set<String> getNamespaces(PackType packType) {
      return this.getPathsForType(packType).keySet().stream().<String>map(ResourceLocation::getNamespace).collect(Collectors.toSet());
   }
}
