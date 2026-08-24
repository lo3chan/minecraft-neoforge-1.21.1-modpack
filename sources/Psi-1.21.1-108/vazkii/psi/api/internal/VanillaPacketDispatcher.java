package vazkii.psi.api.internal;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class VanillaPacketDispatcher {
   public static void dispatchTEToNearbyPlayers(BlockEntity tile) {
      Level world = tile.getLevel();
      if (world != null) {
         for (Player player : world.players()) {
            if (player instanceof ServerPlayer mp
               && MathHelper.pointDistancePlane(mp.getX(), mp.getZ(), tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getZ() + 0.5) < 64.0F) {
               dispatchTEToPlayer(tile, mp);
            }
         }
      }
   }

   public static void dispatchTEToPlayer(BlockEntity tile, ServerPlayer p) {
      Packet<ClientGamePacketListener> packet = tile.getUpdatePacket();
      if (packet != null) {
         p.connection.send(packet);
      }
   }
}
