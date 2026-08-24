package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityBaldEagle extends TamableAnimal implements IFollower, IFalconry {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_CAP = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
   private static final Ingredient TEMPT_ITEMS = Ingredient.of(new ItemLike[]{Items.ROTTEN_FLESH, (ItemLike)AMItemRegistry.FISH_OIL.get()});
   public float prevAttackProgress;
   public float attackProgress;
   public float prevFlyProgress;
   public float flyProgress;
   public float prevTackleProgress;
   public float tackleProgress;
   public float prevSwoopProgress;
   public float swoopProgress;
   public float prevFlapAmount;
   public float flapAmount;
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   public float prevSitProgress;
   public float sitProgress;
   private boolean isLandNavigator;
   private int timeFlying;
   private BlockPos orbitPos = null;
   private double orbitDist = 5.0;
   private boolean orbitClockwise = false;
   private int passengerTimer = 0;
   private int launchTime = 0;
   private int lastPlayerControlTime = 0;
   private int returnControlTime = 0;
   private int tackleCapCooldown = 0;
   private boolean controlledFlag = false;
   private int chunkLoadCooldown;
   private int stillTicksCounter = 0;

   protected EntityBaldEagle(EntityType<? extends TamableAnimal> type, Level worldIn) {
      super(type, worldIn);
      this.switchNavigator(true);
      this.lookControl = new LookControl(this) {
         protected boolean resetXRotOnTick() {
            return !EntityBaldEagle.this.controlledFlag;
         }
      };
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   public static boolean canEagleSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getRawBrightness(pos, 0) > 8;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector
         .addGoal(
            0,
            new FloatGoal(this) {
               public boolean canUse() {
                  return super.canUse()
                     && (
                        EntityBaldEagle.this.getAirSupply() < 30
                           || EntityBaldEagle.this.getTarget() == null
                           || !EntityBaldEagle.this.getTarget().isInWaterOrBubble() && EntityBaldEagle.this.getY() > EntityBaldEagle.this.getTarget().getY()
                     );
               }
            }
         );
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0, 25.0F, 2.0F, false));
      this.goalSelector.addGoal(3, new EntityBaldEagle.AITackle());
      this.goalSelector.addGoal(4, new EntityBaldEagle.AILandOnGlove());
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(6, new TemptGoal(this, 1.1, AMCompat.ingredientOfTags(AMTagRegistry.BALD_EAGLE_TAMEABLES, AMTagRegistry.BALD_EAGLE_FOODSTUFFS), false));
      this.goalSelector.addGoal(7, new EntityBaldEagle.AIWanderIdle());
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F) {
         public boolean canUse() {
            return EntityBaldEagle.this.returnControlTime == 0 && !EntityBaldEagle.this.controlledFlag && super.canUse();
         }

         public boolean canContinueToUse() {
            return !EntityBaldEagle.this.controlledFlag && super.canContinueToUse();
         }
      });
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this) {
         public boolean canUse() {
            return EntityBaldEagle.this.returnControlTime == 0 && !EntityBaldEagle.this.controlledFlag && super.canUse();
         }

         public boolean canContinueToUse() {
            return !EntityBaldEagle.this.controlledFlag && super.canContinueToUse();
         }
      });
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new AnimalAIHurtByTargetNotBaby(this));
      this.targetSelector
         .addGoal(
            4,
            new EntityAINearestTarget3D(this, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.BALD_EAGLE_TARGETS)) {
               public boolean canUse() {
                  return super.canUse() && !EntityBaldEagle.this.isLaunched() && EntityBaldEagle.this.getCommand() == 0;
               }
            }
         );
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.BALD_EAGLE_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.BALD_EAGLE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.BALD_EAGLE_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.baldEagleSpawnRolls, this.getRandom(), spawnReasonIn);
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

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.BALD_EAGLE_BREEDABLES);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigatorWide(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntityBaldEagle.MoveHelper(this);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public boolean save(CompoundTag compound) {
      String s = this.getEncodeId();
      compound.putString("id", s);
      super.save(compound);
      return true;
   }

   public boolean saveAsPassenger(CompoundTag compound) {
      if (!this.isTame()) {
         return super.saveAsPassenger(compound);
      } else {
         String s = this.getEncodeId();
         compound.putString("id", s);
         this.saveWithoutId(compound);
         return true;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("BirdSitting", this.isSitting());
      compound.putBoolean("Launched", this.isLaunched());
      compound.putBoolean("HasCap", this.hasCap());
      compound.putInt("EagleCommand", this.getCommand());
      compound.putInt("LaunchTime", this.launchTime);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "BirdSitting"));
      this.setLaunched(AMCompat.getBoolean(compound, "Launched"));
      this.setCap(AMCompat.getBoolean(compound, "HasCap"));
      this.setCommand(AMCompat.getInt(compound, "EagleCommand"));
      this.launchTime = AMCompat.getInt(compound, "LaunchTime");
   }

   public void travel(Vec3 vec3d) {
      if ((this.shouldHoodedReturn() || !this.hasCap() || !this.isTame() || this.isPassenger()) && !this.isSitting()) {
         super.travel(vec3d);
      } else {
         super.travel(Vec3.ZERO);
      }
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.attackProgress == 0.0F && (Integer)this.entityData.get(ATTACK_TICK) == 0 && entityIn.isAlive()) {
         double dist = this.isSitting() ? entityIn.getBbWidth() + 1.0F : entityIn.getBbWidth() + 5.0F;
         if (this.distanceTo(entityIn) < dist) {
            this.entityData.set(ATTACK_TICK, 5);
         }
      }

      return true;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(HAS_CAP, false);
      builder.define(TACKLING, false);
      builder.define(LAUNCHED, false);
      builder.define(ATTACK_TICK, 0);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public boolean isLaunched() {
      return (Boolean)this.entityData.get(LAUNCHED);
   }

   public void setLaunched(boolean flying) {
      this.entityData.set(LAUNCHED, flying);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      if (flying && this.isBaby()) {
         flying = false;
      }

      this.entityData.set(FLYING, flying);
   }

   public boolean hasCap() {
      return (Boolean)this.entityData.get(HAS_CAP);
   }

   public void setCap(boolean cap) {
      this.entityData.set(HAS_CAP, cap);
   }

   public boolean isTackling() {
      return (Boolean)this.entityData.get(TACKLING);
   }

   public void setTackling(boolean tackling) {
      this.entityData.set(TACKLING, tackling);
   }

   @Override
   public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      if (this.distanceTo(owner) > 15.0F) {
         this.setFlying(true);
         this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
      } else if (this.isFlying() && !this.isOverWaterOrVoid()) {
         BlockPos vec = this.getCrowGround(this.blockPosition());
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

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (itemstack.is(AMTagRegistry.BALD_EAGLE_FOODSTUFFS) && this.getHealth() < this.getMaxHealth()) {
         this.heal(10.0F);
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.level().broadcastEntityEvent(this, (byte)7);
         return InteractionResult.CONSUME;
      } else if (itemstack.is(AMTagRegistry.BALD_EAGLE_TAMEABLES)) {
         if (AMCompat.hasCraftingRemainder(itemstack) && !player.getAbilities().instabuild) {
            AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(itemstack));
         }

         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         if (this.random.nextBoolean()) {
            this.level().broadcastEntityEvent(this, (byte)7);
            this.tame(player);
            this.setCommand(1);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }

         return InteractionResult.CONSUME;
      } else {
         if (this.isTame() && !this.isFood(itemstack)) {
            if (!this.isBaby() && item == AMItemRegistry.FALCONRY_HOOD.get()) {
               if (!this.hasCap()) {
                  this.setCap(true);
                  if (!player.isCreative()) {
                     itemstack.shrink(1);
                  }

                  this.gameEvent(GameEvent.ENTITY_INTERACT);
                  this.playSound((SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value(), this.getSoundVolume(), this.getVoicePitch());
                  return InteractionResult.SUCCESS;
               }
            } else {
               if (itemstack.is(net.neoforged.neoforge.common.Tags.Items.TOOLS_SHEAR) && this.hasCap()) {
                  this.gameEvent(GameEvent.ENTITY_INTERACT);
                  this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                  if (!this.level().isClientSide() && player instanceof ServerPlayer) {
                     AMCompat.hurtItem(itemstack, 1, this.random, (ServerPlayer)player);
                  }

                  AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.FALCONRY_HOOD.get());
                  this.setCap(false);
                  return InteractionResult.SUCCESS;
               }

               if (!this.isBaby()
                  && this.getRidingFalcons(player) <= 0
                  && (
                     player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()
                        || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()
                  )) {
                  this.boardingCooldown = 30;
                  this.setLaunched(false);
                  this.ejectPassengers();
                  AMCompat.startRiding(this, player, true);
                  if (!this.level().isClientSide()) {
                     AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getId(), player.getId()));
                  }

                  return InteractionResult.SUCCESS;
               }

               InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
               if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS) {
                  this.setCommand((this.getCommand() + 1) % 3);
                  if (this.getCommand() == 3) {
                     this.setCommand(0);
                  }

                  player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
                  boolean sit = this.getCommand() == 2;
                  if (sit) {
                     this.setOrderedToSit(true);
                     return InteractionResult.SUCCESS;
                  }

                  this.setOrderedToSit(false);
                  return InteractionResult.SUCCESS;
               }
            }
         }

         return type;
      }
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1 && !this.isLaunched();
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (!this.isPassenger() || entity.isAlive() && this.isAlive()) {
         if (this.isTame() && entity instanceof LivingEntity && this.isOwnedBy((LivingEntity)entity)) {
            this.setDeltaMovement(0.0, 0.0, 0.0);
            this.tick();
            if (this.isPassenger()) {
               Entity mount = this.getVehicle();
               if (mount instanceof Player) {
                  float yawAdd = 0.0F;
                  if (((Player)mount).getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).getMainArm() == HumanoidArm.LEFT ? 135.0F : -135.0F;
                  } else if (((Player)mount).getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).getMainArm() == HumanoidArm.LEFT ? -135.0F : 135.0F;
                  } else {
                     this.setCommand(2);
                     this.setOrderedToSit(true);
                     this.removeVehicle();
                     this.copyPosition(mount);
                  }

                  float birdYaw = yawAdd * 0.5F;
                  this.yBodyRot = Mth.wrapDegrees(((LivingEntity)mount).yBodyRot + birdYaw);
                  this.setYRot(Mth.wrapDegrees(mount.getYRot() + birdYaw));
                  this.yHeadRot = Mth.wrapDegrees(((LivingEntity)mount).yHeadRot + birdYaw);
                  float radius = 0.6F;
                  float angle = 0.017453292F * (((LivingEntity)mount).yBodyRot - 180.0F + yawAdd);
                  double extraX = radius * Mth.sin(3.1415927F + angle);
                  double extraZ = radius * Mth.cos(angle);
                  this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() * 0.45F, mount.getY()), mount.getZ() + extraZ);
               }

               if (!mount.isAlive()) {
                  this.removeVehicle();
               }
            }
         } else {
            super.rideTick();
         }
      } else {
         this.stopRiding();
      }
   }

   public void tick() {
      super.tick();
      this.prevAttackProgress = this.attackProgress;
      this.prevBirdPitch = this.birdPitch;
      this.prevTackleProgress = this.tackleProgress;
      this.prevFlyProgress = this.flyProgress;
      this.prevFlapAmount = this.flapAmount;
      this.prevSwoopProgress = this.swoopProgress;
      this.prevSitProgress = this.sitProgress;
      float yMot = -((float)this.getDeltaMovement().y * 57.295776F);
      this.birdPitch = yMot;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.isTackling()) {
         if (this.tackleProgress < 5.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      boolean sit = this.isSitting() || this.isPassenger();
      if (sit) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isLaunched()) {
         this.launchTime++;
      } else {
         this.launchTime = 0;
      }

      if (this.lastPlayerControlTime > 0) {
         this.lastPlayerControlTime--;
      }

      if (this.lastPlayerControlTime <= 0) {
         this.controlledFlag = false;
      }

      if (yMot < 0.1F) {
         this.flapAmount = Math.min(-yMot * 0.2F, 1.0F);
         if (this.swoopProgress > 0.0F) {
            this.swoopProgress--;
         }
      } else {
         if (this.flapAmount > 0.0F) {
            this.flapAmount = this.flapAmount - Math.min(this.flapAmount, 0.1F);
         } else {
            this.flapAmount = 0.0F;
         }

         if (this.swoopProgress < yMot * 0.2F) {
            this.swoopProgress = Math.min(yMot * 0.2F, this.swoopProgress + 1.0F);
         }
      }

      if (this.isTackling()) {
         this.flapAmount = Math.min(2.0F, this.flapAmount + 0.2F);
      }

      if (!this.level().isClientSide()) {
         if (this.isFlying()) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.tackleCapCooldown == 0 && this.isTackling() && !this.isVehicle() && (this.getTarget() == null || !this.getTarget().isAlive())) {
            this.setTackling(false);
         }

         if (!this.isFlying()) {
            this.timeFlying = 0;
            this.setNoGravity(false);
         } else {
            this.timeFlying++;
            this.setNoGravity(true);
            if ((this.isSitting() || this.isPassenger() || this.isInLove()) && !this.isLaunched()) {
               this.setFlying(false);
            }

            if (this.getTarget() != null && this.getTarget().getY() < this.getX() && !this.isVehicle()) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
            }
         }

         if (this.isInWaterOrBubble() && this.isVehicle()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.10000000149011612, 0.0));
         }

         if (this.isSitting() && !this.isLaunched()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.10000000149011612, 0.0));
         }

         if (this.getTarget() != null && this.isInWaterOrBubble()) {
            this.timeFlying = 0;
            this.setFlying(true);
         }

         if (this.onGround() && this.timeFlying > 30 && this.isFlying() && !this.isInWaterOrBubble()) {
            this.setFlying(false);
         }
      }

      int attackTick = (Integer)this.entityData.get(ATTACK_TICK);
      if (attackTick > 0) {
         if (attackTick == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + 2.0) {
            this.getTarget().hurt(this.damageSources().mobAttack(this), 2.0F);
         }

         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (this.isPassenger()) {
         this.setFlying(false);
         this.setTackling(false);
      }

      if (this.boardingCooldown > 0) {
         this.boardingCooldown--;
      }

      if (this.returnControlTime > 0) {
         this.returnControlTime--;
      }

      if (this.tackleCapCooldown > 0) {
         this.tackleCapCooldown--;
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.BALD_EAGLE.get(), p_241840_1_);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.getRandom().nextInt(24) - radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getCrowGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 7 + this.getRandom().nextInt(10);
      BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(7) + 4);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   private BlockPos getCrowGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 320 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.getRandom().nextInt(24);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getCrowGround(radialPos);
      if (ground.getY() == -64) {
         return this.position();
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -64 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   private Vec3 getOrbitVec(Vec3 vector3d, float gatheringCircleDist) {
      float angle = 0.017453292F * (float)this.orbitDist * (this.orbitClockwise ? -this.tickCount : this.tickCount);
      double extraX = gatheringCircleDist * Mth.sin(angle);
      double extraZ = gatheringCircleDist * Mth.cos(angle);
      if (this.orbitPos != null) {
         Vec3 pos = new Vec3(this.orbitPos.getX() + extraX, this.orbitPos.getY() + this.random.nextInt(2) - 2, this.orbitPos.getZ() + extraZ);
         if (this.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
            return pos;
         }
      }

      return null;
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -64 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || position.getY() <= -64;
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float radius = 0.3F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = 0.3F * Mth.sin(3.1415927F + angle);
         double extraZ = 0.3F * Mth.cos(angle);
         passenger.setYRot(this.yBodyRot + 90.0F);
         if (passenger instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot + 90.0F;
         }

         float extraY = 0.0F;
         if (passenger instanceof AbstractFish && !passenger.isInWaterOrBubble()) {
            extraY = 0.1F;
         }

         moveFunc.accept(passenger, this.getX() + extraX, this.getY() - 0.30000001192092896 + extraY + passenger.getBbHeight() * 0.3F, this.getZ() + extraZ);
         this.passengerTimer++;
         if (this.isAlive() && this.passengerTimer > 0 && this.passengerTimer % 40 == 0) {
            passenger.hurt(this.damageSources().mobAttack(this), 1.0F);
         }
      }
   }

   public boolean canBeRiddenInWater(Entity rider) {
      return true;
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
   }

   public boolean shouldHoodedReturn() {
      return this.getOwner() == null || this.getOwner().isAlive() && !this.getOwner().isShiftKeyDown()
         ? !this.isAlive() || this.portalProcess != null || this.launchTime > 12000 || this.isRemoved()
         : true;
   }

   public void remove(RemovalReason reason) {
      if (this.lastPlayerControlTime == 0 && !this.isPassenger()) {
         super.remove(reason);
      }
   }

   public void directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over) {
      Entity owner = this.getOwner();
      if (owner != null && this.distanceTo(owner) > 150.0F) {
         this.returnControlTime = 100;
      }

      if (!(Math.abs(this.xo - this.getX()) > 0.10000000149011612)
         && !(Math.abs(this.yo - this.getY()) > 0.10000000149011612)
         && !(Math.abs(this.zo - this.getZ()) > 0.10000000149011612)) {
         this.stillTicksCounter++;
      } else {
         this.stillTicksCounter = 0;
      }

      int stillTPthreshold = AMConfig.falconryTeleportsBack ? 200 : 6000;
      this.setOrderedToSit(false);
      this.setLaunched(true);
      if (owner != null
         && (this.returnControlTime > 0 && AMConfig.falconryTeleportsBack || this.stillTicksCounter > stillTPthreshold && this.distanceTo(owner) > 30.0F)) {
         this.copyPosition(owner);
         this.returnControlTime = 0;
         this.stillTicksCounter = 0;
         this.launchTime = Math.max(this.launchTime, 12000);
      }

      if (!this.level().isClientSide()) {
         if (this.returnControlTime > 0 && owner != null) {
            this.getLookControl().setLookAt(owner, 30.0F, 30.0F);
         } else {
            this.yBodyRot = rotationYaw;
            this.setYRot(rotationYaw);
            this.yHeadRot = rotationYaw;
            this.setXRot(rotationPitch);
         }

         if (rotationPitch < 10.0F && this.onGround()) {
            this.setFlying(true);
         }

         float yawOffset = rotationYaw + 90.0F;
         float rad = 3.0F;
         float speed = 1.2F;
         if (this.returnControlTime > 0) {
            this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + 10.0, owner.getZ(), 1.2000000476837158);
         } else {
            this.getMoveControl()
               .setWantedPosition(
                  this.getX() + 4.5 * Math.cos(yawOffset * 0.017453292F),
                  this.getY() - 3.0 * Math.sin(rotationPitch * 0.017453292F),
                  this.getZ() + 3.0 * Math.sin(yawOffset * 0.017453292F),
                  1.2000000476837158
               );
         }

         if (loadChunk) {
            this.loadChunkOnServer(this.blockPosition());
         }

         this.setLastHurtByMob(null);
         this.setTarget(null);
         if (over == null) {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(3.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            Entity closest = null;

            for (Entity e : list) {
               if (closest == null || this.distanceTo(e) < this.distanceTo(closest)) {
                  closest = e;
               }
            }

            over = closest;
         }
      }

      if (over != null
         && over != owner
         && !this.isAlliedTo(over)
         && this.canFalconryAttack(over)
         && this.tackleCapCooldown == 0
         && this.distanceTo(over) <= over.getBbWidth() + 4.0) {
         this.setTackling(true);
         if (this.distanceTo(over) <= over.getBbWidth() + 2.0) {
            float speedDamage = (float)Math.ceil(Mth.clamp(this.getDeltaMovement().length() + 0.2, 0.0, 1.2) * 3.333);
            over.hurt(this.damageSources().mobAttack(this), 5.0F + speedDamage + this.random.nextInt(2));
            this.tackleCapCooldown = 22;
         }
      }

      this.lastPlayerControlTime = 10;
      this.controlledFlag = true;
   }

   @Override
   public float getHandOffset() {
      return 0.8F;
   }

   private boolean canFalconryAttack(Entity over) {
      return !(over instanceof ItemEntity) && (!(over instanceof LivingEntity) || !this.isOwnedBy((LivingEntity)over));
   }

   public void awardKillScore(LivingEntity entity, int score, DamageSource src) {
      if (this.isLaunched()
         && this.hasCap()
         && this.isTame()
         && this.getOwner() != null
         && this.getOwner() instanceof ServerPlayer
         && this.distanceTo(this.getOwner()) >= 100.0F) {
         AMAdvancementTriggerRegistry.BALD_EAGLE_CHALLENGE.trigger((ServerPlayer)this.getOwner());
      }

      super.awardKillScore(entity, score, src);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow) && this.isLaunched()) {
            amount = (amount + 1.0F) / 4.0F;
         }

         return super.hurt(source, amount);
      }
   }

   public void loadChunkOnServer(BlockPos center) {
      if (!this.level().isClientSide()) {
         ServerLevel serverWorld = (ServerLevel)this.level();

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               ChunkPos pos = new ChunkPos(this.blockPosition().offset(i * 16, 0, j * 16));
               serverWorld.setChunkForced(pos.x, pos.z, true);
            }
         }
      }
   }

   @Override
   public void onLaunch(Player player, Entity pointedEntity) {
      this.setLaunched(true);
      this.setOrderedToSit(false);
      this.setCommand(0);
      if (this.hasCap()) {
         this.setFlying(true);
         this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.10000000149011612);
         if (this.level().isClientSide()) {
            AlexsMobs.sendMSGToServer(new MessageMosquitoDismount(this.getId(), player.getId()));
         }

         AlexsMobs.PROXY.setRenderViewEntity(this);
      } else {
         this.getNavigation().stop();
         this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.10000000149011612);
         if (pointedEntity != null && pointedEntity.isAlive() && !this.isAlliedTo(pointedEntity)) {
            this.setFlying(true);
            if (pointedEntity instanceof LivingEntity pointedLivingEntity) {
               this.setTarget(pointedLivingEntity);
            }
         } else {
            this.setFlying(false);
            this.setCommand(2);
            this.setOrderedToSit(true);
         }
      }
   }

   private class AILandOnGlove extends Goal {
      protected EntityBaldEagle eagle;
      private int seperateTime = 0;

      public AILandOnGlove() {
         this.eagle = EntityBaldEagle.this;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.eagle.isLaunched()
            && !this.eagle.controlledFlag
            && this.eagle.isTame()
            && !this.eagle.isPassenger()
            && !this.eagle.isVehicle()
            && (this.eagle.getTarget() == null || !this.eagle.getTarget().isAlive());
      }

      public void tick() {
         if (this.eagle.getDeltaMovement().lengthSqr() < 0.03) {
            this.seperateTime++;
         }

         LivingEntity owner = this.eagle.getOwner();
         if (owner != null) {
            if (this.seperateTime > 200) {
               this.seperateTime = 0;
               this.eagle.copyPosition(owner);
            }

            this.eagle.setFlying(true);
            double d0 = this.eagle.getX() - owner.getX();
            double d2 = this.eagle.getZ() - owner.getZ();
            double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
            double yAdd = xzDist > 14.0 ? 5.0 : 0.0;
            this.eagle.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + yAdd + owner.getEyeHeight(), owner.getZ(), 1.0);
            if (this.eagle.distanceTo(owner) < owner.getBbWidth() + 1.4) {
               this.eagle.setLaunched(false);
               if (this.eagle.getRidingFalcons(owner) <= 0) {
                  this.eagle.startRiding(owner);
                  if (!this.eagle.level().isClientSide()) {
                     AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.eagle.getId(), owner.getId()));
                  }
               } else {
                  this.eagle.setCommand(2);
                  this.eagle.setOrderedToSit(true);
               }
            }
         }
      }

      public void stop() {
         this.seperateTime = 0;
      }
   }

   private class AITackle extends Goal {
      protected EntityBaldEagle eagle;
      private int circleTime;
      private int maxCircleTime = 10;

      public AITackle() {
         this.eagle = EntityBaldEagle.this;
      }

      public boolean canUse() {
         return this.eagle.getTarget() != null && !this.eagle.controlledFlag && !this.eagle.isVehicle();
      }

      public void start() {
         this.eagle.orbitPos = null;
      }

      public void stop() {
         this.circleTime = 0;
         this.maxCircleTime = 60 + EntityBaldEagle.this.random.nextInt(60);
      }

      public void tick() {
         LivingEntity target = this.eagle.getTarget();
         boolean smallPrey = target != null && target.getBbHeight() < 1.0F && target.getBbWidth() < 0.7F && !(target instanceof EntityBaldEagle)
            || target instanceof AbstractFish;
         if (this.eagle.orbitPos != null && this.circleTime < this.maxCircleTime) {
            this.circleTime++;
            this.eagle.setTackling(false);
            this.eagle.setFlying(true);
            if (target != null) {
               int i = 0;
               int up = 2 + this.eagle.getRandom().nextInt(4);

               for (this.eagle.orbitPos = target.blockPosition().above((int)target.getBbHeight());
                  this.eagle.level().isEmptyBlock(this.eagle.orbitPos) && i < up;
                  this.eagle.orbitPos = this.eagle.orbitPos.above()
               ) {
                  i++;
               }
            }

            Vec3 vec = this.eagle.getOrbitVec(Vec3.ZERO, (float)(4 + EntityBaldEagle.this.random.nextInt(2)));
            if (vec != null) {
               this.eagle.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.2000000476837158);
            }
         } else if (target != null) {
            if (!this.eagle.isFlying() && !this.eagle.isInWaterOrBubble()) {
               this.eagle.getNavigation().moveTo(target, 1.0);
            } else {
               double d0 = this.eagle.getX() - target.getX();
               double d2 = this.eagle.getZ() - target.getZ();
               double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
               double yAddition = target.getBbHeight();
               if (xzDist > 15.0) {
                  yAddition = 3.0;
               }

               this.eagle.setTackling(true);
               this.eagle
                  .getMoveControl()
                  .setWantedPosition(target.getX(), target.getY() + yAddition, target.getZ(), this.eagle.isInWaterOrBubble() ? 1.2999999523162842 : 1.0);
            }

            if (this.eagle.distanceTo(target) < target.getBbWidth() + 2.5F) {
               if (this.eagle.isTackling()) {
                  if (smallPrey) {
                     this.eagle.setFlying(true);
                     this.eagle.timeFlying = 0;
                     float radius = 0.3F;
                     float angle = 0.017453292F * this.eagle.yBodyRot;
                     double extraX = 0.3F * Mth.sin(3.1415927F + angle);
                     double extraZ = 0.3F * Mth.cos(angle);
                     target.setYRot(this.eagle.yBodyRot + 90.0F);
                     if (target instanceof LivingEntity) {
                        target.yBodyRot = this.eagle.yBodyRot + 90.0F;
                     }

                     target.setPos(
                        this.eagle.getX() + extraX, this.eagle.getY() - 0.4000000059604645 + target.getBbHeight() * 0.45F, this.eagle.getZ() + extraZ
                     );
                     AMCompat.startRiding(target, this.eagle, true);
                  } else {
                     target.hurt(this.eagle.damageSources().mobAttack(this.eagle), 5.0F);
                     this.eagle.setFlying(false);
                     this.eagle.orbitPos = target.blockPosition().above(2);
                     this.circleTime = 0;
                     this.maxCircleTime = 60 + EntityBaldEagle.this.random.nextInt(60);
                  }
               } else {
                  AMCompat.doHurtTarget(this.eagle, target);
               }
            } else if (this.eagle.distanceTo(target) > 12.0F || target.isInWaterOrBubble()) {
               this.eagle.setFlying(true);
            }
         }

         if (this.eagle.isLaunched()) {
            this.eagle.setFlying(true);
         }
      }
   }

   private class AIWanderIdle extends Goal {
      protected final EntityBaldEagle eagle;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;
      private int orbitResetCooldown = 0;
      private int maxOrbitTime = 360;
      private int orbitTime = 0;

      public AIWanderIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.eagle = EntityBaldEagle.this;
      }

      public boolean canUse() {
         if (this.orbitResetCooldown < 0) {
            this.orbitResetCooldown++;
         }

         if ((this.eagle.getTarget() == null || !this.eagle.getTarget().isAlive() || this.eagle.isVehicle())
            && !this.eagle.isPassenger()
            && !this.eagle.isSitting()
            && !this.eagle.controlledFlag) {
            if (this.eagle.getRandom().nextInt(15) != 0 && !this.eagle.isFlying()) {
               return false;
            } else {
               if (this.eagle.isBaby()) {
                  this.flightTarget = false;
               } else if (this.eagle.isInWaterOrBubble()) {
                  this.flightTarget = true;
               } else if (this.eagle.hasCap()) {
                  this.flightTarget = false;
               } else if (this.eagle.onGround()) {
                  this.flightTarget = EntityBaldEagle.this.random.nextBoolean();
               } else {
                  if (this.orbitResetCooldown == 0 && EntityBaldEagle.this.random.nextInt(6) == 0) {
                     this.orbitResetCooldown = 400;
                     this.eagle.orbitPos = this.eagle.blockPosition();
                     this.eagle.orbitDist = 4 + EntityBaldEagle.this.random.nextInt(5);
                     this.eagle.orbitClockwise = EntityBaldEagle.this.random.nextBoolean();
                     this.orbitTime = 0;
                     this.maxOrbitTime = (int)(360.0F + 360.0F * EntityBaldEagle.this.random.nextFloat());
                  }

                  this.flightTarget = this.eagle.isVehicle() || EntityBaldEagle.this.random.nextInt(7) > 0 && this.eagle.timeFlying < 700;
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
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.orbitResetCooldown > 0) {
            this.orbitResetCooldown--;
         }

         if (this.orbitResetCooldown < 0) {
            this.orbitResetCooldown++;
         }

         if (this.orbitResetCooldown > 0 && this.eagle.orbitPos != null) {
            if (this.orbitTime < this.maxOrbitTime && !this.eagle.isInWaterOrBubble()) {
               this.orbitTime++;
            } else {
               this.orbitTime = 0;
               this.eagle.orbitPos = null;
               this.orbitResetCooldown = -400 - EntityBaldEagle.this.random.nextInt(400);
            }
         }

         if (this.eagle.horizontalCollision && !this.eagle.onGround()) {
            this.stop();
         }

         if (this.flightTarget) {
            this.eagle.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else if (this.eagle.onGround() || !this.eagle.isFlying()) {
            this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         } else if (!this.eagle.isInWaterOrBubble()) {
            this.eagle.setDeltaMovement(this.eagle.getDeltaMovement().multiply(1.2000000476837158, 0.6000000238418579, 1.2000000476837158));
         }

         if (!this.flightTarget && this.eagle.onGround() && EntityBaldEagle.this.isFlying()) {
            this.eagle.setFlying(false);
            this.orbitTime = 0;
            this.eagle.orbitPos = null;
            this.orbitResetCooldown = -400 - EntityBaldEagle.this.random.nextInt(400);
         }

         if (this.eagle.timeFlying > 30
            && EntityBaldEagle.this.isFlying()
            && (!EntityBaldEagle.this.level().isEmptyBlock(this.eagle.getBlockPosBelowThatAffectsMyMovement()) || this.eagle.onGround())
            && !this.eagle.isInWaterOrBubble()) {
            this.eagle.setFlying(false);
            this.orbitTime = 0;
            this.eagle.orbitPos = null;
            this.orbitResetCooldown = -400 - EntityBaldEagle.this.random.nextInt(400);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.eagle.position();
         if (this.eagle.isTame() && this.eagle.getCommand() == 1 && this.eagle.getOwner() != null) {
            vector3d = this.eagle.getOwner().position();
            this.eagle.orbitPos = this.eagle.getOwner().blockPosition();
         }

         if (this.orbitResetCooldown > 0 && this.eagle.orbitPos != null) {
            return this.eagle.getOrbitVec(vector3d, (float)(4 + EntityBaldEagle.this.random.nextInt(2)));
         } else {
            if (this.eagle.isVehicle() || this.eagle.isOverWaterOrVoid()) {
               this.flightTarget = true;
            }

            if (this.flightTarget) {
               return this.eagle.timeFlying >= 500 && !this.eagle.isVehicle() && !this.eagle.isOverWaterOrVoid()
                  ? this.eagle.getBlockGrounding(vector3d)
                  : this.eagle.getBlockInViewAway(vector3d, 0.0F);
            } else {
               return LandRandomPos.getPos(this.eagle, 10, 7);
            }
         }
      }

      public boolean canContinueToUse() {
         if (this.eagle.isSitting()) {
            return false;
         } else {
            return this.flightTarget
               ? this.eagle.isFlying() && this.eagle.distanceToSqr(this.x, this.y, this.z) > 2.0
               : !this.eagle.getNavigation().isDone() && !this.eagle.isVehicle();
         }
      }

      public void start() {
         if (this.flightTarget) {
            this.eagle.setFlying(true);
            this.eagle.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.eagle.getNavigation().stop();
         super.stop();
      }
   }

   static class MoveHelper extends MoveControl {
      private final EntityBaldEagle parentEntity;

      public MoveHelper(EntityBaldEagle bird) {
         super(bird);
         this.parentEntity = bird;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
            double d5 = vector3d.length();
            if (d5 < 0.3) {
               this.operation = Operation.WAIT;
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
            } else {
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05 / d5)));
               Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            }
         }
      }

      private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
         AABB axisalignedbb = this.parentEntity.getBoundingBox();

         for (int i = 1; i < p_220673_2_; i++) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }
}
