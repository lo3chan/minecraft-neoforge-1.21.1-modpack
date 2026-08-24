package com.finndog.moogs_structures.world.processors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class BlockAliasCompatCodec {
   private static final Map<String, String> RENAMES = Map.of("minecraft:chain", "minecraft:iron_chain");
   private static final String NAME_FIELD = "Name";

   private BlockAliasCompatCodec() {
   }

   public static <A> Codec<A> wrap(final Codec<A> inner) {
      return new Codec<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            T normalised = BlockAliasCompatCodec.normalise(ops, input);
            return inner.decode(ops, normalised);
         }

         public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
            return inner.encode(input, ops, prefix);
         }

         @Override
         public String toString() {
            return "BlockAliasCompat[" + inner + "]";
         }
      };
   }

   private static <T> T normalise(DynamicOps<T> ops, T input) {
      Optional<MapLike<T>> rootOpt = ops.getMap(input).result();
      if (rootOpt.isPresent()) {
         MapLike<T> root = rootOpt.get();
         T nameValue = (T)root.get("Name");
         if (nameValue == null) {
            return input;
         } else {
            String name = (String)ops.getStringValue(nameValue).result().orElse(null);
            if (name == null) {
               return input;
            } else {
               String renamed = aliasIfRegistered(name);
               if (renamed.equals(name)) {
                  return input;
               } else {
                  Map<T, T> rewritten = new LinkedHashMap<>();
                  root.entries().forEach(pair -> {
                     String key = (String)ops.getStringValue(pair.getFirst()).result().orElse(null);
                     if ("Name".equals(key)) {
                        rewritten.put((T)pair.getFirst(), (T)ops.createString(renamed));
                     } else {
                        rewritten.put((T)pair.getFirst(), (T)pair.getSecond());
                     }
                  });
                  return (T)ops.createMap(rewritten);
               }
            }
         }
      } else {
         Optional<String> strOpt = ops.getStringValue(input).result();
         if (strOpt.isPresent()) {
            String renamed = aliasIfRegistered(strOpt.get());
            if (!renamed.equals(strOpt.get())) {
               return (T)ops.createString(renamed);
            }
         }

         return input;
      }
   }

   private static String aliasIfRegistered(String name) {
      String target = RENAMES.get(name);
      if (target == null) {
         return name;
      } else {
         ResourceLocation targetId = ResourceLocation.tryParse(target);
         if (targetId == null) {
            return name;
         } else {
            return BuiltInRegistries.BLOCK.containsKey(targetId) ? target : name;
         }
      }
   }
}
