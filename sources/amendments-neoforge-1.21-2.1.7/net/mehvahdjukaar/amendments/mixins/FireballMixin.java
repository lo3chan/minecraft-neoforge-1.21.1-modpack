package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.client.TumblingAnimation;
import net.mehvahdjukaar.amendments.common.ProjectileStats;
import net.mehvahdjukaar.amendments.common.entity.IVisualTransformationProvider;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.entity.ParticleTrailEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Fireball.class})
public abstract class FireballMixin extends AbstractHurtingProjectile implements IVisualTransformationProvider {
   @Unique
   private boolean amendments$isExtinguished = false;
   @Unique
   private final ParticleTrailEmitter amendments$trailEmitter = ProjectileStats.makeFireballTrialEmitter();
   @Unique
   private final TumblingAnimation amendments$tumblingAnimation = ProjectileStats.makeTumbler();

   protected FireballMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public Matrix4f amendments$getVisualTransformation(float partialTicks) {
      return new Matrix4f().rotate(this.amendments$tumblingAnimation.getRotation(partialTicks));
   }

   @Shadow
   public abstract ItemStack getItem();

   @Nullable
   public ItemStack getPickResult() {
      return this.getItem().copyWithCount(1);
   }

   public boolean displayFireAnimation() {
      return this.level().isClientSide
            && (this.getType() == EntityType.FIREBALL || this.getType() == EntityType.SMALL_FIREBALL)
            && ClientConfigs.FIREBALL_3D.get()
         ? false
         : super.displayFireAnimation();
   }

   protected boolean shouldBurn() {
      return this.amendments$isExtinguished && this.getType() == EntityType.FIREBALL ? false : super.shouldBurn();
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (ClientConfigs.GHAST_FIREBALL_TRAIL.get()) {
            this.amendments$trailEmitter.tick(this, (p, v) -> {
               if (!this.isInWater()) {
                  this.level().addParticle((ParticleOptions)ModRegistry.FIREBALL_TRAIL_PARTICLE.get(), p.x, p.y, p.z, this.getBbWidth() * 0.8, 0.0, 0.0);
               }
            });
         }

         if (ClientConfigs.CHARGES_TUMBLE.get()) {
            this.amendments$tumblingAnimation.tick(this.random);
         }
      }

      if (!this.amendments$isExtinguished && this.isInWater()) {
         this.amendments$isExtinguished = true;
         if (!this.level().isClientSide()) {
            this.clearFire();
            this.playEntityOnFireExtinguishedSound();
            if (this.getType() == EntityType.SMALL_FIREBALL) {
               this.discard();
            }
         }
      }
   }
}
