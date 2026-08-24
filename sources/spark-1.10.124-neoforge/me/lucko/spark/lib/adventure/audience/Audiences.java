package me.lucko.spark.lib.adventure.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import me.lucko.spark.lib.adventure.resource.ResourcePackCallback;
import me.lucko.spark.lib.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public final class Audiences {
   static final Collector<? super Audience, ?, ForwardingAudience> COLLECTOR = Collectors.collectingAndThen(
      Collectors.toCollection(ArrayList::new), audiences -> Audience.audience(Collections.unmodifiableCollection(audiences))
   );

   private Audiences() {
   }

   @NotNull
   public static Consumer<? super Audience> sendingMessage(@NotNull final ComponentLike message) {
      return audience -> audience.sendMessage(message);
   }

   @NotNull
   static ResourcePackCallback unwrapCallback(final Audience forwarding, final Audience dest, @NotNull final ResourcePackCallback cb) {
      return cb == ResourcePackCallback.noOp() ? cb : (uuid, status, audience) -> cb.packEventReceived(uuid, status, audience == dest ? forwarding : audience);
   }
}
