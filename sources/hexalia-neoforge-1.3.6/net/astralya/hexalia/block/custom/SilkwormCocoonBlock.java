package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SilkwormCocoonBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape NORTH_SHAPE = Shapes.create(new AABB(0.3125, 0.3125, 0.6875, 0.6875, 0.75, 1.0));
   private static final VoxelShape SOUTH_SHAPE = Shapes.create(new AABB(0.3125, 0.3125, 0.0, 0.6875, 0.75, 0.3125));
   private static final VoxelShape WEST_SHAPE = Shapes.create(new AABB(0.6875, 0.3125, 0.3125, 1.0, 0.75, 0.6875));
   private static final VoxelShape EAST_SHAPE = Shapes.create(new AABB(0.0, 0.3125, 0.3125, 0.3125, 0.75, 0.6875));

   public SilkwormCocoonBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.isClientSide()) {
         return ItemInteractionResult.SUCCESS;
      } else {
         ItemStack itemStack = player.getItemInHand(hand);
         if (itemStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block.defaultBlockState().getLightEmission() > 8) {
               level.scheduleTick(pos, this, 200);
               return ItemInteractionResult.SUCCESS;
            }
         }

         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
   }

   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      level.removeBlock(pos, false);
      SilkMothEntity silkMoth = (SilkMothEntity)((EntityType)ModEntities.SILK_MOTH.get()).create(level);
      if (silkMoth != null) {
         silkMoth.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
         SilkMothVariant variant = SilkMothVariant.byId(random.nextInt(SilkMothVariant.values().length));
         silkMoth.setVariant(variant);
         level.addFreshEntity(silkMoth);
      }

      level.gameEvent(null, GameEvent.BLOCK_DESTROY, pos);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case SOUTH -> SOUTH_SHAPE;
         case WEST -> WEST_SHAPE;
         case EAST -> EAST_SHAPE;
         default -> NORTH_SHAPE;
      };
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction face = context.getClickedFace();
      return face.getAxis().isVertical() ? null : (BlockState)this.defaultBlockState().setValue(FACING, face);
   }

   protected BlockState updateShape(
      BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos
   ) {
      return direction == ((Direction)state.getValue(FACING)).getOpposite() && !state.canSurvive(levelAccessor, pos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos attachedPos = pos.relative(facing.getOpposite());
      return level.getBlockState(attachedPos).is(BlockTags.LOGS) && !hasOtherCocoonAttached(level, attachedPos, pos);
   }

   private static boolean hasOtherCocoonAttached(LevelReader level, BlockPos attachedPos, BlockPos pos) {
      for (Direction direction : Plane.HORIZONTAL) {
         BlockPos neighborPos = attachedPos.relative(direction);
         if (!neighborPos.equals(pos) && level.getBlockState(neighborPos).getBlock() instanceof SilkwormCocoonBlock) {
            return true;
         }
      }

      return false;
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected BlockState rotate(BlockState state, Rotation direction) {
      return state;
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
