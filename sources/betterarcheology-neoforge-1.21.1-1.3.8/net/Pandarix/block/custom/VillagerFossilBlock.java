package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import net.Pandarix.block.entity.VillagerFossilBlockEntity;
import net.Pandarix.util.ServerPlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillagerFossilBlock extends FossilBaseWithEntityBlock {
   public static final MapCodec<ArchelogyTable> CODEC = simpleCodec(ArchelogyTable::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final IntegerProperty INVENTORY_LUMINANCE = IntegerProperty.create("inventory_luminance", 0, 15);
   private static final Map<Direction, VoxelShape> VILLAGER_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(
         Block.box(4.75, 0.0, 9.0, 11.0, 10.0, 12.0), new VoxelShape[]{Block.box(4.0, 10.0, 7.0, 12.0, 20.0, 12.5), Block.box(3.0, 20.0, 2.0, 11.0, 29.0, 7.5)}
      ),
      Direction.SOUTH,
      Shapes.or(
         Block.box(5.0, 0.0, 4.0, 11.25, 10.0, 7.0), new VoxelShape[]{Block.box(4.0, 10.0, 3.5, 12.0, 20.0, 9.0), Block.box(5.0, 20.0, 8.5, 13.0, 29.0, 14.0)}
      ),
      Direction.EAST,
      Shapes.or(
         Block.box(4.0, 0.0, 4.75, 7.0, 10.0, 11.0), new VoxelShape[]{Block.box(3.5, 10.0, 4.0, 9.0, 20.0, 12.0), Block.box(8.5, 20.0, 3.0, 14.0, 29.0, 11.0)}
      ),
      Direction.WEST,
      Shapes.or(
         Block.box(9.0, 0.0, 5.0, 12.0, 10.0, 11.25), new VoxelShape[]{Block.box(7.0, 10.0, 4.0, 12.5, 20.0, 12.0), Block.box(2.0, 20.0, 5.0, 7.5, 29.0, 13.0)}
      )
   );

   @NotNull
   @Override
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public VillagerFossilBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(INVENTORY_LUMINANCE, 0));
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock() && pLevel.getBlockEntity(pPos) instanceof VillagerFossilBlockEntity villagerFossilBlockEntity) {
         Containers.dropContents(pLevel, pPos, villagerFossilBlockEntity);
         pLevel.updateNeighbourForOutputSignal(pPos, this);
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return VILLAGER_SHAPES_FOR_DIRECTION.get(pState.getValue(FACING));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{INVENTORY_LUMINANCE});
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new VillagerFossilBlockEntity(pos, state);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @NotNull
   @Override
   public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
      if (!pLevel.isClientSide()) {
         BlockEntity entity = pLevel.getBlockEntity(pPos);
         if (!(entity instanceof VillagerFossilBlockEntity)) {
            throw new IllegalStateException("VillagerFossilBlockEntity Container provider is missing!");
         }

         ServerPlayerHelper.tryOpenScreen(pPlayer, (VillagerFossilBlockEntity)entity);
      }

      return InteractionResult.sidedSuccess(pLevel.isClientSide());
   }

   public void appendHoverText(@NotNull ItemStack stack, TooltipContext pContext, List<Component> component, @NotNull TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.villager_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, pContext, component, flag);
   }
}
