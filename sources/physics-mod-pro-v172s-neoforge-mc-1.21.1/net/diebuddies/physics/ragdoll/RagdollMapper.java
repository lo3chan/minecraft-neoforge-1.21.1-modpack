package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class RagdollMapper {
   private static final List<RagdollHook> hooks = new ObjectArrayList();
   private static final RagdollHook vanillaHook = new VanillaRagdollHook();

   public static void addHook(RagdollHook hook) {
      hooks.add(hook);
   }

   public static void removeHook(RagdollHook hook) {
      hooks.remove(hook);
   }

   public static Ragdoll map(MobPhysicsType type, Entity entity, EntityModel model) {
      Ragdoll ragdoll;
      if (type == MobPhysicsType.RAGDOLL) {
         ragdoll = new BreakableRagdoll(-1.0F);
      } else {
         ragdoll = new BreakableRagdoll(ConfigClient.jointBreakForce);
      }

      ragdoll.bodies.addAll(PhysicsMod.getInstance(entity.getCommandSenderWorld()).blockifiedEntity);

      for (RagdollHook hook : hooks) {
         hook.map(ragdoll, entity, model);
      }

      if (ragdoll.joints.size() > 0) {
         return ragdoll;
      } else {
         vanillaHook.map(ragdoll, entity, model);
         return ragdoll.joints.size() == 0 ? null : ragdoll;
      }
   }

   public static void filterCuboidsFromEntities(Entity entity, EntityModel model) {
      PhysicsMod mod = PhysicsMod.getInstance(entity.getCommandSenderWorld());

      for (RagdollHook hook : hooks) {
         hook.filterCuboidsFromEntities(mod.blockifiedEntity, entity, model);
      }

      vanillaHook.filterCuboidsFromEntities(mod.blockifiedEntity, entity, model);
   }

   public static void printModelParts(EntityModel model) {
      if (model instanceof AgeableListModel) {
         int index = 0;

         for (ModelPart part : ReflectionsForge.headParts((AgeableListModel)model)) {
            index = printModelPart(part, index);
         }

         for (ModelPart part : ReflectionsForge.bodyParts((AgeableListModel)model)) {
            index = printModelPart(part, index);
         }

         System.out.println(model.getClass());
         System.out.println("total: " + index);
      } else if (model instanceof HierarchicalModel) {
         int total = printModelParts((HierarchicalModel)model);
         System.out.println(model.getClass());
         System.out.println("total: " + total);
      }
   }

   public static int countModelParts(Entity entity, EntityModel model) {
      if (model instanceof LlamaModel) {
         int total = 9;
         if (!((AbstractChestedHorse)entity).isBaby() && ((AbstractChestedHorse)entity).hasChest()) {
            total = 11;
         }

         return total;
      } else if (!(model instanceof AgeableListModel)) {
         return model instanceof HierarchicalModel ? printModelParts((HierarchicalModel)model, true) : 0;
      } else {
         int total = 0;

         for (ModelPart part : ReflectionsForge.headParts((AgeableListModel)model)) {
            total = printModelPart(part, total, true);
         }

         for (ModelPart part : ReflectionsForge.bodyParts((AgeableListModel)model)) {
            total = printModelPart(part, total, true);
         }

         return total;
      }
   }

   public static boolean areRagdollsEnabled(Entity entity) {
      MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
      return type == MobPhysicsType.RAGDOLL || type == MobPhysicsType.RAGDOLL_BREAK || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
   }

   public static boolean isMobFracturingEnabled(Entity entity) {
      MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
      return type == MobPhysicsType.FRACTURED || type == MobPhysicsType.FRACTURED_BLOOD;
   }

   private static int printModelParts(HierarchicalModel entityModel, boolean hidePrint) {
      ModelPart root = entityModel.root();
      return printModelPart(root, 0, hidePrint);
   }

   private static int printModelParts(HierarchicalModel entityModel) {
      return printModelParts(entityModel, false);
   }

   public static int printModelPart(ModelPart part, int index, boolean hidePrint) {
      if (part.visible) {
         for (int i = 0; i < part.cubes.size(); i++) {
            index++;
         }

         for (Entry<String, ModelPart> entry : part.children.entrySet()) {
            if (!hidePrint) {
               System.out.println(entry.getKey() + ": " + index);
            }

            ModelPart child = entry.getValue();
            index = printModelPart(child, index, hidePrint);
         }
      }

      return index;
   }

   public static int printModelPart(ModelPart part, int index) {
      return printModelPart(part, index, false);
   }

   public static int getCuboids(Ragdoll ragdoll, ModelPart part, RagdollMapper.Counter counter, boolean onlyVisual) {
      if (part.visible) {
         for (int c = 1; c < part.cubes.size(); c++) {
            ragdoll.addConnection(counter.count + c, counter.count, true, onlyVisual);
         }

         counter.count = counter.count + part.cubes.size();

         for (ModelPart p : part.children.values()) {
            counter.count = getCuboids(ragdoll, p, counter);
         }
      }

      return counter.count;
   }

   public static int getCuboids(Ragdoll ragdoll, ModelPart part, RagdollMapper.Counter counter) {
      return getCuboids(ragdoll, part, counter, false);
   }

   public static class Counter {
      public int count;
   }
}
