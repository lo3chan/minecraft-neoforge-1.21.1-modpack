package tallestegg.guardvillagers.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tallestegg.guardvillagers.client.GuardSounds;
import tallestegg.guardvillagers.common.entities.Guard;

public record GuardSetPatrolPosPacket(int entityId, boolean pressed) implements CustomPacketPayload {
   public static final Type<GuardSetPatrolPosPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("guardvillagers", "set_patrol"));
   public static final StreamCodec<FriendlyByteBuf, GuardSetPatrolPosPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, GuardSetPatrolPosPacket::entityId, ByteBufCodecs.BOOL, GuardSetPatrolPosPacket::pressed, GuardSetPatrolPosPacket::new
   );

   public static void setPatrolPosition(GuardSetPatrolPosPacket packet, IPayloadContext context) {
      Player player = context.player();
      if (player != null && player.level() instanceof ServerLevel && player.level().getEntity(packet.entityId()) instanceof Guard guard) {
         BlockPos pos = packet.pressed() ? null : guard.blockPosition();
         if (guard.blockPosition() != null) {
            guard.setPatrolPos(pos);
         }

         guard.setPatrolling(!packet.pressed());
         guard.playSound((SoundEvent)GuardSounds.GUARD_YES.value());
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
