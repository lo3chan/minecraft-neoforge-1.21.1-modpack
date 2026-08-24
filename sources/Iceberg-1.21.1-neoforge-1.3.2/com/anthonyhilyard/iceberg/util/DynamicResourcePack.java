package com.anthonyhilyard.iceberg.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class DynamicResourcePack implements PackResources {
   private final PackLocationInfo location;
   private final String packName;
   private Map<DynamicResourcePack.DynamicResourceKey, IoSupplier<InputStream>> dynamicResourceMap = new HashMap<>();

   public DynamicResourcePack(String packName) {
      this.location = new PackLocationInfo(packName, Component.literal(packName), PackSource.DEFAULT, null);
      this.packName = packName;
   }

   public void clear() {
      this.dynamicResourceMap.clear();
   }

   public boolean removeResource(PackType type, ResourceLocation location) {
      DynamicResourcePack.DynamicResourceKey key = new DynamicResourcePack.DynamicResourceKey(type.getDirectory(), location.getNamespace(), location.getPath());
      if (this.dynamicResourceMap.containsKey(key)) {
         this.dynamicResourceMap.remove(key);
         return true;
      } else {
         return false;
      }
   }

   public boolean registerResource(PackType type, ResourceLocation location, IoSupplier<InputStream> resourceSupplier) {
      return this.register(type.getDirectory(), location.getNamespace(), location.getPath(), resourceSupplier);
   }

   public boolean registerRootResource(String path, IoSupplier<InputStream> resourceSupplier) {
      return this.register("root", "", path, resourceSupplier);
   }

   private boolean register(String directory, String namespace, String path, IoSupplier<InputStream> resourceSupplier) {
      DynamicResourcePack.DynamicResourceKey key = new DynamicResourcePack.DynamicResourceKey(directory, namespace, path);
      if (!this.dynamicResourceMap.containsKey(key)) {
         this.dynamicResourceMap.put(key, resourceSupplier);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... path) {
      try {
         return this.getResource("root", "", String.join("/", path));
      } catch (IOException var3) {
         return null;
      }
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
      try {
         return this.getResource(type.getDirectory(), location.getNamespace(), location.getPath());
      } catch (IOException var4) {
         return null;
      }
   }

   private IoSupplier<InputStream> getResource(String directory, String namespace, String path) throws IOException {
      DynamicResourcePack.DynamicResourceKey key = new DynamicResourcePack.DynamicResourceKey(directory, namespace, path);
      if (this.dynamicResourceMap.containsKey(key)) {
         return this.dynamicResourceMap.get(key);
      } else {
         throw new FileNotFoundException("Can't find dynamic resource " + path + ". Please ensure it has been registered.");
      }
   }

   public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
      this.dynamicResourceMap
         .entrySet()
         .stream()
         .filter(entry -> entry.getKey().namespace.contentEquals(namespace))
         .filter(entry -> entry.getKey().path.startsWith(path))
         .filter(entry -> entry.getKey().type.contentEquals(type.getDirectory()))
         .forEach(entry -> output.accept(ResourceLocation.fromNamespaceAndPath(namespace, entry.getKey().path), entry.getValue()));
   }

   public Set<String> getNamespaces(PackType type) {
      Set<String> namespaces = new HashSet<>();

      for (DynamicResourcePack.DynamicResourceKey key : this.dynamicResourceMap.keySet()) {
         if (type.getDirectory().contentEquals(key.type)) {
            namespaces.add(key.namespace);
         }
      }

      return namespaces;
   }

   public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10291_) throws IOException {
      return null;
   }

   public String packId() {
      return this.packName;
   }

   public void close() {
   }

   public PackLocationInfo location() {
      return this.location;
   }

   private record DynamicResourceKey(String type, String namespace, String path) {
   }
}
