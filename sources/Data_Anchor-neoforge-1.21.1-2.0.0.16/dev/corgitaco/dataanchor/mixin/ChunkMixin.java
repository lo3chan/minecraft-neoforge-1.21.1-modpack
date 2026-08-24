package dev.corgitaco.dataanchor.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkBlockStateInterceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ProtoChunk.class, LevelChunk.class})
public class ChunkMixin implements ChunkBlockStateInterceptor.Internal {
   @WrapMethod(
      method = {"setBlockState"}
   )
   private BlockState dataAnchor$IncterceptSetBlockState(BlockPos pos, BlockState originalState, boolean isMoving, Operation<BlockState> original) {
      BlockState replacement = this.dataAnchor$getInterceptorState(pos, originalState, originalState, isMoving);
      return (BlockState)original.call(new Object[]{pos, replacement, isMoving});
   }
}
