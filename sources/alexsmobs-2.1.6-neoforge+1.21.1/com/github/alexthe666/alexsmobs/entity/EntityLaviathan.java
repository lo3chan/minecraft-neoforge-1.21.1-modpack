package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWaterLava;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.LaviathanAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.fluids.FluidType;

public class EntityLaviathan extends Animal implements ISemiAquatic, IHerdPanic, IMultipartOwner {
   private static final EntityDataAccessor<Boolean> OBSIDIAN = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> HEAD_HEIGHT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> HEAD_YROT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> CHILL_TIME = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> HAS_BODY_GEAR = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_HEAD_GEAR = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
   private static final Predicate<EntityCrimsonMosquito> HEALTHY_MOSQUITOES = mob -> mob.isAlive() && mob.getHealth() > 0.0F && !mob.isSick();
   public static final ResourceLocation OBSIDIAN_LOOT = AMCompat.rl("alexsmobs", "entities/laviathan_obsidian");
   public final EntityLaviathanPart headPart;
   public final EntityLaviathanPart neckPart1;
   public final EntityLaviathanPart neckPart2;
   public final EntityLaviathanPart neckPart3;
   public final EntityLaviathanPart neckPart4;
   public final EntityLaviathanPart neckPart5;
   public final EntityLaviathanPart seat1;
   public final EntityLaviathanPart seat2;
   public final EntityLaviathanPart seat3;
   public final EntityLaviathanPart seat4;
   public final EntityLaviathanPart[] theEntireNeck;
   public final EntityLaviathanPart[] allParts;
   public final EntityLaviathanPart[] seatParts;
   private final UUID[] riderPositionMap = new UUID[4];
   public float prevHeadHeight = 0.0F;
   public float swimProgress = 0.0F;
   public float prevSwimProgress = 0.0F;
   public float biteProgress;
   public float prevBiteProgress;
   public int revengeCooldown = 0;
   private boolean isLandNavigator;
   private int conversionTime = 0;
   private int dismountCooldown = 0;
   private int headPeakCooldown = 0;
   private boolean hasObsidianArmor;
   private int blockBreakCounter;
   private double lastX = 0.0;
   private double lastZ = 0.0;

   protected EntityLaviathan(EntityType<? extends Animal> entityType, Level level) {
      super(entityType, level);
      AMCompat.setMaxUpStep(this, 1.3F);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.headPart = new EntityLaviathanPart(this, 1.2F, 0.9F);
      this.neckPart1 = new EntityLaviathanPart(this, 0.9F, 0.9F);
      this.neckPart2 = new EntityLaviathanPart(this, 0.9F, 0.9F);
      this.neckPart3 = new EntityLaviathanPart(this, 0.9F, 0.9F);
      this.neckPart4 = new EntityLaviathanPart(this, 0.9F, 0.9F);
      this.neckPart5 = new EntityLaviathanPart(this, 0.9F, 0.9F);
      this.seat1 = new EntityLaviathanPart(this, 0.9F, 0.4F);
      this.seat2 = new EntityLaviathanPart(this, 0.9F, 0.4F);
      this.seat3 = new EntityLaviathanPart(this, 0.9F, 0.4F);
      this.seat4 = new EntityLaviathanPart(this, 0.9F, 0.4F);
      this.theEntireNeck = new EntityLaviathanPart[]{this.neckPart1, this.neckPart2, this.neckPart3, this.neckPart4, this.neckPart5, this.headPart};
      this.allParts = new EntityLaviathanPart[]{
         this.neckPart1, this.neckPart2, this.neckPart3, this.neckPart4, this.neckPart5, this.headPart, this.seat1, this.seat2, this.seat3, this.seat4
      };
      this.seatParts = new EntityLaviathanPart[]{this.seat1, this.seat2, this.seat3, this.seat4};
      this.switchNavigator(true);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.laviathanSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canLaviathanSpawn(
      EntityType<EntityLaviathan> p_234314_0_, LevelAccessor p_234314_1_, MobSpawnType p_234314_2_, BlockPos p_234314_3_, RandomSource p_234314_4_
   ) {
      MutableBlockPos blockpos$mutable = p_234314_3_.mutable();

      do {
         blockpos$mutable.move(Direction.UP);
      } while (p_234314_1_.getFluidState(blockpos$mutable).is(FluidTags.LAVA));

      return p_234314_1_.getBlockState(blockpos$mutable).isAir();
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.LAVIATHAN_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.LAVIATHAN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.LAVIATHAN_HURT.get();
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      return this.isObsidian() ? AMCompat.lootKey(OBSIDIAN_LOOT) : super.getDefaultLootTable();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   public boolean canBeCollidedWith() {
      return AMCompat.isFullyConstructed(this) && this.isAlive();
   }

   protected boolean canAddPassenger(Entity p_38390_) {
      return this.getPassengers().size() < 4 && !this.isEyeInFluid(FluidTags.LAVA) && !this.isEyeInFluid(FluidTags.WATER);
   }

   public void push(Entity entity) {
      if (!entity.isPassengerOfSameVehicle(this)) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.LAVIATHAN_BREEDABLES);
   }

   public boolean isPushable() {
      return false;
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (itemstack.is(AMTagRegistry.LAVIATHAN_FOODSTUFFS) && this.getHealth() < this.getMaxHealth()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.heal(10.0F);
         return InteractionResult.SUCCESS;
      } else if (item == AMItemRegistry.STRADDLE_HELMET.get() && !this.hasHeadGear() && !this.isBaby()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setHeadGear(true);
         return InteractionResult.SUCCESS;
      } else if (item == AMItemRegistry.STRADDLE_SADDLE.get() && !this.hasBodyGear() && !this.isBaby()) {
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setBodyGear(true);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult type = super.mobInteract(player, hand);
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && !this.isFood(itemstack)
            && this.hasBodyGear()
            && !this.isBaby()) {
            if (!player.isShiftKeyDown()) {
               if (!this.level().isClientSide()) {
                  player.startRiding(this);
               }
            } else {
               this.ejectPassengers();
            }

            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      }
   }

   public int getClosestOpenSeat(Vec3 entityPos) {
      int closest = -1;
      double closestDistance = 1.7976931348623157E308;

      for (int i = 0; i < this.seatParts.length; i++) {
         double dist = entityPos.distanceTo(this.seatParts[i].position());
         if ((closest == -1 || closestDistance > dist) && this.riderPositionMap[i] == null) {
            closest = i;
            closestDistance = dist;
         }
      }

      return closest;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      int playerPosition = -1;
      Player player = null;
      if (this.hasHeadGear() && this.hasBodyGear()) {
         for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player player2) {
               int player2Position = this.getRiderPosition(passenger);
               if (player == null || playerPosition > player2Position) {
                  player = player2;
                  playerPosition = player2Position;
               }
            }
         }
      }

      return player;
   }

   public int getSeatRaytrace(Entity player) {
      HitResult result = player.pick(player.distanceTo(this), 0.0F, false);
      if (result != null) {
         Vec3 vec = result.getLocation();
         return this.getClosestOpenSeat(vec);
      } else {
         return -1;
      }
   }

   public void removePassenger(Entity entity) {
      super.removePassenger(entity);
      this.dismountCooldown = 40 + this.random.nextInt(40);
      if (entity != null && entity.getUUID() != null) {
         for (int i = 0; i < this.riderPositionMap.length; i++) {
            if (this.riderPositionMap[i] != null && this.riderPositionMap[i].equals(entity.getUUID())) {
               this.riderPositionMap[i] = null;
            }
         }
      }
   }

   public int getRiderPosition(Entity passenger) {
      int posit = -1;

      for (int i = 0; i < this.riderPositionMap.length; i++) {
         if (this.riderPositionMap[i] != null && passenger != null && passenger.getUUID().equals(this.riderPositionMap[i])) {
            posit = i;
         }
      }

      return posit;
   }

   protected void addPassenger(Entity entity) {
      int rayTrace = this.getSeatRaytrace(entity);
      if (rayTrace >= 0 && rayTrace < 4) {
         if (this.riderPositionMap[rayTrace] != null && !this.level().isClientSide() && this.level() instanceof ServerLevel) {
            Entity kickOff = ((ServerLevel)this.level()).getEntity(this.riderPositionMap[rayTrace]);
            this.riderPositionMap[rayTrace] = null;
            if (kickOff != null) {
               kickOff.stopRiding();
            }
         }

         this.riderPositionMap[rayTrace] = entity.getUUID();
         super.addPassenger(entity);
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Obsidian", this.isObsidian());
      compound.putBoolean("HeadGear", this.hasHeadGear());
      compound.putBoolean("BodyGear", this.hasBodyGear());
      compound.putInt("ChillTime", this.getChillTime());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setObsidian(AMCompat.getBoolean(compound, "Obsidian"));
      this.setHeadGear(AMCompat.getBoolean(compound, "HeadGear"));
      this.setBodyGear(AMCompat.getBoolean(compound, "BodyGear"));
      this.setChillTime(AMCompat.getInt(compound, "ChillTime"));
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         int posit = this.getRiderPosition(passenger);
         if (posit >= 0 && posit <= 3) {
            EntityLaviathanPart seat = this.seatParts[posit];
            passenger.setPos(seat.getX(), this.getY() + this.getPassengersRidingOffset() + AMPlatform.myRidingOffset(passenger, this), seat.getZ());
         } else {
            passenger.stopRiding();
         }
      }
   }

   public double getPassengersRidingOffset() {
      float f = this.walkAnimation.position();
      float f1 = this.walkAnimation.speed();
      float f2 = 0.0F;
      return this.getBbHeight() - 0.4000000059604645;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasBodyGear() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.STRADDLE_SADDLE.get());
      }

      if (this.hasHeadGear() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.STRADDLE_HELMET.get());
      }

      this.setBodyGear(false);
      this.setHeadGear(false);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = this.createNavigation(this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntityLaviathan.MoveController(this);
         this.navigation = new BoneSerpentPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   protected PathNavigation createNavigation(Level level) {
      return new GroundPathNavigatorWide(this, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new AnimalAIHerdPanic(this, 1.0) {
         @Override
         public boolean canUse() {
            return super.canUse() && !EntityLaviathan.this.hasHeadGear();
         }
      });
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(3, new TemptGoal(this, 1.1, AMCompat.ingredientOfTags(AMTagRegistry.LAVIATHAN_BREEDABLES, AMTagRegistry.LAVIATHAN_FOODSTUFFS), false));
      this.goalSelector.addGoal(4, new AnimalAIFindWaterLava(this, 1.0));
      this.goalSelector.addGoal(5, new LaviathanAIRandomSwimming(this, 1.0, 22) {
         @Override
         public boolean canUse() {
            return super.canUse() && !EntityLaviathan.this.hasHeadGear() && !EntityLaviathan.this.hasBodyGear();
         }
      });
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
   }

   public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
      return true;
   }

   protected float getBlockSpeedFactor() {
      return !this.shouldSwim() && !this.getBlockStateOn().is(BlockTags.SOUL_SPEED_BLOCKS) ? super.getBlockSpeedFactor() : 1.0F;
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      if (!worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER) && !worldIn.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) {
         return this.isInLava() ? -1.0F : 0.0F;
      } else {
         return 10.0F;
      }
   }

   public int getMaxFallDistance() {
      return 256;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
      if (player.zza != 0.0F) {
         float f = player.zza < 0.0F ? 0.5F : 1.0F;
         Vec3 lookVec = player.getLookAngle();
         float y = (float)lookVec.y * 0.1F;
         float waterAt = (float)this.getMaxFluidHeight();
         float half = this.getBbHeight() * 0.5F;
         if (waterAt > half) {
            y = Mth.clamp(waterAt - half, 0.0F, 0.15F);
         } else if (waterAt < half) {
            y = Mth.clamp(waterAt - half, -0.15F, 0.0F);
         }

         if (this.horizontalCollision) {
            y += 0.4F;
         }

         Vec3 vec3 = new Vec3(player.xxa * 0.25F, y, player.zza * (this.shouldSwim() ? 0.75F : 0.25F) * f);
         this.setSprinting(true);
         return vec3;
      } else {
         this.setSprinting(false);
         return Vec3.ZERO;
      }
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      this.setRot(player.getYRot(), player.getXRot() * 0.5F);
      this.setYHeadRot(player.getYHeadRot());
      AMCompat.setMaxUpStep(this, 1.3F);
      this.setTarget(null);
   }

   protected float getRiddenSpeed(Player rider) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   public double getFluidMotionScale(FluidType type) {
      return type != AMPlatform.waterType() && type != AMPlatform.lavaType() ? super.getFluidMotionScale(type) : 1.0;
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev && source.getEntity() != null) {
         int fleeTime = 100 + this.getRandom().nextInt(150);
         this.revengeCooldown = fleeTime;
         this.setChillTime(0);
      }

      return prev;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(OBSIDIAN, false);
      builder.define(HAS_BODY_GEAR, false);
      builder.define(HAS_HEAD_GEAR, false);
      builder.define(HEAD_HEIGHT, 0.0F);
      builder.define(HEAD_YROT, 0.0F);
      builder.define(CHILL_TIME, 0);
      builder.define(ATTACK_TICK, 0);
   }

   public void travel(Vec3 travelVector) {
      boolean liquid = this.isInLava() || this.isInWater();
      if (this.isControlledByLocalInstance() && liquid) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(this.isVehicle() ? 0.5 : 0.9));
         this.calculateEntityAnimation(false);
         if (!this.isVehicle() && !this.isChilling()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.009999999776482582, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.lastX, 0.0, this.getZ() - this.lastZ);
      float walkSpeed = 4.0F;
      float f2 = Math.min(f1 * walkSpeed, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   public int getMaxHeadXRot() {
      return 50;
   }

   public int getMaxHeadYRot() {
      return 50;
   }

   public int getHeadRotSpeed() {
      return 4;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void tick() {
      super.tick();
      this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.yBodyRot, this.getHeadRotSpeed());
      this.prevSwimProgress = this.swimProgress;
      this.prevBiteProgress = this.biteProgress;
      this.prevHeadHeight = this.getHeadHeight();
      if (this.shouldSwim()) {
         if (this.swimProgress < 5.0F) {
            this.swimProgress++;
         }
      } else if (this.swimProgress > 0.0F) {
         this.swimProgress--;
      }

      if (this.isObsidian()) {
         if (!this.hasObsidianArmor) {
            this.hasObsidianArmor = true;
            this.getAttribute(Attributes.ARMOR).setBaseValue(30.0);
         }
      } else if (this.hasObsidianArmor) {
         this.hasObsidianArmor = false;
         this.getAttribute(Attributes.ARMOR).setBaseValue(10.0);
      }

      if (!this.level().isClientSide()) {
         if (!this.isObsidian() && this.isInWaterOrBubble()) {
            if (this.conversionTime < 300) {
               this.conversionTime++;
            } else {
               this.setObsidian(true);
            }
         }

         if (this.shouldSwim()) {
            this.fallDistance = 0.0F;
         }
      }

      float neckBase = 0.8F;
      if (!this.isNoAi()) {
         Vec3[] avector3d = new Vec3[this.allParts.length];

         for (int j = 0; j < this.allParts.length; j++) {
            this.allParts[j].collideWithNearbyEntities();
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
         }

         float yaw = this.yBodyRot * 0.017453292F;
         float neckContraction = 2.0F * Math.abs(this.getHeadHeight() / 3.0F) + 0.5F * Math.abs(this.getHeadYaw(0.0F) / 50.0F);

         for (int l = 0; l < this.theEntireNeck.length; l++) {
            float f = (float)l / this.theEntireNeck.length;
            float f1 = -(2.2F + l - f * neckContraction);
            float f2 = Mth.sin(yaw + Maths.rad(f * this.getHeadYaw(0.0F))) * (1.0F - Math.abs(this.getXRot() / 90.0F));
            float f3 = Mth.cos(yaw + Maths.rad(f * this.getHeadYaw(0.0F))) * (1.0F - Math.abs(this.getXRot() / 90.0F));
            this.setPartPosition(this.theEntireNeck[l], f2 * f1, neckBase + Math.sin(f * 3.141592653589793 * 0.5) * (this.getHeadHeight() * 1.1F), -f3 * f1);
         }

         this.setPartPosition(this.seat1, this.getXForPart(yaw, 145.0F) * 0.75F, 2.0, this.getZForPart(yaw, 145.0F) * 0.75F);
         this.setPartPosition(this.seat2, this.getXForPart(yaw, -145.0F) * 0.75F, 2.0, this.getZForPart(yaw, -145.0F) * 0.75F);
         this.setPartPosition(this.seat3, this.getXForPart(yaw, 35.0F) * 0.95F, 2.0, this.getZForPart(yaw, 35.0F) * 0.95F);
         this.setPartPosition(this.seat4, this.getXForPart(yaw, -35.0F) * 0.95F, 2.0, this.getZForPart(yaw, -35.0F) * 0.95F);
         if (this.level().isClientSide() && this.isChilling()) {
            if (!this.isBaby()) {
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     this.getX() + this.getXForPart(yaw, 158.0F) * 1.75F,
                     this.getY(1.0),
                     this.getZ() + this.getZForPart(yaw, 158.0F) * 1.75F,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     this.getX() + this.getXForPart(yaw, -166.0F) * 1.48F,
                     this.getY(1.0),
                     this.getZ() + this.getZForPart(yaw, -166.0F) * 1.48F,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     this.getX() + this.getXForPart(yaw, 14.0F) * 1.78F,
                     this.getY(0.9),
                     this.getZ() + this.getZForPart(yaw, 14.0F) * 1.78F,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     this.getX() + this.getXForPart(yaw, -14.0F) * 1.6F,
                     this.getY(1.1),
                     this.getZ() + this.getZForPart(yaw, -14.0F) * 1.6F,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
            }

            this.level()
               .addParticle(
                  ParticleTypes.SMOKE,
                  this.headPart.getRandomX(0.6),
                  this.headPart.getY(0.9),
                  this.headPart.getRandomZ(0.6),
                  0.0,
                  this.random.nextDouble() / 5.0,
                  0.0
               );
         }

         for (int l = 0; l < this.allParts.length; l++) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
         }
      }

      if ((this.isInLava() || this.isInWaterOrBubble()) && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (!this.isInLava() && !this.isInWaterOrBubble() && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (!this.level().isClientSide()) {
         if (this.getChillTime() > 0) {
            this.setChillTime(this.getChillTime() - 1);
         } else if (this.shouldSwim() && this.random.nextInt(this.isVehicle() ? 200 : 2000) == 0 && this.revengeCooldown == 0) {
            this.setChillTime(100 + this.random.nextInt(500));
         }

         if (this.revengeCooldown > 0) {
            this.revengeCooldown--;
         }

         if (this.headPeakCooldown > 0) {
            this.headPeakCooldown--;
         }

         if (this.revengeCooldown == 0 && this.getLastHurtByMob() != null) {
            this.setLastHurtByMob(null);
         }
      }

      if (!this.level().isClientSide()) {
         if (this.getControllingPassenger() == null && (this.getChillTime() > 0 || this.hasHeadGear() || this.dismountCooldown > 0)) {
            this.floatLaviathan();
         }

         if (!this.isChilling() && this.headPeakCooldown == 0) {
            float low = this.getLowHeadHeight();
            this.setHeadHeight(this.getHeadHeight() + (0.5F + (this.getLowHeadHeight() + this.getHighHeadHeight(low)) / 2.0F - this.getHeadHeight()) * 0.2F);
         } else if (this.getMaxFluidHeight() <= this.getBbHeight() * 0.5F && this.getMaxFluidHeight() >= this.getBbHeight() * 0.25F) {
            float mot = (float)this.getDeltaMovement().lengthSqr();
            this.setHeadHeight(Mth.clamp(this.getHeadHeight() + 0.1F - 0.2F * mot, 0.0F, 2.0F));
            this.headPeakCooldown = 5;
         }
      }

      if (this.isChilling()) {
         boolean keepChillin = false;
         boolean startBiting = false;

         for (EntityCrimsonMosquito entity : this.level()
            .getEntitiesOfClass(EntityCrimsonMosquito.class, this.getBoundingBox().inflate(30.0), HEALTHY_MOSQUITOES)) {
            entity.setLuringLaviathan(this.getId());
            keepChillin = true;
         }

         if (keepChillin) {
            this.setChillTime(Math.max(20, this.getChillTime()));
         }

         for (EntityCrimsonMosquito entity : this.level()
            .getEntitiesOfClass(EntityCrimsonMosquito.class, this.headPart.getBoundingBox().inflate(1.0), HEALTHY_MOSQUITOES)) {
            startBiting = true;
            if (this.biteProgress == 5.0F) {
               entity.hurt(this.damageSources().mobAttack(this), 1000.0F);
               entity.setShrink(true);
               this.setChillTime(0);
            }
         }

         if (startBiting && (Integer)this.entityData.get(ATTACK_TICK) <= 0 && this.biteProgress == 0.0F) {
            this.entityData.set(ATTACK_TICK, 7);
         }
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0 && this.biteProgress < 5.0F) {
         this.biteProgress++;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) <= 0 && this.biteProgress > 0.0F) {
         this.biteProgress--;
      }

      if (this.dismountCooldown > 0) {
         this.dismountCooldown--;
      }

      if (this.hasBodyGear()) {
         List<Entity> list = this.level()
            .getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntitySelector.pushableBy(this));
         if (!list.isEmpty()) {
            boolean flag2 = !this.level().isClientSide();

            for (int j = 0; j < list.size(); j++) {
               Entity entityx = list.get(j);
               if (!entityx.hasPassenger(this)) {
                  if (flag2
                     && !(entityx instanceof Player)
                     && !entityx.isPassenger()
                     && entityx.getBbWidth() < this.getBbWidth()
                     && !(entityx instanceof EntityLaviathan)
                     && !(entityx instanceof Enemy)
                     && entityx instanceof Mob
                     && this.canAddPassenger(entityx)
                     && !(entityx instanceof WaterAnimal)) {
                     entityx.startRiding(this);
                  } else {
                     this.push(entityx);
                  }
               }
            }
         }
      }

      if (this.isVehicle() && !this.level().isClientSide() && this.tickCount % 40 == 0 && this.getPassengers().size() > 3) {
         for (Entity entityx : this.getPassengers()) {
            if (entityx instanceof ServerPlayer) {
               AMAdvancementTriggerRegistry.LAVIATHAN_FOUR_PASSENGERS.trigger((ServerPlayer)entityx);
            }
         }
      }

      this.lastX = this.getX();
      this.lastZ = this.getZ();
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
         if (!this.level().isClientSide() && this.isVehicle() && this.blockBreakCounter == 0 && AMPlatform.mobGriefing(this.level(), this)) {
            for (int a = (int)Math.round(this.getBoundingBox().minX); a <= (int)Math.round(this.getBoundingBox().maxX); a++) {
               for (int b = (int)Math.round(this.getBoundingBox().minY) - 1; b <= (int)Math.round(this.getBoundingBox().maxY) + 1 && b <= 127; b++) {
                  for (int c = (int)Math.round(this.getBoundingBox().minZ); c <= (int)Math.round(this.getBoundingBox().maxZ); c++) {
                     BlockPos pos = new BlockPos(a, b, c);
                     BlockState state = this.level().getBlockState(pos);
                     FluidState fluidState = this.level().getFluidState(pos);
                     Block block = state.getBlock();
                     if (!state.isAir()
                        && !state.getShape(this.level(), pos).isEmpty()
                        && state.is(AMTagRegistry.LAVIATHAN_BREAKABLES)
                        && fluidState.isEmpty()
                        && block != Blocks.AIR) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6000000238418579, 1.0, 0.6000000238418579));
                        flag = true;
                        this.level().destroyBlock(pos, true);
                     }
                  }
               }
            }
         }

         if (flag) {
            this.blockBreakCounter = 10;
         }
      }
   }

   public float getLowHeadHeight() {
      float checkAt = 0.0F;

      while (checkAt > -3.0F && !this.isHeadInWall((float)this.getY() + checkAt) && !this.isHeadInLava((float)this.getY() + checkAt)) {
         checkAt -= 0.2F;
      }

      return checkAt;
   }

   public float getHighHeadHeight(float low) {
      float checkAt = 3.0F;

      while (checkAt > 0.0F && (!this.isHeadInWall((float)this.getY() + checkAt) || this.isHeadInLava((float)this.getY() + checkAt))) {
         checkAt -= 0.2F;
      }

      return checkAt;
   }

   public boolean isHeadInWall(float offset) {
      if (this.noPhysics) {
         return false;
      } else {
         float f = 0.8F;
         Vec3 vec3 = new Vec3(this.headPart.getX(), offset, this.headPart.getZ());
         AABB axisalignedbb = AABB.ofSize(vec3, f, 1.0E-6, f);
         return this.level()
            .getBlockStates(axisalignedbb)
            .filter(Predicate.not(BlockStateBase::isAir))
            .anyMatch(
               p_185969_ -> {
                  BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                  return p_185969_.isSuffocating(this.level(), blockpos)
                     && Shapes.joinIsNotEmpty(
                        p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisalignedbb), BooleanOp.AND
                     );
               }
            );
      }
   }

   public boolean isHeadInLava(float offset) {
      if (this.noPhysics) {
         return false;
      } else {
         float f = 0.8F;
         BlockPos pos = AMBlockPos.fromCoords(this.headPart.getX(), offset, this.headPart.getZ());
         return !this.level().getFluidState(pos).isEmpty();
      }
   }

   private void floatLaviathan() {
      if (this.shouldSwim()) {
         if (this.getMaxFluidHeight() >= this.getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.11999999731779099, this.getDeltaMovement().z);
         } else if (this.getMaxFluidHeight() >= this.getBbHeight() * 0.5F) {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.07999999821186066, this.getDeltaMovement().z);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.0, this.getDeltaMovement().z);
         }
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      float expand = this.getBbWidth() + 1.0F;
      Vec3[] avector3d = new Vec3[]{
         getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot()),
         getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() - 22.5F),
         getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() + 22.5F),
         getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() - 45.0F),
         getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() + 45.0F)
      };
      Set<BlockPos> set = Sets.newLinkedHashSet();
      double d0 = this.getBoundingBox().maxY;
      double d1 = this.getBoundingBox().minY - 0.5;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (Vec3 vector3d : avector3d) {
         blockpos$mutable.set(this.getX() + vector3d.x, d0, this.getZ() + vector3d.z);

         for (double d2 = d0; d2 > d1; d2--) {
            set.add(blockpos$mutable.immutable());
            blockpos$mutable.move(Direction.DOWN);
         }
      }

      for (BlockPos blockpos : set) {
         if (!this.level().getFluidState(blockpos).is(FluidTags.LAVA)) {
            double d3 = this.level().getBlockFloorHeight(blockpos);
            if (DismountHelper.isBlockFloorValid(d3)) {
               Vec3 vector3d1 = Vec3.upFromBottomCenterOf(blockpos, d3);
               UnmodifiableIterator var15 = livingEntity.getDismountPoses().iterator();

               while (var15.hasNext()) {
                  Pose pose = (Pose)var15.next();
                  AABB axisalignedbb = livingEntity.getLocalBoundsForPose(pose);
                  if (DismountHelper.canDismountTo(this.level(), livingEntity, axisalignedbb.move(vector3d1))) {
                     livingEntity.setPose(pose);
                     return vector3d1;
                  }
               }
            }
         }
      }

      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   public float getWaterLevelAbove() {
      AABB axisalignedbb = this.getBoundingBox();
      int i = Mth.floor(axisalignedbb.minX);
      int j = Mth.ceil(axisalignedbb.maxX);
      int k = Mth.floor(axisalignedbb.maxY);
      int l = Mth.ceil(axisalignedbb.maxY);
      int i1 = Mth.floor(axisalignedbb.minZ);
      int j1 = Mth.ceil(axisalignedbb.maxZ);
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      label44:
      for (int k1 = k; k1 < l; k1++) {
         float f = 0.0F;

         for (int l1 = i; l1 < j; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               blockpos$mutable.set(l1, k1, i2);
               FluidState fluidstate = this.level().getFluidState(blockpos$mutable);
               if (fluidstate.is(FluidTags.WATER) || fluidstate.is(FluidTags.LAVA)) {
                  f = Math.max(f, fluidstate.getHeight(this.level(), blockpos$mutable));
               }

               if (f >= 1.0F) {
                  continue label44;
               }
            }
         }

         if (f < 1.0F) {
            return blockpos$mutable.getY() + f;
         }
      }

      return l + 1;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public boolean shouldSwim() {
      return this.getMaxFluidHeight() >= 0.10000000149011612 || this.isInLava() || this.isInWaterOrBubble();
   }

   private float getXForPart(float yaw, float degree) {
      return Mth.sin((float)(yaw + Math.toRadians(degree)));
   }

   private float getZForPart(float yaw, float degree) {
      return -Mth.cos((float)(yaw + Math.toRadians(degree)));
   }

   public float getHeadHeight() {
      return Mth.clamp((Float)this.entityData.get(HEAD_HEIGHT), -3.0F, 3.0F);
   }

   public void setHeadHeight(float height) {
      this.entityData.set(HEAD_HEIGHT, Mth.clamp(height, -3.0F, 3.0F));
   }

   public boolean isObsidian() {
      return (Boolean)this.entityData.get(OBSIDIAN);
   }

   public void setObsidian(boolean obsidian) {
      this.entityData.set(OBSIDIAN, obsidian);
   }

   public boolean hasHeadGear() {
      return (Boolean)this.entityData.get(HAS_HEAD_GEAR);
   }

   public void setHeadGear(boolean headGear) {
      this.entityData.set(HAS_HEAD_GEAR, headGear);
   }

   public boolean hasBodyGear() {
      return (Boolean)this.entityData.get(HAS_BODY_GEAR);
   }

   public void setBodyGear(boolean bodyGear) {
      this.entityData.set(HAS_BODY_GEAR, bodyGear);
   }

   public int getChillTime() {
      return (Integer)this.entityData.get(CHILL_TIME);
   }

   public void setChillTime(int chillTime) {
      this.entityData.set(CHILL_TIME, chillTime);
   }

   public float getHeadYaw(float interp) {
      float f;
      if (interp == 0.0F) {
         f = this.getYHeadRot() - this.yBodyRot;
      } else {
         float yBodyRot1 = this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * interp;
         float yHeadRot1 = this.yHeadRotO + (this.getYHeadRot() - this.yHeadRotO) * interp;
         f = yHeadRot1 - yBodyRot1;
      }

      return Mth.clamp(Mth.wrapDegrees(f), -50.0F, 50.0F);
   }

   private void setPartPosition(EntityLaviathanPart part, double offsetX, double offsetY, double offsetZ) {
      part.setPos(this.getX() + offsetX * part.scale, this.getY() + offsetY * part.scale, this.getZ() + offsetZ * part.scale);
   }

   @Override
   public boolean isMultipartEntity() {
      return true;
   }

   @Override
   public PartEntity<?>[] getParts() {
      return this.allParts;
   }

   public boolean attackEntityPartFrom(EntityLaviathanPart part, DamageSource source, float amount) {
      return AMCompat.hurt(this, source, amount);
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.isVehicle();
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.isVehicle();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isVehicle();
   }

   @Override
   public int getWaterSearchRange() {
      return 15;
   }

   private double getMaxFluidHeight() {
      return Math.max(this.getFluidHeight(FluidTags.LAVA), this.getFluidHeight(FluidTags.WATER));
   }

   public boolean isChilling() {
      return this.getChillTime() > 0 && this.getMaxFluidHeight() <= this.getBbHeight() * 0.5F;
   }

   public void scaleParts() {
      for (EntityLaviathanPart parts : this.allParts) {
         float prev = parts.scale;
         parts.scale = this.isBaby() ? 0.5F : 1.0F;
         if (prev != parts.scale) {
            parts.refreshDimensions();
         }
      }
   }

   public void aiStep() {
      super.aiStep();
      this.scaleParts();
   }

   public Vec3 getLureMosquitoPos() {
      return new Vec3(this.headPart.getX(), this.headPart.getY(0.4000000059604645), this.headPart.getZ());
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return true;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.LAVIATHAN.get(), serverWorld);
   }

   static class MoveController extends MoveControl {
      private final EntityLaviathan laviathan;

      public MoveController(EntityLaviathan dolphinIn) {
         super(dolphinIn);
         this.laviathan = dolphinIn;
      }

      public void tick() {
         float speed = (float)(this.speedModifier * 3.0 * this.laviathan.getAttributeValue(Attributes.MOVEMENT_SPEED));
         if (this.operation == Operation.MOVE_TO && (!this.laviathan.getNavigation().isDone() || this.laviathan.getControllingPassenger() != null)) {
            double lvt_1_1_ = this.wantedX - this.laviathan.getX();
            double lvt_3_1_ = this.wantedY - this.laviathan.getY();
            double lvt_5_1_ = this.wantedZ - this.laviathan.getZ();
            double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
            if (lvt_7_1_ < 2.5) {
               this.laviathan.setZza(0.0F);
            } else {
               float lvt_9_1_ = (float)(Mth.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875) - 90.0F;
               this.laviathan.setYRot(this.rotlerp(this.laviathan.getYRot(), lvt_9_1_, 5.0F));
               this.laviathan.setYHeadRot(this.rotlerp(this.laviathan.getYHeadRot(), lvt_9_1_, 90.0F));
               if (this.laviathan.shouldSwim()) {
                  this.laviathan.setSpeed(speed * 0.03F);
                  float lvt_11_1_ = -((float)(Mth.atan2(lvt_3_1_, Mth.sqrt((float)(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_))) * 57.2957763671875));
                  lvt_11_1_ = Mth.clamp(Mth.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                  this.laviathan.setXRot(this.rotlerp(this.laviathan.getXRot(), lvt_11_1_, 25.0F));
                  float lvt_12_1_ = Mth.cos(this.laviathan.getXRot() * 0.017453292F);
                  float lvt_13_1_ = Mth.sin(this.laviathan.getXRot() * 0.017453292F);
                  this.laviathan.zza = lvt_12_1_ * speed;
                  this.laviathan.yya = -lvt_13_1_ * speed;
               } else {
                  this.laviathan.setSpeed(speed * 0.1F);
               }
            }
         } else if (!this.laviathan.level().getBlockState(this.laviathan.blockPosition().above()).getFluidState().isEmpty()
            && this.laviathan.getChillTime() <= 0) {
            this.laviathan.setDeltaMovement(this.laviathan.getDeltaMovement().add(0.0, -0.05, 0.0));
         }
      }
   }
}
