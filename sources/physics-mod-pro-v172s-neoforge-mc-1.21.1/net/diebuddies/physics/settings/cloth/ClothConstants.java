package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Map.Entry;
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
      ObjectLinkedOpenHashSet<ModelPart> parts = new ObjectLinkedOpenHashSet();

      try {
         if (model instanceof AgeableListModel uncastModel) {
            for (ModelPart headPart : ReflectionsForge.headParts(uncastModel)) {
               parts.add(headPart);
            }

            for (ModelPart bodyPart : ReflectionsForge.bodyParts(uncastModel)) {
               parts.add(bodyPart);
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return parts;
   }

   public static ObjectLinkedOpenHashSet<ModelPart> getModelParts(EntityType<?> entityType) {
      try {
         EntityRenderer<?> renderer = PhysicsMod.renderers.get(entityType);
         EntityModel<LivingEntity> model = (EntityModel<LivingEntity>)MobEntry.getModel(renderer, entityType);
         return getModelParts(model);
      } catch (Exception var3) {
         var3.printStackTrace();
         return new ObjectLinkedOpenHashSet();
      }
   }

   @Nullable
   public static ModelPart getModelPart(ObjectLinkedOpenHashSet<ModelPart> parts, String name) {
      ObjectListIterator var2 = parts.iterator();

      while (var2.hasNext()) {
         ModelPart part = (ModelPart)var2.next();
         if (name.equals(((ModelPartParent)part).physicsmod$getName())) {
            return part;
         }
      }

      return null;
   }

   public static void hideProperParts(Entity entity, Model model) {
      hideProperParts(ConfigCloth.getCustomizationParts(entity), model, entity);
   }

   public static void hideProperParts(String entity, Model model) {
      hideProperParts(ConfigCloth.getCustomizationParts(entity), model, null);
   }

   private static void hideProperParts(Map<String, ConfigCloth.ClothList> customization, Model model, @Nullable Entity entity) {
      if (customization != null) {
         ObjectLinkedOpenHashSet<ModelPart> parts = getModelParts(model);
         if (parts != null) {
            for (Entry<String, ConfigCloth.ClothList> partList : customization.entrySet()) {
               String part = partList.getKey();
               ConfigCloth.ClothList clothList = partList.getValue();

               for (String clothPiece : clothList.getClothPieces()) {
                  Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                  boolean isClothActive = cloth != null;
                  if (isClothActive && entity != null && entity instanceof LivingEntity living && !cloth.rules.getCategory().equals("Elytra")) {
                     isClothActive = isClothActive && !doesArmorHideCloth(cloth, living) && !isElytraHidingCloth(cloth, part, living);
                  }

                  if (isClothActive) {
                     for (String hiddenPart : cloth.rules.getHiddenParts()) {
                        ModelPart modelpart = getModelPart(parts, hiddenPart);
                        if (modelpart != null) {
                           modelpart.visible = false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean doesArmorHideCloth(Cloth cloth, LivingEntity entity) {
      if (ConfigClient.clothForceArmor) {
         for (String equipment : cloth.rules.getHiddenArmorPieces()) {
            EquipmentSlot equipmentSlot = EquipmentSlot.byName(equipment);
            ItemStack itemStack = entity.getItemBySlot(equipmentSlot);
            if (itemStack.getItem() instanceof ArmorItem) {
               ArmorItem armorItem = (ArmorItem)itemStack.getItem();
               if (armorItem.getEquipmentSlot() == equipmentSlot) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isElytraHidingCloth(Cloth cloth, String part, LivingEntity entity) {
      if (cloth.rules.getCategory().equals("Elytra")) {
         return true;
      } else {
         if (part.equals("body")) {
            ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (itemStack.is(Items.ELYTRA)) {
               return true;
            }
         }

         return false;
      }
   }
}
