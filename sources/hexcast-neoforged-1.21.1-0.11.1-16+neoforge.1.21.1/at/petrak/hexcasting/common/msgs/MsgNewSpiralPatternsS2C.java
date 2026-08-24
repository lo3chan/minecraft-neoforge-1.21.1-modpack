package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.client.ClientCastingStack;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record MsgNewSpiralPatternsS2C(UUID playerUUID, List<HexPattern> patterns, int lifetime) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("spi_pats_sc");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgNewSpiralPatternsS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      UUID player = buf.readUUID();
      ArrayList<HexPattern> patterns = (ArrayList<HexPattern>)buf.readCollection(ArrayList::new, buff -> HexPattern.fromNBT(buf.readNbt()));
      int lifetime = buf.readInt();
      return new MsgNewSpiralPatternsS2C(player, patterns, lifetime);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeUUID(this.playerUUID);
      buf.writeCollection(this.patterns, (buff, pattern) -> buff.writeNbt(pattern.serializeToNBT()));
      buf.writeInt(this.lifetime);
   }

   public static void handle(final MsgNewSpiralPatternsS2C self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
               Player player = mc.level.getPlayerByUUID(self.playerUUID);
               if (player != null) {
                  ClientCastingStack stack = IClientXplatAbstractions.INSTANCE.getClientCastingStack(player);

                  for (HexPattern pattern : self.patterns) {
                     stack.addPattern(pattern, self.lifetime);
                  }
               }
            }
         }
      });
   }
}
