package net.joefoxe.hexerei.util.message;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CandleEffectParticlePacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CandleEffectParticlePacket> CODEC = StreamCodec.ofMember(
      CandleEffectParticlePacket::encode, CandleEffectParticlePacket::new
   );
   public static final Type<CandleEffectParticlePacket> TYPE = new Type(HexereiUtil.getResource("candle_effect_particle"));
   BlockPos pos;
   List<String> particleLocations;
   int livingId;
   int stage;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CandleEffectParticlePacket(BlockPos pos, List<String> particleLocations, int livingId, int stage) {
      this.pos = pos;
      this.particleLocations = particleLocations;
      this.livingId = livingId;
      this.stage = stage;
   }

   public CandleEffectParticlePacket(RegistryFriendlyByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      int size = buffer.readInt();
      List<String> list = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         list.add(buffer.readUtf());
      }

      this.particleLocations = list;
      this.livingId = buffer.readInt();
      this.stage = buffer.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeInt(this.particleLocations.size());

      for (String particleLocation : this.particleLocations) {
         buffer.writeUtf(particleLocation);
      }

      buffer.writeInt(this.livingId);
      buffer.writeInt(this.stage);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getBlockEntity(this.pos) != null && player.level().getBlockEntity(this.pos) instanceof CandleTile candleTile) {
         if (this.stage == 0 && player.level().getEntity(this.livingId) instanceof LivingEntity livingEntity) {
            PotionCandleEffect.spawnParticles(player.level(), this.particleLocations, livingEntity);
         }

         if (this.stage == 1) {
            Candle.spawnParticleWave(player.level(), this.pos, true, this.particleLocations, 10);
         }
      }
   }
}
