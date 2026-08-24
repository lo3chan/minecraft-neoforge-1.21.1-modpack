package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import java.util.Optional;

public record EitherRecipeComponent<H, L>(RecipeComponent<H> left, RecipeComponent<L> right, Codec<Either<H, L>> codec, TypeInfo typeInfo)
   implements RecipeComponent<Either<H, L>> {
   public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(
      KubeJS.id("either"),
      (RecipeComponentCodecFactory)((type, ctx) -> RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ctx.recipeComponentCodec().fieldOf("left").forGetter(EitherRecipeComponent::left),
               ctx.recipeComponentCodec().fieldOf("right").forGetter(EitherRecipeComponent::right)
            )
            .apply(instance, EitherRecipeComponent::new)
      ))
   );

   public EitherRecipeComponent(RecipeComponent<H> left, RecipeComponent<L> right) {
      this(left, right, Codec.either(left.codec(), right.codec()), left.typeInfo().or(right.typeInfo()));
   }

   @Override
   public RecipeComponentType<?> type() {
      return TYPE;
   }

   public Either<H, L> wrap(RecipeScriptContext cx, Object from) {
      if (this.left.hasPriority(cx, from)) {
         H value = this.left.wrap(cx, from);
         if (this.left.allowEmpty() || !this.left.isEmpty(value)) {
            return Either.left(value);
         }
      }

      if (this.right.hasPriority(cx, from)) {
         L value = this.right.wrap(cx, from);
         if (this.right.allowEmpty() || !this.right.isEmpty(value)) {
            return Either.right(value);
         }
      }

      Exception ex1 = null;

      try {
         H value = this.left.wrap(cx, from);
         if (this.left.allowEmpty() || !this.left.isEmpty(value)) {
            this.left.validate(cx, value);
            return Either.left(value);
         }
      } catch (Exception var6) {
         ex1 = var6;
      }

      try {
         L value = this.right.wrap(cx, from);
         if (this.right.allowEmpty() || !this.right.isEmpty(value)) {
            return Either.right(value);
         }
      } catch (Exception var5) {
         ConsoleJS.SERVER.warn("Failed to read %s (left: %s)!".formatted(from, this.left), ex1);
         ConsoleJS.SERVER.warn("Failed to read %s (right: %s)!".formatted(from, this.right), var5);
      }

      throw new KubeRuntimeException("Failed to read %s as either %s or %s!".formatted(from, this.left, this.right)).source(cx.recipe().sourceLine);
   }

   public boolean matches(RecipeMatchContext cx, Either<H, L> value, ReplacementMatchInfo match) {
      Optional<H> l = value.left();
      return l.isPresent() ? this.left.matches(cx, l.get(), match) : this.right.matches(cx, (L)value.right().get(), match);
   }

   public Either<H, L> replace(RecipeScriptContext cx, Either<H, L> original, ReplacementMatchInfo match, Object with) {
      Optional<H> l = original.left();
      if (l.isPresent()) {
         H r = this.left.replace(cx, l.get(), match, with);
         return r == l.get() ? original : Either.left(r);
      } else {
         L r = this.right.replace(cx, (L)original.right().get(), match, with);
         return r == original.right().get() ? original : Either.right(r);
      }
   }

   public void buildUniqueId(UniqueIdBuilder builder, Either<H, L> value) {
      Optional<H> l = value.left();
      if (l.isPresent()) {
         this.left.buildUniqueId(builder, l.get());
      } else {
         this.right.buildUniqueId(builder, (L)value.right().get());
      }
   }

   public void validate(RecipeValidationContext ctx, Either<H, L> value) {
      ctx.errors().push(this);
      Optional<H> l = value.left();
      if (l.isPresent()) {
         ctx.errors().setKey("left");
         this.left.validate(ctx, l.get());
      } else {
         ctx.errors().setKey("right");
         this.right.validate(ctx, (L)value.right().get());
      }

      ctx.errors().pop();
   }

   @Override
   public String toString() {
      return "either<" + this.left + ", " + this.right + ">";
   }

   public String toString(OpsContainer ops, Either<H, L> value) {
      Optional<H> l = value.left();
      return l.isPresent() ? this.left.toString(ops, l.get()) : this.right.toString(ops, (L)value.right().get());
   }

   public List<?> spread(Either<H, L> value) {
      Optional<H> l = value.left();
      return l.isPresent() ? this.left.spread(l.get()) : this.right.spread((L)value.right().get());
   }
}
