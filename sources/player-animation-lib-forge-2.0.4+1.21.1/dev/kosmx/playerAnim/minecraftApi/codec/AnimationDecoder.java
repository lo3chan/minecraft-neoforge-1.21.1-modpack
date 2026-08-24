package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AnimationDecoder<T extends IPlayable> {
   @NotNull
   Collection<T> decode(@NotNull InputStream var1) throws IOException;
}
