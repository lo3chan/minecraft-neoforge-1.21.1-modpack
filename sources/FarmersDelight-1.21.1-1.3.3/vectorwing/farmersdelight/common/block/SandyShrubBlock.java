package vectorwing.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;
import vectorwing.farmersdelight.common.world.WildCropGeneration;

public class SandyShrubBlock extends BushBlock implements IShearable, BonemealableBlock {
   public static final MapCodec<SandyShrubBlock> CODEC = simpleCodec(SandyShrubBlock::new);
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

   public SandyShrubBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
      return state.is(BlockTags.SAND);
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      level.registryAccess()
         .registry(Registries.CONFIGURED_FEATURE)
         .flatMap(value -> value.getHolder(WildCropGeneration.FEATURE_PATCH_SANDY_SHRUB))
         .ifPresent(value -> ((ConfiguredFeature)value.value()).place(level, level.getChunkSource().getGenerator(), random, pos.above()));
   }
}
