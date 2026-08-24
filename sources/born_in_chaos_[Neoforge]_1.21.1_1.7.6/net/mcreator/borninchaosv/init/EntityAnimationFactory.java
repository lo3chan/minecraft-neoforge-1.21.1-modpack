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
import net.mcreator.borninchaosv.entity.MissionaryRaiderEntity;
import net.mcreator.borninchaosv.entity.MissionerEntity;
import net.mcreator.borninchaosv.entity.MotherSpiderEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinControlledEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinEntity;
import net.mcreator.borninchaosv.entity.MrsPumpkinEntity;
import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.mcreator.borninchaosv.entity.PhantomCreeperCopyEntity;
import net.mcreator.borninchaosv.entity.PhantomCreeperEntity;
import net.mcreator.borninchaosv.entity.PumpkinBombEntity;
import net.mcreator.borninchaosv.entity.PumpkinBruiserEntity;
import net.mcreator.borninchaosv.entity.PumpkinDunceEntity;
import net.mcreator.borninchaosv.entity.PumpkinSpiritEntity;
import net.mcreator.borninchaosv.entity.PumpkinheadEntity;
import net.mcreator.borninchaosv.entity.RestlessSpiritEntity;
import net.mcreator.borninchaosv.entity.RidingFelsteedEntity;
import net.mcreator.borninchaosv.entity.RidingLordsFelsteedEntity;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber
public class EntityAnimationFactory {
   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event != null && event.getEntity() != null) {
         if (event.getEntity() instanceof DecrepitSkeletonEntity syncable) {
            String animation = syncable.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncable.setAnimation("undefined");
               syncable.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SkeletonDemomanEntity syncablex) {
            String animation = syncablex.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablex.setAnimation("undefined");
               syncablex.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DecayingZombieEntity syncablexx) {
            String animation = syncablexx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexx.setAnimation("undefined");
               syncablexx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BabySkeletonEntity syncablexxx) {
            String animation = syncablexxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxx.setAnimation("undefined");
               syncablexxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BabySkeletonMinionEntity syncablexxxx) {
            String animation = syncablexxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxx.setAnimation("undefined");
               syncablexxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ControlledBabySkeletonEntity syncablexxxxx) {
            String animation = syncablexxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxx.setAnimation("undefined");
               syncablexxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BarrelZombieEntity syncablexxxxxx) {
            String animation = syncablexxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxx.setAnimation("undefined");
               syncablexxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PhantomCreeperEntity syncablexxxxxxx) {
            String animation = syncablexxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxx.setAnimation("undefined");
               syncablexxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PhantomCreeperCopyEntity syncablexxxxxxxx) {
            String animation = syncablexxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DoorKnightEntity syncablexxxxxxxxx) {
            String animation = syncablexxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof RestlessSpiritEntity syncablexxxxxxxxxx) {
            String animation = syncablexxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SkeletonThrasherEntity syncablexxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PumpkinSpiritEntity syncablexxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SearedSpiritEntity syncablexxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FirelightEntity syncablexxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DiamondThermiteEntity syncablexxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BonescallerEntity syncablexxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BoneImpEntity syncablexxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BoneImpMinionEntity syncablexxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SupremeBonescallerEntity syncablexxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SupremeBonescallerStage2Entity syncablexxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DarkVortexEntity syncablexxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DreadHoundEntity syncablexxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FallenChaosKnightEntity syncablexxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ScarletPersecutorEntity syncablexxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SpiritGuideEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SpiritGuideAssistantEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ZombieClownEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MrPumpkinEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MrPumpkinControlledEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SirPumpkinheadEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FelsteedEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SirPumpkinheadWithoutHorseEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SirTheHeadlessEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PumpkinheadEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof RidingFelsteedEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DecayingZombieNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DoorKnightNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BonescallerNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SupremeBonescallerNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SkeletonThrasherNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DreadHoundNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FirelightNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ZombieClownNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SearedSpiritNotDespawnEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DireHoundLeaderEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MaggotEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CorpseFlyEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BloodyGadflyEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CorpseFishEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ZombieFishermanEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SwarmerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThornshellCrabEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof GluttonFishEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ZombieBruiserEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PumpkinBombEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SenorPumpkinEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LordPumpkinheadEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LordsFelsteedEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LordPumpkinheadWithoutaHorseEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LordTheHeadlessEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LordPumpkinheadHeadEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof InfernalSpiritEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof RidingLordsFelsteedEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ZombieLumberjackEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SiameseSkeletonsEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SiameseSkeletonsleftEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SiameseSkeletonsrightEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MissionerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ControlledSpiritualAssistantEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SpiritofChaosEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LifestealerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LifestealerTrueFormEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BabySpiderEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MotherSpiderEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BabySpiderControlledEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NightmareStalkerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PumpkinDunceEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MrsPumpkinEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PumpkinBruiserEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof KrampusEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof KrampusHenchmanEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MissionaryRaiderEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }
      }
   }
}
