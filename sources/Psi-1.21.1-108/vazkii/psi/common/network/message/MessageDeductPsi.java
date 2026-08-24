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

public record MessageDeductPsi(int prev, int current, int cd, boolean shatter) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_deduct_psi");
   public static final Type<MessageDeductPsi> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageDeductPsi> CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      MessageDeductPsi::prev,
      ByteBufCodecs.INT,
      MessageDeductPsi::current,
      ByteBufCodecs.INT,
      MessageDeductPsi::cd,
      ByteBufCodecs.BOOL,
      MessageDeductPsi::shatter,
      MessageDeductPsi::new
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
            data.lastAvailablePsi = data.availablePsi;
            data.availablePsi = this.current;
            data.regenCooldown = this.cd;
            data.deductTick = true;
            data.addDeduction(this.prev, this.prev - this.current, this.shatter);
         }
      });
   }
}
