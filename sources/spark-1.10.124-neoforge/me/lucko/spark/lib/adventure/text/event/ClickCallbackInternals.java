package me.lucko.spark.lib.adventure.text.event;

import me.lucko.spark.lib.adventure.audience.Audience;
import me.lucko.spark.lib.adventure.permission.PermissionChecker;
import me.lucko.spark.lib.adventure.util.Services;
import me.lucko.spark.lib.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;

final class ClickCallbackInternals {
   static final PermissionChecker ALWAYS_FALSE = PermissionChecker.always(TriState.FALSE);
   static final ClickCallback.Provider PROVIDER = Services.service(ClickCallback.Provider.class).orElseGet(ClickCallbackInternals.Fallback::new);

   private ClickCallbackInternals() {
   }

   static final class Fallback implements ClickCallback.Provider {
      @NotNull
      @Override
      public ClickEvent create(@NotNull final ClickCallback<Audience> callback, @NotNull final ClickCallback.Options options) {
         return ClickEvent.suggestCommand("Callbacks are not supported on this platform!");
      }
   }
}
