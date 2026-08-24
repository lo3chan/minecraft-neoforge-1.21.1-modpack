package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIRevealTreasure;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapDecorations.Entry;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntitySeagull extends Animal implements ITargetsDroppedItems {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> FLIGHT_LOOK_YAW = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> TREASURE_POS = SynchedEntityData.defineId(
      EntitySeagull.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   public float prevFlyProgress;
   public float flyProgress;
   public float prevFlapAmount;
   public float flapAmount;
   public boolean aiItemFlag = false;
   public float attackProgress;
   public float prevAttackProgress;
   public float sitProgress;
   public float prevSitProgress;
   public int stealCooldown = this.random.nextInt(2500);
   private boolean isLandNavigator;
   private int timeFlying;
   private BlockPos orbitPos = null;
   private double orbitDist = 5.0;
   private boolean orbitClockwise = false;
   private boolean fallFlag = false;
   private int flightLookCooldown = 0;
   private float targetFlightLookYaw;
   private int heldItemTime = 0;
   public int treasureSitTime;
   public UUID feederUUID = null;

   protected EntitySeagull(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
      this.switchNavigator(false);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SEAGULL_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SEAGULL_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SEAGULL_HURT.get();
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("Sitting", this.isSitting());
      compound.putInt("StealCooldown", this.stealCooldown);
      compound.putInt("TreasureSitTime", this.treasureSitTime);
      if (this.feederUUID != null) {
         AMCompat.putUUID(compound, "FeederUUID", this.feederUUID);
      }

      if (this.getTreasurePos() != null) {
         compound.putInt("TresX", this.getTreasurePos().getX());
         compound.putInt("TresY", this.getTreasurePos().getY());
         compound.putInt("TresZ", this.getTreasurePos().getZ());
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setSitting(AMCompat.getBoolean(compound, "Sitting"));
      this.stealCooldown = AMCompat.getInt(compound, "StealCooldown");
      this.treasureSitTime = AMCompat.getInt(compound, "TreasureSitTime");
      if (AMCompat.hasUUID(compound, "FeederUUID")) {
         this.feederUUID = AMCompat.getUUID(compound, "FeederUUID");
      }

      if (AMCompat.contains(compound, "TresX") && AMCompat.contains(compound, "TresY") && AMCompat.contains(compound, "TresZ")) {
         this.setTreasurePos(new BlockPos(AMCompat.getInt(compound, "TresX"), AMCompat.getInt(compound, "TresY"), AMCompat.getInt(compound, "TresZ")));
      }
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
      this.targetSelector.addGoal(1, new SeagullAIRevealTreasure(this));
      this.targetSelector.addGoal(2, new SeagullAIStealFromPlayers(this));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(4, new TemptGoal(this, 1.0, AMCompat.ingredientOfTags(AMTagRegistry.SEAGULL_BREEDABLES, AMTagRegistry.SEAGULL_OFFERINGS), false) {
            public boolean canUse() {
               return !EntitySeagull.this.aiItemFlag && super.canUse();
            }
         });
      this.goalSelector.addGoal(5, new EntitySeagull.AIWanderIdle());
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new EntitySeagull.AIScatter());
      this.targetSelector.addGoal(1, new EntitySeagull.AITargetItems(this, false, false, 15, 16));
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.SEAGULL_BREEDABLES);
   }

   public static boolean canSeagullSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.getRawBrightness(pos, 0) > 8 && worldIn.getFluidState(pos.below()).isEmpty();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.seagullSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntitySeagull.MoveHelper(this);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(SITTING, false);
      builder.define(ATTACK_TICK, 0);
      builder.define(TREASURE_POS, Optional.empty());
      builder.define(FLIGHT_LOOK_YAW, 0.0F);
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      if (flying && this.isBaby()) {
         flying = false;
      }

      this.entityData.set(FLYING, flying);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setSitting(boolean sitting) {
      this.entityData.set(SITTING, sitting);
   }

   public float getFlightLookYaw() {
      return (Float)this.entityData.get(FLIGHT_LOOK_YAW);
   }

   public void setFlightLookYaw(float yaw) {
      this.entityData.set(FLIGHT_LOOK_YAW, yaw);
   }

   public BlockPos getTreasurePos() {
      return (BlockPos)((Optional)this.entityData.get(TREASURE_POS)).orElse(null);
   }

   public void setTreasurePos(BlockPos pos) {
      this.entityData.set(TREASURE_POS, Optional.ofNullable(pos));
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         boolean prev = super.hurt(source, amount);
         if (prev) {
            this.setSitting(false);
            if (!this.getMainHandItem().isEmpty()) {
               AMCompat.spawnAtLocation(this, this.getMainHandItem());
               this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               this.stealCooldown = 1500 + this.random.nextInt(1500);
            }

            this.feederUUID = null;
            this.treasureSitTime = 0;
         }

         return prev;
      }
   }

   public void tick() {
      super.tick();
      this.prevFlyProgress = this.flyProgress;
      this.prevFlapAmount = this.flapAmount;
      this.prevAttackProgress = this.attackProgress;
      this.prevSitProgress = this.sitProgress;
      float yMot = (float)(-((float)this.getDeltaMovement().y * 57.2957763671875));
      float absYaw = Math.abs(this.getYRot() - this.yRotO);
      boolean flying = this.isFlying();
      boolean sitting = this.isSitting();
      if (flying) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (sitting) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (absYaw > 8.0F) {
         this.flapAmount = Math.min(1.0F, this.flapAmount + 0.1F);
      } else if (yMot < 0.0F) {
         this.flapAmount = Math.min(-yMot * 0.2F, 1.0F);
      } else if (this.flapAmount > 0.0F) {
         this.flapAmount = this.flapAmount - Math.min(this.flapAmount, 0.05F);
      } else {
         this.flapAmount = 0.0F;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isFlying()) {
            float lookYawDist = Math.abs(this.getFlightLookYaw() - this.targetFlightLookYaw);
            if (this.flightLookCooldown > 0) {
               this.flightLookCooldown--;
            }

            if (this.flightLookCooldown == 0 && this.random.nextInt(4) == 0 && lookYawDist < 0.5F) {
               this.targetFlightLookYaw = Mth.clamp(this.random.nextFloat() * 120.0F - 60.0F, -60.0F, 60.0F);
               this.flightLookCooldown = 3 + this.random.nextInt(15);
            }

            if (this.getFlightLookYaw() < this.targetFlightLookYaw && lookYawDist > 0.5F) {
               this.setFlightLookYaw(this.getFlightLookYaw() + Math.min(lookYawDist, 4.0F));
            }

            if (this.getFlightLookYaw() > this.targetFlightLookYaw && lookYawDist > 0.5F) {
               this.setFlightLookYaw(this.getFlightLookYaw() - Math.min(lookYawDist, 4.0F));
            }

            if (this.onGround() && !this.isInWaterOrBubble() && this.timeFlying > 30) {
               this.setFlying(false);
            }

            this.timeFlying++;
            this.setNoGravity(true);
            if (this.isPassenger() || this.isInLove()) {
               this.setFlying(false);
            }
         } else {
            this.fallFlag = false;
            this.timeFlying = 0;
            this.setNoGravity(false);
         }

         if (this.isFlying() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.isFlying() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }
      }

      if (!this.getMainHandItem().isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 200 && this.canTargetItem(this.getMainHandItem())) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            this.eatItemEffect(this.getMainHandItem());
            this.getMainHandItem().shrink(1);
         }
      } else {
         this.heldItemTime = 0;
      }

      if (this.stealCooldown > 0) {
         this.stealCooldown--;
      }

      if (this.treasureSitTime > 0) {
         this.treasureSitTime--;
      }

      if (this.isSitting() && this.isInWaterOrBubble()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.019999999552965164, 0.0));
      }
   }

   public void eatItem() {
      this.heldItemTime = 200;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) && !this.isSitting();
   }

   private void eatItemEffect(ItemStack heldItemMainhand) {
      for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
         double d2 = this.random.nextGaussian() * 0.02;
         double d0 = this.random.nextGaussian() * 0.02;
         double d1 = this.random.nextGaussian() * 0.02;
         float radius = this.getBbWidth() * 0.65F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
         if (heldItemMainhand.getItem() instanceof BlockItem) {
            data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)heldItemMainhand.getItem()).getBlock().defaultBlockState());
         }

         this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
      }
   }

   public void setDataFromTreasureMap(Player player) {
      boolean flag = false;

      for (ItemStack map : player.getHandSlots()) {
         if (map.getItem() == Items.FILLED_MAP || map.getItem() == Items.MAP) {
            MapDecorations decorations = (MapDecorations)map.get(DataComponents.MAP_DECORATIONS);
            if (decorations != null) {
               for (Entry entry : decorations.decorations().values()) {
                  if (entry.type().value() == MapDecorationTypes.RED_X.value() || entry.type().value() == MapDecorationTypes.TARGET_X.value()) {
                     int x = (int)entry.x();
                     int z = (int)entry.z();
                     if (this.distanceToSqr(x, this.getY(), z) <= 400.0) {
                        flag = true;
                        this.setTreasurePos(new BlockPos(x, 0, z));
                     }
                  }
               }
            }
         }
      }

      if (flag) {
         this.feederUUID = player.getUUID();
         this.treasureSitTime = 300;
         this.stealCooldown = 1500 + this.random.nextInt(1500);
      }
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   public boolean isWingull() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.equalsIgnoreCase("wingull");
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.stealCooldown = this.stealCooldown + 600 + this.random.nextInt(1200);
      Entity thrower = e.getOwner();
      if (thrower != null && e.getItem().is(AMTagRegistry.SEAGULL_OFFERINGS)) {
         Player player = this.level().getPlayerByUUID(thrower.getUUID());
         if (player != null) {
            this.setDataFromTreasureMap(player);
            this.feederUUID = thrower.getUUID();
         }
      }

      this.setFlying(true);
      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 5.0F + radiusAdd + this.getRandom().nextInt(5);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getSeagullGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 8 + this.getRandom().nextInt(4);
      BlockPos newPos = ground.above(distFromGround > 3 ? flightHeight : this.getRandom().nextInt(4) + 8);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public BlockPos getSeagullGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 320 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.getRandom().nextInt(15);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getSeagullGround(radialPos);
      if (ground.getY() == 0) {
         return this.position();
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -62 && !this.level().getBlockState(ground).isSolid()) {
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
         Vec3 pos = new Vec3(this.orbitPos.getX() + extraX, this.orbitPos.getY() + this.random.nextInt(2), this.orbitPos.getZ() + extraZ);
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

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.getMainHandItem().isEmpty() && type != InteractionResult.SUCCESS) {
         AMCompat.spawnAtLocation(this, this.getMainHandItem().copy());
         this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         this.stealCooldown = 1500 + this.random.nextInt(1500);
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.SEAGULL.get(), serverWorld);
   }

   @Override
   public void peck() {
      this.entityData.set(ATTACK_TICK, 7);
   }

   private class AIScatter extends Goal {
      protected final EntitySeagull.AIScatter.Sorter theNearestAttackableTargetSorter;
      protected final com.google.common.base.Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIScatter() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntitySeagull.AIScatter.Sorter(EntitySeagull.this);
         this.targetEntitySelector = new com.google.common.base.Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.isAlive() && e.getType().builtInRegistryHolder().is(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player)e).isCreative();
            }
         };
      }

      public boolean canUse() {
         if (!EntitySeagull.this.isPassenger() && !EntitySeagull.this.isSitting() && !EntitySeagull.this.aiItemFlag && !EntitySeagull.this.isVehicle()) {
            if (!this.mustUpdate) {
               long worldTime = EntitySeagull.this.level().getGameTime() % 10L;
               if (EntitySeagull.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntitySeagull.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntitySeagull.this.level()
               .getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.targetEntity != null;
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
            EntitySeagull.this.setFlying(true);
            EntitySeagull.this.getMoveControl().setWantedPosition(this.flightTarget.x, this.flightTarget.y, this.flightTarget.z, 1.0);
            if (this.cooldown == 0 && EntitySeagull.this.isTargetBlocked(this.flightTarget)) {
               this.cooldown = 30;
               this.flightTarget = null;
            }
         }

         if (this.targetEntity != null) {
            if (EntitySeagull.this.onGround()
               || this.flightTarget == null
               || this.flightTarget != null && EntitySeagull.this.distanceToSqr(this.flightTarget) < 3.0) {
               Vec3 vec = EntitySeagull.this.getBlockInViewAway(this.targetEntity.position(), 0.0F);
               if (vec != null && vec.y() > EntitySeagull.this.getY()) {
                  this.flightTarget = vec;
               }
            }

            if (EntitySeagull.this.distanceTo(this.targetEntity) > 20.0F) {
               this.stop();
            }
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntitySeagull.this.getX(), EntitySeagull.this.getY() + 0.5, EntitySeagull.this.getZ());
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
         ((EntitySeagull)this.mob).aiItemFlag = false;
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !((EntitySeagull)this.mob).isSitting() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && !((EntitySeagull)this.mob).isSitting() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
      }

      @Override
      protected void moveTo() {
         EntitySeagull crow = (EntitySeagull)this.mob;
         if (this.targetEntity != null) {
            crow.aiItemFlag = true;
            if (this.mob.distanceTo(this.targetEntity) < 2.0F) {
               crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
               crow.peck();
            }

            if (!(this.mob.distanceTo(this.targetEntity) > 8.0F) && !crow.isFlying()) {
               this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
            } else {
               crow.setFlying(true);
               float f = (float)(crow.getX() - this.targetEntity.getX());
               float f2 = (float)(crow.getZ() - this.targetEntity.getZ());
               if (!crow.hasLineOfSight(this.targetEntity)) {
                  crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1.0 + crow.getY(), this.targetEntity.getZ(), 1.5);
               } else {
                  float f1 = 1.8F;
                  float xzDist = Mth.sqrt(f * f + f2 * f2);
                  if (xzDist < 5.0F) {
                     f1 = 0.0F;
                  }

                  crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
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

   private class AIWanderIdle extends Goal {
      protected final EntitySeagull eagle;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;
      private int orbitResetCooldown = 0;
      private int maxOrbitTime = 360;
      private int orbitTime = 0;

      public AIWanderIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.eagle = EntitySeagull.this;
      }

      public boolean canUse() {
         if (this.orbitResetCooldown < 0) {
            this.orbitResetCooldown++;
         }

         if ((this.eagle.getTarget() == null || !this.eagle.getTarget().isAlive() || this.eagle.isVehicle())
            && !this.eagle.isSitting()
            && !this.eagle.isPassenger()) {
            if ((this.eagle.getRandom().nextInt(20) == 0 || this.eagle.isFlying()) && !this.eagle.aiItemFlag) {
               if (this.eagle.isBaby()) {
                  this.flightTarget = false;
               } else if (this.eagle.isInWaterOrBubble()) {
                  this.flightTarget = true;
               } else if (this.eagle.onGround()) {
                  this.flightTarget = EntitySeagull.this.random.nextInt(10) == 0;
               } else {
                  if (this.orbitResetCooldown == 0 && EntitySeagull.this.random.nextInt(6) == 0) {
                     this.orbitResetCooldown = 100 + EntitySeagull.this.random.nextInt(300);
                     this.eagle.orbitPos = this.eagle.blockPosition();
                     this.eagle.orbitDist = 4 + EntitySeagull.this.random.nextInt(5);
                     this.eagle.orbitClockwise = EntitySeagull.this.random.nextBoolean();
                     this.orbitTime = 0;
                     this.maxOrbitTime = (int)(180.0F + 360.0F * EntitySeagull.this.random.nextFloat());
                  }

                  this.flightTarget = EntitySeagull.this.random.nextInt(5) != 0 && this.eagle.timeFlying < 400;
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
            } else {
               return false;
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
               this.orbitResetCooldown = -400 - EntitySeagull.this.random.nextInt(400);
            }
         }

         if (this.eagle.horizontalCollision && !this.eagle.onGround()) {
            this.stop();
         }

         if (this.flightTarget) {
            this.eagle.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else if (!this.eagle.isFlying() || this.eagle.onGround()) {
            this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntitySeagull.this.isFlying()) {
            this.eagle.fallFlag = true;
            if (this.eagle.onGround()) {
               this.eagle.setFlying(false);
               this.orbitTime = 0;
               this.eagle.orbitPos = null;
               this.orbitResetCooldown = -400 - EntitySeagull.this.random.nextInt(400);
            }
         }

         if (EntitySeagull.this.isFlying()
            && (!EntitySeagull.this.level().isEmptyBlock(this.eagle.getBlockPosBelowThatAffectsMyMovement()) || this.eagle.onGround())
            && !this.eagle.isInWaterOrBubble()
            && this.eagle.timeFlying > 30) {
            this.eagle.setFlying(false);
            this.orbitTime = 0;
            this.eagle.orbitPos = null;
            this.orbitResetCooldown = -400 - EntitySeagull.this.random.nextInt(400);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.eagle.position();
         if (this.orbitResetCooldown > 0 && this.eagle.orbitPos != null) {
            return this.eagle.getOrbitVec(vector3d, (float)(4 + EntitySeagull.this.random.nextInt(4)));
         } else {
            if (this.eagle.isVehicle() || this.eagle.isOverWaterOrVoid()) {
               this.flightTarget = true;
            }

            if (this.flightTarget) {
               return this.eagle.timeFlying >= 340 && !this.eagle.isVehicle() && !this.eagle.isOverWaterOrVoid()
                  ? this.eagle.getBlockGrounding(vector3d)
                  : this.eagle.getBlockInViewAway(vector3d, 0.0F);
            } else {
               return LandRandomPos.getPos(this.eagle, 10, 7);
            }
         }
      }

      public boolean canContinueToUse() {
         return this.flightTarget
            ? this.eagle.isFlying() && this.eagle.distanceToSqr(this.x, this.y, this.z) > 5.0
            : !this.eagle.getNavigation().isDone() && !this.eagle.isVehicle();
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
      private final EntitySeagull parentEntity;

      public MoveHelper(EntitySeagull bird) {
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
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.03 / d5)));
               Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            }
         }
      }
   }
}
