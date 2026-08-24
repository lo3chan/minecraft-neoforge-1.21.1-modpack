package net.cibernet.alchemancy.events.handler;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.cibernet.alchemancy.entity.ai.TemptByRootedGoal;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class MobTemptHandler {
   private static final HashMap<Holder<Property>, Predicate<Entity>> PROPERTY_MAP = new HashMap<>();

   public static void registerTemptProperty(Holder<Property> property, Predicate<Entity> entityPredicate) {
      PROPERTY_MAP.put(property, entityPredicate);
   }

   public static void registerTemptProperty(Holder<Property> property, TagKey<EntityType<?>> entityTag) {
      registerTemptProperty(property, (Predicate<Entity>)(entity -> entity.getType().is(entityTag)));
   }

   public static void performIfTempted(Entity entity, ItemStack stack, MobTemptHandler.Action action) {
      for (Entry<Holder<Property>, Predicate<Entity>> entry : PROPERTY_MAP.entrySet()) {
         if (!entry.getKey().is(AlchemancyTags.Properties.DISABLED)
            && InfusedPropertiesHelper.hasInfusedProperty(stack, entry.getKey())
            && entry.getValue().test(entity)) {
            action.perform();
            return;
         }
      }
   }

   public static void performIfTempted(Entity entity, LivingEntity user, EquipmentSlotGroup slots, MobTemptHandler.Action action) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slots.test(slot)) {
            ItemStack stack = user.getItemBySlot(slot);

            for (Entry<Holder<Property>, Predicate<Entity>> entry : PROPERTY_MAP.entrySet()) {
               if (InfusedPropertiesHelper.hasInfusedProperty(stack, entry.getKey()) && entry.getValue().test(entity)) {
                  action.perform();
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof PathfinderMob mob) {
         mob.goalSelector.addGoal(2, new TemptGoal(mob, 1.0, stack -> InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CHARMING), false));
         mob.goalSelector.addGoal(0, new TemptByRootedGoal(mob, 1.0, AlchemancyProperties.CHARMING));
         PROPERTY_MAP.forEach((propertyHolder, entityPredicate) -> {
            if (entityPredicate.test(mob)) {
               mob.goalSelector.addGoal(3, new TemptGoal(mob, 1.25, stack -> InfusedPropertiesHelper.hasProperty(stack, propertyHolder), false));
               mob.goalSelector.addGoal(0, new TemptByRootedGoal(mob, 1.25, (Holder<Property>)propertyHolder));
            }
         });
      }
   }

   static {
      registerTemptProperty(AlchemancyProperties.SWEET, AlchemancyTags.EntityTypes.TEMPTED_BY_SWEET);
      registerTemptProperty(AlchemancyProperties.WEALTHY, AlchemancyTags.EntityTypes.TEMPTED_BY_WEALTHY);
      registerTemptProperty(AlchemancyProperties.SEEDED, AlchemancyTags.EntityTypes.AGGROED_BY_SEEDED);
      registerTemptProperty(AlchemancyProperties.PUTRID, AlchemancyTags.EntityTypes.TEMPTED_BY_PUTRID);
   }

   public interface Action {
      void perform();
   }
}
