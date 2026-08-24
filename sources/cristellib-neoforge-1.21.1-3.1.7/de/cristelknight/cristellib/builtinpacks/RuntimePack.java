package de.cristelknight.cristellib.builtinpacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.util.JsonHelper;
import de.cristelknight.cristellib.util.runtimepack.RuntimePackUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.flag.FeatureFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimePack implements PackResources {
   public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
   private final Lock waiting = new ReentrantLock();
   private final Map<ResourceLocation, Supplier<byte[]>> data = new ConcurrentHashMap<>();
   private final Map<ResourceLocation, Supplier<byte[]>> assets = new ConcurrentHashMap<>();
   private final Map<List<String>, Supplier<byte[]>> root = new ConcurrentHashMap<>();
   public final int packVersion;
   private final String id;
   private final PackLocationInfo metadata;

   public RuntimePack(ResourceLocation id, int version, String description, @Nullable InputStream imageStream) {
      this.packVersion = version;
      this.id = id.toString();
      this.metadata = new PackLocationInfo(this.id, Component.literal(description), new BuiltinResourcePackSource(), Optional.empty());
      if (imageStream != null) {
         byte[] image = RuntimePackUtil.extractImageBytes(imageStream);
         if (image != null) {
            this.addRootResource("pack.png", image);
         }
      }

      if (!this.hasRootResource("pack.mcmeta")) {
         JsonObject object = new JsonObject();
         JsonObject pack = new JsonObject();
         pack.addProperty("pack_format", this.packVersion);
         pack.addProperty("min_format", this.packVersion);
         pack.addProperty("max_format", this.packVersion);
         pack.addProperty("description", description);
         object.add("pack", pack);
         this.addRootResource("pack.mcmeta", RuntimePackUtil.serializeJson(object));
      }
   }

   protected Map<ResourceLocation, Supplier<byte[]>> getSys(PackType side) {
      return side == PackType.CLIENT_RESOURCES ? this.assets : this.data;
   }

   public byte[] addStructureSet(ResourceLocation identifier, JsonObject set) {
      return this.addDataForJsonLocation("worldgen/structure_set", identifier, set);
   }

   public boolean removeStructureSet(ResourceLocation identifier) {
      return this.removeDataForJsonLocation("worldgen/structure_set", identifier);
   }

   public byte[] addBiome(ResourceLocation identifier, JsonObject biome) {
      return this.addDataForJsonLocation("worldgen/biome", identifier, biome);
   }

   public byte[] addStructure(ResourceLocation identifier, JsonObject structure) {
      return this.addDataForJsonLocation("worldgen/structure", identifier, structure);
   }

   public byte[] addLootTable(ResourceLocation identifier, JsonObject table) {
      return this.addDataForJsonLocation("loot_tables", identifier, table);
   }

   @Nullable
   public byte[] addDataForJsonLocationFromPath(String prefix, ResourceLocation identifier, String fromSubPath, String fromModID) {
      return JsonHelper.getElement(fromModID, fromSubPath) instanceof JsonObject object ? this.addDataForJsonLocation(prefix, identifier, object) : null;
   }

   public byte[] addDataForJsonLocation(String prefix, ResourceLocation identifier, JsonObject object) {
      return this.addAndSerializeDataForLocation(prefix, "json", identifier, object);
   }

   public boolean removeDataForJsonLocation(String prefix, ResourceLocation identifier) {
      return this.removeDataForLocation(prefix, "json", identifier);
   }

   public byte[] addAndSerializeDataForLocation(String prefix, String end, ResourceLocation identifier, JsonObject object) {
      return this.addData(
         ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), prefix + "/" + identifier.getPath() + "." + end),
         RuntimePackUtil.serializeJson(object)
      );
   }

   public boolean removeDataForLocation(String prefix, String end, ResourceLocation identifier) {
      return this.removeData(ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), prefix + "/" + identifier.getPath() + "." + end));
   }

   public byte[] addData(ResourceLocation path, byte[] data) {
      this.lock();

      byte[] var3;
      try {
         this.data.put(path, () -> data);
         var3 = data;
      } finally {
         this.waiting.unlock();
      }

      return var3;
   }

   public byte[] addImageAsset(ResourceLocation path, String modId, String subPath) {
      InputStream stream = PlatformHelper.getResourceStream(modId, subPath);
      if (stream == null) {
         return null;
      } else {
         byte[] asset = RuntimePackUtil.extractImageBytes(stream);
         return this.addAsset(path, asset);
      }
   }

   public byte[] addAsset(ResourceLocation path, byte[] asset) {
      this.lock();

      byte[] var3;
      try {
         this.assets.put(path, () -> asset);
         var3 = asset;
      } finally {
         this.waiting.unlock();
      }

      return var3;
   }

   public boolean removeData(ResourceLocation path) {
      this.lock();

      boolean var2;
      try {
         var2 = this.data.remove(path) != null;
      } finally {
         this.waiting.unlock();
      }

      return var2;
   }

   public void removeAsset(ResourceLocation path) {
      this.lock();

      try {
         this.assets.remove(path);
      } finally {
         this.waiting.unlock();
      }
   }

   public byte[] addRootResource(String path, byte[] data) {
      this.lock();

      byte[] var3;
      try {
         this.root.put(Arrays.asList(path.split("/")), () -> data);
         var3 = data;
      } finally {
         this.waiting.unlock();
      }

      return var3;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(@NotNull String... strings) {
      this.lock();

      Object var3;
      try {
         Supplier<byte[]> supplier = this.root.get(Arrays.asList(strings));
         if (supplier != null) {
            return () -> new ByteArrayInputStream(supplier.get());
         }

         var3 = null;
      } finally {
         this.waiting.unlock();
      }

      return (IoSupplier<InputStream>)var3;
   }

   public boolean hasRootResource(@NotNull String... strings) {
      this.lock();

      boolean var2;
      try {
         var2 = this.root.containsKey(Arrays.asList(strings));
      } finally {
         this.waiting.unlock();
      }

      return var2;
   }

   @Nullable
   public IoSupplier<InputStream> getResource(@NotNull PackType packType, @NotNull ResourceLocation id) {
      this.lock();

      Object var4;
      try {
         Supplier<byte[]> supplier = this.getSys(packType).get(id);
         if (supplier != null) {
            return () -> new ByteArrayInputStream(supplier.get());
         }

         var4 = null;
      } finally {
         this.waiting.unlock();
      }

      return (IoSupplier<InputStream>)var4;
   }

   @Nullable
   public JsonObject getResourceAsJson(PackType packType, ResourceLocation location) {
      IoSupplier<InputStream> stream = this.getResource(packType, location);

      try {
         return GsonHelper.parse(new InputStreamReader((InputStream)stream.get(), StandardCharsets.UTF_8));
      } catch (NullPointerException | IOException var6) {
         Constants.LOG.error("Couldn't get JsonObject from location: {}", location, var6);
         return null;
      }
   }

   public boolean hasData(ResourceLocation location) {
      this.lock();

      boolean var2;
      try {
         var2 = this.data.containsKey(location);
      } finally {
         this.waiting.unlock();
      }

      return var2;
   }

   public boolean hasAsset(ResourceLocation location) {
      this.lock();

      boolean var2;
      try {
         var2 = this.assets.containsKey(location);
      } finally {
         this.waiting.unlock();
      }

      return var2;
   }

   public void listResources(@NotNull PackType packType, @NotNull String namespace, @NotNull String prefix, @NotNull ResourceOutput resourceOutput) {
      this.lock();

      try {
         for (ResourceLocation identifier : this.getSys(packType).keySet()) {
            Supplier<byte[]> supplier = this.getSys(packType).get(identifier);
            if (supplier != null && identifier.getNamespace().equals(namespace) && identifier.getPath().contains(prefix + "/")) {
               IoSupplier<InputStream> inputSupplier = () -> new ByteArrayInputStream(supplier.get());
               resourceOutput.accept(identifier, inputSupplier);
            }
         }
      } finally {
         this.waiting.unlock();
      }
   }

   @NotNull
   public Set<String> getNamespaces(@NotNull PackType packType) {
      this.lock();

      Object var8;
      try {
         Set<String> namespaces = new HashSet<>();

         for (ResourceLocation identifier : this.getSys(packType).keySet()) {
            namespaces.add(identifier.getNamespace());
         }

         var8 = namespaces;
      } finally {
         this.waiting.unlock();
      }

      return (Set<String>)var8;
   }

   @Nullable
   public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> metadataSectionSerializer) {
      InputStream stream = null;

      try {
         IoSupplier<InputStream> supplier = this.getRootResource("pack.mcmeta");
         if (supplier != null) {
            stream = (InputStream)supplier.get();
         }
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }

      if (stream != null) {
         return (T)FilePackResources.getMetadataFromStream(metadataSectionSerializer, stream);
      } else if (metadataSectionSerializer.getMetadataSectionName().equals("pack")) {
         JsonObject object = new JsonObject();
         object.addProperty("pack_format", this.packVersion);
         object.addProperty("description", this.id);
         return (T)metadataSectionSerializer.fromJson(object);
      } else if (metadataSectionSerializer.getMetadataSectionName().equals("features")) {
         return (T)metadataSectionSerializer.fromJson(FeatureFlagsMetadataSection.TYPE.toJson(new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS)));
      } else {
         Constants.LOG.debug("'{}' is an unsupported metadata key", metadataSectionSerializer.getMetadataSectionName());
         return null;
      }
   }

   @NotNull
   public PackLocationInfo location() {
      return this.metadata;
   }

   @NotNull
   public String packId() {
      return this.id;
   }

   private void lock() {
      this.waiting.lock();
   }

   public void clear(PackType packType) {
      this.lock();

      try {
         this.getSys(packType).clear();
      } finally {
         this.waiting.unlock();
      }
   }

   public void close() {
      Constants.LOG.debug("Closing Runtime Pack: {}", this.id);
   }

   public void dumpToFolder(Path output) throws IOException {
      this.lock();

      try {
         for (Entry<List<String>, Supplier<byte[]>> entry : this.root.entrySet()) {
            List<String> pathParts = entry.getKey();
            Path filePath = output.resolve(Paths.get("", pathParts.toArray(new String[0])));
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, entry.getValue().get(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         }

         for (Entry<ResourceLocation, Supplier<byte[]>> entry : this.data.entrySet()) {
            ResourceLocation rl = entry.getKey();
            Path filePath = output.resolve(Paths.get("data", rl.getNamespace(), rl.getPath()));
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, entry.getValue().get(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         }

         for (Entry<ResourceLocation, Supplier<byte[]>> entry : this.assets.entrySet()) {
            ResourceLocation rl = entry.getKey();
            Path filePath = output.resolve(Paths.get("assets", rl.getNamespace(), rl.getPath()));
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, entry.getValue().get(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         }
      } finally {
         this.waiting.unlock();
      }
   }
}
