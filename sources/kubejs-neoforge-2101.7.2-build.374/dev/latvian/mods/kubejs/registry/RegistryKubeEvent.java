package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistryKubeEvent<T> implements KubeStartupEvent, AdditionalObjectRegistry {
   private final ResourceKey<Registry<T>> registryKey;
   private final BuilderTypeRegistryHandler.Info<T> builderInfo;
   public final List<BuilderBase<? extends T>> created;

   public RegistryKubeEvent(ResourceKey<Registry<T>> registryKey) {
      this.registryKey = registryKey;
      this.builderInfo = BuilderTypeRegistryHandler.info(registryKey);
      this.created = new LinkedList<>();
   }

   public BuilderBase<? extends T> create(Context cx, KubeResourceLocation id, KubeResourceLocation type) {
      SourceLine sourceLine = SourceLine.of(cx);
      BuilderType<T> t = this.builderInfo.namedType(type.wrapped());
      if (t == null) {
         throw new KubeRuntimeException("Unknown type '" + type + "' for object '" + id + "'!").source(sourceLine);
      } else {
         BuilderBase b = t.factory().createBuilder(id.wrapped());
         if (b == null) {
            throw new KubeRuntimeException("Unknown type '" + type + "' for object '" + id + "'!").source(sourceLine);
         } else if (this.builderInfo.directCodec() != null) {
            throw new KubeRuntimeException("Type '" + type + "' for object '" + id + "' is a datapack registry type!").source(sourceLine);
         } else {
            b.sourceLine = sourceLine;
            b.registryKey = this.registryKey;
            this.addBuilder(b);
            this.created.add(b);
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
            this.addBuilder(b);
            this.created.add(b);
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
         this.addBuilder(b);
         this.created.add(b);
         return b;
      }
   }

   @Override
   public void afterPosted(EventResult result) {
      for (BuilderBase<? extends T> c : this.created) {
         c.createAdditionalObjects(this);
      }
   }

   @Override
   public <R> void add(ResourceKey<Registry<R>> registry, BuilderBase<? extends R> builder) {
      builder.registryKey = registry;
      this.addBuilder(builder);
   }

   private <R> void addBuilder(BuilderBase<? extends R> builder) {
      if (builder == null) {
         throw new IllegalArgumentException("Can't add null builder in registry '" + builder.registryKey.location() + "'!");
      } else {
         if (DevProperties.get().logRegistryEventObjects) {
            ConsoleJS.STARTUP.info("~ " + builder.registryKey.location() + " | " + builder.id);
         }

         RegistryObjectStorage<? extends R> objStorage = RegistryObjectStorage.of((ResourceKey<Registry<T>>)builder.registryKey);
         if (objStorage.objects.containsKey(builder.id)) {
            throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + builder.registryKey.location() + "'!");
         } else {
            objStorage.objects.put(builder.id, builder);
            RegistryObjectStorage.ALL_BUILDERS.add(builder);
         }
      }
   }
}
