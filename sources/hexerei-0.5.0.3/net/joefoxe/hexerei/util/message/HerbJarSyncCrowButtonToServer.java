package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class HerbJarSyncCrowButtonToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, HerbJarSyncCrowButtonToServer> CODEC = StreamCodec.ofMember(
      HerbJarSyncCrowButtonToServer::encode, HerbJarSyncCrowButtonToServer::new
   );
   public static final Type<HerbJarSyncCrowButtonToServer> TYPE = new Type(HexereiUtil.getResource("herb_jar_sync_crow_button"));
   BlockPos herbJarTile;
   int toggled;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public HerbJarSyncCrowButtonToServer(HerbJarTile herbJarTile, int toggled) {
      this.herbJarTile = herbJarTile.getBlockPos();
      this.toggled = toggled;
   }

   public HerbJarSyncCrowButtonToServer(RegistryFriendlyByteBuf buf) {
      this.herbJarTile = buf.readBlockPos();
      this.toggled = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.herbJarTile);
      buffer.writeInt(this.toggled);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.herbJarTile) instanceof HerbJarTile herbJar) {
         herbJar.setButtonToggled(this.toggled);
      }
   }
}
