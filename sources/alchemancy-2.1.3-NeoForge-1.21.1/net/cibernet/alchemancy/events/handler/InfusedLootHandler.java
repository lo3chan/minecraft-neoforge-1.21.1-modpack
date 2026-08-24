package net.cibernet.alchemancy.events.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.AlchemancyConfig;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber
public class InfusedLootHandler {
   private static final TagKey<Item> PROVIDES_ARROW = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("alchemancy", "provides_arrow"));

   @SubscribeEvent
   private static void finalizeSpawn(FinalizeSpawnEvent event) {
      if (AlchemancyConfig.Server.infusedLoot() && event.getLevel().getRandom().nextFloat() < 0.01F) {
         markForInfusions(event.getEntity());
      }
   }

   @SubscribeEvent
   private static void onEntityTick(Pre event) {
      Level level = event.getEntity().level();
      if (!level.isClientSide() && AlchemancyConfig.Server.infusedLoot() && isMarkedForInfusions(event.getEntity()) && event.getEntity() instanceof Mob entity) {
         List<EquipmentSlot> slots = Arrays.asList(EquipmentSlot.values());
         Collections.shuffle(slots);

         for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (slot == EquipmentSlot.OFFHAND && stack.isEmpty() && entity.getMainHandItem().is(PROVIDES_ARROW)) {
               stack = Items.ARROW.getDefaultInstance();
               stack.setCount(entity.getRandom().nextInt(8) + 1);
            }

            if (!stack.isEmpty()) {
               RandomSource randomSource = entity.getRandom();
               stack = applyRandomProperties(level, stack, slot.getName(), randomSource);
               if (!stack.isEmpty()) {
                  entity.setDropChance(slot, 1.0F);
                  entity.setItemSlot(slot, stack);
               }
               break;
            }
         }

         resolveMarkForInfusions(entity);
      }
   }

   public static void infuseRandomItems(Level level, float luck, List<ItemStack> pool) {
      if (AlchemancyConfig.Server.infusedLoot() && !(level.getRandom().nextFloat() < 0.02F)) {
         RandomSource randomSource = level.getRandom();
         ArrayList<ItemStack> poolCopy = new ArrayList<>(pool);
         Collections.shuffle(poolCopy);
         Optional<ItemStack> tool = poolCopy.stream().filter(ItemStack::isDamageableItem).findAny();
         tool.ifPresent(stackx -> applyRandomProperties(level, stackx, Property.getEquipmentSlotForItem(stackx).getName(), randomSource));
         poolCopy.removeIf(ItemStack::isDamageableItem);
         if (randomSource.nextBoolean()) {
            for (int i = 0; i < randomSource.nextInt(8) + luck && i < poolCopy.size(); i++) {
               ItemStack stack = poolCopy.get(i);
               applyRandomProperties(level, stack, stack.has(DataComponents.FOOD) ? "food" : "material", randomSource);
            }
         }
      }
   }

   private static ItemStack applyRandomProperties(Level level, ItemStack stack, String tagKey, RandomSource randomSource) {
      boolean success = false;

      for (int i = 0; i < 3 && randomSource.nextBoolean() && applyRandomProperty(level, stack, tagKey, randomSource, false); i++) {
         success = true;
      }

      if (randomSource.nextFloat() < 0.05F) {
         applyRandomProperty(level, stack, tagKey, randomSource, true);
         success = true;
      }

      if (success) {
         ItemStack newStack = ForgeRecipeGrid.resolveInteractions(stack, level);
         stack.set(AlchemancyItems.Components.INFUSED_PROPERTIES, (InfusedPropertiesComponent)newStack.get(AlchemancyItems.Components.INFUSED_PROPERTIES));
         stack.set(AlchemancyItems.Components.PROPERTY_DATA, (PropertyDataComponent)newStack.get(AlchemancyItems.Components.PROPERTY_DATA));
         if (!InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty()) {
            return newStack;
         }
      }

      return ItemStack.EMPTY;
   }

   private static boolean applyRandomProperty(Level level, ItemStack stack, String tagKey, RandomSource randomSource, boolean isRare) {
      Optional<Named<Property>> validProperties = level.registryAccess().lookupOrThrow(AlchemancyProperties.REGISTRY_KEY).get(getLootTag(tagKey, isRare));
      if (!validProperties.isEmpty() && !validProperties.get().stream().findAny().isEmpty()) {
         Optional<Holder<Property>> property = validProperties.get().getRandomElement(randomSource);
         if (property.isEmpty()) {
            return false;
         } else {
            InfusedPropertiesHelper.addProperty(stack, property.get());
            return true;
         }
      } else {
         return false;
      }
   }

   private static TagKey<Property> getLootTag(String tagKey, boolean isRare) {
      return TagKey.create(
         AlchemancyProperties.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("alchemancy", "loot_infusions/" + (isRare ? "rare/" : "") + tagKey)
      );
   }

   private static void markForInfusions(Entity target) {
      target.getPersistentData().putBoolean("alchemancy:marked_for_infusions", true);
   }

   private static void resolveMarkForInfusions(Entity target) {
      target.getPersistentData().putBoolean("alchemancy:marked_for_infusions", false);
   }

   private static boolean isMarkedForInfusions(Entity target) {
      return target.getPersistentData().getBoolean("alchemancy:marked_for_infusions");
   }
}
