package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.EchoEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CTriggerEchoPacket(
   Optional<Integer> sourceId, Optional<ResourceKey<Level>> sourceLevelKey, ItemStack sourceItem, Vec3 relativePos, EchoEffect.EffectType effectType
) implements CustomPacketPayload {
   private static final StreamCodec<ByteBuf, EchoEffect.EffectType> TYPE_STREAM_CODEC = ByteBufCodecs.INT
      .map(type -> EchoEffect.EffectType.values()[type], Enum::ordinal);
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CTriggerEchoPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(ByteBufCodecs.INT),
      S2CTriggerEchoPacket::sourceId,
      ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)),
      S2CTriggerEchoPacket::sourceLevelKey,
      ItemStack.STREAM_CODEC,
      S2CTriggerEchoPacket::sourceItem,
      CommonUtils.VEC3_CODEC,
      S2CTriggerEchoPacket::relativePos,
      ByteBufCodecs.INT.map(i -> EchoEffect.EffectType.values()[i], Enum::ordinal),
      S2CTriggerEchoPacket::effectType,
      S2CTriggerEchoPacket::new
   );
   public static final Type<S2CTriggerEchoPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/trigger_echo"));

   public void handleDataOnMain(IPayloadContext context) {
      Player target = context.player();
      Level sourceLevel = this.sourceLevelKey.isPresent() && target.level().dimension().equals(this.sourceLevelKey.get()) ? target.level() : null;
      Entity user = sourceLevel != null && this.sourceId.isPresent() ? sourceLevel.getEntity(this.sourceId.get()) : null;
      EchoEffect.trigger(target, sourceLevel, user, this.sourceItem(), this.relativePos(), this.effectType());
   }

   public Type<S2CTriggerEchoPacket> type() {
      return TYPE;
   }
}
