package com.mcwlights.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class WallLamp extends LightBaseShort {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
   public static final EnumProperty<DyeColor> DYE_COLOR = EnumProperty.create("dye_color", DyeColor.class);
   private static final VoxelShape NORTH = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 16.0);
   private static final VoxelShape EAST = Block.box(0.0, 0.0, 2.0, 14.0, 16.0, 14.0);
   private static final VoxelShape SOUTH = Block.box(2.0, 0.0, 0.0, 14.0, 16.0, 14.0);
   private static final VoxelShape WEST = Block.box(2.0, 0.0, 2.0, 16.0, 16.0, 14.0);

   public WallLamp(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true))
               .setValue(DYE_COLOR, DyeColor.WHITE))
            .setValue(POWERED, false)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      Direction direction = (Direction)state.getValue(FACING);
      switch (direction) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            return Shapes.empty();
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      DyeColor dyeColor = this.getDyeColor(context.getItemInHand());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(DYE_COLOR, dyeColor);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{LIT, FACING, DYE_COLOR, POWERED});
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      ItemStack heldItem = player.getItemInHand(handIn);
      Item item = heldItem.getItem();
      if (item instanceof DyeItem) {
         DyeColor dyeColor = ((DyeItem)item).getDyeColor();
         BlockState newState = (BlockState)state.setValue(DYE_COLOR, dyeColor);
         worldIn.setBlockAndUpdate(pos, newState);
         if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else if (item != this.asItem()) {
         state = (BlockState)state.cycle(LIT);
         worldIn.setBlock(pos, state, 10);
         worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private DyeColor getDyeColor(ItemStack stack) {
      return stack.getItem() instanceof DyeItem ? ((DyeItem)stack.getItem()).getDyeColor() : DyeColor.WHITE;
   }
}
