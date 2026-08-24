package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityFrilledShark extends WaterAnimal implements IAnimatedEntity, Bucketable {
   public static final Animation ANIMATION_ATTACK = Animation.create(17);
   private static final EntityDataAccessor<Boolean> DEPRESSURIZED = SynchedEntityData.defineId(EntityFrilledShark.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityFrilledShark.class, EntityDataSerializers.BOOLEAN);
   public float prevOnLandProgress;
   public float onLandProgress;
   private int animationTick;
   private Animation currentAnimation;

   protected EntityFrilledShark(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new AquaticMoveController(this, 1.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DEPRESSURIZED, false);
      builder.define(FROM_BUCKET, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new EntityFrilledShark.AIMelee());
      this.goalSelector.addGoal(3, new AnimalAISwimBottom(this, 0.800000011920929, 7));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.800000011920929, 3));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(6, new FollowBoatGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Squid.class, 40, false, true, null));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 70, false, true, null));
      this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 100, false, true, null));
      this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, EntityBlobfish.class, 70, false, true, null));
      this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, Drowned.class, 4, false, true, null));
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.frilledSharkSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canFrilledSharkSpawn(
      EntityType<EntityFrilledShark> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.isWaterAt(pos) && iServerWorld.isWaterAt(pos.above());
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean p_203706_1_) {
      this.entityData.set(FROM_BUCKET, p_203706_1_);
   }

   @Nonnull
   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putBoolean("Depressurized", this.isDepressurized());
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.setDepressurized(AMCompat.getBoolean(compound, "Depressurized"));
   }

   private void doInitialPosing(LevelAccessor world) {
      BlockPos down = this.blockPosition();

      while (!world.getFluidState(down).isEmpty() && down.getY() > 1) {
         down = down.below();
      }

      this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (reason == MobSpawnType.NATURAL) {
         this.doInitialPosing(worldIn);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public boolean isDepressurized() {
      return (Boolean)this.entityData.get(DEPRESSURIZED);
   }

   public void setDepressurized(boolean depressurized) {
      this.entityData.set(DEPRESSURIZED, depressurized);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.COD_HURT;
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.FRILLED_SHARK_BUCKET.get());
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      CompoundTag platTag = new CompoundTag();
      AMCompat.saveAdditionalTo(this, platTag);
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      AMCompat.put(compound, "FrilledSharkData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "FrilledSharkData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "FrilledSharkData"));
      }
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.6, 0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   public void tick() {
      super.tick();
      this.prevOnLandProgress = this.onLandProgress;
      if (!this.isInWater() && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (this.isInWater() && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (this.isInWater()) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.8, 1.0));
      }

      boolean clear = this.hasClearance();
      if (this.isDepressurized() && clear) {
         this.setDepressurized(false);
      }

      if (!this.isDepressurized() && !clear) {
         this.setDepressurized(true);
      }

      LivingEntity target = this.getTarget();
      if (!this.level().isClientSide() && target != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 12) {
         float f1 = this.getYRot() * 0.017453292F;
         this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.06F, 0.0, Mth.cos(f1) * 0.06F));
         if (AMCompat.hurt(target, this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue())) {
            target.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.EXSANGUINATION.get()), 60, 2));
            if (this.random.nextInt(15) == 0 && target instanceof Squid) {
               AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.SERRATED_SHARK_TOOTH.get());
            }
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.getEntity() instanceof Drowned) {
         amount *= 0.5F;
      }

      return super.hurt(source, amount);
   }

   private boolean hasClearance() {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int l1 = 0; l1 < 10; l1++) {
         BlockState blockstate = this.level().getBlockState(blockpos$mutable.set(this.getX(), this.getY() + l1, this.getZ()));
         if (!blockstate.getFluidState().is(FluidTags.WATER)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public boolean isKaiju() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && (s.toLowerCase().contains("kamata kun") || s.toLowerCase().contains("kamata-kun"));
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK};
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 68) {
         double d2 = this.random.nextGaussian() * 0.1;
         double d0 = this.random.nextGaussian() * 0.1;
         double d1 = this.random.nextGaussian() * 0.1;
         float radius = this.getBbWidth() * 0.8F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         double x = this.getX() + extraX + d0;
         double y = this.getY() + this.getBbHeight() * 0.15F + d1;
         double z = this.getZ() + extraZ + d2;
         this.level()
            .addParticle(
               (ParticleOptions)AMParticleRegistry.TEETH_GLINT.get(), x, y, z, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z
            );
      } else {
         super.handleEntityEvent(id);
      }
   }

   private class AIMelee extends Goal {
      public AIMelee() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return EntityFrilledShark.this.getTarget() != null && EntityFrilledShark.this.getTarget().isAlive();
      }

      public void tick() {
         LivingEntity target = EntityFrilledShark.this.getTarget();
         double speed = 1.0;
         if (EntityFrilledShark.this.distanceTo(target) < 10.0F) {
            if (EntityFrilledShark.this.distanceTo(target) < 1.9) {
               AMCompat.doHurtTarget(EntityFrilledShark.this, target);
               speed = 0.800000011920929;
            } else {
               speed = 0.6000000238418579;
               EntityFrilledShark.this.lookAt(target, 70.0F, 70.0F);
               if (target instanceof Squid) {
                  Vec3 mouth = EntityFrilledShark.this.position();
                  float squidSpeed = 0.07F;
                  ((Squid)target)
                     .setMovementVector(
                        (float)(mouth.x - target.getX()) * squidSpeed,
                        (float)(mouth.y - target.getEyeY()) * squidSpeed,
                        (float)(mouth.z - target.getZ()) * squidSpeed
                     );
                  EntityFrilledShark.this.level().broadcastEntityEvent(EntityFrilledShark.this, (byte)68);
               }
            }
         }

         if (target instanceof Drowned || target instanceof Player) {
            speed = 1.0;
         }

         EntityFrilledShark.this.getNavigation().moveTo(target, speed);
      }
   }
}
