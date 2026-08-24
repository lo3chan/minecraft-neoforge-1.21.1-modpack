package com.aetherteam.aether.block;

import com.aetherteam.aether.event.FreezeEvent;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface FreezingBehavior<T> {
   int FLAG_SHELL = 3;
   int FLAG_VOLUME = 19;

   default int freezeBlocks(Level level, BlockPos origin, T source, float radius) {
      float radiusSq = radius * radius;
      int blocksFrozen = 0;

      for (int x = (int)radius; x >= 0; x--) {
         boolean firstXZ = true;

         for (int z = (int)radius; z >= 0; z--) {
            int xzLengthSq = x * x + z * z;
            if (!(xzLengthSq > radiusSq)) {
               blocksFrozen += this.quarters(level, origin, x, 0, z, source, firstXZ ? 3 : 19);
               firstXZ = false;
               boolean firstY = true;

               for (int y = (int)radius; y >= 0; y--) {
                  if (!(xzLengthSq + y * y > radiusSq)) {
                     int placementFlag = firstY ? 3 : 19;
                     blocksFrozen += this.quarters(level, origin, x, y, z, source, placementFlag);
                     blocksFrozen += this.quarters(level, origin, x, -y, z, source, placementFlag);
                     firstY = false;
                  }
               }
            }
         }
      }

      return this.freezeFromRecipe(level, origin, origin, source, 3) + blocksFrozen;
   }

   private int quarters(Level level, BlockPos origin, int dX, int dY, int dZ, T source, int flag) {
      return this.freezeFromRecipe(level, origin.offset(dX, dY, dZ), origin, source, flag)
         + this.freezeFromRecipe(level, origin.offset(-dZ, dY, dX), origin, source, flag)
         + this.freezeFromRecipe(level, origin.offset(-dX, dY, -dZ), origin, source, flag)
         + this.freezeFromRecipe(level, origin.offset(dZ, dY, -dX), origin, source, flag);
   }

   int freezeFromRecipe(Level var1, BlockPos var2, BlockPos var3, T var4, int var5);

   default int freezeBlockAt(
      Level level, BlockPos pos, BlockPos origin, BlockState oldBlockState, BlockState newBlockState, Optional<CacheableFunction> function, T source, int flag
   ) {
      FreezeEvent event = this.onFreeze(level, pos, origin, oldBlockState, newBlockState, source);
      if (!event.isCanceled()) {
         level.setBlock(pos, newBlockState, flag);
         if (newBlockState.isRandomlyTicking()) {
            level.scheduleTick(pos, newBlockState.getBlock(), Mth.nextInt(level.getRandom(), 60, 120));
         }

         BlockStateRecipeUtil.executeFunction(level, pos, function);
         return 1;
      } else {
         return 0;
      }
   }

   FreezeEvent onFreeze(LevelAccessor var1, BlockPos var2, BlockPos var3, BlockState var4, BlockState var5, T var6);
}
