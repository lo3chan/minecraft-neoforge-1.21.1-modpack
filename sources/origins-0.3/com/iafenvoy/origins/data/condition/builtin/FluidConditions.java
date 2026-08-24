package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.data.condition.SimpleConditions;
import com.iafenvoy.origins.data.condition.builtin.fluid.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.OrCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FluidConditions {
   public static final DeferredRegister<MapCodec<? extends FluidCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.FLUID_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<? extends FluidCondition>> EMPTY = REGISTRY.register(
      "empty", () -> SimpleConditions.createFluid(FluidState::isEmpty)
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<? extends FluidCondition>> STILL = REGISTRY.register(
      "still", () -> SimpleConditions.createFluid(FluidState::isSource)
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
