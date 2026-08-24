package net.mehvahdjukaar.moonlight.core.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record PatternMatchLootItemCondition(List<Pattern> patterns) implements LootItemCondition {
   public static final MapCodec<PatternMatchLootItemCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(ExtraCodecs.PATTERN.listOf().fieldOf("matches").forGetter(o -> o.patterns)).apply(i, PatternMatchLootItemCondition::new)
   );

   public boolean test(LootContext lootContext) {
      String id = ForgeHelper.getQueriedLootTableId(lootContext).toString();

      for (Pattern p : this.patterns) {
         if (id.equals(p.pattern())) {
            return true;
         }

         if (p.matcher(id).find()) {
            return true;
         }
      }

      return false;
   }

   public LootItemConditionType getType() {
      return MoonlightRegistry.PATTERN_MATCH_CONDITION.get();
   }
}
