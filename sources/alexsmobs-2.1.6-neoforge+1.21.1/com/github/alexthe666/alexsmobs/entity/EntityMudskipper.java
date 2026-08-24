package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.MudskipperAIAttack;
import com.github.alexthe666.alexsmobs.entity.ai.MudskipperAIDisplay;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwnerWater;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityMudskipper extends TamableAnimal implements IFollower, ISemiAquatic, Bucketable {
   public float prevSitProgress;
   public float sitProgress;
   public float prevSwimProgress;
   public float swimProgress;
   public float prevDisplayProgress;
   public float displayProgress;
   public float prevMudProgress;
   public float mudProgress;
   public float nextDisplayAngleFromServer;
   public float prevDisplayAngle;
   public boolean displayDirection;
   public int displayTimer = 0;
   public boolean instantlyTriggerDisplayAI = false;
   public int displayCooldown = 100 + this.random.nextInt(100);
   private static final EntityDataAccessor<Boolean> DISPLAYING = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DISPLAY_ANGLE = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> DISPLAYER_UUID = SynchedEntityData.defineId(
      EntityMudskipper.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> MOUTH_TICKS = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.INT);
   private boolean isLandNavigator;
   private int swimTimer = -1000;

   public EntityMudskipper(EntityType type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchNavigator(true);
   }

   public void travel(Vec3 travelVector) {
      if (this.isOrderedToSit()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         travelVector = Vec3.ZERO;
         super.travel(travelVector);
      } else {
         if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         } else {
            super.travel(travelVector);
         }
      }
   }

   public static <T extends Mob> boolean canMudskipperSpawn(
      EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
      return blockstate.is(Blocks.MUD) || blockstate.is(Blocks.MUDDY_MANGROVE_ROOTS);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mudskipperSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      BlockPos pos = AMBlockPos.fromCoords(this.getX(), this.getEyeY(), this.getZ());
      return !worldIn.getBlockState(pos).isSuffocating(worldIn, pos);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(1, new TameableAIFollowOwnerWater(this, 1.3, 4.0F, 2.0F, false));
      this.goalSelector.addGoal(2, new MudskipperAIAttack(this));
      this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
      this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.MUDSKIPPER_TAMEABLES), false));
      this.goalSelector.addGoal(5, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(6, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(7, new MudskipperAIDisplay(this));
      this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(this, 1.0, 80));
      this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0, 120));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
         public boolean canUse() {
            return EntityMudskipper.this.isTame() && super.canUse();
         }
      });
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1.0F);
         this.navigation = new SemiAquaticPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DISPLAYING, false);
      builder.define(FROM_BUCKET, false);
      builder.define(DISPLAY_ANGLE, 0.0F);
      builder.define(DISPLAYER_UUID, Optional.empty());
      builder.define(MOUTH_TICKS, 0);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 12.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putInt("DisplayCooldown", this.displayCooldown);
      compound.putInt("MudskipperCommand", this.getCommand());
      compound.putBoolean("MudskipperSitting", this.isOrderedToSit());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.displayCooldown = AMCompat.getInt(compound, "DisplayCooldown");
      this.setCommand(AMCompat.getInt(compound, "MudskipperCommand"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "MudskipperSitting"));
   }

   public void tick() {
      super.tick();
      this.prevSwimProgress = this.swimProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevDisplayProgress = this.displayProgress;
      this.prevMudProgress = this.mudProgress;
      if (this.displayProgress < 5.0F && this.isDisplaying()) {
         this.displayProgress++;
      }

      if (this.displayProgress > 0.0F && !this.isDisplaying()) {
         this.displayProgress--;
      }

      if (this.sitProgress < 5.0F && this.isOrderedToSit()) {
         this.sitProgress++;
      }

      if (this.sitProgress > 0.0F && !this.isOrderedToSit()) {
         this.sitProgress--;
      }

      boolean mud = this.onMud();
      if (mud) {
         if (this.mudProgress < 1.0F) {
            this.mudProgress += 0.5F;
         }
      } else if (this.mudProgress > 0.0F) {
         this.mudProgress -= 0.5F;
      }

      boolean swim = !this.onGround() && this.isInWaterOrBubble();
      if (this.swimProgress < 5.0F && swim) {
         this.swimProgress++;
      }

      if (this.swimProgress > 0.0F && !swim) {
         this.swimProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isInWaterOrBubble()) {
            this.swimTimer++;
         } else {
            this.swimTimer--;
         }
      }

      if (this.displayCooldown > 0) {
         this.displayCooldown--;
      }

      if (!this.level().isClientSide()) {
         if (this.getDisplayAngle() < this.nextDisplayAngleFromServer) {
            this.setDisplayAngle(this.getDisplayAngle() + 1.0F);
         }

         if (this.getDisplayAngle() > this.nextDisplayAngleFromServer) {
            this.setDisplayAngle(this.getDisplayAngle() - 1.0F);
         }
      }

      if (this.isMouthOpen()) {
         this.openMouth(this.getMouthTicks() - 1);
      }

      if (this.isInWater() && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (!this.isInWater() && !this.isLandNavigator) {
         this.switchNavigator(true);
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getDirectEntity() instanceof LivingEntity) {
         this.openMouth(10);
      }

      return prev;
   }

   public boolean isDisplaying() {
      return (Boolean)this.entityData.get(DISPLAYING);
   }

   public void setDisplaying(boolean display) {
      this.entityData.set(DISPLAYING, display);
   }

   public float getDisplayAngle() {
      return (Float)this.entityData.get(DISPLAY_ANGLE);
   }

   public void setDisplayAngle(float scale) {
      this.entityData.set(DISPLAY_ANGLE, scale);
   }

   public int getMouthTicks() {
      return (Integer)this.entityData.get(MOUTH_TICKS);
   }

   public void openMouth(int time) {
      this.entityData.set(MOUTH_TICKS, time);
   }

   @Nullable
   public UUID getDisplayingPartnerUUID() {
      return (UUID)((Optional)this.entityData.get(DISPLAYER_UUID)).orElse(null);
   }

   public void setDisplayingPartnerUUID(@Nullable UUID uniqueId) {
      this.entityData.set(DISPLAYER_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getDisplayingPartner() {
      UUID id = this.getDisplayingPartnerUUID();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setDisplayingPartner(@Nullable Entity jostlingPartner) {
      if (jostlingPartner == null) {
         this.setDisplayingPartnerUUID(null);
      } else {
         this.setDisplayingPartnerUUID(jostlingPartner.getUUID());
      }
   }

   public boolean canDisplayWith(EntityMudskipper mudskipper) {
      return !mudskipper.isBaby()
         && !mudskipper.isOrderedToSit()
         && !mudskipper.shouldFollow()
         && mudskipper.onGround()
         && mudskipper.getDisplayingPartnerUUID() == null
         && mudskipper.displayCooldown == 0;
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return AMCompat.create(AMEntityRegistry.MUDSKIPPER.get(), serverLevel);
   }

   public boolean isMouthOpen() {
      return this.getMouthTicks() > 0;
   }

   public boolean onMud() {
      BlockState below = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
      return below.is(Blocks.MUD);
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, 0.0, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(AMSoundRegistry.MUDSKIPPER_WALK.get(), 1.0F, 1.0F);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MUDSKIPPER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MUDSKIPPER_HURT.get();
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public boolean isOrderedToSit() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   @Override
   public boolean shouldEnterWater() {
      return (this.getLastHurtByMob() != null || this.swimTimer <= -1000) && !this.isDisplaying();
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.swimTimer > 200 || this.isDisplaying();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isOrderedToSit();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean bucket) {
      this.entityData.set(FROM_BUCKET, bucket);
   }

   @Nonnull
   public ItemStack getBucketItemStack() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.MUDSKIPPER_BUCKET.get());
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
      AMCompat.put(compound, "MudskipperData", platTag);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      if (AMCompat.contains(compound, "MudskipperData")) {
         AMCompat.readAdditionalFrom(this, AMCompat.getCompound(compound, "MudskipperData"));
      }
   }

   @Nonnull
   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.MUDSKIPPER_BREEDABLES);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isTame() && itemstack.is(AMTagRegistry.MUDSKIPPER_TAMEABLES)) {
         this.usePlayerItem(player, hand, itemstack);
         this.openMouth(10);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
         if (this.getRandom().nextInt(2) == 0) {
            this.tame(player);
            this.level().broadcastEntityEvent(this, (byte)7);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }

         return InteractionResult.SUCCESS;
      } else if (this.isTame() && itemstack.is(AMTagRegistry.MUDSKIPPER_FOODSTUFFS)) {
         if (this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.openMouth(10);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (item != Items.WATER_BUCKET
            && interactionresult != InteractionResult.SUCCESS
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
            return Bucketable.bucketMobPickup(player, hand, this).orElse(type);
         }
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
}
