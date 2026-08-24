package cn.foggyhillside.ends_delight.block;

import cn.foggyhillside.ends_delight.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class DragonLegBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<DragonLegBlock> CODEC = simpleCodec(DragonLegBlock::new);
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 6);
   protected static final VoxelShape[] SHAPES_NORTH_HEAD = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(6.0, 2.0, 0.0, 10.0, 6.0, 5.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 5.0, 5.0), Block.box(6.0, 5.0, 0.0, 10.0, 15.0, 1.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 8.0, 5.0), Block.box(6.0, 8.0, 0.0, 10.0, 15.0, 1.0)}
      ),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 3.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 6.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 9.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 12.0))
   };
   protected static final VoxelShape[] SHAPES_NORTH_FOOT = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(6.0, 2.0, 11.0, 10.0, 6.0, 16.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 5.0, 16.0), Block.box(6.0, 5.0, 13.0, 10.0, 15.0, 16.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 8.0, 16.0), Block.box(6.0, 8.0, 13.0, 10.0, 15.0, 16.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 13.0, 16.0), Block.box(6.0, 5.0, 3.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 13.0, 16.0), Block.box(6.0, 5.0, 3.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 13.0, 16.0), Block.box(6.0, 5.0, 3.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 10.0, 14.0, 13.0, 16.0), Block.box(6.0, 5.0, 3.0, 10.0, 9.0, 10.0)}
      )
   };
   protected static final VoxelShape[] SHAPES_SOUTH_HEAD = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(6.0, 2.0, 11.0, 10.0, 6.0, 16.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 11.0, 14.0, 5.0, 16.0), Block.box(6.0, 5.0, 15.0, 10.0, 15.0, 16.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 11.0, 14.0, 8.0, 16.0), Block.box(6.0, 8.0, 15.0, 10.0, 15.0, 16.0)}
      ),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 13.0, 14.0, 13.0, 16.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 10.0, 14.0, 13.0, 16.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 7.0, 14.0, 13.0, 16.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(2.0, 2.0, 4.0, 14.0, 13.0, 16.0))
   };
   protected static final VoxelShape[] SHAPES_SOUTH_FOOT = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(6.0, 2.0, 0.0, 10.0, 6.0, 5.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 5.0, 6.0), Block.box(6.0, 5.0, 0.0, 10.0, 15.0, 3.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 8.0, 6.0), Block.box(6.0, 8.0, 0.0, 10.0, 15.0, 3.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 6.0), Block.box(6.0, 5.0, 6.0, 10.0, 9.0, 13.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 6.0), Block.box(6.0, 5.0, 6.0, 10.0, 9.0, 13.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 6.0), Block.box(6.0, 5.0, 6.0, 10.0, 9.0, 13.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 0.0, 14.0, 13.0, 6.0), Block.box(6.0, 5.0, 6.0, 10.0, 9.0, 13.0)}
      )
   };
   protected static final VoxelShape[] SHAPES_WEST_HEAD = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 6.0, 5.0, 6.0, 10.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 5.0, 5.0, 14.0), Block.box(0.0, 5.0, 6.0, 1.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 5.0, 8.0, 14.0), Block.box(0.0, 8.0, 6.0, 1.0, 15.0, 10.0)}
      ),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 2.0, 3.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 2.0, 6.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 2.0, 9.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 2.0, 12.0, 13.0, 14.0))
   };
   protected static final VoxelShape[] SHAPES_WEST_FOOT = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(11.0, 2.0, 6.0, 16.0, 6.0, 10.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 5.0, 14.0), Block.box(13.0, 5.0, 6.0, 16.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 8.0, 14.0), Block.box(13.0, 8.0, 6.0, 16.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 13.0, 14.0), Block.box(3.0, 5.0, 6.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 13.0, 14.0), Block.box(3.0, 5.0, 6.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 13.0, 14.0), Block.box(3.0, 5.0, 6.0, 10.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(10.0, 2.0, 2.0, 16.0, 13.0, 14.0), Block.box(3.0, 5.0, 6.0, 10.0, 9.0, 10.0)}
      )
   };
   protected static final VoxelShape[] SHAPES_EAST_HEAD = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(11.0, 2.0, 6.0, 16.0, 6.0, 10.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(11.0, 2.0, 2.0, 16.0, 5.0, 14.0), Block.box(15.0, 5.0, 6.0, 16.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(11.0, 2.0, 2.0, 16.0, 8.0, 14.0), Block.box(15.0, 8.0, 6.0, 16.0, 15.0, 10.0)}
      ),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(13.0, 2.0, 2.0, 16.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(10.0, 2.0, 2.0, 16.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(7.0, 2.0, 2.0, 16.0, 13.0, 14.0)),
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(4.0, 2.0, 2.0, 16.0, 13.0, 14.0))
   };
   protected static final VoxelShape[] SHAPES_EAST_FOOT = new VoxelShape[]{
      Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 2.0, 6.0, 5.0, 6.0, 10.0)),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 5.0, 14.0), Block.box(0.0, 5.0, 6.0, 3.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 8.0, 14.0), Block.box(0.0, 8.0, 6.0, 3.0, 15.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 13.0, 14.0), Block.box(6.0, 5.0, 6.0, 13.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 13.0, 14.0), Block.box(6.0, 5.0, 6.0, 13.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 13.0, 14.0), Block.box(6.0, 5.0, 6.0, 13.0, 9.0, 10.0)}
      ),
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), new VoxelShape[]{Block.box(0.0, 2.0, 2.0, 6.0, 13.0, 14.0), Block.box(6.0, 5.0, 6.0, 13.0, 9.0, 10.0)}
      )
   };

   public DragonLegBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH)).setValue(SERVINGS, 6))
            .setValue(PART, BedPart.HEAD)
      );
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (pState.getValue(PART) == BedPart.HEAD) {
         switch ((Direction)pState.getValue(FACING)) {
            case NORTH:
               return SHAPES_NORTH_HEAD[pState.getValue(SERVINGS)];
            case SOUTH:
               return SHAPES_SOUTH_HEAD[pState.getValue(SERVINGS)];
            case WEST:
               return SHAPES_WEST_HEAD[pState.getValue(SERVINGS)];
            case EAST:
               return SHAPES_EAST_HEAD[pState.getValue(SERVINGS)];
         }
      }

      if (pState.getValue(PART) == BedPart.FOOT) {
         switch ((Direction)pState.getValue(FACING)) {
            case NORTH:
               return SHAPES_NORTH_FOOT[pState.getValue(SERVINGS)];
            case SOUTH:
               return SHAPES_SOUTH_FOOT[pState.getValue(SERVINGS)];
            case WEST:
               return SHAPES_WEST_FOOT[pState.getValue(SERVINGS)];
            case EAST:
               return SHAPES_EAST_FOOT[pState.getValue(SERVINGS)];
         }
      }

      return SHAPES_NORTH_HEAD[pState.getValue(SERVINGS)];
   }

   private static Direction getNeighbourDirection(BedPart pPart, Direction pDirection) {
      return pPart == BedPart.HEAD ? pDirection : pDirection.getOpposite();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, SERVINGS, PART});
   }

   protected RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   protected BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos
   ) {
      if (pDirection != getNeighbourDirection((BedPart)pState.getValue(PART), (Direction)pState.getValue(FACING))) {
         return !pState.canSurvive(pLevel, pPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
      } else {
         return pState.canSurvive(pLevel, pPos) && pNeighborState.is(this) && pNeighborState.getValue(PART) != pState.getValue(PART)
            ? pState
            : Blocks.AIR.defaultBlockState();
      }
   }

   public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
      if (!pLevel.isClientSide && pPlayer.isCreative()) {
         BedPart bedpart = (BedPart)pState.getValue(PART);
         if (bedpart == BedPart.FOOT) {
            BlockPos blockpos = pPos.relative(getNeighbourDirection(bedpart, (Direction)pState.getValue(FACING)));
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
               pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
               pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
            }
         }
      }

      return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getHorizontalDirection();
      BlockPos blockpos = pContext.getClickedPos();
      BlockPos blockpos1 = blockpos.relative(direction);
      Level level = pContext.getLevel();
      return level.getBlockState(blockpos1).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(blockpos1)
         ? (BlockState)this.defaultBlockState().setValue(FACING, direction)
         : null;
   }

   @Nullable
   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
      if (!pLevel.isClientSide) {
         BlockPos facingPos = pPos.relative((Direction)pState.getValue(FACING));
         pLevel.setBlock(facingPos, (BlockState)pState.setValue(PART, BedPart.FOOT), 3);
         pLevel.blockUpdated(pPos, Blocks.AIR);
         pState.updateNeighbourShapes(pLevel, pPos, 3);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult
   ) {
      int servings = (Integer)pState.getValue(SERVINGS);
      ItemStack heldStack = pPlayer.getItemInHand(pHand);
      if (servings != 0) {
         if (heldStack.is(Items.BOWL)) {
            return this.takeServing(pLevel, pPos, pState, pPlayer, pHand, (Item)ModItems.DRAGON_LEG_WITH_SAUCE.get());
         }

         pPlayer.displayClientMessage(TextUtils.block("feast.use_container", new Object[]{new ItemStack(Items.BOWL).getHoverName()}), true);
      }

      if (servings == 0) {
         pLevel.playSound(null, pPos, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
         pLevel.destroyBlock(pPos, true);
      } else {
         pPlayer.displayClientMessage(TextUtils.block("feast.use_container", new Object[]{new ItemStack(Items.BOWL).getHoverName()}), true);
      }

      return ItemInteractionResult.SUCCESS;
   }

   protected ItemInteractionResult takeServing(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer, InteractionHand pHand, Item serving) {
      int servings = (Integer)pState.getValue(SERVINGS);
      BedPart part = (BedPart)pState.getValue(PART);
      BlockPos pairPos = pPos.relative(getNeighbourDirection(part, (Direction)pState.getValue(FACING)));
      BlockState pairState = pLevel.getBlockState(pairPos);
      ItemStack heldItem = pPlayer.getItemInHand(pHand);
      pLevel.setBlock(pairPos, (BlockState)pairState.setValue(SERVINGS, servings - 1), 3);
      pLevel.setBlock(pPos, (BlockState)pState.setValue(SERVINGS, servings - 1), 3);
      if (!pPlayer.isCreative()) {
         heldItem.shrink(1);
      }

      if (!pPlayer.getInventory().add(new ItemStack(serving))) {
         pPlayer.drop(new ItemStack(serving), false);
      }

      pLevel.playSound(null, pPos, (SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
      return ItemInteractionResult.SUCCESS;
   }
}
