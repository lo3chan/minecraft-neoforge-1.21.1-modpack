package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CofferCycleWhitelistButtonToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CofferCycleWhitelistButtonToServer> CODEC = StreamCodec.ofMember(
      CofferCycleWhitelistButtonToServer::encode, CofferCycleWhitelistButtonToServer::new
   );
   public static final Type<CofferCycleWhitelistButtonToServer> TYPE = new Type(HexereiUtil.getResource("coffer_cycle_whitelist_button"));
   BlockPos cofferTile;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CofferCycleWhitelistButtonToServer(CofferTile cofferTile) {
      this.cofferTile = cofferTile.getBlockPos();
   }

   public CofferCycleWhitelistButtonToServer(RegistryFriendlyByteBuf buf) {
      this.cofferTile = buf.readBlockPos();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.cofferTile);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.cofferTile) instanceof CofferTile coffer) {
         coffer.mode = CofferTile.WhitelistMode.byId((coffer.mode.ordinal() + 1) % CofferTile.WhitelistMode.values().length);
         coffer.sync();
      }
   }
}
