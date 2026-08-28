/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.AgeableListModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.mobs.MobEntry;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClothConstants {
    public static boolean trackParts = false;
    public static final ObjectLinkedOpenHashSet<ModelPart> activeParts = new ObjectLinkedOpenHashSet();

    public static ObjectLinkedOpenHashSet<ModelPart> getModelParts(Model model) {
        ObjectLinkedOpenHashSet parts = new ObjectLinkedOpenHashSet();
        try {
            if (model instanceof AgeableListModel) {
                AgeableListModel uncastModel;
                AgeableListModel ageableModel = uncastModel = (AgeableListModel)model;
                for (ModelPart headPart : ReflectionsForge.headParts(ageableModel)) {
                    parts.add((Object)headPart);
                }
                for (ModelPart bodyPart : ReflectionsForge.bodyParts(ageableModel)) {
                    parts.add((Object)bodyPart);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return parts;
    }

    public static ObjectLinkedOpenHashSet<ModelPart> getModelParts(EntityType<?> entityType) {
        try {
            EntityRenderer<?> renderer = PhysicsMod.renderers.get(entityType);
            EntityModel model = (EntityModel)MobEntry.getModel(renderer, entityType);
            return ClothConstants.getModelParts((Model)model);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ObjectLinkedOpenHashSet();
        }
    }

    @Nullable
    public static ModelPart getModelPart(ObjectLinkedOpenHashSet<ModelPart> parts, String name) {
        for (ModelPart part : parts) {
            if (!name.equals(((ModelPartParent)part).physicsmod$getName())) continue;
            return part;
        }
        return null;
    }

    public static void hideProperParts(Entity entity, Model model) {
        ClothConstants.hideProperParts(ConfigCloth.getCustomizationParts(entity), model, entity);
    }

    public static void hideProperParts(String entity, Model model) {
        ClothConstants.hideProperParts(ConfigCloth.getCustomizationParts(entity), model, null);
    }

    private static void hideProperParts(Map<String, ConfigCloth.ClothList> customization, Model model, @Nullable Entity entity) {
        ObjectLinkedOpenHashSet<ModelPart> parts;
        if (customization != null && (parts = ClothConstants.getModelParts(model)) != null) {
            for (Map.Entry<String, ConfigCloth.ClothList> partList : customization.entrySet()) {
                String part = partList.getKey();
                ConfigCloth.ClothList clothList = partList.getValue();
                for (String clothPiece : clothList.getClothPieces()) {
                    boolean isClothActive;
                    Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                    boolean bl = isClothActive = cloth != null;
                    if (isClothActive && entity != null && entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity)entity;
                        if (!cloth.rules.getCategory().equals("Elytra")) {
                            boolean bl2 = isClothActive = isClothActive && !ClothConstants.doesArmorHideCloth(cloth, living) && !ClothConstants.isElytraHidingCloth(cloth, part, living);
                        }
                    }
                    if (!isClothActive) continue;
                    for (String hiddenPart : cloth.rules.getHiddenParts()) {
                        ModelPart modelpart = ClothConstants.getModelPart(parts, hiddenPart);
                        if (modelpart == null) continue;
                        modelpart.visible = false;
                    }
                }
            }
        }
    }

    public static boolean doesArmorHideCloth(Cloth cloth, LivingEntity entity) {
        if (ConfigClient.clothForceArmor) {
            for (String equipment : cloth.rules.getHiddenArmorPieces()) {
                ArmorItem armorItem;
                EquipmentSlot equipmentSlot = EquipmentSlot.byName((String)equipment);
                ItemStack itemStack = entity.getItemBySlot(equipmentSlot);
                if (!(itemStack.getItem() instanceof ArmorItem) || (armorItem = (ArmorItem)itemStack.getItem()).getEquipmentSlot() != equipmentSlot) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isElytraHidingCloth(Cloth cloth, String part, LivingEntity entity) {
        ItemStack itemStack;
        if (cloth.rules.getCategory().equals("Elytra")) {
            return true;
        }
        return part.equals("body") && (itemStack = entity.getItemBySlot(EquipmentSlot.CHEST)).is(Items.ELYTRA);
    }
}

