package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record MobSpawnSettingsModifier(MobSpawnSettings spawnSettings) implements LunarEventModifier {
   public static final MapCodec<MobSpawnSettingsModifier> CODEC = MobSpawnSettings.CODEC
      .fieldOf("spawn_settings")
      .xmap(MobSpawnSettingsModifier::new, MobSpawnSettingsModifier::spawnSettings);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOB_SPAWN_SETTINGS;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.mob_spawn_settings", new Object[]{this.spawnSettings.toString()});
   }
}
