package fuzs.eternalnether.init;

import com.google.common.collect.ImmutableList;
import fuzs.eternalnether.world.entity.animal.horse.WitherSkeletonHorse;
import fuzs.eternalnether.world.entity.monster.Corpor;
import fuzs.eternalnether.world.entity.monster.WarpedEnderMan;
import fuzs.eternalnether.world.entity.monster.Wex;
import fuzs.eternalnether.world.entity.monster.WitherSkeletonKnight;
import fuzs.eternalnether.world.entity.monster.Wraither;
import fuzs.eternalnether.world.entity.monster.piglin.PiglinHunter;
import fuzs.eternalnether.world.entity.monster.piglin.PiglinPrisoner;
import fuzs.eternalnether.world.entity.projectile.ThrownWarpedEnderpearl;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class ModEntityTypes {
   public static final Reference<EntityType<Wex>> WEX = ModRegistry.REGISTRIES
      .registerEntityType(
         "wex",
         () -> Builder.of(Wex::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(0.4F, 0.8F)
            .eyeHeight(0.51875F)
            .passengerAttachments(new float[]{0.7375F})
            .ridingOffset(0.04F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<WarpedEnderMan>> WARPED_ENDERMAN = ModRegistry.REGISTRIES
      .registerEntityType(
         "warped_enderman",
         () -> Builder.of(WarpedEnderMan::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(0.6F, 2.9F)
            .eyeHeight(2.55F)
            .passengerAttachments(new float[]{2.80625F})
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<PiglinPrisoner>> PIGLIN_PRISONER = ModRegistry.REGISTRIES
      .registerEntityType(
         "piglin_prisoner",
         () -> Builder.of(PiglinPrisoner::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .eyeHeight(1.79F)
            .passengerAttachments(new float[]{2.0125F})
            .ridingOffset(-0.7F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<PiglinHunter>> PIGLIN_HUNTER = ModRegistry.REGISTRIES
      .registerEntityType(
         "piglin_hunter",
         () -> Builder.of(PiglinHunter::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .eyeHeight(1.79F)
            .passengerAttachments(new float[]{2.0125F})
            .ridingOffset(-0.7F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<Wraither>> WRAITHER = ModRegistry.REGISTRIES
      .registerEntityType(
         "wraither",
         () -> Builder.of(Wraither::new, MobCategory.MONSTER)
            .fireImmune()
            .immuneTo(new Block[]{Blocks.WITHER_ROSE})
            .sized(0.7F, 2.4F)
            .eyeHeight(2.1F)
            .ridingOffset(-0.875F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<WitherSkeletonKnight>> WITHER_SKELETON_KNIGHT = ModRegistry.REGISTRIES
      .registerEntityType(
         "wither_skeleton_knight",
         () -> Builder.of(WitherSkeletonKnight::new, MobCategory.MONSTER)
            .fireImmune()
            .immuneTo(new Block[]{Blocks.WITHER_ROSE})
            .sized(0.7F, 2.4F)
            .eyeHeight(2.1F)
            .ridingOffset(-0.875F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<Corpor>> CORPOR = ModRegistry.REGISTRIES
      .registerEntityType(
         "corpor",
         () -> Builder.of(Corpor::new, MobCategory.MONSTER)
            .fireImmune()
            .immuneTo(new Block[]{Blocks.WITHER_ROSE})
            .sized(0.7F, 2.4F)
            .eyeHeight(2.1F)
            .ridingOffset(-0.875F)
            .clientTrackingRange(8)
      );
   public static final Reference<EntityType<WitherSkeletonHorse>> WITHER_SKELETON_HORSE = ModRegistry.REGISTRIES
      .registerEntityType(
         "wither_skeleton_horse",
         () -> Builder.of(WitherSkeletonHorse::new, MobCategory.CREATURE)
            .fireImmune()
            .sized(1.3964844F, 1.6F)
            .eyeHeight(1.52F)
            .passengerAttachments(new float[]{1.31875F})
            .clientTrackingRange(10)
      );
   public static final Reference<EntityType<ThrownWarpedEnderpearl>> WARPED_ENDER_PEARL = ModRegistry.REGISTRIES
      .registerEntityType(
         "warped_ender_pearl", () -> Builder.of(ThrownWarpedEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
      );

   public static void boostrap() {
   }

   public static void setPiglinBruteSensorsAndMemories() {
      PiglinBrute.SENSOR_TYPES = ImmutableList.builder()
         .addAll(PiglinBrute.SENSOR_TYPES)
         .add((SensorType)ModSensorTypes.PIGLIN_BRUTE_SPECIFIC_SENSOR_TYPE.value())
         .build();
      PiglinBrute.MEMORY_TYPES = ImmutableList.builder()
         .addAll(PiglinBrute.MEMORY_TYPES)
         .add(new MemoryModuleType[]{MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD})
         .build();
   }
}
