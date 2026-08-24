package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.advancements.criterion.DiscoverPropertyTrigger;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class DepthDwellerProperty extends Property {
   private static final ResourceLocation MOD_KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "depth_dweller_property_modifier");

   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      event.setNewDamage(event.getNewDamage() * (1.0F + getDepthScale(user) * 0.75F));
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      float value = getDepthScale(user) * 0.5F;
      if (user instanceof Player player && slot == EquipmentSlot.MAINHAND) {
         AttributeInstance blockBreakSpeed = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
         blockBreakSpeed.removeModifier(MOD_KEY);
         blockBreakSpeed.addPermanentModifier(new AttributeModifier(MOD_KEY, value, Operation.ADD_MULTIPLIED_TOTAL));
      }

      if (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) {
         AttributeInstance movementSpeed = user.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
         movementSpeed.removeModifier(MOD_KEY);
         movementSpeed.addPermanentModifier(new AttributeModifier(MOD_KEY, value, Operation.ADD_MULTIPLIED_TOTAL));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre event) {
      if (!event.getEntity().level().isClientSide) {
         Player player = event.getEntity();
         AttributeInstance blockBreakSpeed = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
         if (blockBreakSpeed != null
            && blockBreakSpeed.hasModifier(MOD_KEY)
            && !InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.DEPTH_DWELLER)) {
            blockBreakSpeed.removeModifier(MOD_KEY);
         }

         AttributeInstance moveSpeed = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
         if (moveSpeed != null && moveSpeed.hasModifier(MOD_KEY)) {
            boolean equipped = false;

            for (EquipmentSlot slot : EquipmentSlot.values()) {
               if (slot.isArmor() && InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.DEPTH_DWELLER)) {
                  equipped = true;
                  break;
               }
            }

            if (!equipped) {
               moveSpeed.removeModifier(MOD_KEY);
            }
         }
      }
   }

   public static float getDepthScale(Entity user) {
      return user.level().dimensionTypeRegistration().is(AlchemancyTags.Dimensions.DEPTH_DWELLER_EFFECTIVE)
         ? 2.0F
         : Mth.clamp(((float)user.position().y - 10.0F) / (user.level().getMinBuildHeight() - 10), 0.0F, 1.0F);
   }

   @Override
   public int getColor(ItemStack stack) {
      if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer) {
         return 6579300;
      } else {
         float scale = getDepthScale(ClientUtil.getLocalPlayer());
         return scale > 1.0F ? 5313813 : ARGB32.lerp(scale, 6579300, 3092279);
      }
   }

   @Override
   public boolean onEntityItemBelowWorld(ItemStack stack, ItemEntity itemEntity) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.UNDYING)) {
         InfusedPropertiesHelper.removeProperty(stack, AlchemancyProperties.UNDYING);
         InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         itemEntity.level()
            .playSound(null, itemEntity.position().x, itemEntity.position().y, itemEntity.position().z, SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 0.65F, 0.5F);
         InfusedPropertiesHelper.addProperty(stack, AlchemancyProperties.VOIDBORN);
         itemEntity.setItem(ForgeRecipeGrid.resolveInteractions(stack, itemEntity.level()));
         if (itemEntity.getOwner() instanceof ServerPlayer serverPlayer) {
            ItemStack newItem = itemEntity.getItem();
            ((DiscoverPropertyTrigger)AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get()).trigger(serverPlayer, stack);
            ((DiscoverPropertyTrigger)AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get()).trigger(serverPlayer, newItem);
         }

         LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(itemEntity.level());
         if (lightningbolt != null) {
            lightningbolt.moveTo(itemEntity.getX(), Math.max((double)(itemEntity.level().getMinBuildHeight() - 60), itemEntity.getY()), itemEntity.getZ());
            lightningbolt.setVisualOnly(true);
            itemEntity.level().addFreshEntity(lightningbolt);
         }

         return true;
      } else {
         return false;
      }
   }
}
