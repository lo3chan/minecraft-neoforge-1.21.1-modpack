package net.joefoxe.hexerei.util.message;

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

public class CrowFluteCommandSyncToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteCommandSyncToServer> CODEC = StreamCodec.ofMember(
      CrowFluteCommandSyncToServer::encode, CrowFluteCommandSyncToServer::new
   );
   public static final Type<CrowFluteCommandSyncToServer> TYPE = new Type(HexereiUtil.getResource("crow_flute_command_server"));
   ItemStack flute;
   int command;
   UUID entityId;
   int hand;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CrowFluteCommandSyncToServer(ItemStack flute, int command, UUID entityId, int hand) {
      this.flute = flute;
      this.command = command;
      this.entityId = entityId;
      this.hand = hand;
   }

   public CrowFluteCommandSyncToServer(RegistryFriendlyByteBuf buf) {
      this.flute = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
      this.command = buf.readInt();
      this.entityId = buf.readUUID();
      this.hand = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      ItemStack.STREAM_CODEC.encode(buffer, this.flute);
      buffer.writeInt(this.command);
      buffer.writeUUID(this.entityId);
      buffer.writeInt(this.hand);
   }

   public static CrowFluteCommandSyncToServer decode(RegistryFriendlyByteBuf buffer) {
      return new CrowFluteCommandSyncToServer(buffer);
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
                  this.command, fluteData.helpCommandSelected(), fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2()
               );
               stackx.set(ModDataComponents.FLUTE, fluteData);
               stack.setItemInHand(InteractionHand.MAIN_HAND, stackx);
            }
         } else {
            ItemStack stackx = stack.getOffhandItem();
            if (stack.getOffhandItem().getItem() == this.flute.getItem()) {
               FluteData fluteData = (FluteData)stackx.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
               fluteData = new FluteData(
                  this.command, fluteData.helpCommandSelected(), fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2()
               );
               stackx.set(ModDataComponents.FLUTE, fluteData);
               stack.setItemInHand(InteractionHand.OFF_HAND, stackx);
            }
         }
      }
   }
}
