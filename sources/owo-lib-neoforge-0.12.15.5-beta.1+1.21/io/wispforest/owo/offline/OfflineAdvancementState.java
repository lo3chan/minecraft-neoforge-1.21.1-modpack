package io.wispforest.owo.offline;

import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;

public class OfflineAdvancementState {
   private final Map<ResourceLocation, AdvancementProgress> advancementData;

   OfflineAdvancementState(Map<ResourceLocation, AdvancementProgress> advancementData) {
      this.advancementData = advancementData;
   }

   public Map<ResourceLocation, AdvancementProgress> advancementData() {
      return this.advancementData;
   }

   public AdvancementProgress getOrAddProgress(AdvancementHolder advancement) {
      return this.advancementData.computeIfAbsent(advancement.id(), id -> {
         AdvancementProgress progress = new AdvancementProgress();
         progress.update(advancement.value().requirements());
         return progress;
      });
   }

   public void grant(AdvancementHolder advancement) {
      AdvancementProgress progress = this.getOrAddProgress(advancement);

      for (String criterion : progress.getRemainingCriteria()) {
         progress.grantProgress(criterion);
      }
   }

   public void revoke(AdvancementHolder advancement) {
      AdvancementProgress progress = this.getOrAddProgress(advancement);

      for (String criterion : progress.getCompletedCriteria()) {
         progress.revokeProgress(criterion);
      }
   }
}
