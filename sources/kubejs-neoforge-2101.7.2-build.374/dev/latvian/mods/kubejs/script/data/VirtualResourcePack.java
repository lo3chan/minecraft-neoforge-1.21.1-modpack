package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.generator.KubeResourceGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VirtualResourcePack extends AbstractPackResources implements KubeResourceGenerator, ExportablePackResources {
   public final ScriptType scriptType;
   public final PackType packType;
   public final GeneratedDataStage stage;
   public final Supplier<RegistryAccessContainer> registries;
   public final String info;
   public final Component component;
   private final Map<ResourceLocation, GeneratedData> locationToData;
   private final Map<String, GeneratedData> pathToData;
   private final Set<String> namespaces;

   public VirtualResourcePack(ScriptType scriptType, PackType packType, GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
      super(KubeFileResourcePack.PACK_LOCATION_INFO);
      this.scriptType = scriptType;
      this.packType = packType;
      this.stage = stage;
      this.registries = registries;
      this.info = stage.displayName + ", " + packType.getDirectory();
      this.component = Component.empty().append(TextIcons.NAME).append(" (" + this.info + ", )");
      this.locationToData = new HashMap<>();
      this.pathToData = new HashMap<>();
      this.namespaces = new HashSet<>();
   }

   public void reset() {
      this.locationToData.clear();
      this.pathToData.clear();
      this.namespaces.clear();
   }

   @Override
   public RegistryAccessContainer getRegistries() {
      return this.registries.get();
   }

   @Override
   public void add(GeneratedData data) {
      this.locationToData.put(data.id(), data);
      this.pathToData.put(this.packType.getDirectory() + "/" + data.id().getNamespace() + "/" + data.id().getPath(), data);
      this.namespaces.add(data.id().getNamespace());
      if (DevProperties.get().virtualPackOutput) {
         this.scriptType.console.info("Registered virtual file [" + this.info + "] '" + data.id() + "': " + data);
      }
   }

   @Nullable
   @Override
   public GeneratedData getGenerated(ResourceLocation id) {
      return this.locationToData.get(id);
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... path) {
      String var2 = path.length == 1 ? path[0] : "";

      return switch (var2) {
         case "pack.mcmeta" -> GeneratedData.PACK_META;
         case "pack.png" -> GeneratedData.PACK_ICON;
         default -> null;
      };
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
      if (type != this.packType) {
         return null;
      } else {
         GeneratedData s = this.locationToData.get(location);
         if (s != null) {
            if (DevProperties.get().virtualPackOutput) {
               this.scriptType.console.info("Served virtual file [" + this.info + "] '" + location + "': " + s);
            }

            return s;
         } else {
            return null;
         }
      }
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput visitor) {
      if (!path.endsWith("/")) {
         path = path + "/";
      }

      for (ResourceLocation r : this.locationToData.keySet()) {
         if (r.getNamespace().equals(namespace) && r.getPath().startsWith(path)) {
            visitor.accept(r, this.getResource(packType, r));
         }
      }
   }

   public Set<String> getNamespaces(PackType type) {
      return Set.copyOf(this.namespaces);
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
      return null;
   }

   @Override
   public String toString() {
      return this.packId();
   }

   @NotNull
   public String packId() {
      return "KubeJS Virtual Resource Pack [" + this.info + "]";
   }

   @Override
   public String exportPath() {
      return this.packType.getDirectory() + "/" + this.stage.name;
   }

   @Override
   public void export(Path root) throws IOException {
      for (Entry<String, GeneratedData> file : this.pathToData.entrySet()) {
         Path path = root.resolve(file.getKey());
         Path parent = path.getParent();
         if (Files.notExists(parent)) {
            Files.createDirectories(parent);
         }

         Files.write(path, file.getValue().data().get());
      }
   }

   public void close() {
      if (!FMLLoader.isProduction()) {
         KubeJS.LOGGER.info("Closed {}", this.packId());
      }
   }
}
