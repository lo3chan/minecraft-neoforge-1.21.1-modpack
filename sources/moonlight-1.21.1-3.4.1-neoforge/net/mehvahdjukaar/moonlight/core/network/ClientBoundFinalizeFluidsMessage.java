package net.mehvahdjukaar.moonlight.core.network;

import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.misc.DynamicHolder;
import net.mehvahdjukaar.moonlight.api.misc.HolderRef;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.fluid.SoftFluidInternal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientBoundFinalizeFluidsMessage implements Message {
   public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundFinalizeFluidsMessage> TYPE = Message.makeType(
      Moonlight.res("s2c_finalize_fluids"), ClientBoundFinalizeFluidsMessage::new
   );

   public ClientBoundFinalizeFluidsMessage() {
   }

   public ClientBoundFinalizeFluidsMessage(RegistryFriendlyByteBuf pBuffer) {
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void handle(Message.Context context) {
      SoftFluidInternal.postInitClient(Objects.requireNonNull(context.getPlayer().level()).registryAccess());
      DynamicHolder.clearCache();
      HolderRef.clearCache();
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE.type();
   }
}
