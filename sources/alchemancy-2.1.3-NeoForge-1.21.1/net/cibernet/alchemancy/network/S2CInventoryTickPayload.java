package net.cibernet.alchemancy.network;

import java.util.List;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CInventoryTickPayload(int entityId, List<S2CInventoryTickPayload.SlotEntry> items, int selectedSlot) implements CustomPacketPayload {
   public static final Type<S2CInventoryTickPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/inventory_tick"));
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CInventoryTickPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      S2CInventoryTickPayload::entityId,
      S2CInventoryTickPayload.SlotEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
      S2CInventoryTickPayload::items,
      ByteBufCodecs.VAR_INT,
      S2CInventoryTickPayload::selectedSlot,
      S2CInventoryTickPayload::new
   );

   public static void handleDataOnMain(S2CInventoryTickPayload payload, IPayloadContext context) {
      Entity entity = context.player().level().getEntity(payload.entityId());
      if (entity != null) {
         for (S2CInventoryTickPayload.SlotEntry item : payload.items) {
            InfusedPropertiesHelper.forEachProperty(
               item.stack(),
               propertyHolder -> ((Property)propertyHolder.value())
                  .onInventoryTick(entity, item.stack(), entity.level(), item.slot(), item.slot() == payload.selectedSlot())
            );
         }
      }
   }

   public static void sendPacket(ServerPlayer player) {
      NonNullList<ItemStack> inventory = player.getInventory().items;
      PacketDistributor.sendToPlayersTrackingEntity(
         player,
         new S2CInventoryTickPayload(
            player.getId(),
            inventory.stream()
               .map(stack -> new S2CInventoryTickPayload.SlotEntry(inventory.indexOf(stack), stack))
               .filter(stack -> !stack.stack().isEmpty())
               .toList(),
            player.getInventory().selected
         ),
         new CustomPacketPayload[0]
      );
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   private record SlotEntry(int slot, ItemStack stack) {
      private static final StreamCodec<RegistryFriendlyByteBuf, S2CInventoryTickPayload.SlotEntry> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.VAR_INT,
         S2CInventoryTickPayload.SlotEntry::slot,
         ItemStack.OPTIONAL_STREAM_CODEC,
         S2CInventoryTickPayload.SlotEntry::stack,
         S2CInventoryTickPayload.SlotEntry::new
      );
   }
}
