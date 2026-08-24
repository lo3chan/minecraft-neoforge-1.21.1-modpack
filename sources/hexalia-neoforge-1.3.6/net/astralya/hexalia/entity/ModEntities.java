package net.astralya.hexalia.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.entity.boat.ModChestBoatEntity;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.RabbageProjectile;
import net.astralya.hexalia.entity.custom.projectile.SearingSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.ThornArrowEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public final class ModEntities {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create("hexalia", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<SilkMothEntity>> SILK_MOTH = ENTITY_TYPES.register(
      "silk_moth", () -> Builder.of(SilkMothEntity::new, MobCategory.CREATURE).sized(0.6F, 0.45F).build("silk_moth")
   );
   public static final RegistrySupplier<EntityType<CacofeyEntity>> CACOFEY = ENTITY_TYPES.register(
      "cacofey", () -> Builder.of(CacofeyEntity::new, MobCategory.CREATURE).sized(0.6F, 0.8F).build("cacofey")
   );
   public static final RegistrySupplier<EntityType<ModBoatEntity>> MOD_BOAT = ENTITY_TYPES.register(
      "mod_boat", () -> Builder.of(ModBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).build("mod_boat")
   );
   public static final RegistrySupplier<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT = ENTITY_TYPES.register(
      "mod_chest_boat", () -> Builder.of(ModChestBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).build("mod_chest_boat")
   );
   public static final RegistrySupplier<EntityType<RabbageProjectile>> RABBAGE = ENTITY_TYPES.register(
      "rabbage", () -> Builder.of(RabbageProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("rabbage")
   );
   public static final RegistrySupplier<EntityType<PurifyingSacProjectile>> PURIFYING_SAC = ENTITY_TYPES.register(
      "purifying_sac", () -> Builder.of(PurifyingSacProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("purifying_sac")
   );
   public static final RegistrySupplier<EntityType<FoulSacProjectile>> FOUL_SAC = ENTITY_TYPES.register(
      "foul_sac", () -> Builder.of(FoulSacProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("foul_sac")
   );
   public static final RegistrySupplier<EntityType<FrostSacProjectile>> FROST_SAC = ENTITY_TYPES.register(
      "frost_sac", () -> Builder.of(FrostSacProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("frost_sac")
   );
   public static final RegistrySupplier<EntityType<SearingSacProjectile>> SEARING_SAC = ENTITY_TYPES.register(
      "searing_sac", () -> Builder.of(SearingSacProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("searing_sac")
   );
   public static final RegistrySupplier<EntityType<ThornArrowEntity>> THORN_ARROW = ENTITY_TYPES.register(
      "thorn_arrow", () -> Builder.of(ThornArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("thorn_arrow")
   );

   private ModEntities() {
   }

   public static void init() {
      ENTITY_TYPES.register();
   }
}
