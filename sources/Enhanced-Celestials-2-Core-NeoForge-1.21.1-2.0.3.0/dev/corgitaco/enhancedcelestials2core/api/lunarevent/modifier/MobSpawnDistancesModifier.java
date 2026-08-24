package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;

public record MobSpawnDistancesModifier(Map<MobCategory, Integer> distances) implements LunarEventModifier {
   public static final MapCodec<MobSpawnDistancesModifier> CODEC = Codec.unboundedMap(MobCategory.CODEC, Codec.INT)
      .fieldOf("distances")
      .xmap(MobSpawnDistancesModifier::new, MobSpawnDistancesModifier::distances);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOB_SPAWN_DISTANCES;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.mob_spawn_distances", new Object[]{this.distances.toString()});
   }
}
