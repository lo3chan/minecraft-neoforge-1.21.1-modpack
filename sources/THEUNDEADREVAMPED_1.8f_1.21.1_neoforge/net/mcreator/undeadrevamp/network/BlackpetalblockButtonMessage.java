package net.mcreator.undeadrevamp.network;

import java.util.HashMap;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.procedures.BlacpetalblockUpdateTickProcedure;
import net.mcreator.undeadrevamp.world.inventory.BlackpetalblockMenu;
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
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(
   bus = Bus.MOD
)
public record BlackpetalblockButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
   public static final Type<BlackpetalblockButtonMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "blackpetalblock_buttons"));
   public static final StreamCodec<RegistryFriendlyByteBuf, BlackpetalblockButtonMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }, buffer -> new BlackpetalblockButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

   public Type<BlackpetalblockButtonMessage> type() {
      return TYPE;
   }

   public static void handleData(BlackpetalblockButtonMessage message, IPayloadContext context) {
      if (context.flow() == PacketFlow.SERVERBOUND) {
         context.enqueueWork(() -> {
            Player entity = context.player();
            int buttonID = message.buttonID;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            handleButtonAction(entity, buttonID, x, y, z);
         }).exceptionally(e -> {
            context.connection().disconnect(Component.literal(e.getMessage()));
            return null;
         });
      }
   }

   public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
      Level world = entity.level();
      HashMap guistate = BlackpetalblockMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            BlacpetalblockUpdateTickProcedure.execute(world, x, y, z);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      UndeadRevamp2Mod.addNetworkMessage(TYPE, STREAM_CODEC, BlackpetalblockButtonMessage::handleData);
   }
}
