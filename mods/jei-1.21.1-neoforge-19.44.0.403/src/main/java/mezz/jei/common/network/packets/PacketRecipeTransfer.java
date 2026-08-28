/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PlayToServerPacket;
import mezz.jei.common.transfer.BasicRecipeTransferHandlerServer;
import mezz.jei.common.transfer.TransferOperation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class PacketRecipeTransfer
extends PlayToServerPacket<PacketRecipeTransfer> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CustomPacketPayload.Type<PacketRecipeTransfer> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"recipe_transfer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketRecipeTransfer> STREAM_CODEC = StreamCodec.composite((StreamCodec)TransferOperation.STREAM_CODEC.apply(ByteBufCodecs.list()), p -> p.transferOperations, (StreamCodec)ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), p -> p.craftingSlots, (StreamCodec)ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), p -> p.inventorySlots, (StreamCodec)ByteBufCodecs.BOOL, p -> p.maxTransfer, (StreamCodec)ByteBufCodecs.BOOL, p -> p.requireCompleteSets, PacketRecipeTransfer::new);
    public final List<TransferOperation> transferOperations;
    public final List<Integer> craftingSlots;
    public final List<Integer> inventorySlots;
    private final boolean maxTransfer;
    private final boolean requireCompleteSets;

    public static PacketRecipeTransfer fromSlots(List<TransferOperation> transferOperations, List<Slot> craftingSlots, List<Slot> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
        return new PacketRecipeTransfer(transferOperations, craftingSlots.stream().map(s -> s.index).toList(), inventorySlots.stream().map(s -> s.index).toList(), maxTransfer, requireCompleteSets);
    }

    public PacketRecipeTransfer(List<TransferOperation> transferOperations, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
        this.transferOperations = transferOperations;
        this.craftingSlots = craftingSlots;
        this.inventorySlots = inventorySlots;
        this.maxTransfer = maxTransfer;
        this.requireCompleteSets = requireCompleteSets;
    }

    @Override
    public CustomPacketPayload.Type<PacketRecipeTransfer> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PacketRecipeTransfer> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void process(ServerPacketContext context) {
        AbstractContainerMenu container = context.player().containerMenu;
        List<Slot> craftingSlots = PacketRecipeTransfer.getSlots(container, this.craftingSlots);
        List<Slot> inventorySlots = PacketRecipeTransfer.getSlots(container, this.inventorySlots);
        if (craftingSlots == null || inventorySlots == null) {
            return;
        }
        BasicRecipeTransferHandlerServer.setItems((Player)context.player(), this.transferOperations, craftingSlots, inventorySlots, this.maxTransfer, this.requireCompleteSets);
    }

    @Nullable
    static List<Slot> getSlots(AbstractContainerMenu container, List<Integer> slotIds) {
        if (slotIds.size() > container.slots.size()) {
            LOGGER.error("Recipe transfer packet has too many slot ids {} for container {} with {} slots", (Object)slotIds.size(), container.getClass(), (Object)container.slots.size());
            return null;
        }
        ArrayList<Slot> slots = new ArrayList<Slot>(slotIds.size());
        for (int slotId : slotIds) {
            if (slotId < 0 || slotId >= container.slots.size()) {
                LOGGER.error("Recipe transfer packet has invalid slot id {} for container {}", (Object)slotId, container.getClass());
                return null;
            }
            slots.add(container.getSlot(slotId));
        }
        return slots;
    }
}

