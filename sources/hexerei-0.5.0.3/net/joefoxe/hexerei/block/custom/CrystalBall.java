package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystalBall extends BaseEntityBlock implements ITileEntity<CrystalBallTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final IntegerProperty ANGLE = IntegerProperty.create("angle", 0, 180);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final MapCodec<CrystalBall> CODEC = simpleCodec(CrystalBall::new);
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(5.5, 0.0, 5.5, 10.5, 2.0, 10.5), Block.box(6.5, 2.0, 6.5, 9.5, 4.0, 9.5), Block.box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(ANGLE, 0))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return SHAPE;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!level.isClientSide) {
         level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
         level.blockEvent(pos, state.getBlock(), 1, 0);
      } else if (level.getBlockEntity(pos) instanceof CrystalBallTile crystalBallTile) {
         MoonPhases.MoonCondition phase = MoonPhases.MoonCondition.getMoonPhase(level);
         if (Math.abs(crystalBallTile.centerYawIncrement) == 100.0F) {
            player.displayClientMessage(Component.translatable("Nyooooom"), true);
         } else if (phase == MoonPhases.MoonCondition.NONE) {
            player.displayClientMessage(Component.translatable("block.hexerei.crystal_ball.daytime"), true);
         } else {
            player.displayClientMessage(
               Component.translatable("block.hexerei.crystal_ball.moon_phase").append(Component.translatable(phase.getNameTranslated())), true
            );
         }
      }

      return InteractionResult.SUCCESS;
   }

   public CrystalBall(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.crystal_ball_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, ANGLE, WATERLOGGED});
   }

   public void setAngle(Level level, BlockPos pos, BlockState state, int angle) {
      level.setBlock(pos, (BlockState)state.setValue(ANGLE, Mth.clamp(angle, 0, 180)), 2);
   }

   public int getAngle(Level level, BlockPos pos) {
      return (Integer)level.getBlockState(pos).getValue(ANGLE);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return canSupportCenter(level, pos.below(), Direction.UP);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean hasBlockEntity(BlockState state) {
      return true;
   }

   @Override
   public Class<CrystalBallTile> getTileEntityClass() {
      return CrystalBallTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CrystalBallTile((BlockEntityType<?>)ModTileEntities.CRYSTAL_BALL_TILE.get(), pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.CRYSTAL_BALL_TILE.get() ? (world2, pos, state2, entity) -> ((CrystalBallTile)entity).tick() : null;
   }
}
