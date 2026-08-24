package vectorwing.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class CuttingBoardBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CuttingBoardBlock> CODEC = simpleCodec(CuttingBoardBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

   public CuttingBoardBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return null;
   }

   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
         ItemStack mainHandStack = player.getMainHandItem();
         if (mainHandStack.isEmpty()) {
            if (!cuttingBoard.isEmpty() && !level.isClientSide) {
               ItemStack removedStack = cuttingBoard.removeItem();
               if (!player.isCreative()) {
                  player.getInventory().add(removedStack);
               }

               Vec3 centerPos = pos.getCenter();
               level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_REMOVE.get(), SoundSource.BLOCKS, 0.25F, 0.5F);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.CONSUME;
            }
         } else if (cuttingBoard.canAddItem(mainHandStack)) {
            if (level.isClientSide) {
               return ItemInteractionResult.CONSUME;
            } else {
               ItemStack remainderStack = cuttingBoard.addItem(player.getAbilities().instabuild ? mainHandStack.copy() : mainHandStack);
               if (!player.isCreative()) {
                  player.setItemSlot(EquipmentSlot.MAINHAND, remainderStack);
               }

               Vec3 centerPos = pos.getCenter();
               level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_PLACE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
               return ItemInteractionResult.SUCCESS;
            }
         } else {
            return cuttingBoard.processStoredItemUsingTool(mainHandStack, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), cuttingBoard.getStoredItem());
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public boolean isPossibleToRespawnInThis(BlockState state) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return facing == Direction.DOWN && !state.canSurvive(level, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos floorPos = pos.below();
      return canSupportRigidBlock(level, floorPos) || canSupportCenter(level, floorPos, Direction.UP);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
         ItemStack storedStack = cuttingBoard.getStoredItem();
         if (!storedStack.isEmpty()) {
            float proportions = (float)storedStack.getCount() / Math.min(cuttingBoard.getMaxStackSize(), storedStack.getMaxStackSize());
            return Mth.floor(proportions * 14.0F) + 1;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlockEntityTypes.CUTTING_BOARD.get().create(pos, state);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   @EventBusSubscriber(
      modid = "farmersdelight"
   )
   public static class ToolCarvingEvent {
      @SubscribeEvent
      public static void onSneakPlaceTool(RightClickBlock event) {
         Level level = event.getLevel();
         BlockPos pos = event.getPos();
         if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
            Player player = event.getEntity();
            ItemStack heldStack = player.getMainHandItem();
            if (player.isSecondaryUseActive() && !heldStack.isEmpty()) {
               if (cuttingBoard.carveToolOnBoard(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                  if (!player.isCreative()) {
                     player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                  }

                  Vec3 centerPos = pos.getCenter();
                  level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_CARVE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                  event.setCanceled(true);
                  event.setCancellationResult(InteractionResult.SUCCESS);
               }
            }
         }
      }
   }
}
