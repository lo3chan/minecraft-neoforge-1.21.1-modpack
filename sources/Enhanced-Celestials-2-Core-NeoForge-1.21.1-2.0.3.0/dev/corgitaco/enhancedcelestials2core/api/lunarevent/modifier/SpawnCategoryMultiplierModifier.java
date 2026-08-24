package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;

public record SpawnCategoryMultiplierModifier(Map<MobCategory, Double> multipliers) implements LunarEventModifier {
   public static final MapCodec<SpawnCategoryMultiplierModifier> CODEC = Codec.unboundedMap(MobCategory.CODEC, Codec.DOUBLE)
      .fieldOf("multipliers")
      .xmap(SpawnCategoryMultiplierModifier::new, SpawnCategoryMultiplierModifier::multipliers);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.SPAWN_CATEGORY_MULTIPLIER;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.spawn_category_multiplier", new Object[]{this.multipliers.toString()});
   }
}
