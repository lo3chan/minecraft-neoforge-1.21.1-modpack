package net.mehvahdjukaar.moonlight.api.fluids;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class SoftFluidRegistry {
   public static final ResourceKey<Registry<SoftFluid>> KEY = ResourceKey.createRegistryKey(Moonlight.res("soft_fluid"));

   public static Holder<SoftFluid> getEmpty(Provider pr) {
      return MLBuiltinSoftFluids.EMPTY.getHolder(pr);
   }

   public static Holder<SoftFluid> getEmpty(HolderGetter<SoftFluid> reg) {
      return MLBuiltinSoftFluids.EMPTY.lookup(reg);
   }

   public static Registry<SoftFluid> get(RegistryAccess registryAccess) {
      return registryAccess.registryOrThrow(KEY);
   }

   public static RegistryLookup<SoftFluid> get(Provider provider) {
      return provider.lookupOrThrow(KEY);
   }

   public static Registry<SoftFluid> get(Level level) {
      return get(level.registryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public static Registry<SoftFluid> getRegistry(RegistryAccess registryAccess) {
      return registryAccess.registryOrThrow(KEY);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Holder<SoftFluid> getEmpty() {
      return MLBuiltinSoftFluids.EMPTY.getHolder(Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public static Holder<SoftFluid> hackyGetEmpty() {
      return MLBuiltinSoftFluids.EMPTY.getHolder(Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public static SoftFluid empty() {
      return MLBuiltinSoftFluids.EMPTY.get(Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public static Registry<SoftFluid> hackyGetRegistry() {
      return Utils.hackyGetRegistry(KEY);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Collection<SoftFluid> getValues() {
      return hackyGetRegistry().stream().toList();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Collection<Reference<SoftFluid>> getHolders() {
      return hackyGetRegistry().holders().toList();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Set<Entry<ResourceKey<SoftFluid>, SoftFluid>> getEntries() {
      return hackyGetRegistry().entrySet();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Holder<SoftFluid> getHolder(ResourceLocation id) {
      Optional<Reference<SoftFluid>> opt = getOptionalHolder(id);
      return opt.isPresent() ? (Holder)opt.get() : getEmpty();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Optional<Reference<SoftFluid>> getOptionalHolder(ResourceLocation id) {
      return hackyGetRegistry().getHolder(ResourceKey.create(KEY, id));
   }
}
