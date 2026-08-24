package net.mehvahdjukaar.amendments.common.block;

import java.util.Arrays;
import net.mehvahdjukaar.amendments.common.CakeRegistry;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleCakeBlock extends DirectionalCakeBlock {
   protected static final VoxelShape[] SHAPES_NORTH = new VoxelShape[]{
      Shapes.or(box(2.0, 8.0, 2.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 3.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 5.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 7.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 9.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 11.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0)),
      Shapes.or(box(2.0, 8.0, 13.0, 14.0, 16.0, 14.0), box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0))
   };
   protected static final VoxelShape[] SHAPES_WEST = Arrays.stream(SHAPES_NORTH)
      .map(s -> MthUtils.rotateVoxelShape(s, Direction.WEST))
      .toArray(VoxelShape[]::new);
   protected static final VoxelShape[] SHAPES_SOUTH = Arrays.stream(SHAPES_NORTH)
      .map(s -> MthUtils.rotateVoxelShape(s, Direction.SOUTH))
      .toArray(VoxelShape[]::new);
   protected static final VoxelShape[] SHAPES_EAST = Arrays.stream(SHAPES_NORTH)
      .map(s -> MthUtils.rotateVoxelShape(s, Direction.EAST))
      .toArray(VoxelShape[]::new);
   private final BlockState mimic;

   public DoubleCakeBlock(CakeRegistry.CakeType type) {
      super(type);
      this.mimic = type.cake.defaultBlockState();
   }

   public DoubleCakeBlock(Properties properties, CakeRegistry.CakeType type) {
      super(properties, type);
      this.mimic = type.cake.defaultBlockState();
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      Item item = stack.getItem();
      if (stack.is(ItemTags.CANDLES) && Block.byItem(item) instanceof CandleBlock) {
         return hitResult.getDirection() == Direction.UP
            ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case EAST -> SHAPES_EAST[state.getValue(BITES)];
         case SOUTH -> SHAPES_SOUTH[state.getValue(BITES)];
         case NORTH -> SHAPES_NORTH[state.getValue(BITES)];
         default -> SHAPES_WEST[state.getValue(BITES)];
      };
   }

   @Override
   public void removeSlice(BlockState state, BlockPos pos, LevelAccessor level, Player player, Direction dir) {
      int i = (Integer)state.getValue(BITES);
      if (i < 6) {
         if (i == 0 && CommonConfigs.DIRECTIONAL_CAKE.get()) {
            state = (BlockState)state.setValue(FACING, dir);
         }

         level.setBlock(pos, (BlockState)state.setValue(BITES, i + 1), 3);
      } else if (this.type == CakeRegistry.VANILLA && (Boolean)state.getValue(WATERLOGGED) && CommonConfigs.DIRECTIONAL_CAKE.get()) {
         level.setBlock(
            pos,
            (BlockState)((BlockState)ModRegistry.DIRECTIONAL_CAKE.get().defaultBlockState().setValue(FACING, (Direction)state.getValue(FACING)))
               .setValue(WATERLOGGED, (Boolean)state.getValue(WATERLOGGED)),
            3
         );
      } else {
         level.setBlock(pos, this.type.cake.defaultBlockState(), 3);
      }
   }

   @Override
   public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
      if (CompatHandler.SUPPLEMENTARIES) {
         SuppCompat.spawnCakeParticles(level, pos, rand);
      }

      super.animateTick(stateIn, level, pos, rand);
      this.mimic.getBlock().animateTick(this.mimic, level, pos, rand);
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
      return Math.min(super.getDestroyProgress(state, player, worldIn, pos), this.mimic.getDestroyProgress(player, worldIn, pos));
   }

   public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
      return this.mimic.getSoundType();
   }

   public SoundType getSoundType(BlockState state) {
      return this.mimic.getSoundType();
   }

   public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
      return level instanceof Level l
         ? Math.max(ForgeHelper.getExplosionResistance(this.mimic, l, pos, explosion), state.getBlock().getExplosionResistance())
         : super.getExplosionResistance();
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return this.mimic.getBlock().getCloneItemStack(level, pos, state);
   }
}
