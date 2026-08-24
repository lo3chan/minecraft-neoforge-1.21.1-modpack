package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.DroppedItemAttachment;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.attachment.MobAccessoryAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.entity.passive.FlyingCow;
import com.aetherteam.aether.entity.passive.MountableAnimal;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.item.accessories.SlotIdentifierHolder;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReferenceImpl;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityHooks {
   public static void addGoals(Entity entity) {
      if (entity.getClass() == Bee.class) {
         Bee bee = (Bee)entity;
         bee.getGoalSelector().addGoal(7, new BeeGrowBerryBushGoal(bee));
      } else if (entity.getClass() == Fox.class) {
         Fox fox = (Fox)entity;
         fox.goalSelector.addGoal(10, new FoxEatBerryBushGoal(fox, 1.2000000476837158, 12, 1));
      }
   }

   public static boolean canMobSpawnWithAccessories(Entity entity) {
      EntityType<?> entityType = entity.getType();
      return entity instanceof Mob
         && (
            entityType == EntityType.ZOMBIE
               || entityType == EntityType.ZOMBIE_VILLAGER
               || entityType == EntityType.HUSK
               || entityType == EntityType.SKELETON
               || entityType == EntityType.STRAY
               || entityType == EntityType.PIGLIN
         );
   }

   public static void spawnWithAccessories(Entity entity, DifficultyInstance difficulty) {
      if (entity instanceof Mob mob && mob.level() instanceof ServerLevel) {
         RandomSource random = mob.getRandom();
         EntityType<?> entityType = mob.getType();
         SlotTypeReference[] allSlots = new SlotTypeReference[]{GlovesItem.getStaticIdentifier(), PendantItem.getStaticIdentifier()};
         SlotTypeReference[] gloveSlots = new SlotTypeReference[]{GlovesItem.getStaticIdentifier()};
         if (entityType == EntityType.PIGLIN) {
            if (mob instanceof AbstractPiglin abstractPiglin && abstractPiglin.isAdult()) {
               for (SlotTypeReference identifier : allSlots) {
                  if (random.nextFloat() < 0.1F) {
                     equipAccessory(mob, identifier, ArmorMaterials.GOLD);
                  }
               }
            }
         } else {
            boolean fullyArmored = true;

            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
               if (equipmentslot.getType() == Type.HUMANOID_ARMOR) {
                  ItemStack itemStack = mob.getItemBySlot(equipmentslot);
                  if (itemStack.isEmpty()) {
                     fullyArmored = false;
                     break;
                  }
               }
            }

            if (fullyArmored && random.nextInt(4) == 1 && mob.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem) {
               for (SlotTypeReference identifierx : gloveSlots) {
                  equipAccessory(mob, identifierx, armorItem.getMaterial());
               }
            }
         }

         enchantAccessories(mob, difficulty, allSlots);
      }
   }

   private static void equipAccessory(Mob mob, SlotTypeReference identifier, Holder<ArmorMaterial> armorMaterials) {
      AccessoriesCapability accessories = AccessoriesCapability.get(mob);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
         if (accessoriesContainer != null) {
            boolean empty = true;

            for (SlotEntryReference slotResult : accessoriesContainer.capability().getAllEquipped()) {
               if (!slotResult.stack().isEmpty()) {
                  empty = false;
                  break;
               }
            }

            if (empty) {
               Item item = getEquipmentForSlot(identifier, armorMaterials);
               if (item != null) {
                  accessoriesContainer.getAccessories().setItem(0, new ItemStack(item));
               }
            }
         }
      }
   }

   @Nullable
   private static Item getEquipmentForSlot(SlotTypeReference identifier, Holder<ArmorMaterial> armorMaterial) {
      if (identifier.equals(GlovesItem.getStaticIdentifier())) {
         if (armorMaterial.is(ArmorMaterials.LEATHER)) {
            return (Item)AetherItems.LEATHER_GLOVES.get();
         }

         if (armorMaterial.is(ArmorMaterials.GOLD)) {
            return (Item)AetherItems.GOLDEN_GLOVES.get();
         }

         if (armorMaterial.is(ArmorMaterials.CHAIN)) {
            return (Item)AetherItems.CHAINMAIL_GLOVES.get();
         }

         if (armorMaterial.is(ArmorMaterials.IRON)) {
            return (Item)AetherItems.IRON_GLOVES.get();
         }

         if (armorMaterial.is(ArmorMaterials.DIAMOND)) {
            return (Item)AetherItems.DIAMOND_GLOVES.get();
         }
      } else if (identifier.equals(PendantItem.getStaticIdentifier())) {
         if (armorMaterial.is(ArmorMaterials.IRON)) {
            return (Item)AetherItems.IRON_PENDANT.get();
         }

         if (armorMaterial.is(ArmorMaterials.GOLD)) {
            return (Item)AetherItems.GOLDEN_PENDANT.get();
         }
      }

      return null;
   }

   private static void enchantAccessories(Mob mob, DifficultyInstance difficulty, SlotTypeReference[] allowedSlots) {
      RandomSource random = mob.getRandom();
      float chanceMultiplier = difficulty.getSpecialMultiplier();
      AccessoriesCapability accessories = AccessoriesCapability.get(mob);
      if (accessories != null) {
         for (SlotTypeReference identifier : allowedSlots) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer != null) {
               ItemStack itemStack = accessoriesContainer.getAccessories().getItem(0);
               if (!itemStack.isEmpty() && random.nextFloat() < 0.5F * chanceMultiplier) {
                  accessoriesContainer.getAccessories()
                     .setItem(
                        0,
                        EnchantmentHelper.enchantItem(
                           random,
                           itemStack,
                           (int)(5.0F + chanceMultiplier * random.nextInt(18)),
                           mob.registryAccess(),
                           Optional.of(mob.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT))
                        )
                     );
               }
            }
         }
      }
   }

   public static boolean dismountPrevention(Entity rider, Entity mount, boolean dismounting) {
      return dismounting && rider.isShiftKeyDown()
         ? mount instanceof MountableAnimal && !mount.onGround() && !mount.isInFluidType() && !mount.isPassenger()
            || mount instanceof Swet swet && !swet.isFriendly()
         : false;
   }

   public static void trackMount(Entity mount, boolean dismounting) {
      if (mount instanceof MountableAnimal mountableAnimal) {
         mountableAnimal.setHasPassenger(!dismounting);
      }
   }

   public static void launchMount(Player player) {
      Entity mount = player.getVehicle();
      if (player.isPassenger()
         && mount != null
         && mount.level().getBlockStates(mount.getBoundingBox()).anyMatch(state -> state.is((Block)AetherBlocks.BLUE_AERCLOUD.get()))
         && player.level().isClientSide()) {
         mount.setDeltaMovement(mount.getDeltaMovement().x(), 2.0, mount.getDeltaMovement().z());
      }
   }

   public static void skyrootBucketMilking(Entity target, Player player, InteractionHand hand) {
      if ((target instanceof Cow || target instanceof FlyingCow) && !((Animal)target).isBaby()) {
         ItemStack heldStack = player.getItemInHand(hand);
         if (heldStack.is((Item)AetherItems.SKYROOT_BUCKET.get())) {
            if (target instanceof FlyingCow) {
               player.playSound((SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_MILK.get(), 1.0F, 1.0F);
            } else {
               player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            }

            ItemStack filledBucket = ItemUtils.createFilledResult(heldStack, player, ((Item)AetherItems.SKYROOT_MILK_BUCKET.get()).getDefaultInstance());
            player.swing(hand);
            player.setItemInHand(hand, filledBucket);
         }
      }
   }

   public static Optional<InteractionResult> pickupBucketable(Entity target, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      Optional<InteractionResult> interactionResult = Optional.empty();
      if (heldStack.is((Item)AetherItems.SKYROOT_WATER_BUCKET.get())
         && target instanceof Bucketable bucketable
         && target instanceof LivingEntity livingEntity
         && livingEntity.isAlive()) {
         ItemStack bucketStack = bucketable.getBucketItemStack();
         bucketStack = SkyrootBucketItem.swapBucketType(bucketStack);
         if (!bucketStack.isEmpty()) {
            target.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
            bucketable.saveToBucketTag(bucketStack);
            ItemStack filledStack = ItemUtils.createFilledResult(heldStack, player, bucketStack, false);
            player.setItemInHand(hand, filledStack);
            Level level = livingEntity.level();
            if (!level.isClientSide()) {
               CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, bucketStack);
            }

            target.discard();
            interactionResult = Optional.of(InteractionResult.sidedSuccess(level.isClientSide()));
         } else {
            interactionResult = Optional.of(InteractionResult.FAIL);
         }
      }

      return interactionResult;
   }

   public static Optional<InteractionResult> interactWithArmorStand(Entity target, Player player, ItemStack stack, Vec3 pos, InteractionHand hand) {
      if (target instanceof ArmorStand armorStand) {
         if (armorStand.level().isClientSide()) {
            return Optional.of(InteractionResult.SUCCESS);
         }

         if (!stack.isEmpty()) {
            if (stack.is(AetherTags.Items.ACCESSORIES)) {
               SlotTypeReference identifier = null;
               if (stack.getItem() instanceof SlotIdentifierHolder slotIdentifierHolder) {
                  identifier = slotIdentifierHolder.getIdentifier();
               }

               if (identifier != null) {
                  AccessoriesCapability accessories = AccessoriesCapability.get(armorStand);
                  if (accessories != null) {
                     AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
                     if (accessoriesContainer != null) {
                        ItemStack itemStack = accessoriesContainer.getAccessories().getItem(0);
                        if (stack.getItem() instanceof AccessoryItem accessoryItem) {
                           SlotReferenceImpl slotContext = new SlotReferenceImpl(armorStand, identifier.slotName(), 0);
                           accessoriesContainer.getAccessories().setItem(0, stack.copy());
                           if (accessoryItem instanceof GlovesItem glovesItem) {
                              armorStand.level()
                                 .playSound(
                                    null,
                                    armorStand.blockPosition(),
                                    (SoundEvent)glovesItem.getEquipSound(stack, slotContext).event().value(),
                                    armorStand.getSoundSource(),
                                    1.0F,
                                    1.0F
                                 );
                           } else if (accessoryItem instanceof PendantItem pendantItem) {
                              armorStand.level()
                                 .playSound(
                                    null,
                                    armorStand.blockPosition(),
                                    (SoundEvent)pendantItem.getEquipSound(stack, slotContext).event().value(),
                                    armorStand.getSoundSource(),
                                    1.0F,
                                    1.0F
                                 );
                           } else {
                              armorStand.level()
                                 .playSound(
                                    null,
                                    armorStand.blockPosition(),
                                    (SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                                    armorStand.getSoundSource(),
                                    1.0F,
                                    1.0F
                                 );
                           }

                           if (identifier.slotName().equals(GlovesItem.getStaticIdentifier().slotName())) {
                              armorStand.setShowArms(true);
                           }

                           if (!player.isCreative()) {
                              int count = stack.getCount();
                              stack.shrink(count);
                           }

                           if (!itemStack.isEmpty()) {
                              player.setItemInHand(hand, itemStack);
                           }

                           return Optional.of(InteractionResult.SUCCESS);
                        }
                     }
                  }
               }
            }
         } else {
            SlotTypeReference identifierx = slotToUnequip(armorStand, pos);
            if (identifierx != null) {
               AccessoriesCapability accessories = AccessoriesCapability.get(armorStand);
               if (accessories != null) {
                  AccessoriesContainer accessoriesContainer = accessories.getContainer(identifierx);
                  if (accessoriesContainer != null) {
                     ItemStack itemStack = accessoriesContainer.getAccessories().getItem(0);
                     if (!itemStack.isEmpty()) {
                        player.setItemInHand(hand, itemStack);
                        accessoriesContainer.getAccessories().setItem(0, ItemStack.EMPTY);
                        return Optional.of(InteractionResult.SUCCESS);
                     }
                  }
               }
            }
         }
      }

      return Optional.empty();
   }

   private static SlotTypeReference slotToUnequip(ArmorStand armorStand, Vec3 pos) {
      boolean isSmall = armorStand.isSmall();
      Axis axis = armorStand.getDirection().getAxis();
      double x = isSmall ? pos.x * 2.0 : pos.x;
      double z = isSmall ? pos.z * 2.0 : pos.z;
      double front = axis == Axis.X ? z : x;
      double vertical = isSmall ? pos.y * 2.0 : pos.y;
      SlotTypeReference glovesIdentifier = GlovesItem.getStaticIdentifier();
      SlotTypeReference pendantIdentifier = PendantItem.getStaticIdentifier();
      SlotTypeReference capeIdentifier = CapeItem.getStaticIdentifier();
      SlotTypeReference shieldIdentifier = ShieldOfRepulsionItem.getStaticIdentifier();
      if (!getItemByIdentifier(armorStand, glovesIdentifier).isEmpty()
         && Math.abs(front) >= (isSmall ? 0.15 : 0.2)
         && vertical >= (isSmall ? 0.65 : 0.75)
         && vertical < 1.15) {
         return glovesIdentifier;
      } else if (!getItemByIdentifier(armorStand, pendantIdentifier).isEmpty() && vertical >= (isSmall ? 1.2 : 1.3) && vertical < 0.9 + (isSmall ? 0.8 : 0.6)) {
         return pendantIdentifier;
      } else if (!getItemByIdentifier(armorStand, capeIdentifier).isEmpty() && vertical >= (isSmall ? 1.0 : 1.1) && vertical < (isSmall ? 1.7 : 1.4)) {
         return capeIdentifier;
      } else {
         return !getItemByIdentifier(armorStand, shieldIdentifier).isEmpty() && vertical >= (isSmall ? 0.9 : 1.0) && vertical < (isSmall ? 1.5 : 1.2)
            ? shieldIdentifier
            : null;
      }
   }

   private static ItemStack getItemByIdentifier(ArmorStand armorStand, SlotTypeReference identifier) {
      AccessoriesCapability accessories = AccessoriesCapability.get(armorStand);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
         if (accessoriesContainer != null) {
            return accessoriesContainer.getAccessories().getItem(0);
         }
      }

      return ItemStack.EMPTY;
   }

   public static boolean preventEntityHooked(Entity projectileEntity, HitResult rayTraceResult) {
      return !(rayTraceResult instanceof EntityHitResult entityHitResult)
         ? false
         : entityHitResult.getEntity().getType().is(AetherTags.Entities.UNHOOKABLE) && projectileEntity instanceof FishingHook;
   }

   public static boolean preventSliderShieldBlock(DamageSource source) {
      return source.getEntity() instanceof Slider;
   }

   public static boolean lightningHitKeys(Entity entity) {
      return entity instanceof ItemEntity itemEntity ? itemEntity.getItem().is(AetherTags.Items.DUNGEON_KEYS) : false;
   }

   public static boolean thunderCrystalHitItems(Entity entity, LightningBolt lightning) {
      return entity instanceof ItemEntity && lightning.hasData(AetherDataAttachments.LIGHTNING_TRACKER)
         ? ((LightningTrackerAttachment)lightning.getData(AetherDataAttachments.LIGHTNING_TRACKER)).getOwner(lightning.level()) instanceof ValkyrieQueen
         : false;
   }

   public static void trackDrops(LivingEntity entity, Collection<ItemEntity> itemDrops) {
      if (entity instanceof Player player) {
         itemDrops.forEach(itemEntity -> ((DroppedItemAttachment)itemEntity.getData(AetherDataAttachments.DROPPED_ITEM)).setOwner(player));
      }
   }

   public static List<ItemStack> handleEntityAccessoryDrops(LivingEntity entity, List<ItemStack> itemStacks, boolean recentlyHit, int looting) {
      if (entity instanceof Mob mob) {
         SlotTypeReference[] allSlots = new SlotTypeReference[]{GlovesItem.getStaticIdentifier(), PendantItem.getStaticIdentifier()};

         for (SlotTypeReference identifier : allSlots) {
            if (!itemStacks.isEmpty()) {
               ItemStack itemStack = (ItemStack)itemStacks.getFirst();
               float f = ((MobAccessoryAttachment)mob.getData(AetherDataAttachments.MOB_ACCESSORY)).getEquipmentDropChance(identifier);
               boolean flag = f > 1.0F;
               if (!itemStack.isEmpty()) {
                  itemStacks.removeIf(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
               }

               if (!itemStack.isEmpty()
                  && itemStack.getEnchantmentLevel(entity.level().holderOrThrow(Enchantments.VANISHING_CURSE)) == 0
                  && recentlyHit
                  && Math.max(mob.getRandom().nextFloat() - looting * 0.01F, 0.0F) < f) {
                  if (!flag && itemStack.isDamageableItem()) {
                     itemStack.setDamageValue(
                        itemStack.getMaxDamage() - mob.getRandom().nextInt(1 + mob.getRandom().nextInt(Math.max(itemStack.getMaxDamage() - 3, 1)))
                     );
                  }

                  itemStacks.add(itemStack);
               }
            }
         }
      }

      return itemStacks;
   }

   public static int modifyExperience(LivingEntity entity, int experience) {
      if (entity instanceof Mob mob && mob.hasData(AetherDataAttachments.MOB_ACCESSORY)) {
         AccessoriesCapability accessories = AccessoriesCapability.get(entity);
         if (accessories != null && experience > 0) {
            SlotTypeReference[] allSlots = new SlotTypeReference[]{GlovesItem.getStaticIdentifier(), PendantItem.getStaticIdentifier()};

            for (SlotTypeReference identifier : allSlots) {
               AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
               if (accessoriesContainer != null) {
                  ItemStack stack = accessoriesContainer.getAccessories().getItem(0);
                  if (!stack.isEmpty() && ((MobAccessoryAttachment)mob.getData(AetherDataAttachments.MOB_ACCESSORY)).getEquipmentDropChance(identifier) <= 1.0F
                     )
                   {
                     experience += 1 + mob.getRandom().nextInt(3);
                  }
               }
            }
         }
      }

      return experience;
   }

   public static boolean preventInebriation(LivingEntity livingEntity, MobEffectInstance appliedInstance) {
      return livingEntity.hasEffect(AetherEffects.REMEDY) && appliedInstance.getEffect().value() == AetherEffects.INEBRIATION.get();
   }

   public static boolean preventSplit(Mob mob) {
      return mob.getType().is(AetherTags.Entities.SWETS);
   }
}
