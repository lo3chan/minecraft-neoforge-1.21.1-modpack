package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.StatReference;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ResetStatAction(List<StatReference> stats) implements EntityAction {
   public static final MapCodec<ResetStatAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(CombinedCodecs.STAT_REFERENCE.fieldOf("stat").forGetter(ResetStatAction::stats)).apply(i, ResetStatAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source) {
      if (source instanceof ServerPlayer player) {
         for (StatReference ref : this.stats) {
            Stat<?> resolved = ref.resolve();
            if (resolved != null) {
               player.resetStat(resolved);
            }
         }
      }
   }
}
