package com.aetherteam.aether.entity;

import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import com.aetherteam.aether.entity.block.TntPresent;
import com.aetherteam.aether.entity.miscellaneous.CloudMinion;
import com.aetherteam.aether.entity.miscellaneous.Parachute;
import com.aetherteam.aether.entity.miscellaneous.SkyrootBoat;
import com.aetherteam.aether.entity.miscellaneous.SkyrootChestBoat;
import com.aetherteam.aether.entity.monster.AbstractWhirlwind;
import com.aetherteam.aether.entity.monster.AechorPlant;
import com.aetherteam.aether.entity.monster.Cockatrice;
import com.aetherteam.aether.entity.monster.EvilWhirlwind;
import com.aetherteam.aether.entity.monster.PassiveWhirlwind;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.entity.monster.Zephyr;
import com.aetherteam.aether.entity.monster.dungeon.FireMinion;
import com.aetherteam.aether.entity.monster.dungeon.Mimic;
import com.aetherteam.aether.entity.monster.dungeon.Sentry;
import com.aetherteam.aether.entity.monster.dungeon.Valkyrie;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.entity.monster.dungeon.boss.SunSpirit;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.entity.passive.Aerbunny;
import com.aetherteam.aether.entity.passive.Aerwhale;
import com.aetherteam.aether.entity.passive.AetherAnimal;
import com.aetherteam.aether.entity.passive.FlyingCow;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.entity.passive.Phyg;
import com.aetherteam.aether.entity.passive.Sheepuff;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.entity.projectile.ZephyrSnowball;
import com.aetherteam.aether.entity.projectile.crystal.CloudCrystal;
import com.aetherteam.aether.entity.projectile.crystal.FireCrystal;
import com.aetherteam.aether.entity.projectile.crystal.IceCrystal;
import com.aetherteam.aether.entity.projectile.crystal.ThunderCrystal;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.aetherteam.aether.entity.projectile.weapon.HammerProjectile;
import com.aetherteam.aether.entity.projectile.weapon.ThrownLightningKnife;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherEntityTypes {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "aether");
   public static final DeferredHolder<EntityType<?>, EntityType<Phyg>> PHYG = ENTITY_TYPES.register(
      "phyg", () -> Builder.of(Phyg::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build("phyg")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FlyingCow>> FLYING_COW = ENTITY_TYPES.register(
      "flying_cow", () -> Builder.of(FlyingCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10).build("flying_cow")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Sheepuff>> SHEEPUFF = ENTITY_TYPES.register(
      "sheepuff", () -> Builder.of(Sheepuff::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10).build("sheepuff")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Moa>> MOA = ENTITY_TYPES.register(
      "moa", () -> Builder.of(Moa::new, MobCategory.CREATURE).sized(0.9F, 2.15F).clientTrackingRange(10).build("moa")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Aerbunny>> AERBUNNY = ENTITY_TYPES.register(
      "aerbunny", () -> Builder.of(Aerbunny::new, MobCategory.CREATURE).sized(0.6F, 0.5F).clientTrackingRange(10).build("aerbunny")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Aerwhale>> AERWHALE = ENTITY_TYPES.register(
      "aerwhale", () -> Builder.of(Aerwhale::new, AetherMobCategory.AETHER_AERWHALE).fireImmune().sized(3.0F, 3.0F).clientTrackingRange(10).build("aerwhale")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Swet>> BLUE_SWET = ENTITY_TYPES.register(
      "blue_swet", () -> Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build("blue_swet")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Swet>> GOLDEN_SWET = ENTITY_TYPES.register(
      "golden_swet", () -> Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build("golden_swet")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PassiveWhirlwind>> WHIRLWIND = ENTITY_TYPES.register(
      "whirlwind",
      () -> Builder.of(PassiveWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER)
         .fireImmune()
         .sized(0.6F, 0.8F)
         .clientTrackingRange(8)
         .build("whirlwind")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EvilWhirlwind>> EVIL_WHIRLWIND = ENTITY_TYPES.register(
      "evil_whirlwind",
      () -> Builder.of(EvilWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER)
         .fireImmune()
         .sized(0.6F, 0.8F)
         .clientTrackingRange(8)
         .build("evil_whirlwind")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AechorPlant>> AECHOR_PLANT = ENTITY_TYPES.register(
      "aechor_plant",
      () -> Builder.of(AechorPlant::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(1.0F, 1.0F).clientTrackingRange(8).build("aechor_plant")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Cockatrice>> COCKATRICE = ENTITY_TYPES.register(
      "cockatrice", () -> Builder.of(Cockatrice::new, AetherMobCategory.AETHER_DARKNESS_MONSTER).sized(0.9F, 2.15F).clientTrackingRange(10).build("cockatrice")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Zephyr>> ZEPHYR = ENTITY_TYPES.register(
      "zephyr", () -> Builder.of(Zephyr::new, AetherMobCategory.AETHER_SKY_MONSTER).sized(4.5F, 3.5F).clientTrackingRange(10).build("zephyr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Mimic>> MIMIC = ENTITY_TYPES.register(
      "mimic", () -> Builder.of(Mimic::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(8).build("mimic")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Sentry>> SENTRY = ENTITY_TYPES.register(
      "sentry", () -> Builder.of(Sentry::new, MobCategory.MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build("sentry")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Slider>> SLIDER = ENTITY_TYPES.register(
      "slider", () -> Builder.of(Slider::new, MobCategory.MONSTER).sized(2.0F, 2.0F).fireImmune().clientTrackingRange(10).build("slider")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Valkyrie>> VALKYRIE = ENTITY_TYPES.register(
      "valkyrie", () -> Builder.of(Valkyrie::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(8).build("valkyrie")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ValkyrieQueen>> VALKYRIE_QUEEN = ENTITY_TYPES.register(
      "valkyrie_queen",
      () -> Builder.of(ValkyrieQueen::new, MobCategory.MONSTER).sized(0.8F, 1.95F).fireImmune().clientTrackingRange(10).build("valkyrie_queen")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FireMinion>> FIRE_MINION = ENTITY_TYPES.register(
      "fire_minion", () -> Builder.of(FireMinion::new, MobCategory.MONSTER).sized(1.1F, 1.95F).fireImmune().clientTrackingRange(8).build("fire_minion")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SunSpirit>> SUN_SPIRIT = ENTITY_TYPES.register(
      "sun_spirit", () -> Builder.of(SunSpirit::new, MobCategory.MONSTER).sized(2.5F, 3.4F).fireImmune().clientTrackingRange(10).build("sun_spirit")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkyrootBoat>> SKYROOT_BOAT = ENTITY_TYPES.register(
      "skyroot_boat", () -> Builder.of(SkyrootBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("skyroot_boat")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkyrootChestBoat>> SKYROOT_CHEST_BOAT = ENTITY_TYPES.register(
      "skyroot_chest_boat", () -> Builder.of(SkyrootChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("skyroot_boat")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CloudMinion>> CLOUD_MINION = ENTITY_TYPES.register(
      "cloud_minion", () -> Builder.of(CloudMinion::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(5).build("cloud_minion")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Parachute>> COLD_PARACHUTE = ENTITY_TYPES.register(
      "cold_parachute", () -> Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build("cold_parachute")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Parachute>> GOLDEN_PARACHUTE = ENTITY_TYPES.register(
      "golden_parachute", () -> Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build("golden_parachute")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FloatingBlockEntity>> FLOATING_BLOCK = ENTITY_TYPES.register(
      "floating_block",
      () -> Builder.of(FloatingBlockEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(20).build("floating_block")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TntPresent>> TNT_PRESENT = ENTITY_TYPES.register(
      "tnt_present",
      () -> Builder.of(TntPresent::new, MobCategory.MISC)
         .fireImmune()
         .sized(1.0F, 1.0F)
         .eyeHeight(0.15F)
         .clientTrackingRange(10)
         .updateInterval(10)
         .build("tnt_present")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZephyrSnowball>> ZEPHYR_SNOWBALL = ENTITY_TYPES.register(
      "zephyr_snowball",
      () -> Builder.of(ZephyrSnowball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10).build("zephyr_snowball")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CloudCrystal>> CLOUD_CRYSTAL = ENTITY_TYPES.register(
      "cloud_crystal", () -> Builder.of(CloudCrystal::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build("cloud_crystal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FireCrystal>> FIRE_CRYSTAL = ENTITY_TYPES.register(
      "fire_crystal",
      () -> Builder.of(FireCrystal::new, MobCategory.MISC).sized(0.85F, 0.85F).clientTrackingRange(4).updateInterval(10).fireImmune().build("fire_crystal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceCrystal>> ICE_CRYSTAL = ENTITY_TYPES.register(
      "ice_crystal",
      () -> Builder.of(IceCrystal::new, MobCategory.MISC).sized(1.2F, 1.2F).clientTrackingRange(4).updateInterval(10).fireImmune().build("ice_crystal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThunderCrystal>> THUNDER_CRYSTAL = ENTITY_TYPES.register(
      "thunder_crystal", () -> Builder.of(ThunderCrystal::new, MobCategory.MISC).sized(0.7F, 0.7F).updateInterval(2).build("thunder_crystal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GoldenDart>> GOLDEN_DART = ENTITY_TYPES.register(
      "golden_dart", () -> Builder.of(GoldenDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("golden_dart")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PoisonDart>> POISON_DART = ENTITY_TYPES.register(
      "poison_dart", () -> Builder.of(PoisonDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("poison_dart")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EnchantedDart>> ENCHANTED_DART = ENTITY_TYPES.register(
      "enchanted_dart",
      () -> Builder.of(EnchantedDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("enchanted_dart")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PoisonNeedle>> POISON_NEEDLE = ENTITY_TYPES.register(
      "poison_needle", () -> Builder.of(PoisonNeedle::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("poison_needle")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThrownLightningKnife>> LIGHTNING_KNIFE = ENTITY_TYPES.register(
      "lightning_knife",
      () -> Builder.of(ThrownLightningKnife::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("lightning_knife")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HammerProjectile>> HAMMER_PROJECTILE = ENTITY_TYPES.register(
      "hammer_projectile",
      () -> Builder.of(HammerProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(4).updateInterval(10).build("hammer_projectile")
   );

   public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)PHYG.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)FLYING_COW.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)SHEEPUFF.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)MOA.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)AERBUNNY.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)AERWHALE.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Aerwhale::checkAerwhaleSpawnRules, Operation.OR
      );
      event.register((EntityType)BLUE_SWET.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules, Operation.OR);
      event.register((EntityType)GOLDEN_SWET.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules, Operation.OR);
      event.register(
         (EntityType)WHIRLWIND.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AbstractWhirlwind::checkWhirlwindSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)EVIL_WHIRLWIND.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         AbstractWhirlwind::checkWhirlwindSpawnRules,
         Operation.OR
      );
      event.register(
         (EntityType)AECHOR_PLANT.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, AechorPlant::checkAechorPlantSpawnRules, Operation.OR
      );
      event.register(
         (EntityType)COCKATRICE.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Cockatrice::checkCockatriceSpawnRules, Operation.OR
      );
      event.register((EntityType)ZEPHYR.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Zephyr::checkZephyrSpawnRules, Operation.OR);
   }

   public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)PHYG.get(), Phyg.createMobAttributes().build());
      event.put((EntityType)FLYING_COW.get(), FlyingCow.createMobAttributes().build());
      event.put((EntityType)SHEEPUFF.get(), Sheepuff.createMobAttributes().build());
      event.put((EntityType)MOA.get(), Moa.createMobAttributes().build());
      event.put((EntityType)AERBUNNY.get(), Aerbunny.createMobAttributes().build());
      event.put((EntityType)AERWHALE.get(), Aerwhale.createMobAttributes().build());
      event.put((EntityType)BLUE_SWET.get(), Swet.createMobAttributes().build());
      event.put((EntityType)GOLDEN_SWET.get(), Swet.createMobAttributes().build());
      event.put((EntityType)WHIRLWIND.get(), AbstractWhirlwind.createMobAttributes().build());
      event.put((EntityType)EVIL_WHIRLWIND.get(), AbstractWhirlwind.createMobAttributes().build());
      event.put((EntityType)AECHOR_PLANT.get(), AechorPlant.createMobAttributes().build());
      event.put((EntityType)COCKATRICE.get(), Cockatrice.createMobAttributes().build());
      event.put((EntityType)ZEPHYR.get(), Zephyr.createMobAttributes().build());
      event.put((EntityType)MIMIC.get(), Mimic.createMobAttributes().build());
      event.put((EntityType)SENTRY.get(), Sentry.createMobAttributes().build());
      event.put((EntityType)SLIDER.get(), Slider.createMobAttributes().build());
      event.put((EntityType)VALKYRIE.get(), Valkyrie.createMobAttributes().build());
      event.put((EntityType)VALKYRIE_QUEEN.get(), ValkyrieQueen.createMobAttributes().build());
      event.put((EntityType)FIRE_MINION.get(), FireMinion.createMobAttributes().build());
      event.put((EntityType)SUN_SPIRIT.get(), SunSpirit.createMobAttributes().build());
      event.put((EntityType)CLOUD_MINION.get(), CloudMinion.createMobAttributes().build());
   }
}
