package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface PayloadTypesContext {
   <T extends ClientboundPlayMessage> void playToClient(Class<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2);

   <T extends ServerboundPlayMessage> void playToServer(Class<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2);

   <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> var1, StreamCodec<? super FriendlyByteBuf, T> var2);

   <T extends ServerboundConfigurationMessage> void configurationToServer(Class<T> var1, StreamCodec<? super FriendlyByteBuf, T> var2);

   <T extends ClientboundPlayMessage> void playToClient(Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2);

   <T extends ServerboundPlayMessage> void playToServer(Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2);

   <T extends ClientboundConfigurationMessage> void configurationToClient(Type<T> var1, StreamCodec<? super FriendlyByteBuf, T> var2);

   <T extends ServerboundConfigurationMessage> void configurationToServer(Type<T> var1, StreamCodec<? super FriendlyByteBuf, T> var2);

   void optional();
}
