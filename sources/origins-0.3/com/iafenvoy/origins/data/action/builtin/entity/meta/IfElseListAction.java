package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record IfElseListAction(List<IfElseListAction.ConditionedActionHolder> actions) implements EntityAction {
   public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(IfElseListAction.ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions))
         .apply(i, IfElseListAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source) {
      for (IfElseListAction.ConditionedActionHolder holder : this.actions) {
         if (holder.condition.test(source)) {
            holder.action.execute(source);
            break;
         }
      }
   }

   private record ConditionedActionHolder(EntityCondition condition, EntityAction action) {
      public static final Codec<IfElseListAction.ConditionedActionHolder> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               EntityCondition.CODEC.fieldOf("condition").forGetter(IfElseListAction.ConditionedActionHolder::condition),
               EntityAction.CODEC.fieldOf("action").forGetter(IfElseListAction.ConditionedActionHolder::action)
            )
            .apply(i, IfElseListAction.ConditionedActionHolder::new)
      );
   }
}
