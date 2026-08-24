package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.entity.passive.MountableAnimal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StepHeightPacket(int entityID) implements CustomPacketPayload {
   public static final Type<StepHeightPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "sync_step_height"));
   public static final StreamCodec<RegistryFriendlyByteBuf, StepHeightPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, StepHeightPacket::entityID, StepHeightPacket::new
   );

   public Type<StepHeightPacket> type() {
      return TYPE;
   }

   public static void execute(StepHeightPacket payload, IPayloadContext context) {
      Player playerEntity = context.player();
      if (playerEntity.getServer() != null && playerEntity.level().getEntity(payload.entityID()) instanceof MountableAnimal mountableAnimal) {
         AttributeInstance stepHeight = mountableAnimal.getAttribute(Attributes.STEP_HEIGHT);
         if (stepHeight != null) {
            if (stepHeight.hasModifier(mountableAnimal.getDefaultStepHeightModifier().id())) {
               stepHeight.removeModifier(mountableAnimal.getDefaultStepHeightModifier().id());
            }

            if (!stepHeight.hasModifier(mountableAnimal.getMountStepHeightModifier().id())) {
               stepHeight.addTransientModifier(mountableAnimal.getMountStepHeightModifier());
            }
         }
      }
   }
}
