package net.cibernet.alchemancy.properties.special;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.HollowProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class ItemMagnetProperty extends Property {
   private static final double RADIUS = 5.0;

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user.tickCount % 10 == 0) {
         if (user instanceof Player player) {
            for (ItemEntity itemEntity : user.level()
               .getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().inflate(5.0), item -> !item.hasPickUpDelay() && canPickUp(item, stack))) {
               itemEntity.playerTouch(player);
            }
         } else {
            for (ItemEntity itemEntity : user.level().getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().inflate(5.0), item -> canPickUp(item, stack))) {
               itemEntity.setPos(user.position());
            }
         }
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      for (ItemEntity itemEntity : projectile.level()
         .getEntitiesOfClass(ItemEntity.class, projectile.getBoundingBox().inflate(5.0), item -> canPickUp(item, stack))) {
         itemEntity.setPos(projectile.position());
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity projectile) {
      for (ItemEntity itemEntity : projectile.level()
         .getEntitiesOfClass(ItemEntity.class, projectile.getBoundingBox().inflate(5.0), item -> canPickUp(item, stack))) {
         itemEntity.setPos(projectile.position());
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      if (root.getTickCount() % 10 == 0) {
         for (ItemEntity itemEntity : root.getLevel()
            .getEntitiesOfClass(
               ItemEntity.class, CommonUtils.boundingBoxAroundPoint(root.getBlockPos().getCenter(), 5.0F), item -> canPickUp(item, root.getItem())
            )) {
            itemEntity.setPos(root.getBlockPos().getCenter());
         }
      }
   }

   public static boolean canPickUp(ItemEntity target, ItemStack source) {
      ItemStack filter = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(source);
      return filter.isEmpty() || ItemStack.isSameItem(target.getItem(), filter);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 12480767;
   }
}
