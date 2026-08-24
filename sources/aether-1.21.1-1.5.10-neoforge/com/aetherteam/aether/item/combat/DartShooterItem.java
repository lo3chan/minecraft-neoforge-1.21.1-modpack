package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.projectile.dart.AbstractDart;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

public class DartShooterItem extends ProjectileWeaponItem {
   private final Supplier<? extends Item> dartType;

   public DartShooterItem(Supplier<? extends Item> dartType, Properties properties) {
      super(properties);
      this.dartType = dartType;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      boolean hasAmmo = !player.getProjectile(heldStack).isEmpty();
      InteractionResultHolder<ItemStack> result = EventHooks.onArrowNock(heldStack, level, player, hand, hasAmmo);
      if (result == null) {
         if (!player.getAbilities().instabuild && !hasAmmo) {
            return InteractionResultHolder.fail(heldStack);
         } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(heldStack);
         }
      } else {
         return result;
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if (user instanceof Player player) {
         ItemStack itemStack = player.getProjectile(stack);
         if (!itemStack.isEmpty()) {
            EventHooks.onArrowLoose(stack, level, player, 0, !itemStack.isEmpty());
            List<ItemStack> list = draw(stack, itemStack, player);
            if (level instanceof ServerLevel serverlevel && !list.isEmpty()) {
               this.shoot(serverlevel, player, player.getUsedItemHand(), stack, list, 3.1F, 1.2F, false, null);
            }

            level.playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               (SoundEvent)AetherSoundEvents.ITEM_DART_SHOOTER_SHOOT.get(),
               SoundSource.PLAYERS,
               1.0F,
               1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            player.awardStat(Stats.ITEM_USED.get(this));
         }
      }

      return stack;
   }

   protected void shootProjectile(
      LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target
   ) {
      projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, inaccuracy);
   }

   protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
      DartItem dartItem = ammo.getItem() instanceof DartItem dart ? dart : (DartItem)this.getDartType().get();
      AbstractDart dartx = dartItem.createDart(level, ammo, shooter, weapon);
      if (dartx != null) {
         dartx.setNoGravity(true);
         return this.customDart(dartx, ammo, weapon);
      } else {
         return super.createProjectile(level, shooter, weapon, ammo, isCrit);
      }
   }

   public AbstractDart customDart(AbstractDart dart, ItemStack projectileStack, ItemStack weaponStack) {
      return dart;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 10;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BOW;
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return stack -> stack.is(this.getDartType().get());
   }

   public int getDefaultProjectileRange() {
      return 15;
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
      return super.isPrimaryItemFor(stack, enchantment) && !enchantment.is(Enchantments.FLAME) && !enchantment.is(Enchantments.INFINITY);
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return super.supportsEnchantment(stack, enchantment) && !enchantment.is(Enchantments.FLAME) && !enchantment.is(Enchantments.INFINITY);
   }

   public Supplier<? extends Item> getDartType() {
      return this.dartType;
   }
}
