package vazkii.psi.common.network.message;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;

public record MessageVisualEffect(int color, double x, double y, double z, double width, double height, double offset, int effectType)
   implements CustomPacketPayload {
   public static final int TYPE_CRAFT = 0;
   public static final ResourceLocation ID = Psi.location("message_visual_effect");
   public static final Type<MessageVisualEffect> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageVisualEffect> CODEC = new StreamCodec<RegistryFriendlyByteBuf, MessageVisualEffect>() {
      @NotNull
      public MessageVisualEffect decode(RegistryFriendlyByteBuf pBuffer) {
         return new MessageVisualEffect(
            pBuffer.readInt(),
            pBuffer.readDouble(),
            pBuffer.readDouble(),
            pBuffer.readDouble(),
            pBuffer.readDouble(),
            pBuffer.readDouble(),
            pBuffer.readDouble(),
            pBuffer.readInt()
         );
      }

      public void encode(RegistryFriendlyByteBuf pBuffer, MessageVisualEffect message) {
         pBuffer.writeInt(message.color());
         pBuffer.writeDouble(message.x());
         pBuffer.writeDouble(message.y());
         pBuffer.writeDouble(message.z());
         pBuffer.writeDouble(message.width());
         pBuffer.writeDouble(message.height());
         pBuffer.writeDouble(message.offset());
         pBuffer.writeInt(message.effectType());
      }
   };

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      float r = (this.color >> 16 & 0xFF) / 255.0F;
      float g = (this.color >> 8 & 0xFF) / 255.0F;
      float b = (this.color & 0xFF) / 255.0F;
      ctx.enqueueWork(
         () -> {
            Level world = Psi.proxy.getClientWorld();
            if (this.effectType == 0) {
               for (int i = 0; i < 5; i++) {
                  double particleX = this.x + (Math.random() - 0.5) * 2.1 * this.width;
                  double particleY = this.y - this.offset;
                  double particleZ = this.z + (Math.random() - 0.5) * 2.1 * this.width;
                  float grav = -0.05F - (float)Math.random() * 0.01F;
                  Psi.proxy.sparkleFX(particleX, particleY, particleZ, r, g, b, grav, 3.5F, 15);
                  double m = 0.01;
                  double d3 = 10.0;

                  for (int j = 0; j < 3; j++) {
                     double d0 = world.random.nextGaussian() * m;
                     double d1 = world.random.nextGaussian() * m;
                     double d2 = world.random.nextGaussian() * m;
                     world.addParticle(
                        ParticleTypes.EXPLOSION,
                        this.x + ThreadLocalRandom.current().nextFloat() * this.width * 2.0 - this.width - d0 * d3,
                        this.y + ThreadLocalRandom.current().nextFloat() * this.height - d1 * d3,
                        this.z + ThreadLocalRandom.current().nextFloat() * this.width * 2.0 - this.width - d2 * d3,
                        d0,
                        d1,
                        d2
                     );
                  }
               }
            }
         }
      );
   }
}
