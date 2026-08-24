package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.entity.BabySkeletonEntity;
import net.mcreator.borninchaosv.entity.BabySkeletonMinionEntity;
import net.mcreator.borninchaosv.entity.BabySpiderControlledEntity;
import net.mcreator.borninchaosv.entity.BabySpiderEntity;
import net.mcreator.borninchaosv.entity.BarrelZombieEntity;
import net.mcreator.borninchaosv.entity.BloodyGadflyEntity;
import net.mcreator.borninchaosv.entity.BoneImpEntity;
import net.mcreator.borninchaosv.entity.BoneImpMinionEntity;
import net.mcreator.borninchaosv.entity.BonescallerEntity;
import net.mcreator.borninchaosv.entity.BonescallerNotDespawnEntity;
import net.mcreator.borninchaosv.entity.ControlledBabySkeletonEntity;
import net.mcreator.borninchaosv.entity.ControlledSpiritualAssistantEntity;
import net.mcreator.borninchaosv.entity.CorpseFishEntity;
import net.mcreator.borninchaosv.entity.CorpseFlyEntity;
import net.mcreator.borninchaosv.entity.DarkVortexEntity;
import net.mcreator.borninchaosv.entity.DecayingZombieEntity;
import net.mcreator.borninchaosv.entity.DecayingZombieNotDespawnEntity;
import net.mcreator.borninchaosv.entity.DecrepitSkeletonEntity;
import net.mcreator.borninchaosv.entity.DiamondThermiteEntity;
import net.mcreator.borninchaosv.entity.DireHoundLeaderEntity;
import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.entity.DreadHoundEntity;
import net.mcreator.borninchaosv.entity.DreadHoundNotDespawnEntity;
import net.mcreator.borninchaosv.entity.FallenChaosKnightEntity;
import net.mcreator.borninchaosv.entity.FelsteedEntity;
import net.mcreator.borninchaosv.entity.FirelightEntity;
import net.mcreator.borninchaosv.entity.FirelightNotDespawnEntity;
import net.mcreator.borninchaosv.entity.GluttonFishEntity;
import net.mcreator.borninchaosv.entity.InfernalSpiritEntity;
import net.mcreator.borninchaosv.entity.IntoxicatindBombProjectileEntity;
import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.entity.KrampusHenchmanEntity;
import net.mcreator.borninchaosv.entity.LifestealerEntity;
import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadHeadEntity;
import net.mcreator.borninchaosv.entity.LordPumpkinheadWithoutaHorseEntity;
import net.mcreator.borninchaosv.entity.LordTheHeadlessEntity;
import net.mcreator.borninchaosv.entity.LordsFelsteedEntity;
import net.mcreator.borninchaosv.entity.MaggotEntity;
import net.mcreator.borninchaosv.entity.MissionaryChargeEntity;
import net.mcreator.borninchaosv.entity.MissionaryRaiderEntity;
import net.mcreator.borninchaosv.entity.MissionerEntity;
import net.mcreator.borninchaosv.entity.MonstrousEasterEggProjectileEntity;
import net.mcreator.borninchaosv.entity.MotherSpiderEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinControlledEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinEntity;
import net.mcreator.borninchaosv.entity.MrsPumpkinEntity;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.mcreator.borninchaosv.entity.PhantomBombEntityEntity;
import net.mcreator.borninchaosv.entity.PhantomBombProjectileEntity;
import net.mcreator.borninchaosv.entity.PhantomCreeperCopyEntity;
import net.mcreator.borninchaosv.entity.PhantomCreeperEntity;
import net.mcreator.borninchaosv.entity.PumpkinBombEntity;
import net.mcreator.borninchaosv.entity.PumpkinBruiserEntity;
import net.mcreator.borninchaosv.entity.PumpkinDunceEntity;
import net.mcreator.borninchaosv.entity.PumpkinPistol2ProjectileEntity;
import net.mcreator.borninchaosv.entity.PumpkinPistolProjectileEntity;
import net.mcreator.borninchaosv.entity.PumpkinSpiritEntity;
import net.mcreator.borninchaosv.entity.PumpkinStaff2ProjectileEntity;
import net.mcreator.borninchaosv.entity.PumpkinStaffProjectileEntity;
import net.mcreator.borninchaosv.entity.PumpkinheadEntity;
import net.mcreator.borninchaosv.entity.RestlessSpiritEntity;
import net.mcreator.borninchaosv.entity.RidingFelsteedEntity;
import net.mcreator.borninchaosv.entity.RidingLordsFelsteedEntity;
import net.mcreator.borninchaosv.entity.RottenEasterEggProjectileEntity;
import net.mcreator.borninchaosv.entity.ScarletPersecutorEntity;
import net.mcreator.borninchaosv.entity.SearedSpiritEntity;
import net.mcreator.borninchaosv.entity.SearedSpiritNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SenorPumpkinEntity;
import net.mcreator.borninchaosv.entity.SiameseSkeletonsEntity;
import net.mcreator.borninchaosv.entity.SiameseSkeletonsleftEntity;
import net.mcreator.borninchaosv.entity.SiameseSkeletonsrightEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.SirPumpkinheadWithoutHorseEntity;
import net.mcreator.borninchaosv.entity.SirTheHeadlessEntity;
import net.mcreator.borninchaosv.entity.SkeletonDemomanEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SpiritGuideAssistantEntity;
import net.mcreator.borninchaosv.entity.SpiritGuideEntity;
import net.mcreator.borninchaosv.entity.SpiritofChaosEntity;
import net.mcreator.borninchaosv.entity.SpiritualEasterEggProjectileEntity;
import net.mcreator.borninchaosv.entity.StaffofBlindnessProjectileEntity;
import net.mcreator.borninchaosv.entity.StaffofMagicArrows2ProjectileEntity;
import net.mcreator.borninchaosv.entity.StaffofMagicArrowsProjectileEntity;
import net.mcreator.borninchaosv.entity.StimulatingBombprojectileEntity;
import net.mcreator.borninchaosv.entity.SupremeBonescallerEntity;
import net.mcreator.borninchaosv.entity.SupremeBonescallerNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SupremeBonescallerStage2Entity;
import net.mcreator.borninchaosv.entity.SwarmerEntity;
import net.mcreator.borninchaosv.entity.ThornshellCrabEntity;
import net.mcreator.borninchaosv.entity.ZombieBruiserEntity;
import net.mcreator.borninchaosv.entity.ZombieClownEntity;
import net.mcreator.borninchaosv.entity.ZombieClownNotDespawnEntity;
import net.mcreator.borninchaosv.entity.ZombieFishermanEntity;
import net.mcreator.borninchaosv.entity.ZombieLumberjackEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class BornInChaosV1ModEntities {
   public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, "born_in_chaos_v1");
   public static final DeferredHolder<EntityType<?>, EntityType<DecrepitSkeletonEntity>> DECREPIT_SKELETON = register(
      "decrepit_skeleton",
      Builder.of(DecrepitSkeletonEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkeletonDemomanEntity>> SKELETON_DEMOMAN = register(
      "skeleton_demoman",
      Builder.of(SkeletonDemomanEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DecayingZombieEntity>> DECAYING_ZOMBIE = register(
      "decaying_zombie",
      Builder.of(DecayingZombieEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BabySkeletonEntity>> BABY_SKELETON = register(
      "baby_skeleton",
      Builder.of(BabySkeletonEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BabySkeletonMinionEntity>> BABY_SKELETON_MINION = register(
      "baby_skeleton_minion",
      Builder.of(BabySkeletonMinionEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ControlledBabySkeletonEntity>> CONTROLLED_BABY_SKELETON = register(
      "controlled_baby_skeleton",
      Builder.of(ControlledBabySkeletonEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BarrelZombieEntity>> BARREL_ZOMBIE = register(
      "barrel_zombie",
      Builder.of(BarrelZombieEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.8F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhantomCreeperEntity>> PHANTOM_CREEPER = register(
      "phantom_creeper",
      Builder.of(PhantomCreeperEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhantomCreeperCopyEntity>> PHANTOM_CREEPER_COPY = register(
      "phantom_creeper_copy",
      Builder.of(PhantomCreeperCopyEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DoorKnightEntity>> DOOR_KNIGHT = register(
      "door_knight",
      Builder.of(DoorKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.9F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RestlessSpiritEntity>> RESTLESS_SPIRIT = register(
      "restless_spirit",
      Builder.of(RestlessSpiritEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(0.8F, 1.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkeletonThrasherEntity>> SKELETON_THRASHER = register(
      "skeleton_thrasher",
      Builder.of(SkeletonThrasherEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(0.9F, 2.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinSpiritEntity>> PUMPKIN_SPIRIT = register(
      "pumpkin_spirit",
      Builder.of(PumpkinSpiritEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .sized(0.8F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SearedSpiritEntity>> SEARED_SPIRIT = register(
      "seared_spirit",
      Builder.of(SearedSpiritEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FirelightEntity>> FIRELIGHT = register(
      "firelight",
      Builder.of(FirelightEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 0.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DiamondThermiteEntity>> DIAMOND_TERMITE = register(
      "diamond_termite",
      Builder.of(DiamondThermiteEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.7F, 0.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BonescallerEntity>> BONESCALLER = register(
      "bonescaller",
      Builder.of(BonescallerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(85).setUpdateInterval(3).sized(0.7F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BoneImpEntity>> BONE_IMP = register(
      "bone_imp",
      Builder.of(BoneImpEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BoneImpMinionEntity>> BONE_IMP_MINION = register(
      "bone_imp_minion",
      Builder.of(BoneImpMinionEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SupremeBonescallerEntity>> SUPREME_BONESCALLER = register(
      "supreme_bonescaller",
      Builder.of(SupremeBonescallerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(85)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SupremeBonescallerStage2Entity>> SUPREME_BONESCALLER_STAGE_2 = register(
      "supreme_bonescaller_stage_2",
      Builder.of(SupremeBonescallerStage2Entity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(85)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DarkVortexEntity>> DARK_VORTEX = register(
      "dark_vortex",
      Builder.of(DarkVortexEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(70).setUpdateInterval(3).sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DreadHoundEntity>> DREAD_HOUND = register(
      "dread_hound",
      Builder.of(DreadHoundEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(70).setUpdateInterval(3).sized(0.9F, 1.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FallenChaosKnightEntity>> FALLEN_CHAOS_KNIGHT = register(
      "fallen_chaos_knight",
      Builder.of(FallenChaosKnightEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(75)
         .setUpdateInterval(3)
         .sized(0.7F, 2.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ScarletPersecutorEntity>> SCARLET_PERSECUTOR = register(
      "scarlet_persecutor",
      Builder.of(ScarletPersecutorEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(150)
         .setUpdateInterval(3)
         .sized(0.7F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpiritGuideEntity>> SPIRIT_GUIDE = register(
      "spirit_guide",
      Builder.of(SpiritGuideEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(66).setUpdateInterval(3).sized(0.6F, 2.1F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpiritGuideAssistantEntity>> SPIRIT_GUIDE_ASSISTANT = register(
      "spirit_guide_assistant",
      Builder.of(SpiritGuideAssistantEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZombieClownEntity>> ZOMBIE_CLOWN = register(
      "zombie_clown",
      Builder.of(ZombieClownEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(60).setUpdateInterval(3).sized(0.8F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MrPumpkinEntity>> MR_PUMPKIN = register(
      "mr_pumpkin",
      Builder.of(MrPumpkinEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(70).setUpdateInterval(3).sized(0.7F, 1.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MrPumpkinControlledEntity>> MR_PUMPKIN_CONTROLLED = register(
      "mr_pumpkin_controlled",
      Builder.of(MrPumpkinControlledEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .sized(0.9F, 1.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SirPumpkinheadEntity>> SIR_PUMPKINHEAD = register(
      "sir_pumpkinhead",
      Builder.of(SirPumpkinheadEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FelsteedEntity>> FELSTEED = register(
      "felsteed",
      Builder.of(FelsteedEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(69)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SirPumpkinheadWithoutHorseEntity>> SIR_PUMPKINHEAD_WITHOUT_HORSE = register(
      "sir_pumpkinhead_without_horse",
      Builder.of(SirPumpkinheadWithoutHorseEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SirTheHeadlessEntity>> SIR_THE_HEADLESS = register(
      "sir_the_headless",
      Builder.of(SirTheHeadlessEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinheadEntity>> PUMPKINHEAD = register(
      "pumpkinhead",
      Builder.of(PumpkinheadEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RidingFelsteedEntity>> RIDING_FELSTEED = register(
      "riding_felsteed",
      Builder.of(RidingFelsteedEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DecayingZombieNotDespawnEntity>> DECAYING_ZOMBIE_NOT_DESPAWN = register(
      "decaying_zombie_not_despawn",
      Builder.of(DecayingZombieNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DoorKnightNotDespawnEntity>> DOOR_KNIGHT_NOT_DESPAWN = register(
      "door_knight_not_despawn",
      Builder.of(DoorKnightNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.9F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BonescallerNotDespawnEntity>> BONESCALLER_NOT_DESPAWN = register(
      "bonescaller_not_despawn",
      Builder.of(BonescallerNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(85)
         .setUpdateInterval(3)
         .sized(0.7F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SupremeBonescallerNotDespawnEntity>> SUPREME_BONESCALLER_NOT_DESPAWN = register(
      "supreme_bonescaller_not_despawn",
      Builder.of(SupremeBonescallerNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(85)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkeletonThrasherNotDespawnEntity>> SKELETON_THRASHER_NOT_DESPAWN = register(
      "skeleton_thrasher_not_despawn",
      Builder.of(SkeletonThrasherNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(0.9F, 2.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DreadHoundNotDespawnEntity>> DREAD_HOUND_NOT_DESPAWN = register(
      "dread_hound_not_despawn",
      Builder.of(DreadHoundNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(0.9F, 1.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FirelightNotDespawnEntity>> FIRELIGHT_NOT_DESPAWN = register(
      "firelight_not_despawn",
      Builder.of(FirelightNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 0.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZombieClownNotDespawnEntity>> ZOMBIE_CLOWN_NOT_DESPAWN = register(
      "zombie_clown_not_despawn",
      Builder.of(ZombieClownNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(60)
         .setUpdateInterval(3)
         .sized(0.8F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SearedSpiritNotDespawnEntity>> SEARED_SPIRIT_NOT_DESPAWN = register(
      "seared_spirit_not_despawn",
      Builder.of(SearedSpiritNotDespawnEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DireHoundLeaderEntity>> DIRE_HOUND_LEADER = register(
      "dire_hound_leader",
      Builder.of(DireHoundLeaderEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(1.3F, 1.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MaggotEntity>> MAGGOT = register(
      "maggot",
      Builder.of(MaggotEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.5F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CorpseFlyEntity>> CORPSE_FLY = register(
      "corpse_fly",
      Builder.of(CorpseFlyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(65).setUpdateInterval(3).sized(0.5F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodyGadflyEntity>> BLOODY_GADFLY = register(
      "bloody_gadfly",
      Builder.of(BloodyGadflyEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CorpseFishEntity>> CORPSE_FISH = register(
      "corpse_fish",
      Builder.of(CorpseFishEntity::new, MobCategory.WATER_CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .sized(0.9F, 0.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZombieFishermanEntity>> ZOMBIE_FISHERMAN = register(
      "zombie_fisherman",
      Builder.of(ZombieFishermanEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SwarmerEntity>> SWARMER = register(
      "swarmer",
      Builder.of(SwarmerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(40).setUpdateInterval(3).sized(0.8F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThornshellCrabEntity>> THORNSHELL_CRAB = register(
      "thornshell_crab",
      Builder.of(ThornshellCrabEntity::new, MobCategory.CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.9F, 1.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GluttonFishEntity>> GLUTTON_FISH = register(
      "glutton_fish",
      Builder.of(GluttonFishEntity::new, MobCategory.WATER_CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .sized(2.8F, 2.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZombieBruiserEntity>> ZOMBIE_BRUISER = register(
      "zombie_bruiser",
      Builder.of(ZombieBruiserEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .sized(0.9F, 2.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinBombEntity>> PUMPKIN_BOMB = register(
      "pumpkin_bomb",
      Builder.of(PumpkinBombEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 0.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SenorPumpkinEntity>> SENOR_PUMPKIN = register(
      "senor_pumpkin",
      Builder.of(SenorPumpkinEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.7F, 1.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LordPumpkinheadEntity>> LORD_PUMPKINHEAD = register(
      "lord_pumpkinhead",
      Builder.of(LordPumpkinheadEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LordsFelsteedEntity>> LORDS_FELSTEED = register(
      "lords_felsteed",
      Builder.of(LordsFelsteedEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LordPumpkinheadWithoutaHorseEntity>> LORD_PUMPKINHEAD_WITHOUTA_HORSE = register(
      "lord_pumpkinhead_withouta_horse",
      Builder.of(LordPumpkinheadWithoutaHorseEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LordTheHeadlessEntity>> LORD_THE_HEADLESS = register(
      "lord_the_headless",
      Builder.of(LordTheHeadlessEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(80)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LordPumpkinheadHeadEntity>> LORD_PUMPKINHEAD_HEAD = register(
      "lord_pumpkinhead_head",
      Builder.of(LordPumpkinheadHeadEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<InfernalSpiritEntity>> INFERNAL_SPIRIT = register(
      "infernal_spirit",
      Builder.of(InfernalSpiritEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RidingLordsFelsteedEntity>> RIDING_LORDS_FELSTEED = register(
      "riding_lords_felsteed",
      Builder.of(RidingLordsFelsteedEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<StaffofMagicArrowsProjectileEntity>> STAFFOF_MAGIC_ARROWS_PROJECTILE = register(
      "staffof_magic_arrows_projectile",
      Builder.of(StaffofMagicArrowsProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<StaffofBlindnessProjectileEntity>> STAFFOF_BLINDNESS_PROJECTILE = register(
      "staffof_blindness_projectile",
      Builder.of(StaffofBlindnessProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinStaffProjectileEntity>> PUMPKIN_STAFF_PROJECTILE = register(
      "pumpkin_staff_projectile",
      Builder.of(PumpkinStaffProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<StaffofMagicArrows2ProjectileEntity>> STAFFOF_MAGIC_ARROWS_2_PROJECTILE = register(
      "staffof_magic_arrows_2_projectile",
      Builder.of(StaffofMagicArrows2ProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinStaff2ProjectileEntity>> PUMPKIN_STAFF_2_PROJECTILE = register(
      "pumpkin_staff_2_projectile",
      Builder.of(PumpkinStaff2ProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IntoxicatindBombProjectileEntity>> INTOXICATIND_BOMB_PROJECTILE = register(
      "intoxicatind_bomb_projectile",
      Builder.of(IntoxicatindBombProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinPistolProjectileEntity>> PUMPKIN_PISTOL_PROJECTILE = register(
      "pumpkin_pistol_projectile",
      Builder.of(PumpkinPistolProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinPistol2ProjectileEntity>> PUMPKIN_PISTOL_2_PROJECTILE = register(
      "pumpkin_pistol_2_projectile",
      Builder.of(PumpkinPistol2ProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ZombieLumberjackEntity>> ZOMBIE_LUMBERJACK = register(
      "zombie_lumberjack",
      Builder.of(ZombieLumberjackEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SiameseSkeletonsEntity>> SIAMESE_SKELETONS = register(
      "siamese_skeletons",
      Builder.of(SiameseSkeletonsEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SiameseSkeletonsleftEntity>> SIAMESE_SKELETONSLEFT = register(
      "siamese_skeletonsleft",
      Builder.of(SiameseSkeletonsleftEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SiameseSkeletonsrightEntity>> SIAMESE_SKELETONSRIGHT = register(
      "siamese_skeletonsright",
      Builder.of(SiameseSkeletonsrightEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MissionerEntity>> MISSIONER = register(
      "missioner",
      Builder.of(MissionerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.9F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MissionaryChargeEntity>> MISSIONARY_CHARGE = register(
      "missionary_charge",
      Builder.of(MissionaryChargeEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<StimulatingBombprojectileEntity>> STIMULATING_BOMBPROJECTILE = register(
      "stimulating_bombprojectile",
      Builder.of(StimulatingBombprojectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhantomBombEntityEntity>> PHANTOM_BOMB_ENTITY = register(
      "phantom_bomb_entity",
      Builder.of(PhantomBombEntityEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhantomBombProjectileEntity>> PHANTOM_BOMB_PROJECTILE = register(
      "phantom_bomb_projectile",
      Builder.of(PhantomBombProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ControlledSpiritualAssistantEntity>> CONTROLLED_SPIRITUAL_ASSISTANT = register(
      "controlled_spiritual_assistant",
      Builder.of(ControlledSpiritualAssistantEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpiritofChaosEntity>> SPIRITOF_CHAOS = register(
      "spiritof_chaos",
      Builder.of(SpiritofChaosEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(70)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LifestealerEntity>> LIFESTEALER = register(
      "lifestealer",
      Builder.of(LifestealerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(100)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LifestealerTrueFormEntity>> LIFESTEALER_TRUE_FORM = register(
      "lifestealer_true_form",
      Builder.of(LifestealerTrueFormEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(100)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BabySpiderEntity>> BABY_SPIDER = register(
      "baby_spider",
      Builder.of(BabySpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.7F, 0.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MotherSpiderEntity>> MOTHER_SPIDER = register(
      "mother_spider",
      Builder.of(MotherSpiderEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(1.5F, 1.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BabySpiderControlledEntity>> BABY_SPIDER_CONTROLLED = register(
      "baby_spider_controlled",
      Builder.of(BabySpiderControlledEntity::new, MobCategory.CREATURE)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.7F, 0.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NightmareStalkerEntity>> NIGHTMARE_STALKER = register(
      "nightmare_stalker",
      Builder.of(NightmareStalkerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(170)
         .setUpdateInterval(3)
         .sized(0.7F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinDunceEntity>> PUMPKIN_DUNCE = register(
      "pumpkin_dunce",
      Builder.of(PumpkinDunceEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.8F, 0.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MrsPumpkinEntity>> MRS_PUMPKIN = register(
      "mrs_pumpkin",
      Builder.of(MrsPumpkinEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.7F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinBruiserEntity>> PUMPKIN_BRUISER = register(
      "pumpkin_bruiser",
      Builder.of(PumpkinBruiserEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(65)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.9F, 2.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<KrampusEntity>> KRAMPUS = register(
      "krampus",
      Builder.of(KrampusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(90).setUpdateInterval(3).sized(0.9F, 2.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<KrampusHenchmanEntity>> KRAMPUS_HENCHMAN = register(
      "krampus_henchman",
      Builder.of(KrampusHenchmanEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(90)
         .setUpdateInterval(3)
         .sized(0.8F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MissionaryRaiderEntity>> MISSIONARY_RAIDER = register(
      "missionary_raider",
      Builder.of(MissionaryRaiderEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.9F, 3.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RottenEasterEggProjectileEntity>> ROTTEN_EASTER_EGG_PROJECTILE = register(
      "rotten_easter_egg_projectile",
      Builder.of(RottenEasterEggProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpiritualEasterEggProjectileEntity>> SPIRITUAL_EASTER_EGG_PROJECTILE = register(
      "spiritual_easter_egg_projectile",
      Builder.of(SpiritualEasterEggProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MonstrousEasterEggProjectileEntity>> MONSTROUS_EASTER_EGG_PROJECTILE = register(
      "monstrous_easter_egg_projectile",
      Builder.of(MonstrousEasterEggProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );

   private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, Builder<T> entityTypeBuilder) {
      return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
   }

   @SubscribeEvent
   public static void init(RegisterSpawnPlacementsEvent event) {
      DecrepitSkeletonEntity.init(event);
      SkeletonDemomanEntity.init(event);
      DecayingZombieEntity.init(event);
      BabySkeletonEntity.init(event);
      BabySkeletonMinionEntity.init(event);
      ControlledBabySkeletonEntity.init(event);
      BarrelZombieEntity.init(event);
      PhantomCreeperEntity.init(event);
      PhantomCreeperCopyEntity.init(event);
      DoorKnightEntity.init(event);
      RestlessSpiritEntity.init(event);
      SkeletonThrasherEntity.init(event);
      PumpkinSpiritEntity.init(event);
      SearedSpiritEntity.init(event);
      FirelightEntity.init(event);
      DiamondThermiteEntity.init(event);
      BonescallerEntity.init(event);
      BoneImpEntity.init(event);
      BoneImpMinionEntity.init(event);
      SupremeBonescallerEntity.init(event);
      SupremeBonescallerStage2Entity.init(event);
      DarkVortexEntity.init(event);
      DreadHoundEntity.init(event);
      FallenChaosKnightEntity.init(event);
      ScarletPersecutorEntity.init(event);
      SpiritGuideEntity.init(event);
      SpiritGuideAssistantEntity.init(event);
      ZombieClownEntity.init(event);
      MrPumpkinEntity.init(event);
      MrPumpkinControlledEntity.init(event);
      SirPumpkinheadEntity.init(event);
      FelsteedEntity.init(event);
      SirPumpkinheadWithoutHorseEntity.init(event);
      SirTheHeadlessEntity.init(event);
      PumpkinheadEntity.init(event);
      RidingFelsteedEntity.init(event);
      DecayingZombieNotDespawnEntity.init(event);
      DoorKnightNotDespawnEntity.init(event);
      BonescallerNotDespawnEntity.init(event);
      SupremeBonescallerNotDespawnEntity.init(event);
      SkeletonThrasherNotDespawnEntity.init(event);
      DreadHoundNotDespawnEntity.init(event);
      FirelightNotDespawnEntity.init(event);
      ZombieClownNotDespawnEntity.init(event);
      SearedSpiritNotDespawnEntity.init(event);
      DireHoundLeaderEntity.init(event);
      MaggotEntity.init(event);
      CorpseFlyEntity.init(event);
      BloodyGadflyEntity.init(event);
      CorpseFishEntity.init(event);
      ZombieFishermanEntity.init(event);
      SwarmerEntity.init(event);
      ThornshellCrabEntity.init(event);
      GluttonFishEntity.init(event);
      ZombieBruiserEntity.init(event);
      PumpkinBombEntity.init(event);
      SenorPumpkinEntity.init(event);
      LordPumpkinheadEntity.init(event);
      LordsFelsteedEntity.init(event);
      LordPumpkinheadWithoutaHorseEntity.init(event);
      LordTheHeadlessEntity.init(event);
      LordPumpkinheadHeadEntity.init(event);
      InfernalSpiritEntity.init(event);
      RidingLordsFelsteedEntity.init(event);
      ZombieLumberjackEntity.init(event);
      SiameseSkeletonsEntity.init(event);
      SiameseSkeletonsleftEntity.init(event);
      SiameseSkeletonsrightEntity.init(event);
      MissionerEntity.init(event);
      PhantomBombEntityEntity.init(event);
      ControlledSpiritualAssistantEntity.init(event);
      SpiritofChaosEntity.init(event);
      LifestealerEntity.init(event);
      LifestealerTrueFormEntity.init(event);
      BabySpiderEntity.init(event);
      MotherSpiderEntity.init(event);
      BabySpiderControlledEntity.init(event);
      NightmareStalkerEntity.init(event);
      PumpkinDunceEntity.init(event);
      MrsPumpkinEntity.init(event);
      PumpkinBruiserEntity.init(event);
      KrampusEntity.init(event);
      KrampusHenchmanEntity.init(event);
      MissionaryRaiderEntity.init(event);
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)DECREPIT_SKELETON.get(), DecrepitSkeletonEntity.createAttributes().build());
      event.put((EntityType)SKELETON_DEMOMAN.get(), SkeletonDemomanEntity.createAttributes().build());
      event.put((EntityType)DECAYING_ZOMBIE.get(), DecayingZombieEntity.createAttributes().build());
      event.put((EntityType)BABY_SKELETON.get(), BabySkeletonEntity.createAttributes().build());
      event.put((EntityType)BABY_SKELETON_MINION.get(), BabySkeletonMinionEntity.createAttributes().build());
      event.put((EntityType)CONTROLLED_BABY_SKELETON.get(), ControlledBabySkeletonEntity.createAttributes().build());
      event.put((EntityType)BARREL_ZOMBIE.get(), BarrelZombieEntity.createAttributes().build());
      event.put((EntityType)PHANTOM_CREEPER.get(), PhantomCreeperEntity.createAttributes().build());
      event.put((EntityType)PHANTOM_CREEPER_COPY.get(), PhantomCreeperCopyEntity.createAttributes().build());
      event.put((EntityType)DOOR_KNIGHT.get(), DoorKnightEntity.createAttributes().build());
      event.put((EntityType)RESTLESS_SPIRIT.get(), RestlessSpiritEntity.createAttributes().build());
      event.put((EntityType)SKELETON_THRASHER.get(), SkeletonThrasherEntity.createAttributes().build());
      event.put((EntityType)PUMPKIN_SPIRIT.get(), PumpkinSpiritEntity.createAttributes().build());
      event.put((EntityType)SEARED_SPIRIT.get(), SearedSpiritEntity.createAttributes().build());
      event.put((EntityType)FIRELIGHT.get(), FirelightEntity.createAttributes().build());
      event.put((EntityType)DIAMOND_TERMITE.get(), DiamondThermiteEntity.createAttributes().build());
      event.put((EntityType)BONESCALLER.get(), BonescallerEntity.createAttributes().build());
      event.put((EntityType)BONE_IMP.get(), BoneImpEntity.createAttributes().build());
      event.put((EntityType)BONE_IMP_MINION.get(), BoneImpMinionEntity.createAttributes().build());
      event.put((EntityType)SUPREME_BONESCALLER.get(), SupremeBonescallerEntity.createAttributes().build());
      event.put((EntityType)SUPREME_BONESCALLER_STAGE_2.get(), SupremeBonescallerStage2Entity.createAttributes().build());
      event.put((EntityType)DARK_VORTEX.get(), DarkVortexEntity.createAttributes().build());
      event.put((EntityType)DREAD_HOUND.get(), DreadHoundEntity.createAttributes().build());
      event.put((EntityType)FALLEN_CHAOS_KNIGHT.get(), FallenChaosKnightEntity.createAttributes().build());
      event.put((EntityType)SCARLET_PERSECUTOR.get(), ScarletPersecutorEntity.createAttributes().build());
      event.put((EntityType)SPIRIT_GUIDE.get(), SpiritGuideEntity.createAttributes().build());
      event.put((EntityType)SPIRIT_GUIDE_ASSISTANT.get(), SpiritGuideAssistantEntity.createAttributes().build());
      event.put((EntityType)ZOMBIE_CLOWN.get(), ZombieClownEntity.createAttributes().build());
      event.put((EntityType)MR_PUMPKIN.get(), MrPumpkinEntity.createAttributes().build());
      event.put((EntityType)MR_PUMPKIN_CONTROLLED.get(), MrPumpkinControlledEntity.createAttributes().build());
      event.put((EntityType)SIR_PUMPKINHEAD.get(), SirPumpkinheadEntity.createAttributes().build());
      event.put((EntityType)FELSTEED.get(), FelsteedEntity.createAttributes().build());
      event.put((EntityType)SIR_PUMPKINHEAD_WITHOUT_HORSE.get(), SirPumpkinheadWithoutHorseEntity.createAttributes().build());
      event.put((EntityType)SIR_THE_HEADLESS.get(), SirTheHeadlessEntity.createAttributes().build());
      event.put((EntityType)PUMPKINHEAD.get(), PumpkinheadEntity.createAttributes().build());
      event.put((EntityType)RIDING_FELSTEED.get(), RidingFelsteedEntity.createAttributes().build());
      event.put((EntityType)DECAYING_ZOMBIE_NOT_DESPAWN.get(), DecayingZombieNotDespawnEntity.createAttributes().build());
      event.put((EntityType)DOOR_KNIGHT_NOT_DESPAWN.get(), DoorKnightNotDespawnEntity.createAttributes().build());
      event.put((EntityType)BONESCALLER_NOT_DESPAWN.get(), BonescallerNotDespawnEntity.createAttributes().build());
      event.put((EntityType)SUPREME_BONESCALLER_NOT_DESPAWN.get(), SupremeBonescallerNotDespawnEntity.createAttributes().build());
      event.put((EntityType)SKELETON_THRASHER_NOT_DESPAWN.get(), SkeletonThrasherNotDespawnEntity.createAttributes().build());
      event.put((EntityType)DREAD_HOUND_NOT_DESPAWN.get(), DreadHoundNotDespawnEntity.createAttributes().build());
      event.put((EntityType)FIRELIGHT_NOT_DESPAWN.get(), FirelightNotDespawnEntity.createAttributes().build());
      event.put((EntityType)ZOMBIE_CLOWN_NOT_DESPAWN.get(), ZombieClownNotDespawnEntity.createAttributes().build());
      event.put((EntityType)SEARED_SPIRIT_NOT_DESPAWN.get(), SearedSpiritNotDespawnEntity.createAttributes().build());
      event.put((EntityType)DIRE_HOUND_LEADER.get(), DireHoundLeaderEntity.createAttributes().build());
      event.put((EntityType)MAGGOT.get(), MaggotEntity.createAttributes().build());
      event.put((EntityType)CORPSE_FLY.get(), CorpseFlyEntity.createAttributes().build());
      event.put((EntityType)BLOODY_GADFLY.get(), BloodyGadflyEntity.createAttributes().build());
      event.put((EntityType)CORPSE_FISH.get(), CorpseFishEntity.createAttributes().build());
      event.put((EntityType)ZOMBIE_FISHERMAN.get(), ZombieFishermanEntity.createAttributes().build());
      event.put((EntityType)SWARMER.get(), SwarmerEntity.createAttributes().build());
      event.put((EntityType)THORNSHELL_CRAB.get(), ThornshellCrabEntity.createAttributes().build());
      event.put((EntityType)GLUTTON_FISH.get(), GluttonFishEntity.createAttributes().build());
      event.put((EntityType)ZOMBIE_BRUISER.get(), ZombieBruiserEntity.createAttributes().build());
      event.put((EntityType)PUMPKIN_BOMB.get(), PumpkinBombEntity.createAttributes().build());
      event.put((EntityType)SENOR_PUMPKIN.get(), SenorPumpkinEntity.createAttributes().build());
      event.put((EntityType)LORD_PUMPKINHEAD.get(), LordPumpkinheadEntity.createAttributes().build());
      event.put((EntityType)LORDS_FELSTEED.get(), LordsFelsteedEntity.createAttributes().build());
      event.put((EntityType)LORD_PUMPKINHEAD_WITHOUTA_HORSE.get(), LordPumpkinheadWithoutaHorseEntity.createAttributes().build());
      event.put((EntityType)LORD_THE_HEADLESS.get(), LordTheHeadlessEntity.createAttributes().build());
      event.put((EntityType)LORD_PUMPKINHEAD_HEAD.get(), LordPumpkinheadHeadEntity.createAttributes().build());
      event.put((EntityType)INFERNAL_SPIRIT.get(), InfernalSpiritEntity.createAttributes().build());
      event.put((EntityType)RIDING_LORDS_FELSTEED.get(), RidingLordsFelsteedEntity.createAttributes().build());
      event.put((EntityType)ZOMBIE_LUMBERJACK.get(), ZombieLumberjackEntity.createAttributes().build());
      event.put((EntityType)SIAMESE_SKELETONS.get(), SiameseSkeletonsEntity.createAttributes().build());
      event.put((EntityType)SIAMESE_SKELETONSLEFT.get(), SiameseSkeletonsleftEntity.createAttributes().build());
      event.put((EntityType)SIAMESE_SKELETONSRIGHT.get(), SiameseSkeletonsrightEntity.createAttributes().build());
      event.put((EntityType)MISSIONER.get(), MissionerEntity.createAttributes().build());
      event.put((EntityType)PHANTOM_BOMB_ENTITY.get(), PhantomBombEntityEntity.createAttributes().build());
      event.put((EntityType)CONTROLLED_SPIRITUAL_ASSISTANT.get(), ControlledSpiritualAssistantEntity.createAttributes().build());
      event.put((EntityType)SPIRITOF_CHAOS.get(), SpiritofChaosEntity.createAttributes().build());
      event.put((EntityType)LIFESTEALER.get(), LifestealerEntity.createAttributes().build());
      event.put((EntityType)LIFESTEALER_TRUE_FORM.get(), LifestealerTrueFormEntity.createAttributes().build());
      event.put((EntityType)BABY_SPIDER.get(), BabySpiderEntity.createAttributes().build());
      event.put((EntityType)MOTHER_SPIDER.get(), MotherSpiderEntity.createAttributes().build());
      event.put((EntityType)BABY_SPIDER_CONTROLLED.get(), BabySpiderControlledEntity.createAttributes().build());
      event.put((EntityType)NIGHTMARE_STALKER.get(), NightmareStalkerEntity.createAttributes().build());
      event.put((EntityType)PUMPKIN_DUNCE.get(), PumpkinDunceEntity.createAttributes().build());
      event.put((EntityType)MRS_PUMPKIN.get(), MrsPumpkinEntity.createAttributes().build());
      event.put((EntityType)PUMPKIN_BRUISER.get(), PumpkinBruiserEntity.createAttributes().build());
      event.put((EntityType)KRAMPUS.get(), KrampusEntity.createAttributes().build());
      event.put((EntityType)KRAMPUS_HENCHMAN.get(), KrampusHenchmanEntity.createAttributes().build());
      event.put((EntityType)MISSIONARY_RAIDER.get(), MissionaryRaiderEntity.createAttributes().build());
   }
}
