package net.Pandarix.entity;

import net.Pandarix.config.BAConfig;
import net.Pandarix.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BombEntity extends ThrowableItemProjectile {
   public BombEntity(EntityType<? extends BombEntity> entityType, Level level) {
      super(entityType, level);
   }

   public BombEntity(Level level, LivingEntity owner) {
      super((EntityType)ModEntityTypes.BOMB_ENTITY.get(), owner, level);
   }

   public BombEntity(Level pLevel, double pX, double pY, double pZ) {
      super((EntityType)ModEntityTypes.BOMB_ENTITY.get(), pX, pY, pZ, pLevel);
   }

   @NotNull
   protected Item getDefaultItem() {
      return (Item)ModItems.BOMB_ITEM.get();
   }

   public void handleEntityEvent(byte pId) {
      if (pId == 3) {
         Level level = this.level();
         RandomSource random = level.getRandom();

         for (int i = 0; i < 25; i++) {
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               this.getX(),
               this.getY(),
               this.getZ(),
               random.nextDouble() / 5.0 * random.nextIntBetweenInclusive(-1, 1),
               random.nextDouble() / 2.0,
               random.nextDouble() / 5.0 * random.nextIntBetweenInclusive(-1, 1)
            );
         }
      }
   }

   public void tick() {
      Level level = this.level();
      RandomSource random = level.getRandom();
      if (random.nextBoolean()) {
         level.addParticle(random.nextBoolean() ? ParticleTypes.FLAME : ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }

      super.tick();
   }

   protected void onHit(HitResult pResult) {
      super.onHit(pResult);
      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }

      this.level()
         .explode(this, this.getX(), this.getY(), this.getZ(), 2.5F, BAConfig.rustyBombTerrainDamage ? ExplosionInteraction.TNT : ExplosionInteraction.NONE);
   }
}
