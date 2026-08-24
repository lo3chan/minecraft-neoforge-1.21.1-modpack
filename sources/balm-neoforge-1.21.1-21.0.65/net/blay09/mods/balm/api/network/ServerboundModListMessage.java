package net.blay09.mods.balm.api.network;

import java.util.Map;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record ServerboundModListMessage(Map<String, NetworkVersions> modList) implements CustomPacketPayload {
   public static final Type<ServerboundModListMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("balm", "mod_list"));

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
