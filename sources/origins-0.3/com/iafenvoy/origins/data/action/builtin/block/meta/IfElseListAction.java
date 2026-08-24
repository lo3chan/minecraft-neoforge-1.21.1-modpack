package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record IfElseListAction(List<IfElseListAction.ConditionedActionHolder> actions) implements BlockAction {
   public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(IfElseListAction.ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions))
         .apply(i, IfElseListAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BlockAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
      for (IfElseListAction.ConditionedActionHolder holder : this.actions) {
         if (holder.condition.test(level, pos)) {
            holder.action.execute(level, pos, direction);
            break;
         }
      }
   }

   private record ConditionedActionHolder(BlockCondition condition, BlockAction action) {
      public static final Codec<IfElseListAction.ConditionedActionHolder> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               BlockCondition.CODEC.fieldOf("condition").forGetter(IfElseListAction.ConditionedActionHolder::condition),
               BlockAction.CODEC.fieldOf("action").forGetter(IfElseListAction.ConditionedActionHolder::action)
            )
            .apply(i, IfElseListAction.ConditionedActionHolder::new)
      );
   }
}
