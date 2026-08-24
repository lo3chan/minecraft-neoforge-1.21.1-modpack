package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateShipWheel extends BaseEntityBlock implements AMSpecialRenderBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape SOUTH_AABB = Block.box(-2.0, -2.0, 0.0, 18.0, 18.0, 3.0);
   private static final VoxelShape NORTH_AABB = Block.box(-2.0, -2.0, 13.0, 18.0, 18.0, 16.0);
   private static final VoxelShape EAST_AABB = Block.box(0.0, -2.0, -2.0, 3.0, 18.0, 18.0);
   private static final VoxelShape WEST_AABB = Block.box(13.0, -2.0, -2.0, 16.0, 18.0, 18.0);
   private static final VoxelShape UP_AABB = Block.box(-2.0, 0.0, -2.0, 18.0, 3.0, 18.0);
   private static final VoxelShape DOWN_AABB = Block.box(-2.0, 13.0, -2.0, 16.0, 16.0, 18.0);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockEndPirateShipWheel() {
      super(
         Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .noOcclusion()
            .sound(SoundType.ANCIENT_DEBRIS)
            .strength(1.0F)
            .lightLevel(i -> 3)
            .noCollission()
            .requiresCorrectToolForDrops()
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
      return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, p_52801_);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
      return switch ((Direction)p_54561_.getValue(FACING)) {
         case NORTH -> NORTH_AABB;
         case SOUTH -> SOUTH_AABB;
         case EAST -> EAST_AABB;
         case WEST -> WEST_AABB;
         case UP -> UP_AABB;
         default -> DOWN_AABB;
      };
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      boolean remove = false;
      Direction dir = ((Direction)state.getValue(FACING)).getOpposite();
      BlockPos offset = pos.relative(dir);
      return remove || world.getBlockState(offset).isFaceSturdy(world, offset, dir.getOpposite());
   }

   protected ItemInteractionResult useItemOn(
      ItemStack amStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      return AMCompat.itemResult(this.amUse(state, worldIn, pos, player, handIn, hit));
   }

   private InteractionResult amUse(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.getBlockEntity(pos) instanceof TileEntityEndPirateShipWheel wheel) {
         boolean clockwise = false;
         Vec3 offset = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
         switch ((Direction)state.getValue(FACING)) {
            case NORTH:
               clockwise = offset.x <= 0.5;
               break;
            case SOUTH:
               clockwise = offset.x >= 0.5;
               break;
            case EAST:
               clockwise = offset.z <= 0.5;
               break;
            case WEST:
               clockwise = offset.z >= 0.5;
         }

         wheel.rotate(clockwise);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TileEntityEndPirateShipWheel(pos, state);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_SHIP_WHEEL.get(), TileEntityEndPirateShipWheel::commonTick);
   }
}
