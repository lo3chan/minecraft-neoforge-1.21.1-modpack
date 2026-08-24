package vazkii.psi.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public record MessagePsiOverflow(boolean overflow) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_psi_overflow");
   public static final Type<MessagePsiOverflow> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessagePsiOverflow> CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, MessagePsiOverflow::overflow, MessagePsiOverflow::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = Psi.proxy.getClientPlayer();
         if (player != null) {
            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
            data.overflowed = this.overflow;
         }
      });
   }
}
