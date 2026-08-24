package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.net.SyncServerDataPayload;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.registry.ServerRegistryKubeEvent;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistryAccess.RegistryEntry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ServerScriptManager extends ScriptManager {
   private static ServerScriptManager staticInstance;
   public final Map<ResourceKey<?>, PreTagKubeEvent> preTagEvents = new ConcurrentHashMap<>();
   public final RecipeSchemaStorage recipeSchemaStorage = new RecipeSchemaStorage(this);
   public SyncServerDataPayload serverData = null;
   public final VirtualDataPack internalDataPack = new VirtualDataPack(GeneratedDataStage.INTERNAL, this::getRegistries);
   public final VirtualDataPack registriesDataPack = new VirtualDataPack(GeneratedDataStage.REGISTRIES, this::getRegistries);
   public final Map<GeneratedDataStage, VirtualDataPack> virtualPacks = GeneratedDataStage.forScripts(stage -> new VirtualDataPack(stage, this::getRegistries));
   public final Map<ResourceLocation, Set<ResourceLocation>> serverRegistryTags = new HashMap<>();
   public boolean firstLoad = true;

   @Internal
   public static ServerScriptManager createForDataGen() {
      ServerScriptManager manager = new ServerScriptManager();
      manager.reload();
      return manager;
   }

   public static List<PackResources> createPackResources(List<PackResources> original) {
      ArrayList<PackResources> packs = new ArrayList<>(original);
      ArrayList<PackResources> filePacks = new ArrayList<>();
      KubeFileResourcePack.scanAndLoad(KubeJSPaths.DATA, filePacks);
      filePacks.sort((p1, p2) -> p1.packId().compareToIgnoreCase(p2.packId()));
      filePacks.add(new KubeFileResourcePack(PackType.SERVER_DATA));
      int beforeModsIndex = KubeFileResourcePack.findBeforeModsIndex(packs);
      int afterModsIndex = KubeFileResourcePack.findAfterModsIndex(packs);
      ServerScriptManager manager = new ServerScriptManager();
      packs.add(beforeModsIndex, manager.virtualPacks.get(GeneratedDataStage.BEFORE_MODS));
      packs.add(afterModsIndex, manager.internalDataPack);
      packs.add(afterModsIndex + 1, manager.registriesDataPack);
      packs.add(afterModsIndex + 2, manager.virtualPacks.get(GeneratedDataStage.AFTER_MODS));
      packs.addAll(afterModsIndex + 3, filePacks);
      packs.add(manager.virtualPacks.get(GeneratedDataStage.LAST));
      manager.reload();
      staticInstance = manager;
      if (!FMLLoader.isProduction()) {
         KubeJS.LOGGER
            .info("Loaded {} data packs: {}", packs.size(), packs.stream().<CharSequence>map(PackResources::packId).collect(Collectors.joining(", ")));
      }

      return packs;
   }

   public static ServerScriptManager release() {
      ServerScriptManager instance = Objects.requireNonNull(staticInstance);
      staticInstance = null;
      return instance;
   }

   private ServerScriptManager() {
      super(ScriptType.SERVER);

      try {
         if (Files.notExists(KubeJSPaths.DATA)) {
            Files.createDirectories(KubeJSPaths.DATA);
         }
      } catch (Throwable var2) {
         throw new RuntimeException("KubeJS failed to register it's script loader!", var2);
      }
   }

   @Override
   public void loadFromDirectory() {
      ConsoleJS.SERVER.startCapturingErrors();
      super.loadFromDirectory();
      if (FMLLoader.getDist().isDedicatedServer()) {
         this.loadPackFromDirectory(KubeJSPaths.LOCAL_SERVER_SCRIPTS, "local server", true);
      }
   }

   @Override
   public void loadAdditional() {
      for (BuilderBase<?> builder : RegistryObjectStorage.ALL_BUILDERS) {
         builder.generateData(this.internalDataPack);
      }

      KubeJSPlugins.forEachPlugin(this.internalDataPack, KubeJSPlugin::generateData);
      this.internalDataPack.flush();
      if (this.firstLoad) {
         this.firstLoad = false;
         if (ServerEvents.REGISTRY.hasListeners()) {
            ArrayList<BuilderBase<?>> builders = new ArrayList<>();
            final RegistryAccessContainer current = RegistryAccessContainer.current;
            RegistryAccessContainer.current = new RegistryAccessContainer(new Frozen() {
               final Map<ResourceKey<? extends Registry<?>>, Optional<Registry<?>>> registries = new HashMap<>();

               public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> registryKey) {
                  return Cast.to(this.registries.computeIfAbsent(registryKey, key -> {
                     Optional<Registry<Object>> c = current.access().registry(key);
                     return c.isPresent() ? Cast.to(c) : Optional.of(new MappedRegistry(key, Lifecycle.experimental()));
                  }));
               }

               public Stream<RegistryEntry<?>> registries() {
                  return current.access().registries();
               }
            });
            RegistryOps<JsonElement> ops = RegistryAccessContainer.current.json();
            Reference2ObjectOpenHashMap<ResourceKey<?>, Codec<?>> codecs = new Reference2ObjectOpenHashMap();

            for (RegistryData<?> reg : DataPackRegistriesHooks.getDataPackRegistries()) {
               ResourceKey key = reg.key();
               codecs.put(key, reg.elementCodec());
               if (ServerEvents.REGISTRY.hasListeners(key)) {
                  ServerEvents.REGISTRY.post(ScriptType.SERVER, key, new ServerRegistryKubeEvent(key, ops, reg.elementCodec(), builders));
               }
            }

            for (BuilderBase<?> b : List.copyOf(builders)) {
               b.createAdditionalObjects(new ServerScriptManager.AdditionalServerRegistryHandler(b.sourceLine, builders));
            }

            for (BuilderBase<?> b : builders) {
               b.generateData(this.registriesDataPack);
               if (!b.defaultTags.isEmpty()) {
                  this.serverRegistryTags.put(b.id, b.defaultTags);
               }
            }

            for (BuilderBase<?> bx : builders) {
               if (bx.registryKey == null) {
                  ConsoleJS.SERVER.error("", new KubeRuntimeException("Failed to register object '" + bx.id + "' - unknown registry").source(bx.sourceLine));
               } else {
                  try {
                     Codec<?> codec = (Codec<?>)codecs.get(bx.registryKey);
                     if (codec == null) {
                        throw new KubeRuntimeException("Don't know how to encode '" + bx.id + "' of '" + bx.registryKey.location() + "'!")
                           .source(bx.sourceLine);
                     }

                     Object obj = bx.createTransformedObject();
                     JsonElement json = (JsonElement)codec.encodeStart(ops, Cast.to(obj)).getOrThrow();
                     ResourceLocation k = bx.registryKey.location();
                     if (k.getNamespace().equals("minecraft")) {
                        this.registriesDataPack.json(ResourceLocation.fromNamespaceAndPath(bx.id.getNamespace(), k.getPath() + "/" + bx.id.getPath()), json);
                     } else {
                        this.registriesDataPack
                           .json(
                              ResourceLocation.fromNamespaceAndPath(bx.id.getNamespace(), k.getNamespace() + "/" + k.getPath() + "/" + bx.id.getPath()), json
                           );
                     }
                  } catch (Exception var11) {
                     ConsoleJS.SERVER
                        .error(
                           "",
                           new KubeRuntimeException("Failed to register object '" + bx.id + "' of registry '" + bx.registryKey.location() + "'!", var11)
                              .source(bx.sourceLine)
                        );
                  }
               }
            }

            this.registriesDataPack.flush();
            RegistryAccessContainer.current = current;
         }
      }
   }

   @Override
   public void reload() {
      this.internalDataPack.reset();

      for (VirtualDataPack pack : this.virtualPacks.values()) {
         pack.reset();
      }

      this.serverData = null;
      super.reload();
      PreTagKubeEvent.handle(this.preTagEvents);
      this.internalDataPack.flush();

      for (VirtualDataPack pack : this.virtualPacks.values()) {
         if (ServerEvents.GENERATE_DATA.hasListeners(pack.stage)) {
            ServerEvents.GENERATE_DATA.post(ScriptType.SERVER, pack.stage, pack);
         }

         pack.flush();
      }
   }

   @Override
   protected void fullReload() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         server.execute(() -> server.kjs$runCommand("reload"));
      }
   }

   public void reloadAndCapture() {
      this.reload();
      staticInstance = this;
   }

   private record AdditionalServerRegistryHandler(SourceLine sourceLine, List<BuilderBase<?>> builders) implements AdditionalObjectRegistry {
      @Override
      public <T> void add(ResourceKey<Registry<T>> registry, BuilderBase<? extends T> builder) {
         builder.sourceLine = this.sourceLine;
         builder.registryKey = registry;
         this.builders.add(builder);
      }
   }
}
