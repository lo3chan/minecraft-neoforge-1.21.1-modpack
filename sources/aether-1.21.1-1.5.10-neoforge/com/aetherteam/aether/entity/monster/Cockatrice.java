package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.NotGrounded;
import com.aetherteam.aether.entity.WingedBird;
import com.aetherteam.aether.entity.ai.goal.FallingRandomStrollGoal;
import com.aetherteam.aether.entity.ai.navigator.FallPathNavigation;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public class Cockatrice extends Monster implements RangedAttackMob, WingedBird, NotGrounded {
   private static final EntityDataAccessor<Boolean> DATA_ENTITY_ON_GROUND_ID = SynchedEntityData.defineId(Cockatrice.class, EntityDataSerializers.BOOLEAN);
   private float wingRotation;
   private float prevWingRotation;
   private float destPos;
   private float prevDestPos;
   private int flapCooldown;

   public Cockatrice(EntityType<? extends Cockatrice> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0F));
      this.goalSelector.addGoal(3, new FallingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 5.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected PathNavigation createNavigation(Level level) {
      return new FallPathNavigation(this, level);
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ENTITY_ON_GROUND_ID, true);
   }

   public static boolean checkCockatriceSpawnRules(
      EntityType<? extends Cockatrice> cockatrice, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return Mob.checkMobSpawnRules(cockatrice, level, reason, pos, random)
         && isDarkEnoughToSpawn(level, pos, random)
         && !level.getBlockState(pos.below()).is(AetherTags.Blocks.COCKATRICE_SPAWNABLE_BLACKLIST)
         && level.getDifficulty() != Difficulty.PEACEFUL
         && (reason != MobSpawnType.NATURAL || random.nextInt(3) == 0);
   }

   public static boolean isDarkEnoughToSpawn(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
      if (level.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
         return false;
      } else {
         DimensionType dimensiontype = level.dimensionType();
         int i = dimensiontype.monsterSpawnBlockLightLimit();
         return i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i
            ? false
            : level.getMaxLocalRawBrightness(pos) <= dimensiontype.monsterSpawnLightTest().sample(random);
      }
   }

   public void aiStep() {
      super.aiStep();
      this.animateWings();
   }

   public void tick() {
      super.tick();
      if (this.onGround()) {
         this.setEntityOnGround(true);
      }

      AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
      if (gravity != null) {
         double fallSpeed = Math.max(gravity.getValue() * -1.25, -0.1);
         if (this.getDeltaMovement().y() < fallSpeed) {
            this.setDeltaMovement(this.getDeltaMovement().x(), fallSpeed, this.getDeltaMovement().z());
            this.hasImpulse = true;
            this.setEntityOnGround(false);
         }
      }

      if (this.getFlapCooldown() > 0) {
         this.setFlapCooldown(this.getFlapCooldown() - 1);
      } else if (this.getFlapCooldown() == 0 && !this.onGround()) {
         this.level()
            .playSound(
               null,
               this,
               (SoundEvent)AetherSoundEvents.ENTITY_COCKATRICE_FLAP.get(),
               SoundSource.NEUTRAL,
               0.15F,
               Mth.clamp(this.getRandom().nextFloat(), 0.7F, 1.0F) + Mth.clamp(this.getRandom().nextFloat(), 0.0F, 0.3F)
            );
         this.setFlapCooldown(15);
      }
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.setEntityOnGround(false);
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      PoisonNeedle needle = new PoisonNeedle(this.level(), this);
      double d0 = target.getX() - this.getX();
      double d1 = target.getY(0.75) - needle.getY();
      double d2 = target.getZ() - this.getZ();
      double d3 = Mth.sqrt((float)(Mth.square(d0) + Mth.square(d2)));
      needle.shoot(d0, d1 + d3 * 0.2, d2, 1.0F, 14 - this.level().getDifficulty().getId() * 4);
      this.playSound((SoundEvent)AetherSoundEvents.ENTITY_COCKATRICE_SHOOT.get(), 2.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level().addFreshEntity(needle);
   }

   @Override
   public boolean isEntityOnGround() {
      return (Boolean)this.getEntityData().get(DATA_ENTITY_ON_GROUND_ID);
   }

   @Override
   public void setEntityOnGround(boolean onGround) {
      this.getEntityData().set(DATA_ENTITY_ON_GROUND_ID, onGround);
   }

   @Override
   public float getWingRotation() {
      return this.wingRotation;
   }

   @Override
   public void setWingRotation(float rotation) {
      this.wingRotation = rotation;
   }

   @Override
   public float getPrevWingRotation() {
      return this.prevWingRotation;
   }

   @Override
   public void setPrevWingRotation(float rotation) {
      this.prevWingRotation = rotation;
   }

   @Override
   public float getWingDestPos() {
      return this.destPos;
   }

   @Override
   public void setWingDestPos(float pos) {
      this.destPos = pos;
   }

   @Override
   public float getPrevWingDestPos() {
      return this.prevDestPos;
   }

   @Override
   public void setPrevWingDestPos(float pos) {
      this.prevDestPos = pos;
   }

   public int getFlapCooldown() {
      return this.flapCooldown;
   }

   public void setFlapCooldown(int flapCooldown) {
      this.flapCooldown = flapCooldown;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_COCKATRICE_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_COCKATRICE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_COCKATRICE_DEATH.get();
   }

   public int getMaxFallDistance() {
      return this.onGround() ? super.getMaxFallDistance() : 14;
   }

   public boolean canBeAffected(MobEffectInstance effect) {
      return effect.getEffect().value() != AetherEffects.INEBRIATION.get() && super.canBeAffected(effect);
   }
}
