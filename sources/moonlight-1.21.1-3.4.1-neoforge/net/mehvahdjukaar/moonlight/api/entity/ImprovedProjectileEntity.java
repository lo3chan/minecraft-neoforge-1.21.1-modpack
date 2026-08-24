package net.mehvahdjukaar.moonlight.api.entity;

import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;

public abstract class ImprovedProjectileEntity extends ThrowableItemProjectile {
   private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(ImprovedProjectileEntity.class, EntityDataSerializers.BYTE);
   protected Vec3 movementOld;
   protected boolean isStuck = false;
   protected int stuckTime = 0;
   protected int maxAge = 300;
   protected int maxStuckTime = 20;

   protected ImprovedProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level world) {
      super(type, world);
      this.movementOld = this.getDeltaMovement();
   }

   protected ImprovedProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level world) {
      this(type, world);
      this.setPos(x, y, z);
   }

   protected ImprovedProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity thrower, Level world) {
      this(type, thrower.getX(), thrower.getEyeY() - 0.10000000149011612, thrower.getZ(), world);
      this.setOwner(thrower);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ID_FLAGS, (byte)0);
   }

   public boolean hasLeftOwner() {
      return this.leftOwner;
   }

   public void tick() {
      this.noPhysics = this.isNoPhysics();
      if (!this.hasBeenShot) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
         this.hasBeenShot = true;
      }

      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      this.baseTick();
      if (this.hasReachedEndOfLife() && !this.isRemoved()) {
         this.reachedEndOfLife();
      }

      Level level = this.level();
      Vec3 movement = this.getDeltaMovement();
      this.movementOld = movement;
      if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
         movement = movement.multiply(this.stuckSpeedMultiplier);
         this.stuckSpeedMultiplier = Vec3.ZERO;
         this.setDeltaMovement(Vec3.ZERO);
      }

      if (!this.noPhysics && this.isStuck) {
         this.stuckTime++;
      } else {
         this.stuckTime = 0;
      }

      this.move(MoverType.SELF, movement);
      this.tryCheckInsideBlocks();
      this.updateFireState();
      float deceleration = this.isInWater() ? this.getWaterInertia() : this.getInertia();
      this.setDeltaMovement(this.getDeltaMovement().scale(deceleration));
      if (!this.isNoGravity() && !this.noPhysics) {
         this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, this.getGravity(), 0.0));
      }

      if (!this.isStuck) {
         if (level.isClientSide) {
            this.spawnTrailParticles();
         }

         this.updateRotation();
      }

      this.isStuck = !this.noPhysics && this.position().subtract(this.xo, this.yo, this.zo).lengthSqr() < 1.0E-8;
   }

   private void updateFireState() {
      this.wasOnFire = this.isOnFire();
      if (this.level().getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6)).noneMatch(arg -> arg.is(BlockTags.FIRE) || arg.is(Blocks.LAVA))) {
         if (this.getRemainingFireTicks() <= 0) {
            this.setRemainingFireTicks(-this.getFireImmuneTicks());
         }

         if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble() || ForgeHelper.isInFluidThatCanExtinguish(this))) {
            this.playEntityOnFireExtinguishedSound();
         }
      }

      if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble() || ForgeHelper.isInFluidThatCanExtinguish(this))) {
         this.setRemainingFireTicks(-this.getFireImmuneTicks());
      }
   }

   public void move(MoverType moverType, Vec3 movement) {
      if (moverType == MoverType.SELF && !this.noPhysics) {
         movement = this.maybeBackOffFromEdge(movement, moverType);
         Level level = this.level();
         Vec3 pos = this.position();
         ImprovedProjectileEntity.ColliderType colliderType = this.getColliderType();

         HitResult hitResult = switch (colliderType) {
            case RAY -> level.clip(new ClipContext(pos, pos.add(movement), Block.COLLIDER, Fluid.NONE, this));
            case AABB -> MthUtils.collideWithSweptAABB(this, movement, 2.0);
            case ENTITY_COLLIDE -> {
               Vec3 vec3 = this.collide(movement);
               Vec3 sub = vec3.subtract(movement);
               yield vec3 == movement
                  ? BlockHitResult.miss(pos.add(vec3), Direction.UP, BlockPos.containing(pos.add(vec3)))
                  : new BlockHitResult(pos.add(vec3), Direction.getNearest(sub.x, sub.y, sub.z), BlockPos.containing(pos.add(vec3)), false);
            }
         };
         Vec3 newPos = hitResult.getLocation();
         Vec3 newMovement = newPos.subtract(pos);
         this.setPos(newPos.x, newPos.y, newPos.z);
         boolean bl = !Mth.equal(newMovement.x, movement.x);
         boolean bl2 = !Mth.equal(newMovement.z, movement.z);
         this.horizontalCollision = bl || bl2;
         this.verticalCollision = newMovement.y != movement.y;
         this.verticalCollisionBelow = this.verticalCollision && newMovement.y < 0.0;
         if (this.horizontalCollision) {
            this.minorHorizontalCollision = this.isHorizontalCollisionMinor(newMovement);
         } else {
            this.minorHorizontalCollision = false;
         }

         EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
            level, this, pos, newPos, this.getBoundingBox().expandTowards(newPos.subtract(pos)).inflate(1.0), x$0 -> this.canHitEntity(x$0)
         );
         if (entityHitResult != null) {
            hitResult = entityHitResult;
         }

         boolean portalHit = false;
         if (hitResult instanceof EntityHitResult ei) {
            Entity hitEntity = ei.getEntity();
            if (hitEntity == this.getOwner()) {
               if (!this.canHarmOwner()) {
                  hitResult = null;
               }
            } else if (hitEntity instanceof Player p1 && this.getOwner() instanceof Player p2 && !p2.canHarmPlayer(p1)) {
               hitResult = null;
            }
         } else if (hitResult instanceof BlockHitResult bi) {
            BlockPos hitPos = bi.getBlockPos();
            BlockState hitState = level.getBlockState(hitPos);
            if (!hitState.is(Blocks.NETHER_PORTAL) && hitState.is(Blocks.END_GATEWAY)) {
            }
         }

         if (!portalHit && hitResult != null && hitResult.getType() != Type.MISS && !ForgeHelper.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
         }
      } else {
         super.move(moverType, movement);
      }
   }

   public boolean canHarmOwner() {
      return this.getOwner() instanceof Player ? this.level().getDifficulty().getId() >= 1 : false;
   }

   protected float getInertia() {
      return 0.99F;
   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public boolean hasReachedEndOfLife() {
      return this.tickCount > this.maxAge || this.stuckTime > this.maxStuckTime;
   }

   public void reachedEndOfLife() {
      this.remove(RemovalReason.DISCARDED);
   }

   public void spawnTrailParticles() {
      if (this.isInWater()) {
         Vec3 movement = this.getDeltaMovement();
         double velX = movement.x;
         double velY = movement.y;
         double velZ = movement.z;

         for (int j = 0; j < 4; j++) {
            double pY = this.getEyeY();
            this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - velX * 0.25, pY - velY * 0.25, this.getZ() - velZ * 0.25, velX, velY, velZ);
         }
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("stuck", this.isStuck);
      tag.putInt("stuckTime", this.stuckTime);
      tag.putBoolean("noPhysics", this.isNoPhysics());
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.isStuck = tag.getBoolean("stuck");
      this.stuckTime = tag.getInt("stuckTime");
      this.setNoPhysics(tag.getBoolean("noPhysics"));
   }

   public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
      super.shootFromRotation(shooter, x, y, z, velocity, inaccuracy);
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      super.shoot(x, y, z, velocity, inaccuracy);
   }

   public float getDefaultShootVelocity() {
      return 1.5F;
   }

   protected void setFlag(int id, boolean value) {
      byte b0 = (Byte)this.entityData.get(ID_FLAGS);
      if (value) {
         this.entityData.set(ID_FLAGS, (byte)(b0 | id));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(b0 & ~id));
      }
   }

   protected boolean getFlag(int id) {
      return ((Byte)this.entityData.get(ID_FLAGS) & id) != 0;
   }

   public void setNoPhysics(boolean noPhysics) {
      this.noPhysics = noPhysics;
      this.setFlag(2, noPhysics);
   }

   public boolean isNoPhysics() {
      return this.getFlag(2);
   }

   protected ImprovedProjectileEntity.ColliderType getColliderType() {
      return ImprovedProjectileEntity.ColliderType.AABB;
   }

   protected static enum ColliderType {
      RAY,
      AABB,
      ENTITY_COLLIDE;
   }
}
