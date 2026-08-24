package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class ScattershotProperty extends HollowProperty {
   public static final float THROW_VELOCITY = 1.5F;
   public static final int THROW_AMOUNT = 8;

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         this.throwItem(event.getLevel(), event.getEntity(), event.getItemStack(), 8);
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, Pre event) {
   }

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      return null;
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (currentResult != InfusionPropertyDispenseBehavior.DispenseResult.PASS) {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      } else {
         ItemStack storedItem = this.getData(stack);
         if (storedItem.isEmpty()) {
            return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
         } else {
            ItemStack var7 = storedItem.split(8);
            this.setData(stack, storedItem);
            stack = var7.copy();
            stack.setCount(1);
            this.throwItem(stack, blockSource, direction);
            return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
         }
      }
   }

   private void throwItem(ItemStack stack, BlockSource blockSource, Direction direction) {
      Position position = DispenseConfig.DEFAULT.positionFunction().getDispensePosition(blockSource, direction);
      float throwInaccuracy = this.getThrowInaccuracy(stack);
      this.throwItem(
         blockSource.level(),
         stack,
         position.x(),
         position.y(),
         position.z(),
         direction.getStepX(),
         direction.getStepY(),
         direction.getStepZ(),
         DispenseConfig.DEFAULT.power() * (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHARPSHOOTING) ? 1.5F : 1.0F),
         DispenseConfig.DEFAULT.uncertainty() * throwInaccuracy,
         8
      );
      InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
   }

   private void throwItem(
      Level level, ItemStack stack, double x, double y, double z, double xScale, double yScale, double zScale, float power, float inaccuracy, int amount
   ) {
      for (int i = 0; i < amount && !stack.isEmpty(); i++) {
         Projectile projectile;
         if (stack.getItem() instanceof ProjectileItem projectileItem) {
            projectile = projectileItem.asProjectile(level, new Vec3(x, y, z), stack, Direction.UP);
         } else {
            InfusedItemProjectile itemProjectile = new InfusedItemProjectile(x, y, z, level);
            itemProjectile.setItem(stack.split(1));
            projectile = itemProjectile;
         }

         projectile.shoot(xScale, yScale, zScale, power, inaccuracy);
         level.addFreshEntity(projectile);
      }
   }

   private void throwItem(Level level, LivingEntity user, ItemStack stack, int amount) {
      if (!stack.isEmpty()) {
         ItemStack storedItem = this.getData(stack);
         if (storedItem.isEmpty()) {
            user.playSound(SoundEvents.DISPENSER_FAIL);
         } else {
            ItemStack var12 = storedItem.split(8);
            this.setData(stack, storedItem);
            level.playSound(
               null,
               user.getX(),
               user.getY(),
               user.getZ(),
               (SoundEvent)AlchemancySoundEvents.THROWABLE.value(),
               SoundSource.PLAYERS,
               0.5F,
               0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (!level.isClientSide) {
               boolean sharpshooting = InfusedPropertiesHelper.hasProperty(var12, AlchemancyProperties.SHARPSHOOTING);
               float throwInaccuracy = this.getThrowInaccuracy(var12);

               for (int i = 0; i < amount && !var12.isEmpty(); i++) {
                  Projectile projectile;
                  if (var12.getItem() instanceof ProjectileItem projectileItem) {
                     projectile = projectileItem.asProjectile(
                        level, new Vec3(user.getX(), user.getEyeY() - 0.10000000149011612, user.getZ()), var12, Direction.UP
                     );
                  } else {
                     InfusedItemProjectile itemProjectile = new InfusedItemProjectile(user, level);
                     itemProjectile.setItem(var12.split(1));
                     projectile = itemProjectile;
                  }

                  projectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F * (sharpshooting ? 1.5F : 1.0F), throwInaccuracy);
                  level.addFreshEntity(projectile);
               }
            }

            if (user instanceof Player player) {
               player.awardStat(Stats.ITEM_USED.get(var12.getItem()));
            }

            var12.consume(1, user);
         }
      }
   }

   private float getThrowInaccuracy(ItemStack stack) {
      return stack.getCount();
   }

   @Override
   public int getColor(ItemStack stack) {
      return 13383168;
   }
}
