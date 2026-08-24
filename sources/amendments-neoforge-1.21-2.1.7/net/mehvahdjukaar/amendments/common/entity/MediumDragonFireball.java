package net.mehvahdjukaar.amendments.common.entity;

import net.mehvahdjukaar.amendments.client.TumblingAnimation;
import net.mehvahdjukaar.amendments.common.ProjectileStats;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.entity.ImprovedProjectileEntity;
import net.mehvahdjukaar.moonlight.api.entity.ParticleTrailEmitter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class MediumDragonFireball extends ImprovedProjectileEntity implements IVisualTransformationProvider {
   private final ParticleTrailEmitter trailEmitter = ProjectileStats.makeDragonTrialEmitter(false);
   private final TumblingAnimation tumblingAnimation = ProjectileStats.makeTumbler();

   public MediumDragonFireball(EntityType<? extends MediumDragonFireball> entityType, Level level) {
      super(entityType, level);
      if (!CommonConfigs.FIRE_CHARGE_GRAVITY.get()) {
         this.setNoGravity(true);
      }
   }

   public MediumDragonFireball(Level level, double x, double y, double z) {
      super(ModRegistry.MEDIUM_DRAGON_FIREBALL.get(), x, y, z, level);
      if (!CommonConfigs.FIRE_CHARGE_GRAVITY.get()) {
         this.setNoGravity(true);
      }
   }

   public MediumDragonFireball(Level level, LivingEntity shooter) {
      super(ModRegistry.MEDIUM_DRAGON_FIREBALL.get(), shooter, level);
      if (!CommonConfigs.FIRE_CHARGE_GRAVITY.get()) {
         this.setNoGravity(true);
      }
   }

   public void spawnTrailParticles() {
      super.spawnTrailParticles();
      this.trailEmitter
         .tick(
            this,
            (p, motion) -> {
               if (!this.isInWater()) {
                  this.level()
                     .addParticle(
                        ParticleTypes.DRAGON_BREATH,
                        p.x,
                        p.y,
                        p.z,
                        this.random.nextGaussian() * 0.04,
                        this.random.nextGaussian() * 0.04,
                        this.random.nextGaussian() * 0.04
                     );
               }
            }
         );
      if (ClientConfigs.CHARGES_TUMBLE.get()) {
         this.tumblingAnimation.tick(this.random);
      }
   }

   @Override
   public Matrix4f amendments$getVisualTransformation(float partialTicks) {
      return new Matrix4f().rotate(this.tumblingAnimation.getRotation(partialTicks));
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if ((result.getType() != Type.ENTITY || !this.ownedBy(((EntityHitResult)result).getEntity())) && !this.level().isClientSide) {
         if (!this.isSilent()) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, this.random.nextFloat() * 0.1F + 0.9F);
         }

         this.spawnCloud();
         this.discard();
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      Entity entity = result.getEntity();
      entity.hurt(this.level().damageSources().indirectMagic(this, this.getOwner()), 2.0F);
   }

   private void spawnCloud() {
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      Entity entity = this.getOwner();
      if (entity instanceof LivingEntity) {
         areaEffectCloud.setOwner((LivingEntity)entity);
      }

      areaEffectCloud.setParticle(ParticleTypes.DRAGON_BREATH);
      areaEffectCloud.setRadius(1.0F);
      areaEffectCloud.setDuration(40);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setRadiusPerTick((6.5F - areaEffectCloud.getRadius()) / areaEffectCloud.getDuration());
      areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
      this.level().addFreshEntity(areaEffectCloud);
   }

   @Nullable
   public ItemStack getPickResult() {
      return super.getPickResult();
   }

   public boolean isPickable() {
      return CommonConfigs.DEFLECT_FIRE_CHARGES.get();
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public boolean hurt(DamageSource source, float amount) {
      return false;
   }

   protected Item getDefaultItem() {
      return ModRegistry.DRAGON_CHARGE.get();
   }

   public void handleEntityEvent(byte id) {
      super.handleEntityEvent(id);
      Level level = this.level();
      if (id == 3) {
         for (int x = 0; x < 30; x++) {
            float f = 0.2F;
            float speed = this.random.nextFloat() * f;
            float angle = this.random.nextFloat() * 6.2831855F;
            double sx = Mth.cos(angle) * speed;
            double sy = 0.01 + this.random.nextDouble() * f * 0.2;
            double sz = Mth.sin(angle) * speed;
            level.addParticle(ParticleTypes.DRAGON_BREATH, this.getX() + sx * 0.1, this.getY() + 0.3, this.getZ() + sz * 0.1, sx, sy, sz);
         }
      }
   }
}
