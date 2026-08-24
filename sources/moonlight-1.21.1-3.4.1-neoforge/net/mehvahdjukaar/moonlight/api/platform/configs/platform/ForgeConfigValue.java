package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigMetadata;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

abstract class ForgeConfigValue<T, C> implements TrackedConfigValue<T> {
   private final ConfigValue<C> original;
   private final ConfigMetadata meta;
   private T cachedValue = (T)null;
   private C cachedRaw = (C)null;
   private boolean initialized = false;

   ForgeConfigValue(ConfigValue<C> original, ConfigMetadata meta) {
      this.original = original;
      this.meta = meta;
   }

   public static <T> ForgeConfigValue<T, T> simple(ConfigValue<T> original, ConfigMetadata meta) {
      return new ForgeConfigValue<T, T>(original, meta) {
         @Override
         T map(T value) {
            return value;
         }

         @Override
         T unmap(T value) {
            return value;
         }
      };
   }

   public static <T> ForgeConfigValue<T, String> fromString(ConfigValue<String> original, final Codec<T> codec, ConfigMetadata meta) {
      return new ForgeConfigValue<T, String>(original, meta) {
         T map(String value) {
            return (T)codec.parse(JavaOps.INSTANCE, value).getOrThrow();
         }

         String unmap(T value) {
            return codec.encodeStart(JavaOps.INSTANCE, value).getOrThrow().toString();
         }
      };
   }

   public static ForgeConfigValue<JsonElement, String> json(ConfigValue<String> original, ConfigMetadata meta) {
      return new ForgeConfigValue<JsonElement, String>(original, meta) {
         JsonElement map(String value) {
            try {
               return JsonParser.parseString(value.replace("'", "\""));
            } catch (Exception var3) {
               throw new RuntimeException("Failed to parse JSON config value: " + value, var3);
            }
         }

         String unmap(JsonElement value) {
            return value.toString().replace(" ", "").replace("\"", "'");
         }
      };
   }

   public static <T> ForgeConfigValue<T, String> codec(ConfigValue<String> original, final Codec<T> codec, ConfigMetadata meta) {
      return new ForgeConfigValue<T, String>(original, meta) {
         T map(String raw) {
            JsonElement json = JsonParser.parseString(raw.replace("'", "\""));
            return (T)((Pair)codec.decode(JsonOps.INSTANCE, json).getOrThrow()).getFirst();
         }

         String unmap(T value) {
            JsonElement json = (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
            return json.toString().replace(" ", "").replace("\"", "'");
         }
      };
   }

   abstract T map(C var1);

   abstract C unmap(T var1);

   @Override
   public T get() {
      this.pollChanged();
      if (this.cachedValue == null && this.initialized) {
         this.cachedValue = this.map(this.cachedRaw);
      }

      return this.cachedValue;
   }

   @Override
   public boolean pollChanged() {
      C current = (C)this.original.get();
      if (!this.initialized) {
         this.cachedRaw = current;
         this.cachedValue = this.map(current);
         this.initialized = true;
         return false;
      } else if (!Objects.equals(this.cachedRaw, current)) {
         this.cachedRaw = current;
         this.cachedValue = this.map(current);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean setValue(T value) {
      C raw = this.unmap(value);
      boolean changed = !this.initialized || !Objects.equals(this.cachedRaw, raw);
      this.original.set(raw);
      this.original.clearCache();
      this.cachedRaw = raw;
      this.cachedValue = value;
      this.initialized = true;
      return changed;
   }

   @Override
   public boolean affectsDynamicPacks() {
      return this.meta.affectsDynamicPacks();
   }

   @Override
   public ConfigReloadType reloadType() {
      return this.meta.reloadType();
   }
}
