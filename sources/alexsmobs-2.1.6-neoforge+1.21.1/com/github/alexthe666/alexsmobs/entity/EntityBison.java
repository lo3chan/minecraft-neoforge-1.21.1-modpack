package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
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
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

public class EntityBison extends Animal implements IAnimatedEntity, Shearable, IShearable {
   public static final Animation ANIMATION_PREPARE_CHARGE = Animation.create(40);
   public static final Animation ANIMATION_EAT = Animation.create(35);
   public static final Animation ANIMATION_ATTACK = Animation.create(15);
   private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(EntityBison.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.defineId(EntityBison.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(EntityBison.class, EntityDataSerializers.BOOLEAN);
   public float prevChargeProgress;
   public float chargeProgress;
   private int animationTick;
   private Animation currentAnimation;
   private int snowTimer = 0;
   private boolean permSnow = false;
   private int blockBreakCounter;
   private int chargeCooldown = this.random.nextInt(2000);
   private EntityBison chargePartner;
   private boolean hasChargedSpeed = false;
   private int feedingsSinceLastShear = 0;

   protected EntityBison(EntityType<? extends Animal> animal, Level lvl) {
      super(animal, lvl);
      AMCompat.setMaxUpStep(this, 1.1F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.bisonSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(0.25F);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.BISON_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.BISON_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.BISON_HURT.get();
   }

   protected void playStepSound(BlockPos p_28301_, BlockState p_28302_) {
      this.playSound(SoundEvents.COW_STEP, 0.1F, 1.0F);
   }

   public boolean isSnowy() {
      return (Boolean)this.entityData.get(SNOWY);
   }

   public void setSnowy(boolean honeyed) {
      this.entityData.set(SNOWY, honeyed);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(3, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.BISON_BREEDABLES), false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new EntityBison.AIChargeFurthest());
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 70, 1.0, 18, 7));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EntityBison.AIAttackNearPlayers());
      this.targetSelector.addGoal(2, new AnimalAIHurtByTargetNotBaby(this));
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.BISON_BREEDABLES);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHEARED, false);
      builder.define(SNOWY, false);
      builder.define(CHARGING, false);
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
      return AMCompat.create(AMEntityRegistry.BISON.get(), this.level());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSnowy(AMCompat.getBoolean(compound, "Snowy"));
      this.setSheared(AMCompat.getBoolean(compound, "Sheared"));
      this.permSnow = AMCompat.getBoolean(compound, "SnowPerm");
      this.chargeCooldown = AMCompat.getInt(compound, "ChargeCooldown");
      this.feedingsSinceLastShear = AMCompat.getInt(compound, "Feedings");
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Snowy", this.isSnowy());
      compound.putBoolean("Sheared", this.isSheared());
      compound.putBoolean("SnowPerm", this.permSnow);
      compound.putInt("ChargeCooldown", this.chargeCooldown);
      compound.putInt("Feedings", this.feedingsSinceLastShear);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public void tick() {
      super.tick();
      this.prevChargeProgress = this.chargeProgress;
      if (this.isCharging() && this.chargeProgress < 5.0F) {
         this.chargeProgress++;
      }

      if (!this.isCharging() && this.chargeProgress > 0.0F) {
         this.chargeProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.snowTimer == 0) {
            this.snowTimer = 200 + this.random.nextInt(400);
            if (this.isSnowy()) {
               if (!this.permSnow
                  && (
                     this.getRemainingFireTicks() > 0 || this.isInWaterOrBubble() || !EntityGrizzlyBear.isSnowingAt(this.level(), this.blockPosition().above())
                  )) {
                  this.setSnowy(false);
               }
            } else if (EntityGrizzlyBear.isSnowingAt(this.level(), this.blockPosition())) {
               this.setSnowy(true);
            }
         }

         LivingEntity attackTarget = this.getTarget();
         if (this.getDeltaMovement().lengthSqr() < 0.05
            && this.getAnimation() == NO_ANIMATION
            && (attackTarget == null || !attackTarget.isAlive())
            && this.getRandom().nextInt(600) == 0
            && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            this.setAnimation(ANIMATION_EAT);
         }

         if (this.getAnimation() == ANIMATION_EAT
            && this.getAnimationTick() == 30
            && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            this.feedingsSinceLastShear++;
            BlockPos down = this.blockPosition().below();
            this.level().levelEvent(2001, down, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
            this.level().setBlock(down, Blocks.DIRT.defaultBlockState(), 2);
         }

         if (this.isCharging()) {
            if (!this.hasChargedSpeed) {
               this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6499999761581421);
               this.hasChargedSpeed = true;
            }
         } else if (this.hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
            this.hasChargedSpeed = false;
         }

         if (attackTarget != null && attackTarget.isAlive() && this.isAlive()) {
            double dist = this.distanceTo(attackTarget);
            if (this.hasLineOfSight(attackTarget)) {
               this.lookAt(attackTarget, 30.0F, 30.0F);
               this.yBodyRot = this.getYRot();
            }

            if (dist < this.getBbWidth() + 3.0F) {
               if (this.getAnimation() == ANIMATION_ATTACK
                  && this.getAnimationTick() > 8
                  && dist < this.getBbWidth() + 1.0F
                  && this.hasLineOfSight(attackTarget)) {
                  float dmg = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                  if (attackTarget instanceof Wolf) {
                     dmg = 2.0F;
                  }

                  this.launch(attackTarget, this.isCharging());
                  if (this.isCharging()) {
                     dmg += 3.0F;
                     this.setCharging(false);
                  }

                  attackTarget.hurt(this.damageSources().mobAttack(this), dmg);
               }
            } else if (!this.isCharging()) {
               Animation animation = this.getAnimation();
               if (animation == NO_ANIMATION) {
                  this.setAnimation(ANIMATION_PREPARE_CHARGE);
               } else if (animation == ANIMATION_PREPARE_CHARGE) {
                  this.getNavigation().stop();
                  if (this.getAnimationTick() > 30) {
                     this.setCharging(true);
                  }
               }
            }
         }
      }

      if (this.chargeCooldown > 0) {
         this.chargeCooldown--;
      }

      if (this.feedingsSinceLastShear >= 5 && this.isSheared()) {
         this.feedingsSinceLastShear = 0;
         this.setSheared(false);
      }

      if (!this.level().isClientSide() && this.isCharging() && (this.getTarget() == null && this.chargePartner == null || this.isInWaterOrBubble())) {
         this.setCharging(false);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   public boolean isSheared() {
      return (Boolean)this.entityData.get(SHEARED);
   }

   public void setSheared(boolean b) {
      this.entityData.set(SHEARED, b);
   }

   private void launch(Entity launch, boolean huge) {
      float rot = 180.0F + this.getYRot();
      float hugeScale = huge ? 4.0F : 0.6F;
      float strength = (float)(hugeScale * (1.0 - ((LivingEntity)launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
      float rotRad = rot * 0.017453292F;
      float x = Mth.sin(rotRad);
      float z = -Mth.cos(rotRad);
      launch.hasImpulse = true;
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 vec31 = vec3.add(new Vec3(x, 0.0, z).normalize().scale(strength));
      launch.setDeltaMovement(vec31.x, huge ? 1.0 : 0.5, vec31.z);
      launch.setOnGround(false);
   }

   private void knockbackTarget(LivingEntity entity, float strength, float angle) {
      float rot = this.getYRot() + angle;
      if (entity != null) {
         AMCompat.knockback(entity, strength, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.level().isClientSide()) {
         if (item == Items.SNOW && !this.isSnowy()) {
            this.usePlayerItem(player, hand, itemstack);
            this.permSnow = true;
            this.setSnowy(true);
            this.playSound(SoundEvents.SNOW_PLACE, this.getSoundVolume(), this.getVoicePitch());
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return InteractionResult.SUCCESS;
         }

         if (item instanceof ShovelItem && this.isSnowy()) {
            this.permSnow = false;
            if (!player.isCreative()) {
               AMCompat.hurtItem(itemstack, 1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
            }

            this.setSnowy(false);
            this.playSound(SoundEvents.SNOW_BREAK, this.getSoundVolume(), this.getVoicePitch());
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return InteractionResult.SUCCESS;
         }
      }

      return type;
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.breakBlock();
   }

   public void breakBlock() {
      if (this.blockBreakCounter > 0) {
         this.blockBreakCounter--;
      } else {
         boolean flag = false;
         if (!this.level().isClientSide() && this.blockBreakCounter == 0 && AMPlatform.mobGriefing(this.level(), this)) {
            for (int a = (int)Math.round(this.getBoundingBox().minX); a <= (int)Math.round(this.getBoundingBox().maxX); a++) {
               for (int b = (int)Math.round(this.getBoundingBox().minY) - 1; b <= (int)Math.round(this.getBoundingBox().maxY) + 1 && b <= 127; b++) {
                  for (int c = (int)Math.round(this.getBoundingBox().minZ); c <= (int)Math.round(this.getBoundingBox().maxZ); c++) {
                     BlockPos pos = new BlockPos(a, b, c);
                     BlockState state = this.level().getBlockState(pos);
                     Block block = state.getBlock();
                     if (block == Blocks.SNOW && (Integer)state.getValue(SnowLayerBlock.LAYERS) <= 1) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6000000238418579, 1.0, 0.6000000238418579));
                        flag = true;
                        this.level().destroyBlock(pos, true);
                     }
                  }
               }
            }
         }

         if (flag) {
            this.blockBreakCounter = this.isCharging() && this.getTarget() != null ? 2 : 20;
         }
      }
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
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
      return new Animation[]{ANIMATION_PREPARE_CHARGE, ANIMATION_ATTACK, ANIMATION_EAT};
   }

   public boolean isShearable(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      return this.readyForShearing();
   }

   public void shear(SoundSource category) {
      this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      this.setSheared(true);
      this.feedingsSinceLastShear = 0;

      for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.BISON_FUR.get());
      }
   }

   public boolean isCharging() {
      return (Boolean)this.entityData.get(CHARGING);
   }

   public void setCharging(boolean charging) {
      this.entityData.set(CHARGING, charging);
   }

   public boolean readyForShearing() {
      return !this.isSheared() && !this.isBaby();
   }

   @Nonnull
   public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      List<ItemStack> list = new ArrayList<>(6);

      for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
         list.add(new ItemStack((ItemLike)AMItemRegistry.BISON_FUR.get()));
      }

      this.feedingsSinceLastShear = 0;
      this.setSheared(true);
      return list;
   }

   public boolean isValidCharging() {
      return !this.isBaby() && this.isAlive() && this.chargeCooldown == 0 && !this.isInWaterOrBubble();
   }

   public void pushBackJostling(EntityBison bison, float strength) {
      this.applyKnockbackFromBuffalo(strength, bison.getX() - this.getX(), bison.getZ() - this.getZ());
   }

   private void applyKnockbackFromBuffalo(float strength, double ratioX, double ratioZ) {
      LivingKnockBackEvent event = CommonHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
      if (!event.isCanceled()) {
         strength = event.getStrength();
         ratioX = event.getRatioX();
         ratioZ = event.getRatioZ();
         if (!(strength <= 0.0F)) {
            this.hasImpulse = true;
            Vec3 vector3d = this.getDeltaMovement();
            Vec3 vector3d1 = new Vec3(ratioX, 0.0, ratioZ).normalize().scale(strength);
            this.setDeltaMovement(vector3d.x / 2.0 - vector3d1.x, 0.30000001192092896, vector3d.z / 2.0 - vector3d1.z);
         }
      }
   }

   private void resetChargeCooldown() {
      this.setCharging(false);
      this.chargePartner = null;
      this.chargeCooldown = 1000 + this.random.nextInt(2000);
   }

   class AIAttackNearPlayers extends NearestAttackableTargetGoal<Player> {
      public AIAttackNearPlayers() {
         super(EntityBison.this, Player.class, 80, true, true, null);
      }

      public boolean canUse() {
         return !EntityBison.this.isBaby() && !EntityBison.this.isInLove() ? super.canUse() : false;
      }

      protected double getFollowDistance() {
         return 3.0;
      }
   }

   private class AIChargeFurthest extends Goal {
      public AIChargeFurthest() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (EntityBison.this.isValidCharging()) {
            if (EntityBison.this.chargePartner != null
               && EntityBison.this.chargePartner.isValidCharging()
               && EntityBison.this.chargePartner != EntityBison.this) {
               EntityBison.this.chargePartner.chargePartner = EntityBison.this;
               return true;
            }

            if (EntityBison.this.random.nextInt(100) == 0) {
               EntityBison furthest = null;

               for (EntityBison bison : EntityBison.this.level().getEntitiesOfClass(EntityBison.class, EntityBison.this.getBoundingBox().inflate(15.0))) {
                  if (bison.chargeCooldown == 0
                     && !bison.isBaby()
                     && !bison.is(EntityBison.this)
                     && (furthest == null || EntityBison.this.distanceTo(furthest) < EntityBison.this.distanceTo(bison))) {
                     furthest = bison;
                  }
               }

               if (furthest != null && furthest != EntityBison.this) {
                  EntityBison.this.chargePartner = furthest;
                  furthest.chargePartner = EntityBison.this;
                  return true;
               }
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return EntityBison.this.isValidCharging()
            && EntityBison.this.chargePartner != null
            && EntityBison.this.chargePartner.isValidCharging()
            && !EntityBison.this.chargePartner.is(EntityBison.this);
      }

      public void tick() {
         EntityBison.this.lookAt(EntityBison.this.chargePartner, 30.0F, 30.0F);
         EntityBison.this.yBodyRot = EntityBison.this.getYRot();
         if (!EntityBison.this.isCharging()) {
            Animation bisonAnimation = EntityBison.this.getAnimation();
            if (bisonAnimation == IAnimatedEntity.NO_ANIMATION
               || bisonAnimation == EntityBison.ANIMATION_PREPARE_CHARGE && EntityBison.this.getAnimationTick() > 35) {
               EntityBison.this.setCharging(true);
            }
         } else {
            float dist = EntityBison.this.distanceTo(EntityBison.this.chargePartner);
            EntityBison.this.getNavigation().moveTo(EntityBison.this.chargePartner, 1.0);
            if (EntityBison.this.hasLineOfSight(EntityBison.this.chargePartner)) {
               float flingAnimAt = EntityBison.this.getBbWidth() + 1.0F;
               if (!(dist < flingAnimAt) || EntityBison.this.getAnimation() != EntityBison.ANIMATION_ATTACK) {
                  float startFlingAnimAt = EntityBison.this.getBbWidth() + 3.0F;
                  if (dist < startFlingAnimAt && EntityBison.this.getAnimation() != EntityBison.ANIMATION_ATTACK) {
                     EntityBison.this.setAnimation(EntityBison.ANIMATION_ATTACK);
                  }
               } else if (EntityBison.this.getAnimationTick() > 8) {
                  boolean flag = false;
                  if (EntityBison.this.onGround()) {
                     EntityBison.this.pushBackJostling(EntityBison.this.chargePartner, 0.2F);
                     flag = true;
                  }

                  if (EntityBison.this.chargePartner.onGround()) {
                     EntityBison.this.chargePartner.pushBackJostling(EntityBison.this, 0.9F);
                     flag = true;
                  }

                  if (flag) {
                     EntityBison.this.resetChargeCooldown();
                  }
               }
            }
         }
      }
   }
}
