package net.cibernet.alchemancy.network;

import java.util.List;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CDiscoverCodexIngredientsPayload(List<ResourceLocation> itemsToDiscover) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CDiscoverCodexIngredientsPayload> STREAM_CODEC = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CDiscoverCodexIngredientsPayload::itemsToDiscover, S2CDiscoverCodexIngredientsPayload::new
   );
   public static final Type<S2CDiscoverCodexIngredientsPayload> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/discover_codex_ingredients")
   );

   public void handleDataOnMain(IPayloadContext context) {
      for (ResourceLocation item : this.itemsToDiscover) {
         InfusionCodexSaveData.discoverItem(item);
      }
   }

   public Type<S2CDiscoverCodexIngredientsPayload> type() {
      return TYPE;
   }
}
