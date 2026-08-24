package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class HollowProperty extends Property implements IDataHolder<ItemStack> {
   @Override
   public int getColor(ItemStack stack) {
      return 7360551;
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (currentResult != InfusionPropertyDispenseBehavior.DispenseResult.PASS) {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      } else {
         ItemStack storedStack = this.getData(stack);
         if (!stack.isEmpty()) {
            DefaultDispenseItemBehavior.spawnItem(blockSource.level(), storedStack, 6, direction, DispenserBlock.getDispensePosition(blockSource));
            this.setData(stack, this.getDefaultData());
            InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
            return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
         } else {
            return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
         }
      }
   }

   @Override
   public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, Pre event) {
      if (canPickUp) {
         ItemStack storedStack = this.getData(stack);
         ItemStack stackToPickUp = event.getItemEntity().getItem();
         int toPickUp = 0;
         if (storedStack.isEmpty()) {
            storedStack = stackToPickUp.copy();
            toPickUp = stackToPickUp.getCount();
            stackToPickUp.setCount(0);
         } else if (ItemStack.isSameItemSameComponents(storedStack, stackToPickUp)) {
            int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
            toPickUp = Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + stackToPickUp.getCount());
            storedStack.setCount(toPickUp);
            stackToPickUp.shrink(mergeLimit);
         }

         if (toPickUp > 0) {
            this.playInsertSound(user);
            user.take(itemToPickUp, toPickUp);
            this.setData(stack, storedStack);
         }
      }
   }

   @Override
   public void onStackedOverItem(
      ItemStack hollowItem,
      ItemStack carriedItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get() && clickAction == ClickAction.SECONDARY) {
         ItemStack storedStack = this.getData(hollowItem);
         if (!storedStack.isEmpty()) {
            if (carriedItem.isEmpty()) {
               stackedOnSlot.set(storedStack);
               this.setData(hollowItem, this.getDefaultData());
               isCancelled.set(true);
               return;
            }

            if (ItemStack.isSameItemSameComponents(storedStack, carriedItem)) {
               int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
               storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + carriedItem.getCount()));
               carriedItem.shrink(mergeLimit);
               this.setData(hollowItem, storedStack);
               stackedOnSlot.set(carriedItem);
               isCancelled.set(true);
            }
         }

         if (!carriedItem.isEmpty() && this.storeItem(player, hollowItem, carriedItem)) {
            stackedOnSlot.set(carriedItem);
            isCancelled.set(true);
         }
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get() && clickAction == ClickAction.SECONDARY) {
         ItemStack storedStack = this.getData(stackedOnItem);
         if (!storedStack.isEmpty()) {
            if (carriedItem.isEmpty()) {
               carriedSlot.set(storedStack);
               this.setData(stackedOnItem, this.getDefaultData());
               isCancelled.set(true);
               return;
            }

            if (ItemStack.isSameItemSameComponents(storedStack, carriedItem)) {
               int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
               storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + carriedItem.getCount()));
               carriedItem.shrink(mergeLimit);
               this.setData(stackedOnItem, storedStack);
               carriedSlot.set(carriedItem);
               isCancelled.set(true);
            }
         }

         if (!carriedItem.isEmpty() && this.storeItem(player, stackedOnItem, carriedItem)) {
            carriedSlot.set(carriedItem);
            isCancelled.set(true);
         }
      }
   }

   public boolean storeItem(@Nullable Entity player, ItemStack hollowItem, ItemStack itemToPickUp) {
      ItemStack storedStack = this.getData(hollowItem);
      if (this.canStore(hollowItem, itemToPickUp)) {
         if (storedStack.isEmpty()) {
            storedStack = itemToPickUp.copy();
            itemToPickUp.setCount(0);
         } else {
            int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
            storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + itemToPickUp.getCount()));
            itemToPickUp.shrink(mergeLimit);
         }

         if (player != null) {
            this.playInsertSound(player);
         }

         this.setData(hollowItem, storedStack);
         return true;
      } else {
         return false;
      }
   }

   public boolean canStore(ItemStack hollowItem, ItemStack itemToPickUp) {
      ItemStack storedStack = this.getData(hollowItem);
      return storedStack.isEmpty()
         || ItemStack.isSameItemSameComponents(storedStack, itemToPickUp) && storedStack.getCount() + itemToPickUp.getCount() <= storedStack.getMaxStackSize();
   }

   public boolean isFull(ItemStack hollowItem) {
      ItemStack storedStack = this.getData(hollowItem);
      return !storedStack.isEmpty() && storedStack.getCount() >= storedStack.getMaxStackSize();
   }

   private void playInsertSound(Entity entity) {
      entity.playSound((SoundEvent)AlchemancySoundEvents.HOLLOW_INSERT.value(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity entity) {
      entity.playSound((SoundEvent)AlchemancySoundEvents.HOLLOW_DROP_CONTENTS.value(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      ItemStack rootStack = root.getItem();
      if (!this.isFull(rootStack)) {
         Level level = root.getLevel();
         BlockPos pos = root.getBlockPos();

         for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, root.getBlockState().getShape(level, pos).bounds().move(pos))) {
            ItemStack itemToPickUp = itemEntity.getItem();
            if (!itemEntity.isRemoved() && this.storeItem(null, rootStack, itemToPickUp) && itemToPickUp.getCount() <= 0) {
               itemEntity.discard();
               break;
            }
         }
      }
   }

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      ItemStack heldItem = user.getItemInHand(hand);
      if (heldItem.isEmpty() && !InfusedPropertiesHelper.hasProperty(root.getItem(), AlchemancyProperties.DISPENSING)) {
         user.addItem(this.getData(root.getItem()));
         this.setData(root.getItem(), this.getDefaultData());
         return ItemInteractionResult.sidedSuccess(user.level().isClientSide());
      } else {
         return this.storeItem(user, root.getItem(), heldItem) ? ItemInteractionResult.sidedSuccess(user.level().isClientSide()) : null;
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.getLevel().isClientSide() && !event.isCanceled()) {
         ItemStack stack = event.getItemStack();
         ItemStack storedStack = this.getData(stack);
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.THROWABLE)
            && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DISPENSING)) {
            return;
         }

         if (!stack.isEmpty()) {
            if (event.getEntity().isShiftKeyDown()) {
               ItemStack stackToDrop = storedStack.copy();
               stackToDrop.setCount(1);
               ItemEntity item = event.getEntity().drop(stackToDrop, true);
               if (item != null) {
                  item.setDefaultPickUpDelay();
               }

               storedStack.shrink(1);
               this.setData(stack, storedStack);
               this.playDropContentsSound(event.getEntity());
            } else {
               this.dropItems(stack, storedStack, event.getEntity());
            }
         }
      }
   }

   public boolean dropItems(ItemStack hollowItem, Entity user) {
      return this.dropItems(hollowItem, this.getData(hollowItem), user);
   }

   protected boolean dropItems(ItemStack hollowItem, ItemStack toDrop, Entity user) {
      if (toDrop.isEmpty()) {
         return false;
      } else {
         if (user instanceof Player player) {
            ItemEntity item = player.drop(toDrop, true);
            if (item != null) {
               item.setDefaultPickUpDelay();
            }
         } else if (nonPlayerDrop(user, toDrop, false, true) == null) {
            return false;
         }

         this.setData(hollowItem, ItemStack.EMPTY);
         this.playDropContentsSound(user);
         return true;
      }
   }

   public static ItemEntity drop(Entity user, ItemStack droppedItem, boolean dropAround, boolean includeThrowerName) {
      return user instanceof Player player
         ? player.drop(droppedItem, dropAround, includeThrowerName)
         : nonPlayerDrop(user, droppedItem, dropAround, includeThrowerName);
   }

   public static ItemEntity nonPlayerDrop(Entity user, ItemStack droppedItem, boolean dropAround, boolean includeThrowerName) {
      if (droppedItem.isEmpty()) {
         return null;
      } else {
         if (user.level().isClientSide && user instanceof LivingEntity living) {
            living.swing(InteractionHand.MAIN_HAND);
         }

         double d0 = user.getEyeY() - 0.30000001192092896;
         ItemEntity itementity = new ItemEntity(user.level(), user.getX(), d0, user.getZ(), droppedItem);
         itementity.setDefaultPickUpDelay();
         if (includeThrowerName) {
            itementity.setThrower(user);
         }

         RandomSource random = user.getRandom();
         if (dropAround) {
            float f = random.nextFloat() * 0.5F;
            float f1 = random.nextFloat() * 6.2831855F;
            itementity.setDeltaMovement(-Mth.sin(f1) * f, 0.20000000298023224, Mth.cos(f1) * f);
         } else {
            float f7 = 0.3F;
            float f8 = Mth.sin(user.getXRot() * 0.017453292F);
            float f2 = Mth.cos(user.getXRot() * 0.017453292F);
            float f3 = Mth.sin(user.getYRot() * 0.017453292F);
            float f4 = Mth.cos(user.getYRot() * 0.017453292F);
            float f5 = random.nextFloat() * 6.2831855F;
            float f6 = 0.02F * random.nextFloat();
            itementity.setDeltaMovement(
               -f3 * f2 * 0.3F + Math.cos(f5) * f6, -f8 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, f4 * f2 * 0.3F + Math.sin(f5) * f6
            );
         }

         if (!user.level().isClientSide) {
            user.getCommandSenderWorld().addFreshEntity(itementity);
         }

         return itementity;
      }
   }

   @Override
   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {
      if (!stack.has(DataComponents.INTANGIBLE_PROJECTILE) && !(itemEntity instanceof AbstractArrow arrow && arrow.pickup == Pickup.DISALLOWED)) {
         ItemStack storedItem = this.getData(stack);
         if (!storedItem.isEmpty()) {
            onContainerDestroyed(itemEntity, List.of(storedItem));
            this.setData(stack, this.getDefaultData());
         }
      }
   }

   public static void onContainerDestroyed(Entity container, Iterable<ItemStack> contents) {
      Level level = container.level();
      if (!level.isClientSide) {
         contents.forEach(p_352858_ -> level.addFreshEntity(new ItemEntity(level, container.getX(), container.getY(), container.getZ(), p_352858_)));
      }
   }

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      if (user != null && stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount) {
         this.dropItems(stack, user);
      }

      return resultingAmount;
   }

   public ItemStack readData(CompoundTag tag) {
      return tag.isEmpty() ? this.getDefaultData() : ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(this.getDefaultData());
   }

   public CompoundTag writeData(final ItemStack data) {
      return new CompoundTag() {
         {
            if (!data.isEmpty()) {
               this.put("item", data.save(CommonUtils.registryAccessStatic()));
            }
         }
      };
   }

   public ItemStack combineData(@Nullable ItemStack currentData, ItemStack newData) {
      if (currentData != null && !currentData.isEmpty()) {
         if (ItemStack.matches(currentData, newData)) {
            currentData.setCount(Math.min(currentData.getMaxStackSize(), currentData.getCount() + newData.getCount()));
         }

         return currentData;
      } else {
         return newData;
      }
   }

   public ItemStack getDefaultData() {
      return ItemStack.EMPTY;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      ItemStack storedStack = this.getData(stack);
      return (Component)(!storedStack.isEmpty()
         ? Component.translatable(
               "property.detail",
               new Object[]{name, Component.translatable("property.detail.item_count", new Object[]{storedStack.getHoverName(), storedStack.getCount()})}
            )
            .withColor(this.getColor(stack))
         : name);
   }

   public boolean shrinkContents(ItemStack hollowItem, int amount) {
      ItemStack storedItem = this.getData(hollowItem);
      if (!hollowItem.isEmpty() && storedItem.getCount() >= amount) {
         storedItem.shrink(amount);
         this.setData(hollowItem, storedItem.isEmpty() ? ItemStack.EMPTY : storedItem);
         return true;
      } else {
         return false;
      }
   }
}
