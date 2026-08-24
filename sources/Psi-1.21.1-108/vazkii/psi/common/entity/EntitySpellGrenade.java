package vazkii.psi.common.entity;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.Vector3;

public class EntitySpellGrenade extends EntitySpellProjectile {
   boolean sound = false;

   public EntitySpellGrenade(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
      super(type, worldIn);
   }

   protected EntitySpellGrenade(EntityType<? extends ThrowableProjectile> type, Level worldIn, LivingEntity throwerIn) {
      super(type, worldIn, throwerIn);
      double speed = 0.65;
      this.setDeltaMovement(this.getDeltaMovement().multiply(speed, speed, speed));
   }

   public EntitySpellGrenade(Level world, LivingEntity thrower) {
      this(ModEntities.spellGrenade, world, thrower);
   }

   @Override
   protected double getDefaultGravity() {
      return 0.05;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount > 60 && this.isAlive() && this.explodes()) {
         this.doExplosion();
      }
   }

   public void doExplosion() {
      if (this.getAttackTarget() != null) {
         this.cast(context -> {
            if (context != null) {
               context.attackedEntity = this.getAttackTarget();
            }
         });
      } else {
         this.cast();
      }

      this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 0.5F, 1.0F);
      double m = 0.1;

      for (int j = 0; j < 40; j++) {
         double d0 = this.getCommandSenderWorld().random.nextGaussian() * m;
         double d1 = this.getCommandSenderWorld().random.nextGaussian() * m;
         double d2 = this.getCommandSenderWorld().random.nextGaussian() * m;
         double x = this.getX() + 0.75 * ThreadLocalRandom.current().nextFloat() - 0.375;
         double y = this.getY() + 0.5 * ThreadLocalRandom.current().nextFloat();
         double z = this.getZ() + 0.75 * ThreadLocalRandom.current().nextFloat() - 0.375;
         this.getCommandSenderWorld().addParticle(ParticleTypes.EXPLOSION, x, y, z, d0, d1, d2);
      }
   }

   public boolean explodes() {
      return true;
   }

   @Override
   protected void onHit(@NotNull HitResult ray) {
      if (ray instanceof EntityHitResult && ((EntityHitResult)ray).getEntity() instanceof LivingEntity) {
         this.entityData.set(ATTACKTARGET_UUID, Optional.of(((EntityHitResult)ray).getEntity().getUUID()));
      }

      if (!this.getCommandSenderWorld().isClientSide && !this.sound && this.explodes()) {
         this.playSound(SoundEvents.CREEPER_PRIMED, 2.0F, 1.0F);
         this.sound = true;
      }

      if (ray.getType() == Type.BLOCK) {
         Direction face = ((BlockHitResult)ray).getDirection();
         Vector3 position = Vector3.fromVec3d(ray.getLocation());
         if (face != Direction.UP) {
            position.add(Vector3.fromDirection(face).multiply(0.1));
         }

         this.teleportTo(position.x, position.y, position.z);
         this.setDeltaMovement(Vec3.ZERO);
      } else if (ray.getType() == Type.ENTITY) {
         this.teleportTo(ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
         this.setDeltaMovement(Vec3.ZERO);
      }
   }
}
