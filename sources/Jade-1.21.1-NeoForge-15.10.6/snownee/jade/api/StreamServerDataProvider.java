package snownee.jade.api;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public interface StreamServerDataProvider<T extends Accessor<?>, D> extends IServerDataProvider<T> {
   @Override
   default void appendServerData(CompoundTag data, T accessor) {
      D value = this.streamData(accessor);
      if (value != null) {
         data.put(this.getUid().toString(), accessor.encodeAsNbt(this.streamCodec(), value));
      }
   }

   default Optional<D> decodeFromData(T accessor) {
      Tag tag = accessor.getServerData().get(this.getUid().toString());
      return tag == null ? Optional.empty() : accessor.decodeFromNbt(this.streamCodec(), tag);
   }

   @Nullable
   D streamData(T var1);

   StreamCodec<RegistryFriendlyByteBuf, D> streamCodec();
}
