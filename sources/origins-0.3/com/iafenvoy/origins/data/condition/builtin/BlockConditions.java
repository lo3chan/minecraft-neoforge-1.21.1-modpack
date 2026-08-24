package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.SimpleConditions;
import com.iafenvoy.origins.data.condition.builtin.block.AdjacentCondition;
import com.iafenvoy.origins.data.condition.builtin.block.BlastResistanceCondition;
import com.iafenvoy.origins.data.condition.builtin.block.BlockIdCondition;
import com.iafenvoy.origins.data.condition.builtin.block.BlockStateCondition;
import com.iafenvoy.origins.data.condition.builtin.block.DistanceFromCoordinatesCondition;
import com.iafenvoy.origins.data.condition.builtin.block.FluidIdCondition;
import com.iafenvoy.origins.data.condition.builtin.block.HardnessCondition;
import com.iafenvoy.origins.data.condition.builtin.block.HeightCondition;
import com.iafenvoy.origins.data.condition.builtin.block.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.block.LightLevelCondition;
import com.iafenvoy.origins.data.condition.builtin.block.MovementBlockingCondition;
import com.iafenvoy.origins.data.condition.builtin.block.NbtCondition;
import com.iafenvoy.origins.data.condition.builtin.block.SlipperinessCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.OffsetCondition;
import com.iafenvoy.origins.data.condition.builtin.block.meta.OrCondition;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockConditions {
   public static final DeferredRegister<MapCodec<? extends BlockCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BLOCK_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AdjacentCondition>> ADJACENT = REGISTRY.register(
      "adjacent", () -> AdjacentCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> AIR = REGISTRY.register(
      "air", () -> SimpleConditions.createBlock((level, pos) -> level.getBlockState(pos).isAir())
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> ATTACHABLE = REGISTRY.register(
      "attachable",
      () -> SimpleConditions.createBlock(
         (level, pos) -> Arrays.stream(Direction.values()).anyMatch(d -> level.getBlockState(pos.relative(d)).isFaceSturdy(level, pos, d.getOpposite()))
      )
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlastResistanceCondition>> BLAST_RESISTANCE = REGISTRY.register(
      "blast_resistance", () -> BlastResistanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> BLOCK_ENTITY = REGISTRY.register(
      "block_entity", () -> SimpleConditions.createBlock((level, pos) -> Objects.nonNull(level.getBlockEntity(pos)))
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockIdCondition>> BLOCK_ID = REGISTRY.register(
      "block_id", () -> BlockIdCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockStateCondition>> BLOCK_STATE = REGISTRY.register(
      "block_state", () -> BlockStateCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<DistanceFromCoordinatesCondition>> DISTANCE_FROM_COORDINATES = REGISTRY.register(
      "distance_from_coordinates", () -> DistanceFromCoordinatesCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> EXPOSED_TO_SKY = REGISTRY.register(
      "exposed_to_sky", () -> SimpleConditions.createBlock(BlockAndTintGetter::canSeeSky)
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<FluidIdCondition>> FLUID = REGISTRY.register(
      "fluid", () -> FluidIdCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<HardnessCondition>> HARDNESS = REGISTRY.register(
      "hardness", () -> HardnessCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<HeightCondition>> HEIGHT = REGISTRY.register(
      "height", () -> HeightCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> IN_RAIN = REGISTRY.register(
      "in_rain", () -> SimpleConditions.createBlock(Level::isRainingAt)
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> LIGHT_BLOCKING = REGISTRY.register(
      "light_blocking", () -> SimpleConditions.createBlock((level, pos) -> level.getBlockState(pos).canOcclude())
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<LightLevelCondition>> LIGHT_LEVEL = REGISTRY.register(
      "light_level", () -> LightLevelCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<MovementBlockingCondition>> MOVEMENT_BLOCKING = REGISTRY.register(
      "movement_blocking", () -> MovementBlockingCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<NbtCondition>> NBT = REGISTRY.register("nbt", () -> NbtCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> RAINING = REGISTRY.register(
      "raining", () -> SimpleConditions.createBlock((level, pos) -> level.isRaining())
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> REPLACEABLE = REGISTRY.register(
      "replaceable", () -> SimpleConditions.createBlock((level, pos) -> level.getBlockState(pos).canBeReplaced())
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<SlipperinessCondition>> SLIPPERINESS = REGISTRY.register(
      "slipperiness", () -> SlipperinessCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> THUNDERING = REGISTRY.register(
      "thundering", () -> SimpleConditions.createBlock((level, pos) -> level.isThundering())
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> WATER_LOGGABLE = REGISTRY.register(
      "water_loggable", () -> SimpleConditions.createBlock((level, pos) -> level.getBlockState(pos).getBlock() instanceof LiquidBlockContainer)
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<OffsetCondition>> OFFSET = REGISTRY.register(
      "offset", () -> OffsetCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
