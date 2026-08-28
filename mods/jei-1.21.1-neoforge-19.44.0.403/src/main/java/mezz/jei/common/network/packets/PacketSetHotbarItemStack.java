/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.common.network.packets;

import com.google.common.base.Preconditions;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PlayToServerPacket;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ServerCommandUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class PacketSetHotbarItemStack
extends PlayToServerPacket<PacketSetHotbarItemStack> {
    public static final CustomPacketPayload.Type<PacketSetHotbarItemStack> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"set_hotbar_item_stack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetHotbarItemStack> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.STREAM_CODEC, p -> p.itemStack, (StreamCodec)ByteBufCodecs.VAR_INT, p -> p.hotbarSlot, PacketSetHotbarItemStack::new);
    private final ItemStack itemStack;
    private final int hotbarSlot;

    public PacketSetHotbarItemStack(ItemStack itemStack, int hotbarSlot) {
        ErrorUtil.checkNotNull(itemStack, "itemStack");
        Preconditions.checkArgument((boolean)Inventory.isHotbarSlot((int)hotbarSlot), (Object)("hotbar slot must be in the hotbar. got: " + hotbarSlot));
        this.itemStack = itemStack;
        this.hotbarSlot = hotbarSlot;
    }

    @Override
    public void process(ServerPacketContext context) {
        ServerCommandUtil.setHotbarSlot(context, this.itemStack, this.hotbarSlot);
    }

    @Override
    public CustomPacketPayload.Type<PacketSetHotbarItemStack> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PacketSetHotbarItemStack> streamCodec() {
        return STREAM_CODEC;
    }
}

