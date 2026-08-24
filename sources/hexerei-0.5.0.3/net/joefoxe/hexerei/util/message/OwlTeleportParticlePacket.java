package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.render.OwlVariant;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class OwlTeleportParticlePacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, OwlTeleportParticlePacket> CODEC = StreamCodec.ofMember(
      OwlTeleportParticlePacket::encode, OwlTeleportParticlePacket::new
   );
   public static final Type<OwlTeleportParticlePacket> TYPE = new Type(HexereiUtil.getResource("owl_teleport_particles"));
   Vec3 pos;
   OwlVariant owlVariant;
   ResourceKey<Level> dimension;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public OwlTeleportParticlePacket(ResourceKey<Level> dimension, Vec3 pos, OwlVariant owlVariant) {
      this.pos = pos;
      this.owlVariant = owlVariant;
      this.dimension = dimension;
   }

   public OwlTeleportParticlePacket(RegistryFriendlyByteBuf buf) {
      this(buf.readResourceKey(Registries.DIMENSION), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), OwlVariant.byId(buf.readInt()));
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeResourceKey(this.dimension);
      buffer.writeDouble(this.pos.x);
      buffer.writeDouble(this.pos.y);
      buffer.writeDouble(this.pos.z);
      buffer.writeInt(this.owlVariant.getId());
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().dimension().equals(this.dimension)) {
         OwlEntity.teleportParticles(player.level(), this.pos, this.owlVariant);
      }
   }
}
