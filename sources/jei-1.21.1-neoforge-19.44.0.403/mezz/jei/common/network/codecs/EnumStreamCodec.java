package mezz.jei.common.network.codecs;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EnumStreamCodec<T extends Enum<T>> implements StreamCodec<FriendlyByteBuf, T> {
   private final Class<T> enumClass;

   public EnumStreamCodec(Class<T> enumClass) {
      this.enumClass = enumClass;
   }

   public T decode(FriendlyByteBuf buf) {
      return (T)buf.readEnum(this.enumClass);
   }

   public void encode(FriendlyByteBuf buf, T e) {
      buf.writeEnum(e);
   }
}
