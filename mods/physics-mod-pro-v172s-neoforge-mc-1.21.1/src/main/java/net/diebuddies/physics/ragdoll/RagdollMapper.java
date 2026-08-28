/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.model.AgeableListModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.LlamaModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.animal.horse.AbstractChestedHorse
 *  net.minecraft.world.level.Level
 */
package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.BreakableRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollHook;
import net.diebuddies.physics.ragdoll.VanillaRagdollHook;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.level.Level;

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
        BreakableRagdoll ragdoll = type == MobPhysicsType.RAGDOLL ? new BreakableRagdoll(-1.0f) : new BreakableRagdoll(ConfigClient.jointBreakForce);
        ragdoll.bodies.addAll(PhysicsMod.getInstance((Level)entity.getCommandSenderWorld()).blockifiedEntity);
        for (RagdollHook hook : hooks) {
            hook.map(ragdoll, entity, model);
        }
        if (ragdoll.joints.size() > 0) {
            return ragdoll;
        }
        vanillaHook.map(ragdoll, entity, model);
        if (ragdoll.joints.size() == 0) {
            return null;
        }
        return ragdoll;
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
                index = RagdollMapper.printModelPart(part, index);
            }
            for (ModelPart part : ReflectionsForge.bodyParts((AgeableListModel)model)) {
                index = RagdollMapper.printModelPart(part, index);
            }
            System.out.println(model.getClass());
            System.out.println("total: " + index);
        } else if (model instanceof HierarchicalModel) {
            int total = RagdollMapper.printModelParts((HierarchicalModel)model);
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
        }
        if (model instanceof AgeableListModel) {
            int total = 0;
            for (ModelPart part : ReflectionsForge.headParts((AgeableListModel)model)) {
                total = RagdollMapper.printModelPart(part, total, true);
            }
            for (ModelPart part : ReflectionsForge.bodyParts((AgeableListModel)model)) {
                total = RagdollMapper.printModelPart(part, total, true);
            }
            return total;
        }
        if (model instanceof HierarchicalModel) {
            int total = RagdollMapper.printModelParts((HierarchicalModel)model, true);
            return total;
        }
        return 0;
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
        return RagdollMapper.printModelPart(root, 0, hidePrint);
    }

    private static int printModelParts(HierarchicalModel entityModel) {
        return RagdollMapper.printModelParts(entityModel, false);
    }

    public static int printModelPart(ModelPart part, int index, boolean hidePrint) {
        if (part.visible) {
            for (int i = 0; i < part.cubes.size(); ++i) {
                ++index;
            }
            for (Map.Entry entry : part.children.entrySet()) {
                if (!hidePrint) {
                    System.out.println((String)entry.getKey() + ": " + index);
                }
                ModelPart child = (ModelPart)entry.getValue();
                index = RagdollMapper.printModelPart(child, index, hidePrint);
            }
        }
        return index;
    }

    public static int printModelPart(ModelPart part, int index) {
        return RagdollMapper.printModelPart(part, index, false);
    }

    public static int getCuboids(Ragdoll ragdoll, ModelPart part, Counter counter, boolean onlyVisual) {
        if (part.visible) {
            for (int c = 1; c < part.cubes.size(); ++c) {
                ragdoll.addConnection(counter.count + c, counter.count, true, onlyVisual);
            }
            counter.count += part.cubes.size();
            for (ModelPart p : part.children.values()) {
                counter.count = RagdollMapper.getCuboids(ragdoll, p, counter);
            }
        }
        return counter.count;
    }

    public static int getCuboids(Ragdoll ragdoll, ModelPart part, Counter counter) {
        return RagdollMapper.getCuboids(ragdoll, part, counter, false);
    }

    public static class Counter {
        public int count;
    }
}

