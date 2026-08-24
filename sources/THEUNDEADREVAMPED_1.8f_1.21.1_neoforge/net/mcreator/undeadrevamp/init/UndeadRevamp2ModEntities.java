package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.entity.AcidsackProjectileEntity;
import net.mcreator.undeadrevamp.entity.ArapoholiasprayProjectileEntity;
import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.mcreator.undeadrevamp.entity.BigsuckerEntity;
import net.mcreator.undeadrevamp.entity.BomberEntity;
import net.mcreator.undeadrevamp.entity.BoulderthrowProjectileEntity;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.entity.CoppertarEntity;
import net.mcreator.undeadrevamp.entity.CrackleballEntity;
import net.mcreator.undeadrevamp.entity.DeadcloggerEntity;
import net.mcreator.undeadrevamp.entity.FIresackProjectileEntity;
import net.mcreator.undeadrevamp.entity.FlameEntity;
import net.mcreator.undeadrevamp.entity.HonniethrowProjectileEntity;
import net.mcreator.undeadrevamp.entity.INVISIBLEBIDYEntity;
import net.mcreator.undeadrevamp.entity.InvisicloggerEntity;
import net.mcreator.undeadrevamp.entity.InvisiimmortalEntity;
import net.mcreator.undeadrevamp.entity.InvisilehceryEntity;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.entity.NeocrorinesEntity;
import net.mcreator.undeadrevamp.entity.PregnantneccProjectileEntity;
import net.mcreator.undeadrevamp.entity.Propball1Entity;
import net.mcreator.undeadrevamp.entity.QueenbeeperfumeProjectileEntity;
import net.mcreator.undeadrevamp.entity.SkeeperthrowprojectileEntity;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.mcreator.undeadrevamp.entity.SleepsmokebombEntity;
import net.mcreator.undeadrevamp.entity.SmokesmitterEntity;
import net.mcreator.undeadrevamp.entity.SpitterneccProjectileEntity;
import net.mcreator.undeadrevamp.entity.SuckerEntity;
import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.mcreator.undeadrevamp.entity.ThebidyupsideEntity;
import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.mcreator.undeadrevamp.entity.ThegliterEntity;
import net.mcreator.undeadrevamp.entity.TheheavyEntity;
import net.mcreator.undeadrevamp.entity.ThehorrorsEntity;
import net.mcreator.undeadrevamp.entity.ThehorrorsdecoysEntity;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.mcreator.undeadrevamp.entity.ThelurkerEntity;
import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.entity.TherodEntity;
import net.mcreator.undeadrevamp.entity.TheskeeperEntity;
import net.mcreator.undeadrevamp.entity.ThesmokerEntity;
import net.mcreator.undeadrevamp.entity.ThesomnolenceEntity;
import net.mcreator.undeadrevamp.entity.ThespectreEntity;
import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.mcreator.undeadrevamp.entity.TheswarmerEntity;
import net.mcreator.undeadrevamp.entity.ThewolfEntity;
import net.mcreator.undeadrevamp.entity.WINDTEXTProjectileEntity;
import net.mcreator.undeadrevamp.entity.WeakspotEntity;
import net.mcreator.undeadrevamp.entity.WitherballEntity;
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
public class UndeadRevamp2ModEntities {
   public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, "undead_revamp2");
   public static final DeferredHolder<EntityType<?>, EntityType<BomberEntity>> BOMBER = register(
      "bomber",
      Builder.of(BomberEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThespectreEntity>> THESPECTRE = register(
      "thespectre",
      Builder.of(ThespectreEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThespitterEntity>> THESPITTER = register(
      "thespitter",
      Builder.of(ThespitterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThehorrorsEntity>> THEHORRORS = register(
      "thehorrors",
      Builder.of(ThehorrorsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.5F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThehorrorsdecoysEntity>> THEHORRORSDECOYS = register(
      "thehorrorsdecoys",
      Builder.of(ThehorrorsdecoysEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.5F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThesmokerEntity>> THESMOKER = register(
      "thesmoker",
      Builder.of(ThesmokerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheMoonflowerEntity>> THE_MOONFLOWER = register(
      "the_moonflower",
      Builder.of(TheMoonflowerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(128)
         .setUpdateInterval(3)
         .sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SuckerEntity>> SUCKER = register(
      "sucker",
      Builder.of(SuckerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 0.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThehunterEntity>> THEHUNTER = register(
      "thehunter",
      Builder.of(ThehunterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).sized(0.7F, 1.3F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThebeartamerEntity>> THEBEARTAMER = register(
      "thebeartamer",
      Builder.of(ThebeartamerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.4F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThewolfEntity>> THEWOLF = register(
      "thewolf",
      Builder.of(ThewolfEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.4F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AxestromEntity>> AXESTROM = register(
      "axestrom",
      Builder.of(AxestromEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(1.0F, 0.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheordureEntity>> THEORDURE = register(
      "theordure",
      Builder.of(TheordureEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.9F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CrackleballEntity>> CRACKLEBALL = register(
      "crackleball",
      Builder.of(CrackleballEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.3F, 0.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheswarmerEntity>> THESWARMER = register(
      "theswarmer",
      Builder.of(TheswarmerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThebidyEntity>> THEBIDY = register(
      "thebidy",
      Builder.of(ThebidyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.82F, 0.45F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<INVISIBLEBIDYEntity>> INVISIBLEBIDY = register(
      "invisiblebidy",
      Builder.of(INVISIBLEBIDYEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.7F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThebidyupsideEntity>> THEBIDYUPSIDE = register(
      "thebidyupside",
      Builder.of(ThebidyupsideEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.7F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThepregnantEntity>> THEPREGNANT = register(
      "thepregnant",
      Builder.of(ThepregnantEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.9F, 2.1F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FlameEntity>> FLAME = register(
      "flame",
      Builder.of(FlameEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.1F, 0.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SlavemanEntity>> SLAVEMAN = register(
      "slaveman",
      Builder.of(SlavemanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(100).setUpdateInterval(3).sized(0.5F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TherodEntity>> THEROD = register(
      "therod",
      Builder.of(TherodEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.5F, 2.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CoppertarEntity>> COPPERTAR = register(
      "coppertar",
      Builder.of(CoppertarEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(1.0F, 0.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThegliterEntity>> THEGLITER = register(
      "thegliter",
      Builder.of(ThegliterEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).sized(0.6F, 1.98F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheheavyEntity>> THEHEAVY = register(
      "theheavy",
      Builder.of(TheheavyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CloggerEntity>> CLOGGER = register(
      "clogger",
      Builder.of(CloggerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(256).setUpdateInterval(3).sized(2.0F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<InvisicloggerEntity>> INVISICLOGGER = register(
      "invisiclogger",
      Builder.of(InvisicloggerEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(1.8F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Propball1Entity>> PROPBALL_1 = register(
      "propball_1",
      Builder.of(Propball1Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.3F, 0.2F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DeadcloggerEntity>> DEADCLOGGER = register(
      "deadclogger",
      Builder.of(DeadcloggerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(2.0F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheimmortalEntity>> THEIMMORTAL = register(
      "theimmortal",
      Builder.of(TheimmortalEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.95F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<InvisiimmortalEntity>> INVISIIMMORTAL = register(
      "invisiimmortal",
      Builder.of(InvisiimmortalEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.7F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpitterneccProjectileEntity>> SPITTERNECC_PROJECTILE = register(
      "spitternecc_projectile",
      Builder.of(SpitterneccProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ArapoholiasprayProjectileEntity>> ARAPOHOLIASPRAY_PROJECTILE = register(
      "arapoholiaspray_projectile",
      Builder.of(ArapoholiasprayProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AcidsackProjectileEntity>> ACIDSACK_PROJECTILE = register(
      "acidsack_projectile",
      Builder.of(AcidsackProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HonniethrowProjectileEntity>> HONNIETHROW_PROJECTILE = register(
      "honniethrow_projectile",
      Builder.of(HonniethrowProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.4F, 0.4F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<QueenbeeperfumeProjectileEntity>> QUEENBEEPERFUME_PROJECTILE = register(
      "queenbeeperfume_projectile",
      Builder.of(QueenbeeperfumeProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PregnantneccProjectileEntity>> PREGNANTNECC_PROJECTILE = register(
      "pregnantnecc_projectile",
      Builder.of(PregnantneccProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FIresackProjectileEntity>> F_IRESACK_PROJECTILE = register(
      "f_iresack_projectile",
      Builder.of(FIresackProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WINDTEXTProjectileEntity>> WINDTEXT_PROJECTILE = register(
      "windtext_projectile",
      Builder.of(WINDTEXTProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(3.0F, 1.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BoulderthrowProjectileEntity>> BOULDERTHROW_PROJECTILE = register(
      "boulderthrow_projectile",
      Builder.of(BoulderthrowProjectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheskeeperEntity>> THESKEEPER = register(
      "theskeeper",
      Builder.of(TheskeeperEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.9F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SkeeperthrowprojectileEntity>> SKEEPERTHROWPROJECTILE = register(
      "skeeperthrowprojectile",
      Builder.of(SkeeperthrowprojectileEntity::new, MobCategory.MISC)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThesomnolenceEntity>> THESOMNOLENCE = register(
      "thesomnolence",
      Builder.of(ThesomnolenceEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(1.2F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SleepsmokebombEntity>> SLEEPSMOKEBOMB = register(
      "sleepsmokebomb",
      Builder.of(SleepsmokebombEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SmokesmitterEntity>> SMOKESMITTER = register(
      "smokesmitter",
      Builder.of(SmokesmitterEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.6F, 1.6F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThelurkerEntity>> THELURKER = register(
      "thelurker",
      Builder.of(ThelurkerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(2.1F, 2.0F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WitherballEntity>> WITHERBALL = register(
      "witherball",
      Builder.of(WitherballEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NeocrorinesEntity>> NEOCRORINES = register(
      "neocrorines",
      Builder.of(NeocrorinesEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.5F, 0.5F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThedungeonEntity>> THEDUNGEON = register(
      "thedungeon",
      Builder.of(ThedungeonEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(128)
         .setUpdateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BigsuckerEntity>> BIGSUCKER = register(
      "bigsucker",
      Builder.of(BigsuckerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 0.7F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LecheryEntity>> LECHERY = register(
      "lechery",
      Builder.of(LecheryEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(1.0F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WeakspotEntity>> WEAKSPOT = register(
      "weakspot",
      Builder.of(WeakspotEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).sized(0.6F, 1.8F)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<InvisilehceryEntity>> INVISILEHCERY = register(
      "invisilehcery",
      Builder.of(InvisilehceryEntity::new, MobCategory.MONSTER)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(64)
         .setUpdateInterval(3)
         .sized(0.7F, 0.4F)
   );

   private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, Builder<T> entityTypeBuilder) {
      return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
   }

   @SubscribeEvent
   public static void init(RegisterSpawnPlacementsEvent event) {
      BomberEntity.init(event);
      ThespectreEntity.init(event);
      ThespitterEntity.init(event);
      ThehorrorsEntity.init(event);
      ThehorrorsdecoysEntity.init(event);
      ThesmokerEntity.init(event);
      TheMoonflowerEntity.init(event);
      SuckerEntity.init(event);
      ThehunterEntity.init(event);
      ThebeartamerEntity.init(event);
      ThewolfEntity.init(event);
      AxestromEntity.init(event);
      TheordureEntity.init(event);
      CrackleballEntity.init(event);
      TheswarmerEntity.init(event);
      ThebidyEntity.init(event);
      INVISIBLEBIDYEntity.init(event);
      ThebidyupsideEntity.init(event);
      ThepregnantEntity.init(event);
      FlameEntity.init(event);
      SlavemanEntity.init(event);
      TherodEntity.init(event);
      CoppertarEntity.init(event);
      ThegliterEntity.init(event);
      TheheavyEntity.init(event);
      CloggerEntity.init(event);
      InvisicloggerEntity.init(event);
      Propball1Entity.init(event);
      DeadcloggerEntity.init(event);
      TheimmortalEntity.init(event);
      InvisiimmortalEntity.init(event);
      TheskeeperEntity.init(event);
      ThesomnolenceEntity.init(event);
      SmokesmitterEntity.init(event);
      ThelurkerEntity.init(event);
      NeocrorinesEntity.init(event);
      ThedungeonEntity.init(event);
      BigsuckerEntity.init(event);
      LecheryEntity.init(event);
      WeakspotEntity.init(event);
      InvisilehceryEntity.init(event);
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)BOMBER.get(), BomberEntity.createAttributes().build());
      event.put((EntityType)THESPECTRE.get(), ThespectreEntity.createAttributes().build());
      event.put((EntityType)THESPITTER.get(), ThespitterEntity.createAttributes().build());
      event.put((EntityType)THEHORRORS.get(), ThehorrorsEntity.createAttributes().build());
      event.put((EntityType)THEHORRORSDECOYS.get(), ThehorrorsdecoysEntity.createAttributes().build());
      event.put((EntityType)THESMOKER.get(), ThesmokerEntity.createAttributes().build());
      event.put((EntityType)THE_MOONFLOWER.get(), TheMoonflowerEntity.createAttributes().build());
      event.put((EntityType)SUCKER.get(), SuckerEntity.createAttributes().build());
      event.put((EntityType)THEHUNTER.get(), ThehunterEntity.createAttributes().build());
      event.put((EntityType)THEBEARTAMER.get(), ThebeartamerEntity.createAttributes().build());
      event.put((EntityType)THEWOLF.get(), ThewolfEntity.createAttributes().build());
      event.put((EntityType)AXESTROM.get(), AxestromEntity.createAttributes().build());
      event.put((EntityType)THEORDURE.get(), TheordureEntity.createAttributes().build());
      event.put((EntityType)CRACKLEBALL.get(), CrackleballEntity.createAttributes().build());
      event.put((EntityType)THESWARMER.get(), TheswarmerEntity.createAttributes().build());
      event.put((EntityType)THEBIDY.get(), ThebidyEntity.createAttributes().build());
      event.put((EntityType)INVISIBLEBIDY.get(), INVISIBLEBIDYEntity.createAttributes().build());
      event.put((EntityType)THEBIDYUPSIDE.get(), ThebidyupsideEntity.createAttributes().build());
      event.put((EntityType)THEPREGNANT.get(), ThepregnantEntity.createAttributes().build());
      event.put((EntityType)FLAME.get(), FlameEntity.createAttributes().build());
      event.put((EntityType)SLAVEMAN.get(), SlavemanEntity.createAttributes().build());
      event.put((EntityType)THEROD.get(), TherodEntity.createAttributes().build());
      event.put((EntityType)COPPERTAR.get(), CoppertarEntity.createAttributes().build());
      event.put((EntityType)THEGLITER.get(), ThegliterEntity.createAttributes().build());
      event.put((EntityType)THEHEAVY.get(), TheheavyEntity.createAttributes().build());
      event.put((EntityType)CLOGGER.get(), CloggerEntity.createAttributes().build());
      event.put((EntityType)INVISICLOGGER.get(), InvisicloggerEntity.createAttributes().build());
      event.put((EntityType)PROPBALL_1.get(), Propball1Entity.createAttributes().build());
      event.put((EntityType)DEADCLOGGER.get(), DeadcloggerEntity.createAttributes().build());
      event.put((EntityType)THEIMMORTAL.get(), TheimmortalEntity.createAttributes().build());
      event.put((EntityType)INVISIIMMORTAL.get(), InvisiimmortalEntity.createAttributes().build());
      event.put((EntityType)THESKEEPER.get(), TheskeeperEntity.createAttributes().build());
      event.put((EntityType)THESOMNOLENCE.get(), ThesomnolenceEntity.createAttributes().build());
      event.put((EntityType)SMOKESMITTER.get(), SmokesmitterEntity.createAttributes().build());
      event.put((EntityType)THELURKER.get(), ThelurkerEntity.createAttributes().build());
      event.put((EntityType)NEOCRORINES.get(), NeocrorinesEntity.createAttributes().build());
      event.put((EntityType)THEDUNGEON.get(), ThedungeonEntity.createAttributes().build());
      event.put((EntityType)BIGSUCKER.get(), BigsuckerEntity.createAttributes().build());
      event.put((EntityType)LECHERY.get(), LecheryEntity.createAttributes().build());
      event.put((EntityType)WEAKSPOT.get(), WeakspotEntity.createAttributes().build());
      event.put((EntityType)INVISILEHCERY.get(), InvisilehceryEntity.createAttributes().build());
   }
}
