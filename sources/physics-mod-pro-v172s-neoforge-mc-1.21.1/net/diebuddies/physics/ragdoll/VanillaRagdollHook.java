package net.diebuddies.physics.ragdoll;

import java.util.Iterator;
import java.util.List;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.physics.PhysicsEntity;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Vindicator;

public class VanillaRagdollHook implements RagdollHook {
   @Override
   public void map(Ragdoll ragdoll, Entity entity, EntityModel model) {
      if (model instanceof PiglinModel) {
         AgeableListModel animal = (AgeableListModel)model;
         PiglinModel piglin = (PiglinModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter, true);
         }

         int headOffset = 0;
         int bodyOffset = counter.count;
         Iterator<ModelPart> body = ReflectionsForge.bodyParts(animal).iterator();
         int rightArmOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int leftArmOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int rightLegOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int leftLegOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         ragdoll.addConnection(4, headOffset);
         ragdoll.addConnection(5, headOffset);
         ragdoll.addConnection(headOffset, bodyOffset);
         ragdoll.addConnection(rightArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(leftArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(rightLegOffset, bodyOffset);
         ragdoll.addConnection(leftLegOffset, bodyOffset);
         int hatOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int leftPantsOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int rightPantsOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int leftSleeveOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int rightSleeveOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         int jacketOffset = RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         if (piglin.hat.visible) {
            ragdoll.addConnection(hatOffset, headOffset, true, true);
         }

         if (piglin.leftPants.visible) {
            ragdoll.addConnection(leftPantsOffset, leftLegOffset, true, true);
         }

         if (piglin.rightPants.visible) {
            ragdoll.addConnection(rightPantsOffset, rightLegOffset, true, true);
         }

         if (piglin.leftSleeve.visible) {
            ragdoll.addConnection(leftSleeveOffset, leftArmOffset, true, true);
         }

         if (piglin.rightSleeve.visible) {
            ragdoll.addConnection(rightSleeveOffset, rightArmOffset, true, true);
         }

         if (piglin.jacket.visible) {
            ragdoll.addConnection(jacketOffset, bodyOffset, true, true);
         }

         while (body.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, body.next(), counter, true);
         }
      } else if (model instanceof VexModel) {
         HierarchicalModel animal = (HierarchicalModel)model;
         int headOffsetx = 0;
         int bodyOffsetx = 1;
         int clothOffset = 2;
         int rightArmOffsetx = 3;
         int leftArmOffsetx = 4;
         int rightWingOffset = 5;
         int leftWingOffset = 6;
         ragdoll.addConnection(headOffsetx, bodyOffsetx).stopCollision = true;
         ragdoll.addConnection(clothOffset, bodyOffsetx, true);
         ragdoll.addConnection(rightArmOffsetx, bodyOffsetx).stopCollision = true;
         ragdoll.addConnection(leftArmOffsetx, bodyOffsetx).stopCollision = true;
         ragdoll.addConnection(rightWingOffset, bodyOffsetx, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetx, true);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof HumanoidModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         }

         int headOffsetx = 0;
         int bodyOffsetx = counter.count;
         Iterator<ModelPart> bodyx = ReflectionsForge.bodyParts(animal).iterator();
         int rightArmOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         int leftArmOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         int rightLegOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         int leftLegOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         ragdoll.addConnection(headOffsetx, bodyOffsetx);
         ragdoll.addConnection(rightArmOffsetx, bodyOffsetx);
         ragdoll.addConnection(leftArmOffsetx, bodyOffsetx);
         ragdoll.addConnection(rightLegOffsetx, bodyOffsetx);
         ragdoll.addConnection(leftLegOffsetx, bodyOffsetx);
         int hatOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         if (((HumanoidModel)model).hat.visible) {
            ragdoll.addConnection(hatOffsetx, headOffsetx, true, true);
         }

         if (model instanceof PlayerModel playerModel) {
            try {
               int leftPantsOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int rightPantsOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int leftSleeveOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int rightSleeveOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int jacketOffsetx = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               if (playerModel.leftPants.visible) {
                  ragdoll.addConnection(leftPantsOffsetx, leftLegOffsetx, true, true);
               }

               if (playerModel.rightPants.visible) {
                  ragdoll.addConnection(rightPantsOffsetx, rightLegOffsetx, true, true);
               }

               if (playerModel.leftSleeve.visible) {
                  ragdoll.addConnection(leftSleeveOffsetx, leftArmOffsetx, true, true);
               }

               if (playerModel.rightSleeve.visible) {
                  ragdoll.addConnection(rightSleeveOffsetx, rightArmOffsetx, true, true);
               }

               if (playerModel.jacket.visible) {
                  ragdoll.addConnection(jacketOffsetx, bodyOffsetx, true, true);
               }
            } catch (Exception var41) {
            }
         } else if (model instanceof ArmorStandModel) {
            try {
               int rightBodyStickOffset = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int leftBodyStickOffset = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int shoulderStickOffset = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               int basePlateOffset = RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
               ragdoll.addConnection(rightBodyStickOffset, bodyOffsetx, true);
               ragdoll.addConnection(leftBodyStickOffset, bodyOffsetx, true);
               ragdoll.addConnection(shoulderStickOffset, bodyOffsetx, true);
            } catch (Exception var40) {
            }
         } else if (model instanceof BoggedModel) {
            boolean hasBow = false;
            boolean sheared = false;
            if (entity instanceof Bogged bogged) {
               sheared = bogged.isSheared();
            }

            for (int i = 0; i < ragdoll.bodies.size(); i++) {
               PhysicsEntity b = ragdoll.bodies.get(i);
               if (b.feature instanceof ItemInHandLayer) {
                  ragdoll.addConnection(i, rightArmOffsetx, true);
                  hasBow = true;
               }
            }

            int count = RagdollMapper.countModelParts(entity, model);
            if (sheared) {
               if (count + (hasBow ? 1 : 0) < ragdoll.bodies.size()) {
                  ragdoll.addOverlayConnections(true, count * 2, hasBow ? 1 : 0);
               }
            } else {
               ragdoll.addConnection(13, 0, true, true);
               ragdoll.addConnection(14, 7, true, true);
               ragdoll.addConnection(15, 8, true, true);
               ragdoll.addConnection(16, 9, true, true);
               ragdoll.addConnection(17, 10, true, true);
               ragdoll.addConnection(18, 11, true, true);
               ragdoll.addConnection(19, 12, true, true);
               ragdoll.addConnection(1, 0, true, true);
               ragdoll.addConnection(2, 0, true, true);
               ragdoll.addConnection(3, 0, true, true);
               ragdoll.addConnection(4, 0, true, true);
               ragdoll.addConnection(5, 0, true, true);
               ragdoll.addConnection(6, 0, true, true);
            }
         } else if (!(model instanceof SkeletonModel)) {
            if (model instanceof DrownedModel) {
               int count = RagdollMapper.countModelParts(entity, model);
               if (ragdoll.bodies.size() > count * 2) {
                  ragdoll.addOverlayConnections(true, 14, 5);
                  int base = 7;
                  int spike1 = 8;
                  int spike2 = 9;
                  int spike3 = 10;
                  int base2 = 11;
                  ragdoll.addConnection(base2, base, true);
                  ragdoll.addConnection(spike1, base, true);
                  ragdoll.addConnection(spike2, base, true);
                  ragdoll.addConnection(spike3, base, true);
               } else if (count < ragdoll.bodies.size()) {
                  ragdoll.addOverlayConnections(true);
               }
            } else if (model instanceof EndermanModel) {
               ragdoll.addOverlayConnections(true);
            } else if (model instanceof ZombieVillagerModel) {
               int count = RagdollMapper.countModelParts(entity, model);
               if (count < ragdoll.bodies.size()) {
                  int nbodyOffset = 0;
                  int nrightArmOffset = 2;
                  int nleftArmOffset = 3;
                  int nrightLegOffset = 4;
                  int nleftLegOffset = 5;
                  int overlays = (int)Math.ceil((double)ragdoll.bodies.size() / count);
                  boolean hasHat = ragdoll.bodies.size() % count != 0;

                  for (int ix = 1; ix < overlays; ix++) {
                     int offset = count * ix + (ix != 1 ? (hasHat ? -4 : 0) : 0);
                     if (ix == 1 && hasHat) {
                        ragdoll.addConnection(nbodyOffset + offset, bodyOffsetx, true, true);
                        ragdoll.addConnection(nbodyOffset + 1 + offset, bodyOffsetx, true, true);
                        ragdoll.addConnection(nleftArmOffset + offset, leftArmOffsetx, true, true);
                        ragdoll.addConnection(nrightArmOffset + offset, rightArmOffsetx, true, true);
                        ragdoll.addConnection(nrightLegOffset + offset, rightLegOffsetx, true, true);
                        ragdoll.addConnection(nleftLegOffset + offset, leftLegOffsetx, true, true);
                     } else {
                        ragdoll.addConnection(headOffsetx + offset, headOffsetx, true, true);
                        ragdoll.addConnection(headOffsetx + 1 + offset, headOffsetx, true, true);
                        ragdoll.addConnection(bodyOffsetx + offset, bodyOffsetx, true, true);
                        ragdoll.addConnection(bodyOffsetx + 1 + offset, bodyOffsetx, true, true);
                        ragdoll.addConnection(leftArmOffsetx + offset, leftArmOffsetx, true, true);
                        ragdoll.addConnection(rightArmOffsetx + offset, rightArmOffsetx, true, true);
                        ragdoll.addConnection(rightLegOffsetx + offset, rightLegOffsetx, true, true);
                        ragdoll.addConnection(leftLegOffsetx + offset, leftLegOffsetx, true, true);
                        ragdoll.addConnection(hatOffsetx + offset, headOffsetx, true, true);
                        ragdoll.addConnection(hatOffsetx + 1 + offset, headOffsetx, true, true);
                     }
                  }
               }
            }
         } else {
            boolean hasBowx = false;

            for (int ixx = 0; ixx < ragdoll.bodies.size(); ixx++) {
               PhysicsEntity b = ragdoll.bodies.get(ixx);
               if (b.feature instanceof ItemInHandLayer) {
                  ragdoll.addConnection(ixx, rightArmOffsetx, true);
                  hasBowx = true;
               }
            }

            int count = RagdollMapper.countModelParts(entity, model);
            if (count + (hasBowx ? 1 : 0) < ragdoll.bodies.size()) {
               ragdoll.addOverlayConnections(true, count * 2, hasBowx ? 1 : 0);
            }
         }

         while (bodyx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyx.next(), counter);
         }
      } else if (model instanceof QuadrupedModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();
         int headOffsetxx = 0;
         if (model instanceof GoatModel) {
            counter.count = 6;
            headOffsetxx = 3;
            ragdoll.addConnection(0, headOffsetxx, true);
            ragdoll.addConnection(1, headOffsetxx, true);
            ragdoll.addConnection(2, headOffsetxx, true);
            ragdoll.addConnection(4, headOffsetxx, true);
            ragdoll.addConnection(5, headOffsetxx, true);
         } else {
            while (head.hasNext()) {
               RagdollMapper.getCuboids(ragdoll, head.next(), counter);
            }
         }

         int bodyOffsetxx = counter.count;
         Iterator<ModelPart> bodyxx = ReflectionsForge.bodyParts(animal).iterator();
         int rightArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int rightLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);

         while (bodyxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }
      } else if (model instanceof ChickenModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();
         int headOffsetxx = 0;
         int beakOffset = RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         int wattleOffset = RagdollMapper.getCuboids(ragdoll, head.next(), counter);

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         }

         Iterator<ModelPart> bodyxx = ReflectionsForge.bodyParts(animal).iterator();
         int bodyOffsetxx = counter.count;
         int rightLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int rightWingOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftWingOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         ragdoll.addConnection(beakOffset, headOffsetxx, true);
         ragdoll.addConnection(wattleOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx);

         while (bodyxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }
      } else if (model instanceof WolfModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();
         int headOffsetxx = 0;

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         }

         Iterator<ModelPart> bodyxx = ReflectionsForge.bodyParts(animal).iterator();
         int bodyOffsetxx = counter.count;
         int rightFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int rightHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int tailOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int neckOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         ragdoll.addConnection(headOffsetxx, neckOffset);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         ragdoll.addConnection(neckOffset, bodyOffsetxx);

         while (bodyxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }
      } else if (model instanceof SquidModel) {
         ragdoll.addConnection(0, 4);
         ragdoll.addConnection(1, 4);
         ragdoll.addConnection(2, 4);
         ragdoll.addConnection(3, 4);
         ragdoll.addConnection(5, 4);
         ragdoll.addConnection(6, 4);
         ragdoll.addConnection(7, 4);
         ragdoll.addConnection(8, 4);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof CreeperModel) {
         int headOffsetxx = 0;
         int rightArmOffsetxx = 1;
         int rightLegOffsetxx = 2;
         int leftLegOffsetxx = 3;
         int bodyOffsetxx = 4;
         int leftArmOffsetxx = 5;
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof DolphinModel) {
         int bodyOffsetxx = 0;
         int headOffsetxx = 1;
         int noseOffset = 2;
         int leftFinOffset = 3;
         int rightFinOffset = 4;
         int tailOffset = 5;
         int tailFinOffset = 6;
         int backFinOffset = 7;
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(backFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         ragdoll.addConnection(tailFinOffset, tailOffset);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof GhastModel) {
         ragdoll.addConnection(0, 5);
         ragdoll.addConnection(1, 5);
         ragdoll.addConnection(2, 5);
         ragdoll.addConnection(3, 5);
         ragdoll.addConnection(4, 5);
         ragdoll.addConnection(6, 5);
         ragdoll.addConnection(7, 5);
         ragdoll.addConnection(8, 5);
         ragdoll.addConnection(9, 5);
      } else if (model instanceof IronGolemModel) {
         int headOffsetxx = 0;
         int noseOffset = 1;
         int rightArmOffsetxx = 2;
         int leftLegOffsetxx = 3;
         int leftArmOffsetxx = 4;
         int rightLegOffsetxx = 5;
         int bodyOffsetxx = 6;
         int lowerBodyOffset = 7;
         ragdoll.addConnection(headOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(leftArmOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(rightArmOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(leftLegOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(rightLegOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(bodyOffsetxx, lowerBodyOffset, true);
      } else if (model instanceof SpiderModel) {
         int headOffsetxx = 0;
         int rightFrontLegOffset = 1;
         int rightHindLegOffset = 2;
         int leftMiddleFrontLegOffset = 3;
         int body0Offset = 4;
         int body1Offset = 5;
         int leftHindLegOffset = 6;
         int rightMiddleHindLegOffset = 7;
         int rightMiddleFrontLegOffset = 8;
         int leftMiddleHindLegOffset = 9;
         int leftFrontLegOffset = 10;
         ragdoll.addConnection(headOffsetxx, body0Offset);
         ragdoll.addConnection(body1Offset, body0Offset);
         ragdoll.addConnection(rightFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftMiddleFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightMiddleHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightMiddleFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftMiddleHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftFrontLegOffset, body0Offset).stopCollision = true;
      } else if (model instanceof SnowGolemModel) {
         int headOffsetxx = 0;
         int rightArmOffsetxx = 1;
         int upperBodyOffset = 2;
         int leftArmOffsetxx = 3;
         int lowerBodyOffset = 4;
         int pumpkinOffset = 5;
         ragdoll.addConnection(headOffsetxx, upperBodyOffset);
         ragdoll.addConnection(rightArmOffsetxx, upperBodyOffset);
         ragdoll.addConnection(leftArmOffsetxx, upperBodyOffset);
         ragdoll.addConnection(upperBodyOffset, lowerBodyOffset);
         if (ragdoll.bodies.size() == 6) {
            ragdoll.addConnection(pumpkinOffset, headOffsetxx, true);
         }
      } else if (model instanceof GuardianModel) {
         int headOffsetxx = 0;
         int spike0 = 21;
         int spike1 = 5;
         int spike2 = 6;
         int spike3 = 7;
         int spike4 = 8;
         int spike5 = 9;
         int spike6 = 10;
         int spike7 = 16;
         int spike8 = 17;
         int spike9 = 18;
         int spike10 = 19;
         int spike11 = 20;
         int eye = 11;
         int tail0 = 12;
         int tail1 = 13;
         int tail2 = 14;
         int tail3 = 15;
         ragdoll.addConnection(tail0, headOffsetxx, true);
         ragdoll.addConnection(tail1, headOffsetxx, true);
         ragdoll.addConnection(tail2, headOffsetxx, true);
         ragdoll.addConnection(tail3, headOffsetxx, true);
         ragdoll.addConnection(spike0, headOffsetxx, true);
         ragdoll.addConnection(spike1, headOffsetxx, true);
         ragdoll.addConnection(spike2, headOffsetxx, true);
         ragdoll.addConnection(spike3, headOffsetxx, true);
         ragdoll.addConnection(spike4, headOffsetxx, true);
         ragdoll.addConnection(spike5, headOffsetxx, true);
         ragdoll.addConnection(spike6, headOffsetxx, true);
         ragdoll.addConnection(spike7, headOffsetxx, true);
         ragdoll.addConnection(spike8, headOffsetxx, true);
         ragdoll.addConnection(spike9, headOffsetxx, true);
         ragdoll.addConnection(spike10, headOffsetxx, true);
         ragdoll.addConnection(spike11, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 1, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 2, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 3, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 4, headOffsetxx, true);
         ragdoll.addConnection(eye, headOffsetxx, true);
      } else if (model instanceof WitchModel) {
         int headOffsetxx = 0;
         int noseOffset = 1;
         int moleOffset = 2;
         int hatOffsetxx = 3;
         int hatRimOffset = 4;
         int hat2Offset = 5;
         int hat3Offset = 6;
         int hat4Offset = 7;
         int leftLegOffsetxx = 8;
         int rightLegOffsetxx = 9;
         int armsOffset = 10;
         int bodyOffsetxx = 13;
         int jacketOffsetxx = 14;
         ragdoll.addConnection(moleOffset, headOffsetxx, true);
         ragdoll.addConnection(hat2Offset, headOffsetxx, true);
         ragdoll.addConnection(hat3Offset, headOffsetxx, true);
         ragdoll.addConnection(hat4Offset, headOffsetxx, true);
         ragdoll.addConnection(hatOffsetxx, headOffsetxx, true);
         ragdoll.addConnection(hatRimOffset, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(jacketOffsetxx, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 2, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(armsOffset, bodyOffsetxx);
      } else if (model instanceof VillagerModel) {
         int headOffsetxx = 0;
         int noseOffset = 1;
         int hatOffsetxx = 2;
         int hatRimOffset = 3;
         int leftLegOffsetxx = 4;
         int rightLegOffsetxx = 5;
         int armsOffset = 6;
         int bodyOffsetxx = 9;
         int jacketOffsetxx = 10;
         ragdoll.addConnection(hatOffsetxx, headOffsetxx, true);
         ragdoll.addConnection(hatRimOffset, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(jacketOffsetxx, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 2, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(armsOffset, bodyOffsetxx);
         int count = RagdollMapper.countModelParts(entity, model);
         if (count < ragdoll.bodies.size()) {
            int nleftLegOffset = 0;
            int nrightLegOffset = 1;
            int narmsOffset = 2;
            int nbodyOffset = 5;
            int njacketOffset = 6;
            int overlays = (int)Math.ceil((double)ragdoll.bodies.size() / count);
            boolean hasHat = ragdoll.bodies.size() % count != 0;

            for (int ixxx = 1; ixxx < overlays; ixxx++) {
               int offset = count * ixxx + (ixxx != 1 ? (hasHat ? -4 : 0) : 0);
               if (ixxx == 1 && hasHat) {
                  ragdoll.addConnection(nbodyOffset + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(njacketOffset + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(narmsOffset + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(narmsOffset + 1 + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(narmsOffset + 2 + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(nrightLegOffset + offset, rightLegOffsetxx, true, true);
                  ragdoll.addConnection(nleftLegOffset + offset, leftLegOffsetxx, true, true);
               } else {
                  ragdoll.addConnection(hatOffsetxx + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(hatRimOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(noseOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(headOffsetxx + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(bodyOffsetxx + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(jacketOffsetxx + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(armsOffset + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(armsOffset + 1 + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(armsOffset + 2 + offset, bodyOffsetxx, true, true);
                  ragdoll.addConnection(rightLegOffsetxx + offset, rightLegOffsetxx, true, true);
                  ragdoll.addConnection(leftLegOffsetxx + offset, leftLegOffsetxx, true, true);
               }
            }
         }
      } else if (model instanceof IllagerModel) {
         if (entity instanceof Illusioner) {
            int headOffsetxx = 0;
            int noseOffset = 1;
            int hatOffsetxx = 2;
            int leftLegOffsetxx = 3;
            int rightLegOffsetxx = 4;
            int arms1Offset = 5;
            int arms2Offset = 6;
            int leftShoulderOffset = 7;
            int body1Offset = 8;
            int body2Offset = 9;
            ragdoll.addConnection(hatOffsetxx, headOffsetxx, true);
            ragdoll.addConnection(noseOffset, headOffsetxx, true);
            ragdoll.addConnection(headOffsetxx, body1Offset);
            ragdoll.addConnection(leftLegOffsetxx, body1Offset);
            ragdoll.addConnection(arms1Offset, body1Offset, true);
            ragdoll.addConnection(rightLegOffsetxx, body1Offset);
            ragdoll.addConnection(arms2Offset, body1Offset, true);
            ragdoll.addConnection(leftShoulderOffset, body1Offset, true);
            ragdoll.addConnection(body2Offset, body1Offset, true);
         } else if (!(entity instanceof Evoker) && !(entity instanceof Vindicator)) {
            int headOffsetxx = 0;
            int noseOffset = 1;
            int leftLegOffsetxx = 2;
            int rightArmOffsetxx = 3;
            int rightLegOffsetxx = 4;
            int leftArmOffsetxx = 5;
            int bodyOffsetxx = 6;
            ragdoll.addConnection(noseOffset, headOffsetxx, true);
            ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(bodyOffsetxx + 1, bodyOffsetxx, true);
         } else {
            int headOffsetxx = 0;
            int noseOffset = 1;
            int leftLegOffsetxx = 2;
            int rightLegOffsetxx = 3;
            int arms1Offset = 4;
            int arms2Offset = 5;
            int leftShoulderOffset = 6;
            int body1Offset = 7;
            int body2Offset = 8;
            ragdoll.addConnection(noseOffset, headOffsetxx, true);
            ragdoll.addConnection(headOffsetxx, body1Offset);
            ragdoll.addConnection(leftLegOffsetxx, body1Offset);
            ragdoll.addConnection(arms1Offset, body1Offset, true);
            ragdoll.addConnection(rightLegOffsetxx, body1Offset);
            ragdoll.addConnection(arms2Offset, body1Offset, true);
            ragdoll.addConnection(leftShoulderOffset, body1Offset, true);
            ragdoll.addConnection(body2Offset, body1Offset, true);
         }

         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof StriderModel) {
         int leftLegOffsetxx = 0;
         int rightLegOffsetxx = 1;
         int bodyOffsetxx = 2;
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(3, bodyOffsetxx, true);
         ragdoll.addConnection(4, bodyOffsetxx, true);
         ragdoll.addConnection(5, bodyOffsetxx, true);
         ragdoll.addConnection(6, bodyOffsetxx, true);
         ragdoll.addConnection(7, bodyOffsetxx, true);
         ragdoll.addConnection(8, bodyOffsetxx, true);
         ragdoll.bodies.get(bodyOffsetxx).backfaceCulling = false;
      } else if (model instanceof RavagerModel) {
         int rightFrontLegOffset = 0;
         int rightHindLegOffset = 1;
         int leftHindLegOffset = 2;
         int neckOffset = 3;
         int headOffsetxx = 4;
         int headChildOffset = 5;
         int rightHornOffset = 6;
         int mouthOffset = 7;
         int leftHornOffset = 8;
         int bodyOffsetxx = 9;
         int bodyChildOffset = 10;
         int leftFrontLegOffset = 11;
         ragdoll.addConnection(bodyChildOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(neckOffset, headOffsetxx, true);
         ragdoll.addConnection(mouthOffset, headOffsetxx, true);
         ragdoll.addConnection(rightHornOffset, headOffsetxx, true);
         ragdoll.addConnection(leftHornOffset, headOffsetxx, true);
         ragdoll.addConnection(headChildOffset, headOffsetxx, true);
      } else if (model instanceof BatModel) {
         int headOffsetxx = 0;
         int rightEarOffset = 1;
         int leftEarOffset = 2;
         int bodyOffsetxx = 3;
         int bodyChildOffset = 4;
         int rightWingOffset = 5;
         int rightWingTipOffset = 6;
         int leftWingOffset = 7;
         int leftWingTipOffset = 8;
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(bodyChildOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxx, true);
         ragdoll.addConnection(leftEarOffset, headOffsetxx, true);
         ragdoll.addConnection(rightWingTipOffset, rightWingOffset, true);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx);
         ragdoll.addConnection(leftWingTipOffset, leftWingOffset, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx);
      } else if (model instanceof BeeModel) {
         int frontLegsOffset = 0;
         int rightWingOffset = 1;
         int leftWingOffset = 2;
         int middleLegsOffset = 3;
         int leftAntennaOffset = 4;
         int rightAntennaOffset = 5;
         int stingerOffset = 6;
         int bodyOffsetxx = 7;
         int backLegsOffset = 8;
         ragdoll.addConnection(frontLegsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx, true);
         ragdoll.addConnection(middleLegsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightAntennaOffset, bodyOffsetxx, true);
         ragdoll.addConnection(stingerOffset, bodyOffsetxx, true);
         ragdoll.addConnection(leftAntennaOffset, bodyOffsetxx, true);
         ragdoll.addConnection(backLegsOffset, bodyOffsetxx, true);
      } else if (model instanceof RabbitModel) {
         int headOffsetxx;
         int noseOffset;
         int rightFrontLegOffset;
         int rightHindFootOffset;
         int tailOffset;
         int leftHaunchOffset;
         int rightHaunchOffset;
         int bodyOffsetxx;
         int rightEarOffset;
         int leftFrontLegOffset;
         int leftHindFootOffset;
         int leftEarOffset;
         if (model.young) {
            headOffsetxx = 0;
            leftEarOffset = 1;
            rightEarOffset = 2;
            noseOffset = 3;
            leftHindFootOffset = 4;
            rightHindFootOffset = 5;
            leftHaunchOffset = 6;
            rightHaunchOffset = 7;
            bodyOffsetxx = 8;
            leftFrontLegOffset = 9;
            rightFrontLegOffset = 10;
            tailOffset = 11;
         } else {
            leftHindFootOffset = 0;
            rightHindFootOffset = 1;
            leftHaunchOffset = 2;
            rightHaunchOffset = 3;
            bodyOffsetxx = 4;
            leftFrontLegOffset = 5;
            rightFrontLegOffset = 6;
            headOffsetxx = 7;
            rightEarOffset = 8;
            leftEarOffset = 9;
            tailOffset = 10;
            noseOffset = 11;
         }

         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftEarOffset, headOffsetxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightHaunchOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHaunchOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindFootOffset, rightHaunchOffset, true);
         ragdoll.addConnection(leftHindFootOffset, leftHaunchOffset, true);
      } else if (model instanceof WitherBossModel) {
         int shouldersOffset = 0;
         int ribcageOffset = 1;
         int tailOffset = 5;
         int leftHeadOffset = 6;
         int rightHeadOffset = 7;
         int centerHeadOffset = 8;
         ragdoll.addConnection(tailOffset, ribcageOffset);
         ragdoll.addConnection(shouldersOffset, ribcageOffset, true);
         ragdoll.addConnection(leftHeadOffset, ribcageOffset);
         ragdoll.addConnection(rightHeadOffset, ribcageOffset);
         ragdoll.addConnection(centerHeadOffset, ribcageOffset);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof OcelotModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         }

         int headOffsetxx = 0;
         int bodyOffsetxx = counter.count;
         Iterator<ModelPart> bodyxx = ReflectionsForge.bodyParts(animal).iterator();
         int rightArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int rightLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int upperTailOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int lowerTailOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(upperTailOffset, bodyOffsetxx);
         ragdoll.addConnection(lowerTailOffset, upperTailOffset);

         while (bodyxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }
      } else if (model instanceof FoxModel) {
         int headOffsetxx = 0;
         int noseOffset = 1;
         int rightEarOffset = 2;
         int leftEarOffset = 3;
         int bodyOffsetxx = 4;
         int tailOffset = 5;
         int leg1Offset = 6;
         int leg2Offset = 7;
         int leg3Offset = 8;
         int leg4Offset = 9;
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxx, true);
         ragdoll.addConnection(leftEarOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         ragdoll.addConnection(leg1Offset, bodyOffsetxx);
         ragdoll.addConnection(leg2Offset, bodyOffsetxx);
         ragdoll.addConnection(leg3Offset, bodyOffsetxx);
         ragdoll.addConnection(leg4Offset, bodyOffsetxx);
      } else if (model instanceof SilverfishModel) {
         int segment2Offset = 0;
         int segment1Offset = 1;
         int segment0Offset = 2;
         int layer0Offset = 3;
         int layer1Offset = 4;
         int layer2Offset = 5;
         int segment6Offset = 6;
         int segment5Offset = 7;
         int segment4Offset = 8;
         int segment3Offset = 9;
         ragdoll.addConnection(segment0Offset, segment1Offset);
         ragdoll.addConnection(segment1Offset, segment2Offset);
         ragdoll.addConnection(segment2Offset, segment3Offset);
         ragdoll.addConnection(segment3Offset, segment4Offset);
         ragdoll.addConnection(segment4Offset, segment5Offset);
         ragdoll.addConnection(segment5Offset, segment6Offset);
         ragdoll.addConnection(layer0Offset, segment2Offset, true);
         ragdoll.addConnection(layer1Offset, segment4Offset, true);
         ragdoll.addConnection(layer2Offset, segment1Offset, true);
      } else if (model instanceof EndermiteModel) {
         int segment2Offset = 0;
         int segment1Offset = 1;
         int segment0Offset = 2;
         int segment3Offset = 3;
         ragdoll.addConnection(segment0Offset, segment1Offset);
         ragdoll.addConnection(segment1Offset, segment2Offset);
         ragdoll.addConnection(segment2Offset, segment3Offset);
      } else if (model instanceof ParrotModel) {
         int headOffsetxx = 0;
         int beak1Offset = 1;
         int beak2Offset = 2;
         int featherOffset = 3;
         int head2Offset = 4;
         int leftLegOffsetxx = 5;
         int rightWingOffset = 6;
         int rightLegOffsetxx = 7;
         int tailOffset = 8;
         int leftWingOffset = 9;
         int bodyOffsetxx = 10;
         ragdoll.addConnection(beak1Offset, headOffsetxx, true);
         ragdoll.addConnection(beak2Offset, headOffsetxx, true);
         ragdoll.addConnection(featherOffset, headOffsetxx, true);
         ragdoll.addConnection(head2Offset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
      } else if (model instanceof HorseModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> head = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();

         while (head.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, head.next(), counter);
         }

         boolean hasSaddle = ReflectionsForge.headParts(animal).iterator().next().getChild("left_saddle_mouth").visible;
         int neckOffset = 0;
         int bodyOffsetxx = counter.count;
         Iterator<ModelPart> bodyxx = ReflectionsForge.bodyParts(animal).iterator();
         int rightHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int rightFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         int leftFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         if (model.young) {
            rightHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
            leftHindLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
            rightFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
            leftFrontLegOffset = RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }

         ragdoll.addConnection(neckOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);

         while (bodyxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxx.next(), counter);
         }

         int leftEarOffset = 2;
         int rightEarOffset = 3;
         int headOffsetxx = 1;
         int mane = 4;
         int upperMouth = 5;
         int tail = 7;
         if (hasSaddle) {
            int leftSaddleMouthOffset = 4;
            int mouthSaddleWrapOffset = 5;
            int rightSaddleLineOffset = 6;
            int rightSaddleMouthOffset = 7;
            int leftSaddleLineOffset = 8;
            int saddleOffset = 11;
            int headSaddleOffset = 9;
            upperMouth = 8;
            mane = 6;
            tail = 12;
            if (model instanceof ChestedHorseModel) {
               boolean hasChests = ((AbstractChestedHorse)entity).hasChest();
               if (hasChests) {
                  saddleOffset = 12;
                  tail = 13;
               }
            }

            ragdoll.addConnection(leftSaddleMouthOffset, neckOffset, true);
            ragdoll.addConnection(mouthSaddleWrapOffset, neckOffset, true);
            ragdoll.addConnection(rightSaddleMouthOffset, neckOffset, true);
            ragdoll.addConnection(headSaddleOffset, neckOffset, true);
            ragdoll.addConnection(saddleOffset, bodyOffsetxx, true);
         } else if (model instanceof ChestedHorseModel) {
            boolean hasChests = ((AbstractChestedHorse)entity).hasChest();
            if (hasChests) {
               tail = 8;
            }
         }

         ragdoll.addConnection(headOffsetxx, neckOffset, true);
         ragdoll.addConnection(leftEarOffset, neckOffset, true);
         ragdoll.addConnection(rightEarOffset, neckOffset, true);
         ragdoll.addConnection(tail, bodyOffsetxx);
         ragdoll.addConnection(mane, neckOffset, true);
         ragdoll.addConnection(upperMouth, neckOffset, true);
         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof LlamaModel) {
         int headOffsetxxx = 0;
         int neckOffsetx = 1;
         int earLeftOffset = 2;
         int earRightOffset = 3;
         int bodyOffsetxxx = 4;
         int rightFrontLegOffsetx = 5;
         int rightHindLegOffsetx = 6;
         int leftHindLegOffsetx = 7;
         int leftFrontLegOffsetx = 8;
         int rightChestOffset = 9;
         int leftChestOffset = 10;
         ragdoll.addConnection(headOffsetxxx, neckOffsetx, true);
         ragdoll.addConnection(earLeftOffset, neckOffsetx, true);
         ragdoll.addConnection(earRightOffset, neckOffsetx, true);
         ragdoll.addConnection(neckOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(rightFrontLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(rightHindLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(leftHindLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffsetx, bodyOffsetxxx);
         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof CamelModel) {
         String SADDLE = "saddle";
         String REINS = "reins";
         ModelPart root = ((CamelModel)model).root();
         ModelPart bodyxxx = root.getChild("body");
         ModelPart head = bodyxxx.getChild("head");
         boolean saddle = bodyxxx.getChild(SADDLE).visible;
         boolean reins = head.getChild(REINS).visible;
         int rightFrontLegOffsetx = 0;
         int rightHindLegOffsetx = 1;
         int leftHindLegOffsetx = 2;
         int bodyOffsetxxx = 3;
         int headOffsetxxx = 4;
         int neck1Offset = 5;
         int neck2Offset = 6;
         int earRightOffset = 7;
         int earLeftOffset = 8;
         int humpOffset = 9;
         int tailOffset = 10;
         int leftFrontLegOffsetx = 11;
         if (saddle) {
            int reinOffset = reins ? 3 : 0;
            earRightOffset = 12;
            earLeftOffset = 13 + reinOffset;
            humpOffset = 14 + reinOffset;
            tailOffset = 18 + reinOffset;
            leftFrontLegOffsetx = 19 + reinOffset;
            int saddleHump1Offset = 15 + reinOffset;
            int saddleHump2Offset = 16 + reinOffset;
            int saddleBodyOffset = 17 + reinOffset;
            int bridle1Offset = 7;
            int bridle2Offset = 8;
            int bridle3Offset = 9;
            int bridle4Offset = 10;
            int bridle5Offset = 11;
            ragdoll.addConnection(bridle1Offset, neck2Offset, true);
            ragdoll.addConnection(bridle2Offset, neck2Offset, true);
            ragdoll.addConnection(bridle3Offset, neck2Offset, true);
            ragdoll.addConnection(bridle4Offset, neck2Offset, true);
            ragdoll.addConnection(bridle5Offset, neck2Offset, true);
            ragdoll.addConnection(saddleHump1Offset, bodyOffsetxxx, true);
            ragdoll.addConnection(saddleHump2Offset, bodyOffsetxxx, true);
            ragdoll.addConnection(saddleBodyOffset, bodyOffsetxxx, true);
         }

         ragdoll.addConnection(headOffsetxxx, neck2Offset, true);
         ragdoll.addConnection(earLeftOffset, neck2Offset, true);
         ragdoll.addConnection(earRightOffset, neck2Offset, true);
         ragdoll.addConnection(neck1Offset, neck2Offset, true);
         ragdoll.addConnection(humpOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(neck2Offset, bodyOffsetxxx);
         ragdoll.addConnection(rightFrontLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(rightHindLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(leftHindLegOffsetx, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffsetx, bodyOffsetxxx);
         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof HoglinModel) {
         AgeableListModel animal = (AgeableListModel)model;
         Iterator<ModelPart> headx = ReflectionsForge.headParts(animal).iterator();
         RagdollMapper.Counter counter = new RagdollMapper.Counter();

         while (headx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, headx.next(), counter);
         }

         int headOffsetxxxx = 0;
         int bodyOffsetxxxx = counter.count;
         Iterator<ModelPart> bodyxxxx = ReflectionsForge.bodyParts(animal).iterator();
         int rightArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxxxx.next(), counter);
         int leftArmOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxxxx.next(), counter);
         int rightLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxxxx.next(), counter);
         int leftLegOffsetxx = RagdollMapper.getCuboids(ragdoll, bodyxxxx.next(), counter);
         ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxxxx);
         int rightHornOffset = 1;
         int leftHornOffset = 2;
         int rightEarOffsetx = 3;
         int leftEarOffsetx = 4;
         int maneOffset = 6;
         ragdoll.addConnection(rightHornOffset, headOffsetxxxx, true);
         ragdoll.addConnection(leftHornOffset, headOffsetxxxx, true);
         ragdoll.addConnection(rightEarOffsetx, headOffsetxxxx, true);
         ragdoll.addConnection(leftEarOffsetx, headOffsetxxxx, true);
         ragdoll.addConnection(maneOffset, bodyOffsetxxxx, true);

         while (bodyxxxx.hasNext()) {
            RagdollMapper.getCuboids(ragdoll, bodyxxxx.next(), counter);
         }
      } else if (model instanceof SalmonModel) {
         int headOffsetxxxx = 0;
         int leftFinOffset = 1;
         int bodyBackOffset = 2;
         int topBackFinOffset = 3;
         int backFinOffset = 4;
         int rightFinOffset = 5;
         int bodyFrontOffset = 6;
         int topFrontFinOffset = 7;
         ragdoll.addConnection(headOffsetxxxx, bodyFrontOffset);
         ragdoll.addConnection(bodyBackOffset, bodyFrontOffset);
         ragdoll.addConnection(topFrontFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(leftFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(rightFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(topBackFinOffset, bodyBackOffset, true);
         ragdoll.addConnection(backFinOffset, bodyBackOffset, true);
      } else if (model instanceof AxolotlModel) {
         int bodyOffsetxxxx = 0;
         int bodyGillsOffset = 1;
         int headOffsetxxxx = 2;
         int topGillsOffset = 3;
         int leftGillsOffset = 4;
         int rightGillsOffset = 5;
         int rightFrontLegOffsetxx = 6;
         int rightHindLegffset = 7;
         int tailOffsetx = 8;
         int leftHindLegOffsetxx = 9;
         int leftFrontLegOffsetxx = 10;
         ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
         ragdoll.addConnection(bodyGillsOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topGillsOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftGillsOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightGillsOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightHindLegffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxx, true);
         ragdoll.addConnection(tailOffsetx, bodyOffsetxxxx, true);
      } else if (model instanceof PhantomModel) {
         int bodyOffsetxxxx = 0;
         int headOffsetxxxx = 1;
         int rightWingBaseOffset = 2;
         int rightWingTipOffset = 3;
         int tailBaseOffset = 4;
         int tailTipOffset = 5;
         int leftWingBaseOffset = 6;
         int leftWingTipOffset = 7;
         ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightWingBaseOffset, bodyOffsetxxxx);
         ragdoll.addConnection(tailBaseOffset, bodyOffsetxxxx);
         ragdoll.addConnection(leftWingBaseOffset, bodyOffsetxxxx);
         ragdoll.addConnection(rightWingTipOffset, rightWingBaseOffset, true);
         ragdoll.addConnection(tailTipOffset, tailBaseOffset, true);
         ragdoll.addConnection(leftWingTipOffset, leftWingBaseOffset, true);
      } else if (model instanceof CodModel) {
         int headOffsetxxxx = 0;
         int noseOffset = 1;
         int leftFinOffset = 2;
         int topFinOffset = 3;
         int rightFinOffset = 4;
         int bodyOffsetxxxx = 5;
         int tailFinOffset = 6;
         ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
         ragdoll.addConnection(noseOffset, headOffsetxxxx, true);
         ragdoll.addConnection(tailFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxx, true);
      } else if (model instanceof PufferfishSmallModel) {
         int rightEyeOffset = 0;
         int leftFinOffset = 1;
         int rightFinOffset = 2;
         int leftEyeOffset = 3;
         int bodyOffsetxxxx = 4;
         int backFinOffset = 5;
         ragdoll.addConnection(rightEyeOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftEyeOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(backFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxx, true);
      } else if (model instanceof PufferfishMidModel) {
         int leftBlueFinOffset = 0;
         int topBackFinOffset = 1;
         int leftBackFinOffset = 2;
         int leftFrontFinOffset = 3;
         int bottomFrontFinOffset = 4;
         int rightFrontFinOffset = 5;
         int rightBackFinOffset = 6;
         int bodyOffsetxxxx = 7;
         int topFrontFinOffset = 8;
         int bottomBackFinOffset = 9;
         int rightBlueFinOffset = 10;
         ragdoll.addConnection(leftBlueFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(bottomFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(bottomBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightBlueFinOffset, bodyOffsetxxxx, true);
      } else if (model instanceof PufferfishBigModel) {
         int leftBlueFinOffset = 0;
         int topBackFinOffset = 1;
         int leftBackFinOffset = 2;
         int leftFrontFinOffset = 3;
         int bottomFrontFinOffset = 4;
         int bodyOffsetxxxx = 5;
         int rightFrontFinOffset = 6;
         int rightBackFinOffset = 7;
         int topFrontFinOffset = 8;
         int bottomBackFinOffset = 9;
         int rightBlueFinOffset = 10;
         int rightBlueBackFinOffset = 11;
         int topBlueFrontFinOffset = 12;
         ragdoll.addConnection(leftBlueFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(leftFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(bottomFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(bottomBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightBlueBackFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(topBlueFrontFinOffset, bodyOffsetxxxx, true);
         ragdoll.addConnection(rightBlueFinOffset, bodyOffsetxxxx, true);
      } else if (model instanceof TropicalFishModelB) {
         int leftFinOffset = 0;
         int topFinOffset = 1;
         int tailOffsetx = 2;
         int rightFinOffset = 3;
         int bodyOffsetxxxx = 4;
         int bottomFinOffset = 5;
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(bottomFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(tailOffsetx, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addOverlayConnections(true);
      } else if (model instanceof TropicalFishModelA) {
         int leftFinOffset = 0;
         int topFinOffset = 1;
         int tailOffsetx = 2;
         int rightFinOffset = 3;
         int bodyOffsetxxxx = 4;
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(tailOffsetx, bodyOffsetxxxx, true, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxx, true, true);
         ragdoll.addOverlayConnections(true);
      } else if (model instanceof TadpoleModel) {
         int bodyOffsetxxxx = 1;
         int tailOffsetx = 2;
         ragdoll.addConnection(tailOffsetx, bodyOffsetxxxx, true, true);
      } else if (model instanceof FrogModel<?> frog) {
         int leftLeg = 0;
         int leftFoot = 1;
         int rightLeg = 2;
         int rightFoot = 3;
         int bodyxxxx = 4;
         int body2 = 5;
         int headx = 6;
         int head2 = 7;
         int rightEye = 8;
         int leftEye = 9;
         int rightArm = 10;
         int rightHand = 11;
         int tongue = 12;
         int leftArm = 13;
         int leftHand = 14;
         ragdoll.addConnection(leftFoot, leftLeg, true, true);
         ragdoll.addConnection(rightFoot, rightLeg, true, true);
         ragdoll.addConnection(leftLeg, bodyxxxx);
         ragdoll.addConnection(rightLeg, bodyxxxx);
         ragdoll.addConnection(head2, bodyxxxx, true);
         ragdoll.addConnection(body2, bodyxxxx, true);
         ragdoll.addConnection(rightEye, bodyxxxx, true);
         ragdoll.addConnection(leftEye, bodyxxxx, true);
         ragdoll.addConnection(rightArm, bodyxxxx);
         ragdoll.addConnection(leftArm, bodyxxxx);
         ragdoll.addConnection(rightHand, rightArm, true, true);
         ragdoll.addConnection(leftHand, leftArm, true, true);
         ragdoll.addConnection(tongue, bodyxxxx);
         ragdoll.addConnection(headx, bodyxxxx, true, true);
         if (ragdoll.bodies.size() > 15) {
            int croakingBody = 15;
            ragdoll.addConnection(croakingBody, bodyxxxx, true);
         }

         RagdollMapper.getCuboids(ragdoll, frog.root(), new RagdollMapper.Counter());
      } else if (model instanceof AllayModel allay) {
         int headx = 0;
         int bodyxxxx = 1;
         int body2 = 2;
         int rightArm = 3;
         int leftArm = 4;
         int rightWing = 5;
         int leftWing = 6;
         ragdoll.addConnection(headx, bodyxxxx);
         ragdoll.addConnection(rightArm, bodyxxxx);
         ragdoll.addConnection(leftArm, bodyxxxx);
         ragdoll.addConnection(body2, bodyxxxx);
         ragdoll.addConnection(rightWing, bodyxxxx, true, true);
         ragdoll.addConnection(leftWing, bodyxxxx, true, true);
         RagdollMapper.getCuboids(ragdoll, allay.root(), new RagdollMapper.Counter());
      } else if (model instanceof WardenModel<?> warden) {
         int bone = 0;
         int leftLeg = 0;
         int rightLeg = 1;
         int bodyxxxx = 2;
         int headx = 3;
         int rightTendril = 4;
         int leftTendril = 5;
         int rightArm = 6;
         int leftArm = 7;
         int leftRibcage = 8;
         int rightRibcage = 9;
         ragdoll.addConnection(rightTendril, headx, true, true);
         ragdoll.addConnection(leftTendril, headx, true, true);
         ragdoll.addConnection(headx, bodyxxxx);
         ragdoll.addConnection(rightArm, bodyxxxx);
         ragdoll.addConnection(leftArm, bodyxxxx);
         ragdoll.addConnection(rightLeg, bodyxxxx);
         ragdoll.addConnection(leftLeg, bodyxxxx);
         ragdoll.addConnection(leftRibcage, bodyxxxx, true, true);
         ragdoll.addConnection(rightRibcage, bodyxxxx, true, true);
         int leftLegBL = 10;
         int rightLegBL = 11;
         int headBL = 12;
         int rightArmBL = 13;
         int leftArmBL = 14;
         int leftLegP1 = 15;
         int rightLegP1 = 16;
         int bodyP1 = 17;
         int headP1 = 18;
         int rightArmP1 = 19;
         int leftArmP1 = 20;
         int leftLegP2 = 21;
         int rightLegP2 = 22;
         int bodyP2 = 23;
         int headP2 = 24;
         int rightArmP2 = 25;
         int leftArmP2 = 26;
         int rightTendrilT = 27;
         int leftTendrilT = 28;
         int bodyH = 29;
         ragdoll.addConnection(headBL, headx, true, true);
         ragdoll.addConnection(leftArmBL, leftArm, true, true);
         ragdoll.addConnection(rightArmBL, rightArm, true, true);
         ragdoll.addConnection(leftLegBL, leftLeg, true, true);
         ragdoll.addConnection(rightLegBL, rightLeg, true, true);
         ragdoll.addConnection(bodyP1, bodyxxxx, true, true);
         ragdoll.addConnection(headP1, headx, true, true);
         ragdoll.addConnection(leftArmP1, leftArm, true, true);
         ragdoll.addConnection(rightArmP1, rightArm, true, true);
         ragdoll.addConnection(leftLegP1, leftLeg, true, true);
         ragdoll.addConnection(rightLegP1, rightLeg, true, true);
         ragdoll.addConnection(bodyP2, bodyxxxx, true, true);
         ragdoll.addConnection(headP2, headx, true, true);
         ragdoll.addConnection(leftArmP2, leftArm, true, true);
         ragdoll.addConnection(rightArmP2, rightArm, true, true);
         ragdoll.addConnection(leftLegP2, leftLeg, true, true);
         ragdoll.addConnection(rightLegP2, rightLeg, true, true);
         ragdoll.addConnection(leftTendrilT, leftTendril, true, true);
         ragdoll.addConnection(rightTendrilT, rightTendril, true, true);
         ragdoll.addConnection(bodyH, bodyxxxx, true, true);
      } else if (entity instanceof EnderDragon) {
         for (int ixxxx = 0; ixxxx < 5; ixxxx++) {
            ragdoll.addConnection(ixxxx * 2 + 1, ixxxx * 2, true);
            if (ixxxx != 0) {
               ragdoll.addConnection((ixxxx - 1) * 2, ixxxx * 2);
            }
         }

         int offset = 10;
         int head0 = 0 + offset;
         int head1 = 1 + offset;
         int head2 = 2 + offset;
         int head3 = 3 + offset;
         int head4 = 4 + offset;
         int head5 = 5 + offset;
         int jaw = 6 + offset;
         ragdoll.addConnection(jaw, head0, true);
         ragdoll.addConnection(head1, head0, true);
         ragdoll.addConnection(head2, head0, true);
         ragdoll.addConnection(head3, head0, true);
         ragdoll.addConnection(head4, head0, true);
         ragdoll.addConnection(head5, head0, true);
         ragdoll.addConnection(head0, 8);
         int body0 = 7 + offset;
         int body1 = 8 + offset;
         int body2 = 9 + offset;
         int body3 = 10 + offset;
         int leftWing = 11 + offset;
         int leftWingTexture = 12 + offset;
         int leftWingTip = 13 + offset;
         int leftWingTipTexture = 14 + offset;
         int leftFrontLeg = 15 + offset;
         int leftFrontLegTip = 16 + offset;
         int leftFrontFoot = 17 + offset;
         int leftHindLeg = 18 + offset;
         int leftHindLegTip = 19 + offset;
         int leftHindFoot = 20 + offset;
         int rightWing = 21 + offset;
         int rightWingTexture = 22 + offset;
         int rightWingTip = 23 + offset;
         int rightWingTipTexture = 24 + offset;
         int rightFrontLeg = 25 + offset;
         int rightFrontLegTip = 26 + offset;
         int rightFrontFoot = 27 + offset;
         int rightHindLeg = 28 + offset;
         int rightHindLegTip = 29 + offset;
         int rightHindFoot = 30 + offset;
         ragdoll.addConnection(0, body0);
         ragdoll.addConnection(body1, body0, true);
         ragdoll.addConnection(body2, body0, true);
         ragdoll.addConnection(body3, body0, true);
         ragdoll.addConnection(rightWing, body0);
         ragdoll.addConnection(leftWing, body0);
         ragdoll.addConnection(rightWingTip, rightWing);
         ragdoll.addConnection(leftWingTip, leftWing);
         ragdoll.addConnection(rightWingTexture, rightWing, true);
         ragdoll.addConnection(leftWingTexture, leftWing, true);
         ragdoll.addConnection(rightWingTipTexture, rightWingTip, true);
         ragdoll.addConnection(leftWingTipTexture, leftWingTip, true);
         ragdoll.addConnection(rightFrontLeg, body0);
         ragdoll.addConnection(rightHindLeg, body0);
         ragdoll.addConnection(leftFrontLeg, body0);
         ragdoll.addConnection(leftHindLeg, body0);
         ragdoll.addConnection(rightFrontLegTip, rightFrontLeg);
         ragdoll.addConnection(rightHindLegTip, rightHindLeg);
         ragdoll.addConnection(leftFrontLegTip, leftFrontLeg);
         ragdoll.addConnection(leftHindLegTip, leftHindLeg);
         ragdoll.addConnection(rightFrontFoot, rightFrontLegTip);
         ragdoll.addConnection(rightHindFoot, rightHindLegTip);
         ragdoll.addConnection(leftFrontFoot, leftFrontLegTip);
         ragdoll.addConnection(leftHindFoot, leftHindLegTip);
         int var88 = 41;

         for (int ixxxxx = 0; ixxxxx < 12; ixxxxx++) {
            ragdoll.addConnection(ixxxxx * 2 + 1 + var88, ixxxxx * 2 + var88, true);
            if (ixxxxx != 0) {
               ragdoll.addConnection((ixxxxx - 1) * 2 + var88, ixxxxx * 2 + var88);
            }
         }

         ragdoll.addConnection(var88, body0);
      } else if (model instanceof SnifferModel) {
         int rightFrontLegOffsetxx = 0;
         int rightHindLegOffsetxx = 1;
         int rightMidLegOffset = 2;
         int leftHindLegOffsetxx = 3;
         int bodyOffsetxxxx = 4;
         int headOffsetxxxx = 7;
         int noseOffset = 9;
         int lowerBeakOffset = 10;
         int rightEarOffsetx = 11;
         int leftEarOffsetx = 12;
         int leftMidLegOffset = 13;
         int leftFrontLegOffsetxx = 14;
         ragdoll.addConnection(rightEarOffsetx, headOffsetxxxx, true);
         ragdoll.addConnection(leftEarOffsetx, headOffsetxxxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxxxx, true);
         ragdoll.addConnection(lowerBeakOffset, headOffsetxxxx, true);
         ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(rightMidLegOffset, bodyOffsetxxxx);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxx);
         ragdoll.addConnection(leftMidLegOffset, bodyOffsetxxxx);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxx);
         RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
      } else if (model instanceof ArmadilloModel) {
         boolean inShell = false;
         if (entity instanceof Armadillo armadillo) {
            inShell = armadillo.shouldHideInShell();
         }

         if (inShell) {
            int rightFrontLegOffsetxx = 0;
            int cubeOffset = 1;
            int headOffsetxxxx = 2;
            int rightEarOffsetx = 3;
            int leftEarOffsetx = 4;
            int leftFrontLegOffsetxx = 5;
            ragdoll.addConnection(rightEarOffsetx, cubeOffset, true, true);
            ragdoll.addConnection(leftEarOffsetx, cubeOffset, true, true);
            ragdoll.addConnection(headOffsetxxxx, cubeOffset, true, true);
            ragdoll.addConnection(rightFrontLegOffsetxx, cubeOffset);
            ragdoll.addConnection(leftFrontLegOffsetxx, cubeOffset);
         } else {
            int rightFrontLegOffsetxx = 0;
            int rightHindLegOffsetxx = 1;
            int leftHindLegOffsetxx = 2;
            int bodyOffsetxxxx = 3;
            int headOffsetxxxx = 5;
            int rightEarOffsetx = 6;
            int leftEarOffsetx = 7;
            int tailOffsetx = 8;
            int leftFrontLegOffsetxx = 9;
            ragdoll.addConnection(rightEarOffsetx, headOffsetxxxx, true);
            ragdoll.addConnection(leftEarOffsetx, headOffsetxxxx, true);
            ragdoll.addConnection(headOffsetxxxx, bodyOffsetxxxx);
            ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxx);
            ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxx);
            ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxx);
            ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxx);
            ragdoll.addConnection(tailOffsetx, bodyOffsetxxxx);
            RagdollMapper.getCuboids(ragdoll, ((HierarchicalModel)model).root(), new RagdollMapper.Counter());
         }
      }
   }

   @Override
   public void filterCuboidsFromEntities(List<PhysicsEntity> blockifiedEntity, Entity entity, EntityModel model) {
      boolean ragdollsEnabled = RagdollMapper.areRagdollsEnabled(entity);
      if (model instanceof IronGolemModel) {
         while (blockifiedEntity.size() > 8) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof SpiderModel) {
         while (blockifiedEntity.size() > 11) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof StriderModel) {
         while (blockifiedEntity.size() > 9) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof WitherBossModel) {
         while (blockifiedEntity.size() > 9) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof SheepModel) {
         while (blockifiedEntity.size() > 6) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (entity instanceof EnderDragon) {
         while (blockifiedEntity.size() > 65) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (!(model instanceof TropicalFishModelB)
         && !(model instanceof TropicalFishModelA)
         && !(model instanceof SkeletonModel)
         && !(model instanceof HorseModel)
         && !(model instanceof LlamaModel)
         && !(model instanceof DrownedModel)
         && !(model instanceof IllagerModel)
         && !(model instanceof VillagerModel)
         && !(model instanceof EndermanModel)) {
         if (model instanceof PhantomModel || model instanceof ZombieModel || model instanceof PiglinModel) {
            int count = RagdollMapper.countModelParts(entity, model);

            while (blockifiedEntity.size() > count) {
               blockifiedEntity.remove(blockifiedEntity.size() - 1);
            }
         }
      } else {
         int count = RagdollMapper.countModelParts(entity, model);
         if (!ragdollsEnabled) {
            while (blockifiedEntity.size() > count) {
               blockifiedEntity.remove(blockifiedEntity.size() - 1);
            }
         }
      }

      Iterator<PhysicsEntity> it = blockifiedEntity.iterator();

      while (it.hasNext()) {
         PhysicsEntity physicsEntity = it.next();
         if ((
               physicsEntity.feature instanceof HumanoidArmorLayer
                  || physicsEntity.feature instanceof CustomHeadLayer
                  || physicsEntity.feature instanceof ElytraLayer
                  || physicsEntity.feature instanceof ItemInHandLayer
                  || physicsEntity.feature instanceof ArrowLayer
                  || physicsEntity.feature instanceof Deadmau5EarsLayer
                  || physicsEntity.feature instanceof CapeLayer
                  || physicsEntity.feature instanceof SpinAttackEffectLayer
                  || physicsEntity.feature instanceof ParrotOnShoulderLayer
                  || physicsEntity.feature instanceof BeeStingerLayer
            )
            && (!(model instanceof SkeletonModel) || !(physicsEntity.feature instanceof ItemInHandLayer))) {
            it.remove();
         }
      }
   }
}
