package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

public class EntitySharkToothArrow extends Arrow {
   public EntitySharkToothArrow(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   public EntitySharkToothArrow(EntityType type, double x, double y, double z, Level worldIn) {
      this(type, worldIn);
      this.setPos(x, y, z);
   }

   public EntitySharkToothArrow(Level worldIn, LivingEntity shooter) {
      this(AMEntityRegistry.SHARK_TOOTH_ARROW.get(), shooter.getX(), shooter.getEyeY() - 0.10000000149011612, shooter.getZ(), worldIn);
      this.setOwner(shooter);
      if (shooter instanceof Player) {
         this.pickup = Pickup.ALLOWED;
      }
   }

   public EntitySharkToothArrow(Level worldIn, LivingEntity shooter, ItemStack ammo) {
      this(worldIn, shooter);
      this.setPickupItemStack(ammo);
   }

   protected void damageShield(Player player, float damage) {
      if (damage >= 3.0F && AMCompat.canShieldBlock(player.getUseItem())) {
         ItemStack copyBeforeUse = player.getUseItem().copy();
         int i = 1 + Mth.floor(damage);
         AMCompat.hurtAndBreak(player.getUseItem(), i, player, EquipmentSlot.CHEST);
         if (player.getUseItem().isEmpty()) {
            InteractionHand Hand = player.getUsedItemHand();
            EventHooks.onPlayerDestroyItem(player, copyBeforeUse, Hand);
            if (Hand == InteractionHand.MAIN_HAND) {
               player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
               player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            player.stopUsingItem();
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().getRandom().nextFloat() * 0.4F);
         }
      }
   }

   protected void doPostHurtEffects(LivingEntity living) {
      if (living instanceof Player) {
         this.damageShield((Player)living, (float)this.getBaseDamage());
      }

      Entity entity1 = this.getOwner();
      if (AMCompat.isAquatic(living) || living instanceof Drowned || !AMCompat.isUndead(living) && living.canBreatheUnderwater()) {
         DamageSource damagesource;
         if (entity1 == null) {
            damagesource = this.damageSources().arrow(this, this);
         } else {
            damagesource = this.damageSources().arrow(this, entity1);
         }

         living.hurt(damagesource, 7.0F);
      }
   }

   public boolean isInWater() {
      return false;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   protected ItemStack getPickupItem() {
      return new ItemStack((ItemLike)AMItemRegistry.SHARK_TOOTH_ARROW.get());
   }
}
