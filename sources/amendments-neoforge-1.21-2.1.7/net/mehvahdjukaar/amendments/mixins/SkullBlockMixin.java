package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net.minecraft.world.level.block.SkullBlock", "net.minecraft.world.level.block.WallSkullBlock"}
)
public abstract class SkullBlockMixin extends AbstractSkullBlock implements SimpleWaterloggedBlock {
   public SkullBlockMixin(Type type, Properties properties) {
      super(type, properties);
   }

   public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!state.hasProperty(BlockStateProperties.WATERLOGGED)) {
         return false;
      } else if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
         if (!level.isClientSide()) {
            level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
      if (!state.hasProperty(BlockStateProperties.WATERLOGGED)) {
         return ItemStack.EMPTY;
      } else if ((Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
         level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
         if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
         }

         return new ItemStack(Items.WATER_BUCKET);
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void amendments$dangerousAddWaterlogging(Type type, Properties properties, CallbackInfo ci) {
      if (this.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED)) {
         this.registerDefaultState((BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE));
      }
   }

   public FluidState getFluidState(BlockState state) {
      return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)
         ? Fluids.WATER.getSource(false)
         : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)stateIn.getValue(BlockStateProperties.WATERLOGGED)) {
         worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      }

      return stateIn;
   }

   @ModifyReturnValue(
      method = {"getStateForPlacement"},
      at = {@At("RETURN")}
   )
   public BlockState amendments$addPlacementWaterlogging(BlockState original, BlockPlaceContext context) {
      if (original != null && original.hasProperty(BlockStateProperties.WATERLOGGED)) {
         FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
         return (BlockState)original.setValue(BlockStateProperties.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8);
      } else {
         return original;
      }
   }

   @Inject(
      method = {"createBlockStateDefinition"},
      at = {@At("RETURN")}
   )
   protected void amendments$addWaterlogging(Builder<Block, BlockState> builder, CallbackInfo ci) {
      if (!builder.properties.containsValue(BlockStateProperties.WATERLOGGED)) {
         builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
      }
   }
}
