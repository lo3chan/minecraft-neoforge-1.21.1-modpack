package fuzs.puzzleslib.api.network.v2;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public interface WritableMessage<T extends MessageV2<T>> extends MessageV2<T> {
   @NonExtendable
   @Override
   default void read(FriendlyByteBuf friendlyByteBuf) {
      throw new UnsupportedOperationException();
   }
}
