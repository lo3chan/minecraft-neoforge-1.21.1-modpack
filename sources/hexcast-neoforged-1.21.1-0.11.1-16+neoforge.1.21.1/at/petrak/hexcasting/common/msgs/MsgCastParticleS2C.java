package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record MsgCastParticleS2C(ParticleSpray spray, FrozenPigment colorizer) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("cprtcl");
   private static final Random RANDOM = new Random();

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgCastParticleS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      double posX = buf.readDouble();
      double posY = buf.readDouble();
      double posZ = buf.readDouble();
      double velX = buf.readDouble();
      double velY = buf.readDouble();
      double velZ = buf.readDouble();
      double fuzziness = buf.readDouble();
      double spread = buf.readDouble();
      int count = buf.readInt();
      CompoundTag tag = buf.readNbt();
      FrozenPigment colorizer = FrozenPigment.fromNBT(tag);
      return new MsgCastParticleS2C(new ParticleSpray(new Vec3(posX, posY, posZ), new Vec3(velX, velY, velZ), fuzziness, spread, count), colorizer);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeDouble(this.spray.getPos().x);
      buf.writeDouble(this.spray.getPos().y);
      buf.writeDouble(this.spray.getPos().z);
      buf.writeDouble(this.spray.getVel().x);
      buf.writeDouble(this.spray.getVel().y);
      buf.writeDouble(this.spray.getVel().z);
      buf.writeDouble(this.spray.getFuzziness());
      buf.writeDouble(this.spray.getSpread());
      buf.writeInt(this.spray.getCount());
      buf.writeNbt(this.colorizer.serializeToNBT());
   }

   private static Vec3 randomInCircle(double maxTh) {
      double th = RANDOM.nextDouble(0.0, maxTh + 0.001);
      double z = RANDOM.nextDouble(-1.0, 1.0);
      return new Vec3(Math.sqrt(1.0 - z * z) * Math.cos(th), Math.sqrt(1.0 - z * z) * Math.sin(th), z);
   }

   public static void handle(final MsgCastParticleS2C msg) {
      Minecraft.getInstance()
         .execute(
            new Runnable() {
               @Override
               public void run() {
                  ColorProvider colProvider = msg.colorizer().getColorProvider();

                  for (int i = 0; i < msg.spray().getCount(); i++) {
                     Vec3 offset = MsgCastParticleS2C.randomInCircle(6.2831854820251465)
                        .normalize()
                        .scale(MsgCastParticleS2C.RANDOM.nextFloat() * msg.spray().getFuzziness() / 2.0);
                     Vec3 pos = msg.spray().getPos().add(offset);
                     double phi = Math.acos(1.0 - MsgCastParticleS2C.RANDOM.nextDouble() * (1.0 - Math.cos(msg.spray().getSpread())));
                     double theta = 6.283185307179586 * MsgCastParticleS2C.RANDOM.nextDouble();
                     Vec3 v = msg.spray().getVel().normalize();
                     Vec3 k;
                     if (v.x == 0.0 && v.y == 0.0) {
                        k = new Vec3(1.0, 0.0, 0.0);
                     } else {
                        k = v.cross(new Vec3(0.0, 0.0, 1.0));
                     }

                     Vec3 velUnlen = v.scale(Math.cos(phi))
                        .add(k.scale(Math.sin(phi) * Math.cos(theta)))
                        .add(v.cross(k).scale(Math.sin(phi) * Math.sin(theta)));
                     Vec3 vel = velUnlen.scale(msg.spray().getVel().length() / 20.0);
                     int color = colProvider.getColor(ClientTickCounter.getTotal(), velUnlen);
                     Minecraft.getInstance().level.addParticle(new ConjureParticleOptions(color), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
                  }
               }
            }
         );
   }
}
