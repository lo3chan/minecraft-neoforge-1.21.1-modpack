package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.resources.ModPackResourcesSupplier;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

public class AbstractModPackResources implements PackResources {
   protected final String modLogoPath;
   @Nullable
   private PackType packType;
   @Nullable
   private PackLocationInfo info;
   @Nullable
   private BuiltInMetadata metadata;

   public AbstractModPackResources() {
      this("mod_logo.png");
   }

   public AbstractModPackResources(String modLogoPath) {
      Objects.requireNonNull(modLogoPath, "mod logo path is null");
      this.modLogoPath = modLogoPath;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... elements) {
      String path = String.join("/", elements);
      return "pack.png".equals(path)
         ? ModLoaderEnvironment.INSTANCE
            .getModContainer(this.getNamespace())
            .flatMap(container -> container.findResource(this.modLogoPath))
            .map(modResource -> () -> Files.newInputStream(modResource))
            .orElse(null)
         : null;
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
      return null;
   }

   public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
   }

   public Set<String> getNamespaces(PackType type) {
      Objects.requireNonNull(this.packType, "pack type is null");
      return this.packType == type ? Collections.singleton(this.getNamespace()) : Collections.emptySet();
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
      Objects.requireNonNull(this.metadata, "metadata is null");
      return (T)this.metadata.get(deserializer);
   }

   public PackLocationInfo location() {
      Objects.requireNonNull(this.info, "info is null");
      return this.info;
   }

   public void close() {
   }

   public String getNamespace() {
      return ResourceLocationHelper.parse(this.packId()).getNamespace();
   }

   @OverrideOnly
   protected void setup() {
   }

   public static Pack buildPack(
      PackType packType,
      ResourceLocation id,
      Supplier<AbstractModPackResources> factory,
      Component title,
      Component description,
      boolean required,
      Position position,
      boolean fixedPosition,
      boolean hidden,
      FeatureFlagSet features
   ) {
      PackLocationInfo info = new PackLocationInfo(id.toString(), title, PackSource.BUILT_IN, Optional.empty());
      ResourcesSupplier resourcesSupplier = ModPackResourcesSupplier.create(packType, info, createSupplier(factory), description);
      Metadata metadata = CommonAbstractions.INSTANCE.createPackInfo(id, description, PackCompatibility.COMPATIBLE, features, hidden);
      PackSelectionConfig config = new PackSelectionConfig(required, position, fixedPosition);
      return new Pack(info, resourcesSupplier, metadata, config);
   }

   private static ModPackResourcesSupplier.PackResourcesSupplier<AbstractModPackResources> createSupplier(Supplier<AbstractModPackResources> factory) {
      return (packType, info, metadata) -> {
         AbstractModPackResources packResources = factory.get();
         packResources.info = info;
         packResources.metadata = metadata;
         packResources.packType = packType;
         packResources.setup();
         return packResources;
      };
   }
}
