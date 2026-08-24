package com.aetherteam.aether.block.dispenser;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.MobAccessoryAttachment;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.combat.loot.HammerOfKingbdogzItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.EquipAction;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import it.unimi.dsi.fastutil.Pair;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySelector.MobCanWearArmorEntitySelector;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class AetherDispenseBehaviors {
   public static final DispenseItemBehavior DISPENSE_ACCESSORY_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
         return AetherDispenseBehaviors.dispenseAccessory(blockSource, stack) ? stack : super.execute(blockSource, stack);
      }
   };
   public static final DispenseItemBehavior DISPENSE_KINGBDOGZ_HAMMER_BEHAVIOR = new DefaultDispenseItemBehavior() {
      public ItemStack execute(BlockSource source, ItemStack stack) {
         Level level = source.level();
         Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
         HammerOfKingbdogzItem item = (HammerOfKingbdogzItem)AetherItems.HAMMER_OF_KINGBDOGZ.get();
         DispenseConfig config = item.createDispenseConfig();
         Position position = config.positionFunction().getDispensePosition(source, direction);
         Projectile projectile = item.asProjectile(level, position, stack, direction);
         item.shoot(projectile, direction.getStepX(), direction.getStepY(), direction.getStepZ(), config.power(), config.uncertainty());
         level.addFreshEntity(projectile);
         stack.setDamageValue(stack.getDamageValue() + 1);
         if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
         }

         return stack;
      }

      protected void playSound(BlockSource source) {
         source.level().levelEvent(1002, source.pos(), 0);
      }
   };
   public static final DispenseItemBehavior SKYROOT_BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      public ItemStack execute(BlockSource source, ItemStack stack) {
         DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)stack.getItem();
         BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         Level level = source.level();
         if (dispensibleContainerItem.emptyContents(null, level, blockpos, null, stack)) {
            dispensibleContainerItem.checkExtraContent(null, level, stack, blockpos);
            return new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get());
         } else {
            return this.defaultDispenseItemBehavior.dispense(source, stack);
         }
      }
   };
   public static final DispenseItemBehavior SKYROOT_BUCKET_PICKUP_BEHAVIOR = new DefaultDispenseItemBehavior() {
      public ItemStack execute(BlockSource source, ItemStack stack) {
         LevelAccessor levelAccessor = source.level();
         BlockPos blockPos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         BlockState blockState = levelAccessor.getBlockState(blockPos);
         if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
            ItemStack bucketStack = bucketPickup.pickupBlock(null, levelAccessor, blockPos, blockState);
            bucketStack = SkyrootBucketItem.swapBucketType(bucketStack);
            if (bucketStack.isEmpty()) {
               return super.execute(source, stack);
            } else {
               levelAccessor.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
               Item item = bucketStack.getItem();
               return this.consumeWithRemainder(source, stack, new ItemStack(item));
            }
         } else {
            return super.execute(source, stack);
         }
      }
   };

   public static boolean dispenseAccessory(BlockSource blockSource, ItemStack stack) {
      BlockPos pos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
      List<LivingEntity> list = blockSource.level()
         .getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS.and(new MobCanWearArmorEntitySelector(stack)));
      if (list.isEmpty()) {
         return false;
      } else {
         LivingEntity livingEntity = (LivingEntity)list.getFirst();
         ItemStack itemStack = stack.split(1);
         AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
         if (capability != null) {
            Accessory accessory = AccessoriesAPI.getOrDefaultAccessory(itemStack);
            Pair<SlotReference, EquipAction> equipReference = capability.canEquipAccessory(itemStack, true);
            if (equipReference != null) {
               SlotTypeReference slotTypeReference = new SlotTypeReference(((SlotReference)equipReference.first()).slotName());
               if (accessory.canEquip(itemStack, (SlotReference)equipReference.first())) {
                  accessory.onEquipFromUse(itemStack, (SlotReference)equipReference.left());
                  ((EquipAction)equipReference.second()).equipStack(itemStack.copy());
                  if (livingEntity instanceof ArmorStand armorStand) {
                     if (((SlotReference)equipReference.first()).slotName().equals(GlovesItem.getStaticIdentifier().slotName())) {
                        armorStand.setShowArms(true);
                     }
                  } else if (livingEntity instanceof Mob mob && EntityHooks.canMobSpawnWithAccessories(mob)) {
                     ((MobAccessoryAttachment)mob.getData(AetherDataAttachments.MOB_ACCESSORY)).setGuaranteedDrop(slotTypeReference);
                     mob.setPersistenceRequired();
                  }
               }
            }
         }

         return true;
      }
   }
}
