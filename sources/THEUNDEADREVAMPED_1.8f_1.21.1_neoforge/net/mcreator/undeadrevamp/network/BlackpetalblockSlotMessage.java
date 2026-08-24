package net.mcreator.undeadrevamp.network;

import java.util.HashMap;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.procedures.RuslingProcedure;
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
public record BlackpetalblockSlotMessage(int slotID, int x, int y, int z, int changeType, int meta) implements CustomPacketPayload {
   public static final Type<BlackpetalblockSlotMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "blackpetalblock_slots"));
   public static final StreamCodec<RegistryFriendlyByteBuf, BlackpetalblockSlotMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
      buffer.writeInt(message.slotID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
      buffer.writeInt(message.changeType);
      buffer.writeInt(message.meta);
   }, buffer -> new BlackpetalblockSlotMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

   public Type<BlackpetalblockSlotMessage> type() {
      return TYPE;
   }

   public static void handleData(BlackpetalblockSlotMessage message, IPayloadContext context) {
      if (context.flow() == PacketFlow.SERVERBOUND) {
         context.enqueueWork(() -> {
            Player entity = context.player();
            int slotID = message.slotID;
            int changeType = message.changeType;
            int meta = message.meta;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            handleSlotAction(entity, slotID, changeType, meta, x, y, z);
         }).exceptionally(e -> {
            context.connection().disconnect(Component.literal(e.getMessage()));
            return null;
         });
      }
   }

   public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
      Level world = entity.level();
      HashMap guistate = BlackpetalblockMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (slot == 12 && changeType == 0) {
            RuslingProcedure.execute(world, x, y, z);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      UndeadRevamp2Mod.addNetworkMessage(TYPE, STREAM_CODEC, BlackpetalblockSlotMessage::handleData);
   }
}
