package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class EntityRattlesnake extends Animal implements IAnimatedEntity {
   public float prevCurlProgress;
   public float curlProgress;
   public int randomToungeTick = 0;
   public int maxCurlTime = 75;
   private int curlTime = 0;
   private static final EntityDataAccessor<Boolean> RATTLING = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CURLED = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.BOOLEAN);
   private static final Predicate<LivingEntity> WARNABLE_PREDICATE = mob -> mob instanceof Player && !((Player)mob).isCreative() && !mob.isSpectator()
      || mob instanceof EntityRoadrunner;
   private static final Predicate<LivingEntity> TARGETABLE_PREDICATE = mob -> mob instanceof Player && !((Player)mob).isCreative() && !mob.isSpectator()
      || mob instanceof EntityRoadrunner;
   private int animationTick;
   private Animation currentAnimation;
   public static final Animation ANIMATION_BITE = Animation.create(20);
   private int loopSoundTick = 0;

   protected EntityRattlesnake(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(2, new EntityRattlesnake.WarnPredatorsGoal());
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 60, 1.0, 7, 7));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Rabbit.class, 15, true, true, null));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, EntityJerboa.class, 15, true, true, null));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(4, new EntityRattlesnake.ShortDistanceTarget());
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.RATTLESNAKE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.RATTLESNAKE_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.rattlesnakeSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean doHurtTarget(Entity entityIn) {
      this.setAnimation(ANIMATION_BITE);
      return true;
   }

   public boolean canBeAffected(MobEffectInstance potioneffectIn) {
      return potioneffectIn.getEffect() == MobEffects.POISON ? false : super.canBeAffected(potioneffectIn);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CURLED, false);
      builder.define(RATTLING, false);
   }

   public boolean isCurled() {
      return (Boolean)this.entityData.get(CURLED);
   }

   public void setCurled(boolean curled) {
      this.entityData.set(CURLED, curled);
   }

   public boolean isRattling() {
      return (Boolean)this.entityData.get(RATTLING);
   }

   public void setRattling(boolean rattling) {
      this.entityData.set(RATTLING, rattling);
   }

   public void tick() {
      super.tick();
      this.prevCurlProgress = this.curlProgress;
      if (this.isCurled()) {
         if (this.curlProgress < 5.0F) {
            this.curlProgress += 0.5F;
         }
      } else if (this.curlProgress > 0.0F) {
         this.curlProgress--;
      }

      if (this.randomToungeTick == 0 && this.random.nextInt(15) == 0) {
         this.randomToungeTick = 10 + this.random.nextInt(20);
      }

      if (this.randomToungeTick > 0) {
         this.randomToungeTick--;
      }

      if (this.isCurled() && !this.isRattling() && ++this.curlTime > this.maxCurlTime) {
         this.setCurled(false);
         this.curlTime = 0;
         this.maxCurlTime = 75 + this.random.nextInt(50);
      }

      LivingEntity target = this.getTarget();
      if (!this.level().isClientSide()) {
         if (this.isCurled() && target != null && target.isAlive()) {
            this.setCurled(false);
         }

         if (this.isRattling() && target == null) {
            this.setCurled(true);
         }

         if (!this.isCurled() && this.getTarget() == null && this.random.nextInt(500) == 0) {
            this.maxCurlTime = 300 + this.random.nextInt(250);
            this.setCurled(true);
         }
      }

      if (this.getAnimation() == ANIMATION_BITE) {
         if (this.getAnimationTick() == 4) {
            this.playSound(AMSoundRegistry.RATTLESNAKE_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
         }

         if (this.getAnimationTick() == 8 && target != null && this.distanceTo(target) < 2.0) {
            boolean meepMeep = target instanceof EntityRoadrunner;
            int f = this.isBaby() ? 2 : 1;
            target.hurt(this.damageSources().mobAttack(this), meepMeep ? 1.0F : f * (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            if (!meepMeep) {
               target.addEffect(new MobEffectInstance(MobEffects.POISON, 300, f * 2));
            }
         }
      }

      if (this.isRattling()) {
         if (this.loopSoundTick == 0) {
            this.gameEvent(AMPlatform.ENTITY_ACTION);
            this.playSound(AMSoundRegistry.RATTLESNAKE_LOOP.get(), this.getSoundVolume() * 0.5F, this.getVoicePitch());
         }

         this.loopSoundTick++;
         if (this.loopSoundTick > 50) {
            this.loopSoundTick = 0;
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void travel(Vec3 vec3d) {
      if (this.onGround() && this.isCurled()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2800000011920929);
   }

   public boolean isFood(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) && AMCompat.getFoodProperties(stack.getItem()) != null && AMCompat.isMeat(stack.getItem());
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.RATTLESNAKE.get(), p_241840_1_);
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
      return new Animation[]{ANIMATION_BITE};
   }

   public static boolean canRattlesnakeSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.RATTLESNAKE_SPAWNS);
      return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
   }

   class ShortDistanceTarget extends NearestAttackableTargetGoal<Player> {
      public ShortDistanceTarget() {
         super(EntityRattlesnake.this, Player.class, 3, true, true, AMCompat.selector(EntityRattlesnake.TARGETABLE_PREDICATE));
      }

      public boolean canUse() {
         return EntityRattlesnake.this.isBaby() ? false : super.canUse();
      }

      public void start() {
         super.start();
         EntityRattlesnake.this.setCurled(false);
         EntityRattlesnake.this.setRattling(true);
      }

      protected double getFollowDistance() {
         return 2.0;
      }
   }

   class WarnPredatorsGoal extends Goal {
      int executionChance = 20;
      Entity target = null;

      public boolean canUse() {
         if (EntityRattlesnake.this.getRandom().nextInt(this.executionChance) == 0) {
            double dist = 5.0;
            List<LivingEntity> list = EntityRattlesnake.this.level()
               .getEntitiesOfClass(LivingEntity.class, EntityRattlesnake.this.getBoundingBox().inflate(5.0, 5.0, 5.0), EntityRattlesnake.WARNABLE_PREDICATE);
            double d0 = 1.7976931348623157E308;
            Entity possibleTarget = null;

            for (Entity entity : list) {
               double d1 = EntityRattlesnake.this.distanceToSqr(entity);
               if (!(d1 > d0)) {
                  d0 = d1;
                  possibleTarget = entity;
               }
            }

            this.target = possibleTarget;
            return !list.isEmpty();
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null && EntityRattlesnake.this.distanceTo(this.target) < 5.0 && EntityRattlesnake.this.getTarget() == null;
      }

      public void stop() {
         this.target = null;
         EntityRattlesnake.this.setRattling(false);
      }

      public void tick() {
         EntityRattlesnake.this.setRattling(true);
         EntityRattlesnake.this.setCurled(true);
         EntityRattlesnake.this.curlTime = 0;
         EntityRattlesnake.this.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
      }
   }
}
