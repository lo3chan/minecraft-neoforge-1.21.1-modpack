package net.cibernet.alchemancy.properties.voidborn;

import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class BigSuckProperty extends Property {
   @Override
   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      this.suckBigly(level, user);
      this.damageItem(user, stack, EquipmentSlot.MAINHAND, 20);
      return false;
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (target != source) {
         this.suckBigly(target.level(), target);
         this.damageOrConsumeItem(target.level(), source, stack, EquipmentSlot.MAINHAND, 20);
      }
   }

   @Override
   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {
      if (target.level() instanceof ServerLevel serverLevel) {
         boolean var9 = false;

         for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemx -> true)) {
            item.moveTo(position.getBottomCenter());
            item.setDeltaMovement(Vec3.ZERO);
            var9 = true;
         }

         if (var9) {
            this.damageOrConsumeItem(level, null, stack, EquipmentSlot.MAINHAND, 20);
         }
      }
   }

   private void suckBigly(Level level, Entity user) {
      if (level instanceof ServerLevel serverLevel) {
         if (user instanceof Player player) {
            for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemx -> true)) {
               item.playerTouch(player);
            }
         } else {
            for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemx -> true)) {
               item.moveTo(user.position());
               item.setDeltaMovement(Vec3.ZERO);
            }
         }
      }
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (!projectile.isRemoved() && !event.isCanceled() && projectile.level() instanceof ServerLevel serverLevel) {
         boolean var9 = false;

         for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemx -> true)) {
            item.moveTo(rayTraceResult.getLocation());
            var9 = true;
         }

         if (var9) {
            projectile.discard();
         }
      }
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      return List.of();
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16711935;
   }
}
