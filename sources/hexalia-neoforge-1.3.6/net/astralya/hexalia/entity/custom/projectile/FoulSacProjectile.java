package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.gameplay.cloud.FoulCloud;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class FoulSacProjectile extends ThrowableItemProjectile {
   public FoulSacProjectile(EntityType<? extends FoulSacProjectile> type, Level level) {
      super(type, level);
   }

   public FoulSacProjectile(Level level) {
      super((EntityType)ModEntities.FOUL_SAC.get(), level);
   }

   public FoulSacProjectile(Level level, LivingEntity owner) {
      super((EntityType)ModEntities.FOUL_SAC.get(), owner, level);
   }

   protected Item getDefaultItem() {
      return (Item)ModItems.FOUL_SAC.get();
   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   protected void onHit(HitResult hit) {
      super.onHit(hit);
      if (!this.level().isClientSide) {
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               (SoundEvent)ModSoundEvents.SAC_IMPACT.get(),
               SoundSource.PLAYERS,
               0.9F,
               0.8F + this.level().getRandom().nextFloat() * 0.4F
            );
         int durationSeconds = Math.max(1, HexaliaConfig.foulSacDuration());
         FoulCloud cloud = new FoulCloud(this.level(), this.getX(), this.getY(), this.getZ(), durationSeconds);
         if (this.getOwner() instanceof LivingEntity le) {
            cloud.setCloudOwner(le);
         }

         this.level().addFreshEntity(cloud);
         this.discard();
      }
   }
}
