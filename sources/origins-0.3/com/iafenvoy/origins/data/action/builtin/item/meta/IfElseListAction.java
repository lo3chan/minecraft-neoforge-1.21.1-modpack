package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record IfElseListAction(List<IfElseListAction.ConditionedActionHolder> actions) implements ItemAction {
   public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(IfElseListAction.ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions))
         .apply(i, IfElseListAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends ItemAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
      for (IfElseListAction.ConditionedActionHolder holder : this.actions) {
         if (holder.condition.test(level, access.get())) {
            holder.action.execute(level, source, access);
            break;
         }
      }
   }

   private record ConditionedActionHolder(ItemCondition condition, ItemAction action) {
      public static final Codec<IfElseListAction.ConditionedActionHolder> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               ItemCondition.CODEC.fieldOf("condition").forGetter(IfElseListAction.ConditionedActionHolder::condition),
               ItemAction.CODEC.fieldOf("action").forGetter(IfElseListAction.ConditionedActionHolder::action)
            )
            .apply(i, IfElseListAction.ConditionedActionHolder::new)
      );
   }
}
