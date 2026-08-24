package vazkii.psi.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.common.block.tile.TileCADAssembler;

public class BlockCADAssembler extends HorizontalDirectionalBlock implements EntityBlock {
   public static final MapCodec<BlockCADAssembler> CODEC = simpleCodec(BlockCADAssembler::new);

   public BlockCADAssembler(Properties props) {
      super(props);
   }

   @NotNull
   public MapCodec<BlockCADAssembler> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
   }

   public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos) {
      IItemHandler handler = (IItemHandler)worldIn.getCapability(ItemHandler.BLOCK, pos, null);
      return handler != null ? ItemHandlerHelper.calcRedstoneFromInventory(handler) : 0;
   }

   @NotNull
   public InteractionResult useWithoutItem(
      @NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player playerIn, @NotNull BlockHitResult rayTraceResult
   ) {
      if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         MenuProvider container = state.getMenuProvider(world, pos);
         if (container != null) {
            playerIn.openMenu(container, pos);
         }

         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
      BlockEntity te = world.getBlockEntity(pos);
      return te instanceof TileCADAssembler ? (MenuProvider)te : null;
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileCADAssembler(pos, state);
   }

   public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock() && !isMoving) {
         TileCADAssembler te = (TileCADAssembler)world.getBlockEntity(pos);
         if (te != null) {
            for (int i = 0; i < te.getInventory().getSlots(); i++) {
               ItemStack stack = te.getInventory().getStackInSlot(i);
               if (!stack.isEmpty()) {
                  Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
               }
            }
         }
      }

      super.onRemove(state, world, pos, newState, isMoving);
   }
}
