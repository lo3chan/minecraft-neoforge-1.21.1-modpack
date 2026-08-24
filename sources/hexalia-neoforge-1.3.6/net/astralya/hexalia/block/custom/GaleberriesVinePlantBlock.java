package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GaleberriesVinePlantBlock extends GrowingPlantBodyBlock implements BonemealableBlock, CaveVines {
   public static final MapCodec<GaleberriesVinePlantBlock> CODEC = simpleCodec(GaleberriesVinePlantBlock::new);
   public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public GaleberriesVinePlantBlock(Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BERRIES, false));
   }

   protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
      return CODEC;
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)ModBlocks.GALEBERRIES_VINE.get();
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if ((Boolean)state.getValue(BERRIES)) {
         if (!level.isClientSide()) {
            popResource(level, pos, new ItemStack((ItemLike)ModItems.GALEBERRIES.get()));
            level.setBlock(pos, (BlockState)state.setValue(BERRIES, false), 2);
         }

         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   protected BlockState updateHeadAfterConvertedFromBody(BlockState body, BlockState head) {
      return (BlockState)head.setValue(BERRIES, (Boolean)body.getValue(BERRIES));
   }

   protected boolean isRandomlyTicking(BlockState state) {
      return !(Boolean)state.getValue(BERRIES);
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!(Boolean)state.getValue(BERRIES) && random.nextFloat() < 0.11F) {
         level.setBlock(pos, (BlockState)state.setValue(BERRIES, true), 2);
      }
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return !(Boolean)state.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      level.setBlock(pos, (BlockState)state.setValue(BERRIES, true), 2);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{BERRIES});
   }
}
