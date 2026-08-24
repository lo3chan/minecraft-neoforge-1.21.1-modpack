package dev.latvian.mods.kubejs.holder;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
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

public class RegExHolderSet<T> extends ListBacked<T> implements ICustomHolderSet<T> {
   public final RegistryLookup<T> registryLookup;
   public final Pattern pattern;
   @Nullable
   private Set<Holder<T>> set = null;
   @Nullable
   private List<Holder<T>> list = null;

   public static <T> MapCodec<RegExHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
      return RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               RegistryOps.retrieveRegistryLookup(registryKey).forGetter(s -> s.registryLookup), RegExpKJS.CODEC.fieldOf("pattern").forGetter(s -> s.pattern)
            )
            .apply(instance, RegExHolderSet::new)
      );
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, RegExHolderSet<T>> streamCodec(ResourceKey<? extends Registry<T>> registryKey) {
      return null;
   }

   private RegExHolderSet(RegistryLookup<T> registryLookup, Pattern pattern) {
      this.registryLookup = registryLookup;
      this.pattern = pattern;
   }

   public static <T> HolderSet<T> of(RegistryLookup<T> registryLookup, Pattern pattern) {
      RegExHolderSet<T> set = new RegExHolderSet<>(registryLookup, pattern);
      return (HolderSet<T>)(CommonProperties.get().serverOnly ? HolderSet.direct(set.contents()) : set);
   }

   public HolderSetType type() {
      return (HolderSetType)KubeJSHolderSets.REGEX.value();
   }

   protected List<Holder<T>> contents() {
      if (this.list == null) {
         this.list = List.copyOf(this.registryLookup.listElements().filter(ref -> this.pattern.matcher(ref.key().location().toString()).find()).toList());
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
      return "KubeJSRegExHolderSet[" + RegExpKJS.toRegExpString(this.pattern) + "]";
   }
}
