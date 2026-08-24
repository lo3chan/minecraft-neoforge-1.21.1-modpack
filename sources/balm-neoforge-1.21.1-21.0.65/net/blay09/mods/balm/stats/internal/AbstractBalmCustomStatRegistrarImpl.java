package net.blay09.mods.balm.stats.internal;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;

public abstract class AbstractBalmCustomStatRegistrarImpl implements BalmCustomStatRegistrar {
   private final BalmRegistrar registrar;
   protected final String namespace;

   public AbstractBalmCustomStatRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public final ResourceLocation register(String name, StatFormatter formatter) {
      return this.register(ResourceLocation.fromNamespaceAndPath(this.namespace, name), formatter);
   }

   @Override
   public ResourceLocation register(ResourceLocation statIdentifier, StatFormatter formatter) {
      ResourceKey<ResourceLocation> resourceKey = ResourceKey.create(Registries.CUSTOM_STAT, statIdentifier);
      this.registrar.register(resourceKey, () -> statIdentifier);
      return statIdentifier;
   }
}
