package dev.latvian.mods.kubejs.holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;

public interface KubeJSHolderSets {
   DeferredRegister<HolderSetType> REGISTRY = DeferredRegister.create(Keys.HOLDER_SET_TYPES, "kubejs");
   Holder<HolderSetType> REGEX = REGISTRY.register(
      "regex",
      () -> new HolderSetType() {
         public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(
            ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList
         ) {
            return RegExHolderSet.codec(registryKey);
         }

         public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
            return RegExHolderSet.streamCodec(registryKey);
         }
      }
   );
   Holder<HolderSetType> NAMESPACE = REGISTRY.register(
      "namespace",
      () -> new HolderSetType() {
         public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(
            ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList
         ) {
            return NamespaceHolderSet.codec(registryKey);
         }

         public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
            return NamespaceHolderSet.streamCodec(registryKey);
         }
      }
   );
}
