package io.github.razordevs.deep_aether.init;

import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.passive.AetherAnimal;
import io.github.razordevs.deep_aether.entity.DABoatEntity;
import io.github.razordevs.deep_aether.entity.DAChestBoatEntity;
import io.github.razordevs.deep_aether.entity.StormArrow;
import io.github.razordevs.deep_aether.entity.living.AerglowFish;
import io.github.razordevs.deep_aether.entity.living.BabyZephyr;
import io.github.razordevs.deep_aether.entity.living.GentleWind;
import io.github.razordevs.deep_aether.entity.living.Venomite;
import io.github.razordevs.deep_aether.entity.living.Windfly;
import io.github.razordevs.deep_aether.entity.living.boss.eots.EOTSController;
import io.github.razordevs.deep_aether.entity.living.boss.eots.EOTSSegment;
import io.github.razordevs.deep_aether.entity.living.quail.Quail;
import io.github.razordevs.deep_aether.entity.projectile.FireProjectile;
import io.github.razordevs.deep_aether.entity.projectile.ThrownQuailEgg;
import io.github.razordevs.deep_aether.entity.projectile.VenomiteBubble;
import io.github.razordevs.deep_aether.entity.projectile.WindCrystal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "deep_aether"
)
public class DAEntities {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "deep_aether");
   public static final DeferredHolder<EntityType<?>, EntityType<DABoatEntity>> BOAT = ENTITY_TYPES.register(
      "boat", () -> Builder.of(DABoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DAChestBoatEntity>> CHEST_BOAT = ENTITY_TYPES.register(
      "chest_boat", () -> Builder.of(DAChestBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("chest_boat")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThrownQuailEgg>> QUAIL_EGG = ENTITY_TYPES.register(
      "quail_egg", () -> Builder.of(ThrownQuailEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("quail_egg")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AerglowFish>> AERGLOW_FISH = register(
      "aerglow_fish",
      Builder.of(AerglowFish::new, MobCategory.WATER_CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .clientTrackingRange(10)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Quail>> QUAIL = register(
      "quail",
      Builder.of(Quail::new, MobCategory.CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .clientTrackingRange(10)
         .sized(0.35F, 0.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Venomite>> VENOMITE = register("venomite", Venomite::new, 0.7F, 0.6F);
   public static final DeferredHolder<EntityType<?>, EntityType<Windfly>> WINDFLY = register("windfly", Windfly::new, 1.0F, 0.3F);
   public static final DeferredHolder<EntityType<?>, EntityType<EOTSController>> EOTS_CONTROLLER = register("eots_controller", EOTSController::new, 3.0F, 3.0F);
   public static final DeferredHolder<EntityType<?>, EntityType<EOTSSegment>> EOTS_SEGMENT = register("eots_segment", EOTSSegment::new, 1.0F, 1.0F);
   public static final DeferredHolder<EntityType<?>, EntityType<WindCrystal>> WIND_CRYSTAL = ENTITY_TYPES.register(
      "wind_crystal",
      () -> Builder.of(WindCrystal::new, MobCategory.MISC).sized(0.85F, 0.85F).clientTrackingRange(4).updateInterval(10).fireImmune().build("wind_crystal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FireProjectile>> FIRE_PROJECTILE = ENTITY_TYPES.register(
      "fire_projectile",
      () -> Builder.of(FireProjectile::new, MobCategory.MISC)
         .sized(0.35F, 0.35F)
         .clientTrackingRange(4)
         .fireImmune()
         .updateInterval(10)
         .build("fire_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<StormArrow>> STORM_ARROW = ENTITY_TYPES.register(
      "storm_arrow", () -> Builder.of(StormArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("storm_arrow")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<VenomiteBubble>> VENOMITE_BUBBLE = ENTITY_TYPES.register(
      "venomite_bubble",
      () -> Builder.of(VenomiteBubble::new, MobCategory.MISC).sized(0.35F, 0.2F).clientTrackingRange(4).updateInterval(10).build("venomite_bubble")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BabyZephyr>> BABY_ZEPHYR = ENTITY_TYPES.register(
      "baby_zephyr", () -> Builder.of(BabyZephyr::new, AetherMobCategory.AETHER_SKY_MONSTER).sized(1.5F, 1.0F).clientTrackingRange(10).build("baby_zephyr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GentleWind>> GENTLE_WIND = register("gentle_wind", GentleWind::new, 1.0F, 0.3F);

   private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, Builder<T> entityTypeBuilder) {
      return ENTITY_TYPES.register(registryname, () -> entityTypeBuilder.build(registryname));
   }

   private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityFactory<T> entity, float width, float height) {
      return ENTITY_TYPES.register(name, () -> Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
   }

   @SubscribeEvent
   public static void spawnPlacementRegisterEvent(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)AERGLOW_FISH.get(),
         SpawnPlacementTypes.IN_WATER,
         Types.WORLD_SURFACE,
         (entityType, world, reason, pos, random) -> world.getBlockState(pos).getFluidState().isSourceOfType(Fluids.WATER),
         Operation.OR
      );
      event.register(
         (EntityType)QUAIL.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register((EntityType)WINDFLY.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, DAEntities::checkWindFly, Operation.OR);
      event.register(
         (EntityType)VENOMITE.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, serverLevel, spawnType, pos, random) -> serverLevel.getBlockState(pos.above()).is(Blocks.AIR),
         Operation.OR
      );
   }

   public static boolean checkWindFly(EntityType<Windfly> animal, LevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
      return Mob.checkMobSpawnRules(animal, level, spawnReason, pos, random) && EntityUtil.wholeHitboxCanSeeSky(level, pos, 2);
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)AERGLOW_FISH.get(), AerglowFish.createAttributes().build());
      event.put((EntityType)QUAIL.get(), Quail.createAttributes().build());
      event.put((EntityType)VENOMITE.get(), Venomite.createAttributes().build());
      event.put((EntityType)EOTS_SEGMENT.get(), EOTSSegment.createMobAttributes().build());
      event.put((EntityType)EOTS_CONTROLLER.get(), EOTSController.createMobAttributes().build());
      event.put((EntityType)WINDFLY.get(), Windfly.createAttributes().build());
      event.put((EntityType)BABY_ZEPHYR.get(), BabyZephyr.createMobAttributes().build());
      event.put((EntityType)GENTLE_WIND.get(), GentleWind.createMobAttributes().build());
   }
}
