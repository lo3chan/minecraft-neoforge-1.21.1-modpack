package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.client.ClientCastingStack;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record MsgClearSpiralPatternsS2C(UUID playerUUID) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("clr_spi_pats_sc");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgClearSpiralPatternsS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      UUID player = buf.readUUID();
      return new MsgClearSpiralPatternsS2C(player);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeUUID(this.playerUUID);
   }

   public static void handle(final MsgClearSpiralPatternsS2C self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
               Player player = mc.level.getPlayerByUUID(self.playerUUID);
               if (player != null) {
                  ClientCastingStack stack = IClientXplatAbstractions.INSTANCE.getClientCastingStack(player);
                  stack.slowClear();
               }
            }
         }
      });
   }
}
