package io.wispforest.owo.serialization;

import io.wispforest.endec.SerializationAttribute;
import io.wispforest.endec.SerializationAttribute.Instance;
import io.wispforest.endec.SerializationAttribute.WithValue;
import io.wispforest.owo.mixin.CachedRegistryInfoGetterAccessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps.HolderLookupAdapter;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class RegistriesAttribute implements Instance {
   public static final WithValue<RegistriesAttribute> REGISTRIES = SerializationAttribute.withValue("registries");
   private final RegistryInfoLookup infoGetter;
   @Nullable
   private final RegistryAccess registryManager;

   private RegistriesAttribute(RegistryInfoLookup infoGetter, @Nullable RegistryAccess registryManager) {
      this.infoGetter = infoGetter;
      this.registryManager = registryManager;
   }

   public static RegistriesAttribute of(RegistryAccess registryManager) {
      return new RegistriesAttribute(new HolderLookupAdapter(registryManager), registryManager);
   }

   @Internal
   public static RegistriesAttribute tryFromCachedInfoGetter(RegistryInfoLookup lookup) {
      return lookup instanceof HolderLookupAdapter cachedGetter ? fromCachedInfoGetter(cachedGetter) : fromInfoGetter(lookup);
   }

   public static RegistriesAttribute fromCachedInfoGetter(HolderLookupAdapter cachedGetter) {
      RegistryAccess registryManager = null;
      if (((CachedRegistryInfoGetterAccessor)cachedGetter).owo$getRegistriesLookup() instanceof RegistryAccess drm) {
         registryManager = drm;
      }

      return new RegistriesAttribute(cachedGetter, registryManager);
   }

   public static RegistriesAttribute fromInfoGetter(RegistryInfoLookup lookup) {
      return new RegistriesAttribute(lookup, null);
   }

   public RegistryInfoLookup infoGetter() {
      return this.infoGetter;
   }

   public boolean hasRegistryManager() {
      return this.registryManager != null;
   }

   @NotNull
   public RegistryAccess registryManager() {
      if (!this.hasRegistryManager()) {
         throw new IllegalStateException("This instance of RegistriesAttribute does not supply a DynamicRegistryManager");
      } else {
         return this.registryManager;
      }
   }

   public SerializationAttribute attribute() {
      return REGISTRIES;
   }

   public Object value() {
      return this;
   }
}
