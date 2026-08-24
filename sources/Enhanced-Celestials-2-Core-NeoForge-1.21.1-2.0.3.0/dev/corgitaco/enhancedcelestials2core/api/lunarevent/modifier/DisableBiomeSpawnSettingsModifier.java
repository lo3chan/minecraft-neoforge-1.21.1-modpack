package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record DisableBiomeSpawnSettingsModifier() implements LunarEventModifier {
   public static final MapCodec<DisableBiomeSpawnSettingsModifier> CODEC = MapCodec.unit(DisableBiomeSpawnSettingsModifier::new);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.DISABLE_BIOME_SPAWN_SETTINGS;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.disable_biome_spawn_settings");
   }
}
