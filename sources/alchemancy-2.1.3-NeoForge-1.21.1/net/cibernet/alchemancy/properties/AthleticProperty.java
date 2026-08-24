package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber
public class AthleticProperty extends Property {
   private static final ResourceLocation MOD_KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "athletic_property_modifier");
   private static final AttributeModifier SPEED_MOD = new AttributeModifier(MOD_KEY, 0.6499999761581421, Operation.ADD_MULTIPLIED_TOTAL);
   private static final AttributeModifier SAFE_FALL_MOD = new AttributeModifier(MOD_KEY, 3.0, Operation.ADD_VALUE);

   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      event.setNewDamage(event.getNewDamage() * (1.0F + getDepthScale(user) * 0.75F));
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) {
         AttributeInstance movementSpeed = user.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
         if (movementSpeed != null) {
            movementSpeed.removeModifier(MOD_KEY);
            if (user.isSprinting()) {
               movementSpeed.addPermanentModifier(SPEED_MOD);
            }
         }

         AttributeInstance jumpStrength = user.getAttributes().getInstance(Attributes.JUMP_STRENGTH);
         if (jumpStrength != null) {
            jumpStrength.removeModifier(MOD_KEY);
            if (user.isSprinting()) {
               jumpStrength.addPermanentModifier(SPEED_MOD);
            }
         }

         AttributeInstance safeFall = user.getAttributes().getInstance(Attributes.SAFE_FALL_DISTANCE);
         if (safeFall != null) {
            safeFall.removeModifier(MOD_KEY);
            if (user.isSprinting()) {
               safeFall.addPermanentModifier(SAFE_FALL_MOD);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre event) {
      if (!event.getEntity().level().isClientSide) {
         Player player = event.getEntity();
         AttributeInstance moveSpeed = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
         AttributeInstance jumpStrength = player.getAttributes().getInstance(Attributes.JUMP_STRENGTH);
         AttributeInstance safeFall = player.getAttributes().getInstance(Attributes.SAFE_FALL_DISTANCE);
         if (moveSpeed != null && moveSpeed.hasModifier(MOD_KEY)
            || jumpStrength != null && jumpStrength.hasModifier(MOD_KEY)
            || safeFall != null && safeFall.hasModifier(MOD_KEY)) {
            boolean equipped = false;

            for (EquipmentSlot slot : EquipmentSlot.values()) {
               if (slot.isArmor() && InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.ATHLETIC)) {
                  equipped = true;
                  break;
               }
            }

            if (!equipped) {
               if (moveSpeed != null) {
                  moveSpeed.removeModifier(MOD_KEY);
               }

               if (jumpStrength != null) {
                  jumpStrength.removeModifier(MOD_KEY);
               }

               if (safeFall != null) {
                  safeFall.removeModifier(MOD_KEY);
               }
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
      return 4234679;
   }
}
