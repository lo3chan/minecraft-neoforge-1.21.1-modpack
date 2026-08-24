package tannyjung.tanshugetrees.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tannyjung.tanshugetrees.TanshugetreesMod;
import tannyjung.tanshugetrees.procedures.TreeSummonerStaffGUIApplyProcedure;

@EventBusSubscriber
public record TreeSummonerStaffGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
   public static final Type<TreeSummonerStaffGUIButtonMessage> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("tanshugetrees", "tree_summoner_staff_gui_buttons")
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, TreeSummonerStaffGUIButtonMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }, buffer -> new TreeSummonerStaffGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

   public Type<TreeSummonerStaffGUIButtonMessage> type() {
      return TYPE;
   }

   public static void handleData(TreeSummonerStaffGUIButtonMessage message, IPayloadContext context) {
      if (context.flow() == PacketFlow.SERVERBOUND) {
         context.enqueueWork(() -> handleButtonAction(context.player(), message.buttonID, message.x, message.y, message.z)).exceptionally(e -> {
            context.connection().disconnect(Component.literal(e.getMessage()));
            return null;
         });
      }
   }

   public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
      Level world = entity.level();
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            TreeSummonerStaffGUIApplyProcedure.execute(entity);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      TanshugetreesMod.addNetworkMessage(TYPE, STREAM_CODEC, TreeSummonerStaffGUIButtonMessage::handleData);
   }
}
