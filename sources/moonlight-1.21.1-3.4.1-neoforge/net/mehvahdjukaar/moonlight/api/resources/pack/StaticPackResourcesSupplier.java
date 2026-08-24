package net.mehvahdjukaar.moonlight.api.resources.pack;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;

public record StaticPackResourcesSupplier(PackResources resources) implements ResourcesSupplier {
   public PackResources openPrimary(PackLocationInfo location) {
      return this.resources;
   }

   public PackResources openFull(PackLocationInfo location, Metadata metadata) {
      return this.resources;
   }
}
