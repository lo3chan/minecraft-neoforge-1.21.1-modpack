package net.cibernet.alchemancy.properties;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public abstract class Property {
   public static final Codec<Holder<Property>> CODEC = RegistryFixedCodec.create(AlchemancyProperties.REGISTRY.getRegistryKey());
   public static final Codec<List<Holder<Property>>> LIST_CODEC = Codec.withAlternative(Codec.list(CODEC), CODEC, List::of);
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Property>> STREAM_CODEC = ByteBufCodecs.holderRegistry(
      AlchemancyProperties.REGISTRY.getRegistryKey()
   );

   public ResourceLocation getKey() {
      return AlchemancyProperties.getKeyFor(this);
   }

   public static Property simple(final UnaryOperator<Style> style, final Supplier<Integer> colorSupplier) {
      return new Property() {
         @Override
         public int getColor(ItemStack stack) {
            return colorSupplier.get();
         }

         @Override
         public Component getDisplayText(ItemStack stack) {
            return super.getDisplayText(stack).copy().withStyle(style);
         }
      };
   }

   public static Property simple(int color) {
      return simple(style -> style, () -> color);
   }

   public static Property simpleSine(float time, boolean bold, int colorA, int colorB) {
      return simple(style -> style.withBold(bold), () -> ColorUtils.sineColorsOverTime(time, colorA, colorB));
   }

   public static Property simpleFlashing(boolean bold, double time, int... colors) {
      return simple(style -> style.withBold(bold), () -> ColorUtils.flashColorsOverTime(time, colors));
   }

   public static Property simpleInterpolated(boolean bold, float time, int... colors) {
      return simple(style -> style.withBold(bold), () -> ColorUtils.interpolateColorsOverTime(time, colors));
   }

   public Holder<Property> asHolder() {
      return AlchemancyProperties.getHolder(this);
   }

   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
   }

   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
   }

   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
   }

   public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event) {
      if (event.isCriticalHit()) {
         this.onCriticalAttack(user, weapon, event.getTarget());
      }
   }

   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      this.onAttack(user, weapon, event.getSource(), event.getEntity());
   }

   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      this.onDamageReceived(user, weapon, slot, event.getSource());
   }

   public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
      if (event.isCanceled()) {
         this.onAttack(user, weapon, event.getSource(), target);
      }
   }

   public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event) {
   }

   public void modifyHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingHealEvent event) {
      this.onHeal(user, stack, slot, event.getAmount());
   }

   public void onHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, float amount) {
   }

   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      this.onCriticalAttack(source instanceof Player player ? player : null, stack, target);
      if (source != null && target instanceof LivingEntity livingTarget) {
         this.onAttack(source, stack, damageSource, livingTarget);
      }
   }

   public boolean shouldActivate(Level level, @Nullable Entity source, Entity target, ItemStack stack) {
      return true;
   }

   public boolean shouldActivateByBlock(Level level, BlockPos pos, Entity target, ItemStack stack) {
      return true;
   }

   public final void onActivation(@Nonnull Entity source, Entity target, ItemStack stack) {
      this.onActivation(source, target, stack, activationDamageSource(source.level(), source, source.position()));
   }

   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {
      this.onActivation(null, target, stack, activationDamageSource(level, null, position.getCenter()));
   }

   public static DamageSource activationDamageSource(Level level, @Nullable Entity source, Vec3 position) {
      return new DamageSource(level.holderOrThrow(DamageTypes.GENERIC), source, source, position);
   }

   public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
   }

   public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event) {
   }

   public void modifyLivingDrops(LivingEntity dropsSource, ItemStack weapon, LivingEntity user, Collection<ItemEntity> drops, LivingDropsEvent event) {
   }

   public void modifyLivingExperienceDrops(Player user, ItemStack weapon, EquipmentSlot slot, LivingEntity entity, LivingExperienceDropEvent event) {
   }

   public abstract int getColor(ItemStack var1);

   public Component getDisplayText(ItemStack stack) {
      return Component.translatable(this.getLanguageKey()).withColor(this.getColor(stack));
   }

   public Component getName(ItemStack stack) {
      return Component.translatable(this.getLanguageKey()).withColor(this.getColor(stack));
   }

   public Component getName() {
      return this.getName(ItemStack.EMPTY);
   }

   public String getRawName() {
      return Component.translatable(this.getLanguageKey()).getString();
   }

   public String getLanguageKey() {
      return "property." + this.getKey().toLanguageKey();
   }

   public int getPriority() {
      return 0;
   }

   @Override
   public String toString() {
      return this.getKey().toString();
   }

   public void onRightClickBlock(UseItemOnBlockEvent event) {
   }

   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
   }

   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
   }

   public void onJump(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingJumpEvent event) {
   }

   public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event) {
   }

   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
   }

   public void applyAttributes(ItemAttributeModifierEvent event) {
   }

   public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
   }

   public void onItemTossed(Player player, ItemStack stack, ItemEntity itemEntity, ItemTossEvent event) {
   }

   public void modifyKnockBackReceived(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingKnockBackEvent event) {
   }

   public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event) {
   }

   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      return resultingAmount;
   }

   public final int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount) {
      return this.modifyDurabilityConsumed(stack, level, user, originalAmount, resultingAmount, user == null ? level.getRandom() : user.getRandom());
   }

   public TriState isItemInTag(ItemStack stack, TagKey<Item> tagKey) {
      return TriState.DEFAULT;
   }

   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return data;
   }

   public float modifyStepOnFriction(Entity user, ItemStack stack, float originalResult, float result) {
      return result;
   }

   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      return false;
   }

   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return result;
   }

   public void onRightClickItem(RightClickItem event) {
   }

   public void onRightClickItemPost(RightClickItem event) {
   }

   public void onRightClickEntity(EntityInteractSpecific event) {
   }

   public boolean modifyAcceptAbility(ItemStack stack, ItemAbility itemAbility, boolean original, boolean result) {
      return result;
   }

   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {
   }

   public void modifyEnchantmentLevels(GetEnchantmentLevelEvent event) {
   }

   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
   }

   public static boolean entityLowOnHealth(LivingEntity entity) {
      return entity.getHealth() / entity.getMaxHealth() <= 0.2F;
   }

   public static EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
      EquipmentSlot slot = stack.getEquipmentSlot();
      if (slot != null) {
         return slot;
      } else {
         Equipable equipable = Equipable.get(stack);
         return equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
      }
   }

   public ItemStack consumeItem(@Nullable Entity user, ItemStack stack, @Nullable EquipmentSlot brokenOnSlot) {
      if (PropertyModifierComponent.getOrElse(stack, this.asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, false)) {
         return stack;
      } else {
         if (brokenOnSlot != null && user instanceof Player player) {
            player.onEquippedItemBroken(stack.getItem(), brokenOnSlot);
            player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
         }

         stack.shrink(1);
         return stack;
      }
   }

   public static void modifyEnchantmentLevel(
      Mutable enchantments, RegistryLookup<Enchantment> lookup, ResourceKey<Enchantment> enchantmentKey, Function<Integer, Integer> func
   ) {
      Optional<Reference<Enchantment>> enchantment = lookup.get(enchantmentKey);
      enchantment.ifPresent(enchantmentReference -> enchantments.set(enchantmentReference, func.apply(enchantments.getLevel(enchantmentReference))));
   }

   public static double getItemAttackDamage(ItemStack stack) {
      return getItemAttackDamage(EquipmentSlotGroup.MAINHAND, stack, 1.0);
   }

   public static double getItemAttackDamage(EquipmentSlotGroup slots, ItemStack stack, double baseValue) {
      AtomicReference<Double> d0 = new AtomicReference<>(baseValue);
      stack.getAttributeModifiers().forEach(slots, (attributeHolder, attributeModifier) -> {
         double d1 = attributeModifier.amount();
         d0.updateAndGet(v -> {
            return v + switch (attributeModifier.operation()) {
               case ADD_VALUE -> d1;
               case ADD_MULTIPLIED_BASE -> d1 * baseValue;
               case ADD_MULTIPLIED_TOTAL -> d1 * d0.get();
               default -> throw new MatchException(null, null);
            };
         });
      });
      return d0.get();
   }

   public void damageOrConsumeItem(@Nonnull Entity user, ItemStack stack, @Nullable EquipmentSlot slot, int amount) {
      this.damageOrConsumeItem(user.level(), user, stack, slot, amount);
   }

   public void damageOrConsumeItem(Level level, @Nullable Entity user, ItemStack stack, @Nullable EquipmentSlot slot, int amount) {
      if (!stack.has(DataComponents.UNBREAKABLE)) {
         if (slot == null) {
            slot = EquipmentSlot.MAINHAND;
         }

         if (PropertyModifierComponent.getOrElse(stack, this.asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem())) {
            int durabilityConsumed = PropertyModifierComponent.<Integer>get(stack, this.asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION)
               * amount;
            if (user instanceof LivingEntity livingEntity) {
               stack.hurtAndBreak(durabilityConsumed, livingEntity, slot);
            } else if (level instanceof ServerLevel serverLevel) {
               stack.hurtAndBreak(durabilityConsumed, serverLevel, null, item -> {});
            }
         } else {
            this.consumeItem(user, stack, slot);
         }
      }
   }

   public boolean damageItem(@Nonnull Entity user, ItemStack stack, EquipmentSlot slot, int amount) {
      return this.damageItem(user.level(), user, stack, slot, amount);
   }

   public boolean damageItem(Level level, @Nullable Entity user, ItemStack stack, EquipmentSlot slot, int amount) {
      if (!stack.isDamageableItem() && !PropertyModifierComponent.<Boolean>get(stack, this.asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION)) {
         return false;
      } else {
         int durabilityConsumed = PropertyModifierComponent.<Integer>get(stack, this.asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION)
            * amount;
         if (user instanceof LivingEntity livingEntity) {
            stack.hurtAndBreak(durabilityConsumed, livingEntity, slot);
         } else if (level instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(durabilityConsumed, serverLevel, null, item -> {});
         }

         return true;
      }
   }

   public static void playRootedParticles(RootedItemBlockEntity root, RandomSource random, ParticleOptions particles) {
      BlockPos pPos = root.getBlockPos();
      Level pLevel = root.getLevel();
      double d0 = pPos.getX();
      double d1 = pPos.getY();
      double d2 = pPos.getZ();
      double d5 = random.nextDouble();
      double d6 = random.nextDouble();
      double d7 = random.nextDouble();
      pLevel.addParticle(particles, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
   }

   public void onKill(LivingEntity target, LivingEntity user, ItemStack stack, LivingDeathEvent event) {
   }

   public void onUserDeath(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingDeathEvent event) {
   }

   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      return !InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder());
   }

   public void onInfusedByForgeRecipe(ItemStack stack, ForgePropertyRecipe recipe, ForgeRecipeGrid grid) {
   }

   public void onStopUsingItem(ItemStack stack, LivingEntity user, Stop event) {
   }

   public void onProjectileTick(ItemStack stack, Projectile projectile) {
   }

   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
   }

   public void onStackedOverItem(
      ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
   }

   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
   }

   public void onPickUpAnyItem(
      Player user,
      ItemStack stack,
      EquipmentSlot slot,
      ItemEntity itemToPickUp,
      boolean canPickUp,
      net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre event
   ) {
   }

   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      return List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), holder));
   }

   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return current;
   }

   public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
      return slot;
   }

   public void onItemUseTick(LivingEntity user, ItemStack stack, Tick event) {
   }

   public TriState allowArrowClipBlocks(AbstractArrow arrow, ItemStack stack) {
      return TriState.DEFAULT;
   }

   @Nullable
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      return null;
   }

   public boolean hasJournalEntry() {
      return true;
   }

   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
   }

   public int modifyEnchantmentValue(int originalValue, int result) {
      return result;
   }

   public void onMobEffectAdded(ItemStack stack, EquipmentSlot slot, LivingEntity user, Added event) {
   }

   public void isMobEffectApplicable(ItemStack stack, EquipmentSlot slot, LivingEntity user, Applicable event) {
   }

   public int onItemRepaired(ItemStack stack, int amount, int originalAmount) {
      return amount;
   }

   public boolean onEntityItemBelowWorld(ItemStack stack, ItemEntity itemEntity) {
      return false;
   }

   public static boolean canRepair(ItemStack stack) {
      return canRepair(stack, 1);
   }

   public static boolean canRepair(ItemStack stack, int amountToRepair) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LIVING_BATTERY)) {
         IEnergyStorage energy = (IEnergyStorage)stack.getCapability(EnergyStorage.ITEM);
         if (energy != null && energy.receiveEnergy(amountToRepair * 50, true) > 0) {
            return true;
         }
      }

      return stack.isDamageableItem() && stack.getDamageValue() >= amountToRepair;
   }

   public static void activateByEntity(@Nonnull Entity user, Entity target, ItemStack stack) {
      AtomicBoolean activate = new AtomicBoolean(true);
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
         if (activate.get() && !((Property)propertyHolder.value()).shouldActivate(user.level(), user, target, stack)) {
            activate.set(false);
         }
      });
      if (activate.get()) {
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onActivation(user, target, stack));
      }
   }

   public static void activateByBlock(Level level, BlockPos pos, Entity target, ItemStack stack) {
      AtomicBoolean activate = new AtomicBoolean(true);
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
         if (activate.get() && !((Property)propertyHolder.value()).shouldActivateByBlock(level, pos, target, stack)) {
            activate.set(false);
         }
      });
      if (activate.get()) {
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onActivationByBlock(level, pos, target, stack));
      }
   }

   public static void activateByBlock(ItemStackHolderBlockEntity root, Entity target) {
      activateByBlock(root.getLevel(), root.getBlockPos(), target, root.getItem());
   }

   public static void repairItem(ItemStack stack, int amount) {
      AtomicInteger newAmount = new AtomicInteger(amount);
      InfusedPropertiesHelper.forEachProperty(
         stack, propertyHolder -> newAmount.set(((Property)propertyHolder.value()).onItemRepaired(stack, newAmount.get(), amount))
      );
      if (newAmount.get() > 0 && stack.isDamaged()) {
         stack.setDamageValue(Math.max(0, stack.getDamageValue() - newAmount.get()));
      }
   }

   public boolean preventRecursion(ItemStack item) {
      return item.has(AlchemancyItems.Components.RECURSION_PREVENTION);
   }

   public final TagKey<Item> getDormantPropertyTag() {
      ResourceLocation id = this.asHolder().getKey().location();
      return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "dormant_properties/" + id.getPath()));
   }

   public static class Priority {
      public static final int LOWEST = 2147483647;
      public static final int LOWER = 100;
      public static final int LOW = 50;
      public static final int NORMAL = 0;
      public static final int HIGH = -50;
      public static final int HIGHER = -100;
      public static final int HIGHEST = -2147483648;
   }
}
