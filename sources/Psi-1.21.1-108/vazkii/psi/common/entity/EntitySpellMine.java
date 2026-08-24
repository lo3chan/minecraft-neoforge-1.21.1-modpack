package vazkii.psi.common.entity;

import java.util.List;
import java.util.Optional;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class EntitySpellMine extends EntitySpellGrenade {
   boolean triggered = false;

   public EntitySpellMine(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
      super(type, worldIn);
   }

   public EntitySpellMine(Level worldIn, LivingEntity throwerIn) {
      super(ModEntities.spellMine, worldIn, throwerIn);
   }

   @Override
   public void tick() {
      super.tick();
      List<LivingEntity> entities = this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0, 1.0, 1.0));
      Entity thrower = this.getOwner();
      if (thrower != null && this.tickCount < 30) {
         entities.remove(thrower);
      }

      if (!entities.isEmpty()) {
         if (!this.triggered) {
            this.playSound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, 0.5F, 0.6F);
         }

         this.triggered = true;
         this.entityData.set(ATTACKTARGET_UUID, Optional.of(((LivingEntity)entities.getFirst()).getUUID()));
      } else if (this.triggered) {
         this.doExplosion();
      }
   }

   @Override
   public int getParticleCount() {
      return 1;
   }

   @Override
   public int getLiveTime() {
      return 6000;
   }

   @Override
   public boolean explodes() {
      return false;
   }
}
