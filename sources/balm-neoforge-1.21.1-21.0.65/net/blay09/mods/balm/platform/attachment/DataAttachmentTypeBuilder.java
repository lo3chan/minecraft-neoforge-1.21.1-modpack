package net.blay09.mods.balm.platform.attachment;

import com.mojang.serialization.Codec;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public interface DataAttachmentTypeBuilder<T> {
   DataAttachmentTypeBuilder<T> initializer(Supplier<T> var1);

   DataAttachmentTypeBuilder<T> persistent(Codec<T> var1);

   default DataAttachmentTypeBuilder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
      return this.networkSynchronized(streamCodec, (ignored, player) -> true);
   }

   DataAttachmentTypeBuilder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> var1, BiPredicate<Object, ServerPlayer> var2);

   DataAttachmentTypeBuilder<T> copyOnDeath();
}
