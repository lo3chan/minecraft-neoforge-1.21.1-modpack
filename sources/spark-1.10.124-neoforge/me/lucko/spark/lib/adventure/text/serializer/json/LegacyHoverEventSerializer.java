package me.lucko.spark.lib.adventure.text.serializer.json;

import java.io.IOException;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

public interface LegacyHoverEventSerializer {
   @NotNull
   HoverEvent.ShowItem deserializeShowItem(@NotNull Component input) throws IOException;

   @NotNull
   Component serializeShowItem(@NotNull HoverEvent.ShowItem input) throws IOException;

   @NotNull
   HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentDecoder) throws IOException;

   @NotNull
   Component serializeShowEntity(@NotNull HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentEncoder) throws IOException;
}
