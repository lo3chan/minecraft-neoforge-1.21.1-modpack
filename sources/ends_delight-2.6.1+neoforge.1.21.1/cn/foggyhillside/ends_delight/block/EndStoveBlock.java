package cn.foggyhillside.ends_delight.block;

import cn.foggyhillside.ends_delight.block.entity.EndStoveBlockEntity;
import cn.foggyhillside.ends_delight.registry.ModBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class EndStoveBlock extends AbstractStoveBlock {
   public static final MapCodec<EndStoveBlock> CODEC = simpleCodec(EndStoveBlock::new);

   public MapCodec<EndStoveBlock> codec() {
      return CODEC;
   }

   public EndStoveBlock(Properties properties) {
      super(properties);
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new EndStoveBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide && state.getValue(LIT)
         ? createTickerHelper(blockEntityType, ModBlockEntityTypes.END_STOVE.get(), EndStoveBlockEntity::particleTick)
         : AbstractStoveBlock.createStoveTicker(level, blockEntityType, ModBlockEntityTypes.END_STOVE.get());
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if ((Boolean)state.getValue(LIT)) {
         double x = pos.getX() + 0.5;
         double y = pos.getY();
         double z = pos.getZ() + 0.5;
         if (random.nextInt(10) == 0) {
            level.playLocalSound(x, y, z, (SoundEvent)ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = (Direction)state.getValue(HorizontalDirectionalBlock.FACING);
         Axis direction$axis = direction.getAxis();
         double horizontalOffset = random.nextDouble() * 0.6 - 0.3;
         double xOffset = direction$axis == Axis.X ? direction.getStepX() * 0.52 : horizontalOffset;
         double yOffset = random.nextDouble() * 6.0 / 16.0;
         double zOffset = direction$axis == Axis.Z ? direction.getStepZ() * 0.52 : horizontalOffset;
         level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
         level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
      }
   }
}
