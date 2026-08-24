package com.github.alexthe666.alexsmobs.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "alexsmobs",
   bus = Bus.MOD
)
public class AMEntityRegistry {
   public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(Registries.ENTITY_TYPE, "alexsmobs");
   public static final Supplier<EntityType<EntityGrizzlyBear>> GRIZZLY_BEAR = DEF_REG.register(
      "grizzly_bear", () -> registerEntity(Builder.of(EntityGrizzlyBear::new, MobCategory.CREATURE).sized(1.6F, 1.8F).setTrackingRange(10), "grizzly_bear")
   );
   public static final Supplier<EntityType<EntityRoadrunner>> ROADRUNNER = DEF_REG.register(
      "roadrunner", () -> registerEntity(Builder.of(EntityRoadrunner::new, MobCategory.CREATURE).sized(0.45F, 0.75F).setTrackingRange(10), "roadrunner")
   );
   public static final Supplier<EntityType<EntityBoneSerpent>> BONE_SERPENT = DEF_REG.register(
      "bone_serpent",
      () -> registerEntity(Builder.of(EntityBoneSerpent::new, MobCategory.MONSTER).sized(1.2F, 1.15F).fireImmune().setTrackingRange(10), "bone_serpent")
   );
   public static final Supplier<EntityType<EntityBoneSerpentPart>> BONE_SERPENT_PART = DEF_REG.register(
      "bone_serpent_part",
      () -> registerEntity(Builder.of(EntityBoneSerpentPart::new, MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().setTrackingRange(10), "bone_serpent_part")
   );
   public static final Supplier<EntityType<EntityGazelle>> GAZELLE = DEF_REG.register(
      "gazelle", () -> registerEntity(Builder.of(EntityGazelle::new, MobCategory.CREATURE).sized(0.85F, 1.25F).setTrackingRange(10), "gazelle")
   );
   public static final Supplier<EntityType<EntityCrocodile>> CROCODILE = DEF_REG.register(
      "crocodile", () -> registerEntity(Builder.of(EntityCrocodile::new, MobCategory.CREATURE).sized(2.15F, 0.75F).setTrackingRange(10), "crocodile")
   );
   public static final Supplier<EntityType<EntityFly>> FLY = DEF_REG.register(
      "fly", () -> registerEntity(Builder.of(EntityFly::new, MobCategory.AMBIENT).sized(0.35F, 0.35F).setTrackingRange(4), "fly")
   );
   public static final Supplier<EntityType<EntityHummingbird>> HUMMINGBIRD = DEF_REG.register(
      "hummingbird", () -> registerEntity(Builder.of(EntityHummingbird::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(5), "hummingbird")
   );
   public static final Supplier<EntityType<EntityOrca>> ORCA = DEF_REG.register(
      "orca", () -> registerEntity(Builder.of(EntityOrca::new, MobCategory.WATER_CREATURE).sized(3.75F, 1.75F).setTrackingRange(10), "orca")
   );
   public static final Supplier<EntityType<EntitySunbird>> SUNBIRD = DEF_REG.register(
      "sunbird",
      () -> registerEntity(
         Builder.of(EntitySunbird::new, MobCategory.CREATURE)
            .sized(2.75F, 1.5F)
            .fireImmune()
            .setTrackingRange(12)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "sunbird"
      )
   );
   public static final Supplier<EntityType<EntityGorilla>> GORILLA = DEF_REG.register(
      "gorilla", () -> registerEntity(Builder.of(EntityGorilla::new, MobCategory.CREATURE).sized(1.15F, 1.35F).setTrackingRange(10), "gorilla")
   );
   public static final Supplier<EntityType<EntityCrimsonMosquito>> CRIMSON_MOSQUITO = DEF_REG.register(
      "crimson_mosquito",
      () -> registerEntity(Builder.of(EntityCrimsonMosquito::new, MobCategory.MONSTER).sized(1.25F, 1.15F).fireImmune().setTrackingRange(8), "crimson_mosquito")
   );
   public static final Supplier<EntityType<EntityMosquitoSpit>> MOSQUITO_SPIT = DEF_REG.register(
      "mosquito_spit", () -> registerEntity(Builder.of(EntityMosquitoSpit::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "mosquito_spit")
   );
   public static final Supplier<EntityType<EntityRattlesnake>> RATTLESNAKE = DEF_REG.register(
      "rattlesnake", () -> registerEntity(Builder.of(EntityRattlesnake::new, MobCategory.CREATURE).sized(0.95F, 0.35F).setTrackingRange(10), "rattlesnake")
   );
   public static final Supplier<EntityType<EntityEndergrade>> ENDERGRADE = DEF_REG.register(
      "endergrade", () -> registerEntity(Builder.of(EntityEndergrade::new, MobCategory.CREATURE).sized(0.95F, 0.85F).setTrackingRange(10), "endergrade")
   );
   public static final Supplier<EntityType<EntityHammerheadShark>> HAMMERHEAD_SHARK = DEF_REG.register(
      "hammerhead_shark",
      () -> registerEntity(Builder.of(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.25F).setTrackingRange(10), "hammerhead_shark")
   );
   public static final Supplier<EntityType<EntitySharkToothArrow>> SHARK_TOOTH_ARROW = DEF_REG.register(
      "shark_tooth_arrow", () -> registerEntity(Builder.of(EntitySharkToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F), "shark_tooth_arrow")
   );
   public static final Supplier<EntityType<EntityLobster>> LOBSTER = DEF_REG.register(
      "lobster", () -> registerEntity(Builder.of(EntityLobster::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).setTrackingRange(5), "lobster")
   );
   public static final Supplier<EntityType<EntityKomodoDragon>> KOMODO_DRAGON = DEF_REG.register(
      "komodo_dragon", () -> registerEntity(Builder.of(EntityKomodoDragon::new, MobCategory.CREATURE).sized(1.9F, 0.9F).setTrackingRange(10), "komodo_dragon")
   );
   public static final Supplier<EntityType<EntityCapuchinMonkey>> CAPUCHIN_MONKEY = DEF_REG.register(
      "capuchin_monkey",
      () -> registerEntity(Builder.of(EntityCapuchinMonkey::new, MobCategory.CREATURE).sized(0.65F, 0.75F).setTrackingRange(10), "capuchin_monkey")
   );
   public static final Supplier<EntityType<EntityTossedItem>> TOSSED_ITEM = DEF_REG.register(
      "tossed_item", () -> registerEntity(Builder.of(EntityTossedItem::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "tossed_item")
   );
   public static final Supplier<EntityType<EntityCentipedeHead>> CENTIPEDE_HEAD = DEF_REG.register(
      "centipede_head", () -> registerEntity(Builder.of(EntityCentipedeHead::new, MobCategory.MONSTER).sized(0.9F, 0.9F).setTrackingRange(8), "centipede_head")
   );
   public static final Supplier<EntityType<EntityCentipedeBody>> CENTIPEDE_BODY = DEF_REG.register(
      "centipede_body",
      () -> registerEntity(
         Builder.of(EntityCentipedeBody::new, MobCategory.MISC)
            .sized(0.9F, 0.9F)
            .fireImmune()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "centipede_body"
      )
   );
   public static final Supplier<EntityType<EntityCentipedeTail>> CENTIPEDE_TAIL = DEF_REG.register(
      "centipede_tail",
      () -> registerEntity(
         Builder.of(EntityCentipedeTail::new, MobCategory.MISC)
            .sized(0.9F, 0.9F)
            .fireImmune()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "centipede_tail"
      )
   );
   public static final Supplier<EntityType<EntityWarpedToad>> WARPED_TOAD = DEF_REG.register(
      "warped_toad",
      () -> registerEntity(
         Builder.of(EntityWarpedToad::new, MobCategory.CREATURE)
            .sized(0.9F, 1.4F)
            .fireImmune()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "warped_toad"
      )
   );
   public static final Supplier<EntityType<EntityMoose>> MOOSE = DEF_REG.register(
      "moose", () -> registerEntity(Builder.of(EntityMoose::new, MobCategory.CREATURE).sized(1.7F, 2.4F).setTrackingRange(10), "moose")
   );
   public static final Supplier<EntityType<EntityMimicube>> MIMICUBE = DEF_REG.register(
      "mimicube", () -> registerEntity(Builder.of(EntityMimicube::new, MobCategory.MONSTER).sized(0.9F, 0.9F).setTrackingRange(8), "mimicube")
   );
   public static final Supplier<EntityType<EntityRaccoon>> RACCOON = DEF_REG.register(
      "raccoon", () -> registerEntity(Builder.of(EntityRaccoon::new, MobCategory.CREATURE).sized(0.8F, 0.9F).setTrackingRange(10), "raccoon")
   );
   public static final Supplier<EntityType<EntityBlobfish>> BLOBFISH = DEF_REG.register(
      "blobfish", () -> registerEntity(Builder.of(EntityBlobfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F).setTrackingRange(5), "blobfish")
   );
   public static final Supplier<EntityType<EntitySeal>> SEAL = DEF_REG.register(
      "seal", () -> registerEntity(Builder.of(EntitySeal::new, MobCategory.CREATURE).sized(1.45F, 0.9F).setTrackingRange(10), "seal")
   );
   public static final Supplier<EntityType<EntityCockroach>> COCKROACH = DEF_REG.register(
      "cockroach", () -> registerEntity(Builder.of(EntityCockroach::new, MobCategory.AMBIENT).sized(0.7F, 0.3F).setTrackingRange(5), "cockroach")
   );
   public static final Supplier<EntityType<EntityCockroachEgg>> COCKROACH_EGG = DEF_REG.register(
      "cockroach_egg", () -> registerEntity(Builder.of(EntityCockroachEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "cockroach_egg")
   );
   public static final Supplier<EntityType<EntityShoebill>> SHOEBILL = DEF_REG.register(
      "shoebill",
      () -> registerEntity(Builder.of(EntityShoebill::new, MobCategory.CREATURE).sized(0.8F, 1.5F).setUpdateInterval(1).setTrackingRange(10), "shoebill")
   );
   public static final Supplier<EntityType<EntityElephant>> ELEPHANT = DEF_REG.register(
      "elephant",
      () -> registerEntity(Builder.of(EntityElephant::new, MobCategory.CREATURE).sized(3.1F, 3.5F).setUpdateInterval(1).setTrackingRange(10), "elephant")
   );
   public static final Supplier<EntityType<EntitySoulVulture>> SOUL_VULTURE = DEF_REG.register(
      "soul_vulture",
      () -> registerEntity(
         Builder.of(EntitySoulVulture::new, MobCategory.MONSTER).sized(0.9F, 1.3F).setUpdateInterval(1).fireImmune().setTrackingRange(8), "soul_vulture"
      )
   );
   public static final Supplier<EntityType<EntitySnowLeopard>> SNOW_LEOPARD = DEF_REG.register(
      "snow_leopard",
      () -> registerEntity(
         Builder.of(EntitySnowLeopard::new, MobCategory.CREATURE).sized(1.2F, 1.3F).immuneTo(new Block[]{Blocks.POWDER_SNOW}).setTrackingRange(10),
         "snow_leopard"
      )
   );
   public static final Supplier<EntityType<EntitySpectre>> SPECTRE = DEF_REG.register(
      "spectre",
      () -> registerEntity(
         Builder.of(EntitySpectre::new, MobCategory.CREATURE)
            .sized(3.15F, 0.8F)
            .fireImmune()
            .setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "spectre"
      )
   );
   public static final Supplier<EntityType<EntityCrow>> CROW = DEF_REG.register(
      "crow", () -> registerEntity(Builder.of(EntityCrow::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "crow")
   );
   public static final Supplier<EntityType<EntityAlligatorSnappingTurtle>> ALLIGATOR_SNAPPING_TURTLE = DEF_REG.register(
      "alligator_snapping_turtle",
      () -> registerEntity(
         Builder.of(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).sized(1.25F, 0.65F).setTrackingRange(10), "alligator_snapping_turtle"
      )
   );
   public static final Supplier<EntityType<EntityMungus>> MUNGUS = DEF_REG.register(
      "mungus", () -> registerEntity(Builder.of(EntityMungus::new, MobCategory.CREATURE).sized(0.75F, 1.45F).setTrackingRange(10), "mungus")
   );
   public static final Supplier<EntityType<EntityMantisShrimp>> MANTIS_SHRIMP = DEF_REG.register(
      "mantis_shrimp",
      () -> registerEntity(Builder.of(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).sized(1.25F, 1.2F).setTrackingRange(10), "mantis_shrimp")
   );
   public static final Supplier<EntityType<EntityGuster>> GUSTER = DEF_REG.register(
      "guster", () -> registerEntity(Builder.of(EntityGuster::new, MobCategory.MONSTER).sized(1.42F, 2.35F).fireImmune().setTrackingRange(8), "guster")
   );
   public static final Supplier<EntityType<EntitySandShot>> SAND_SHOT = DEF_REG.register(
      "sand_shot", () -> registerEntity(Builder.of(EntitySandShot::new, MobCategory.MISC).sized(0.95F, 0.65F).fireImmune(), "sand_shot")
   );
   public static final Supplier<EntityType<EntityGust>> GUST = DEF_REG.register(
      "gust", () -> registerEntity(Builder.of(EntityGust::new, MobCategory.MISC).sized(0.8F, 0.8F).fireImmune(), "gust")
   );
   public static final Supplier<EntityType<EntityWarpedMosco>> WARPED_MOSCO = DEF_REG.register(
      "warped_mosco",
      () -> registerEntity(Builder.of(EntityWarpedMosco::new, MobCategory.MONSTER).sized(1.99F, 3.25F).fireImmune().setTrackingRange(10), "warped_mosco")
   );
   public static final Supplier<EntityType<EntityHemolymph>> HEMOLYMPH = DEF_REG.register(
      "hemolymph", () -> registerEntity(Builder.of(EntityHemolymph::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "hemolymph")
   );
   public static final Supplier<EntityType<EntityStraddler>> STRADDLER = DEF_REG.register(
      "straddler", () -> registerEntity(Builder.of(EntityStraddler::new, MobCategory.MONSTER).sized(1.65F, 3.0F).fireImmune().setTrackingRange(8), "straddler")
   );
   public static final Supplier<EntityType<EntityStradpole>> STRADPOLE = DEF_REG.register(
      "stradpole",
      () -> registerEntity(Builder.of(EntityStradpole::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).fireImmune().setTrackingRange(4), "stradpole")
   );
   public static final Supplier<EntityType<EntityStraddleboard>> STRADDLEBOARD = DEF_REG.register(
      "straddleboard",
      () -> registerEntity(
         Builder.of(EntityStraddleboard::new, MobCategory.MISC)
            .sized(1.5F, 0.35F)
            .fireImmune()
            .setUpdateInterval(1)
            .clientTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true),
         "straddleboard"
      )
   );
   public static final Supplier<EntityType<EntityEmu>> EMU = DEF_REG.register(
      "emu", () -> registerEntity(Builder.of(EntityEmu::new, MobCategory.CREATURE).sized(1.1F, 1.8F).setTrackingRange(10), "emu")
   );
   public static final Supplier<EntityType<EntityEmuEgg>> EMU_EGG = DEF_REG.register(
      "emu_egg", () -> registerEntity(Builder.of(EntityEmuEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "emu_egg")
   );
   public static final Supplier<EntityType<EntityPlatypus>> PLATYPUS = DEF_REG.register(
      "platypus", () -> registerEntity(Builder.of(EntityPlatypus::new, MobCategory.CREATURE).sized(0.8F, 0.5F).setTrackingRange(10), "platypus")
   );
   public static final Supplier<EntityType<EntityDropBear>> DROPBEAR = DEF_REG.register(
      "dropbear", () -> registerEntity(Builder.of(EntityDropBear::new, MobCategory.MONSTER).sized(1.65F, 1.5F).fireImmune().setTrackingRange(8), "dropbear")
   );
   public static final Supplier<EntityType<EntityTasmanianDevil>> TASMANIAN_DEVIL = DEF_REG.register(
      "tasmanian_devil",
      () -> registerEntity(Builder.of(EntityTasmanianDevil::new, MobCategory.CREATURE).sized(0.7F, 0.8F).setTrackingRange(10), "tasmanian_devil")
   );
   public static final Supplier<EntityType<EntityKangaroo>> KANGAROO = DEF_REG.register(
      "kangaroo", () -> registerEntity(Builder.of(EntityKangaroo::new, MobCategory.CREATURE).sized(1.65F, 1.5F).setTrackingRange(10), "kangaroo")
   );
   public static final Supplier<EntityType<EntityCachalotWhale>> CACHALOT_WHALE = DEF_REG.register(
      "cachalot_whale",
      () -> registerEntity(Builder.of(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).sized(9.0F, 4.0F).setTrackingRange(10), "cachalot_whale")
   );
   public static final Supplier<EntityType<EntityCachalotEcho>> CACHALOT_ECHO = DEF_REG.register(
      "cachalot_echo", () -> registerEntity(Builder.of(EntityCachalotEcho::new, MobCategory.MISC).sized(2.0F, 2.0F).fireImmune(), "cachalot_echo")
   );
   public static final Supplier<EntityType<EntityLeafcutterAnt>> LEAFCUTTER_ANT = DEF_REG.register(
      "leafcutter_ant",
      () -> registerEntity(Builder.of(EntityLeafcutterAnt::new, MobCategory.CREATURE).sized(0.8F, 0.5F).setTrackingRange(5), "leafcutter_ant")
   );
   public static final Supplier<EntityType<EntityEnderiophage>> ENDERIOPHAGE = DEF_REG.register(
      "enderiophage",
      () -> registerEntity(
         Builder.of(EntityEnderiophage::new, MobCategory.CREATURE).sized(0.85F, 1.95F).setUpdateInterval(1).setTrackingRange(8), "enderiophage"
      )
   );
   public static final Supplier<EntityType<EntityEnderiophageRocket>> ENDERIOPHAGE_ROCKET = DEF_REG.register(
      "enderiophage_rocket",
      () -> registerEntity(Builder.of(EntityEnderiophageRocket::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "enderiophage_rocket")
   );
   public static final Supplier<EntityType<EntityBaldEagle>> BALD_EAGLE = DEF_REG.register(
      "bald_eagle",
      () -> registerEntity(Builder.of(EntityBaldEagle::new, MobCategory.CREATURE).sized(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle")
   );
   public static final Supplier<EntityType<EntityTiger>> TIGER = DEF_REG.register(
      "tiger", () -> registerEntity(Builder.of(EntityTiger::new, MobCategory.CREATURE).sized(1.45F, 1.2F).setTrackingRange(10), "tiger")
   );
   public static final Supplier<EntityType<EntityTarantulaHawk>> TARANTULA_HAWK = DEF_REG.register(
      "tarantula_hawk",
      () -> registerEntity(Builder.of(EntityTarantulaHawk::new, MobCategory.CREATURE).sized(1.2F, 0.9F).setTrackingRange(10), "tarantula_hawk")
   );
   public static final Supplier<EntityType<EntityVoidWorm>> VOID_WORM = DEF_REG.register(
      "void_worm",
      () -> registerEntity(
         Builder.of(EntityVoidWorm::new, MobCategory.MONSTER)
            .sized(3.4F, 3.0F)
            .fireImmune()
            .setTrackingRange(20)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "void_worm"
      )
   );
   public static final Supplier<EntityType<EntityVoidWormPart>> VOID_WORM_PART = DEF_REG.register(
      "void_worm_part",
      () -> registerEntity(
         Builder.of(EntityVoidWormPart::new, MobCategory.MONSTER)
            .sized(1.2F, 1.35F)
            .fireImmune()
            .setTrackingRange(20)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "void_worm_part"
      )
   );
   public static final Supplier<EntityType<EntityVoidWormShot>> VOID_WORM_SHOT = DEF_REG.register(
      "void_worm_shot", () -> registerEntity(Builder.of(EntityVoidWormShot::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "void_worm_shot")
   );
   public static final Supplier<EntityType<EntityVoidPortal>> VOID_PORTAL = DEF_REG.register(
      "void_portal", () -> registerEntity(Builder.of(EntityVoidPortal::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "void_portal")
   );
   public static final Supplier<EntityType<EntityFrilledShark>> FRILLED_SHARK = DEF_REG.register(
      "frilled_shark",
      () -> registerEntity(Builder.of(EntityFrilledShark::new, MobCategory.WATER_CREATURE).sized(1.3F, 0.4F).setTrackingRange(8), "frilled_shark")
   );
   public static final Supplier<EntityType<EntityMimicOctopus>> MIMIC_OCTOPUS = DEF_REG.register(
      "mimic_octopus",
      () -> registerEntity(Builder.of(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).setTrackingRange(8), "mimic_octopus")
   );
   public static final Supplier<EntityType<EntitySeagull>> SEAGULL = DEF_REG.register(
      "seagull", () -> registerEntity(Builder.of(EntitySeagull::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "seagull")
   );
   public static final Supplier<EntityType<EntityFroststalker>> FROSTSTALKER = DEF_REG.register(
      "froststalker",
      () -> registerEntity(
         Builder.of(EntityFroststalker::new, MobCategory.CREATURE).sized(0.95F, 1.15F).immuneTo(new Block[]{Blocks.POWDER_SNOW}), "froststalker"
      )
   );
   public static final Supplier<EntityType<EntityIceShard>> ICE_SHARD = DEF_REG.register(
      "ice_shard", () -> registerEntity(Builder.of(EntityIceShard::new, MobCategory.MISC).sized(0.45F, 0.45F).fireImmune(), "ice_shard")
   );
   public static final Supplier<EntityType<EntityTusklin>> TUSKLIN = DEF_REG.register(
      "tusklin",
      () -> registerEntity(
         Builder.of(EntityTusklin::new, MobCategory.CREATURE).sized(2.2F, 1.9F).immuneTo(new Block[]{Blocks.POWDER_SNOW}).setTrackingRange(10), "tusklin"
      )
   );
   public static final Supplier<EntityType<EntityLaviathan>> LAVIATHAN = DEF_REG.register(
      "laviathan",
      () -> registerEntity(
         Builder.of(EntityLaviathan::new, MobCategory.CREATURE)
            .sized(3.3F, 2.4F)
            .fireImmune()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "laviathan"
      )
   );
   public static final Supplier<EntityType<EntityCosmaw>> COSMAW = DEF_REG.register(
      "cosmaw", () -> registerEntity(Builder.of(EntityCosmaw::new, MobCategory.CREATURE).sized(1.95F, 1.8F).setTrackingRange(10), "cosmaw")
   );
   public static final Supplier<EntityType<EntityToucan>> TOUCAN = DEF_REG.register(
      "toucan", () -> registerEntity(Builder.of(EntityToucan::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "toucan")
   );
   public static final Supplier<EntityType<EntityManedWolf>> MANED_WOLF = DEF_REG.register(
      "maned_wolf", () -> registerEntity(Builder.of(EntityManedWolf::new, MobCategory.CREATURE).sized(0.9F, 1.26F).setTrackingRange(10), "maned_wolf")
   );
   public static final Supplier<EntityType<EntityAnaconda>> ANACONDA = DEF_REG.register(
      "anaconda", () -> registerEntity(Builder.of(EntityAnaconda::new, MobCategory.CREATURE).sized(0.8F, 0.8F).setTrackingRange(10), "anaconda")
   );
   public static final Supplier<EntityType<EntityAnacondaPart>> ANACONDA_PART = DEF_REG.register(
      "anaconda_part",
      () -> registerEntity(
         Builder.of(EntityAnacondaPart::new, MobCategory.MISC)
            .sized(0.8F, 0.8F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "anaconda_part"
      )
   );
   public static final Supplier<EntityType<EntityVineLasso>> VINE_LASSO = DEF_REG.register(
      "vine_lasso", () -> registerEntity(Builder.of(EntityVineLasso::new, MobCategory.MISC).sized(0.85F, 0.2F).fireImmune(), "vine_lasso")
   );
   public static final Supplier<EntityType<EntityAnteater>> ANTEATER = DEF_REG.register(
      "anteater", () -> registerEntity(Builder.of(EntityAnteater::new, MobCategory.CREATURE).sized(1.3F, 1.1F).setTrackingRange(10), "anteater")
   );
   public static final Supplier<EntityType<EntityRockyRoller>> ROCKY_ROLLER = DEF_REG.register(
      "rocky_roller", () -> registerEntity(Builder.of(EntityRockyRoller::new, MobCategory.MONSTER).sized(1.2F, 1.45F).setTrackingRange(8), "rocky_roller")
   );
   public static final Supplier<EntityType<EntityFlutter>> FLUTTER = DEF_REG.register(
      "flutter", () -> registerEntity(Builder.of(EntityFlutter::new, MobCategory.AMBIENT).sized(0.5F, 0.7F).setTrackingRange(6), "flutter")
   );
   public static final Supplier<EntityType<EntityPollenBall>> POLLEN_BALL = DEF_REG.register(
      "pollen_ball", () -> registerEntity(Builder.of(EntityPollenBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune(), "pollen_ball")
   );
   public static final Supplier<EntityType<EntityGeladaMonkey>> GELADA_MONKEY = DEF_REG.register(
      "gelada_monkey", () -> registerEntity(Builder.of(EntityGeladaMonkey::new, MobCategory.CREATURE).sized(1.2F, 1.2F).setTrackingRange(10), "gelada_monkey")
   );
   public static final Supplier<EntityType<EntityJerboa>> JERBOA = DEF_REG.register(
      "jerboa", () -> registerEntity(Builder.of(EntityJerboa::new, MobCategory.AMBIENT).sized(0.5F, 0.5F).setTrackingRange(5), "jerboa")
   );
   public static final Supplier<EntityType<EntityTerrapin>> TERRAPIN = DEF_REG.register(
      "terrapin", () -> registerEntity(Builder.of(EntityTerrapin::new, MobCategory.WATER_AMBIENT).sized(0.75F, 0.45F).setTrackingRange(5), "terrapin")
   );
   public static final Supplier<EntityType<EntityCombJelly>> COMB_JELLY = DEF_REG.register(
      "comb_jelly", () -> registerEntity(Builder.of(EntityCombJelly::new, MobCategory.WATER_AMBIENT).sized(0.65F, 0.8F).setTrackingRange(5), "comb_jelly")
   );
   public static final Supplier<EntityType<EntityCosmicCod>> COSMIC_COD = DEF_REG.register(
      "cosmic_cod", () -> registerEntity(Builder.of(EntityCosmicCod::new, MobCategory.AMBIENT).sized(0.85F, 0.4F).setTrackingRange(5), "cosmic_cod")
   );
   public static final Supplier<EntityType<EntityBunfungus>> BUNFUNGUS = DEF_REG.register(
      "bunfungus", () -> registerEntity(Builder.of(EntityBunfungus::new, MobCategory.CREATURE).sized(1.85F, 2.1F).setTrackingRange(10), "bunfungus")
   );
   public static final Supplier<EntityType<EntityBison>> BISON = DEF_REG.register(
      "bison", () -> registerEntity(Builder.of(EntityBison::new, MobCategory.CREATURE).sized(2.4F, 2.1F).setTrackingRange(10), "bison")
   );
   public static final Supplier<EntityType<EntityGiantSquid>> GIANT_SQUID = DEF_REG.register(
      "giant_squid", () -> registerEntity(Builder.of(EntityGiantSquid::new, MobCategory.WATER_CREATURE).sized(0.9F, 1.2F).setTrackingRange(10), "giant_squid")
   );
   public static final Supplier<EntityType<EntitySquidGrapple>> SQUID_GRAPPLE = DEF_REG.register(
      "squid_grapple", () -> registerEntity(Builder.of(EntitySquidGrapple::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "squid_grapple")
   );
   public static final Supplier<EntityType<EntitySeaBear>> SEA_BEAR = DEF_REG.register(
      "sea_bear", () -> registerEntity(Builder.of(EntitySeaBear::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.99F).setTrackingRange(10), "sea_bear")
   );
   public static final Supplier<EntityType<EntityDevilsHolePupfish>> DEVILS_HOLE_PUPFISH = DEF_REG.register(
      "devils_hole_pupfish",
      () -> registerEntity(Builder.of(EntityDevilsHolePupfish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).setTrackingRange(4), "devils_hole_pupfish")
   );
   public static final Supplier<EntityType<EntityCatfish>> CATFISH = DEF_REG.register(
      "catfish", () -> registerEntity(Builder.of(EntityCatfish::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F).setTrackingRange(5), "catfish")
   );
   public static final Supplier<EntityType<EntityFlyingFish>> FLYING_FISH = DEF_REG.register(
      "flying_fish", () -> registerEntity(Builder.of(EntityFlyingFish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).setTrackingRange(5), "flying_fish")
   );
   public static final Supplier<EntityType<EntitySkelewag>> SKELEWAG = DEF_REG.register(
      "skelewag",
      () -> registerEntity(
         Builder.of(EntitySkelewag::new, MobCategory.MONSTER).sized(2.0F, 1.2F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(8),
         "skelewag"
      )
   );
   public static final Supplier<EntityType<EntityRainFrog>> RAIN_FROG = DEF_REG.register(
      "rain_frog", () -> registerEntity(Builder.of(EntityRainFrog::new, MobCategory.AMBIENT).sized(0.55F, 0.5F).setTrackingRange(5), "rain_frog")
   );
   public static final Supplier<EntityType<EntityPotoo>> POTOO = DEF_REG.register(
      "potoo", () -> registerEntity(Builder.of(EntityPotoo::new, MobCategory.CREATURE).sized(0.6F, 0.8F).setTrackingRange(10), "potoo")
   );
   public static final Supplier<EntityType<EntityMudskipper>> MUDSKIPPER = DEF_REG.register(
      "mudskipper", () -> registerEntity(Builder.of(EntityMudskipper::new, MobCategory.CREATURE).sized(0.7F, 0.44F).setTrackingRange(10), "mudskipper")
   );
   public static final Supplier<EntityType<EntityMudBall>> MUD_BALL = DEF_REG.register(
      "mud_ball", () -> registerEntity(Builder.of(EntityMudBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune(), "mud_ball")
   );
   public static final Supplier<EntityType<EntityRhinoceros>> RHINOCEROS = DEF_REG.register(
      "rhinoceros", () -> registerEntity(Builder.of(EntityRhinoceros::new, MobCategory.CREATURE).sized(2.3F, 2.4F).setTrackingRange(10), "rhinoceros")
   );
   public static final Supplier<EntityType<EntitySugarGlider>> SUGAR_GLIDER = DEF_REG.register(
      "sugar_glider", () -> registerEntity(Builder.of(EntitySugarGlider::new, MobCategory.CREATURE).sized(0.8F, 0.45F).setTrackingRange(10), "sugar_glider")
   );
   public static final Supplier<EntityType<EntityFarseer>> FARSEER = DEF_REG.register(
      "farseer",
      () -> registerEntity(
         Builder.of(EntityFarseer::new, MobCategory.MONSTER)
            .sized(0.99F, 1.5F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .fireImmune()
            .setTrackingRange(8),
         "farseer"
      )
   );
   public static final Supplier<EntityType<EntitySkreecher>> SKREECHER = DEF_REG.register(
      "skreecher",
      () -> registerEntity(
         Builder.of(EntitySkreecher::new, MobCategory.CREATURE)
            .sized(0.99F, 0.95F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "skreecher"
      )
   );
   public static final Supplier<EntityType<EntityUnderminer>> UNDERMINER = DEF_REG.register(
      "underminer", () -> registerEntity(Builder.of(EntityUnderminer::new, MobCategory.AMBIENT).sized(0.8F, 1.8F).setTrackingRange(8), "underminer")
   );
   public static final Supplier<EntityType<EntityMurmur>> MURMUR = DEF_REG.register(
      "murmur", () -> registerEntity(Builder.of(EntityMurmur::new, MobCategory.MONSTER).sized(0.7F, 1.45F).setTrackingRange(8), "murmur")
   );
   public static final Supplier<EntityType<EntityMurmurHead>> MURMUR_HEAD = DEF_REG.register(
      "murmur_head", () -> registerEntity(Builder.of(EntityMurmurHead::new, MobCategory.MONSTER).sized(0.55F, 0.55F).setTrackingRange(8), "murmur_head")
   );
   public static final Supplier<EntityType<EntityTendonSegment>> TENDON_SEGMENT = DEF_REG.register(
      "tendon_segment", () -> registerEntity(Builder.of(EntityTendonSegment::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune(), "tendon_segment")
   );
   public static final Supplier<EntityType<EntitySkunk>> SKUNK = DEF_REG.register(
      "skunk", () -> registerEntity(Builder.of(EntitySkunk::new, MobCategory.CREATURE).sized(0.85F, 0.65F).setTrackingRange(10), "skunk")
   );
   public static final Supplier<EntityType<EntityFart>> FART = DEF_REG.register(
      "fart", () -> registerEntity(Builder.of(EntityFart::new, MobCategory.MISC).sized(0.7F, 0.3F).fireImmune(), "fart")
   );
   public static final Supplier<EntityType<EntityBananaSlug>> BANANA_SLUG = DEF_REG.register(
      "banana_slug", () -> registerEntity(Builder.of(EntityBananaSlug::new, MobCategory.CREATURE).sized(0.8F, 0.4F).setTrackingRange(10), "banana_slug")
   );
   public static final Supplier<EntityType<EntityBlueJay>> BLUE_JAY = DEF_REG.register(
      "blue_jay", () -> registerEntity(Builder.of(EntityBlueJay::new, MobCategory.CREATURE).sized(0.5F, 0.6F).setTrackingRange(10), "blue_jay")
   );
   public static final Supplier<EntityType<EntityCaiman>> CAIMAN = DEF_REG.register(
      "caiman", () -> registerEntity(Builder.of(EntityCaiman::new, MobCategory.CREATURE).sized(1.3F, 0.6F).setTrackingRange(10), "caiman")
   );
   public static final Supplier<EntityType<EntityTriops>> TRIOPS = DEF_REG.register(
      "triops", () -> registerEntity(Builder.of(EntityTriops::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.25F).setTrackingRange(5), "triops")
   );
   public static final SpawnPlacementType PLACE_ON_GROUND = SpawnPlacementTypes.ON_GROUND;
   public static final SpawnPlacementType PLACE_IN_WATER = SpawnPlacementTypes.IN_WATER;
   public static final SpawnPlacementType PLACE_IN_LAVA = SpawnPlacementTypes.IN_LAVA;
   public static final SpawnPlacementType PLACE_NO_RESTRICTIONS = SpawnPlacementTypes.NO_RESTRICTIONS;
   public static final SpawnPlacementType PLACE_ON_LEAVES = AMEntityRegistry::createLeavesSpawnPlacement;
   private static RegisterSpawnPlacementsEvent spawnPlacementEvent;

   private static EntityType registerEntity(Builder builder, String entityName) {
      return builder.build(entityName);
   }

   @SubscribeEvent
   public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      spawnPlacementEvent = event;
      registerSpawnPlacements();
      spawnPlacementEvent = null;
   }

   private static <T extends Mob> void placement(EntityType<T> type, SpawnPlacementType placement, Types heightmap, SpawnPredicate<T> predicate) {
      spawnPlacementEvent.register(type, placement, heightmap, predicate, Operation.REPLACE);
   }

   public static void registerSpawnPlacements() {
      placement(GRIZZLY_BEAR.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(ROADRUNNER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
      placement(BONE_SERPENT.get(), PLACE_IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
      placement(GAZELLE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(CROCODILE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
      placement(FLY.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
      placement(HUMMINGBIRD.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
      placement(ORCA.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
      placement(SUNBIRD.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
      placement(GORILLA.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
      placement(CRIMSON_MOSQUITO.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
      placement(RATTLESNAKE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
      placement(ENDERGRADE.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
      placement(HAMMERHEAD_SHARK.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn);
      placement(LOBSTER.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
      placement(KOMODO_DRAGON.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
      placement(CAPUCHIN_MONKEY.get(), PLACE_ON_LEAVES, Types.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn);
      placement(CENTIPEDE_HEAD.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
      placement(WARPED_TOAD.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
      placement(MOOSE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
      placement(MIMICUBE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      placement(RACCOON.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(BLOBFISH.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
      placement(SEAL.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
      placement(COCKROACH.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
      placement(SHOEBILL.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(ELEPHANT.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(SOUL_VULTURE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
      placement(SNOW_LEOPARD.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn);
      placement(CROW.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING, EntityCrow::canCrowSpawn);
      placement(ALLIGATOR_SNAPPING_TURTLE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn);
      placement(MUNGUS.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
      placement(MANTIS_SHRIMP.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
      placement(GUSTER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
      placement(WARPED_MOSCO.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
      placement(STRADDLER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
      placement(STRADPOLE.get(), PLACE_IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
      placement(EMU.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
      placement(PLATYPUS.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
      placement(DROPBEAR.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
      placement(TASMANIAN_DEVIL.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(KANGAROO.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
      placement(CACHALOT_WHALE.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
      placement(LEAFCUTTER_ANT.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(ENDERIOPHAGE.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
      placement(BALD_EAGLE.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
      placement(TIGER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
      placement(TARANTULA_HAWK.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
      placement(VOID_WORM.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
      placement(FRILLED_SHARK.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
      placement(MIMIC_OCTOPUS.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
      placement(SEAGULL.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn);
      placement(FROSTSTALKER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn);
      placement(TUSKLIN.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn);
      placement(LAVIATHAN.get(), PLACE_IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn);
      placement(COSMAW.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn);
      placement(TOUCAN.get(), PLACE_ON_LEAVES, Types.MOTION_BLOCKING, EntityToucan::canToucanSpawn);
      placement(MANED_WOLF.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(ANACONDA.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn);
      placement(ANTEATER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn);
      placement(ROCKY_ROLLER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRockyRoller::checkRockyRollerSpawnRules);
      placement(FLUTTER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFlutter::canFlutterSpawn);
      placement(GELADA_MONKEY.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(JERBOA.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityJerboa::canJerboaSpawn);
      placement(TERRAPIN.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityTerrapin::canTerrapinSpawn);
      placement(COMB_JELLY.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCombJelly::canCombJellySpawn);
      placement(BUNFUNGUS.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityBunfungus::canBunfungusSpawn);
      placement(BISON.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(GIANT_SQUID.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityGiantSquid::canGiantSquidSpawn);
      placement(DEVILS_HOLE_PUPFISH.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityDevilsHolePupfish::canPupfishSpawn);
      placement(CATFISH.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCatfish::canCatfishSpawn);
      placement(FLYING_FISH.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      placement(SKELEWAG.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntitySkelewag::canSkelewagSpawn);
      placement(RAIN_FROG.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRainFrog::canRainFrogSpawn);
      placement(POTOO.get(), PLACE_ON_LEAVES, Types.MOTION_BLOCKING, EntityPotoo::canPotooSpawn);
      placement(MUDSKIPPER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMudskipper::canMudskipperSpawn);
      placement(RHINOCEROS.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(SUGAR_GLIDER.get(), PLACE_ON_LEAVES, Types.MOTION_BLOCKING, EntitySugarGlider::canSugarGliderSpawn);
      placement(FARSEER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFarseer::checkFarseerSpawnRules);
      placement(SKREECHER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySkreecher::checkSkreecherSpawnRules);
      placement(UNDERMINER.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityUnderminer::checkUnderminerSpawnRules);
      placement(MURMUR.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMurmur::checkMurmurSpawnRules);
      placement(SKUNK.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      placement(BANANA_SLUG.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityBananaSlug::checkBananaSlugSpawnRules);
      placement(BLUE_JAY.get(), PLACE_ON_LEAVES, Types.MOTION_BLOCKING, EntityBlueJay::checkBlueJaySpawnRules);
      placement(CAIMAN.get(), PLACE_ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCaiman::canCaimanSpawn);
      placement(TRIOPS.get(), PLACE_IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      placement(SPECTRE.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntitySpectre::canSpectreSpawn);
      placement(COSMIC_COD.get(), PLACE_NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> true);
   }

   private static void put(
      EntityAttributeCreationEvent event, EntityType<? extends LivingEntity> type, net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder
   ) {
      event.put(type, builder.build());
   }

   @SubscribeEvent
   public static void initializeAttributes(EntityAttributeCreationEvent event) {
      put(event, GRIZZLY_BEAR.get(), EntityGrizzlyBear.bakeAttributes());
      put(event, ROADRUNNER.get(), EntityRoadrunner.bakeAttributes());
      put(event, BONE_SERPENT.get(), EntityBoneSerpent.bakeAttributes());
      put(event, BONE_SERPENT_PART.get(), EntityBoneSerpentPart.bakeAttributes());
      put(event, GAZELLE.get(), EntityGazelle.bakeAttributes());
      put(event, CROCODILE.get(), EntityCrocodile.bakeAttributes());
      put(event, FLY.get(), EntityFly.bakeAttributes());
      put(event, HUMMINGBIRD.get(), EntityHummingbird.bakeAttributes());
      put(event, ORCA.get(), EntityOrca.bakeAttributes());
      put(event, SUNBIRD.get(), EntitySunbird.bakeAttributes());
      put(event, GORILLA.get(), EntityGorilla.bakeAttributes());
      put(event, CRIMSON_MOSQUITO.get(), EntityCrimsonMosquito.bakeAttributes());
      put(event, RATTLESNAKE.get(), EntityRattlesnake.bakeAttributes());
      put(event, ENDERGRADE.get(), EntityEndergrade.bakeAttributes());
      put(event, HAMMERHEAD_SHARK.get(), EntityHammerheadShark.bakeAttributes());
      put(event, LOBSTER.get(), EntityLobster.bakeAttributes());
      put(event, KOMODO_DRAGON.get(), EntityKomodoDragon.bakeAttributes());
      put(event, CAPUCHIN_MONKEY.get(), EntityCapuchinMonkey.bakeAttributes());
      put(event, CENTIPEDE_HEAD.get(), EntityCentipedeHead.bakeAttributes());
      put(event, CENTIPEDE_BODY.get(), EntityCentipedeBody.bakeAttributes());
      put(event, CENTIPEDE_TAIL.get(), EntityCentipedeTail.bakeAttributes());
      put(event, WARPED_TOAD.get(), EntityWarpedToad.bakeAttributes());
      put(event, MOOSE.get(), EntityMoose.bakeAttributes());
      put(event, MIMICUBE.get(), EntityMimicube.bakeAttributes());
      put(event, RACCOON.get(), EntityRaccoon.bakeAttributes());
      put(event, BLOBFISH.get(), EntityBlobfish.bakeAttributes());
      put(event, SEAL.get(), EntitySeal.bakeAttributes());
      put(event, COCKROACH.get(), EntityCockroach.bakeAttributes());
      put(event, SHOEBILL.get(), EntityShoebill.bakeAttributes());
      put(event, ELEPHANT.get(), EntityElephant.bakeAttributes());
      put(event, SOUL_VULTURE.get(), EntitySoulVulture.bakeAttributes());
      put(event, SNOW_LEOPARD.get(), EntitySnowLeopard.bakeAttributes());
      put(event, SPECTRE.get(), EntitySpectre.bakeAttributes());
      put(event, CROW.get(), EntityCrow.bakeAttributes());
      put(event, ALLIGATOR_SNAPPING_TURTLE.get(), EntityAlligatorSnappingTurtle.bakeAttributes());
      put(event, MUNGUS.get(), EntityMungus.bakeAttributes());
      put(event, MANTIS_SHRIMP.get(), EntityMantisShrimp.bakeAttributes());
      put(event, GUSTER.get(), EntityGuster.bakeAttributes());
      put(event, WARPED_MOSCO.get(), EntityWarpedMosco.bakeAttributes());
      put(event, STRADDLER.get(), EntityStraddler.bakeAttributes());
      put(event, STRADPOLE.get(), EntityStradpole.bakeAttributes());
      put(event, EMU.get(), EntityEmu.bakeAttributes());
      put(event, PLATYPUS.get(), EntityPlatypus.bakeAttributes());
      put(event, DROPBEAR.get(), EntityDropBear.bakeAttributes());
      put(event, TASMANIAN_DEVIL.get(), EntityTasmanianDevil.bakeAttributes());
      put(event, KANGAROO.get(), EntityKangaroo.bakeAttributes());
      put(event, CACHALOT_WHALE.get(), EntityCachalotWhale.bakeAttributes());
      put(event, LEAFCUTTER_ANT.get(), EntityLeafcutterAnt.bakeAttributes());
      put(event, ENDERIOPHAGE.get(), EntityEnderiophage.bakeAttributes());
      put(event, BALD_EAGLE.get(), EntityBaldEagle.bakeAttributes());
      put(event, TIGER.get(), EntityTiger.bakeAttributes());
      put(event, TARANTULA_HAWK.get(), EntityTarantulaHawk.bakeAttributes());
      put(event, VOID_WORM.get(), EntityVoidWorm.bakeAttributes());
      put(event, VOID_WORM_PART.get(), EntityVoidWormPart.bakeAttributes());
      put(event, FRILLED_SHARK.get(), EntityFrilledShark.bakeAttributes());
      put(event, MIMIC_OCTOPUS.get(), EntityMimicOctopus.bakeAttributes());
      put(event, SEAGULL.get(), EntitySeagull.bakeAttributes());
      put(event, FROSTSTALKER.get(), EntityFroststalker.bakeAttributes());
      put(event, TUSKLIN.get(), EntityTusklin.bakeAttributes());
      put(event, LAVIATHAN.get(), EntityLaviathan.bakeAttributes());
      put(event, COSMAW.get(), EntityCosmaw.bakeAttributes());
      put(event, TOUCAN.get(), EntityToucan.bakeAttributes());
      put(event, MANED_WOLF.get(), EntityManedWolf.bakeAttributes());
      put(event, ANACONDA.get(), EntityAnaconda.bakeAttributes());
      put(event, ANACONDA_PART.get(), EntityAnacondaPart.bakeAttributes());
      put(event, ANTEATER.get(), EntityAnteater.bakeAttributes());
      put(event, ROCKY_ROLLER.get(), EntityRockyRoller.bakeAttributes());
      put(event, FLUTTER.get(), EntityFlutter.bakeAttributes());
      put(event, GELADA_MONKEY.get(), EntityGeladaMonkey.bakeAttributes());
      put(event, JERBOA.get(), EntityJerboa.bakeAttributes());
      put(event, TERRAPIN.get(), EntityTerrapin.bakeAttributes());
      put(event, COMB_JELLY.get(), EntityCombJelly.bakeAttributes());
      put(event, COSMIC_COD.get(), EntityCosmicCod.bakeAttributes());
      put(event, BUNFUNGUS.get(), EntityBunfungus.bakeAttributes());
      put(event, BISON.get(), EntityBison.bakeAttributes());
      put(event, GIANT_SQUID.get(), EntityGiantSquid.bakeAttributes());
      put(event, SEA_BEAR.get(), EntitySeaBear.bakeAttributes());
      put(event, DEVILS_HOLE_PUPFISH.get(), EntityDevilsHolePupfish.bakeAttributes());
      put(event, CATFISH.get(), EntityCatfish.bakeAttributes());
      put(event, FLYING_FISH.get(), EntityFlyingFish.bakeAttributes());
      put(event, SKELEWAG.get(), EntitySkelewag.bakeAttributes());
      put(event, RAIN_FROG.get(), EntityRainFrog.bakeAttributes());
      put(event, POTOO.get(), EntityPotoo.bakeAttributes());
      put(event, MUDSKIPPER.get(), EntityMudskipper.bakeAttributes());
      put(event, RHINOCEROS.get(), EntityRhinoceros.bakeAttributes());
      put(event, SUGAR_GLIDER.get(), EntitySugarGlider.bakeAttributes());
      put(event, FARSEER.get(), EntityFarseer.bakeAttributes());
      put(event, SKREECHER.get(), EntitySkreecher.bakeAttributes());
      put(event, UNDERMINER.get(), EntityUnderminer.bakeAttributes());
      put(event, MURMUR.get(), EntityMurmur.bakeAttributes());
      put(event, MURMUR_HEAD.get(), EntityMurmurHead.bakeAttributes());
      put(event, SKUNK.get(), EntitySkunk.bakeAttributes());
      put(event, BANANA_SLUG.get(), EntityBananaSlug.bakeAttributes());
      put(event, BLUE_JAY.get(), EntityBlueJay.bakeAttributes());
      put(event, CAIMAN.get(), EntityCaiman.bakeAttributes());
      put(event, TRIOPS.get(), EntityTriops.bakeAttributes());
   }

   public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag) {
      return entityTag == null ? Predicates.alwaysFalse() : e -> e.isAlive() && e.getType().builtInRegistryHolder().is(entityTag);
   }

   public static Predicate<LivingEntity> buildPredicateFromTagTameable(TagKey<EntityType<?>> entityTag, LivingEntity owner) {
      return entityTag == null ? Predicates.alwaysFalse() : e -> e.isAlive() && e.getType().builtInRegistryHolder().is(entityTag) && !owner.isAlliedTo(e);
   }

   public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason) {
      return reason == MobSpawnType.SPAWNER ? true : rolls <= 0 || random.nextInt(rolls) == 0;
   }

   public static boolean createLeavesSpawnPlacement(LevelReader level, BlockPos pos, EntityType<?> type) {
      BlockPos blockpos = pos.above();
      BlockPos blockpos1 = pos.below();
      FluidState fluidstate = level.getFluidState(pos);
      BlockState blockstate = level.getBlockState(pos);
      BlockState blockstate1 = level.getBlockState(blockpos1);
      return !blockstate1.isValidSpawn(level, blockpos1, type) && !blockstate1.is(BlockTags.LEAVES)
         ? false
         : NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockstate, fluidstate, type)
            && NaturalSpawner.isValidEmptySpawnBlock(level, blockpos, level.getBlockState(blockpos), level.getFluidState(blockpos), type);
   }
}
