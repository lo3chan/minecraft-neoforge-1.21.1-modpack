package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.CrowAICircleCrops;
import com.github.alexthe666.alexsmobs.entity.ai.CrowAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.CrowAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.message.MessageCrowDismount;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMItemHandlers;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class EntityCrow extends TamableAnimal implements ITargetsDroppedItems {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityCrow.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityCrow.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCrow.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCrow.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(
      EntityCrow.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   public float prevFlyProgress;
   public float flyProgress;
   public float prevAttackProgress;
   public float attackProgress;
   public int fleePumpkinFlag = 0;
   public boolean aiItemFlag = false;
   public boolean aiItemFrameFlag = false;
   public float prevSitProgress;
   public float sitProgress;
   private boolean isLandNavigator;
   private int timeFlying = 0;
   @Nullable
   private UUID seedThrowerID;
   private int heldItemTime = 0;
   private int checkPerchCooldown = 0;
   private final boolean gatheringClockwise = false;

   protected EntityCrow(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
      this.switchNavigator(false);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new CrowAIMelee(this));
      this.goalSelector.addGoal(3, new CrowAIFollowOwner(this, 1.0, 4.0F, 2.0F, true));
      this.goalSelector.addGoal(4, new EntityCrow.AIDepositChests());
      this.goalSelector.addGoal(4, new EntityCrow.AIScatter());
      this.goalSelector.addGoal(5, new EntityCrow.AIAvoidPumpkins());
      this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(6, new CrowAICircleCrops(this));
      this.goalSelector.addGoal(7, new EntityCrow.AIWalkIdle());
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EntityCrow.AITargetItems(this, false, false, 40, 16));
      this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(4, new HurtByTargetGoal(this, new Class[]{Player.class}).setAlertOthers(new Class[0]));
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.crowSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static <T extends Mob> boolean canCrowSpawn(
      EntityType<EntityCrow> crow, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return isBrightEnoughToSpawn(worldIn, pos);
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

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 0.7F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 4.0F;
         }

         if (this.isPassenger()) {
            this.stopRiding();
         }

         boolean prev = super.hurt(source, amount);
         if (prev && !this.getMainHandItem().isEmpty()) {
            AMCompat.spawnAtLocation(this, this.getMainHandItem().copy());
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         }

         return prev;
      }
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.stopRiding();
      } else if (this.isTame() && entity instanceof LivingEntity && this.isOwnedBy((LivingEntity)entity)) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         Entity riding = this.getVehicle();
         if (this.isPassenger()) {
            int i = riding.getPassengers().indexOf(this);
            float radius = 0.43F;
            float angle = 0.017453292F * (((Player)riding).yBodyRot + (i == 0 ? -90 : 90));
            double extraX = 0.43F * Mth.sin(3.1415927F + angle);
            double extraZ = 0.43F * Mth.cos(angle);
            double extraY = riding.isShiftKeyDown() ? 1.25 : 1.45;
            this.yHeadRot = ((Player)riding).yHeadRot;
            this.yRotO = ((Player)riding).yHeadRot;
            this.setPos(riding.getX() + extraX, riding.getY() + extraY, riding.getZ() + extraZ);
            if (!riding.isAlive()
               || this.boardingCooldown == 0 && riding.isShiftKeyDown()
               || ((Player)riding).isFallFlying()
               || this.getTarget() != null && this.getTarget().isAlive()) {
               this.removeVehicle();
               if (!this.level().isClientSide()) {
                  AlexsMobs.sendMSGToAll(new MessageCrowDismount(this.getId(), riding.getId()));
               }
            }
         }
      } else {
         super.rideTick();
      }
   }

   public boolean canBoardOwner(LivingEntity owner) {
      return !owner.isShiftKeyDown() && this.boardingCooldown <= 0;
   }

   public int getRidingCrows(LivingEntity player) {
      int crowCount = 0;

      for (Entity e : player.getPassengers()) {
         if (e instanceof EntityCrow) {
            crowCount++;
         }
      }

      return crowCount;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.CROW_BREEDABLES) && this.isTame();
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.getMainHandItem().isEmpty() && type != InteractionResult.SUCCESS) {
         AMCompat.spawnAtLocation(this, this.getMainHandItem().copy());
         this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.isTame()
            && this.isOwnedBy(player)
            && !this.isFood(itemstack)) {
            if (this.isCrowEdible(itemstack) && this.getMainHandItem().isEmpty()) {
               ItemStack cop = itemstack.copy();
               cop.setCount(1);
               this.setItemInHand(InteractionHand.MAIN_HAND, cop);
               itemstack.shrink(1);
            }

            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 4) {
               this.setCommand(0);
            }

            if (this.getCommand() == 3) {
               player.displayClientMessage(Component.translatable("entity.alexsmobs.crow.command_3", new Object[]{this.getName()}), true);
            } else {
               player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
            }

            boolean sit = this.getCommand() == 2;
            this.setOrderedToSit(sit);
            return InteractionResult.SUCCESS;
         } else {
            return super.mobInteract(player, hand);
         }
      }
   }

   public void tick() {
      super.tick();
      this.prevAttackProgress = this.attackProgress;
      this.prevFlyProgress = this.flyProgress;
      this.prevSitProgress = this.sitProgress;
      boolean isSittingOrPassenger = this.isSitting() || this.isPassenger();
      if (isSittingOrPassenger) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isFlying() && !isSittingOrPassenger) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.fleePumpkinFlag > 0) {
         this.fleePumpkinFlag--;
      }

      if (!this.level().isClientSide()) {
         boolean isFlying = this.isFlying();
         if (isFlying && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!isFlying && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (isFlying) {
            this.timeFlying++;
            this.setNoGravity(true);
            if (this.isSitting() || this.isPassenger() || this.isInLove()) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
            this.setNoGravity(false);
         }
      }

      if (!this.getMainHandItem().isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 60 && this.isCrowEdible(this.getMainHandItem()) && (!this.isTame() || this.getHealth() < this.getMaxHealth())) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (this.seedThrowerID != null && this.getMainHandItem().is(AMTagRegistry.CROW_TAMEABLES) && !this.isTame()) {
               if (this.getRandom().nextFloat() < 0.3F) {
                  AMCompat.setTame(this, true);
                  this.setCommand(1);
                  AMCompat.setOwnerUUID(this, this.seedThrowerID);
                  if (this.level().getPlayerByUUID(this.seedThrowerID) instanceof ServerPlayer serverPlayer) {
                     CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, this);
                  }

                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            this.getMainHandItem().shrink(1);
         }
      } else {
         this.heldItemTime = 0;
      }

      if (this.boardingCooldown > 0) {
         this.boardingCooldown--;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (this.checkPerchCooldown > 0) {
         this.checkPerchCooldown--;
      }

      if (this.isTame()) {
         if (this.checkPerchCooldown == 0) {
            this.checkPerchCooldown = 50;
            BlockState below = this.getBlockStateOn();
            if (below.is(AMTagRegistry.CROW_HOME_BLOCKS)) {
               this.heal(1.0F);
               this.level().broadcastEntityEvent(this, (byte)67);
               this.setPerchPos(this.getBlockPosBelowThatAffectsMyMovement());
            }
         }

         if (this.getCommand() == 3 && this.getPerchPos() != null && this.checkPerchCooldown == 0) {
            this.checkPerchCooldown = 120;
            BlockState below = this.level().getBlockState(this.getPerchPos());
            if (below.is(AMTagRegistry.CROW_HOME_BLOCKS)) {
               this.level().broadcastEntityEvent(this, (byte)68);
               this.setPerchPos(null);
               this.setCommand(2);
               this.setOrderedToSit(true);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 67) {
         for (int i = 0; i < 7; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
         }
      } else if (id == 68) {
         for (int i = 0; i < 7; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("MonkeySitting", this.isSitting());
      compound.putInt("Command", this.getCommand());
      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      if (this.isInWater() && this.getDeltaMovement().y > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
      }

      super.travel(vec3d);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "MonkeySitting"));
      this.setCommand(AMCompat.getInt(compound, "Command"));
      if (AMCompat.contains(compound, "PerchX") && AMCompat.contains(compound, "PerchY") && AMCompat.contains(compound, "PerchZ")) {
         this.setPerchPos(new BlockPos(AMCompat.getInt(compound, "PerchX"), AMCompat.getInt(compound, "PerchY"), AMCompat.getInt(compound, "PerchZ")));
      }
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      if (!flying || !this.isBaby() && !this.isPassenger()) {
         this.entityData.set(FLYING, flying);
      }
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

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(ATTACK_TICK, 0);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
      builder.define(PERCH_POS, Optional.empty());
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || super.isInvulnerableTo(source);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.CROW.get(), serverWorld);
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public int getAmbientSoundInterval() {
      return 60;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.CROW_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.CROW_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.CROW_HURT.get();
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.450001F - this.getRandom().nextInt(24) - radiusAdd;
      float angle = this.getAngle1();
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getCrowGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      BlockPos newPos;
      if (distFromGround > 8) {
         int flightHeight = 4 + this.getRandom().nextInt(10);
         newPos = ground.above(flightHeight);
      } else {
         newPos = ground.above(this.getRandom().nextInt(6) + 1);
      }

      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   private BlockPos getCrowGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.450001F - this.getRandom().nextInt(24);
      float angle = this.getAngle1();
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), (int)this.getY(), (int)(fleePos.z() + extraZ));
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

   private float getAngle1() {
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      return 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
   }

   private boolean isOverWater() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -64 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty();
   }

   @Override
   public void peck() {
      this.entityData.set(ATTACK_TICK, 7);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack != null && this.isCrowEdible(stack) || this.isTame();
   }

   private boolean isCrowEdible(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) || stack.is(AMTagRegistry.CROW_FOODSTUFFS);
   }

   @Override
   public double getMaxDistToItem() {
      return 1.0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
      Entity itemThrower = e.getOwner();
      if (e.getItem().is(AMTagRegistry.CROW_TAMEABLES) && !this.isTame() && itemThrower != null) {
         this.seedThrowerID = itemThrower.getUUID();
      } else {
         this.seedThrowerID = null;
      }
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.entityData.get(PERCH_POS)).orElse(null);
   }

   public void setPerchPos(BlockPos pos) {
      this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
   }

   private Vec3 getGatheringVec(Vec3 vector3d, float gatheringCircleDist) {
      if (this.getPerchPos() != null) {
         float angle = 0.13962634F * this.tickCount;
         double extraX = gatheringCircleDist * Mth.sin(angle);
         double extraZ = gatheringCircleDist * Mth.cos(angle);
         Vec3 pos = new Vec3(this.getPerchPos().getX() + extraX, this.getPerchPos().getY() + 2, this.getPerchPos().getZ() + extraZ);
         if (this.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
            return pos;
         }
      }

      return null;
   }

   private class AIAvoidPumpkins extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      protected int runDelay = 70;
      private Vec3 flightTarget;

      private AIAvoidPumpkins() {
         this.searchLength = 20;
         this.verticalSearchRange = 1;
      }

      public boolean canContinueToUse() {
         return this.destinationBlock != null && this.isPumpkin(EntityCrow.this.level(), this.destinationBlock.mutable()) && this.isCloseToPumpkin(16.0);
      }

      public boolean isCloseToPumpkin(double dist) {
         return this.destinationBlock == null || EntityCrow.this.distanceToSqr(Vec3.atCenterOf(this.destinationBlock)) < dist * dist;
      }

      public boolean canUse() {
         if (EntityCrow.this.isTame()) {
            return false;
         } else if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 70 + EntityCrow.this.random.nextInt(150);
            return this.searchForDestination();
         }
      }

      public void start() {
         EntityCrow.this.fleePumpkinFlag = 200;
         Vec3 vec = EntityCrow.this.getBlockInViewAway(Vec3.atCenterOf(this.destinationBlock), 10.0F);
         if (vec != null) {
            this.flightTarget = vec;
            EntityCrow.this.setFlying(true);
            EntityCrow.this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.0);
         }
      }

      public void tick() {
         if (this.isCloseToPumpkin(16.0)) {
            EntityCrow.this.fleePumpkinFlag = 200;
            if (this.flightTarget == null || EntityCrow.this.distanceToSqr(this.flightTarget) < 2.0) {
               Vec3 vec = EntityCrow.this.getBlockInViewAway(Vec3.atCenterOf(this.destinationBlock), 10.0F);
               if (vec != null) {
                  this.flightTarget = vec;
                  EntityCrow.this.setFlying(true);
               }
            }

            if (this.flightTarget != null) {
               EntityCrow.this.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0);
            }
         }
      }

      public void stop() {
         this.flightTarget = null;
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         BlockPos lvt_3_1_ = EntityCrow.this.blockPosition();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isPumpkin(EntityCrow.this.level(), lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isPumpkin(Level world, MutableBlockPos lvt_4_1_) {
         return world.getBlockState(lvt_4_1_).is(AMTagRegistry.CROW_FEARS);
      }
   }

   private class AIDepositChests extends Goal {
      protected final EntityCrow.AIDepositChests.Sorter theNearestAttackableTargetSorter;
      protected final Predicate<ItemFrame> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private ItemFrame targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIDepositChests() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityCrow.AIDepositChests.Sorter(EntityCrow.this);
         this.targetEntitySelector = new Predicate<ItemFrame>() {
            public boolean apply(@Nullable ItemFrame e) {
               BlockPos hangingPosition = e.getPos().relative(e.getDirection().getOpposite());
               BlockEntity entity = e.level().getBlockEntity(hangingPosition);
               if (entity != null) {
                  IItemHandler handler = AMItemHandlers.find(entity, e.getDirection().getOpposite());
                  if (handler != null) {
                     return ItemStack.isSameItem(e.getItem(), EntityCrow.this.getMainHandItem());
                  }
               }

               return false;
            }
         };
      }

      public boolean canUse() {
         if (EntityCrow.this.isPassenger()
            || EntityCrow.this.aiItemFlag
            || EntityCrow.this.isVehicle()
            || EntityCrow.this.isSitting()
            || EntityCrow.this.getCommand() != 3) {
            return false;
         } else if (EntityCrow.this.getMainHandItem().isEmpty()) {
            return false;
         } else {
            if (!this.mustUpdate) {
               long worldTime = EntityCrow.this.level().getGameTime() % 10L;
               if (worldTime != 0L) {
                  if (EntityCrow.this.getNoActionTime() >= 100) {
                     return false;
                  }

                  if (EntityCrow.this.getRandom().nextInt(this.executionChance) != 0) {
                     return false;
                  }
               }
            }

            List<ItemFrame> list = EntityCrow.this.level()
               .getEntitiesOfClass(ItemFrame.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               list.sort(this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               EntityCrow.this.aiItemFrameFlag = true;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null && EntityCrow.this.getCommand() == 3 && !EntityCrow.this.getMainHandItem().isEmpty();
      }

      public void stop() {
         this.flightTarget = null;
         this.targetEntity = null;
         EntityCrow.this.aiItemFrameFlag = false;
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.flightTarget != null) {
            EntityCrow.this.setFlying(true);
            if (EntityCrow.this.horizontalCollision) {
               EntityCrow.this.getMoveControl().setWantedPosition(this.flightTarget.x, EntityCrow.this.getY() + 1.0, this.flightTarget.z, 1.0);
            } else {
               EntityCrow.this.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0);
            }
         }

         if (this.targetEntity != null) {
            this.flightTarget = this.targetEntity.position();
            if (EntityCrow.this.distanceTo(this.targetEntity) < 2.0F) {
               try {
                  BlockPos hangingPosition = this.targetEntity.getPos().relative(this.targetEntity.getDirection().getOpposite());
                  BlockEntity entity = this.targetEntity.level().getBlockEntity(hangingPosition);
                  Direction deposit = this.targetEntity.getDirection();
                  IItemHandler handler = AMItemHandlers.find(entity, deposit);
                  if (handler != null && this.cooldown == 0) {
                     ItemStack duplicate = EntityCrow.this.getItemInHand(InteractionHand.MAIN_HAND).copy();
                     ItemStack insertSimulate = ItemHandlerHelper.insertItem(handler, duplicate, true);
                     if (!insertSimulate.equals(duplicate)) {
                        ItemStack shrunkenStack = ItemHandlerHelper.insertItem(handler, duplicate, false);
                        if (shrunkenStack.isEmpty()) {
                           EntityCrow.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        } else {
                           EntityCrow.this.setItemInHand(InteractionHand.MAIN_HAND, shrunkenStack);
                        }

                        EntityCrow.this.peck();
                     } else {
                        this.cooldown = 20;
                     }
                  }
               } catch (Exception var8) {
               }

               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityCrow.this.getX(), EntityCrow.this.getY(), EntityCrow.this.getZ());
         AABB aabb = new AABB(-16.0, -16.0, -16.0, 16.0, 16.0, 16.0);
         return aabb.move(renderCenter);
      }

      public record Sorter(Entity theEntity) implements Comparator<Entity> {
         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   private class AIScatter extends Goal {
      protected final EntityCrow.AIScatter.Sorter theNearestAttackableTargetSorter;
      protected final Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIScatter() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityCrow.AIScatter.Sorter(EntityCrow.this);
         this.targetEntitySelector = new Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive() && e.getType().builtInRegistryHolder().is(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player)e).isCreative();
            }
         };
      }

      public boolean canUse() {
         if (!EntityCrow.this.isPassenger() && !EntityCrow.this.aiItemFlag && !EntityCrow.this.isVehicle() && !EntityCrow.this.isTame()) {
            if (!this.mustUpdate) {
               long worldTime = EntityCrow.this.level().getGameTime() % 10L;
               if (worldTime != 0L) {
                  if (EntityCrow.this.getNoActionTime() >= 100) {
                     return false;
                  }

                  if (EntityCrow.this.getRandom().nextInt(this.executionChance) != 0) {
                     return false;
                  }
               }
            }

            List<Entity> list = EntityCrow.this.level()
               .getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               list.sort(this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null && !EntityCrow.this.isTame();
      }

      public void stop() {
         this.flightTarget = null;
         this.targetEntity = null;
      }

      public void tick() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.flightTarget != null) {
            EntityCrow.this.setFlying(true);
            EntityCrow.this.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0);
            if (this.cooldown == 0 && EntityCrow.this.isTargetBlocked(this.flightTarget)) {
               this.cooldown = 30;
               this.flightTarget = null;
            }
         }

         if (this.targetEntity != null) {
            if (EntityCrow.this.onGround() || this.flightTarget == null || EntityCrow.this.distanceToSqr(this.flightTarget) < 3.0) {
               Vec3 vec = EntityCrow.this.getBlockInViewAway(this.targetEntity.position(), 0.0F);
               if (vec != null && vec.y() > EntityCrow.this.getY()) {
                  this.flightTarget = vec;
               }
            }

            if (EntityCrow.this.distanceTo(this.targetEntity) > 20.0F) {
               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityCrow.this.getX(), EntityCrow.this.getY() + 0.5, EntityCrow.this.getZ());
         AABB aabb = new AABB(-2.0, -2.0, -2.0, 2.0, 2.0, 2.0);
         return aabb.move(renderCenter);
      }

      public record Sorter(Entity theEntity) implements Comparator<Entity> {
         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   private static class AITargetItems extends CreatureAITargetItems {
      public AITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
         super(creature, checkSight, onlyNearby, tickThreshold, radius);
         this.executionChance = 1;
      }

      @Override
      public void stop() {
         super.stop();
         ((EntityCrow)this.mob).aiItemFlag = false;
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !((EntityCrow)this.mob).isSitting() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && !((EntityCrow)this.mob).isSitting() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      protected void moveTo() {
         EntityCrow crow = (EntityCrow)this.mob;
         if (this.targetEntity != null) {
            crow.aiItemFlag = true;
            if (this.mob.distanceTo(this.targetEntity) < 2.0F) {
               crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
               crow.peck();
            }

            if (!(this.mob.distanceTo(this.targetEntity) > 8.0F) && !crow.isFlying()) {
               this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
            } else {
               crow.setFlying(true);
               if (!crow.hasLineOfSight(this.targetEntity)) {
                  crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1.0 + crow.getY(), this.targetEntity.getZ(), 1.0);
               } else {
                  float f = (float)(crow.getX() - this.targetEntity.getX());
                  float f2 = (float)(crow.getZ() - this.targetEntity.getZ());
                  float xzDist = Mth.sqrt(f * f + f2 * f2);
                  float f1 = xzDist < 5.0F ? 0.0F : 1.8F;
                  crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
               }
            }
         }
      }

      @Override
      public void tick() {
         super.tick();
         this.moveTo();
      }
   }

   private class AIWalkIdle extends Goal {
      protected final EntityCrow crow;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.crow = EntityCrow.this;
      }

      public boolean canUse() {
         if (this.crow.isVehicle()
            || EntityCrow.this.getCommand() == 1
            || EntityCrow.this.aiItemFlag
            || this.crow.getTarget() != null && this.crow.getTarget().isAlive()
            || this.crow.isPassenger()
            || this.crow.isSitting()) {
            return false;
         } else if (this.crow.getRandom().nextInt(30) != 0 && !this.crow.isFlying()) {
            return false;
         } else {
            if (this.crow.onGround()) {
               this.flightTarget = EntityCrow.this.random.nextBoolean();
            } else {
               this.flightTarget = EntityCrow.this.random.nextInt(5) > 0 && this.crow.timeFlying < 200;
            }

            if (this.crow.getCommand() == 3) {
               if (this.crow.aiItemFrameFlag) {
                  return false;
               }

               this.flightTarget = true;
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
            this.crow.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.crow.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
            if (EntityCrow.this.isFlying() && this.crow.onGround()) {
               this.crow.setFlying(false);
            }
         }

         if (EntityCrow.this.isFlying() && this.crow.onGround() && this.crow.timeFlying > 10) {
            this.crow.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.crow.position();
         if (this.crow.getCommand() == 3 && this.crow.getPerchPos() != null) {
            return this.crow.getGatheringVec(vector3d, (float)(4 + EntityCrow.this.random.nextInt(2)));
         } else {
            if (this.crow.isOverWater()) {
               this.flightTarget = true;
            }

            if (this.flightTarget) {
               return this.crow.timeFlying >= 50 && !this.crow.isOverWater()
                  ? this.crow.getBlockGrounding(vector3d)
                  : this.crow.getBlockInViewAway(vector3d, 0.0F);
            } else {
               return LandRandomPos.getPos(this.crow, 10, 7);
            }
         }
      }

      public boolean canContinueToUse() {
         if (this.crow.aiItemFlag || this.crow.isSitting() || EntityCrow.this.getCommand() == 1) {
            return false;
         } else {
            return this.flightTarget
               ? this.crow.isFlying() && this.crow.distanceToSqr(this.x, this.y, this.z) > 2.0
               : !this.crow.getNavigation().isDone() && !this.crow.isVehicle();
         }
      }

      public void start() {
         if (this.flightTarget) {
            this.crow.setFlying(true);
            this.crow.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.crow.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.crow.getNavigation().stop();
         super.stop();
      }
   }
}
