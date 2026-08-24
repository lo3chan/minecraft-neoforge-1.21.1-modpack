package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.amendments.common.IBellConnection;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BellBlock.class})
public abstract class BellMixin extends Block {
   protected BellMixin(Properties properties) {
      super(properties);
   }

   @Unique
   public boolean amendments$tryConnect(BlockPos pos, BlockState facingState, LevelAccessor world) {
      BlockEntity te = world.getBlockEntity(pos);
      if (!(te instanceof IBellConnection bc)) {
         return false;
      } else {
         IBellConnection.Type connection = IBellConnection.Type.NONE;
         if (facingState.getBlock() instanceof ChainBlock && facingState.getValue(RotatedPillarBlock.AXIS) == Axis.Y) {
            connection = IBellConnection.Type.CHAIN;
         } else if (CompatHandler.SUPPLEMENTARIES && SuppCompat.isRope(facingState.getBlock())) {
            connection = IBellConnection.Type.ROPE;
         }

         bc.amendments$setConnected(connection);
         te.setChanged();
         return true;
      }
   }

   @Inject(
      method = {"updateShape"},
      at = {@At("HEAD")}
   )
   public void updateShape(
      BlockState stateIn,
      Direction facing,
      BlockState facingState,
      LevelAccessor worldIn,
      BlockPos currentPos,
      BlockPos facingPos,
      CallbackInfoReturnable<BlockState> info
   ) {
      try {
         if (facing == Direction.DOWN && this.amendments$tryConnect(currentPos, facingState, worldIn) && worldIn instanceof Level level) {
            level.sendBlockUpdated(currentPos, stateIn, stateIn, 2);
         }
      } catch (Exception var9) {
      }
   }

   @Inject(
      method = {"attemptToRing(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
      )}
   )
   public void amendments$updateObservers(
      Entity entity, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir, @Local BlockEntity blockEntity
   ) {
      BlockState state = blockEntity.getBlockState();
      if (!(Boolean)state.getValue(BellBlock.POWERED)) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(BellBlock.POWERED, true));
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(BellBlock.POWERED, false));
      }
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(worldIn, pos, state, placer, stack);
      this.amendments$tryConnect(pos, worldIn.getBlockState(pos.below()), worldIn);
   }
}
