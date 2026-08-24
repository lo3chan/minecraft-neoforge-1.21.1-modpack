package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.error.RecipeComponentTooLargeException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.IntBounds;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

public record ListRecipeComponent<T>(
   RecipeComponent<T> component,
   boolean canWriteSelf,
   TypeInfo listTypeInfo,
   Codec<List<T>> listCodec,
   boolean conditional,
   IntBounds bounds,
   Optional<RecipeComponent<?>> spread,
   Optional<RecipeComponent<?>> spreadWrap
) implements RecipeComponent<List<T>> {
   public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(
      KubeJS.id("list"),
      (RecipeComponentCodecFactory)((type, ctx) -> RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ctx.recipeComponentCodec().fieldOf("component").forGetter(ListRecipeComponent::component),
               Codec.BOOL.optionalFieldOf("can_write_self", false).forGetter(ListRecipeComponent::canWriteSelf),
               Codec.BOOL.optionalFieldOf("conditional", false).forGetter(ListRecipeComponent::conditional),
               IntBounds.MAP_CODEC.forGetter(ListRecipeComponent::bounds),
               ctx.recipeComponentCodec().optionalFieldOf("spread").forGetter(ListRecipeComponent::spread)
            )
            .apply(instance, ListRecipeComponent::create)
      ))
   );

   public static <L> ListRecipeComponent<L> create(RecipeComponent<L> component, boolean canWriteSelf, boolean conditional) {
      return create(component, canWriteSelf, conditional, IntBounds.DEFAULT, Optional.empty());
   }

   public static <L> ListRecipeComponent<L> create(
      RecipeComponent<L> component, boolean canWriteSelf, boolean conditional, IntBounds bounds, Optional<RecipeComponent<?>> spread
   ) {
      TypeInfo typeInfo = component.typeInfo();
      Codec<L> codec = component.codec();
      Codec<List<L>> listCodec = conditional ? NeoForgeExtraCodecs.listWithOptionalElements(ConditionalOps.createConditionalCodec(codec)) : codec.listOf();
      TypeInfo listTypeInfo = TypeInfo.RAW_LIST.withParams(new TypeInfo[]{typeInfo});
      if (canWriteSelf) {
         listCodec = KubeJSCodecs.listOfOrSelf((Codec<List<T>>)listCodec, (Codec<T>)codec);
         listTypeInfo = listTypeInfo.or(typeInfo);
      }

      Optional<RecipeComponent<?>> spreadWrap = wrapSpread(component, spread);
      return new ListRecipeComponent<>(component, canWriteSelf, listTypeInfo, listCodec, conditional, bounds, spread, spreadWrap);
   }

   @NotNull
   private static <L> Optional<RecipeComponent<?>> wrapSpread(RecipeComponent<L> component, Optional<RecipeComponent<?>> spread) {
      Optional<RecipeComponent<?>> spreadWrap = spread;
      if (spread.isPresent()
         && component instanceof EitherRecipeComponent<?, ?> either
         && spread.get() instanceof EitherRecipeComponent<?, ?> seither
         && (seither.left().isIgnored() || seither.right().isIgnored())) {
         spreadWrap = Optional.of(seither.left().isIgnored() ? either.left().or(seither.right()) : seither.left().or(either.right()));
      }

      return spreadWrap;
   }

   @Override
   public RecipeComponentType<?> type() {
      return TYPE;
   }

   @Override
   public Codec<List<T>> codec() {
      return this.listCodec;
   }

   @Override
   public TypeInfo typeInfo() {
      return this.listTypeInfo;
   }

   @Override
   public boolean hasPriority(RecipeMatchContext cx, Object from) {
      return from instanceof Iterable || from != null && from.getClass().isArray();
   }

   public static <T> List<T> wrap0(RecipeScriptContext cx, RecipeComponent<T> component, Object from) {
      if (from instanceof Iterable<?> iterable) {
         int size;
         if (iterable instanceof Collection<?> c) {
            size = c.size();
         } else if (iterable instanceof JsonArray a) {
            size = a.size();
         } else {
            size = -1;
         }

         if (size == 0) {
            return List.of();
         } else if (size == 1) {
            return List.of(component.wrap(cx, iterable.iterator().next()));
         } else if (size == 2) {
            Iterator<?> itr = iterable.iterator();
            return List.of(component.wrap(cx, itr.next()), component.wrap(cx, itr.next()));
         } else if (size > 0) {
            ArrayList<T> arr = new ArrayList<>(size);

            for (Object e : iterable) {
               arr.add(component.wrap(cx, e));
            }

            return arr;
         } else {
            ArrayList<T> list = new ArrayList<>();

            for (Object e : iterable) {
               list.add(component.wrap(cx, e));
            }

            return list;
         }
      } else if (!from.getClass().isArray()) {
         return List.of(component.wrap(cx, from));
      } else {
         int length = Array.getLength(from);
         if (length == 0) {
            return List.of();
         } else {
            ArrayList<T> arr = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
               arr.add(component.wrap(cx, Array.get(from, i)));
            }

            return arr;
         }
      }
   }

   public List<T> wrap(RecipeScriptContext cx, Object from) {
      RecipeComponent<?> spreadComponent = this.spread.orElse(null);
      if (spreadComponent != null && this.spreadWrap.isPresent()) {
         List<?> original = wrap0(cx, this.spreadWrap.get(), from);
         ArrayList<T> result = new ArrayList<>();

         for (Object o : original) {
            for (Object s : ((RecipeComponent<Object>)spreadComponent).spread(Cast.to(o))) {
               result.add(this.component.wrap(cx, s));
            }
         }

         return result;
      } else {
         return wrap0(cx, this.component, from);
      }
   }

   public boolean matches(RecipeMatchContext cx, List<T> value, ReplacementMatchInfo match) {
      for (T v : value) {
         if (this.component.matches(cx, v, match)) {
            return true;
         }
      }

      return false;
   }

   public List<T> replace(RecipeScriptContext cx, List<T> original, ReplacementMatchInfo match, Object with) {
      List<T> arr = original;

      for (int i = 0; i < original.size(); i++) {
         T r = this.component.replace(cx, original.get(i), match, with);
         if (arr.get(i) != r) {
            if (arr == original) {
               arr = new ArrayList<>(original);
            }

            if (arr != original) {
               arr.set(i, r);
            }
         }
      }

      return arr;
   }

   public void buildUniqueId(UniqueIdBuilder builder, List<T> value) {
      for (int i = 0; i < value.size(); i++) {
         if (i > 0) {
            builder.appendSeparator();
         }

         this.component.buildUniqueId(builder, value.get(i));
      }
   }

   @Override
   public String toString() {
      return this.component + (this.canWriteSelf ? "[?]" : "[]") + (this.conditional ? "?" : "");
   }

   public void validate(RecipeValidationContext ctx, List<T> value) {
      RecipeComponent.super.validate(ctx, (T)value);
      if (value.size() > this.bounds.max()) {
         throw new RecipeComponentTooLargeException(this, value, value.size(), this.bounds.max());
      } else {
         ctx.errors().push(this);

         for (int i = 0; i < value.size(); i++) {
            ctx.errors().setKey(i);
            this.component.validate(ctx, value.get(i));
         }

         ctx.errors().pop();
      }
   }

   @Override
   public boolean allowEmpty() {
      return this.bounds.min() <= 0;
   }

   public boolean isEmpty(List<T> value) {
      return value.isEmpty();
   }

   public List<?> spread(List<T> value) {
      return value;
   }

   public ListRecipeComponent<T> withBounds(IntBounds bounds) {
      return create(this.component, this.canWriteSelf, this.conditional, bounds, this.spread);
   }

   public ListRecipeComponent<T> orSelf() {
      return create(this.component, true, this.conditional, this.bounds, this.spread);
   }

   public ListRecipeComponent<T> asConditional() {
      return create(this.component, this.canWriteSelf, true, this.bounds, this.spread);
   }

   public ListRecipeComponent<T> withSpread(Optional<RecipeComponent<?>> spread) {
      return create(this.component, this.canWriteSelf, this.conditional, this.bounds, spread);
   }
}
