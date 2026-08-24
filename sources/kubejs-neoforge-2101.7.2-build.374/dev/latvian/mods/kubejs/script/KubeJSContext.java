package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public class KubeJSContext extends Context {
   public final KubeJSContextFactory kjsFactory;
   public final Scriptable topLevelScope;
   private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;

   public KubeJSContext(KubeJSContextFactory factory) {
      super(factory);
      this.kjsFactory = factory;
      this.setApplicationClassLoader(KubeJS.class.getClassLoader());
      this.topLevelScope = this.initStandardObjects();
      BindingRegistry bindingsEvent = new BindingRegistry(this, this.topLevelScope);

      for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
         plugin.registerBindings(bindingsEvent);
      }

      KubeJSPlugins.addSidedBindings(bindingsEvent);
   }

   public boolean visibleToScripts(String fullClassName, ClassVisibilityContext type) {
      return type != ClassVisibilityContext.CLASS_IN_PACKAGE && type != ClassVisibilityContext.ARGUMENT
         ? true
         : this.kjsFactory.manager.isClassAllowed(fullClassName);
   }

   public ScriptType getType() {
      return this.kjsFactory.manager.scriptType;
   }

   public ConsoleJS getConsole() {
      return this.kjsFactory.manager.scriptType.console;
   }

   public RegistryAccessContainer getRegistries() {
      return this.kjsFactory.manager.getRegistries();
   }

   public Scriptable wrapAsJavaObject(Scriptable scope, Object javaObject, TypeInfo target) {
      if (javaObject instanceof AccessibleObject) {
         this.getConsole().error("Reflection access denied");
         return Undefined.SCRIPTABLE_INSTANCE;
      } else if (javaObject instanceof ClassLoader) {
         this.getConsole().error("ClassLoader access denied");
         return Undefined.SCRIPTABLE_INSTANCE;
      } else {
         return super.wrapAsJavaObject(scope, javaObject, target);
      }
   }

   public int internalConversionWeightLast(Object fromObj, TypeInfo target) {
      Class<?> c = target.asClass();
      if (c != Optional.class && c != ResourceKey.class && c != Holder.class && c != TagKey.class && c != Reference.class) {
         if (c != Object.class) {
            List<? extends RegistryType<?>> reg = RegistryType.allOfClass(target.asClass());
            if (!reg.isEmpty()) {
               return 1;
            }
         }

         return super.internalConversionWeightLast(fromObj, target);
      } else {
         return 1;
      }
   }

   public RegistryType<?> lookupRegistryType(TypeInfo type, Object from) {
      RegistryType<?> registryType = RegistryType.lookup(type);
      if (registryType == null) {
         throw reportRuntimeError("Can't interpret '" + from + "': no registries for type '" + type + "' found", this);
      } else {
         return registryType;
      }
   }

   public Registry<?> lookupRegistry(TypeInfo type, Object from) {
      RegistryType<?> registryType = this.lookupRegistryType(type, from);
      Registry<?> registry = (Registry<?>)this.getRegistries().access().registry(registryType.key()).orElse(null);
      if (registry == null) {
         throw reportRuntimeError("Can't interpret '" + from + "' as '" + registryType.key().location() + "': registry not found", this);
      } else {
         return registry;
      }
   }

   protected Object internalJsToJavaLast(Object from, TypeInfo target) {
      Class<?> c = target.asClass();
      if (c == Optional.class) {
         return from instanceof Optional<?> o ? o : Optional.ofNullable(this.jsToJava(from, target.param(0)));
      } else if (c == ResourceKey.class) {
         if (from instanceof ResourceKey<?> k) {
            return k;
         } else {
            RegistryType<?> registryType = this.lookupRegistryType(target.param(0), from);
            ResourceLocation id = ID.mc(from);
            return ResourceKey.create(registryType.key(), id);
         }
      } else if (c == Holder.class) {
         return HolderWrapper.wrap(this, from, target.param(0));
      } else if (c == Reference.class) {
         return HolderWrapper.wrapRef(this, from, target.param(0));
      } else if (c == HolderSet.class) {
         return HolderWrapper.wrapSet(this, from, target.param(0));
      } else if (c == TagKey.class) {
         if (from instanceof TagKey<?> k) {
            return k;
         } else {
            RegistryType<?> registryType = this.lookupRegistryType(target.param(0), from);
            ResourceLocation id = ID.mc(from);
            return TagKey.create(registryType.key(), id);
         }
      } else if (AccessibleObject.class.isAssignableFrom(c)) {
         throw throwAsScriptRuntimeEx(new IllegalAccessException("Reflection access denied"), this);
      } else if (ClassLoader.class.isAssignableFrom(c)) {
         throw throwAsScriptRuntimeEx(new IllegalAccessException("ClassLoader access denied"), this);
      } else if (from instanceof Holder<?> holder && c.isInstance(holder.value())) {
         return holder.value();
      } else {
         if (ID.isKey(from)) {
            RegistryType<?> reg = RegistryType.lookup(target);
            if (reg != null) {
               Registry<?> registry = (Registry<?>)this.getRegistries().access().registry(reg.key()).orElse(null);
               if (registry == null) {
                  throw reportRuntimeError("Can't interpret '" + from + "' as '" + reg.key().location() + "': registry not found", this);
               }

               Object value = registry.get(ID.mc(from));
               if (value != null) {
                  return value;
               }

               throw reportRuntimeError("Can't interpret '" + from + "' as '" + reg.key().location() + "': entry not found", this);
            }
         }

         return super.internalJsToJavaLast(from, target);
      }
   }

   public NativeJavaClass loadJavaClass(String name, boolean error) {
      if (name != null && !name.equals("null") && !name.isEmpty()) {
         if (this.javaClassCache == null) {
            this.javaClassCache = new HashMap<>();
         }

         Either<NativeJavaClass, Boolean> either = this.javaClassCache.get(name);
         if (either == null) {
            either = Either.right(false);
            if (!this.kjsFactory.manager.isClassAllowed(name)) {
               either = Either.right(true);
            } else {
               try {
                  either = Either.left(new NativeJavaClass(this, this.topLevelScope, Class.forName(name)));
                  this.getConsole().info("Loaded Java class '%s'".formatted(name));
               } catch (Exception var6) {
               }
            }

            this.javaClassCache.put(name, either);
         }

         NativeJavaClass l = (NativeJavaClass)either.left().orElse(null);
         if (l != null) {
            return l;
         } else if (error) {
            Boolean found = either.right().orElse(false);
            throw reportRuntimeError(
               (found ? "Failed to load Java class '%s': Class is not allowed by class filter!" : "Failed to load Java class '%s': Class could not be found!")
                  .formatted(name),
               this
            );
         } else {
            return null;
         }
      } else if (error) {
         throw reportRuntimeError("Class name can't be empty!", this);
      } else {
         return null;
      }
   }

   public Object classOf(Object from) {
      if (from instanceof Class<?> c) {
         return c;
      } else {
         return from instanceof NativeJavaClass c ? c.getClassObject() : this.loadJavaClass(String.valueOf(from), true).getClassObject();
      }
   }

   public Map<String, Either<NativeJavaClass, Boolean>> getJavaClassCache() {
      return this.javaClassCache == null ? Map.of() : Collections.unmodifiableMap(this.javaClassCache);
   }

   public boolean isMapLike(Object from) {
      return super.isMapLike(from) || from instanceof CompoundTag || from instanceof JsonObject;
   }

   public Object mapOf(@Nullable Object from, TypeInfo kTarget, TypeInfo vTarget) {
      if (from instanceof CompoundTag tag) {
         LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

         for (String key : tag.getAllKeys()) {
            map.put(kTarget == TypeInfo.STRING ? key : String.valueOf(this.jsToJava(key, kTarget)), this.jsToJava(tag.get(key), vTarget));
         }

         return map;
      } else if (from instanceof JsonObject json) {
         LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

         for (Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(
               kTarget == TypeInfo.STRING ? entry.getKey() : this.jsToJava(entry.getKey(), kTarget),
               this.jsToJava(JsonUtils.toObject(entry.getValue()), vTarget)
            );
         }

         return map;
      } else {
         return super.mapOf(from, kTarget, vTarget);
      }
   }
}
