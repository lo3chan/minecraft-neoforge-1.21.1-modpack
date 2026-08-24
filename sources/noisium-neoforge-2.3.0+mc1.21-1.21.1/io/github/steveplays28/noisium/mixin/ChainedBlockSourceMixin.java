package io.github.steveplays28.noisium.mixin;

import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.NoiseChunk.BlockStateFiller;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({MaterialRuleList.class})
public abstract class ChainedBlockSourceMixin {
   @Shadow
   @Final
   private List<BlockStateFiller> materialRuleList;

   @Overwrite
   @Nullable
   public BlockState calculate(FunctionContext pos) {
      for (int i = 0; i < this.materialRuleList.size(); i++) {
         BlockState blockState = this.materialRuleList.get(i).calculate(pos);
         if (blockState != null) {
            return blockState;
         }
      }

      return null;
   }
}
