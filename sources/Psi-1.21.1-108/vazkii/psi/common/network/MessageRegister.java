package vazkii.psi.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.network.message.MessageAdditiveMotion;
import vazkii.psi.common.network.message.MessageBlink;
import vazkii.psi.common.network.message.MessageChangeControllerSlot;
import vazkii.psi.common.network.message.MessageChangeSocketableSlot;
import vazkii.psi.common.network.message.MessageDataSync;
import vazkii.psi.common.network.message.MessageDeductPsi;
import vazkii.psi.common.network.message.MessageEidosSync;
import vazkii.psi.common.network.message.MessageFlashRingSync;
import vazkii.psi.common.network.message.MessageLoopcastSync;
import vazkii.psi.common.network.message.MessageParticleTrail;
import vazkii.psi.common.network.message.MessagePsiOverflow;
import vazkii.psi.common.network.message.MessageSpamlessChat;
import vazkii.psi.common.network.message.MessageSpellError;
import vazkii.psi.common.network.message.MessageSpellModified;
import vazkii.psi.common.network.message.MessageTriggerJumpSpell;
import vazkii.psi.common.network.message.MessageVisualEffect;

public class MessageRegister {
   public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3 = new StreamCodec<RegistryFriendlyByteBuf, Vec3>() {
      @NotNull
      public Vec3 decode(RegistryFriendlyByteBuf pBuffer) {
         return pBuffer.readVec3();
      }

      public void encode(RegistryFriendlyByteBuf pBuffer, @NotNull Vec3 pVec3) {
         pBuffer.writeVec3(pVec3);
      }
   };
   private static final String VERSION = "3";

   @SubscribeEvent
   public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("psi").versioned("3").optional();
      registrar.playBidirectional(MessageAdditiveMotion.TYPE, MessageAdditiveMotion.CODEC, MessageAdditiveMotion::handle);
      registrar.playBidirectional(MessageBlink.TYPE, MessageBlink.CODEC, MessageBlink::handle);
      registrar.playBidirectional(MessageChangeControllerSlot.TYPE, MessageChangeControllerSlot.CODEC, MessageChangeControllerSlot::handle);
      registrar.playBidirectional(MessageChangeSocketableSlot.TYPE, MessageChangeSocketableSlot.CODEC, MessageChangeSocketableSlot::handle);
      registrar.playBidirectional(MessageDataSync.TYPE, MessageDataSync.CODEC, MessageDataSync::handle);
      registrar.playBidirectional(MessageDeductPsi.TYPE, MessageDeductPsi.CODEC, MessageDeductPsi::handle);
      registrar.playBidirectional(MessageEidosSync.TYPE, MessageEidosSync.CODEC, MessageEidosSync::handle);
      registrar.playBidirectional(MessageLoopcastSync.TYPE, MessageLoopcastSync.CODEC, MessageLoopcastSync::handle);
      registrar.playBidirectional(MessageParticleTrail.TYPE, MessageParticleTrail.CODEC, MessageParticleTrail::handle);
      registrar.playBidirectional(MessageSpamlessChat.TYPE, MessageSpamlessChat.CODEC, MessageSpamlessChat::handle);
      registrar.playBidirectional(MessageSpellError.TYPE, MessageSpellError.CODEC, MessageSpellError::handle);
      registrar.playBidirectional(MessageSpellModified.TYPE, MessageSpellModified.CODEC, MessageSpellModified::handle);
      registrar.playBidirectional(MessageFlashRingSync.TYPE, MessageFlashRingSync.CODEC, MessageFlashRingSync::handle);
      registrar.playBidirectional(MessageTriggerJumpSpell.TYPE, MessageTriggerJumpSpell.CODEC, MessageTriggerJumpSpell::handle);
      registrar.playBidirectional(MessageVisualEffect.TYPE, MessageVisualEffect.CODEC, MessageVisualEffect::handle);
      registrar.playBidirectional(MessagePsiOverflow.TYPE, MessagePsiOverflow.CODEC, MessagePsiOverflow::handle);
   }

   public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
      PacketDistributor.sendToServer(message, new CustomPacketPayload[0]);
   }

   public static <MSG extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, MSG message) {
      PacketDistributor.sendToPlayer(player, message, new CustomPacketPayload[0]);
   }

   public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntity(Entity entity, MSG message) {
      PacketDistributor.sendToPlayersTrackingEntity(entity, message, new CustomPacketPayload[0]);
   }

   public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntityAndSelf(Entity entity, MSG message) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, message, new CustomPacketPayload[0]);
   }

   public static <MSG extends CustomPacketPayload> void sendToPlayersInDimension(ServerLevel level, MSG message) {
      PacketDistributor.sendToPlayersInDimension(level, message, new CustomPacketPayload[0]);
   }
}
