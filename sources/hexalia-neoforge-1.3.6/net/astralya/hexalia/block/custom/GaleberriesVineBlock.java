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

public class GaleberriesVineBlock extends GrowingPlantHeadBlock implements BonemealableBlock, CaveVines {
   public static final MapCodec<GaleberriesVineBlock> CODEC = simpleCodec(GaleberriesVineBlock::new);
   public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public GaleberriesVineBlock(Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false, 0.1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(BERRIES, false));
   }

   protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
      return CODEC;
   }

   protected Block getBodyBlock() {
      return (Block)ModBlocks.GALEBERRIES_VINE_PLANT.get();
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState head, BlockState body) {
      return (BlockState)body.setValue(BERRIES, (Boolean)head.getValue(BERRIES));
   }

   protected BlockState getGrowIntoState(BlockState state, RandomSource random) {
      return (BlockState)super.getGrowIntoState(state, random).setValue(BERRIES, random.nextFloat() < 0.11F);
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      super.randomTick(state, level, pos, random);
      BlockState current = level.getBlockState(pos);
      if (current.is(this) && !(Boolean)current.getValue(BERRIES) && random.nextFloat() < 0.11F) {
         level.setBlock(pos, (BlockState)current.setValue(BERRIES, true), 2);
      }
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

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return !(Boolean)state.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      level.setBlock(pos, (BlockState)state.setValue(BERRIES, true), 2);
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
      return 1;
   }

   protected boolean canGrowInto(BlockState state) {
      return state.isAir();
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{BERRIES});
   }
}
