package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AndAction(List<BlockAction> actions) implements BlockAction {
   public static final MapCodec<AndAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BlockAction.CODEC.listOf().fieldOf("actions").forGetter(AndAction::actions)).apply(i, AndAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BlockAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
      this.actions.forEach(x -> x.execute(level, pos, direction));
   }
}
