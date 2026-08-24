package net.mehvahdjukaar.moonlight.api.util.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record EnumStreamCodec<T extends Enum<T>>(Class<T> enumClass) implements StreamCodec<FriendlyByteBuf, T> {
   public T decode(FriendlyByteBuf buf) {
      return (T)buf.readEnum(this.enumClass);
   }

   public void encode(FriendlyByteBuf buf, T e) {
      buf.writeEnum(e);
   }

   @NotNull
   @Override
   public String toString() {
      return "EnumStreamCodec[" + this.enumClass.getSimpleName() + "]";
   }
}
