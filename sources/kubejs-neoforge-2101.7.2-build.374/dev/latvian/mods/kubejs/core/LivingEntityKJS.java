package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.entity.EntityPotionEffectsJS;
import dev.latvian.mods.kubejs.entity.KubeRayTraceResult;
import dev.latvian.mods.kubejs.item.FoodEatenKubeEvent;
import dev.latvian.mods.kubejs.item.ItemBehavior;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface LivingEntityKJS extends EntityKJS {
   ResourceLocation KJS_PLAYER_CUSTOM_SPEED = KubeJS.id("player.speed.modifier");

   @HideFromJS
   default LivingEntity kjs$self() {
      return (LivingEntity)this;
   }

   default void kjs$foodEaten(ItemStack eatenStack, FoodProperties food) {
      FoodEatenKubeEvent event = new FoodEatenKubeEvent(this.kjs$self(), eatenStack);
      Item item = eatenStack.getItem();
      ItemBehavior behavior = item.kjs$getItemBehavior();
      if (behavior != null && behavior.foodEaten != null) {
         behavior.foodEaten.accept(event);
      }

      ResourceKey<Item> key = item.kjs$getKey();
      if (ItemEvents.FOOD_EATEN.hasListeners(key)) {
         ItemEvents.FOOD_EATEN.post(this.kjs$self(), key, event);
      }
   }

   @ThisIs({LivingEntity.class})
   @Override
   default boolean kjs$isLiving() {
      return true;
   }

   @Info(
      value = "Sets the entity's maximum health to specified HP.",
      params = {@Param(
         name = "hp",
         value = "The new maximum health of the entity."
      )}
   )
   default void kjs$setMaxHealth(float hp) {
      this.kjs$self().getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
   }

   default boolean kjs$isUndead() {
      return this.kjs$self().isInvertedHealAndHarm();
   }

   default EntityPotionEffectsJS kjs$getPotionEffects() {
      return new EntityPotionEffectsJS(this.kjs$self());
   }

   default void kjs$swing(InteractionHand hand) {
      this.kjs$self().swing(hand, true);
   }

   default void kjs$swing() {
      this.kjs$self().swing(InteractionHand.MAIN_HAND, true);
   }

   default ItemStack kjs$getEquipment(EquipmentSlot slot) {
      return this.kjs$self().getItemBySlot(slot);
   }

   default void kjs$setEquipment(EquipmentSlot slot, ItemStack item) {
      this.kjs$self().setItemSlot(slot, item);
   }

   default ItemStack kjs$getHeldItem(InteractionHand hand) {
      return this.kjs$self().getItemInHand(hand);
   }

   default void kjs$setHeldItem(InteractionHand hand, ItemStack item) {
      this.kjs$self().setItemInHand(hand, item);
   }

   default ItemStack kjs$getMainHandItem() {
      return this.kjs$getEquipment(EquipmentSlot.MAINHAND);
   }

   default void kjs$setMainHandItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.MAINHAND, item);
   }

   default ItemStack kjs$getOffHandItem() {
      return this.kjs$getEquipment(EquipmentSlot.OFFHAND);
   }

   default void kjs$setOffHandItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.OFFHAND, item);
   }

   default ItemStack kjs$getHeadArmorItem() {
      return this.kjs$getEquipment(EquipmentSlot.HEAD);
   }

   default void kjs$setHeadArmorItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.HEAD, item);
   }

   default ItemStack kjs$getChestArmorItem() {
      return this.kjs$getEquipment(EquipmentSlot.CHEST);
   }

   default void kjs$setChestArmorItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.CHEST, item);
   }

   default ItemStack kjs$getLegsArmorItem() {
      return this.kjs$getEquipment(EquipmentSlot.LEGS);
   }

   default void kjs$setLegsArmorItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.LEGS, item);
   }

   default ItemStack kjs$getFeetArmorItem() {
      return this.kjs$getEquipment(EquipmentSlot.FEET);
   }

   default void kjs$setFeetArmorItem(ItemStack item) {
      this.kjs$setEquipment(EquipmentSlot.FEET, item);
   }

   default void kjs$damageEquipment(EquipmentSlot slot, int amount, Consumer<ItemStack> onBroken) {
      ItemStack stack = this.kjs$self().getItemBySlot(slot);
      if (!stack.isEmpty()) {
         stack.hurtAndBreak(amount, (ServerLevel)this.kjs$self().level(), this.kjs$self(), item -> onBroken.accept(stack));
         if (stack.isEmpty()) {
            this.kjs$self().setItemSlot(slot, ItemStack.EMPTY);
         }
      }
   }

   default void kjs$damageEquipment(EquipmentSlot slot, int amount) {
      this.kjs$damageEquipment(slot, amount, stack -> {});
   }

   default void kjs$damageEquipment(EquipmentSlot slot) {
      this.kjs$damageEquipment(slot, 1);
   }

   default void kjs$damageHeldItem(InteractionHand hand, int amount, Consumer<ItemStack> onBroken) {
      this.kjs$damageEquipment(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, amount, onBroken);
   }

   default void kjs$damageHeldItem(InteractionHand hand, int amount) {
      this.kjs$damageHeldItem(hand, amount, stack -> {});
   }

   default void kjs$damageHeldItem() {
      this.kjs$damageHeldItem(InteractionHand.MAIN_HAND, 1);
   }

   default boolean kjs$isHoldingInAnyHand(ItemPredicate itemPredicate) {
      return itemPredicate.test(this.kjs$self().getItemInHand(InteractionHand.MAIN_HAND))
         || itemPredicate.test(this.kjs$self().getItemInHand(InteractionHand.OFF_HAND));
   }

   default double kjs$getTotalMovementSpeed() {
      return this.kjs$self().getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   default double kjs$getDefaultMovementSpeed() {
      return this.kjs$self().getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
   }

   default void kjs$setDefaultMovementSpeed(double speed) {
      this.kjs$self().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
   }

   default void kjs$setMovementSpeedAddition(double speed) {
      AttributeInstance instance = this.kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
      if (instance != null) {
         instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
         instance.addTransientModifier(this.kjs$createSpeedModifier(speed, Operation.ADD_VALUE));
      }
   }

   default void kjs$setDefaultMovementSpeedMultiplier(double speed) {
      AttributeInstance instance = this.kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
      if (instance != null) {
         instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
         instance.addTransientModifier(this.kjs$createSpeedModifier(speed, Operation.ADD_MULTIPLIED_BASE));
      }
   }

   default void kjs$setTotalMovementSpeedMultiplier(double speed) {
      AttributeInstance instance = this.kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
      if (instance != null) {
         instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
         instance.addTransientModifier(this.kjs$createSpeedModifier(speed, Operation.ADD_MULTIPLIED_TOTAL));
      }
   }

   default boolean kjs$canEntityBeSeen(LivingEntity entity) {
      return BehaviorUtils.canSee(this.kjs$self(), entity);
   }

   default double kjs$getReachDistance() {
      return this.kjs$self().getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
   }

   default KubeRayTraceResult kjs$rayTrace() {
      return this.kjs$rayTrace(this.kjs$getReachDistance());
   }

   @Nullable
   default Entity kjs$rayTraceEntity(Predicate<Entity> filter) {
      return this.kjs$rayTraceEntity(this.kjs$getReachDistance(), filter);
   }

   default double kjs$getAttributeTotalValue(Holder<Attribute> attribute) {
      AttributeInstance instance = this.kjs$self().getAttribute(attribute);
      return instance != null ? instance.getValue() : 0.0;
   }

   default double kjs$getAttributeBaseValue(Holder<Attribute> attribute) {
      AttributeInstance instance = this.kjs$self().getAttribute(attribute);
      return instance != null ? instance.getBaseValue() : 0.0;
   }

   default void kjs$setAttributeBaseValue(Holder<Attribute> attribute, double value) {
      AttributeInstance instance = this.kjs$self().getAttribute(attribute);
      if (instance != null) {
         instance.setBaseValue(value);
      }
   }

   default void kjs$modifyAttribute(Holder<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
      AttributeInstance instance = this.kjs$self().getAttribute(attribute);
      if (instance != null) {
         instance.removeModifier(id);
         instance.addTransientModifier(new AttributeModifier(id, amount, operation));
      }
   }

   default void kjs$removeAttribute(Holder<Attribute> attribute, ResourceLocation id) {
      AttributeInstance instance = this.kjs$self().getAttribute(attribute);
      if (instance != null) {
         instance.removeModifier(id);
      }
   }

   private AttributeModifier kjs$createSpeedModifier(double speed, Operation operation) {
      return new AttributeModifier(KJS_PLAYER_CUSTOM_SPEED, speed, operation);
   }
}
