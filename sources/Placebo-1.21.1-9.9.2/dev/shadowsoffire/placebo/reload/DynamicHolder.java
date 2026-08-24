package dev.shadowsoffire.placebo.reload;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class DynamicHolder<R extends CodecProvider<? super R>> implements Supplier<R> {
   public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("empty", "empty");
   protected final DynamicRegistry<R> registry;
   protected final ResourceLocation id;
   @Nullable
   protected R value;

   DynamicHolder(DynamicRegistry<R> registry, ResourceLocation id) {
      this.id = id;
      this.registry = registry;
   }

   public boolean isBound() {
      this.bind();
      return this.value != null;
   }

   public R get() {
      this.bind();
      Objects.requireNonNull(this.value, "Trying to access unbound value: " + this.id);
      return this.value;
   }

   public Optional<R> getOptional() {
      return this.isBound() ? Optional.of(this.get()) : Optional.empty();
   }

   public String getRegistryPath() {
      return this.registry.getPath();
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean is(ResourceLocation id) {
      return this.id.equals(id);
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof DynamicHolder dh && dh.registry == this.registry && dh.id.equals(this.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.registry);
   }

   @Override
   public String toString() {
      return "DynamicHolder{%s / %s}".formatted(this.registry == null ? "null" : this.registry.getPath(), this.id);
   }

   void bind() {
      if (this.value == null) {
         this.value = this.registry.getValue(this.id);
      }
   }

   void unbind() {
      this.value = null;
   }
}
