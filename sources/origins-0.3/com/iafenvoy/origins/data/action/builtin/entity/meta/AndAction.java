package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AndAction(List<EntityAction> actions) implements EntityAction {
   public static final MapCodec<AndAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(EntityAction.CODEC.listOf().fieldOf("actions").forGetter(AndAction::actions)).apply(i, AndAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source) {
      this.actions.forEach(x -> x.execute(source));
   }
}
