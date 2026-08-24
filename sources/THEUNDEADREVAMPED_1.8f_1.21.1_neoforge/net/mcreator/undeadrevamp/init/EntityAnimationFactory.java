package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.mcreator.undeadrevamp.entity.BigsuckerEntity;
import net.mcreator.undeadrevamp.entity.BomberEntity;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.entity.CrackleballEntity;
import net.mcreator.undeadrevamp.entity.DeadcloggerEntity;
import net.mcreator.undeadrevamp.entity.INVISIBLEBIDYEntity;
import net.mcreator.undeadrevamp.entity.InvisicloggerEntity;
import net.mcreator.undeadrevamp.entity.InvisiimmortalEntity;
import net.mcreator.undeadrevamp.entity.InvisilehceryEntity;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.entity.Propball1Entity;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber
public class EntityAnimationFactory {
   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event != null && event.getEntity() != null) {
         if (event.getEntity() instanceof BomberEntity syncable) {
            String animation = syncable.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncable.setAnimation("undefined");
               syncable.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThespectreEntity syncablex) {
            String animation = syncablex.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablex.setAnimation("undefined");
               syncablex.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThespitterEntity syncablexx) {
            String animation = syncablexx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexx.setAnimation("undefined");
               syncablexx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThehorrorsEntity syncablexxx) {
            String animation = syncablexxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxx.setAnimation("undefined");
               syncablexxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThehorrorsdecoysEntity syncablexxxx) {
            String animation = syncablexxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxx.setAnimation("undefined");
               syncablexxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThesmokerEntity syncablexxxxx) {
            String animation = syncablexxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxx.setAnimation("undefined");
               syncablexxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheMoonflowerEntity syncablexxxxxx) {
            String animation = syncablexxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxx.setAnimation("undefined");
               syncablexxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SuckerEntity syncablexxxxxxx) {
            String animation = syncablexxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxx.setAnimation("undefined");
               syncablexxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThehunterEntity syncablexxxxxxxx) {
            String animation = syncablexxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThebeartamerEntity syncablexxxxxxxxx) {
            String animation = syncablexxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThewolfEntity syncablexxxxxxxxxx) {
            String animation = syncablexxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof AxestromEntity syncablexxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheordureEntity syncablexxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CrackleballEntity syncablexxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheswarmerEntity syncablexxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThebidyEntity syncablexxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof INVISIBLEBIDYEntity syncablexxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThebidyupsideEntity syncablexxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThepregnantEntity syncablexxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TherodEntity syncablexxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThegliterEntity syncablexxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheheavyEntity syncablexxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CloggerEntity syncablexxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof InvisicloggerEntity syncablexxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof Propball1Entity syncablexxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof DeadcloggerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheimmortalEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof InvisiimmortalEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TheskeeperEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThesomnolenceEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThelurkerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ThedungeonEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BigsuckerEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LecheryEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof InvisilehceryEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }
      }
   }
}
