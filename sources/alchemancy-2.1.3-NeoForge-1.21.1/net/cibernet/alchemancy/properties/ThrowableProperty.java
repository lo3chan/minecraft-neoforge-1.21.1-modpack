package net.cibernet.alchemancy.properties;

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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class ThrowableProperty extends Property {
   public static final float THROW_VELOCITY = 1.5F;

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!this.preventRecursion(event.getItemStack()) && !event.isCanceled()) {
         this.throwItem(event.getLevel(), event.getEntity(), event.getItemStack());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (currentResult != InfusionPropertyDispenseBehavior.DispenseResult.PASS) {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      } else {
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DISPENSING)
            && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW)) {
            ItemStack storedItem = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
            if (storedItem.isEmpty()) {
               return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
            }

            storedItem.shrink(1);
            ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, storedItem);
            stack = storedItem.copy();
         }

         stack = stack.copy();
         stack.setCount(1);
         this.throwItem(stack, blockSource, direction);
         return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
      }
   }

   private void throwItem(ItemStack stack, BlockSource blockSource, Direction direction) {
      Position position = DispenseConfig.DEFAULT.positionFunction().getDispensePosition(blockSource, direction);
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
         DispenseConfig.DEFAULT.uncertainty()
      );
      InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
   }

   private void throwItem(
      Level level, ItemStack stack, double x, double y, double z, double xScale, double yScale, double zScale, float power, float inaccuracy
   ) {
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

   private void throwItem(Level level, LivingEntity user, ItemStack stack) {
      if (!stack.isEmpty()) {
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DISPENSING)
            && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW)) {
            ItemStack storedItem = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack).copy();
            if (storedItem.isEmpty()) {
               user.playSound(SoundEvents.DISPENSER_FAIL);
               return;
            }

            ItemStack thrown = storedItem.split(1);
            ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, storedItem);
            stack = thrown;
         }

         if (user.hasInfiniteMaterials()) {
            stack = stack.copy();
         }

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
            boolean sharpshooting = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHARPSHOOTING);
            Projectile projectile;
            if (stack.getItem() instanceof ProjectileItem projectileItem) {
               projectile = projectileItem.asProjectile(level, new Vec3(user.getX(), user.getEyeY() - 0.10000000149011612, user.getZ()), stack, Direction.UP);
            } else {
               InfusedItemProjectile itemProjectile = new InfusedItemProjectile(user, level);
               itemProjectile.setItem(stack.split(1));
               projectile = itemProjectile;
            }

            projectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F * (sharpshooting ? 1.5F : 1.0F), 1.0F);
            level.addFreshEntity(projectile);
         }

         if (user instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
         }

         stack.consume(1, user);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14667419;
   }
}
