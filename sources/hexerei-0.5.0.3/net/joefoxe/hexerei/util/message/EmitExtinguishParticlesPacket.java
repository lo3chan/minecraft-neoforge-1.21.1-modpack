package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class EmitExtinguishParticlesPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, EmitExtinguishParticlesPacket> CODEC = StreamCodec.ofMember(
      EmitExtinguishParticlesPacket::encode, EmitExtinguishParticlesPacket::new
   );
   public static final Type<EmitExtinguishParticlesPacket> TYPE = new Type(HexereiUtil.getResource("emit_extinguish_particles"));
   BlockPos pos;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public EmitExtinguishParticlesPacket(BlockPos pos) {
      this.pos = pos;
   }

   public EmitExtinguishParticlesPacket(RegistryFriendlyByteBuf buf) {
      this(buf.readBlockPos());
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getBlockEntity(this.pos) instanceof SageBurningPlateTile sageBurningPlateTile) {
         sageBurningPlateTile.extinguishParticles();
      }
   }
}
