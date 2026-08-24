package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;

public record MsgBeepS2C(Vec3 target, int note, NoteBlockInstrument instrument) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("beep");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgBeepS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      double x = buf.readDouble();
      double y = buf.readDouble();
      double z = buf.readDouble();
      int note = buf.readInt();
      NoteBlockInstrument instrument = (NoteBlockInstrument)buf.readEnum(NoteBlockInstrument.class);
      return new MsgBeepS2C(new Vec3(x, y, z), note, instrument);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeDouble(this.target.x);
      buf.writeDouble(this.target.y);
      buf.writeDouble(this.target.z);
      buf.writeInt(this.note);
      buf.writeEnum(this.instrument);
   }

   public static void handle(final MsgBeepS2C msg) {
      Minecraft.getInstance()
         .execute(
            new Runnable() {
               @Override
               public void run() {
                  Minecraft minecraft = Minecraft.getInstance();
                  ClientLevel world = minecraft.level;
                  if (world != null) {
                     float pitch = (float)Math.pow(2.0, (msg.note() - 12) / 12.0);
                     world.playLocalSound(
                        msg.target().x,
                        msg.target().y,
                        msg.target().z,
                        (SoundEvent)msg.instrument().getSoundEvent().value(),
                        SoundSource.PLAYERS,
                        3.0F,
                        pitch,
                        false
                     );
                     world.addParticle(ParticleTypes.NOTE, msg.target().x, msg.target().y + 0.2, msg.target().z, msg.note() / 24.0, 0.0, 0.0);
                  }
               }
            }
         );
   }
}
