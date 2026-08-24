package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WeatheringWallLanternBlock extends WallLanternBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringWallLanternBlock> CODEC = simpleCodec(WeatheringWallLanternBlock::new);
   private final WeatherState weatherState;

   public WeatheringWallLanternBlock(Properties properties, LanternRegistry.LanternType type) {
      super(properties, type);
      this.weatherState = type.lantern instanceof WeatheringCopper wc ? (WeatherState)wc.getAge() : WeatherState.UNAFFECTED;
   }

   @Deprecated
   public WeatheringWallLanternBlock(Properties properties) {
      super(properties);
      this.weatherState = WeatherState.UNAFFECTED;
   }

   @Override
   protected MapCodec<? extends WallLanternBlock> codec() {
      return CODEC;
   }

   public WeatherState getAge() {
      return this.weatherState;
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.changeOverTime(state, level, pos, random);
   }

   protected boolean isRandomlyTicking(BlockState state) {
      return WeatheringCopper.getNext(state.getBlock()).isPresent();
   }
}
