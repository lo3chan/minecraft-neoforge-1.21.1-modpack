package net.joefoxe.hexerei.util.message;

import java.util.UUID;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrowFluteClearCrowPerchToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteClearCrowPerchToServer> CODEC = StreamCodec.ofMember(
      CrowFluteClearCrowPerchToServer::encode, CrowFluteClearCrowPerchToServer::new
   );
   public static final Type<CrowFluteClearCrowPerchToServer> TYPE = new Type(HexereiUtil.getResource("crow_flute_clear_perch_server"));
   ItemStack flute;
   UUID entityId;
   int hand;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowFluteClearCrowPerchToServer(ItemStack flute, UUID entityId, int hand) {
      this.flute = flute;
      this.entityId = entityId;
      this.hand = hand;
   }

   public CrowFluteClearCrowPerchToServer(RegistryFriendlyByteBuf buf) {
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
      if (this.hand == 0) {
         ItemStack stack = player.level().getPlayerByUUID(this.entityId).getMainHandItem();
         if (stack.getItem() == this.flute.getItem()) {
            FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

            for (FluteData.CrowIds crowIds : fluteData.crowList()) {
               if (((ServerLevel)player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity crow) {
                  crow.setPerchPos(null);
               }
            }
         }
      } else {
         ItemStack stack = player.level().getPlayerByUUID(this.entityId).getOffhandItem();
         if (stack.getItem() == this.flute.getItem()) {
            FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

            for (FluteData.CrowIds crowIdsx : fluteData.crowList()) {
               if (((ServerLevel)player.level()).getEntity(crowIdsx.uuid()) instanceof CrowEntity crow) {
                  crow.setPerchPos(null);
               }
            }
         }
      }
   }
}
