package dev.latvian.mods.kubejs.holder;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.CommonProperties;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet.ListBacked;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.Nullable;

public class NamespaceHolderSet<T> extends ListBacked<T> implements ICustomHolderSet<T> {
   public final RegistryLookup<T> registryLookup;
   public final String namespace;
   @Nullable
   private Set<Holder<T>> set = null;
   @Nullable
   private List<Holder<T>> list = null;

   public static <T> MapCodec<NamespaceHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
      return RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               RegistryOps.retrieveRegistryLookup(registryKey).forGetter(s -> s.registryLookup), Codec.STRING.fieldOf("namespace").forGetter(s -> s.namespace)
            )
            .apply(instance, NamespaceHolderSet::new)
      );
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, NamespaceHolderSet<T>> streamCodec(ResourceKey<? extends Registry<T>> registryKey) {
      return null;
   }

   private NamespaceHolderSet(RegistryLookup<T> registryLookup, String namespace) {
      this.registryLookup = registryLookup;
      this.namespace = namespace;
   }

   public static <T> HolderSet<T> of(RegistryLookup<T> registryLookup, String namespace) {
      NamespaceHolderSet<T> set = new NamespaceHolderSet<>(registryLookup, namespace);
      return (HolderSet<T>)(CommonProperties.get().serverOnly ? HolderSet.direct(set.contents()) : set);
   }

   public HolderSetType type() {
      return (HolderSetType)KubeJSHolderSets.NAMESPACE.value();
   }

   protected List<Holder<T>> contents() {
      if (this.list == null) {
         this.list = List.copyOf(this.registryLookup.listElements().filter(ref -> ref.key().location().getNamespace().equals(this.namespace)).toList());
      }

      return this.list;
   }

   public Either<TagKey<T>, List<Holder<T>>> unwrap() {
      return Either.right(this.contents());
   }

   public boolean contains(Holder<T> holder) {
      if (this.set == null) {
         this.set = Set.copyOf(this.contents());
      }

      return this.set.contains(holder);
   }

   public Optional<TagKey<T>> unwrapKey() {
      return Optional.empty();
   }

   public String toString() {
      return "KubeJSNamespaceHolderSet[" + this.namespace + "]";
   }
}
