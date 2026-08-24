package fuzs.puzzleslib.api.attachment.v4;

import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

public interface DataAttachmentType<T, V> {
   @Nullable
   V get(T var1);

   V getOrDefault(T var1, V var2);

   boolean has(T var1);

   void set(T var1, @Nullable V var2);

   @Nullable
   V remove(T var1);

   void apply(T var1, UnaryOperator<V> var2);

   @Deprecated
   default void update(T holder, UnaryOperator<V> valueUpdater) {
      this.apply(holder, valueUpdater);
   }
}
