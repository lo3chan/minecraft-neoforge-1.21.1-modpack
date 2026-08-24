package net.joefoxe.hexerei.block;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ITileEntity<T extends BlockEntity> {
   Class<T> getTileEntityClass();

   default void sync() {
   }

   default void withTileEntityDo(BlockGetter world, BlockPos pos, Consumer<T> action) {
      this.getTileEntityOptional(world, pos).ifPresent(action);
   }

   default InteractionResult onTileEntityUse(BlockGetter world, BlockPos pos, Function<T, InteractionResult> action) {
      return this.getTileEntityOptional(world, pos).map(action).orElse(InteractionResult.PASS);
   }

   default Optional<T> getTileEntityOptional(BlockGetter world, BlockPos pos) {
      return Optional.ofNullable(this.getBlockEntity(world, pos));
   }

   @Nullable
   default T getBlockEntity(BlockGetter worldIn, BlockPos pos) {
      BlockEntity tileEntity = worldIn.getBlockEntity(pos);
      Class<T> expectedClass = this.getTileEntityClass();
      if (tileEntity == null) {
         return null;
      } else {
         return (T)(!expectedClass.isInstance(tileEntity) ? null : tileEntity);
      }
   }
}
