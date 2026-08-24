package me.lucko.spark.lib.adventure.nbt.api;

import java.util.Objects;
import me.lucko.spark.lib.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

final class BinaryTagHolderImpl implements BinaryTagHolder {
   private final String string;

   BinaryTagHolderImpl(final String string) {
      this.string = Objects.requireNonNull(string, "string");
   }

   @NotNull
   @Override
   public String string() {
      return this.string;
   }

   @NotNull
   @Override
   public <T, DX extends Exception> T get(@NotNull final Codec<T, String, DX, ?> codec) throws DX {
      return codec.decode(this.string);
   }

   @Override
   public int hashCode() {
      return 31 * this.string.hashCode();
   }

   @Override
   public boolean equals(final Object that) {
      return !(that instanceof BinaryTagHolderImpl) ? false : this.string.equals(((BinaryTagHolderImpl)that).string);
   }

   @Override
   public String toString() {
      return this.string;
   }
}
