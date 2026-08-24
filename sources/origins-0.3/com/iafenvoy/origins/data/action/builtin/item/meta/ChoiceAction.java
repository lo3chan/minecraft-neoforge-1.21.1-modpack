package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data._common.WeightedActionEntry;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ChoiceAction(List<WeightedActionEntry<ItemAction>> actions) implements ItemAction {
   public static final MapCodec<ChoiceAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(WeightedActionEntry.codec(ItemAction.CODEC).listOf().fieldOf("actions").forGetter(ChoiceAction::actions)).apply(i, ChoiceAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends ItemAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
      WeightedActionEntry<ItemAction> holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
      if (holder != null) {
         holder.element().execute(level, source, access);
      }
   }
}
