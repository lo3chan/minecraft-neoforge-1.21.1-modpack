package io.wispforest.owo.mixin;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.RegistryOps.HolderLookupAdapter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({HolderLookupAdapter.class})
public interface CachedRegistryInfoGetterAccessor {
   @Accessor("lookupProvider")
   Provider owo$getRegistriesLookup();
}
