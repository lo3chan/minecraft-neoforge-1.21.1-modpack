package vazkii.psi.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.common.Psi;
import vazkii.psi.common.network.MessageRegister;

public record MessageParticleTrail(Vec3 position, Vec3 direction, double length, int time, ItemStack cad) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_particle_trail");
   public static final Type<MessageParticleTrail> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageParticleTrail> CODEC = StreamCodec.composite(
      MessageRegister.VEC3,
      MessageParticleTrail::position,
      MessageRegister.VEC3,
      MessageParticleTrail::direction,
      ByteBufCodecs.DOUBLE,
      MessageParticleTrail::length,
      ByteBufCodecs.INT,
      MessageParticleTrail::time,
      ItemStack.STREAM_CODEC,
      MessageParticleTrail::cad,
      MessageParticleTrail::new
   );
   private static final int STEPS_PER_UNIT = 4;

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Level world = Psi.proxy.getClientWorld();
         int color = Psi.proxy.getColorForCAD(this.cad);
         float red = PsiRenderHelper.r(color);
         float green = PsiRenderHelper.g(color);
         float blue = PsiRenderHelper.b(color);
         Vec3 ray = this.direction.normalize().scale(0.25);
         int steps = (int)(this.length * 4.0);

         for (int i = 0; i < steps; i++) {
            double x = this.position.x + ray.x * i;
            double y = this.position.y + ray.y * i;
            double z = this.position.z + ray.z * i;
            Psi.proxy.sparkleFX(world, x, y, z, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, this.time);
         }
      });
   }
}
