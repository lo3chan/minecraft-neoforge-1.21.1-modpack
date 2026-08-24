package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

@RemapPrefixForJS("kjs$")
public interface RegistryObjectKJS<T> extends SpecialEquality {
   default boolean specialEquals(Context cx, Object o, boolean shallow) {
      return switch (o) {
         case CharSequence cs -> this.kjs$getId().equals(cs.toString());
         case ResourceLocation id -> this.kjs$getIdLocation().equals(id);
         case null, default -> this.equals(o);
      };
   }

   default ResourceKey<Registry<T>> kjs$getRegistryId() {
      throw new NoMixinException();
   }

   default Registry<T> kjs$getRegistry() {
      return RegistryAccessContainer.current.access().registryOrThrow(this.kjs$getRegistryId());
   }

   default Holder<T> kjs$asHolder() {
      try {
         return this.kjs$getRegistry().wrapAsHolder(this);
      } catch (Exception var2) {
         return Holder.direct(this);
      }
   }

   default ResourceKey<T> kjs$getKey() {
      try {
         return this.kjs$asHolder().getKey();
      } catch (Exception var2) {
         return (ResourceKey<T>)this.kjs$getRegistry().getResourceKey(this).orElseThrow();
      }
   }

   default ResourceLocation kjs$getIdLocation() {
      return this.kjs$getKey().location();
   }

   default String kjs$getId() {
      return this.kjs$getIdLocation().toString();
   }

   default String kjs$getMod() {
      return this.kjs$getIdLocation().getNamespace();
   }

   default List<TagKey<T>> kjs$getTagKeys() {
      return this.kjs$asHolder().tags().toList();
   }

   default List<ResourceLocation> kjs$getTags() {
      return this.kjs$asHolder().tags().<ResourceLocation>map(TagKey::location).toList();
   }

   default boolean kjs$hasTag(ResourceLocation tag) {
      return this.kjs$asHolder().is(TagKey.create(this.kjs$getRegistryId(), tag));
   }
}
