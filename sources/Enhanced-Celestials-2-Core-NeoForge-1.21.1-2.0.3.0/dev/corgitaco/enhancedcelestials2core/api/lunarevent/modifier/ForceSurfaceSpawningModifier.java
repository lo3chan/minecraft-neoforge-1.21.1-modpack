package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record ForceSurfaceSpawningModifier() implements LunarEventModifier {
   public static final MapCodec<ForceSurfaceSpawningModifier> CODEC = MapCodec.unit(ForceSurfaceSpawningModifier::new);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.FORCE_SURFACE_SPAWNING;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.force_surface_spawning");
   }
}
