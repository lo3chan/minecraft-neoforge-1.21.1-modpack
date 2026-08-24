package fuzs.puzzleslib.impl.init;

import java.util.Objects;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Holder.Reference.Type;
import net.minecraft.resources.ResourceKey;

public final class DirectReferenceHolder<T> extends Reference<T> {
   public DirectReferenceHolder(ResourceKey<T> key, T value) {
      super(Type.STAND_ALONE, null, key, value);
      Objects.requireNonNull(key, "key is null");
      Objects.requireNonNull(value, "value is null");
   }

   public boolean canSerializeIn(HolderOwner<T> owner) {
      return true;
   }

   public void bindValue(T value) {
      throw new UnsupportedOperationException();
   }
}
