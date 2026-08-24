package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record BlockTintFunctionWrapper(BlockTintFunction function) implements BlockColor {
   public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
      KubeColor c = this.function.getColor(state, level, pos, index);
      return c == null ? -1 : c.kjs$getARGB();
   }
}
