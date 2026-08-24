package mezz.jei.common.network.packets;

import mezz.jei.common.network.ClientPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public abstract class PlayToClientPacket<T extends PlayToClientPacket<T>> implements CustomPacketPayload {
   public abstract Type<T> type();

   public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

   public abstract void process(ClientPacketContext var1);
}
