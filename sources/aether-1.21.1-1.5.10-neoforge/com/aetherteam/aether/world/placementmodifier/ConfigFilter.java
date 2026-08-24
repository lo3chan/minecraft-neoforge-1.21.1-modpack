package com.aetherteam.aether.world.placementmodifier;

import com.aetherteam.aether.data.ConfigSerializationUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigFilter extends PlacementFilter {
   public static final MapCodec<ConfigFilter> CODEC = Codec.STRING
      .comapFlatMap(ConfigFilter::buildDeserialization, configFilter -> ConfigSerializationUtil.serialize(configFilter.config))
      .fieldOf("value");
   private final ConfigValue<Boolean> config;

   public ConfigFilter(ConfigValue<Boolean> config) {
      this.config = config;
   }

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      return (Boolean)this.config.get();
   }

   public PlacementModifierType<?> type() {
      return (PlacementModifierType<?>)AetherPlacementModifiers.CONFIG_FILTER.get();
   }

   private static DataResult<ConfigFilter> buildDeserialization(String configId) {
      return ConfigSerializationUtil.deserialize(configId) instanceof BooleanValue booleanConfigEntry
         ? DataResult.success(new ConfigFilter(booleanConfigEntry))
         : DataResult.error(() -> "Config entry " + configId + " does not provide a boolean! Must be boolean (true/false), to be valid for ConfigFilter.");
   }
}
