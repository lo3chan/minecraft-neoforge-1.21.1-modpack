package com.mcwfurnitures.kikoz.objects.chairs;

import com.mcwfurnitures.kikoz.MacawsFurnitures;
import com.mcwfurnitures.kikoz.objects.FurnitureObject;
import com.mcwfurnitures.kikoz.storage.ChairEntity;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModernChair extends FurnitureObject {
   private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape SHAPE = Shapes.or(
      Block.box(2.0, 12.0, 12.0, 14.0, 18.0, 15.0),
      new VoxelShape[]{
         Block.box(0.0, 0.0, 0.0, 2.0, 15.0, 2.0),
         Block.box(14.0, 0.0, 0.0, 16.0, 15.0, 2.0),
         Block.box(0.0, 0.0, 14.0, 2.0, 15.0, 16.0),
         Block.box(14.0, 0.0, 14.0, 16.0, 15.0, 16.0),
         Block.box(0.0, 15.0, 0.0, 2.0, 16.0, 16.0),
         Block.box(14.0, 15.0, 0.0, 16.0, 16.0, 16.0),
         Block.box(1.0, 8.0, 0.1, 15.0, 10.0, 15.9),
         Block.box(2.0, 9.0, 1.0, 14.0, 12.0, 15.0)
      }
   );

   protected void runCalculation(VoxelShape shape) {
      for (Direction direction : Direction.values()) {
         SHAPES.put(direction, MacawsFurnitures.calculateShapes(direction, shape));
      }
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   public ModernChair(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
      this.runCalculation(SHAPE);
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      return ChairEntity.create(level, pos, 0.4, player);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
