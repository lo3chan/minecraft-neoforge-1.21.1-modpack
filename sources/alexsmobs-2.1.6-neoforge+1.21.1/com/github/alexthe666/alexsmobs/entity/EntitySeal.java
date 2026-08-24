package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SealAIBask;
import com.github.alexthe666.alexsmobs.entity.ai.SealAIDiveForItems;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntitySeal extends Animal implements ISemiAquatic, IHerdPanic, ITargetsDroppedItems {
   private static final EntityDataAccessor<Float> SWIM_ANGLE = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> BASKING = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ARCTIC = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOB_TICKS = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.INT);
   public float prevSwimAngle;
   public float prevBaskProgress;
   public float baskProgress;
   public float prevDigProgress;
   public float digProgress;
   public float prevBobbingProgress;
   public float bobbingProgress;
   public int revengeCooldown = 0;
   public UUID feederUUID = null;
   private int baskingTimer = 0;
   private int swimTimer = -1000;
   private int ticksSinceInWater = 0;
   private boolean isLandNavigator;
   public int fishFeedings = 0;

   protected EntitySeal(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SEAL_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SEAL_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SEAL_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.18000000715255737);
   }

   public static boolean canSealSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      Holder<Biome> holder = worldIn.getBiome(pos);
      if (!holder.is(Biomes.FROZEN_OCEAN) && !holder.is(Biomes.DEEP_FROZEN_OCEAN)) {
         boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.SEAL_SPAWNS);
         return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
      } else {
         return worldIn.getRawBrightness(pos, 0) > 8 && worldIn.getBlockState(pos.below()).is(Blocks.ICE);
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SealAIBask(this));
      this.goalSelector.addGoal(1, new BreathAirGoal(this));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(4, new AnimalAIHerdPanic(this, 1.6));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(6, new SealAIDiveForItems(this));
      this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0, 7));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new AvoidEntityGoal(this, EntityOrca.class, 20.0F, 1.3, 1.0));
      this.goalSelector.addGoal(10, new TemptGoal(this, 1.1, AMCompat.ingredientOfTags(AMTagRegistry.SEAL_BREEDABLES, AMTagRegistry.SEAL_OFFERINGS), false));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, EntityFlyingFish.class, 55, true, true, null));
      this.targetSelector.addGoal(2, new CreatureAITargetItems(this, false));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AquaticMoveController(this, 1.5F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         double range = 15.0;
         int fleeTime = 100 + this.getRandom().nextInt(150);
         this.revengeCooldown = fleeTime;

         for (EntitySeal gaz : this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(15.0, 7.5, 15.0))) {
            gaz.revengeCooldown = fleeTime;
            gaz.setBasking(false);
         }

         this.setBasking(false);
      }

      return prev;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SWIM_ANGLE, 0.0F);
      builder.define(BASKING, false);
      builder.define(DIGGING, false);
      builder.define(ARCTIC, false);
      builder.define(VARIANT, 0);
      builder.define(BOB_TICKS, 0);
   }

   public boolean isTearsEasterEgg() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("he was");
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, 0.0, this.getZ() - this.zo);
      float f2 = Math.min(f1 * (this.isInWater() ? 4.0F : 48.0F), 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   public float getSwimAngle() {
      return (Float)this.entityData.get(SWIM_ANGLE);
   }

   public void setSwimAngle(float progress) {
      this.entityData.set(SWIM_ANGLE, progress);
   }

   public void tick() {
      super.tick();
      this.prevBaskProgress = this.baskProgress;
      this.prevDigProgress = this.digProgress;
      this.prevBobbingProgress = this.bobbingProgress;
      this.prevSwimAngle = this.getSwimAngle();
      boolean dig = this.isDigging() && this.isInWaterOrBubble();
      float f2 = (float)(-((float)this.getDeltaMovement().y * 57.2957763671875));
      if (this.isInWater()) {
         this.setXRot(f2 * 2.5F);
         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else if (!this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (this.isBasking()) {
         if (this.baskProgress < 5.0F) {
            this.baskProgress++;
         }
      } else if (this.baskProgress > 0.0F) {
         this.baskProgress--;
      }

      if (dig) {
         if (this.digProgress < 5.0F) {
            this.digProgress++;
         }
      } else if (this.digProgress > 0.0F) {
         this.digProgress--;
      }

      if (dig && this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).canOcclude()) {
         BlockPos posit = this.getBlockPosBelowThatAffectsMyMovement();
         BlockState understate = this.level().getBlockState(posit);

         for (int i = 0; i < 4 + this.random.nextInt(2); i++) {
            double particleX = posit.getX() + this.random.nextFloat();
            double particleY = posit.getY() + 1.0F;
            double particleZ = posit.getZ() + this.random.nextFloat();
            double motX = this.random.nextGaussian() * 0.02;
            double motY = 0.1F + this.random.nextFloat() * 0.2F;
            double motZ = this.random.nextGaussian() * 0.02;
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, understate), particleX, particleY, particleZ, motX, motY, motZ);
         }
      }

      if (!this.level().isClientSide()) {
         if (this.isBasking()) {
            if (this.getLastHurtByMob() != null
               || this.isInLove()
               || this.revengeCooldown > 0
               || this.isInWaterOrBubble()
               || this.getTarget() != null
               || this.isSeekingTreasure()
               || this.baskingTimer > 1000 && this.getRandom().nextInt(100) == 0) {
               this.setBasking(false);
            }
         } else if (this.getTarget() == null
            && !this.isInLove()
            && this.getLastHurtByMob() == null
            && this.revengeCooldown == 0
            && !this.isBasking()
            && this.baskingTimer == 0
            && !this.isSeekingTreasure()
            && this.getRandom().nextInt(15) == 0
            && !this.isInWaterOrBubble()) {
            this.setBasking(true);
         }

         if (this.revengeCooldown > 0) {
            this.revengeCooldown--;
         }

         if (this.revengeCooldown == 0 && this.getLastHurtByMob() != null) {
            this.setLastHurtByMob(null);
         }

         float threshold = 0.05F;
         if (this.isInWater() && this.yRotO - this.getYRot() > threshold) {
            this.setSwimAngle(this.getSwimAngle() + 2.0F);
         } else if (this.isInWater() && this.yRotO - this.getYRot() < -threshold) {
            this.setSwimAngle(this.getSwimAngle() - 2.0F);
         } else if (this.getSwimAngle() > 0.0F) {
            this.setSwimAngle(Math.max(this.getSwimAngle() - 10.0F, 0.0F));
         } else if (this.getSwimAngle() < 0.0F) {
            this.setSwimAngle(Math.min(this.getSwimAngle() + 10.0F, 0.0F));
         }

         this.setSwimAngle(Mth.clamp(this.getSwimAngle(), -70.0F, 70.0F));
         if (this.isBasking()) {
            this.baskingTimer++;
         } else {
            this.baskingTimer = 0;
         }

         if (this.isInWater()) {
            this.swimTimer++;
            this.ticksSinceInWater = 0;
         } else {
            this.ticksSinceInWater++;
            this.swimTimer--;
         }

         this.swimTimer = Mth.clamp(this.swimTimer, -1200, 800);
      }

      int bob = (Integer)this.entityData.get(BOB_TICKS);
      if (bob > 0) {
         bob--;
         if (this.bobbingProgress < 5.0F) {
            this.bobbingProgress++;
         }

         this.entityData.set(BOB_TICKS, bob);
      } else {
         if (this.bobbingProgress > 0.0F) {
            this.bobbingProgress--;
         }

         if (!this.level().isClientSide() && this.random.nextInt(300) == 0 && !this.isInWater() && this.revengeCooldown == 0) {
            bob = 20 + this.random.nextInt(20);
            this.entityData.set(BOB_TICKS, bob);
         }
      }
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public boolean isBasking() {
      return (Boolean)this.entityData.get(BASKING);
   }

   public void setBasking(boolean basking) {
      this.entityData.set(BASKING, basking);
   }

   public boolean isDigging() {
      return (Boolean)this.entityData.get(DIGGING);
   }

   public void setDigging(boolean digging) {
      this.entityData.set(DIGGING, digging);
   }

   public boolean isArctic() {
      return (Boolean)this.entityData.get(ARCTIC);
   }

   public void setArctic(boolean arctic) {
      this.entityData.set(ARCTIC, arctic);
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int currentAir) {
      return this.getMaxAirSupply();
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData data) {
      this.setArctic(this.isBiomeArctic(worldIn, this.blockPosition()));
      int i;
      if (data instanceof EntitySeal.SealGroupData) {
         i = ((EntitySeal.SealGroupData)data).variant;
      } else {
         i = this.random.nextInt(2);
         data = new EntitySeal.SealGroupData(i);
      }

      this.setVariant(i);
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, data);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Arctic", this.isArctic());
      compound.putBoolean("Basking", this.isBasking());
      compound.putInt("BaskingTimer", this.baskingTimer);
      compound.putInt("SwimTimer", this.swimTimer);
      compound.putInt("FishFeedings", this.fishFeedings);
      compound.putInt("Variant", this.getVariant());
      if (this.feederUUID != null) {
         AMCompat.putUUID(compound, "FeederUUID", this.feederUUID);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setArctic(AMCompat.getBoolean(compound, "Arctic"));
      this.setBasking(AMCompat.getBoolean(compound, "Basking"));
      this.baskingTimer = AMCompat.getInt(compound, "BaskingTimer");
      this.swimTimer = AMCompat.getInt(compound, "SwimTimer");
      this.fishFeedings = AMCompat.getInt(compound, "FishFeedings");
      if (AMCompat.hasUUID(compound, "FeederUUID")) {
         this.feederUUID = AMCompat.getUUID(compound, "FeederUUID");
      }

      this.setVariant(AMCompat.getInt(compound, "Variant"));
   }

   private boolean isBiomeArctic(LevelAccessor worldIn, BlockPos position) {
      return worldIn.getBiome(position).is(AMTagRegistry.SPAWNS_WHITE_SEALS);
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }

         if (this.isDigging()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.SEAL_BREEDABLES);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntitySeal seal = AMCompat.create(AMEntityRegistry.SEAL.get(), serverWorld);
      seal.setArctic(this.isBiomeArctic(serverWorld, this.blockPosition()));
      return seal;
   }

   public boolean isSeekingTreasure() {
      return this.feederUUID != null && this.getMainHandItem().isEmpty() && this.level().getPlayerByUUID(this.feederUUID) != null;
   }

   public boolean isFleeingToWater() {
      return this.revengeCooldown > 0 || this.getLastHurtByMob() != null;
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.isSeekingTreasure() && !this.isFleeingToWater() ? !this.shouldLeaveWater() && this.swimTimer <= -1000 : true;
   }

   @Override
   public boolean shouldLeaveWater() {
      if (!this.getPassengers().isEmpty()) {
         return false;
      } else if (this.isSeekingTreasure() || this.isFleeingToWater()) {
         return false;
      } else {
         return this.getTarget() != null && !this.getTarget().isInWater() ? true : this.swimTimer > 600;
      }
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isBasking();
   }

   @Override
   public int getWaterSearchRange() {
      return 32;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.SEAL_OFFERINGS) || stack.is(AMTagRegistry.SEAL_BREEDABLES);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      if (e.getItem().is(AMTagRegistry.SEAL_OFFERINGS)) {
         this.fishFeedings++;
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
         Entity itemThrower = e.getOwner();
         if (this.fishFeedings >= 3) {
            if (itemThrower != null) {
               this.feederUUID = itemThrower.getUUID();
            }

            this.fishFeedings = 0;
         }
      } else {
         this.feederUUID = null;
      }

      this.heal(10.0F);
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return !this.isBasking() && !this.isInWaterOrBubble();
   }

   public static class SealGroupData extends AgeableMobGroupData {
      public final int variant;

      SealGroupData(int variant) {
         super(true);
         this.variant = variant;
      }
   }
}
