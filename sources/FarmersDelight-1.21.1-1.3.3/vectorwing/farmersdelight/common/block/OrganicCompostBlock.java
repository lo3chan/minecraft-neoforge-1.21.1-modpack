package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.tag.ModTags;

public class OrganicCompostBlock extends Block {
   public static IntegerProperty COMPOSTING = IntegerProperty.create("composting", 0, 7);

   public OrganicCompostBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)super.defaultBlockState().setValue(COMPOSTING, 0));
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{COMPOSTING});
      super.createBlockStateDefinition(builder);
   }

   public int getMaxCompostingStage() {
      return 7;
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!level.isClientSide) {
         float chance = 0.0F;
         boolean hasWater = false;
         int maxLight = 0;

         for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(ModTags.Blocks.COMPOST_ACTIVATORS)) {
               chance += 0.02F;
            }

            if (neighborState.getFluidState().is(FluidTags.WATER)) {
               hasWater = true;
            }

            int light = level.getBrightness(LightLayer.SKY, neighborPos.above());
            if (light > maxLight) {
               maxLight = light;
            }
         }

         chance += maxLight > 12 ? 0.1F : 0.05F;
         chance += hasWater ? 0.1F : 0.0F;
         if (level.getRandom().nextFloat() <= chance) {
            if ((Integer)state.getValue(COMPOSTING) == this.getMaxCompostingStage()) {
               level.setBlock(pos, ModBlocks.RICH_SOIL.get().defaultBlockState(), 3);
            } else {
               level.setBlock(pos, (BlockState)state.setValue(COMPOSTING, (Integer)state.getValue(COMPOSTING) + 1), 3);
            }
         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
      return this.getMaxCompostingStage() + 1 - (Integer)blockState.getValue(COMPOSTING);
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if (random.nextInt(10) == 0) {
         level.addParticle(
            ParticleTypes.MYCELIUM, (double)pos.getX() + random.nextFloat(), pos.getY() + 1.1, (double)pos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0
         );
      }
   }
}
