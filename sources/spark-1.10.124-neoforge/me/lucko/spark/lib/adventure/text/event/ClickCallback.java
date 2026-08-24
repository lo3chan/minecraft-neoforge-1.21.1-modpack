package me.lucko.spark.lib.adventure.text.event;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.lucko.spark.lib.adventure.audience.Audience;
import me.lucko.spark.lib.adventure.builder.AbstractBuilder;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.permission.PermissionChecker;
import me.lucko.spark.lib.adventure.util.PlatformAPI;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@FunctionalInterface
public interface ClickCallback<T extends Audience> {
   Duration DEFAULT_LIFETIME = Duration.ofHours(12L);
   int UNLIMITED_USES = -1;

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   static <W extends Audience, N extends W> ClickCallback<W> widen(
      @NotNull final ClickCallback<N> original, @NotNull final Class<N> type, @Nullable final Consumer<? super Audience> otherwise
   ) {
      return audience -> {
         if (type.isInstance(audience)) {
            original.accept((N)((Audience)type.cast(audience)));
         } else if (otherwise != null) {
            otherwise.accept(audience);
         }
      };
   }

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   static <W extends Audience, N extends W> ClickCallback<W> widen(@NotNull final ClickCallback<N> original, @NotNull final Class<N> type) {
      return widen(original, type, null);
   }

   void accept(@NotNull final T audience);

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   default ClickCallback<T> filter(@NotNull final Predicate<T> filter) {
      return this.filter(filter, null);
   }

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   default ClickCallback<T> filter(@NotNull final Predicate<T> filter, @Nullable final Consumer<? super Audience> otherwise) {
      return audience -> {
         if (filter.test(audience)) {
            this.accept(audience);
         } else if (otherwise != null) {
            otherwise.accept(audience);
         }
      };
   }

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   default ClickCallback<T> requiringPermission(@NotNull final String permission) {
      return this.requiringPermission(permission, null);
   }

   @CheckReturnValue
   @Contract(
      pure = true
   )
   @NotNull
   default ClickCallback<T> requiringPermission(@NotNull final String permission, @Nullable final Consumer<? super Audience> otherwise) {
      return this.filter(audience -> audience.getOrDefault(PermissionChecker.POINTER, ClickCallbackInternals.ALWAYS_FALSE).test(permission), otherwise);
   }

   @NonExtendable
   public interface Options extends Examinable {
      @NotNull
      static ClickCallback.Options.Builder builder() {
         return new ClickCallbackOptionsImpl.BuilderImpl();
      }

      @NotNull
      static ClickCallback.Options.Builder builder(@NotNull final ClickCallback.Options existing) {
         return new ClickCallbackOptionsImpl.BuilderImpl(existing);
      }

      int uses();

      @NotNull
      Duration lifetime();

      @NonExtendable
      public interface Builder extends AbstractBuilder<ClickCallback.Options> {
         @NotNull
         ClickCallback.Options.Builder uses(int useCount);

         @NotNull
         ClickCallback.Options.Builder lifetime(@NotNull final TemporalAmount duration);
      }
   }

   @PlatformAPI
   @Internal
   public interface Provider {
      @NotNull
      ClickEvent create(@NotNull final ClickCallback<Audience> callback, @NotNull final ClickCallback.Options options);
   }
}
