package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public final class LightUpAction implements BlockAction {
   public static final MapCodec<LightUpAction> CODEC = MapCodec.unit(new LightUpAction());

   @NotNull
   @Override
   public MapCodec<? extends BlockAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
      BlockState state = level.getBlockState(pos);
      if (state.hasProperty(BlockStateProperties.LIT) && !(Boolean)state.getValue(BlockStateProperties.LIT)) {
         level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, true), 3);
      }
   }
}
