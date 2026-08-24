package vazkii.psi.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;

public record MessageAdditiveMotion(int entityID, double motionX, double motionY, double motionZ) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_additive_motion");
   public static final Type<MessageAdditiveMotion> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageAdditiveMotion> CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      MessageAdditiveMotion::entityID,
      ByteBufCodecs.DOUBLE,
      MessageAdditiveMotion::motionX,
      ByteBufCodecs.DOUBLE,
      MessageAdditiveMotion::motionY,
      ByteBufCodecs.DOUBLE,
      MessageAdditiveMotion::motionZ,
      MessageAdditiveMotion::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Level world = Psi.proxy.getClientWorld();
         if (world != null) {
            Entity entity = world.getEntity(this.entityID);
            if (entity != null) {
               entity.setDeltaMovement(entity.getDeltaMovement().add(this.motionX, this.motionY, this.motionZ));
            }
         }
      });
   }
}
