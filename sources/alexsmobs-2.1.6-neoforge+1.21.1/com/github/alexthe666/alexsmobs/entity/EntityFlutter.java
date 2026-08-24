package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EntityFlutter extends TamableAnimal implements IFollower, FlyingAnimal {
   private static final EntityDataAccessor<Float> FLUTTER_PITCH = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> POTTED = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TENTACLING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> SHAKING_HEAD_TICKS = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.INT);
   public float prevFlyProgress;
   public float flyProgress;
   public float prevShootProgress;
   public float shootProgress;
   public float prevSitProgress;
   public float sitProgress;
   public float prevFlutterPitch;
   public float tentacleProgress;
   public float prevTentacleProgress;
   public float FlutterRotation;
   private float rotationVelocity;
   private int squishCooldown = 0;
   private float randomMotionSpeed;
   private boolean isLandNavigator;
   private int timeFlying;
   private List<String> flowersEaten = new ArrayList<>();
   private boolean hasPotStats = false;

   protected EntityFlutter(EntityType type, Level level) {
      super(type, level);
      this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.5F;
      this.switchNavigator(false);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.FLYING_SPEED, 0.800000011920929)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20999999344348907);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.requiresCustomPersistence() && !this.hasCustomName();
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName() || this.isTame() || this.isPotted();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.flutterSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canFlutterSpawnInLight(
      EntityType<? extends EntityFlutter> p_223325_0_,
      ServerLevelAccessor p_223325_1_,
      MobSpawnType p_223325_2_,
      BlockPos p_223325_3_,
      RandomSource p_223325_4_
   ) {
      return checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
   }

   public static <T extends Mob> boolean canFlutterSpawn(
      EntityType<EntityFlutter> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      BlockState blockstate = iServerWorld.getBlockState(pos.below());
      return reason == MobSpawnType.SPAWNER
         || !iServerWorld.canSeeSky(pos)
            && blockstate.is(AMTagRegistry.FLUTTER_SPAWNS)
            && pos.getY() <= 64
            && canFlutterSpawnInLight(entityType, iServerWorld, reason, pos, random);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityFlutter.FlyAwayFromTarget(this));
      this.goalSelector.addGoal(2, new TameableAITempt(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.FLUTTER_BREEDABLES), false) {
         @Override
         public boolean shouldFollowAM(LivingEntity le) {
            return EntityFlutter.this.canEatFlower(le.getMainHandItem()) || EntityFlutter.this.canEatFlower(le.getOffhandItem()) || super.shouldFollowAM(le);
         }
      });
      this.goalSelector.addGoal(3, new FlyingAIFollowOwner(this, 1.3, 7.0F, 2.0F, false));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(5, new EntityFlutter.AIWalkIdle());
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 1.0F, false, true);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLUTTER_PITCH, 0.0F);
      builder.define(FLYING, false);
      builder.define(POTTED, false);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
      builder.define(TENTACLING, false);
      builder.define(SHOOTING, false);
      builder.define(SHAKING_HEAD_TICKS, 0);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   public boolean isPotted() {
      return (Boolean)this.entityData.get(POTTED);
   }

   public void setPotted(boolean potted) {
      this.entityData.set(POTTED, potted);
   }

   public float getFlutterPitch() {
      return Mth.clamp((Float)this.entityData.get(FLUTTER_PITCH), -90.0F, 90.0F);
   }

   public void setFlutterPitch(float pitch) {
      this.entityData.set(FLUTTER_PITCH, pitch);
   }

   public void incrementFlutterPitch(float pitch) {
      this.entityData.set(FLUTTER_PITCH, this.getFlutterPitch() + pitch);
   }

   public void decrementFlutterPitch(float pitch) {
      this.entityData.set(FLUTTER_PITCH, this.getFlutterPitch() - pitch);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.FLUTTER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.FLUTTER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.FLUTTER_HURT.get();
   }

   public void tick() {
      super.tick();
      this.prevShootProgress = this.shootProgress;
      this.prevFlyProgress = this.flyProgress;
      this.prevFlutterPitch = this.getFlutterPitch();
      this.prevSitProgress = this.sitProgress;
      float extraMotionSlow = 1.0F;
      float extraMotionSlowY = 1.0F;
      this.yBodyRot = this.getYRot();
      this.yHeadRot = this.getYRot();
      this.prevFlutterPitch = this.getFlutterPitch();
      this.prevTentacleProgress = this.tentacleProgress;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.tentacleProgress < 5.0F && (Boolean)this.entityData.get(TENTACLING)) {
         this.tentacleProgress++;
      }

      if (this.tentacleProgress == 5.0F && !(Boolean)this.entityData.get(TENTACLING) && this.squishCooldown == 0 && this.isFlying()) {
         this.squishCooldown = 10;
         this.playSound(AMSoundRegistry.FLUTTER_FLAP.get(), this.getSoundVolume(), 1.5F * this.getVoicePitch());
      }

      if (this.tentacleProgress > 0.0F && !(Boolean)this.entityData.get(TENTACLING)) {
         this.tentacleProgress--;
      }

      this.FlutterRotation = this.FlutterRotation + this.rotationVelocity;
      if (this.FlutterRotation > 6.2831854820251465) {
         if (this.level().isClientSide()) {
            this.FlutterRotation = 6.2831855F;
         } else {
            this.FlutterRotation = (float)(this.FlutterRotation - 6.2831854820251465);
            if (this.random.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.5F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.FlutterRotation < 3.1415927F) {
         float f = this.FlutterRotation / 3.1415927F;
         if (f >= 0.949999988079071) {
            this.entityData.set(TENTACLING, true);
            if (this.squishCooldown == 0 && this.isFlying()) {
               this.squishCooldown = 10;
               this.gameEvent(AMPlatform.ENTITY_ACTION);
               this.playSound(AMSoundRegistry.FLUTTER_FLAP.get(), 3.0F, 1.5F * this.getVoicePitch());
            }

            this.randomMotionSpeed = 0.8F;
         } else {
            this.entityData.set(TENTACLING, false);
            this.randomMotionSpeed = 0.01F;
         }
      }

      if (!this.level().isClientSide()) {
         if (this.isFlying() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.isFlying() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            this.setDeltaMovement(
               this.getDeltaMovement().x * this.randomMotionSpeed * extraMotionSlow,
               this.getDeltaMovement().y * this.randomMotionSpeed * extraMotionSlowY,
               this.getDeltaMovement().z * this.randomMotionSpeed * extraMotionSlow
            );
            this.timeFlying++;
            if (this.onGround() && this.timeFlying > 20 || this.isSitting()) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
         }
      }

      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.8, 1.0));
      }

      if (this.isFlying()) {
         float dist = (float)((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 30.0);
         this.incrementFlutterPitch(-dist);
         if (this.horizontalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.20000000298023224, 0.0));
         }
      }

      if (this.getFlutterPitch() > 0.0F) {
         float decrease = Math.min(2.5F, this.getFlutterPitch());
         this.decrementFlutterPitch(decrease);
      }

      if (this.getFlutterPitch() < 0.0F) {
         float decrease = Math.min(2.5F, -this.getFlutterPitch());
         this.incrementFlutterPitch(decrease);
      }

      boolean shooting = (Boolean)this.entityData.get(SHOOTING);
      if (shooting && this.shootProgress < 5.0F) {
         this.shootProgress++;
      }

      if (!shooting && this.shootProgress > 0.0F) {
         this.shootProgress--;
      }

      if (shooting) {
         this.incrementFlutterPitch(-30.0F);
      }

      if (!this.level().isClientSide() && shooting && this.shootProgress == 5.0F) {
         if (this.getTarget() != null) {
            this.spit(this.getTarget());
         }

         this.entityData.set(SHOOTING, false);
      }

      if (this.hasPotStats && !this.isPotted()) {
         this.hasPotStats = false;
         this.getAttribute(Attributes.ARMOR).setBaseValue(0.21);
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.21);
      }

      if (!this.hasPotStats && this.isPotted()) {
         this.hasPotStats = true;
         this.getAttribute(Attributes.ARMOR).setBaseValue(16.0);
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.18);
      }

      if ((Integer)this.entityData.get(SHAKING_HEAD_TICKS) > 0) {
         this.entityData.set(SHAKING_HEAD_TICKS, (Integer)this.entityData.get(SHAKING_HEAD_TICKS) - 1);
      }

      if (this.squishCooldown > 0) {
         this.squishCooldown--;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.FLUTTER_BREEDABLES) && this.isTame();
   }

   private void spit(LivingEntity target) {
      EntityPollenBall llamaspitentity = new EntityPollenBall(this.level(), this);
      double d0 = target.getX() - this.getX();
      double d1 = target.getY(0.3333333333333333) - llamaspitentity.getY();
      double d2 = target.getZ() - this.getZ();
      float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.2F;
      llamaspitentity.shoot(d0, d1 + f, d2, 0.5F, 13.0F);
      if (!this.isSilent()) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT);
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LLAMA_SPIT,
               this.getSoundSource(),
               1.0F,
               1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
      }

      this.level().addFreshEntity(llamaspitentity);
   }

   public boolean isShakingHead() {
      return (Integer)this.entityData.get(SHAKING_HEAD_TICKS) > 0;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isTame() && this.canEatFlower(itemstack)) {
         this.usePlayerItem(player, hand, itemstack);
         this.flowersEaten.add(BuiltInRegistries.ITEM.getKey(itemstack.getItem()).toString());
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(AMSoundRegistry.FLUTTER_YES.get(), this.getSoundVolume(), this.getVoicePitch());
         if ((this.flowersEaten.size() <= 3 || this.getRandom().nextInt(3) != 0) && this.flowersEaten.size() <= 6) {
            this.level().broadcastEntityEvent(this, (byte)6);
         } else {
            this.tame(player);
            this.level().broadcastEntityEvent(this, (byte)7);
         }

         return InteractionResult.SUCCESS;
      } else {
         if (!this.isTame() && itemstack.is(ItemTags.FLOWERS)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(AMSoundRegistry.FLUTTER_NO.get(), this.getSoundVolume(), this.getVoicePitch());
            this.entityData.set(SHAKING_HEAD_TICKS, 20);
         }

         if (this.isTame() && itemstack.is(ItemTags.FLOWERS) && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
            if (interactionresult == InteractionResult.SUCCESS
               || type == InteractionResult.SUCCESS
               || !this.isTame()
               || !this.isOwnedBy(player)
               || this.isFood(itemstack)
               || itemstack.is(ItemTags.FLOWERS)) {
               return type;
            } else if (item == Items.FLOWER_POT && !this.isPotted()) {
               this.setPotted(true);
               return InteractionResult.SUCCESS;
            } else if (itemstack.is(net.neoforged.neoforge.common.Tags.Items.TOOLS_SHEAR) && this.isPotted()) {
               this.setPotted(false);
               AMCompat.spawnAtLocation(this, Items.FLOWER_POT);
               return InteractionResult.SUCCESS;
            } else if (this.isPotted() && player.isShiftKeyDown()) {
               ItemStack fish = this.getFishBucket();
               if (!player.addItem(fish)) {
                  player.drop(fish, false);
               }

               this.remove(RemovalReason.DISCARDED);
               return AMCompat.sidedSuccess(this.level().isClientSide());
            } else {
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
            }
         }
      }
   }

   @Override
   public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      if (this.distanceTo(owner) > 8.0F) {
         this.setFlying(true);
         this.getNavigation().moveTo(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
      } else if (this.isFlying() && !this.isOverWaterOrVoid()) {
         BlockPos vec = this.getFlutterGround(this.blockPosition());
         if (vec != null) {
            this.getMoveControl().setWantedPosition(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
         }

         if (this.onGround()) {
            this.setFlying(false);
         }
      } else {
         this.getNavigation().moveTo(owner, followSpeed);
      }
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isPotted() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, Items.FLOWER_POT);
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(entityIn);
         }
      }

      return super.isAlliedTo(entityIn);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("Potted", this.isPotted());
      compound.putInt("FlowersEaten", this.flowersEaten.size());

      for (int i = 0; i < this.flowersEaten.size(); i++) {
         compound.putString("FlowerEaten" + i, this.flowersEaten.get(i));
      }

      compound.putInt("FlutterCommand", this.getCommand());
      compound.putBoolean("FlutterSitting", this.isSitting());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setPotted(AMCompat.getBoolean(compound, "Potted"));
      int flowerCount = AMCompat.getInt(compound, "FlowersEaten");
      this.flowersEaten = new ArrayList<>();

      for (int i = 0; i < flowerCount; i++) {
         String s = AMCompat.getString(compound, "FlowerEaten" + i);
         if (s != null) {
            this.flowersEaten.add(s);
         }
      }

      this.setCommand(AMCompat.getInt(compound, "FlutterCommand"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "FlutterSitting"));
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -63 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || position.getY() < -63;
   }

   private BlockPos getFlutterGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > -63 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return position.getY() < -62 ? position.above(120 + this.random.nextInt(5)) : position;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 1 + this.getRandom().nextInt(3) + radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + this.getRandom().nextFloat() * neg * 0.2F;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getFlutterGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 3 + this.getRandom().nextInt(2);
      BlockPos newPos = ground.above(distFromGround > 4 ? flightHeight : distFromGround - 2 + this.getRandom().nextInt(4));
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.getRandom().nextInt(24);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getFlutterGround(radialPos);
      if (ground.getY() <= -63) {
         return Vec3.upFromBottomCenterOf(ground, 110 + this.random.nextInt(20));
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -63 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground.below()) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   protected ItemStack getFishBucket() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.POTTED_FLUTTER.get());
      CompoundTag platTag = new CompoundTag();
      AMCompat.saveAdditionalTo(this, platTag);
      AMCompat.addTagElement(stack, "FlutterData", platTag);
      if (this.hasCustomName()) {
         AMCompat.setHoverName(stack, this.getCustomName());
      }

      return stack;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mobo) {
      EntityFlutter baby = AMCompat.create(AMEntityRegistry.FLUTTER.get(), this.level());
      baby.setPersistenceRequired();
      return baby;
   }

   public boolean hasEatenFlower(ItemStack stack) {
      return this.flowersEaten != null && this.flowersEaten.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
   }

   public boolean canEatFlower(ItemStack stack) {
      return !this.hasEatenFlower(stack) && stack.is(ItemTags.FLOWERS);
   }

   private void setupShooting() {
      this.entityData.set(SHOOTING, true);
   }

   public void spawnChildFromBreeding(ServerLevel world, Animal partner) {
      super.spawnChildFromBreeding(world, partner);

      for (int i = 0; i < 15 + this.random.nextInt(10); i++) {
         BlockPos nearby = this.blockPosition().offset(this.random.nextInt(16) - 8, this.random.nextInt(2), this.random.nextInt(16) - 8);
         if (world.getBlockState(nearby).getBlock() == Blocks.AZALEA) {
            world.setBlockAndUpdate(nearby, Blocks.FLOWERING_AZALEA.defaultBlockState());
            world.levelEvent(1505, nearby, 0);
         }

         if (world.getBlockState(nearby).getBlock() == Blocks.AZALEA_LEAVES) {
            world.setBlockAndUpdate(nearby, Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState());
            world.levelEvent(1505, nearby, 0);
         }
      }
   }

   private class AIWalkIdle extends Goal {
      protected final EntityFlutter phage;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.phage = EntityFlutter.this;
      }

      public boolean canUse() {
         if (this.phage.isVehicle()
            || this.phage.isSitting()
            || this.phage.shouldFollow()
            || this.phage.getTarget() != null && this.phage.getTarget().isAlive()
            || this.phage.isPassenger()) {
            return false;
         } else if (this.phage.getRandom().nextInt(30) != 0 && !this.phage.isFlying() && !this.phage.isInWaterOrBubble()) {
            return false;
         } else {
            if (this.phage.onGround() && !this.phage.isInWaterOrBubble()) {
               this.flightTarget = EntityFlutter.this.random.nextInt(4) == 0 && !this.phage.isBaby();
            } else {
               this.flightTarget = EntityFlutter.this.random.nextInt(5) > 0 && this.phage.timeFlying < 100 && !this.phage.isBaby();
            }

            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.x;
               this.y = lvt_1_1_.y;
               this.z = lvt_1_1_.z;
               return true;
            }
         }
      }

      public void tick() {
         if (this.flightTarget) {
            this.phage.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityFlutter.this.isFlying() && this.phage.onGround()) {
            this.phage.setFlying(false);
         }

         if (EntityFlutter.this.isFlying() && this.phage.onGround() && this.phage.timeFlying > 40) {
            this.phage.setFlying(false);
         }
      }

      @javax.annotation.Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.phage.position();
         if (this.phage.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.phage.timeFlying >= 180 && !this.phage.isOverWaterOrVoid()
               ? this.phage.getBlockGrounding(vector3d)
               : this.phage.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.getPos(this.phage, 5, 5);
         }
      }

      public boolean canContinueToUse() {
         if (this.phage.isSitting()) {
            return false;
         } else {
            return this.flightTarget
               ? this.phage.isFlying() && this.phage.distanceToSqr(this.x, this.y, this.z) > 2.0 && !this.phage.isBaby()
               : !this.phage.getNavigation().isDone() && !this.phage.isVehicle();
         }
      }

      public void start() {
         if (this.flightTarget) {
            this.phage.setFlying(true);
            this.phage.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.phage.getNavigation().stop();
         super.stop();
      }
   }

   private class FlyAwayFromTarget extends Goal {
      private final EntityFlutter parentEntity;
      private int spitCooldown = 0;
      private BlockPos shootPos = null;

      public FlyAwayFromTarget(EntityFlutter entityFlutter) {
         this.parentEntity = entityFlutter;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return !this.parentEntity.isSitting()
            && this.parentEntity.getTarget() != null
            && this.parentEntity.getTarget().isAlive()
            && !this.parentEntity.isBaby();
      }

      public void tick() {
         if (this.spitCooldown > 0) {
            this.spitCooldown--;
         }

         if (this.parentEntity.getTarget() != null) {
            this.parentEntity.setFlying(true);
            if (this.shootPos == null
               || this.parentEntity.distanceTo(this.parentEntity.getTarget()) >= 10.0F
               || this.parentEntity.getTarget().distanceToSqr(this.shootPos.getX() + 0.5F, this.shootPos.getY(), this.shootPos.getZ() + 0.5F) < 4.0) {
               this.shootPos = this.getShootFromPos(this.parentEntity.getTarget());
            }

            if (this.shootPos != null) {
               this.parentEntity.getMoveControl().setWantedPosition(this.shootPos.getX() + 0.5, this.shootPos.getY() + 0.5, this.shootPos.getZ() + 0.5, 1.5);
            }

            if (this.parentEntity.distanceTo(this.parentEntity.getTarget()) < 25.0F) {
               this.parentEntity.lookAt(this.parentEntity.getTarget(), 30.0F, 30.0F);
               if (this.spitCooldown == 0) {
                  this.parentEntity.setupShooting();
                  this.spitCooldown = 10 + EntityFlutter.this.random.nextInt(10);
               }

               this.shootPos = null;
            }
         }
      }

      public BlockPos getShootFromPos(LivingEntity target) {
         float radius = 3 + this.parentEntity.getRandom().nextInt(5);
         float angle = 0.017453292F * (target.yHeadRot + 90.0F + this.parentEntity.getRandom().nextInt(180));
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(target.getX() + extraX, target.getY() + 2.0, target.getZ() + extraZ);
         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(radialPos))
            ? radialPos
            : this.parentEntity.blockPosition().above((int)Math.ceil(target.getBbHeight() + 1.0F));
      }
   }
}
