package net.mcreator.borninchaosv.block;

import java.util.List;
import net.mcreator.borninchaosv.procedures.GraveDestroyerProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TombstoneFubukiBanzaiBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public TombstoneFubukiBanzaiBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.LODESTONE)
            .strength(4.0F, 50.0F)
            .lightLevel(s -> 3)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering((bs, br, bp) -> true)
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
      );
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("block.born_in_chaos_v1.tombstone_fubuki_banzai.description_0"));
      list.add(Component.translatable("block.born_in_chaos_v1.tombstone_fubuki_banzai.description_1"));
      list.add(Component.translatable("block.born_in_chaos_v1.tombstone_fubuki_banzai.description_2"));
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> Shapes.or(
            box(3.0, 5.0, 3.0, 13.0, 31.0, 13.0),
            new VoxelShape[]{box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0), box(3.0, 1.0, 3.0, 13.0, 5.0, 13.0), box(5.0, 2.0, 2.9, 11.0, 5.0, 3.9)}
         );
         case EAST -> Shapes.or(
            box(3.0, 5.0, 3.0, 13.0, 31.0, 13.0),
            new VoxelShape[]{box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0), box(3.0, 1.0, 3.0, 13.0, 5.0, 13.0), box(12.1, 2.0, 5.0, 13.1, 5.0, 11.0)}
         );
         case WEST -> Shapes.or(
            box(3.0, 5.0, 3.0, 13.0, 31.0, 13.0),
            new VoxelShape[]{box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0), box(3.0, 1.0, 3.0, 13.0, 5.0, 13.0), box(2.9, 2.0, 5.0, 3.9, 5.0, 11.0)}
         );
         default -> Shapes.or(
            box(3.0, 5.0, 3.0, 13.0, 31.0, 13.0),
            new VoxelShape[]{box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0), box(3.0, 1.0, 3.0, 13.0, 5.0, 13.0), box(5.0, 2.0, 12.1, 11.0, 5.0, 13.1)}
         );
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, flag);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      GraveDestroyerProcedure.execute(entity);
      return retval;
   }
}
