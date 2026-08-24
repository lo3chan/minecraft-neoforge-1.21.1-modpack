package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AndAction(List<ItemAction> actions) implements ItemAction {
   public static final MapCodec<AndAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(ItemAction.CODEC.listOf().fieldOf("actions").forGetter(AndAction::actions)).apply(i, AndAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends ItemAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
      this.actions.forEach(x -> x.execute(level, source, access));
   }
}
