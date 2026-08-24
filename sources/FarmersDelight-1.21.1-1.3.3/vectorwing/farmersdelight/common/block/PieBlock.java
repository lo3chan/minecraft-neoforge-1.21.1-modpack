package vectorwing.farmersdelight.common.block;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.ShapeUtils;

public class PieBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
      Shapes.join(Block.box(2.0, 0.0, 8.0, 8.0, 4.0, 14.0), Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 8.0), BooleanOp.OR),
      Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 8.0),
      Block.box(8.0, 0.0, 2.0, 14.0, 4.0, 8.0)
   };
   private static final VoxelShape[][] ROTATED_SHAPES = buildShapes();
   public final Supplier<Item> pieSlice;

   public PieBlock(Properties properties, Supplier<Item> pieSlice) {
      super(properties);
      this.pieSlice = pieSlice;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(BITES, 0));
   }

   public ItemStack getPieSliceItem() {
      return new ItemStack((ItemLike)this.pieSlice.get());
   }

   public int getMaxBites() {
      return 4;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return ROTATED_SHAPES[state.getValue(BITES)][((Direction)state.getValue(FACING)).get2DDataValue()];
   }

   private static VoxelShape[][] buildShapes() {
      VoxelShape[][] result = new VoxelShape[SHAPES.length][4];

      for (int i = 0; i < SHAPES.length; i++) {
         Map<Direction, VoxelShape> rotated = ShapeUtils.getShapesRotatedFromNorth(SHAPES[i]);

         for (Entry<Direction, VoxelShape> entry : rotated.entrySet()) {
            result[i][entry.getKey().get2DDataValue()] = entry.getValue();
         }
      }

      return result;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }

   public ItemInteractionResult useItemOn(
      ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      return ItemUtils.isKnife(heldStack)
         ? this.cutSlice(level, pos, state, player, heldStack.getItem())
         : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.isClientSide) {
         if (this.consumeBite(level, pos, state, player).consumesAction()) {
            return InteractionResult.SUCCESS;
         }

         if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.CONSUME;
         }
      }

      return this.consumeBite(level, pos, state, player);
   }

   protected InteractionResult consumeBite(Level level, BlockPos pos, BlockState state, Player player) {
      if (!player.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         ItemStack sliceStack = this.getPieSliceItem();
         FoodProperties sliceFood = sliceStack.getItem().getFoodProperties(sliceStack, player);
         if (sliceFood != null) {
            player.getFoodData().eat(sliceFood);

            for (PossibleEffect effect : sliceFood.effects()) {
               if (!level.isClientSide && effect != null && level.random.nextFloat() < effect.probability()) {
                  player.addEffect(effect.effect());
               }
            }
         }

         int bites = (Integer)state.getValue(BITES);
         if (bites < this.getMaxBites() - 1) {
            level.setBlock(pos, (BlockState)state.setValue(BITES, bites + 1), 3);
         } else {
            level.removeBlock(pos, false);
         }

         level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
         if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.001
            );
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected ItemInteractionResult cutSlice(Level level, BlockPos pos, BlockState state, Player player, Item knife) {
      int bites = (Integer)state.getValue(BITES);
      if (bites < this.getMaxBites() - 1) {
         level.setBlock(pos, (BlockState)state.setValue(BITES, bites + 1), 3);
      } else {
         level.removeBlock(pos, false);
      }

      Direction direction = player.getDirection().getOpposite();
      ItemUtils.spawnItemEntity(
         level, this.getPieSliceItem(), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, direction.getStepX() * 0.15, 0.05, direction.getStepZ() * 0.15
      );
      level.playSound(null, pos, ModSounds.BLOCK_FOOD_SLICE.get(), SoundSource.PLAYERS, 0.8F, 0.8F);
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.001
         );
      }

      player.awardStat(Stats.ITEM_USED.get(knife));
      return ItemInteractionResult.SUCCESS;
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      return facing == Direction.DOWN && !state.canSurvive(level, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return canSupportRigidBlock(level, pos.below());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, BITES});
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return this.getMaxBites() - (Integer)state.getValue(BITES);
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }
}
