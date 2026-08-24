package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCachedEditableResources implements PackResources, IEditablePackResources {
   protected final Path path;
   protected final PackLocationInfo locationInfo;
   protected final PackMetadataSection metadata;
   protected final PackType packType;
   protected final Set<String> namespaces = new HashSet<>();
   protected final Map<String, byte[]> rootResources = new ConcurrentHashMap<>();
   protected PackResources cachedResources = null;

   public AbstractCachedEditableResources(Path path, PackLocationInfo locationInfo, PackType packType, Component description) {
      this.path = path;
      this.locationInfo = locationInfo;
      this.packType = packType;
      this.metadata = new PackMetadataSection(description, SharedConstants.getCurrentVersion().getPackVersion(packType), Optional.empty());
   }

   public PackLocationInfo location() {
      return this.locationInfo;
   }

   public Set<String> getNamespaces(PackType type) {
      return type != this.packType ? Set.of() : this.namespaces;
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
      if (packType == this.packType) {
         if (this.cachedResources != null) {
            this.cachedResources.listResources(packType, namespace, path, resourceOutput);
         }
      }
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      if (packType != this.packType) {
         return null;
      } else {
         return this.cachedResources == null ? null : this.cachedResources.getResource(packType, location);
      }
   }

   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
      try {
         return (T)(serializer == PackMetadataSection.TYPE ? this.metadata : null);
      } catch (Exception var3) {
         return null;
      }
   }

   @Override
   public void addNamespaces(String... namespaces) {
      this.namespaces.addAll(Arrays.asList(namespaces));
   }

   @Override
   public void addRootResource(String name, byte[] resource) {
      this.rootResources.put(name, resource);
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... strings) {
      String fileName = String.join("/", strings);
      byte[] resource = this.rootResources.get(fileName);
      return resource == null ? null : () -> new ByteArrayInputStream(resource);
   }

   public void close() {
      if (this.cachedResources != null) {
         this.cachedResources.close();
      }
   }
}
