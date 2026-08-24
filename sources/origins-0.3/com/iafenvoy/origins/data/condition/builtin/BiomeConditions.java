package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.SimpleConditions;
import com.iafenvoy.origins.data.condition.builtin.biome.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.PrecipitationCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.TemperatureCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.OrCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BiomeConditions {
   public static final DeferredRegister<MapCodec<? extends BiomeCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<? extends BiomeCondition>> HIGH_HUMIDITY = REGISTRY.register(
      "high_humidity",
      () -> SimpleConditions.createBiome((biome, pos) -> biome.isBound() && ((Biome)biome.value()).getModifiedClimateSettings().downfall() > 0.85F)
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<PrecipitationCondition>> PRECIPITATION = REGISTRY.register(
      "precipitation", () -> PrecipitationCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<TemperatureCondition>> TEMPERATURE = REGISTRY.register(
      "temperature", () -> TemperatureCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
