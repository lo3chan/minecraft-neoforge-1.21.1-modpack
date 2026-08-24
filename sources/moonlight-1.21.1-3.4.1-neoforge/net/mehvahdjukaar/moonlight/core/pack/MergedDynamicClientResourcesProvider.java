package net.mehvahdjukaar.moonlight.core.pack;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcePack;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.SimplePackProvider;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class MergedDynamicClientResourcesProvider implements PackResources, SimplePackProvider {
   private final Set<DynamicResourcesProvider> providers = new HashSet<>();
   private final List<PackResources> packResourcesStack = new ArrayList<>();
   private final PackLocationInfo locationInfo;
   private final Set<String> modNamespaces = new HashSet<>();
   private PackMetadataSection metadata;
   private byte[] packIcon;

   public MergedDynamicClientResourcesProvider(PackLocationInfo info) {
      this.locationInfo = info;
   }

   public synchronized void add(DynamicResourcesProvider provider) {
      if (provider.getPackType() != PackType.CLIENT_RESOURCES) {
         throw new IllegalArgumentException(
            "Tried to merge a pack provider of type " + provider.getPackType() + " to a merged provider of type " + PackType.CLIENT_RESOURCES
         );
      } else {
         if (this.providers.add(provider)) {
            this.packResourcesStack.add(provider.getPackResources());
            this.packResourcesStack.sort(Comparator.comparing(PackResources::packId));
            this.modNamespaces.add(provider.getName().getNamespace());
         }
      }
   }

   public synchronized void addLegacy(DynamicResourcePack dynPack) {
      this.packResourcesStack.add(dynPack);
      this.packResourcesStack.sort(Comparator.comparing(PackResources::packId));
      this.modNamespaces.add(dynPack.mainNamespace);
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... strings) {
      String fileName = String.join("/", strings);
      if (fileName.equals("pack.png") && this.packIcon != null) {
         return () -> new ByteArrayInputStream(this.packIcon);
      } else {
         for (PackResources packResources : this.packResourcesStack) {
            IoSupplier<InputStream> r = packResources.getRootResource(strings);
            if (r != null) {
               return r;
            }
         }

         return null;
      }
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      for (PackResources packResources : this.packResourcesStack) {
         IoSupplier<InputStream> ioSupplier = packResources.getResource(packType, location);
         if (ioSupplier != null) {
            return ioSupplier;
         }
      }

      return null;
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
      Map<ResourceLocation, IoSupplier<InputStream>> map = new HashMap<>();

      for (PackResources packResources : this.packResourcesStack) {
         packResources.listResources(packType, namespace, path, map::putIfAbsent);
      }

      map.forEach(resourceOutput);
   }

   public Set<String> getNamespaces(PackType type) {
      Set<String> set = new HashSet<>();

      for (PackResources packResources : this.packResourcesStack) {
         set.addAll(packResources.getNamespaces(type));
      }

      return set;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
      if (this.metadata == null) {
         this.metadata = new PackMetadataSection(
            Component.translatable("message.moonlight.merged_pack.description", new Object[]{this.modNamespaces.size()}),
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
            Optional.empty()
         );
      }

      return (T)(serializer == PackMetadataSection.TYPE ? this.metadata : null);
   }

   public PackLocationInfo location() {
      return this.locationInfo;
   }

   public void close() {
      this.packResourcesStack.forEach(PackResources::close);
   }

   public int size() {
      return this.packResourcesStack.size();
   }

   private void checkInitialized() {
      if (this.packIcon == null) {
         this.packIcon = this.createIcon();
      }
   }

   private byte[] createIcon() {
      List<NativeImage> icons = new ArrayList<>();

      for (PackResources p : this.packResourcesStack) {
         IoSupplier<InputStream> icon = p.getRootResource(new String[]{"pack.png"});
         if (icon != null) {
            try (InputStream s = (InputStream)icon.get()) {
               icons.add(NativeImage.read(s));
            } catch (Exception var24) {
               Moonlight.LOGGER.error("Failed to read pack icon from {}", p.packId(), var24);
            }
         } else {
            Moonlight.LOGGER.warn("Pack {} has no icon", p.packId());
         }
      }

      try {
         NativeImage image = ImageMerger.mergeSquare(icons, ImageMerger.Mode.MIN_AREA_NO_UPSCALE, -16777216);

         byte[] var26;
         try {
            var26 = image.asByteArray();
         } catch (Throwable var20) {
            if (image != null) {
               try {
                  image.close();
               } catch (Throwable var18) {
                  var20.addSuppressed(var18);
               }
            }

            throw var20;
         }

         if (image != null) {
            image.close();
         }

         return var26;
      } catch (Exception var21) {
         Moonlight.LOGGER.error("Failed to merge pack icons");
      } finally {
         for (NativeImage i : icons) {
            i.close();
         }
      }

      return null;
   }

   @Override
   public Pack createPack() {
      return Pack.readMetaAndCreate(this.locationInfo, new ResourcesSupplier() {
         public PackResources openPrimary(PackLocationInfo location) {
            MergedDynamicClientResourcesProvider.this.checkInitialized();
            return MergedDynamicClientResourcesProvider.this;
         }

         public PackResources openFull(PackLocationInfo location, Metadata metadata) {
            return MergedDynamicClientResourcesProvider.this;
         }
      }, PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Position.TOP, false));
   }
}
