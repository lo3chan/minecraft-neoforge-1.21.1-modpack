package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record IfElseListAction(List<IfElseListAction.ConditionedActionHolder> actions) implements BiEntityAction {
   public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(IfElseListAction.ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions))
         .apply(i, IfElseListAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiEntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source, @NotNull Entity target) {
      for (IfElseListAction.ConditionedActionHolder holder : this.actions) {
         if (holder.condition.test(source, target)) {
            holder.action.execute(source, target);
            break;
         }
      }
   }

   private record ConditionedActionHolder(BiEntityCondition condition, BiEntityAction action) {
      public static final Codec<IfElseListAction.ConditionedActionHolder> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               BiEntityCondition.CODEC.fieldOf("condition").forGetter(IfElseListAction.ConditionedActionHolder::condition),
               BiEntityAction.CODEC.fieldOf("action").forGetter(IfElseListAction.ConditionedActionHolder::action)
            )
            .apply(i, IfElseListAction.ConditionedActionHolder::new)
      );
   }
}
