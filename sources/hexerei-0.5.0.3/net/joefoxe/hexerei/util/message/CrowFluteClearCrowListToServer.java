package net.joefoxe.hexerei.util.message;

import java.util.ArrayList;
import java.util.UUID;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CrowFluteClearCrowListToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteClearCrowListToServer> CODEC = StreamCodec.ofMember(
      CrowFluteClearCrowListToServer::encode, CrowFluteClearCrowListToServer::new
   );
   public static final Type<CrowFluteClearCrowListToServer> TYPE = new Type(HexereiUtil.getResource("crow_flute_clear_list_server"));
   ItemStack flute;
   UUID entityId;
   int hand;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowFluteClearCrowListToServer(ItemStack flute, UUID entityId, int hand) {
      this.flute = flute;
      this.entityId = entityId;
      this.hand = hand;
   }

   public CrowFluteClearCrowListToServer(RegistryFriendlyByteBuf buf) {
      this.flute = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
      this.entityId = buf.readUUID();
      this.hand = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      ItemStack.STREAM_CODEC.encode(buffer, this.flute);
      buffer.writeUUID(this.entityId);
      buffer.writeInt(this.hand);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      Player stack = player.level().getPlayerByUUID(this.entityId);
      if (stack instanceof Player) {
         if (this.hand == 0) {
            ItemStack stackx = stack.getMainHandItem();
            if (stackx.getItem() == this.flute.getItem()) {
               FluteData fluteData = (FluteData)stackx.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
               fluteData = new FluteData(
                  fluteData.commandSelected(),
                  fluteData.helpCommandSelected(),
                  fluteData.commandMode(),
                  new ArrayList<>(),
                  fluteData.dyeColor1(),
                  fluteData.dyeColor2()
               );
               stackx.set(ModDataComponents.FLUTE, fluteData);
               stack.setItemInHand(InteractionHand.MAIN_HAND, stackx);
            }
         } else {
            ItemStack stackx = stack.getOffhandItem();
            if (stack.getOffhandItem().getItem() == this.flute.getItem()) {
               FluteData fluteData = (FluteData)stackx.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
               fluteData = new FluteData(
                  fluteData.commandSelected(),
                  fluteData.helpCommandSelected(),
                  fluteData.commandMode(),
                  new ArrayList<>(),
                  fluteData.dyeColor1(),
                  fluteData.dyeColor2()
               );
               stackx.set(ModDataComponents.FLUTE, fluteData);
               stack.setItemInHand(InteractionHand.OFF_HAND, stackx);
            }
         }
      }
   }
}
