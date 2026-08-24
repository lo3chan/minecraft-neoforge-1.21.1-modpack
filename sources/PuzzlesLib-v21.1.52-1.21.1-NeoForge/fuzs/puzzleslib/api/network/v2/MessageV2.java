package fuzs.puzzleslib.api.network.v2;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.network.ClientboundLegacyMessageAdapter;
import fuzs.puzzleslib.impl.network.ServerboundLegacyMessageAdapter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface MessageV2<T extends MessageV2<T>> {
   void write(FriendlyByteBuf var1);

   void read(FriendlyByteBuf var1);

   MessageV2.MessageHandler<T> makeHandler();

   default ClientboundMessage<T> toClientboundMessage() {
      return new ClientboundLegacyMessageAdapter<>((T)this);
   }

   default ServerboundMessage<T> toServerboundMessage() {
      return new ServerboundLegacyMessageAdapter<>((T)this);
   }

   public abstract static class MessageHandler<T extends MessageV2<T>> {
      public abstract void handle(T var1, Player var2, Object var3);
   }
}
