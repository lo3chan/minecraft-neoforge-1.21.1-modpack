package me.lucko.spark.lib.adventure.text.event;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.internal.Internals;
import org.jetbrains.annotations.NotNull;

final class ClickCallbackOptionsImpl implements ClickCallback.Options {
   static final ClickCallback.Options DEFAULT = new ClickCallbackOptionsImpl.BuilderImpl().build();
   private final int uses;
   private final Duration lifetime;

   ClickCallbackOptionsImpl(final int uses, final Duration lifetime) {
      this.uses = uses;
      this.lifetime = lifetime;
   }

   @Override
   public int uses() {
      return this.uses;
   }

   @NotNull
   @Override
   public Duration lifetime() {
      return this.lifetime;
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("uses", this.uses), ExaminableProperty.of("expiration", this.lifetime));
   }

   @Override
   public String toString() {
      return Internals.toString(this);
   }

   static final class BuilderImpl implements ClickCallback.Options.Builder {
      private static final int DEFAULT_USES = 1;
      private int uses;
      private Duration lifetime;

      BuilderImpl() {
         this.uses = 1;
         this.lifetime = ClickCallback.DEFAULT_LIFETIME;
      }

      BuilderImpl(@NotNull final ClickCallback.Options existing) {
         this.uses = existing.uses();
         this.lifetime = existing.lifetime();
      }

      @NotNull
      public ClickCallback.Options build() {
         return new ClickCallbackOptionsImpl(this.uses, this.lifetime);
      }

      @NotNull
      @Override
      public ClickCallback.Options.Builder uses(final int uses) {
         this.uses = uses;
         return this;
      }

      @NotNull
      @Override
      public ClickCallback.Options.Builder lifetime(@NotNull final TemporalAmount lifetime) {
         this.lifetime = lifetime instanceof Duration ? (Duration)lifetime : Duration.from(Objects.requireNonNull(lifetime, "lifetime"));
         return this;
      }
   }
}
