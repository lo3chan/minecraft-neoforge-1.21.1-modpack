package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record SlimesSpawnEverywhereModifier() implements LunarEventModifier {
   public static final MapCodec<SlimesSpawnEverywhereModifier> CODEC = MapCodec.unit(SlimesSpawnEverywhereModifier::new);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.SLIMES_SPAWN_EVERYWHERE;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.slimes_spawn_everywhere");
   }
}
