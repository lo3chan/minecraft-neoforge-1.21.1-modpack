package com.aetherteam.aether.event.listeners;

import com.aetherteam.aether.event.hooks.EntityHooks;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.EquipAction;
import io.wispforest.accessories.api.events.OnDeathCallback;
import io.wispforest.accessories.api.slot.SlotReference;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobSplitEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

public class EntityListener {
   public static void listen(IEventBus bus) {
      bus.addListener(EntityListener::onEntityJoin);
      bus.addListener(EntityListener::onMountEntity);
      bus.addListener(EntityListener::onRiderTick);
      bus.addListener(EntityListener::onInteractWithEntity);
      bus.addListener(EntityListener::onProjectileHitEntity);
      bus.addListener(EntityListener::onShieldBlock);
      bus.addListener(EntityListener::onLightningStrike);
      bus.addListener(EntityListener::onPlayerDrops);
      bus.addListener(EntityListener::onDropExperience);
      bus.addListener(EntityListener::onEffectApply);
      bus.addListener(EntityListener::onEntitySplit);
      bus.addListener(EntityListener::onLoadPlayerFile);
      OnDeathCallback.EVENT.register((OnDeathCallback)(currentState, entity, capability, damageSource, droppedStacks) -> {
         List<ItemStack> droppedStacksCopy = new ArrayList<>(droppedStacks);
         boolean recentlyHit = entity.hurtMarked;
         int looting = EnchantmentHelper.getEnchantmentLevel(entity.level().registryAccess().holderOrThrow(Enchantments.LOOTING), entity);
         droppedStacks.clear();
         droppedStacks.addAll(EntityHooks.handleEntityAccessoryDrops(entity, droppedStacksCopy, recentlyHit, looting));
         return TriState.DEFAULT;
      });
   }

   public static void onEntityJoin(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      EntityHooks.addGoals(entity);
   }

   public static void onMountEntity(EntityMountEvent event) {
      Entity riderEntity = event.getEntityMounting();
      Entity mountEntity = event.getEntityBeingMounted();
      boolean isDismounting = event.isDismounting();
      if (EntityHooks.dismountPrevention(riderEntity, mountEntity, isDismounting)) {
         event.setCanceled(true);
      } else {
         EntityHooks.trackMount(mountEntity, isDismounting);
      }
   }

   public static void onRiderTick(Post event) {
      Player player = event.getEntity();
      EntityHooks.launchMount(player);
   }

   public static void onInteractWithEntity(EntityInteractSpecific event) {
      Entity targetEntity = event.getTarget();
      Player player = event.getEntity();
      ItemStack itemStack = event.getItemStack();
      Vec3 position = event.getLocalPos();
      InteractionHand interactionHand = event.getHand();
      EntityHooks.skyrootBucketMilking(targetEntity, player, interactionHand);
      Optional<InteractionResult> result = EntityHooks.pickupBucketable(targetEntity, player, interactionHand);
      if (result.isEmpty()) {
         result = EntityHooks.interactWithArmorStand(targetEntity, player, itemStack, position, interactionHand);
      }

      result.ifPresent(event::setCancellationResult);
      event.setCanceled(result.isPresent());
   }

   public static void onProjectileHitEntity(ProjectileImpactEvent event) {
      Entity projectileEntity = event.getEntity();
      HitResult rayTraceResult = event.getRayTraceResult();
      if (EntityHooks.preventEntityHooked(projectileEntity, rayTraceResult)) {
         event.setCanceled(true);
      }
   }

   public static void onShieldBlock(LivingShieldBlockEvent event) {
      if (!event.isCanceled()) {
         event.setCanceled(EntityHooks.preventSliderShieldBlock(event.getDamageSource()));
      }
   }

   public static void onLightningStrike(EntityStruckByLightningEvent event) {
      Entity entity = event.getEntity();
      LightningBolt lightningBolt = event.getLightning();
      if (EntityHooks.lightningHitKeys(entity) || EntityHooks.thunderCrystalHitItems(entity, lightningBolt)) {
         event.setCanceled(true);
      }
   }

   public static void onPlayerDrops(LivingDropsEvent event) {
      LivingEntity entity = event.getEntity();
      Collection<ItemEntity> itemDrops = event.getDrops();
      EntityHooks.trackDrops(entity, itemDrops);
   }

   public static void onDropExperience(LivingExperienceDropEvent event) {
      LivingEntity livingEntity = event.getEntity();
      int experience = event.getDroppedExperience();
      int newExperience = EntityHooks.modifyExperience(livingEntity, experience);
      event.setDroppedExperience(newExperience);
   }

   public static void onEffectApply(Applicable event) {
      LivingEntity livingEntity = event.getEntity();
      MobEffectInstance effectInstance = event.getEffectInstance();
      if (EntityHooks.preventInebriation(livingEntity, effectInstance)) {
         event.setResult(Result.DO_NOT_APPLY);
      }
   }

   public static void onEntitySplit(MobSplitEvent event) {
      Mob mob = event.getParent();
      if (EntityHooks.preventSplit(mob)) {
         event.setCanceled(true);
      }
   }

   public static void onLoadPlayerFile(LoadFromFile event) {
      Player player = event.getEntity();
      if (player instanceof ServerPlayer serverPlayer) {
         CompoundTag playerTag = serverPlayer.server.getWorldData().getLoadedPlayerTag();
         if (playerTag == null) {
            return;
         }

         Optional<CompoundTag> capsTag = tryGetCapsTag(playerTag);
         if (capsTag.isEmpty()) {
            return;
         }

         CompoundTag curiosInventoryTag = capsTag.get().getCompound("curios:inventory");
         if (curiosInventoryTag.getBoolean("AccessoriesEncoded") || !curiosInventoryTag.contains("Curios")) {
            return;
         }

         if (curiosInventoryTag.get("Curios") instanceof ListTag curiosListTag) {
            for (Tag tag : curiosListTag) {
               if (tag instanceof CompoundTag compoundTag && compoundTag.contains("StacksHandler") && compoundTag.contains("Identifier")) {
                  CompoundTag stacksHandlerTag = compoundTag.getCompound("StacksHandler");
                  if (stacksHandlerTag.contains("Stacks")) {
                     CompoundTag stacksTag = stacksHandlerTag.getCompound("Stacks");
                     if (stacksTag.contains("Items")) {
                        Tag itemsTag = stacksTag.get("Items");
                        if (itemsTag instanceof ListTag) {
                           for (Tag itemTag : (ListTag)itemsTag) {
                              if (itemTag instanceof CompoundTag itemCompoundTag && itemCompoundTag.contains("id")) {
                                 ResourceLocation location = ResourceLocation.parse(itemCompoundTag.getString("id"));
                                 if (location.getNamespace().equals("aether")) {
                                    Item item = (Item)BuiltInRegistries.ITEM.get(location);
                                    if (item != Items.AIR) {
                                       ItemStack stack = new ItemStack(item);
                                       AccessoriesCapability accessories = AccessoriesCapability.get(player);
                                       if (accessories != null) {
                                          Accessory accessory = AccessoriesAPI.getOrDefaultAccessory(stack);
                                          Pair<SlotReference, EquipAction> equipReference = accessories.canEquipAccessory(stack, true);
                                          if (equipReference != null && accessory.canEquip(stack, (SlotReference)equipReference.first())) {
                                             ((EquipAction)equipReference.second()).equipStack(stack.copy());
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static Optional<CompoundTag> tryGetCapsTag(CompoundTag playerTag) {
      if (playerTag == null) {
         return Optional.empty();
      } else {
         CompoundTag capsTag = null;
         if (playerTag.contains("ForgeCaps")) {
            capsTag = playerTag.getCompound("ForgeCaps");
         } else {
            if (!playerTag.contains("neoforge:attachments")) {
               return Optional.empty();
            }

            capsTag = playerTag.getCompound("neoforge:attachments");
         }

         return capsTag.contains("curios:inventory") ? Optional.of(capsTag) : Optional.empty();
      }
   }
}
