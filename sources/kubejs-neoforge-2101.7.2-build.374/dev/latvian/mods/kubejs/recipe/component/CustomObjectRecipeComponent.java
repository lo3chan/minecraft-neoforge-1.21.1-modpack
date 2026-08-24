package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import org.jetbrains.annotations.NotNull;

public class CustomObjectRecipeComponent implements RecipeComponent<List<CustomObjectRecipeComponent.Value>> {
   public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(
      KubeJS.id("custom_object"),
      (RecipeComponentCodecFactory)((type, ctx) -> RecordCodecBuilder.mapCodec(
         instance -> instance.group(CustomObjectRecipeComponent.Key.createCodec(ctx).listOf().fieldOf("keys").forGetter(CustomObjectRecipeComponent::keys))
            .apply(instance, CustomObjectRecipeComponent::new)
      ))
   );
   private final List<CustomObjectRecipeComponent.Key> keys;
   public Predicate<Set<String>> hasPriority;
   private Codec<List<CustomObjectRecipeComponent.Value>> codec;
   private TypeInfo typeInfo;

   public CustomObjectRecipeComponent(List<CustomObjectRecipeComponent.Key> keys) {
      this.keys = List.copyOf(keys);
   }

   @Override
   public RecipeComponentType<?> type() {
      return TYPE;
   }

   public List<CustomObjectRecipeComponent.Key> keys() {
      return this.keys;
   }

   public CustomObjectRecipeComponent hasPriority(Predicate<Set<String>> hasPriority) {
      this.hasPriority = hasPriority;
      return this;
   }

   public CustomObjectRecipeComponent createCopy() {
      CustomObjectRecipeComponent copy = new CustomObjectRecipeComponent(this.keys);
      copy.hasPriority = this.hasPriority;
      return copy;
   }

   public List<CustomObjectRecipeComponent.Value> wrap(RecipeScriptContext rcx, Object from) {
      Context cx = rcx.cx();
      List<Object> wrapped = cx.optionalListOf(from, TypeInfo.of(CustomObjectRecipeComponent.Value.class));
      if (wrapped != null) {
         return Cast.to(wrapped);
      } else if (cx.isMapLike(from)) {
         Map<Object, Object> map = Objects.requireNonNull(cx.optionalMapOf(from, TypeInfo.NONE, TypeInfo.NONE));
         List<CustomObjectRecipeComponent.Value> list = new ArrayList<>(this.keys.size());
         Map<String, CustomObjectRecipeComponent.Key> keyMap = new HashMap<>();
         this.keys.forEach(keyx -> keyMap.put(keyx.name, keyx));

         for (Entry<Object, Object> entry : map.entrySet()) {
            CustomObjectRecipeComponent.Key key = switch (entry.getKey()) {
               case null -> null;
               case CustomObjectRecipeComponent.Key id -> id;
               case CharSequence cs -> (CustomObjectRecipeComponent.Key)keyMap.get(cs.toString());
               default -> (CustomObjectRecipeComponent.Key)keyMap.get(Objects.toString(entry.getKey()));
            };
            if (key == null) {
               throw new IllegalStateException("Unknown key in custom object: " + entry.getKey());
            }

            try {
               Object value = Objects.requireNonNull(key.component.wrap(rcx, entry.getValue()), "Wrapped value is null!");
               list.add(new CustomObjectRecipeComponent.Value(key, this.keys.indexOf(key), value));
            } catch (Throwable var15) {
               throw new RecipeComponentException("Failed to wrap key " + key + " for custom component!", var15, this, null, cx.toString(entry.getValue()));
            }
         }

         return list;
      } else {
         throw new IllegalStateException("Unexpected value: " + from);
      }
   }

   public MapCodec<List<CustomObjectRecipeComponent.Value>> mapCodec() {
      return new MapCodec<List<CustomObjectRecipeComponent.Value>>() {
         public <T> Stream<T> keys(DynamicOps<T> ops) {
            return CustomObjectRecipeComponent.this.keys.stream().map(CustomObjectRecipeComponent.Key::name).map(ops::createString);
         }

         public <T> DataResult<List<CustomObjectRecipeComponent.Value>> decode(DynamicOps<T> ops, MapLike<T> input) {
            List<CustomObjectRecipeComponent.Value> list = new ArrayList<>(CustomObjectRecipeComponent.this.keys.size());
            Map<String, CustomObjectRecipeComponent.Key> keyMap = new HashMap<>();
            CustomObjectRecipeComponent.this.keys.forEach(key -> keyMap.put(key.name, key));
            Builder<Pair<T, T>> failed = Stream.builder();
            DataResult<Unit> result = input.entries()
               .reduce(
                  DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                  (r, pair) -> {
                     DataResult<CustomObjectRecipeComponent.Key> keyResult = ops.getStringValue(pair.getFirst())
                        .flatMap(k -> keyMap.containsKey(k) ? DataResult.success(keyMap.get(k)) : DataResult.error(() -> "Unknown key in custom object: " + k));
                     DataResult<?> valueResult = keyResult.map(k -> k.component.codec())
                        .flatMap(codec -> codec.decode(ops, pair.getSecond()))
                        .map(Pair::getFirst);
                     DataResult<CustomObjectRecipeComponent.Value> entryResult = keyResult.apply2stable(
                        (k, v) -> new CustomObjectRecipeComponent.Value(k, CustomObjectRecipeComponent.this.keys.indexOf(k), v), valueResult
                     );
                     entryResult.resultOrPartial().ifPresent(list::add);
                     if (entryResult.isError()) {
                        failed.add((Pair<T, T>)pair);
                     }

                     return r.apply2stable((u, p) -> u, entryResult);
                  },
                  (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
               );
            if (list.size() >= 2) {
               list.sort(null);
            }

            T errors = (T)ops.createMap(failed.build());
            return result.map(unit -> list).setPartial(list).mapError(e -> e + " missed input: " + errors);
         }

         public <T> RecordBuilder<T> encode(List<CustomObjectRecipeComponent.Value> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();

            for (CustomObjectRecipeComponent.Value entry : input) {
               builder.add(ops.createString(entry.key.name), entry.key.component.codec().encodeStart(ops, Cast.to(entry.value)));
            }

            return builder;
         }
      };
   }

   @Override
   public Codec<List<CustomObjectRecipeComponent.Value>> codec() {
      if (this.codec == null) {
         this.codec = this.mapCodec().codec();
      }

      return this.codec;
   }

   @Override
   public TypeInfo typeInfo() {
      if (this.typeInfo == null) {
         ArrayList<JSOptionalParam> list = new ArrayList<>(this.keys.size());

         for (CustomObjectRecipeComponent.Key key : this.keys) {
            list.add(new JSOptionalParam(key.name, key.component.typeInfo(), key.optional()));
         }

         this.typeInfo = new JSObjectTypeInfo(list);
      }

      return this.typeInfo;
   }

   @Override
   public boolean hasPriority(RecipeMatchContext cx, Object from) {
      if (from instanceof Map m) {
         if (this.hasPriority != null) {
            return this.hasPriority.test(m.keySet());
         } else {
            for (CustomObjectRecipeComponent.Key key : this.keys) {
               if (!key.optional() && !m.containsKey(key.name)) {
                  return false;
               }
            }

            return true;
         }
      } else if (from instanceof JsonObject json) {
         if (this.hasPriority != null) {
            return this.hasPriority.test(json.keySet());
         } else {
            for (CustomObjectRecipeComponent.Key keyx : this.keys) {
               if (!keyx.optional() && !json.has(keyx.name)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean matches(RecipeMatchContext cx, List<CustomObjectRecipeComponent.Value> value, ReplacementMatchInfo match) {
      for (CustomObjectRecipeComponent.Value e : value) {
         if (((RecipeComponent<Object>)e.key.component).matches(cx, Cast.to(e.value), match)) {
            return true;
         }
      }

      return false;
   }

   public List<CustomObjectRecipeComponent.Value> replace(
      RecipeScriptContext cx, List<CustomObjectRecipeComponent.Value> original, ReplacementMatchInfo match, Object with
   ) {
      List<CustomObjectRecipeComponent.Value> replaced = original;

      for (CustomObjectRecipeComponent.Value e : original) {
         Object r = ((RecipeComponent<Object>)e.key.component).replace(cx, Cast.to(e.value), match, with);
         if (r != e.value) {
            if (replaced == original) {
               replaced = new ArrayList<>(original);
            }

            replaced.set(e.index, new CustomObjectRecipeComponent.Value(e.key, e.index, r));
         }
      }

      return replaced;
   }

   public void buildUniqueId(UniqueIdBuilder builder, List<CustomObjectRecipeComponent.Value> list) {
      boolean first = true;

      for (CustomObjectRecipeComponent.Value value : list) {
         if (value.value != null) {
            if (first) {
               first = false;
            } else {
               builder.appendSeparator();
            }

            ((RecipeComponent<Object>)value.key.component).buildUniqueId(builder, Cast.to(value.value));
         }
      }
   }

   public void validate(RecipeValidationContext ctx, List<CustomObjectRecipeComponent.Value> value) {
      RecipeComponent.super.validate(ctx, value);
      ctx.errors().push(this);

      for (CustomObjectRecipeComponent.Value entry : value) {
         ctx.errors().setKey(entry.key.name);
         ((RecipeComponent<Object>)entry.key.component).validate(ctx, Cast.to(entry.value));
      }

      ctx.errors().pop();
   }

   public boolean isEmpty(List<CustomObjectRecipeComponent.Value> value) {
      return this.keys.isEmpty();
   }

   @Override
   public String toString() {
      return this.keys.stream().map(CustomObjectRecipeComponent.Key::toString).collect(Collectors.joining(", ", "{", "}"));
   }

   public record Key(String name, RecipeComponent<?> component, boolean optional, boolean alwaysWrite) {
      public Key(String name, RecipeComponent<?> component, boolean optional) {
         this(name, component, optional, false);
      }

      public Key(String name, RecipeComponent<?> component) {
         this(name, component, false);
      }

      public static Codec<CustomObjectRecipeComponent.Key> createCodec(RecipeTypeRegistryContext ctx) {
         return RecordCodecBuilder.create(
            instance -> instance.group(
                  Codec.STRING.fieldOf("name").forGetter(CustomObjectRecipeComponent.Key::name),
                  ctx.recipeComponentCodec().fieldOf("component").forGetter(CustomObjectRecipeComponent.Key::component),
                  Codec.BOOL.optionalFieldOf("optional", false).forGetter(CustomObjectRecipeComponent.Key::optional),
                  Codec.BOOL.optionalFieldOf("always_write", false).forGetter(CustomObjectRecipeComponent.Key::alwaysWrite)
               )
               .apply(instance, CustomObjectRecipeComponent.Key::new)
         );
      }

      @Override
      public String toString() {
         return this.name + (this.optional ? "?" : "") + (this.alwaysWrite ? "!" : "") + ": " + this.component;
      }
   }

   public record Value(CustomObjectRecipeComponent.Key key, int index, Object value)
      implements Entry<CustomObjectRecipeComponent.Key, Object>,
      Comparable<CustomObjectRecipeComponent.Value> {
      public CustomObjectRecipeComponent.Key getKey() {
         return this.key;
      }

      @Override
      public Object getValue() {
         return this.value;
      }

      @Override
      public Object setValue(Object object) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(@NotNull CustomObjectRecipeComponent.Value value) {
         return Integer.compare(this.index, value.index);
      }
   }
}
