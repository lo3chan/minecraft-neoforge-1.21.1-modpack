package dev.latvian.mods.kubejs.registry;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ServerRegistryKubeEvent<T> implements KubeEvent {
   public final ResourceKey<Registry<T>> registryKey;
   private final BuilderTypeRegistryHandler.Info<T> builderInfo;
   public final DynamicOps<JsonElement> jsonOps;
   public final Codec<T> codec;
   private final List<BuilderBase<?>> builders;

   public ServerRegistryKubeEvent(ResourceKey<Registry<T>> registryKey, DynamicOps<JsonElement> jsonOps, Codec<T> codec, List<BuilderBase<?>> builders) {
      this.registryKey = registryKey;
      this.builderInfo = BuilderTypeRegistryHandler.info(registryKey);
      this.jsonOps = jsonOps;
      this.codec = codec;
      this.builders = builders;
   }

   public BuilderBase<? extends T> create(Context cx, KubeResourceLocation id, KubeResourceLocation type) {
      SourceLine sourceLine = SourceLine.of(cx);
      BuilderType<T> t = this.builderInfo.namedType(type.wrapped());
      if (t == null) {
         throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
      } else {
         BuilderBase b = t.factory().createBuilder(id.wrapped());
         if (b == null) {
            throw new KubeRuntimeException("Unknown type '" + type + "' for object '" + id + "'!").source(sourceLine);
         } else {
            b.sourceLine = sourceLine;
            b.registryKey = this.registryKey;
            this.builders.add(b);
            return b;
         }
      }
   }

   public BuilderBase<? extends T> create(Context cx, KubeResourceLocation id) {
      SourceLine sourceLine = SourceLine.of(cx);
      BuilderType<T> t = this.builderInfo.defaultType();
      if (t == null) {
         throw new KubeRuntimeException("Registry '" + this.registryKey.location() + "' doesn't have a default type registered!").source(sourceLine);
      } else {
         BuilderBase b = t.factory().createBuilder(id.wrapped());
         if (b == null) {
            throw new KubeRuntimeException("Unknown type '" + t.type() + "' for object '" + id + "'!").source(sourceLine);
         } else {
            b.sourceLine = sourceLine;
            b.registryKey = this.registryKey;
            this.builders.add(b);
            return b;
         }
      }
   }

   public CustomBuilderObject createCustom(Context cx, KubeResourceLocation id, Supplier<Object> object) {
      SourceLine sourceLine = SourceLine.of(cx);
      if (object == null) {
         throw new KubeRuntimeException("Tried to register a null object with id: " + id).source(sourceLine);
      } else {
         CustomBuilderObject b = new CustomBuilderObject(id.wrapped(), object);
         b.sourceLine = sourceLine;
         b.registryKey = this.registryKey;
         this.builders.add(b);
         return b;
      }
   }

   public CustomBuilderObject createFromJson(Context cx, KubeResourceLocation id, JsonElement json) {
      SourceLine sourceLine = SourceLine.of(cx);
      CustomBuilderObject b = new CustomBuilderObject(id.wrapped(), () -> this.codec.parse(this.jsonOps, json).result().orElseThrow());
      b.sourceLine = sourceLine;
      b.registryKey = this.registryKey;
      this.builders.add(b);
      return b;
   }
}
