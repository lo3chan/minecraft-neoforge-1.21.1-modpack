package net.cibernet.alchemancy.network;

import java.util.List;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CUnlockCodexEntriesPayload(List<Holder<Property>> propertiesToUnlock) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CUnlockCodexEntriesPayload> STREAM_CODEC = StreamCodec.composite(
      Property.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CUnlockCodexEntriesPayload::propertiesToUnlock, S2CUnlockCodexEntriesPayload::new
   );
   public static final Type<S2CUnlockCodexEntriesPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/unlock_codex_entries"));

   public S2CUnlockCodexEntriesPayload(ItemStack stack) {
      this(InfusionCodexSaveData.getPropertiesToUnlock(stack));
   }

   public void handleDataOnMain(IPayloadContext context) {
      for (Holder<Property> propertyHolder : this.propertiesToUnlock) {
         InfusionCodexSaveData.unlock(propertyHolder);
      }
   }

   public Type<S2CUnlockCodexEntriesPayload> type() {
      return TYPE;
   }
}
