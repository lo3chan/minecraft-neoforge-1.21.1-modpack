package fuzs.puzzleslib.api.network.v3;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface MessageV3<T, H> {
   H getHandler();

   @Internal
   default T unwrap() {
      return (T)this;
   }
}
