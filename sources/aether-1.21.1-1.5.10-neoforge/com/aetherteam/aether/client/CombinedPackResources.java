package com.aetherteam.aether.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.IoSupplier;

public class CombinedPackResources extends AbstractPackResources {
   private final PackMetadataSection packInfo;
   private final List<PackResources> packs;
   private final Map<String, List<PackResources>> assets;
   private final Map<String, List<PackResources>> data;
   private final Path source;

   public CombinedPackResources(PackLocationInfo id, PackMetadataSection packInfo, List<? extends PackResources> packs, Path sourcePack) {
      super(id);
      this.packInfo = packInfo;
      this.packs = ImmutableList.copyOf(packs);
      this.assets = this.buildNamespaceMap(PackType.CLIENT_RESOURCES, packs);
      this.data = this.buildNamespaceMap(PackType.SERVER_DATA, packs);
      this.source = sourcePack;
   }

   private Map<String, List<PackResources>> buildNamespaceMap(PackType type, List<? extends PackResources> packList) {
      Map<String, List<PackResources>> map = new HashMap<>();

      for (PackResources pack : packList) {
         for (String namespace : pack.getNamespaces(type)) {
            map.computeIfAbsent(namespace, k -> new ArrayList<>()).add(pack);
         }
      }

      map.replaceAll((k, list) -> ImmutableList.copyOf(list));
      return ImmutableMap.copyOf(map);
   }

   public Path getSource() {
      return this.source;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... paths) {
      FileUtil.validatePath(paths);
      Path path = FileUtil.resolvePath(this.getSource(), List.of(paths));
      return Files.exists(path) ? IoSupplier.create(path) : null;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
      return (T)(deserializer.getMetadataSectionName().equals("pack") ? this.packInfo : null);
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
      for (PackResources pack : this.getCandidatePacks(type, location)) {
         IoSupplier<InputStream> ioSupplier = pack.getResource(type, location);
         if (ioSupplier != null) {
            return ioSupplier;
         }
      }

      return null;
   }

   public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
      for (PackResources delegate : this.packs) {
         delegate.listResources(type, namespace, path, output);
      }
   }

   public Set<String> getNamespaces(PackType type) {
      return type == PackType.CLIENT_RESOURCES ? this.assets.keySet() : this.data.keySet();
   }

   public void close() {
      for (PackResources pack : this.packs) {
         pack.close();
      }
   }

   @Nullable
   public Collection<PackResources> getChildren() {
      return this.packs;
   }

   private List<PackResources> getCandidatePacks(PackType type, ResourceLocation location) {
      Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? this.assets : this.data;
      List<PackResources> packsWithNamespace = map.get(location.getNamespace());
      return packsWithNamespace == null ? Collections.emptyList() : packsWithNamespace;
   }

   public String toString() {
      return String.format("%s: %s", this.getClass().getName(), this.getSource());
   }

   public record CombinedResourcesSupplier(PackMetadataSection packInfo, List<? extends PackResources> packs, Path sourcePack) implements ResourcesSupplier {
      public PackResources openPrimary(PackLocationInfo location) {
         return new CombinedPackResources(location, this.packInfo, this.packs, this.sourcePack);
      }

      public PackResources openFull(PackLocationInfo location, Metadata metadata) {
         PackResources packresources = this.openPrimary(location);
         List<String> list = metadata.overlays();
         if (list.isEmpty()) {
            return packresources;
         } else {
            List<PackResources> list1 = new ArrayList<>(list.size());

            for (String s : list) {
               Path path = this.sourcePack.resolve(s);
               list1.add(new CombinedPackResources(location, this.packInfo, this.packs, path));
            }

            return new CompositePackResources(packresources, list1);
         }
      }
   }
}
