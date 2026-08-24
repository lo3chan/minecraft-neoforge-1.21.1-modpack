package me.lucko.spark.api.placeholder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PlaceholderResolver {
   @Nullable
   String resolveLegacyFormatting(@NonNull String var1);

   @Nullable
   String resolveComponentJson(@NonNull String var1);
}
