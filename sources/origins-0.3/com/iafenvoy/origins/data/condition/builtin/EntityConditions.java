package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.SimpleConditions;
import com.iafenvoy.origins.data.condition.builtin.entity.AbilityCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.AdvancementCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.AirCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.AttributeCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.BiomeInCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.BlockCollisionCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.BlockInRadiusCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.BrightnessCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.CanHaveEffectCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.CommandCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.DimensionCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.DistanceFromCoordinatesCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.ElytraFlightPossibleCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.EnchantmentCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.EntityInRadiusCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.EntityTypeCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.EquippedItemCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.ExposedToSkyCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.ExposedToSunCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.FallDistanceCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.FluidHeightCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.FoodLevelCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.GamemodeCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.GlowingCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.HealthCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.InBlockAnywhereCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.InBlockCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.InventoryCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.MobEffectCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.NbtCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.OnBlockCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.OriginCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PassengerCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PassengerRecursiveCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PowerActiveCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PowerTypeCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PredicateCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.RaycastCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.RelativeHealthCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.ResourceCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.RidingCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.RidingRecursiveCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.RidingRootCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.SaturationLevelCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.ScoreboardCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.SetSizeCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.SneakingCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.StatCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.SubmergedInCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.TimeOfDayCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.UsingEffectiveToolCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.UsingItemCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.XPLevelsCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.XPPointsCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.OrCondition;
import com.iafenvoy.origins.util.LevelUtil;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityConditions {
   public static final DeferredRegister<MapCodec<? extends EntityCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.ENTITY_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AbilityCondition>> ABILITY = REGISTRY.register(
      "ability", () -> AbilityCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AdvancementCondition>> ADVANCEMENT = REGISTRY.register(
      "advancement", () -> AdvancementCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> ATTACKER_CONDITION = REGISTRY.register(
      "attacker_condition", () -> SimpleConditions.createEntity(entity -> entity instanceof LivingEntity living && living.getLastHurtByMob() != null)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> ATTACK_TARGET_CONDITION = REGISTRY.register(
      "attack_target_condition", () -> SimpleConditions.createEntity(entity -> entity instanceof Mob mob && mob.getTarget() != null)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AirCondition>> AIR = REGISTRY.register("air", () -> AirCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AttributeCondition>> ATTRIBUTE = REGISTRY.register(
      "attribute", () -> AttributeCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BiomeInCondition>> BIOME = REGISTRY.register(
      "biome_in", () -> BiomeInCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BlockCollisionCondition>> BLOCK_COLLISION = REGISTRY.register(
      "block_collision", () -> BlockCollisionCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BlockInRadiusCondition>> BLOCK_IN_RADIUS = REGISTRY.register(
      "block_in_radius", () -> BlockInRadiusCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BrightnessCondition>> BRIGHTNESS = REGISTRY.register(
      "brightness", () -> BrightnessCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<CanHaveEffectCondition>> CAN_HAVE_EFFECT = REGISTRY.register(
      "can_have_effect", () -> CanHaveEffectCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> CLIMBING = REGISTRY.register(
      "climbing", () -> SimpleConditions.createEntity(entity -> entity instanceof LivingEntity living && living.onClimbable())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> COLLIDED_HORIZONTALLY = REGISTRY.register(
      "collided_horizontally", () -> SimpleConditions.createEntity(entity -> entity.horizontalCollision)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<CommandCondition>> COMMAND = REGISTRY.register(
      "command", () -> CommandCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> CREATIVE_FLYING = REGISTRY.register(
      "creative_flying", () -> SimpleConditions.createEntity(entity -> entity instanceof Player player && player.getAbilities().flying)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> DAYTIME = REGISTRY.register(
      "daytime", () -> SimpleConditions.createEntity(entity -> entity.level().getDayTime() % 24000L < 13000L)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<DimensionCondition>> DIMENSION = REGISTRY.register(
      "dimension", () -> DimensionCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<DistanceFromCoordinatesCondition>> DISTANCE_FROM_COORDINATES = REGISTRY.register(
      "distance_from_coordinates", () -> DistanceFromCoordinatesCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ElytraFlightPossibleCondition>> ELYTRA_FLIGHT_POSSIBLE = REGISTRY.register(
      "elytra_flight_possible", () -> ElytraFlightPossibleCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EnchantmentCondition>> ENCHANTMENT = REGISTRY.register(
      "enchantment", () -> EnchantmentCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EntityInRadiusCondition>> ENTITY_IN_RADIUS = REGISTRY.register(
      "entity_in_radius", () -> EntityInRadiusCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EntityTypeCondition>> ENTITY_TYPE = REGISTRY.register(
      "entity_type", () -> EntityTypeCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EquippedItemCondition>> EQUIPPED_ITEM = REGISTRY.register(
      "equipped_item", () -> EquippedItemCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> EXISTS = REGISTRY.register(
      "exists", () -> SimpleConditions.createEntity(entity -> !entity.isRemoved())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ExposedToSkyCondition>> EXPOSED_TO_SKY = REGISTRY.register(
      "exposed_to_sky", () -> ExposedToSkyCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ExposedToSunCondition>> EXPOSED_TO_SUN = REGISTRY.register(
      "exposed_to_sun", () -> ExposedToSunCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<FallDistanceCondition>> FALL_DISTANCE = REGISTRY.register(
      "fall_distance", () -> FallDistanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> FALL_FLYING = REGISTRY.register(
      "fall_flying", () -> SimpleConditions.createEntity(entity -> entity instanceof LivingEntity living && living.isFallFlying())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<FluidHeightCondition>> FLUID_HEIGHT = REGISTRY.register(
      "fluid_height", () -> FluidHeightCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<FoodLevelCondition>> FOOD_LEVEL = REGISTRY.register(
      "food_level", () -> FoodLevelCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<GamemodeCondition>> GAMEMODE = REGISTRY.register(
      "gamemode", () -> GamemodeCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<GlowingCondition>> GLOWING = REGISTRY.register(
      "glowing", () -> GlowingCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> GROUNDED = REGISTRY.register(
      "grounded", () -> SimpleConditions.createEntity(Entity::onGround)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<HealthCondition>> HEALTH = REGISTRY.register(
      "health", () -> HealthCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> HOSTILE = REGISTRY.register(
      "hostile", () -> SimpleConditions.createEntity(Enemy.class::isInstance)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InBlockCondition>> IN_BLOCK = REGISTRY.register(
      "in_block", () -> InBlockCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InBlockAnywhereCondition>> IN_BLOCK_ANYWHERE = REGISTRY.register(
      "in_block_anywhere", () -> InBlockAnywhereCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> IN_RAIN = REGISTRY.register(
      "in_rain", () -> SimpleConditions.createEntity(Entity::isInRain)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> IN_SNOW = REGISTRY.register(
      "in_snow",
      () -> SimpleConditions.createEntity(
         entity -> LevelUtil.inSnow(entity.level(), BlockPos.containing(MiscUtil.getPoseDependentEyePos(entity)), entity.blockPosition())
      )
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> IN_THUNDERSTORM = REGISTRY.register(
      "in_thunderstorm",
      () -> SimpleConditions.createEntity(
         entity -> LevelUtil.inThunderstorm(entity.level(), BlockPos.containing(MiscUtil.getPoseDependentEyePos(entity)), entity.blockPosition())
      )
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InventoryCondition>> INVENTORY = REGISTRY.register(
      "inventory", () -> InventoryCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> INVISIBLE = REGISTRY.register(
      "invisible", () -> SimpleConditions.createEntity(Entity::isInvisible)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> LIVING = REGISTRY.register(
      "living", () -> SimpleConditions.createEntity(LivingEntity.class::isInstance)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<MobEffectCondition>> MOB_EFFECT = REGISTRY.register(
      "mob_effect", () -> MobEffectCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> MOVING = REGISTRY.register(
      "moving", () -> SimpleConditions.createEntity(entity -> ((MovingEntity)entity).origins$isMoving())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<NbtCondition>> NBT = REGISTRY.register("nbt", () -> NbtCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OnBlockCondition>> ON_BLOCK = REGISTRY.register(
      "on_block", () -> OnBlockCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> ON_FIRE = REGISTRY.register(
      "on_fire", () -> SimpleConditions.createEntity(Entity::isOnFire)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OriginCondition>> ORIGIN = REGISTRY.register(
      "origin", () -> OriginCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PassengerCondition>> PASSENGER = REGISTRY.register(
      "passenger", () -> PassengerCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PassengerRecursiveCondition>> PASSENGER_RECURSIVE = REGISTRY.register(
      "passenger_recursive", () -> PassengerRecursiveCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PowerActiveCondition>> POWER_ACTIVE = REGISTRY.register(
      "power_active", () -> PowerActiveCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PowerTypeCondition>> POWER_TYPE = REGISTRY.register(
      "power_type", () -> PowerTypeCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PredicateCondition>> PREDICATE = REGISTRY.register(
      "predicate", () -> PredicateCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<RaycastCondition>> RAYCAST = REGISTRY.register(
      "raycast", () -> RaycastCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<RelativeHealthCondition>> RELATIVE_HEALTH = REGISTRY.register(
      "relative_health", () -> RelativeHealthCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> RAINING = REGISTRY.register(
      "raining", () -> SimpleConditions.createEntity(entity -> entity.level().isRaining())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ResourceCondition>> RESOURCE = REGISTRY.register(
      "resource", () -> ResourceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<RidingCondition>> RIDING = REGISTRY.register(
      "riding", () -> RidingCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<RidingRecursiveCondition>> RIDING_RECURSIVE = REGISTRY.register(
      "riding_recursive", () -> RidingRecursiveCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<RidingRootCondition>> RIDING_ROOT = REGISTRY.register(
      "riding_root", () -> RidingRootCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SaturationLevelCondition>> SATURATION_LEVEL = REGISTRY.register(
      "saturation_level", () -> SaturationLevelCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ScoreboardCondition>> SCOREBOARD = REGISTRY.register(
      "scoreboard", () -> ScoreboardCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SetSizeCondition>> SET_SIZE = REGISTRY.register(
      "set_size", () -> SetSizeCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SneakingCondition>> SNEAKING = REGISTRY.register(
      "sneaking", () -> SneakingCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> SPRINTING = REGISTRY.register(
      "sprinting", () -> SimpleConditions.createEntity(Entity::isSprinting)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<StatCondition>> STAT = REGISTRY.register("stat", () -> StatCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SubmergedInCondition>> SUBMERGED_IN = REGISTRY.register(
      "submerged_in", () -> SubmergedInCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> SWIMMING = REGISTRY.register(
      "swimming", () -> SimpleConditions.createEntity(Entity::isSwimming)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> TAMED = REGISTRY.register(
      "tamed", () -> SimpleConditions.createEntity(entity -> entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null)
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<TimeOfDayCondition>> TIME_OF_DAY = REGISTRY.register(
      "time_of_day", () -> TimeOfDayCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<? extends EntityCondition>> THUNDERING = REGISTRY.register(
      "thundering", () -> SimpleConditions.createEntity(entity -> entity.level().isThundering())
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<UsingEffectiveToolCondition>> USING_EFFECTIVE_TOOL = REGISTRY.register(
      "using_effective_tool", () -> UsingEffectiveToolCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<UsingItemCondition>> USING_ITEM = REGISTRY.register(
      "using_item", () -> UsingItemCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<XPLevelsCondition>> XP_LEVELS = REGISTRY.register(
      "xp_levels", () -> XPLevelsCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<XPPointsCondition>> XP_POINTS = REGISTRY.register(
      "xp_points", () -> XPPointsCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
