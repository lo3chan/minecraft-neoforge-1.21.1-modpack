package me.lucko.spark.lib.adventure.resource;

import java.util.UUID;
import java.util.function.BiConsumer;
import me.lucko.spark.lib.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ResourcePackCallback {
   @NotNull
   static ResourcePackCallback noOp() {
      return ResourcePackCallbacks.NO_OP;
   }

   @NotNull
   static ResourcePackCallback onTerminal(@NotNull final BiConsumer<UUID, Audience> success, @NotNull final BiConsumer<UUID, Audience> failure) {
      return (uuid, status, audience) -> {
         if (status == ResourcePackStatus.SUCCESSFULLY_LOADED) {
            success.accept(uuid, audience);
         } else if (!status.intermediate()) {
            failure.accept(uuid, audience);
         }
      };
   }

   void packEventReceived(@NotNull final UUID uuid, @NotNull final ResourcePackStatus status, @NotNull final Audience audience);
}
