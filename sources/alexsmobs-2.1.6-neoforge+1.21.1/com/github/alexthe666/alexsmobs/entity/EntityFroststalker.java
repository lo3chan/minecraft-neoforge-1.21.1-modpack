package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIMelee;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMFrostWalker;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public class EntityFroststalker extends Animal implements IAnimatedEntity, ISemiAquatic {
   public static final ResourceLocation SPIKED_LOOT = AMCompat.rl("alexsmobs", "entities/froststalker_spikes");
   public static final Animation ANIMATION_BITE = Animation.create(13);
   public static final Animation ANIMATION_SPEAK = Animation.create(11);
   public static final Animation ANIMATION_SLASH_L = Animation.create(12);
   public static final Animation ANIMATION_SLASH_R = Animation.create(12);
   public static final Animation ANIMATION_SHOVE = Animation.create(12);
   private static final EntityDataAccessor<Boolean> SPIKES = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SPIKE_SHAKING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BIPEDAL = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> TURN_ANGLE = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.FLOAT);
   public static final Predicate<Player> VALID_LEADER_PLAYERS = player -> player.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get());
   public float bipedProgress;
   public float prevBipedProgress;
   public float tackleProgress;
   public float prevTackleProgress;
   public float spikeShakeProgress;
   public float prevSpikeShakeProgress;
   public float prevTurnAngle;
   private int animationTick;
   private Animation currentAnimation;
   private int standingTime = 400 - this.random.nextInt(700);
   private int currentSpeedMode = -1;
   private LivingEntity leader;
   private int packSize = 1;
   private int shakeTime = 0;
   private boolean hasSpikedArmor = false;
   private int fleeFireFlag;
   private int resetLeaderCooldown = 100;

   protected EntityFroststalker(EntityType<? extends Animal> type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.FROSTSTALKER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.FROSTSTALKER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.FROSTSTALKER_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.froststalkerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canFroststalkerSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return worldIn.getRawBrightness(pos, 0) > 8
         && (worldIn.getBlockState(pos.below()).is(AMTagRegistry.FROSTSTALKER_SPAWNS) || worldIn.getBlockState(pos.below()).isSolid());
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      return this.hasSpikes() ? AMCompat.lootKey(SPIKED_LOOT) : super.getDefaultLootTable();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ATTACK_DAMAGE, 4.5)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.is(DamageTypeTags.IS_FIRE)) {
         amount *= 2.0F;
      }

      boolean prev = super.hurt(source, amount);
      if (prev && this.hasSpikes() && !this.isSpikeShaking() && source.getEntity() != null && source.getEntity().distanceTo(this) < 10.0F) {
         this.setSpikeShaking(true);
         this.shakeTime = 20 + this.random.nextInt(60);
         this.standFor(this.shakeTime + 10);
      }

      return prev;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this) {
         public void tick() {
            if (EntityFroststalker.this.getRandom().nextFloat() < 0.8F) {
               if (EntityFroststalker.this.hasSpikes()) {
                  EntityFroststalker.this.jumpUnderwater();
               } else {
                  EntityFroststalker.this.getJumpControl().jump();
               }
            }
         }
      });
      this.goalSelector.addGoal(1, new EntityFroststalker.AIAvoidFire());
      this.goalSelector.addGoal(2, new FroststalkerAIMelee(this));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, new FroststalkerAIFollowLeader(this));
      this.goalSelector.addGoal(6, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(7, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(8, new AnimalAIWanderRanged(this, 90, 1.0, 7, 7));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntityFroststalker.class}).setAlertOthers(new Class[0]));
      this.targetSelector
         .addGoal(
            2,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 40, false, true, AMCompat.selector(AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.FROSTSTALKER_TARGETS))
            )
         );
      this.targetSelector
         .addGoal(
            3,
            new NearestAttackableTargetGoal(
               this,
               Player.class,
               80,
               false,
               true,
               AMCompat.selector(livingEntity -> !livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get()))
            )
         );
   }

   private void jumpUnderwater() {
      BlockPos pos = this.getOnPos();
      if (this.level().isWaterAt(pos) && !this.level().isWaterAt(pos.above())) {
         this.setPos(this.getX(), this.getY() + 1.0, this.getZ());
         this.level().setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
         this.level().scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.getRandom(), 60, 120));
      }

      double d0 = 0.20000000298023224;
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x, d0, vec3.z);
   }

   public void setInLove(@Nullable Player player) {
      if (player != null && this.isValidLeader(player)) {
         super.setInLove(player);
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(TURN_ANGLE, 0.0F);
      builder.define(SPIKES, true);
      builder.define(BIPEDAL, false);
      builder.define(SPIKE_SHAKING, false);
      builder.define(TACKLING, false);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Spiked", this.hasSpikes());
      compound.putBoolean("Bipedal", this.isBipedal());
      compound.putBoolean("SpikeShaking", this.isSpikeShaking());
      compound.putInt("StandingTime", this.standingTime);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSpiked(AMCompat.getBoolean(compound, "Spiked"));
      this.setBipedal(AMCompat.getBoolean(compound, "Bipedal"));
      this.setSpikeShaking(AMCompat.getBoolean(compound, "SpikeShaking"));
      this.standingTime = AMCompat.getInt(compound, "StandingTime");
   }

   public BlockPos getRestrictCenter() {
      return this.leader == null ? super.getRestrictCenter() : this.leader.getOnPos();
   }

   public boolean hasRestriction() {
      return this.isFollower();
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.FROSTSTALKER_BREEDABLES);
   }

   public void tick() {
      super.tick();
      this.prevBipedProgress = this.bipedProgress;
      this.prevTackleProgress = this.tackleProgress;
      this.prevSpikeShakeProgress = this.spikeShakeProgress;
      this.prevTurnAngle = this.getTurnAngle();
      if (this.isBipedal()) {
         if (this.bipedProgress < 5.0F) {
            this.bipedProgress++;
         }
      } else if (this.bipedProgress > 0.0F) {
         this.bipedProgress--;
      }

      if (this.isTackling()) {
         if (this.tackleProgress < 5.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      if (this.isSpikeShaking()) {
         if (this.spikeShakeProgress < 5.0F) {
            this.spikeShakeProgress++;
         }

         if (this.currentSpeedMode != 2) {
            this.currentSpeedMode = 2;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612);
         }
      } else {
         if (this.spikeShakeProgress > 0.0F) {
            this.spikeShakeProgress--;
         }

         if (this.isBipedal()) {
            if (this.currentSpeedMode != 0) {
               this.currentSpeedMode = 0;
               this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
            }
         } else if (this.currentSpeedMode != 1) {
            this.currentSpeedMode = 1;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
         }
      }

      if (this.hasSpikes()) {
         if (!this.hasSpikedArmor) {
            this.hasSpikedArmor = true;
            this.getAttribute(Attributes.ARMOR).setBaseValue(12.0);
         }
      } else if (this.hasSpikedArmor) {
         this.hasSpikedArmor = false;
         this.getAttribute(Attributes.ARMOR).setBaseValue(0.0);
      }

      if (!this.level().isClientSide()) {
         if (this.tickCount % 200 == 0) {
            if (this.isInWaterRainOrBubble() && !this.hasSpikes()) {
               this.setSpiked(true);
            }

            if (this.isHotBiome() && !this.isInWaterRainOrBubble()) {
               this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400));
               if (this.random.nextInt(2) == 0 && !this.isInWaterRainOrBubble()) {
                  this.setSpiked(false);
               }
            }
         }

         float threshold = 1.0F;
         boolean flag = false;
         if (this.isBipedal() && this.yRotO - this.getYRot() > threshold) {
            this.setTurnAngle(this.getTurnAngle() + 5.0F);
            flag = true;
         }

         if (this.isBipedal() && this.yRotO - this.getYRot() < -threshold) {
            this.setTurnAngle(this.getTurnAngle() - 5.0F);
            flag = true;
         }

         if (!flag) {
            if (this.getTurnAngle() > 0.0F) {
               this.setTurnAngle(Math.max(this.getTurnAngle() - 10.0F, 0.0F));
            }

            if (this.getTurnAngle() < 0.0F) {
               this.setTurnAngle(Math.min(this.getTurnAngle() + 10.0F, 0.0F));
            }
         }

         this.setTurnAngle(Mth.clamp(this.getTurnAngle(), -60.0F, 60.0F));
         if (this.standingTime > 0) {
            this.standingTime--;
         }

         if (this.standingTime < 0) {
            this.standingTime++;
         }

         if (this.standingTime <= 0 && this.isBipedal()) {
            this.standingTime = -200 - this.random.nextInt(400);
            this.setBipedal(false);
         }

         if (this.standingTime == 0 && !this.isBipedal() && this.getDeltaMovement().lengthSqr() >= 0.03) {
            this.standingTime = 200 + this.random.nextInt(600);
            this.setBipedal(true);
         }

         if (this.shakeTime > 0) {
            if (this.shakeTime % 5 == 0) {
               int spikeCount = 2 + this.random.nextInt(4);

               for (int i = 0; i < spikeCount; i++) {
                  float f = (float)(i + 1) / spikeCount * 360.0F;
                  EntityIceShard shard = new EntityIceShard(this.level(), this);
                  shard.shootFromRotation(this, this.getXRot() - this.random.nextInt(40), f, 0.0F, 0.15F + this.random.nextFloat() * 0.2F, 1.0F);
                  this.level().addFreshEntity(shard);
               }
            }

            this.shakeTime--;
         }

         if (this.shakeTime == 0 && this.isSpikeShaking()) {
            this.setSpikeShaking(false);
            if (this.random.nextInt(2) == 0) {
               this.setSpiked(false);
            }
         }

         if (this.getTarget() != null && this.isValidLeader(this.getTarget())) {
            this.setTarget(null);
         }

         if (this.getTarget() != null
            && !this.isValidLeader(this.getTarget())
            && this.getTarget().isAlive()
            && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive())) {
            this.setLastHurtByMob(this.getTarget());
         }

         LivingEntity playerTarget = null;
         if (this.leader instanceof Player) {
            playerTarget = this.leader.getLastHurtMob();
            if (playerTarget == null || !playerTarget.isAlive() || playerTarget instanceof EntityFroststalker) {
               playerTarget = this.leader.getLastHurtByMob();
            }
         }

         if (playerTarget != null && playerTarget.isAlive() && !(playerTarget instanceof EntityFroststalker)) {
            this.setTarget(playerTarget);
         }

         boolean attackAnim = this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5
            || this.getAnimation() == ANIMATION_SHOVE && this.getAnimationTick() == 8
            || this.getAnimation() == ANIMATION_SLASH_L && this.getAnimationTick() == 7
            || this.getAnimation() == ANIMATION_SLASH_R && this.getAnimationTick() == 7;
         if (this.getTarget() != null && attackAnim) {
            AMCompat.knockback(this.getTarget(), 0.20000000298023224, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
            this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
         }
      }

      if (this.fleeFireFlag > 0) {
         this.fleeFireFlag--;
      }

      if (!this.level().isClientSide()) {
         if (this.resetLeaderCooldown > 0) {
            this.resetLeaderCooldown--;
         } else {
            this.resetLeaderCooldown = 200 + this.getRandom().nextInt(200);
            this.lookForPlayerLeader();
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void lookForPlayerLeader() {
      if (!(this.leader instanceof Player)) {
         float range = 10.0F;
         List<Player> playerList = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range, range, range), VALID_LEADER_PLAYERS);
         Player closestPlayer = null;

         for (Player player : playerList) {
            if (closestPlayer == null || player.distanceTo(this) < closestPlayer.distanceTo(this)) {
               closestPlayer = player;
            }
         }

         if (closestPlayer != null) {
            this.stopFollowing();
            this.startFollowing(closestPlayer);
         }
      }
   }

   public boolean isFleeingFire() {
      return this.fleeFireFlag > 0;
   }

   public boolean isHotBiome() {
      if (this.isNoAi()) {
         return false;
      } else if (this.level().dimension() == Level.NETHER) {
         return true;
      } else {
         int i = Mth.floor(this.getX());
         int k = Mth.floor(this.getZ());
         return this.level().getBiome(new BlockPos(i, 0, k)).is(BiomeTags.SNOW_GOLEM_MELTS);
      }
   }

   public void standFor(int time) {
      this.setBipedal(true);
      this.standingTime = time;
   }

   protected float getJumpPower() {
      return 0.52F * this.getBlockJumpFactor();
   }

   public void jumpFromGround() {
      double d0 = (double)this.getJumpPower() + this.getJumpBoostPower();
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x, d0, vec3.z);
      float f = this.getYRot() * 0.017453292F;
      this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.0, Mth.cos(f) * 0.2F));
      this.hasImpulse = true;
      CommonHooks.onLivingJump(this);
   }

   public void frostJump() {
      this.jumpFromGround();
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
      return new Animation[]{ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_SHOVE};
   }

   public float getTurnAngle() {
      return (Float)this.entityData.get(TURN_ANGLE);
   }

   public void setTurnAngle(float progress) {
      this.entityData.set(TURN_ANGLE, progress);
   }

   public boolean hasSpikes() {
      return (Boolean)this.entityData.get(SPIKES);
   }

   public void setSpiked(boolean bar) {
      this.entityData.set(SPIKES, bar);
   }

   public boolean isTackling() {
      return (Boolean)this.entityData.get(TACKLING);
   }

   public void setTackling(boolean bar) {
      this.entityData.set(TACKLING, bar);
   }

   public boolean isBipedal() {
      return (Boolean)this.entityData.get(BIPEDAL);
   }

   public void setBipedal(boolean bar) {
      this.entityData.set(BIPEDAL, bar);
   }

   public boolean isSpikeShaking() {
      return (Boolean)this.entityData.get(SPIKE_SHAKING);
   }

   public void setSpikeShaking(boolean bar) {
      this.entityData.set(SPIKE_SHAKING, bar);
   }

   public boolean isFollower() {
      return this.leader != null && this.isValidLeader(this.leader);
   }

   public boolean isValidLeader(LivingEntity leader) {
      if (leader instanceof Player) {
         return this.getLastHurtByMob() != null && this.getLastHurtByMob().equals(leader)
            ? false
            : leader.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get());
      } else {
         return leader.isAlive() && leader instanceof EntityFroststalker;
      }
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         int anim = this.random.nextInt(4);
         switch (anim) {
            case 0:
               this.setAnimation(ANIMATION_SHOVE);
               break;
            case 1:
               this.setAnimation(ANIMATION_BITE);
               break;
            case 2:
               this.setAnimation(ANIMATION_SLASH_L);
               break;
            case 3:
               this.setAnimation(ANIMATION_SLASH_R);
         }
      }

      return true;
   }

   public LivingEntity startFollowing(LivingEntity leader) {
      this.leader = leader;
      if (leader instanceof EntityFroststalker) {
         ((EntityFroststalker)leader).addFollower();
      }

      return leader;
   }

   public void stopFollowing() {
      if (this.leader instanceof EntityFroststalker) {
         ((EntityFroststalker)this.leader).removeFollower();
      }

      this.leader = null;
   }

   private void addFollower() {
      this.packSize++;
   }

   private void removeFollower() {
      this.packSize--;
   }

   public boolean canBeFollowed() {
      return this.hasFollowers() && this.packSize < this.getMaxPackSize() && this.isValidLeader(this);
   }

   public boolean hasFollowers() {
      return this.packSize > 1;
   }

   public int getMaxSpawnClusterSize() {
      return 6;
   }

   public int getMaxPackSize() {
      return this.getMaxSpawnClusterSize();
   }

   public void addFollowers(Stream<EntityFroststalker> p_27534_) {
      p_27534_.limit(this.getMaxPackSize() - this.packSize).filter(p_27538_ -> p_27538_ != this).forEach(p_27536_ -> p_27536_.startFollowing(this));
   }

   public boolean inRangeOfLeader() {
      return this.distanceTo(this.leader) <= 60.0;
   }

   public void pathToLeader() {
      if (this.isFollower()) {
         double speed = 1.0;
         if (this.leader instanceof Player) {
            speed = 1.3;
            if (this.distanceTo(this.leader) > 24.0F) {
               speed = 1.399999976158142;
               this.standFor(20);
            }
         }

         if (this.distanceTo(this.leader) > 6.0F && this.getNavigation().isDone()) {
            this.getNavigation().moveTo(this.leader, speed);
         }
      }
   }

   protected void onChangedBlock(ServerLevel level, BlockPos pos) {
      super.onChangedBlock(level, pos);
      if (this.hasSpikes()) {
         AMFrostWalker.freezeAround(this, level, pos, 1);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_27528_, DifficultyInstance p_27529_, MobSpawnType p_27530_, @Nullable SpawnGroupData p_27531_) {
      this.getAttribute(Attributes.FOLLOW_RANGE)
         .addPermanentModifier(AMCompat.attributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, Operation.ADD_MULTIPLIED_BASE));
      if (p_27531_ == null) {
         p_27531_ = new EntityFroststalker.SchoolSpawnGroupData(this);
      } else {
         this.startFollowing(((EntityFroststalker.SchoolSpawnGroupData)p_27531_).leader);
      }

      return p_27531_;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
      return AMCompat.create(AMEntityRegistry.FROSTSTALKER.get(), p_146743_);
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.hasSpikes() && (this.getTarget() == null || !this.getTarget().isAlive());
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.hasSpikes() || this.getTarget() != null && this.getTarget().isAlive();
   }

   @Override
   public boolean shouldStopMoving() {
      return false;
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   private class AIAvoidFire extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      protected int runDelay = 20;
      private Vec3 fleeTarget;

      private AIAvoidFire() {
         this.searchLength = 20;
         this.verticalSearchRange = 1;
      }

      public boolean canContinueToUse() {
         return this.destinationBlock != null && this.isFire(EntityFroststalker.this.level(), this.destinationBlock.mutable()) && this.isCloseToFire(16.0);
      }

      public boolean isCloseToFire(double dist) {
         return this.destinationBlock == null || EntityFroststalker.this.distanceToSqr(Vec3.atCenterOf(this.destinationBlock)) < dist * dist;
      }

      public boolean canUse() {
         if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 30 + EntityFroststalker.this.random.nextInt(100);
            return this.searchForDestination();
         }
      }

      public void start() {
         EntityFroststalker.this.fleeFireFlag = 200;
         Vec3 vec = LandRandomPos.getPosAway(EntityFroststalker.this, 15, 5, Vec3.atCenterOf(this.destinationBlock));
         if (vec != null) {
            EntityFroststalker.this.standFor(100 + EntityFroststalker.this.random.nextInt(100));
            this.fleeTarget = vec;
            EntityFroststalker.this.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.2000000476837158);
         }
      }

      public void tick() {
         if (this.isCloseToFire(16.0)) {
            EntityFroststalker.this.fleeFireFlag = 200;
            if (this.fleeTarget == null || EntityFroststalker.this.distanceToSqr(this.fleeTarget) < 2.0) {
               Vec3 vec = LandRandomPos.getPosAway(EntityFroststalker.this, 15, 5, Vec3.atCenterOf(this.destinationBlock));
               if (vec != null) {
                  this.fleeTarget = vec;
               }
            }

            if (this.fleeTarget != null) {
               EntityFroststalker.this.getNavigation().moveTo(this.fleeTarget.x, this.fleeTarget.y, this.fleeTarget.z, 1.0);
            }
         }
      }

      public void stop() {
         this.fleeTarget = null;
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         int lvt_2_1_ = this.verticalSearchRange;
         BlockPos lvt_3_1_ = EntityFroststalker.this.blockPosition();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isFire(EntityFroststalker.this.level(), lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isFire(Level world, MutableBlockPos lvt_4_1_) {
         return world.getBlockState(lvt_4_1_).is(AMTagRegistry.FROSTSTALKER_FEARS);
      }
   }

   public static class SchoolSpawnGroupData implements SpawnGroupData {
      public final EntityFroststalker leader;

      public SchoolSpawnGroupData(EntityFroststalker p_27553_) {
         this.leader = p_27553_;
      }
   }
}
