package net.bettercombat.api.fx;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ConditionalTrailAppearance(TrailAppearance default_appearance, LinkedHashMap<String, TrailAppearance> conditional) {
   public ConditionalTrailAppearance(TrailAppearance default_appearance) {
      this(default_appearance, new LinkedHashMap<>());
   }

   public ConditionalTrailAppearance() {
      this(TrailAppearance.DEFAULT, new LinkedHashMap<>());
   }

   @Nullable
   public TrailAppearance resolve(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         for (Entry<String, TrailAppearance> entry : this.conditional.entrySet()) {
            String conditionId = entry.getKey();
            if (ItemConditions.test(conditionId, itemStack)) {
               return entry.getValue();
            }
         }

         return this.default_appearance;
      } else {
         return this.default_appearance;
      }
   }

   public ConditionalTrailAppearance merge(@Nullable ConditionalTrailAppearance override) {
      if (override == null) {
         return this;
      } else {
         TrailAppearance mergedDefault = override.default_appearance != null ? override.default_appearance : this.default_appearance;
         LinkedHashMap<String, TrailAppearance> mergedConditional = new LinkedHashMap<>(this.conditional);
         mergedConditional.putAll(override.conditional);
         return new ConditionalTrailAppearance(mergedDefault, mergedConditional);
      }
   }
}
