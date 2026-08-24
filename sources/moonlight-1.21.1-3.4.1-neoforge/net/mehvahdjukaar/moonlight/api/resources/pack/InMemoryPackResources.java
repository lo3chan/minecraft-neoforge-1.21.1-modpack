package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.mehvahdjukaar.moonlight.api.misc.ResourceLocationSearchTrie;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class InMemoryPackResources extends AbstractPackResources implements IEditablePackResources, IDebugDumpable {
   protected final boolean hidden;
   protected final PackType packType;
   protected final PackMetadataSection metadata;
   protected final Set<String> namespaces = new HashSet<>();
   protected final Map<ResourceLocation, byte[]> resources = new ConcurrentHashMap<>();
   protected final Map<String, byte[]> rootResources = new ConcurrentHashMap<>();
   protected final ResourceLocationSearchTrie searchTrie = new ResourceLocationSearchTrie();

   protected InMemoryPackResources(PackLocationInfo info, PackType type) {
      this(info, type, false);
   }

   protected InMemoryPackResources(PackLocationInfo info, PackType type, boolean hidden) {
      super(info);
      this.packType = type;
      this.hidden = hidden;
      this.metadata = new PackMetadataSection(
         Component.translatable("message.moonlight.runtime"), SharedConstants.getCurrentVersion().getPackVersion(this.packType), Optional.empty()
      );
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public Set<String> getNamespaces(PackType packType) {
      return packType != this.packType ? Set.of() : this.namespaces;
   }

   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
      try {
         return (T)(serializer == PackMetadataSection.TYPE ? this.metadata : null);
      } catch (Exception var3) {
         return null;
      }
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... strings) {
      String fileName = String.join("/", strings);
      byte[] resource = this.rootResources.get(fileName);
      return resource == null ? null : () -> new ByteArrayInputStream(resource);
   }

   public void listResources(PackType packType, String namespace, String id, ResourceOutput output) {
      if (packType == this.packType) {
         synchronized (this) {
            this.searchTrie.search(namespace + "/" + id).forEach(r -> {
               byte[] buf = this.resources.get(r);
               output.accept(r, (IoSupplier)() -> {
                  if (buf == null) {
                     throw new IllegalStateException("Somehow search tree returned a resource not in resources " + r);
                  } else {
                     return new ByteArrayInputStream(buf);
                  }
               });
            });
         }
      }
   }

   public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
      byte[] res = this.resources.get(id);
      return res != null ? () -> {
         if (type != this.packType) {
            throw new IOException(String.format("Tried to access wrong type of resource on %s.", this.packId()));
         } else {
            return new ByteArrayInputStream(res);
         }
      } : null;
   }

   public void close() {
   }

   @Override
   public void addNamespaces(String... namespaces) {
      this.namespaces.addAll(Arrays.asList(namespaces));
   }

   @Override
   public void addRootResource(String name, byte[] resource) {
      this.rootResources.put(name, resource);
   }

   @Override
   public void addResource(ResourceLocation id, byte[] bytes) {
      synchronized (this) {
         this.namespaces.add(id.getNamespace());
         this.resources.put(id, bytes);
         this.searchTrie.insert(id);
      }
   }

   @Override
   public void removeResource(ResourceLocation id) {
      synchronized (this) {
         this.resources.remove(id);
         this.searchTrie.remove(id);
      }
   }

   @Override
   public void removeRootResource(String name) {
      this.rootResources.remove(name);
   }

   @Override
   public boolean clearAllResources() {
      synchronized (this) {
         this.resources.clear();
         this.rootResources.clear();
         this.searchTrie.clear();
         return true;
      }
   }

   @Override
   public PackType getPackType() {
      return this.packType;
   }

   @Override
   public boolean isEmpty() {
      return this.resources.isEmpty();
   }

   @Override
   public void dumpToDisk(Path path) {
      this.resources.forEach((k, v) -> {
         try {
            Path p = RPUtils.getResourcePath(path, k, this.packType);
            Files.createDirectories(p.getParent());
            Files.write(p, v, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
         } catch (IOException var5) {
         }
      });
   }
}
