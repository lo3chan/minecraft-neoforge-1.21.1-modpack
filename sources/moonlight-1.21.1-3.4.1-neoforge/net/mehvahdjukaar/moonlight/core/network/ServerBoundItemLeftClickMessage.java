package net.mehvahdjukaar.moonlight.core.network;

import net.mehvahdjukaar.moonlight.api.item.ILeftClickReact;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ServerBoundItemLeftClickMessage(InteractionHand hand) implements Message {
   public static final TypeAndCodec<RegistryFriendlyByteBuf, ServerBoundItemLeftClickMessage> TYPE = Message.makeType(
      Moonlight.res("c2s_item_left_click"), ServerBoundItemLeftClickMessage::new
   );

   public ServerBoundItemLeftClickMessage(RegistryFriendlyByteBuf buf) {
      this((InteractionHand)buf.readEnum(InteractionHand.class));
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeEnum(this.hand);
   }

   @Override
   public void handle(Message.Context context) {
      Player player = context.getPlayer();
      ItemStack stack = player.getItemInHand(this.hand);
      if (stack.getItem() instanceof ILeftClickReact lr) {
         lr.onLeftClick(stack, player, this.hand);
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE.type();
   }
}
