package fuzs.puzzleslib.impl.resources;

import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;

public record ModPackResourcesSupplier(
   PackType packType, PackLocationInfo info, ModPackResourcesSupplier.PackResourcesSupplier<AbstractModPackResources> supplier, BuiltInMetadata metadata
) implements ResourcesSupplier {
   public static ModPackResourcesSupplier create(
      PackType packType, PackLocationInfo info, ModPackResourcesSupplier.PackResourcesSupplier<AbstractModPackResources> supplier, Component description
   ) {
      PackMetadataSection metadataSection = new PackMetadataSection(description, SharedConstants.getCurrentVersion().getPackVersion(packType), Optional.empty());
      return new ModPackResourcesSupplier(packType, info, supplier, BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection));
   }

   public PackResources openPrimary(PackLocationInfo info) {
      return this.getAndSetupPackResources();
   }

   public PackResources openFull(PackLocationInfo info, Metadata packMetadata) {
      return this.getAndSetupPackResources();
   }

   private AbstractModPackResources getAndSetupPackResources() {
      return this.supplier.apply(this.packType, this.info, this.metadata);
   }

   @FunctionalInterface
   public interface PackResourcesSupplier<T extends PackResources> {
      T apply(PackType var1, PackLocationInfo var2, BuiltInMetadata var3);
   }
}
