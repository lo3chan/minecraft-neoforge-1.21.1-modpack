package net.joefoxe.hexerei.data.candle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CandleEffectParticlePacket;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;

public class SunshineCandleEffect extends AbstractCandleEffect {
   private static final int MAX_TIME = 160;

   private static int getDuration(RandomSource pSource, int pTime, IntProvider pTimeProvider) {
      return pTime == -1 ? pTimeProvider.sample(pSource) : pTime;
   }

   @Override
   public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
      if (candleData.lit) {
         if (candleData.cooldown >= 160) {
            if (level instanceof ServerLevel serverLevel) {
               serverLevel.getLevel().setWeatherParameters(getDuration(level.getRandom(), -1, ServerLevel.RAIN_DELAY), 0, false, false);
               if (!candleData.effectParticle.isEmpty()) {
                  HexereiPacketHandler.sendToNearbyClient(
                     serverLevel, blockEntity.getBlockPos(), new CandleEffectParticlePacket(blockEntity.getBlockPos(), candleData.effectParticle, 0, 1)
                  );
               }
            }

            candleData.cooldown = 0;
         }

         candleData.cooldown = (candleData.cooldown + 1) % 2147483647;

         try {
            if (candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && !candleData.effectParticle.isEmpty()) {
               this.particle = ParticleArgument.readParticle(
                  new StringReader(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size()))), level.registryAccess()
               );
            }
         } catch (CommandSyntaxException var5) {
         }
      }
   }

   @Override
   public <T> AbstractCandleEffect getCopy() {
      return new SunshineCandleEffect();
   }

   @Override
   public String getLocationName() {
      return HexereiUtil.getResource("sunshine_effect").toString();
   }
}
