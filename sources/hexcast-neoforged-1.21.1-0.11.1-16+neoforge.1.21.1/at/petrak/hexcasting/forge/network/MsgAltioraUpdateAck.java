package at.petrak.hexcasting.forge.network;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.player.AltioraAbility;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record MsgAltioraUpdateAck(@Nullable AltioraAbility altiora) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("altiora");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgAltioraUpdateAck deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      boolean extant = buf.readBoolean();
      if (!extant) {
         return new MsgAltioraUpdateAck(null);
      } else {
         int grace = buf.readVarInt();
         return new MsgAltioraUpdateAck(new AltioraAbility(grace));
      }
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeBoolean(this.altiora != null);
      if (this.altiora != null) {
         buf.writeVarInt(this.altiora.gracePeriod());
      }
   }

   public static void handle(final MsgAltioraUpdateAck self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               IXplatAbstractions.INSTANCE.setAltiora(player, self.altiora);
            }
         }
      });
   }
}
