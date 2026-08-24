package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.GeladaAIGroom;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntityGeladaMonkey extends Animal implements IAnimatedEntity, IHerdPanic {
   public static final Animation ANIMATION_SWIPE_R = Animation.create(13);
   public static final Animation ANIMATION_SWIPE_L = Animation.create(13);
   public static final Animation ANIMATION_GROOM = Animation.create(35);
   public static final Animation ANIMATION_CHEST = Animation.create(35);
   private static final EntityDataAccessor<Boolean> LEADER = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> GRASS_TIME = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.INT);
   public float prevSitProgress;
   public float sitProgress;
   public boolean isGrooming = false;
   public int groomerID = -1;
   private int animationTick;
   private Animation currentAnimation;
   private int sittingTime;
   private int maxSitTime;
   private int leaderFightTime;
   private HurtByTargetGoal hurtByTargetGoal = null;
   private NearestAttackableTargetGoal<EntityGeladaMonkey> leaderFightGoal = null;
   private int revengeCooldown = 0;
   private boolean hasSpedUp = false;

   protected EntityGeladaMonkey(EntityType type, Level lvl) {
      super(type, lvl);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.geladaMonkeySpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 18.0).add(Attributes.ATTACK_DAMAGE, 4.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public int getMaxSpawnClusterSize() {
      return 10;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.GELADA_MONKEY_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.GELADA_MONKEY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.GELADA_MONKEY_HURT.get();
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            1,
            new MeleeAttackGoal(this, 1.5, true) {
               protected double getAttackReachSqr(LivingEntity attackTarget) {
                  return AMPlatform.attackReachSqr(this.mob, attackTarget) + 1.5;
               }

               protected boolean canPerformAttack(LivingEntity target) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ()) <= this.getAttackReachSqr(target)
                     && this.mob.getSensing().hasLineOfSight(target);
               }

               public boolean canUse() {
                  return super.canUse() && EntityGeladaMonkey.this.revengeCooldown <= 0;
               }

               public boolean canContinueToUse() {
                  return super.canContinueToUse() && EntityGeladaMonkey.this.revengeCooldown <= 0;
               }
            }
         );
      this.goalSelector.addGoal(2, new EntityGeladaMonkey.AIClearGrass());
      this.goalSelector.addGoal(3, new AnimalAIHerdPanic(this, 1.5));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(
            6,
            new TemptGoal(this, 1.0, AMCompat.ingredientOfTags(AMTagRegistry.GELADA_MONKEY_BREEDABLES, AMTagRegistry.GELADA_MONKEY_LAND_CLEARING_FOODS), false)
         );
      this.goalSelector.addGoal(7, new GeladaAIGroom(this));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0, 120));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, this.hurtByTargetGoal = new HurtByTargetGoal(this, new Class[]{EntityGeladaMonkey.class}).setAlertOthers(new Class[0]));
      this.targetSelector
         .addGoal(
            2,
            this.leaderFightGoal = new NearestAttackableTargetGoal(
               this,
               EntityGeladaMonkey.class,
               70,
               false,
               false,
               AMCompat.selector(
                  monkey -> this.isLeader()
                     && this.leaderFightTime == 0
                     && ((EntityGeladaMonkey)monkey).isLeader()
                     && ((EntityGeladaMonkey)monkey).leaderFightTime == 0
               )
            )
         );
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Leader", this.isLeader());
      compound.putInt("GrassTime", this.getClearGrassTime());
      compound.putInt("FightTime", this.leaderFightTime);
      compound.putBoolean("MonkeySitting", this.isSitting());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setLeader(AMCompat.getBoolean(compound, "Leader"));
      this.setClearGrassTime(AMCompat.getInt(compound, "GrassTime"));
      this.setSitting(AMCompat.getBoolean(compound, "MonkeySitting"));
      this.leaderFightTime = AMCompat.getInt(compound, "FightTime");
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.GELADA_MONKEY_BREEDABLES);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LEADER, false);
      builder.define(SITTING, false);
      builder.define(HAS_TARGET, false);
      builder.define(GRASS_TIME, 0);
   }

   public boolean isLeader() {
      return (Boolean)this.entityData.get(LEADER) && !this.isBaby();
   }

   public void setLeader(boolean leader) {
      this.entityData.set(LEADER, leader);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setSitting(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isAggro() {
      return (Boolean)this.entityData.get(HAS_TARGET);
   }

   public void setAggro(boolean sit) {
      this.entityData.set(HAS_TARGET, sit);
   }

   public int getClearGrassTime() {
      return (Integer)this.entityData.get(GRASS_TIME);
   }

   public void setClearGrassTime(int i) {
      this.entityData.set(GRASS_TIME, i);
   }

   public void tick() {
      super.tick();
      this.prevSitProgress = this.sitProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isSitting() && ++this.sittingTime > this.maxSitTime) {
            this.setSitting(false);
            this.sittingTime = 0;
            this.maxSitTime = 75 + this.random.nextInt(50);
         }

         if (this.getDeltaMovement().lengthSqr() < 0.03 && this.getAnimation() == NO_ANIMATION && !this.isSitting() && this.random.nextInt(500) == 0) {
            this.sittingTime = 0;
            this.maxSitTime = 200 + this.random.nextInt(550);
            this.setSitting(true);
         }

         if (this.isSitting() && (this.getTarget() != null || this.isInLove())) {
            this.setSitting(false);
         }

         if (this.getTarget() != null
            && (this.getAnimation() == ANIMATION_SWIPE_L || this.getAnimation() == ANIMATION_SWIPE_R)
            && this.getAnimationTick() == 7
            && this.hasLineOfSight(this.getTarget())
            && this.distanceTo(this.getTarget()) < this.getBbHeight() + this.getTarget().getBbHeight() + 1.0F) {
            AMCompat.knockback(this.getTarget(), 0.4000000059604645, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
            float dmg = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            if (this.isLeader() && this.getTarget() instanceof EntityGeladaMonkey monkey && monkey.isLeader()) {
               monkey.setTarget(this);
               monkey.leaderFightTime = this.leaderFightTime;
               dmg = 0.0F;
            }

            this.getTarget().hurt(this.damageSources().mobAttack(this), dmg);
         }

         if (this.getTarget() != null && this.getTarget().isAlive()) {
            this.setAggro(true);
            if (this.isLeader() && this.getTarget() instanceof EntityGeladaMonkey monkey) {
               if (monkey.isLeader()) {
                  this.leaderFightTime++;
               }

               if (this.leaderFightTime < 10 && this.random.nextInt(5) == 0 && this.getAnimation() == NO_ANIMATION) {
                  this.setAnimation(ANIMATION_CHEST);
               }

               if (Math.max(this.leaderFightTime, monkey.leaderFightTime) >= 250) {
                  this.resetAttackAI();
                  monkey.resetAttackAI();
               }
            }
         } else {
            this.setAggro(false);
         }

         if (this.leaderFightTime < 0) {
            this.leaderFightTime++;
         }
      }

      if (this.isAggro()) {
         if (!this.hasSpedUp) {
            this.hasSpedUp = true;
            this.setSprinting(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3100000023841858);
         }
      } else if (this.hasSpedUp) {
         this.hasSpedUp = false;
         this.setSprinting(false);
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
      }

      if (this.getClearGrassTime() > 0) {
         this.setClearGrassTime(this.getClearGrassTime() - 1);
      }

      if (this.getClearGrassTime() < 0) {
         this.setClearGrassTime(this.getClearGrassTime() + 1);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void resetAttackAI() {
      this.leaderFightTime = -500 - this.random.nextInt(2000);
      this.setTarget(null);
      this.setLastHurtByMob(null);
      if (this.leaderFightGoal != null) {
         this.leaderFightGoal.stop();
      }

      if (this.hurtByTargetGoal != null) {
         this.hurtByTargetGoal.stop();
      }
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.attackAnimation();
      }

      return true;
   }

   public float getGeladaScale() {
      return this.isBaby() ? 0.5F : (this.isLeader() ? 1.15F : 1.0F);
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_SWIPE_R, ANIMATION_SWIPE_L, ANIMATION_GROOM, ANIMATION_CHEST};
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         Entity direct = source.getEntity();
         if (direct instanceof EntityGeladaMonkey) {
            int fleeTime = 100 + this.getRandom().nextInt(5);
            this.revengeCooldown = fleeTime;
            this.revengeCooldown = 10 + this.getRandom().nextInt(30);
         }
      }

      return prev;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (itemstack.is(AMTagRegistry.GELADA_MONKEY_LAND_CLEARING_FOODS) && this.getClearGrassTime() == 0) {
         this.usePlayerItem(player, hand, itemstack);
         this.eatGrassWithBuddies(3 + this.random.nextInt(2));
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel lvl, AgeableMob mob) {
      EntityGeladaMonkey baby = AMCompat.create(AMEntityRegistry.GELADA_MONKEY.get(), lvl);
      baby.setLeader(this.random.nextInt(2) == 0);
      return baby;
   }

   public void eatGrassWithBuddies(int otherMonkies) {
      int i = 300 + this.random.nextInt(300);
      this.setClearGrassTime(i);
      int monky = 0;

      for (EntityGeladaMonkey entity : this.level().getEntitiesOfClass(EntityGeladaMonkey.class, this.getBoundingBox().inflate(15.0))) {
         if (monky < otherMonkies && entity.getId() != this.getId() && !entity.shouldStopBeingGroomed()) {
            monky++;
            entity.setClearGrassTime(i);
         }
      }
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return this.getLastHurtByMob() instanceof EntityGeladaMonkey && this.random.nextInt(3) == 0;
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting() || this.getAnimation() == ANIMATION_CHEST) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   @javax.annotation.Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn
   ) {
      if (spawnDataIn instanceof AgeableMobGroupData pack) {
         if (pack.getGroupSize() == 0 || pack.getGroupSize() > 4 && this.random.nextInt(2) == 0) {
            this.setLeader(true);
         }
      } else {
         this.setLeader(this.getRandom().nextInt(4) == 0);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public boolean canBeGroomed() {
      return this.groomerID == -1;
   }

   public boolean shouldStopBeingGroomed() {
      return this.getTarget() != null && this.getTarget().isAlive() || this.isInLove() || this.revengeCooldown > 0;
   }

   private void attackAnimation() {
      this.setAnimation(this.random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
   }

   private class AIClearGrass extends Goal {
      private BlockPos target;

      public AIClearGrass() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (EntityGeladaMonkey.this.getClearGrassTime() > 0) {
            this.target = this.generateTarget();
            return this.target != null;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null && EntityGeladaMonkey.this.level().getBlockState(this.target).is(AMTagRegistry.GELADA_MONKEY_GRASS);
      }

      public void tick() {
         EntityGeladaMonkey.this.setSitting(false);
         EntityGeladaMonkey.this.getNavigation().moveTo(this.target.getX() + 0.5F, this.target.getY() + 0.5F, this.target.getZ() + 0.5F, 1.399999976158142);
         if (EntityGeladaMonkey.this.distanceToSqr(Vec3.atCenterOf(this.target)) < 3.4000000953674316) {
            if (EntityGeladaMonkey.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               EntityGeladaMonkey.this.attackAnimation();
            } else if (EntityGeladaMonkey.this.getAnimationTick() > 7) {
               EntityGeladaMonkey.this.level().destroyBlock(this.target, true);
            }
         }
      }

      public BlockPos generateTarget() {
         BlockPos blockpos = null;
         Random random = new Random();
         int range = 7;

         for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = EntityGeladaMonkey.this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

            while (EntityGeladaMonkey.this.level().isEmptyBlock(blockpos1) && blockpos1.getY() > -63) {
               blockpos1 = blockpos1.below();
            }

            if (EntityGeladaMonkey.this.level().getBlockState(blockpos1).is(AMTagRegistry.GELADA_MONKEY_GRASS)) {
               blockpos = blockpos1;
            }
         }

         return blockpos;
      }
   }
}
