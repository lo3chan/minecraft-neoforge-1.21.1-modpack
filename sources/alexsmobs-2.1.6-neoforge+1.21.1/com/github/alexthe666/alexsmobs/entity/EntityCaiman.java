package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CaimanAIBellow;
import com.github.alexthe666.alexsmobs.entity.ai.CaimanAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwnerWater;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntityCaiman extends TamableAnimal implements ISemiAquatic, IFollower {
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BELLOWING = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
   public float prevSitProgress;
   public float sitProgress;
   public float prevHoldProgress;
   public float holdProgress;
   public float prevSwimProgress;
   public float swimProgress;
   public float prevVibrateProgress;
   public float vibrateProgress;
   private int swimTimer = -1000;
   public int bellowCooldown = 100 + this.random.nextInt(1000);
   private boolean isLandNavigator;
   public boolean tameAttackFlag = false;

   public EntityCaiman(EntityType type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COMMAND, 0);
      builder.define(BELLOWING, false);
      builder.define(SITTING, false);
      builder.define(HAS_EGG, false);
      builder.define(HELD_MOB_ID, -1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(1, new EntityCaiman.MateGoal(this, 1.0));
      this.goalSelector.addGoal(1, new EntityCaiman.LayEggGoal(this, 1.0));
      this.goalSelector.addGoal(2, new CaimanAIMelee(this));
      this.goalSelector.addGoal(3, new BreathAirGoal(this));
      this.goalSelector.addGoal(4, new TameableAIFollowOwnerWater(this, 1.1, 4.0F, 2.0F, false));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2000000476837158, false));
      this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.CAIMAN_BREEDABLES), false));
      this.goalSelector.addGoal(7, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(7, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(8, new CaimanAIBellow(this));
      this.goalSelector.addGoal(9, new SemiAquaticAIRandomSwimming(this, 1.0, 30));
      this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this) {
         public void start() {
            super.start();
            EntityCaiman.this.tameAttackFlag = true;
         }

         public void stop() {
            super.start();
            EntityCaiman.this.tameAttackFlag = false;
         }
      });
      this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this) {
         public void start() {
            super.start();
            EntityCaiman.this.tameAttackFlag = true;
         }

         public void stop() {
            super.start();
            EntityCaiman.this.tameAttackFlag = false;
         }
      });
      this.targetSelector
         .addGoal(
            5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CAIMAN_TARGETS)) {
               public boolean canUse() {
                  return !EntityCaiman.this.isBaby() && !EntityCaiman.this.isTame() && super.canUse();
               }
            }
         );
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.caimanSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static <T extends Mob> boolean canCaimanSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
      BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
      return blockstate.is(Blocks.MUD) || blockstate.is(Blocks.MUDDY_MANGROVE_ROOTS) || blockstate.is(AMTagRegistry.CAIMAN_SPAWNS);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.CAIMAN_BREEDABLES);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AquaticMoveController(this, 1.1F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public int getMaxSpawnClusterSize() {
      return 2;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? AMSoundRegistry.CROCODILE_BABY.get() : AMSoundRegistry.CAIMAN_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.CAIMAN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.CAIMAN_HURT.get();
   }

   public void tick() {
      super.tick();
      this.prevHoldProgress = this.holdProgress;
      this.prevSwimProgress = this.swimProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevVibrateProgress = this.vibrateProgress;
      boolean ground = !this.isInWaterOrBubble();
      boolean bellowing = this.isBellowing();
      boolean grabbing = this.getHeldMobId() != -1;
      boolean sitting = this.isSitting() && ground;
      if (!ground && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (ground && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (ground && this.swimProgress > 0.0F) {
         this.swimProgress--;
      }

      if (!ground && this.swimProgress < 5.0F) {
         this.swimProgress++;
      }

      if (bellowing && this.vibrateProgress < 5.0F) {
         this.vibrateProgress++;
      }

      if (!bellowing && this.vibrateProgress > 0.0F) {
         this.vibrateProgress--;
      }

      if (sitting && this.sitProgress < 5.0F) {
         this.sitProgress++;
      }

      if (!sitting && this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (grabbing && this.holdProgress < 5.0F) {
         this.holdProgress += 2.5F;
      }

      if (!grabbing && this.holdProgress > 0.0F) {
         this.holdProgress -= 2.5F;
      }

      if (!this.level().isClientSide()) {
         if (this.isInWater()) {
            this.swimTimer++;
         } else {
            if (this.isBellowing()) {
               this.setBellowing(false);
            }

            this.swimTimer--;
         }

         if (this.getTarget() instanceof WaterAnimal && !this.isTame()) {
            WaterAnimal fish = (WaterAnimal)this.getTarget();
            CompoundTag fishNbt = new CompoundTag();
            AMCompat.saveAdditionalTo(fish, fishNbt);
            fishNbt.putString("DeathLootTable", "minecraft:empty");
            AMCompat.readAdditionalFrom(fish, fishNbt);
         }
      } else if (this.isInWaterOrBubble() && this.isBellowing()) {
         int particles = 4 + this.getRandom().nextInt(3);

         for (int i = 0; i <= particles; i++) {
            Vec3 particleVec = new Vec3(0.0, 0.0, 1.0).yRot((float)i / particles * 3.1415927F * 2.0F).add(this.position());
            double particleY = this.getBoundingBox().minY + AMPlatform.fluidHeightWater(this);
            this.level().addParticle(ParticleTypes.SPLASH, particleVec.x, particleY, particleVec.z, 0.0, 0.30000001192092896, 0.0);
         }
      }

      if (this.bellowCooldown > 0) {
         this.bellowCooldown--;
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (this.isTame() && itemstack.is(AMTagRegistry.CAIMAN_FOODSTUFFS) && this.getHealth() < this.getMaxHealth()) {
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
         this.heal(5.0F);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.isTame()
            && this.isOwnedBy(player)
            && !this.isFood(itemstack)) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.setOrderedToSit(true);
               return InteractionResult.SUCCESS;
            } else {
               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         } else {
            return type;
         }
      }
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public void setHeldMobId(int i) {
      this.entityData.set(HELD_MOB_ID, i);
   }

   public int getHeldMobId() {
      return (Integer)this.entityData.get(HELD_MOB_ID);
   }

   public boolean hasEgg() {
      return (Boolean)this.entityData.get(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.entityData.set(HAS_EGG, hasEgg);
   }

   public Entity getHeldMob() {
      int id = this.getHeldMobId();
      return id == -1 ? null : this.level().getEntity(id);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isBellowing() {
      return (Boolean)this.entityData.get(BELLOWING);
   }

   public void setBellowing(boolean bellowing) {
      this.entityData.set(BELLOWING, bellowing);
   }

   public void travel(Vec3 travelVector) {
      if (this.isSitting()) {
         super.travel(Vec3.ZERO);
      } else if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   public void calculateEntityAnimation(LivingEntity living, boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, 0.0, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.shouldLeaveWater() && this.swimTimer <= -1000 || this.bellowCooldown == 0;
   }

   @Override
   public boolean shouldLeaveWater() {
      LivingEntity target = this.getTarget();
      return target != null && !target.isInWater() ? true : this.swimTimer > 600 && !this.isBellowing();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isSitting();
   }

   @Override
   public int getWaterSearchRange() {
      return 12;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return AMCompat.create(AMEntityRegistry.CAIMAN.get(), serverLevel);
   }

   public Vec3 getShakePreyPos() {
      Vec3 jaw = new Vec3(0.0, -0.1, 1.0);
      Vec3 head = jaw.xRot(-this.getXRot() * 0.017453292F).yRot(-this.getYHeadRot() * 0.017453292F);
      return this.getEyePosition().add(head);
   }

   public void push(double x, double y, double z) {
      if (this.getHeldMobId() == -1) {
         super.push(x, y, z);
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("HasEgg", this.hasEgg());
      compound.putBoolean("Bellowing", this.isBellowing());
      compound.putInt("CaimanCommand", this.getCommand());
      compound.putBoolean("CaimanSitting", this.isOrderedToSit());
      compound.putInt("BellowCooldown", this.bellowCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setHasEgg(AMCompat.getBoolean(compound, "HasEgg"));
      this.setBellowing(AMCompat.getBoolean(compound, "Bellowing"));
      this.bellowCooldown = AMCompat.getInt(compound, "BellowCooldown");
      this.setCommand(AMCompat.getInt(compound, "CaimanCommand"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "CaimanSitting"));
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final EntityCaiman caiman;
      private int digTime;

      LayEggGoal(EntityCaiman caiman, double speedIn) {
         super(caiman, speedIn, 16);
         this.caiman = caiman;
      }

      public void stop() {
         this.digTime = 0;
      }

      public boolean canUse() {
         return this.caiman.hasEgg() && super.canUse();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.caiman.hasEgg();
      }

      public double acceptedDistance() {
         return this.caiman.getBbWidth() + 0.5;
      }

      public void tick() {
         super.tick();
         BlockPos blockpos = this.caiman.blockPosition();
         this.caiman.swimTimer = 1000;
         if (!this.caiman.isInWater() && this.isReachedTarget()) {
            Level world = this.caiman.level();
            this.caiman.gameEvent(GameEvent.BLOCK_PLACE);
            world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.getRandom().nextFloat() * 0.2F);
            world.setBlock(
               this.blockPos.above(),
               (BlockState)AMBlockRegistry.CAIMAN_EGG.get().defaultBlockState().setValue(BlockReptileEgg.EGGS, this.caiman.random.nextInt(1) + 3),
               3
            );
            this.caiman.setHasEgg(false);
            this.caiman.setInLoveTime(600);
         }
      }

      protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
         return worldIn.isEmptyBlock(pos.above()) && BlockReptileEgg.isProperHabitat(worldIn, pos);
      }
   }

   static class MateGoal extends BreedGoal {
      private final EntityCaiman caiman;

      MateGoal(EntityCaiman caiman, double speedIn) {
         super(caiman, speedIn);
         this.caiman = caiman;
      }

      public boolean canUse() {
         return super.canUse() && !this.caiman.hasEgg();
      }

      protected void breed() {
         ServerPlayer serverplayerentity = this.animal.getLoveCause();
         if (serverplayerentity == null && this.partner.getLoveCause() != null) {
            serverplayerentity = this.partner.getLoveCause();
         }

         if (serverplayerentity != null) {
            serverplayerentity.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, this.animal);
         }

         this.caiman.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         RandomSource random = this.animal.getRandom();
         if (AMCompat.gameRule(this.level, AMCompat.Rule.MOB_LOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }
      }
   }
}
