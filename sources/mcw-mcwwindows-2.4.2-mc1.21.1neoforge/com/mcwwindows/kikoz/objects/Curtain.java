package com.mcwwindows.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Curtain extends Block {
   private static final EnumProperty<Curtain.TiedEnum> TIED = EnumProperty.create("tied", Curtain.TiedEnum.class);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   String infoname;
   boolean hasTextInfo = true;
   protected static final VoxelShape WEST = Shapes.or(box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape SOUTH = Shapes.or(box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape EAST = Shapes.or(box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0), new VoxelShape[0]);
   protected static final VoxelShape NORTH = Shapes.or(box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0), new VoxelShape[0]);

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            return null;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public Curtain(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TIED, Curtain.TiedEnum.NONE)
      );
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return (BlockState)state.setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      boolean isCrouching = player.isCrouching();
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         int cycleDirection = isCrouching ? -1 : 1;
         Curtain.TiedEnum currentTied = (Curtain.TiedEnum)state.getValue(TIED);
         int nextIndex = (currentTied.ordinal() + cycleDirection + Curtain.TiedEnum.values().length) % Curtain.TiedEnum.values().length;
         Curtain.TiedEnum nextTied = Curtain.TiedEnum.values()[nextIndex];
         state = (BlockState)state.setValue(TIED, nextTied);
         world.setBlock(pos, state, 2);
         return ItemInteractionResult.SUCCESS;
      }
   }

   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{TIED, FACING});
   }

   public static enum TiedEnum implements StringRepresentable {
      NONE("none"),
      LEFT("left"),
      RIGHT("right"),
      HALF_RIGHT_TIE("half_right_tie"),
      HALF_RIGHT("half_right"),
      HALF_RIGHT_BOTTOM("half_right_bottom"),
      HALF_LEFT_TIE("half_left_tie"),
      HALF_LEFT("half_left"),
      HALF_LEFT_BOTTOM("half_left_bottom"),
      SINGLE("single"),
      HALF_SINGLE("half_single"),
      BASE_WAVE("base_wave"),
      BASE_HALF_WAVE("base_half_wave");

      private final String name;

      private TiedEnum(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
