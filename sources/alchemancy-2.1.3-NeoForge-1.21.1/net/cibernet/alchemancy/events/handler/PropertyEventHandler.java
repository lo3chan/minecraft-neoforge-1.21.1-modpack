package net.cibernet.alchemancy.events.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.cibernet.alchemancy.entity.ai.ScareGoal;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.ItemEntityAccessor;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.network.S2CInventoryTickPayload;
import net.cibernet.alchemancy.properties.HollowProperty;
import net.cibernet.alchemancy.properties.LoyalProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.special.AuxiliaryProperty;
import net.cibernet.alchemancy.properties.voidborn.VoidtouchProperty;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent.UsePhase;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PropertyEventHandler {
   private static final HashMap<EquipmentSlot, List<Holder<GameEvent>>> MUFFLED_EVENTS = new HashMap<EquipmentSlot, List<Holder<GameEvent>>>() {
      {
         this.put(
            EquipmentSlot.MAINHAND,
            List.of(
               GameEvent.BLOCK_PLACE,
               GameEvent.ITEM_INTERACT_START,
               GameEvent.ITEM_INTERACT_FINISH,
               GameEvent.ENTITY_PLACE,
               GameEvent.ENTITY_ACTION,
               GameEvent.SHEAR,
               GameEvent.PROJECTILE_SHOOT
            )
         );
         this.put(
            EquipmentSlot.OFFHAND,
            List.of(
               GameEvent.BLOCK_PLACE,
               GameEvent.ITEM_INTERACT_START,
               GameEvent.ITEM_INTERACT_FINISH,
               GameEvent.ENTITY_PLACE,
               GameEvent.ENTITY_ACTION,
               GameEvent.SHEAR
            )
         );
         this.put(EquipmentSlot.HEAD, List.of(GameEvent.EAT, GameEvent.DRINK));
         this.put(EquipmentSlot.CHEST, List.of(GameEvent.ENTITY_DAMAGE, GameEvent.ENTITY_DIE, GameEvent.ELYTRA_GLIDE, GameEvent.TELEPORT));
         this.put(EquipmentSlot.LEGS, List.of(GameEvent.ENTITY_DAMAGE, GameEvent.ENTITY_DIE, GameEvent.SPLASH, GameEvent.SWIM, GameEvent.TELEPORT));
         this.put(EquipmentSlot.FEET, List.of(GameEvent.STEP, GameEvent.HIT_GROUND, GameEvent.SPLASH));
      }
   };
   private static final Component PROPERTY_INGREDIENT_NAME = Component.translatable("item.alchemancy.property_capsule.ingredient");

   @SubscribeEvent
   public static void onEntityInvulnerableCheck(EntityInvulnerabilityCheckEvent event) {
      ItemStack stack = ItemStack.EMPTY;
      if (event.getEntity() instanceof ItemEntity itemEntity) {
         stack = itemEntity.getItem();
      } else if (event.getEntity() instanceof ItemSupplier itemSupplier) {
         stack = itemSupplier.getItem();
      }

      if (!stack.isEmpty()) {
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ETERNAL)) {
            event.setInvulnerable(true);
            return;
         }

         if (event.getSource().is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INSULATED)) {
            event.setInvulnerable(true);
         }

         if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BLAST_RESISTANT)) {
            event.setInvulnerable(true);
         }

         if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.VOIDBORN)) {
            event.setInvulnerable(true);
         }

         if (event.getSource().is(AlchemancyTags.DamageTypes.AFFECTED_BY_SMOOTH) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SMOOTH)) {
            event.setInvulnerable(true);
         }
      }

      if (event.getEntity() instanceof LivingEntity living
         && InfusedPropertiesHelper.hasItemWithProperty(living, AlchemancyProperties.VOIDBORN, true)
         && (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) || event.getSource().is(VoidtouchProperty.VOIDTOUCH_DAMAGE_KEY))) {
         event.setInvulnerable(true);
      }
   }

   @SubscribeEvent
   public static void onVisibilityCheck(LivingVisibilityEvent event) {
      double modifier = event.getVisibilityModifier();
      if (event.getLookingEntity() != null && event.getLookingEntity().getType().is(AlchemancyTags.EntityTypes.TEMPTED_BY_PUTRID)) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (InfusedPropertiesHelper.hasProperty(event.getEntity().getItemBySlot(slot), AlchemancyProperties.PUTRID)) {
               modifier += 0.25;
            }
         }
      }

      event.modifyVisibility(modifier);
   }

   @SubscribeEvent
   public static void onLivingHeal(LivingHealEvent event) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).modifyHeal(event.getEntity(), stack, slot, event));
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Pre event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user) {
         ItemStack stack = user.getMainHandItem();
         InfusedPropertiesHelper.forEachProperty(stack, holder -> ((Property)holder.value()).modifyAttackDamage(user, stack, event));
      } else if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_PROJECTILE_HIT_EFFECTS)
         && event.getSource().getDirectEntity() instanceof Projectile projectile) {
         ItemStack stack = getProjectileItemStack(projectile);
         if (!stack.isEmpty()) {
            InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).modifyAttackDamage(projectile, stack, event));
         }
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         float currentDamage = event.getNewDamage();
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).modifyDamageReceived(event.getEntity(), stack, slot, event)
         );
         if (currentDamage >= 2.1474836E9F) {
            event.setNewDamage(currentDamage);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user) {
         ItemStack stack = user.getMainHandItem();
         InfusedPropertiesHelper.forEachProperty(stack, holder -> ((Property)holder.value()).onKill(event.getEntity(), user, stack, event));
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onUserDeath(event.getEntity(), stack, slot, event));
      }
   }

   @SubscribeEvent
   public static void onIncomingDamage(LivingIncomingDamageEvent event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user) {
         ItemStack stack = user.getMainHandItem();
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).onIncomingAttack(user, stack, event.getEntity(), event)
         );
      } else if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_PROJECTILE_HIT_EFFECTS)
         && event.getSource().getDirectEntity() instanceof Projectile projectile) {
         ItemStack stack = getProjectileItemStack(projectile);
         if (!stack.isEmpty()) {
            InfusedPropertiesHelper.forEachProperty(
               stack, propertyHolder -> ((Property)propertyHolder.value()).onIncomingAttack(projectile, stack, event.getEntity(), event)
            );
         }
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).onIncomingDamageReceived(event.getEntity(), stack, slot, event.getSource(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onCriticalHit(CriticalHitEvent event) {
      ItemStack stack = event.getEntity().getMainHandItem();
      InfusedPropertiesHelper.forEachProperty(stack, holder -> ((Property)holder.value()).modifyCriticalAttack(event.getEntity(), stack, event));
   }

   @SubscribeEvent
   public static void onKnockBack(LivingKnockBackEvent event) {
      LivingEntity target = event.getEntity();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = target.getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).modifyKnockBackReceived(target, stack, slot, event)
         );
      }

      if (target.getLastHurtByMobTimestamp() == target.tickCount && target.getLastHurtByMob() != null) {
         LivingEntity user = target.getLastHurtByMob();
         ItemStack weapon = user.getMainHandItem();
         InfusedPropertiesHelper.forEachProperty(
            weapon, propertyHolder -> ((Property)propertyHolder.value()).modifyKnockBackApplied(user, weapon, target, event)
         );
      }
   }

   @SubscribeEvent
   public static void onPlayerTickPost(Post event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer && !serverPlayer.isSpectator() && !serverPlayer.isDeadOrDying()) {
         S2CInventoryTickPayload.sendPacket(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onEntityTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
      Entity user = event.getEntity();
      if (!user.isSpectator() && !(user instanceof LivingEntity living && living.isDeadOrDying())) {
         if (user.level().isClientSide() && user instanceof LocalPlayer localPlayer) {
            localPlayer.connection
               .send(new ServerboundPlayerInputPacket(localPlayer.xxa, localPlayer.zza, ((LivingEntityAccessor)user).isJumping(), localPlayer.isShiftKeyDown()));
         }

         Objects.requireNonNull(user);
         switch (user) {
            case LivingEntity livingx when !(livingx instanceof Player):
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  ItemStack stackx = livingx.getItemBySlot(slot);
                  InfusedPropertiesHelper.forEachProperty(stackx, propertyHolder -> ((Property)propertyHolder.value()).onEquippedTick(living, slot, stack));
               }
               break;
            case ItemEntity itemEntity: {
               ItemStack stack = itemEntity.getItem();
               InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onEntityItemTick(stack, itemEntity));
               break;
            }
            case Projectile projectile: {
               ItemStack stack = getProjectileItemStack(projectile);
               if (stack != null && !stack.isEmpty()) {
                  InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onProjectileTick(stack, projectile));
               }
               break;
            }
            default:
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      ItemStack stack = getProjectileItemStack(projectile);
      if (!stack.isEmpty()) {
         InfusedPropertiesHelper.forEachProperty(
            stack,
            propertyHolder -> {
               if (propertyHolder.equals(AlchemancyProperties.LOYAL)
                  || ((LoyalProperty)AlchemancyProperties.LOYAL.value()).canTriggerImpactEffects(projectile, event.getRayTraceResult())) {
                  ((Property)propertyHolder.value()).onProjectileImpact(stack, projectile, event.getRayTraceResult(), event);
               }
            }
         );
      }
   }

   public static ItemStack getProjectileItemStack(Projectile entity) {
      ItemStack result = null;
      if (entity instanceof ItemSupplier itemSupplier) {
         result = itemSupplier.getItem();
      } else if (entity instanceof AbstractArrow arrow) {
         result = arrow.getPickupItemStackOrigin();
      }

      return result == null ? ItemStack.EMPTY : result;
   }

   @SubscribeEvent
   public static void onItemPickUp(net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre event) {
      ItemEntity itemEntity = event.getItemEntity();
      ItemStack droppedItem = itemEntity.getItem();
      InfusedPropertiesHelper.forEachProperty(
         droppedItem, propertyHolder -> ((Property)propertyHolder.value()).onItemPickedUp(event.getPlayer(), droppedItem, event.getItemEntity())
      );

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getPlayer().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack,
            propertyHolder -> ((Property)propertyHolder.value())
               .onPickUpAnyItem(event.getPlayer(), stack, slot, itemEntity, !itemEntity.hasPickUpDelay(), event)
         );
      }

      if (event.canPickup().isTrue() || event.canPickup().isDefault() && !itemEntity.hasPickUpDelay()) {
         Inventory inventory = event.getPlayer().getInventory();

         for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.COMPACT)
               && stack.getCount() < stack.getMaxStackSize()
               && ItemStack.isSameItemSameComponents(stack, InfusedPropertiesHelper.addProperty(droppedItem.copy(), AlchemancyProperties.COMPACT))) {
               int remaining = droppedItem.getCount() - (stack.getMaxStackSize() - stack.getCount());
               if (remaining > 0) {
                  ItemStack remainingStack = droppedItem.copy();
                  remainingStack.setCount(remaining);
                  ItemEntity remainingEntity = new ItemEntity(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), remainingStack);
                  ((ItemEntityAccessor)remainingEntity).setAge(remainingEntity.getAge());
                  itemEntity.level().addFreshEntity(remainingEntity);
                  droppedItem.shrink(remaining);
               }

               InfusedPropertiesHelper.addProperty(droppedItem, AlchemancyProperties.COMPACT);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onItemToss(ItemTossEvent event) {
      InfusedPropertiesHelper.forEachProperty(
         event.getEntity().getItem(),
         propertyHolder -> ((Property)propertyHolder.value()).onItemTossed(event.getPlayer(), event.getEntity().getItem(), event.getEntity(), event)
      );
   }

   @SubscribeEvent
   public static void onAttributeModification(ItemAttributeModifierEvent event) {
      InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).applyAttributes(event));
   }

   @SubscribeEvent
   public static void onLivingDrops(LivingDropsEvent event) {
      if (event.getSource().getDirectEntity() instanceof LivingEntity user) {
         ItemStack weapon = user.getMainHandItem();
         InfusedPropertiesHelper.forEachProperty(
            weapon, propertyHolder -> ((Property)propertyHolder.value()).modifyLivingDrops(event.getEntity(), weapon, user, event.getDrops(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onLivingXpDrops(LivingExperienceDropEvent event) {
      Player user = event.getAttackingPlayer();
      if (user != null) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = user.getItemBySlot(slot);
            InfusedPropertiesHelper.forEachProperty(
               item, propertyHolder -> ((Property)propertyHolder.value()).modifyLivingExperienceDrops(user, item, slot, event.getEntity(), event)
            );
         }

         AuxiliaryProperty.triggerAuxiliaryEffects(
            user,
            (propertyHolder, stack) -> ((Property)propertyHolder.value())
               .modifyLivingExperienceDrops(user, stack, EquipmentSlot.MAINHAND, event.getEntity(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onBlockDrops(BlockDropsEvent event) {
      ItemStack tool = event.getBreaker() instanceof LivingEntity living
            && !living.getMainHandItem().isEmpty()
            && ItemStack.isSameItem(event.getTool(), living.getMainHandItem())
         ? living.getMainHandItem()
         : event.getTool();
      InfusedPropertiesHelper.forEachProperty(
         tool, propertyHolder -> ((Property)propertyHolder.value()).modifyBlockDrops(event.getBreaker(), tool, EquipmentSlot.MAINHAND, event.getDrops(), event)
      );
      if (event.getBreaker() instanceof Player player) {
         AuxiliaryProperty.triggerAuxiliaryEffects(
            player,
            (propertyHolder, stack) -> ((Property)propertyHolder.value()).modifyBlockDrops(player, stack, EquipmentSlot.MAINHAND, event.getDrops(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onItemUseTick(Tick event) {
      LivingEntity user = event.getEntity();
      if (!user.isSpectator() && (!(user instanceof LivingEntity) || !user.isDeadOrDying())) {
         InfusedPropertiesHelper.forEachProperty(
            event.getItem(), propertyHolder -> ((Property)propertyHolder.value()).onItemUseTick(user, event.getItem(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).onMobEffectAdded(stack, slot, event.getEntity(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onEffectApplicable(Applicable event) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(
            stack, propertyHolder -> ((Property)propertyHolder.value()).isMobEffectApplicable(stack, slot, event.getEntity(), event)
         );
      }
   }

   @SubscribeEvent
   public static void onRightClickItem(RightClickItem event) {
      if (CommonUtils.calculateHitResult(event.getEntity()).getType() != Type.BLOCK) {
         InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickItem(event));
         InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickItemPost(event));
      }
   }

   @SubscribeEvent
   public static void onRightClickItemOnBlock(UseItemOnBlockEvent event) {
      if (event.getUsePhase() == UsePhase.ITEM_AFTER_BLOCK && event.getPlayer() != null) {
         InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickBlock(event));
         if (!event.isCanceled()) {
            RightClickItem clickEvent = new RightClickItem(event.getPlayer(), event.getHand());
            InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickItem(clickEvent));
            InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickItemPost(clickEvent));
            if (clickEvent.isCanceled()) {
               event.cancelWithResult(resultToItemResult(clickEvent.getCancellationResult()));
            }
         }
      }
   }

   private static ItemInteractionResult resultToItemResult(InteractionResult result) {
      return switch (result) {
         case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
         case CONSUME -> ItemInteractionResult.CONSUME;
         case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
         case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         case FAIL -> ItemInteractionResult.FAIL;
         default -> throw new MatchException(null, null);
      };
   }

   @SubscribeEvent
   public static void onRightClickSpecific(EntityInteractSpecific event) {
      InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> ((Property)propertyHolder.value()).onRightClickEntity(event));
   }

   @SubscribeEvent
   public static void onRightClickEntity(EntityInteractSpecific event) {
      if (event.getTarget() instanceof LivingEntity target) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor()
               && InfusedPropertiesHelper.hasProperty(target.getItemBySlot(slot), AlchemancyProperties.SADDLED)
               && event.getEntity().startRiding(target)) {
               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onStopUsingItem(Stop event) {
      ItemStack stack = event.getItem();
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onStopUsingItem(stack, event.getEntity(), event));
   }

   @SubscribeEvent
   public static void onItemStacking(ItemStackedOnOtherEvent event) {
      if (!event.isCanceled()) {
         ItemStack carriedItem = event.getCarriedItem();
         ItemStack stackedOnItem = event.getStackedOnItem();
         AtomicBoolean canceled = new AtomicBoolean(false);
         InfusedPropertiesHelper.forEachProperty(
            carriedItem,
            propertyHolder -> ((Property)propertyHolder.value())
               .onStackedOverItem(
                  carriedItem, stackedOnItem, event.getPlayer(), event.getClickAction(), event.getCarriedSlotAccess(), event.getSlot(), canceled
               )
         );
         if (!canceled.get()) {
            InfusedPropertiesHelper.forEachProperty(
               stackedOnItem,
               propertyHolder -> ((Property)propertyHolder.value())
                  .onStackedOverMe(
                     carriedItem, stackedOnItem, event.getPlayer(), event.getClickAction(), event.getCarriedSlotAccess(), event.getSlot(), canceled
                  )
            );
         }

         event.setCanceled(canceled.get());
      }
   }

   @SubscribeEvent
   public static void onLivingJump(LivingJumpEvent event) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onJump(event.getEntity(), stack, slot, event));
      }
   }

   @SubscribeEvent
   public static void onLivingFall(LivingFallEvent event) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onFall(event.getEntity(), stack, slot, event));
      }
   }

   @SubscribeEvent
   public static void onFlyingPlayerFall(PlayerFlyableFallEvent event) {
      LivingFallEvent livingFall = new LivingFallEvent(event.getEntity(), event.getDistance(), event.getMultiplier());

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = event.getEntity().getItemBySlot(slot);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onFall(event.getEntity(), stack, slot, livingFall));
      }

      event.setDistance(livingFall.getDistance());
      event.setMultiplier(livingFall.getDamageMultiplier());
   }

   @SubscribeEvent
   public static void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
      ItemStack stack = event.getStack();
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).modifyEnchantmentLevels(event));
   }

   @SubscribeEvent
   public static void onFurnaceBurnTime(FurnaceFuelBurnTimeEvent event) {
      ItemStack stack = event.getItemStack();
      int burnTime = event.getBurnTime();
      float burnMultiplier = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.FLAMMABLE) ? 1.5F : 1.0F;
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CHARRED)) {
         burnMultiplier *= 3.0F;
      }

      if (burnMultiplier != 1.0F) {
         if (stack.isDamageableItem()) {
            float durability = (event.getItemStack().getMaxDamage() - event.getItemStack().getDamageValue()) / 50.0F;
            if (durability > 1.0F) {
               durability = 1.0F + (durability - 1.0F) * 0.5F;
            }

            burnMultiplier *= durability;
         }

         event.setBurnTime((int)(Math.max(burnTime, 300) * burnMultiplier));
      }
   }

   @SubscribeEvent
   public static void onEnderManAnger(EnderManAngerEvent event) {
      if (InfusedPropertiesHelper.hasProperty(event.getPlayer().getItemBySlot(EquipmentSlot.HEAD), AlchemancyTags.Properties.PREVENTS_ENDERMAN_AGGRO)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void modifyFov(ComputeFovModifierEvent event) {
      Player player = event.getPlayer();
      if (isScoping(player)) {
         event.setNewFovModifier(0.0F);
      }
   }

   public static boolean isScoping(Player player) {
      return !player.isShiftKeyDown()
         ? false
         : InfusedPropertiesHelper.hasItemWithProperty(player, AlchemancyProperties.SCOPING, true, EquipmentSlotGroup.HAND)
            || InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.SCOPING);
   }

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof PathfinderMob mob) {
         if (!AlchemancyProperties.SCARY.is(AlchemancyTags.Properties.DISABLED) && mob.getType().is(AlchemancyTags.EntityTypes.SCARED_BY_SCARY)) {
            mob.goalSelector.addGoal(0, new ScareGoal(mob, 2.0, AlchemancyProperties.SCARY));
         }

         if (!AlchemancyProperties.SEEDED.is(AlchemancyTags.Properties.DISABLED)
            && mob.getType().is(AlchemancyTags.EntityTypes.AGGROED_BY_SEEDED)
            && mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            mob.goalSelector.addGoal(3, new MeleeAttackGoal(mob, 1.6, true));
            mob.targetSelector.addGoal(0, targetLivingHoldingProperty(mob, AlchemancyProperties.SEEDED, EquipmentSlotGroup.ARMOR));
         }
      }
   }

   public static NearestAttackableTargetGoal<LivingEntity> targetLivingHoldingProperty(Mob mob, Holder<Property> property, EquipmentSlotGroup slotsToCheck) {
      return new NearestAttackableTargetGoal(mob, LivingEntity.class, 0, false, false, living -> {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slotsToCheck.test(slot) && InfusedPropertiesHelper.hasProperty(living.getItemBySlot(slot), property)) {
               return true;
            }
         }

         return false;
      });
   }

   @SubscribeEvent
   public static void onGetProjectile(LivingGetProjectileEvent event) {
      ItemStack shootable = event.getProjectileWeaponItemStack();
      ItemStack projectile = event.getProjectileItemStack();
      if (projectile.isEmpty() && event.getEntity() instanceof Player user) {
         Predicate<ItemStack> predicate = ((ProjectileWeaponItem)shootable.getItem()).getAllSupportedProjectiles(shootable);

         for (int i = 0; i < user.getInventory().getContainerSize(); i++) {
            ItemStack stack = user.getInventory().getItem(i);
            ItemStack storedStack = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
            if (!storedStack.isEmpty() && predicate.test(storedStack)) {
               event.setProjectileItemStack(CommonHooks.getProjectile(user, shootable, storedStack));
               storedStack.shrink(1);
               ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, stack.isEmpty() ? ItemStack.EMPTY : storedStack);
               return;
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFarmlandTrample(FarmlandTrampleEvent event) {
      if (event.getEntity() instanceof LivingEntity living
         && InfusedPropertiesHelper.hasProperty(living.getItemBySlot(EquipmentSlot.FEET), AlchemancyProperties.LIGHTWEIGHT)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onVanillaGameEvent(VanillaGameEvent event) {
      if (event.getCause() instanceof LivingEntity living) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (InfusedPropertiesHelper.hasProperty(living.getItemBySlot(slot), AlchemancyProperties.MUFFLED)
               && (!MUFFLED_EVENTS.containsKey(slot) || MUFFLED_EVENTS.get(slot).contains(event.getVanillaEvent()))) {
               event.setCanceled(true);
            }
         }
      } else if (event.getCause() instanceof Projectile projectile
         && InfusedPropertiesHelper.hasProperty(getProjectileItemStack(projectile), AlchemancyProperties.MUFFLED)
         && event.getVanillaEvent().equals(GameEvent.PROJECTILE_LAND)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onBlockLeftClicked(LeftClickBlock event) {
      ItemStack stack = event.getItemStack();
      if (event.getEntity().isCreative() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_BLOCK_ATTACK_IN_CREATIVE)
         || InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.FLIMSY)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      boolean hasInfusions = !InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty();
      if (stack.has(AlchemancyItems.Components.INGREDIENT_DISPLAY)) {
         event.getToolTip().clear();
         if (stack.is(AlchemancyItems.PROPERTY_CAPSULE)) {
            event.getToolTip().add(PROPERTY_INGREDIENT_NAME);
         } else {
            event.getToolTip().add(stack.getDisplayName());
         }
      }

      if (hasInfusions) {
         ((InfusedPropertiesComponent)stack.get(AlchemancyItems.Components.INFUSED_PROPERTIES))
            .forEachProperty(
               holder -> event.getToolTip()
                  .add(
                     holder.is(AlchemancyTags.Properties.DISABLED)
                        ? Component.translatable("property.disabled", new Object[]{Component.translatable(((Property)holder.value()).getLanguageKey())})
                           .withStyle(ChatFormatting.DARK_GRAY)
                        : ((Property)holder.value()).getDisplayText(stack)
                  ),
               false
            );
      }

      if (stack.has(AlchemancyItems.Components.STORED_PROPERTIES)) {
         InfusedPropertiesComponent storedProperties = (InfusedPropertiesComponent)stack.get(AlchemancyItems.Components.STORED_PROPERTIES);
         if (hasInfusions && !storedProperties.properties().isEmpty()) {
            event.getToolTip().add(Component.translatable("item.alchemancy.tooltip.stored_properties").withStyle(ChatFormatting.GRAY));
         }

         storedProperties.forEachProperty(
            holder -> event.getToolTip()
               .add(
                  holder.is(AlchemancyTags.Properties.DISABLED)
                     ? Component.translatable("property.disabled", new Object[]{Component.translatable(((Property)holder.value()).getLanguageKey())})
                        .withStyle(ChatFormatting.DARK_GRAY)
                     : ((Property)holder.value()).getDisplayText(stack)
               ),
            false
         );
      }

      if (event.getEntity() != null
         && (
            InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.REVEALED)
               || InfusedPropertiesHelper.hasProperty(event.getEntity().getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING)
         )) {
         List<Holder<Property>> dormantProperties = AlchemancyProperties.getDormantProperties(stack);
         if (!dormantProperties.isEmpty()) {
            event.getToolTip().add(Component.translatable("item.alchemancy.tooltip.dormant_properties").withStyle(ChatFormatting.GRAY));

            for (Holder<Property> dormantProperty : dormantProperties) {
               event.getToolTip().add(((Property)dormantProperty.value()).getName(stack));
            }
         }
      }

      if (event.getEntity() != null
         && (
            InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.SCRAMBLED)
               || InfusedPropertiesHelper.hasProperty(event.getEntity().getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.SCRAMBLED)
         )) {
         for (int i = 1; i < event.getToolTip().size(); i++) {
            event.getToolTip().set(i, ((Component)event.getToolTip().get(i)).copy().withStyle(ChatFormatting.OBFUSCATED));
         }
      }
   }
}
