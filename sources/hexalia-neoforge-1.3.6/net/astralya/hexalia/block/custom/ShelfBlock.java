package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ShelfBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);
   private static final Map<Direction, VoxelShape> SHAPES = createShapes();

   public ShelfBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
         int slot = slotFromHit(state, pos, hitResult);
         if (slot < 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack shelfItem = shelf.getItem(slot);
            if (!shelfItem.isEmpty()) {
               ItemStack removedItem = shelf.removeItemNoUpdate(slot);
               level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
               if (!player.getInventory().add(removedItem)) {
                  Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removedItem);
               }

               level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
               return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else if (isValidItem(heldItem)) {
               ItemStack toPlace = heldItem.copy();
               toPlace.setCount(1);
               if (!player.isCreative()) {
                  heldItem.shrink(1);
               }

               shelf.setItem(slot, toPlace);
               level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
               return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES.getOrDefault(state.getValue(FACING), SHAPES.get(Direction.NORTH));
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction direction = ((Direction)state.getValue(FACING)).getOpposite();
      BlockPos supportPos = pos.relative(direction);
      return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, direction.getOpposite());
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(level, pos)) {
         level.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
         if (level instanceof ServerLevel) {
            for (int slot = 0; slot < shelf.getContainerSize(); slot++) {
               ItemStack stack = shelf.getItem(slot);
               if (!stack.isEmpty()) {
                  Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
               }
            }
         }

         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, movedByPiston);
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable("tooltip.hexalia.shelf").withStyle(ChatFormatting.GRAY));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ShelfBlockEntity(pos, state);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public static boolean isValidItem(ItemStack stack) {
      return !stack.isEmpty() && (stack.is(ModTags.Items.BREWS) || stack.is(Items.POTION) || stack.is(Items.LINGERING_POTION) || stack.is(Items.SPLASH_POTION));
   }

   private static int slotFromHit(BlockState state, BlockPos pos, BlockHitResult hitResult) {
      Vec3 hitPos = hitResult.getLocation();
      Direction facing = (Direction)state.getValue(FACING);
      double x = hitPos.x() - pos.getX();
      double y = hitPos.y() - pos.getY();
      double z = hitPos.z() - pos.getZ();
      double relativeX;
      double relativeZ;
      switch (facing) {
         case NORTH:
            relativeX = 1.0 - x;
            relativeZ = z;
            break;
         case SOUTH:
            relativeX = x;
            relativeZ = 1.0 - z;
            break;
         case EAST:
            relativeX = 1.0 - z;
            relativeZ = 1.0 - x;
            break;
         case WEST:
            relativeX = z;
            relativeZ = x;
            break;
         default:
            return -1;
      }

      if (!(relativeZ < 0.5) && !(relativeZ > 1.0) && !(y < 0.25) && !(y > 0.75)) {
         int row = relativeZ < 0.75 ? 1 : 0;
         int column = relativeX < 0.33 ? 0 : (relativeX < 0.67 ? 1 : 2);
         return column + row * 3;
      } else {
         return -1;
      }
   }

   private static VoxelShape createShape() {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.0, 0.25, 0.5, 1.0, 0.3125, 1.0), BooleanOp.OR);
      return Shapes.join(shape, Shapes.box(0.0, 0.125, 0.875, 1.0, 0.25, 1.0), BooleanOp.OR);
   }

   private static Map<Direction, VoxelShape> createShapes() {
      Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
      VoxelShape baseShape = createShape();

      for (Direction direction : Plane.HORIZONTAL) {
         shapes.put(direction, rotateShape(Direction.NORTH, direction, baseShape));
      }

      return shapes;
   }

   private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
      VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
      int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

      for (int i = 0; i < times; i++) {
         buffer[0]
            .forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX)));
         buffer[0] = buffer[1];
         buffer[1] = Shapes.empty();
      }

      return buffer[0];
   }
}
