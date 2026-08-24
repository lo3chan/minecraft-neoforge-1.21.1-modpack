package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.JerboaAIBeg;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityJerboa extends Animal {
   private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.defineId(EntityJerboa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.defineId(EntityJerboa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityJerboa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BEFRIENDED = SynchedEntityData.defineId(EntityJerboa.class, EntityDataSerializers.BOOLEAN);
   public float jumpProgress;
   public float prevJumpProgress;
   public float reboundProgress;
   public float prevReboundProgress;
   public float begProgress;
   public float prevBegProgress;
   public float sleepProgress;
   public float prevSleepProgress;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;

   protected EntityJerboa(EntityType<? extends Animal> jerboa, Level lvl) {
      super(jerboa, lvl);
      this.moveControl = new EntityJerboa.MoveHelperController(this);
      this.jumpControl = new EntityJerboa.JumpHelperController(this);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.44999998807907104);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(JUMP_ACTIVE, false);
      builder.define(BEGGING, false);
      builder.define(SLEEPING, false);
      builder.define(BEFRIENDED, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new JerboaAIBeg(this, 1.0));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Player.class, 5.0F, 1.3, 1.0) {
         public boolean canUse() {
            return !EntityJerboa.this.isBefriended() && super.canUse();
         }
      });
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, Cat.class, 9.0F, 1.3, 1.0));
      this.goalSelector.addGoal(5, new AvoidEntityGoal(this, Ocelot.class, 9.0F, 1.3, 1.0));
      this.goalSelector.addGoal(6, new AvoidEntityGoal(this, EntityRattlesnake.class, 9.0F, 1.3, 1.0));
      this.goalSelector.addGoal(7, new PanicGoal(this, 1.1));
      this.goalSelector.addGoal(8, new AnimalAIWanderRanged(this, 20, 1.0, 10, 7));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setBefriended(AMCompat.getBoolean(compound, "Befriended"));
      this.setSleeping(AMCompat.getBoolean(compound, "Sleeping"));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Befriended", this.isBefriended());
      compound.putBoolean("Sleeping", this.isSleeping());
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.requiresCustomPersistence();
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.isBefriended();
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.JERBOA_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.JERBOA_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.JERBOA_HURT.get();
   }

   public void tick() {
      super.tick();
      this.prevJumpProgress = this.jumpProgress;
      this.prevReboundProgress = this.reboundProgress;
      this.prevSleepProgress = this.sleepProgress;
      this.prevBegProgress = this.begProgress;
      if (!this.level().isClientSide()) {
         this.entityData.set(JUMP_ACTIVE, !this.onGround());
      }

      if ((Boolean)this.entityData.get(JUMP_ACTIVE)) {
         if (this.jumpProgress < 5.0F) {
            this.jumpProgress++;
            if (this.reboundProgress > 0.0F) {
               this.reboundProgress--;
            }
         }

         if (this.jumpProgress >= 5.0F && this.reboundProgress < 5.0F) {
            this.reboundProgress++;
         }
      } else {
         if (this.reboundProgress > 0.0F) {
            this.reboundProgress = Math.max(this.reboundProgress - 1.0F, 0.0F);
         }

         if (this.jumpProgress > 0.0F) {
            this.jumpProgress = Math.max(this.jumpProgress - 1.0F, 0.0F);
         }
      }

      if (this.isBegging()) {
         if (this.begProgress < 5.0F) {
            this.begProgress++;
         }
      } else if (this.begProgress > 0.0F) {
         this.begProgress--;
      }

      if (this.isSleeping()) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress++;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.level().isDay() && this.getLastHurtByMob() == null && !this.isBegging()) {
            if (this.tickCount % 10 == 0 && this.getRandom().nextInt(750) == 0) {
               this.setSleeping(true);
            }
         } else if (this.isSleeping()) {
            this.setSleeping(false);
         }
      }
   }

   public boolean isBegging() {
      return (Boolean)this.entityData.get(BEGGING);
   }

   public void setBegging(boolean begging) {
      this.entityData.set(BEGGING, begging);
   }

   public boolean isSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
   }

   public boolean isBefriended() {
      return (Boolean)this.entityData.get(BEFRIENDED);
   }

   public void setBefriended(boolean befriended) {
      this.entityData.set(BEFRIENDED, befriended);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (!itemstack.is(AMTagRegistry.JERBOA_BEGS_FOR) && !this.isFood(itemstack) || !(this.getHealth() < this.getMaxHealth()) && this.isBefriended()) {
         InteractionResult type = super.mobInteract(player, hand);
         if (type != InteractionResult.SUCCESS && !this.isFood(itemstack) && itemstack.is(AMTagRegistry.JERBOA_BEGS_FOR)) {
            this.setSleeping(false);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.PARROT_EAT, this.getVoicePitch(), this.getSoundVolume());

            for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, itemstack),
                     this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                     this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            if (this.random.nextFloat() <= 0.3F) {
               player.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.FLEET_FOOTED.get()), 12000));
            }

            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      } else {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setBefriended(true);
         this.heal(4.0F);
         return InteractionResult.SUCCESS;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         this.setSleeping(false);
         if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            LivingEntity hurter = (LivingEntity)source.getEntity();
            if (hurter.hasEffect(AMCompat.effect(AMEffectRegistry.FLEET_FOOTED.get()))) {
               hurter.removeEffect(AMCompat.effect(AMEffectRegistry.FLEET_FOOTED.get()));
            }
         }

         return prev;
      } else {
         return prev;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.JERBOA_BREEDABLES);
   }

   public boolean shouldMove() {
      return !this.isSleeping();
   }

   public float getJumpCompletion(float partialTicks) {
      return this.jumpDuration == 0 ? 0.0F : (this.jumpTicks + partialTicks) / this.jumpDuration;
   }

   protected float getJumpPower() {
      return this.horizontalCollision ? super.getJumpPower() + 0.2F : 0.25F + this.random.nextFloat() * 0.15F;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public static boolean isValidLightLevel(ServerLevelAccessor p_223323_0_, BlockPos p_223323_1_, RandomSource p_223323_2_) {
      int light = p_223323_0_.getMaxLocalRawBrightness(p_223323_1_);
      return light <= 4;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.jerboaSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canMonsterSpawnInLight(
      EntityType<? extends EntityJerboa> p_223325_0_, ServerLevelAccessor p_223325_1_, MobSpawnType p_223325_2_, BlockPos p_223325_3_, RandomSource p_223325_4_
   ) {
      return isValidLightLevel(p_223325_1_, p_223325_3_, p_223325_4_) && checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
   }

   public static <T extends Mob> boolean canJerboaSpawn(
      EntityType<EntityJerboa> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.canSeeSky(pos.above()) && canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0) {
         double d1 = this.getDeltaMovement().horizontalDistance();
         if (d1 < 0.01) {
         }
      }

      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)1);
      }
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigation().setSpeedModifier(newSpeed);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), newSpeed);
   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   private void calculateRotationYaw(double x, double z) {
      this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * 57.2957763671875) - 90.0F);
   }

   private void enableJumpControl() {
      if (this.jumpControl instanceof EntityJerboa.JumpHelperController) {
         ((EntityJerboa.JumpHelperController)this.jumpControl).setCanJump(true);
      }
   }

   private void disableJumpControl() {
      if (this.jumpControl instanceof EntityJerboa.JumpHelperController) {
         ((EntityJerboa.JumpHelperController)this.jumpControl).setCanJump(false);
      }
   }

   private void updateMoveTypeDuration() {
      if (this.moveControl.getSpeedModifier() < 2.2) {
         this.currentMoveTypeDuration = 2;
      } else {
         this.currentMoveTypeDuration = 1;
      }
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      if (this.currentMoveTypeDuration > 0) {
         this.currentMoveTypeDuration--;
      }

      if (this.onGround() && this.shouldMove()) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.currentMoveTypeDuration == 0) {
            LivingEntity livingentity = this.getTarget();
            if (livingentity != null && this.distanceToSqr(livingentity) < 16.0) {
               this.calculateRotationYaw(livingentity.getX(), livingentity.getZ());
               this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         if (this.jumpControl instanceof EntityJerboa.JumpHelperController rabbitController) {
            if (!rabbitController.getIsJumping()) {
               if (this.moveControl.hasWanted() && this.currentMoveTypeDuration == 0) {
                  Path path = this.navigation.getPath();
                  Vec3 vector3d = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                  if (path != null && !path.isDone()) {
                     vector3d = path.getNextEntityPos(this);
                  }

                  this.calculateRotationYaw(vector3d.x, vector3d.z);
                  this.startJumping();
               }
            } else if (!rabbitController.canJump()) {
               this.enableJumpControl();
            }
         }
      } else if (!this.shouldMove()) {
         this.setJumping(false);
         this.checkLandingDelay();
      }

      this.wasOnGround = this.onGround();
   }

   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         this.jumpTicks++;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
      EntityJerboa boa = AMCompat.create(AMEntityRegistry.JERBOA.get(), p_146743_);
      boa.setBefriended(true);
      return boa;
   }

   public boolean hasJumper() {
      return this.jumpControl instanceof EntityJerboa.JumpHelperController;
   }

   public static class JumpHelperController extends JumpControl {
      private final EntityJerboa jerboa;
      private boolean canJump;

      public JumpHelperController(EntityJerboa jerboa) {
         super(jerboa);
         this.jerboa = jerboa;
      }

      public boolean getIsJumping() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJumpIn) {
         this.canJump = canJumpIn;
      }

      public void tick() {
         if (this.jump) {
            this.jerboa.startJumping();
            this.jump = false;
         }
      }
   }

   static class MoveHelperController extends MoveControl {
      private final EntityJerboa jerboa;
      private double nextJumpSpeed;

      public MoveHelperController(EntityJerboa jerboa) {
         super(jerboa);
         this.jerboa = jerboa;
      }

      public void tick() {
         if (this.jerboa.hasJumper()
            && this.jerboa.onGround()
            && !this.jerboa.jumping
            && !((EntityJerboa.JumpHelperController)this.jerboa.jumpControl).getIsJumping()) {
            this.jerboa.setMovementSpeed(0.0);
         } else if (this.hasWanted()) {
            this.jerboa.setMovementSpeed(this.nextJumpSpeed);
         }

         if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            Vec3 vector3d = new Vec3(this.wantedX - this.jerboa.getX(), this.wantedY - this.jerboa.getY(), this.wantedZ - this.jerboa.getZ());
            double d0 = vector3d.length();
            this.jerboa.setDeltaMovement(this.jerboa.getDeltaMovement().add(vector3d.scale(this.speedModifier * 1.0 * 0.05 / d0)));
         }

         super.tick();
      }

      public void setWantedPosition(double x, double y, double z, double speedIn) {
         if (this.jerboa.isInWater()) {
            speedIn = 1.5;
         }

         super.setWantedPosition(x, y, z, speedIn);
         if (speedIn > 0.0) {
            this.nextJumpSpeed = speedIn;
         }
      }
   }
}
