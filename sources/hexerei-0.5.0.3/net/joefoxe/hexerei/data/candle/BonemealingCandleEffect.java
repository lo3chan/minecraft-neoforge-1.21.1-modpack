package net.joefoxe.hexerei.data.candle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Random;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BonemealingCandleEffect extends AbstractCandleEffect {
   private static final int MAX_TIME = 160;

   @Override
   public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
      if (candleData.lit) {
         if (candleData.cooldown >= 160.0F * candleData.getEffectCooldownMultiplier()) {
            for (int i = 0; i < 3; i++) {
               BlockPos crop = this.findCrop(level, blockEntity.getBlockPos());
               if (crop != null && !level.isClientSide() && level.getBlockState(crop).getBlock() instanceof CropBlock cropBlock) {
                  ServerLevel serverLevel = (ServerLevel)level;
                  cropBlock.performBonemeal(serverLevel, level.random, crop, level.getBlockState(crop));
                  serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, crop.getX() + 0.5, crop.getY() + 0.5, crop.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.2);
               }
            }

            candleData.cooldown = 0;
         }

         try {
            if (candleData.effectParticle != null && level.isClientSide() && candleData.effectParticle != null && !candleData.effectParticle.isEmpty()) {
               this.particle = ParticleArgument.readParticle(
                  new StringReader(candleData.effectParticle.get(new Random().nextInt(candleData.effectParticle.size()))), Hexerei.DynamicRegistries.get()
               );
            }
         } catch (CommandSyntaxException var9) {
         }

         candleData.cooldown = (candleData.cooldown + 1) % 2147483647;
      }
   }

   @Nullable
   public BlockPos findCrop(Level level, BlockPos jarPos) {
      ArrayList<BlockPos> crops = new ArrayList<>();

      for (BlockPos pos : area) {
         BlockPos relativePos = jarPos.offset(pos);
         BlockState state = level.getBlockState(relativePos);
         if (!state.isAir() && state.getBlock() instanceof CropBlock cropBlock && cropBlock.isValidBonemealTarget(level, pos, state)) {
            crops.add(relativePos);
         }
      }

      return crops.isEmpty() ? null : crops.get(level.random.nextInt(crops.size()));
   }

   @Override
   public <T> AbstractCandleEffect getCopy() {
      return new BonemealingCandleEffect();
   }

   @Override
   public String getLocationName() {
      return HexereiUtil.getResource("growth_effect").toString();
   }

   @Override
   public ParticleOptions getParticleType() {
      return ParticleTypes.HAPPY_VILLAGER;
   }
}
