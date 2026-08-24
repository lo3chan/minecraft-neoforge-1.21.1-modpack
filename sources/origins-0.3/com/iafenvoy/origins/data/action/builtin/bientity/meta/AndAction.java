package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AndAction(List<BiEntityAction> actions) implements BiEntityAction {
   public static final MapCodec<AndAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BiEntityAction.CODEC.listOf().fieldOf("actions").forGetter(AndAction::actions)).apply(i, AndAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiEntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source, @NotNull Entity target) {
      this.actions.forEach(x -> x.execute(source, target));
   }
}
