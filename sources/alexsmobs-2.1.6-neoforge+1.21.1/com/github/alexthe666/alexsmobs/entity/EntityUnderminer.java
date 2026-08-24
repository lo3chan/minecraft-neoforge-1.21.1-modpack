package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EtherealMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.MonsterAIWalkThroughHallsOfStructure;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.common.Tags.Blocks;

public class EntityUnderminer extends PathfinderMob {
   protected static final EntityDataAccessor<Optional<BlockPos>> TARGETED_BLOCK_POS = SynchedEntityData.defineId(
      EntityUnderminer.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Boolean> DWARF = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> MINING_PROGRESS = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> VISUALLY_MINING = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);
   private int mineCooldown = 100;
   private int resetStackTime = 0;
   private ItemStack lastGivenStack = null;
   public float hidingProgress = 0.0F;
   public float prevHidingProgress = 0.0F;
   private boolean mineAIFlag = false;
   private BlockPos lastPosition = this.blockPosition();

   public EntityUnderminer(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
      this.moveControl = new EtherealMoveController(this, 1.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   protected PathNavigation createNavigation(Level level) {
      return new EntityUnderminer.PathNavigator(this, this.level());
   }

   public static <T extends Mob> boolean checkUnderminerSpawnRules(
      EntityType<EntityUnderminer> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      if (reason == MobSpawnType.SPAWNER) {
         return true;
      } else {
         int j = 3;
         if (pos.getY() >= iServerWorld.getSeaLevel()) {
            return false;
         } else {
            if (AlexsMobs.isHalloween()) {
               j = 7;
            } else if (random.nextBoolean()) {
               return false;
            }

            int i = iServerWorld.getMaxLocalRawBrightness(pos);
            return i > random.nextInt(j) ? false : checkMobSpawnRules(entityType, iServerWorld, reason, pos, random);
         }
      }
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return !this.requiresCustomPersistence() && !this.hasCustomName();
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasCustomName() || this.lastGivenStack != null;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.underminerSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DWARF, true);
      builder.define(HIDING, false);
      builder.define(VISUALLY_MINING, false);
      builder.define(TARGETED_BLOCK_POS, Optional.empty());
      builder.define(MINING_PROGRESS, 0.0F);
      builder.define(VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Dwarf", this.isDwarf());
      compound.putBoolean("Hiding", this.isHiding());
      compound.putInt("Variant", this.getVariant());
      compound.putInt("ResetItemTime", this.resetStackTime);
      compound.putInt("MineCooldown", this.mineCooldown);
      if (this.lastGivenStack != null) {
         AMCompat.put(compound, "MineStack", AMCompat.saveItem(this.level().registryAccess(), this.lastGivenStack));
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setDwarf(AMCompat.getBoolean(compound, "Dwarf"));
      this.setHiding(AMCompat.getBoolean(compound, "Hiding"));
      this.setVariant(AMCompat.getInt(compound, "Variant"));
      this.resetStackTime = AMCompat.getInt(compound, "ResetItemTime");
      this.mineCooldown = AMCompat.getInt(compound, "MineCooldown");
      if (AMCompat.contains(compound, "MineStack")) {
         this.lastGivenStack = AMCompat.loadItem(this.level().registryAccess(), AMCompat.getCompound(compound, "MineStack"));
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.UNDERMINER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.UNDERMINER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.UNDERMINER_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
   }

   public boolean isDwarf() {
      return (Boolean)this.entityData.get(DWARF) && !this.isExtraSpooky();
   }

   public void setDwarf(boolean phasing) {
      this.entityData.set(DWARF, phasing);
   }

   public int getVariant() {
      return this.isExtraSpooky() ? 1 : (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int i) {
      this.entityData.set(VARIANT, i);
   }

   public boolean isHiding() {
      return (Boolean)this.entityData.get(HIDING);
   }

   public void setHiding(boolean phasing) {
      this.entityData.set(HIDING, phasing);
   }

   @Nullable
   public BlockPos getMiningPos() {
      return (BlockPos)((Optional)this.getEntityData().get(TARGETED_BLOCK_POS)).orElse(null);
   }

   public void setMiningPos(@Nullable BlockPos beamTarget) {
      this.getEntityData().set(TARGETED_BLOCK_POS, Optional.ofNullable(beamTarget));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2000000476837158, true));
      this.goalSelector.addGoal(2, new EntityUnderminer.MineGoal());
      this.goalSelector.addGoal(3, new MonsterAIWalkThroughHallsOfStructure(this, 0.5, 60, StructureTags.MINESHAFT, 50.0));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return !source.is(DamageTypes.MAGIC) && source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.isCreativePlayer() || super.isInvulnerableTo(source);
   }

   private float calculateDistanceToFloor() {
      BlockPos floor = AMBlockPos.fromCoords(this.getX(), this.getBoundingBox().maxY, this.getZ());

      while (!this.level().getBlockState(floor).isFaceSturdy(this.level(), floor, Direction.UP) && floor.getY() > AMCompat.minBuildHeight(this.level())) {
         floor = floor.below();
      }

      return (float)(this.getBoundingBox().minY - (floor.getY() + 1));
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.6, 0.9));
      } else {
         super.travel(travelVector);
      }
   }

   protected void populateDefaultEquipmentSlots(RandomSource p_218949_, DifficultyInstance p_218950_) {
      super.populateDefaultEquipmentSlots(p_218949_, p_218950_);
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AMItemRegistry.GHOSTLY_PICKAXE.get()));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.5F);
   }

   protected float getEquipmentDropChance(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND ? 0.5F : super.getEquipmentDropChance(slot);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData
   ) {
      spawnData = super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnData);
      RandomSource randomsource = level.getRandom();
      this.populateDefaultEquipmentSlots(randomsource, difficultyInstance);
      if (this.random.nextFloat() < 0.3F) {
         this.setVariant(this.random.nextInt(2));
         this.setDwarf(false);
      } else {
         this.setDwarf(true);
      }

      return spawnData;
   }

   public boolean isFullyHidden() {
      return this.isHiding() && this.hidingProgress >= 10.0F;
   }

   public void tick() {
      this.noPhysics = true;
      super.tick();
      this.prevHidingProgress = this.hidingProgress;
      this.noPhysics = false;
      if (this.isHiding() && this.hidingProgress < 10.0F) {
         this.hidingProgress++;
      }

      if (!this.isHiding() && this.hidingProgress > 0.0F) {
         this.hidingProgress--;
      }

      if (!this.level().isClientSide()) {
         double xzSpeed = this.getDeltaMovement().horizontalDistance();
         double distToFloor = Mth.clamp(this.calculateDistanceToFloor(), -1.0F, 1.0F);
         if (Math.abs(distToFloor) > 0.01 && xzSpeed < 0.05 && !this.isActuallyInAWall()) {
            if (distToFloor < 0.0) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -Math.min(distToFloor * 0.10000000149011612, 0.0), 0.0));
            } else if (distToFloor > 0.0) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -Math.max(distToFloor * 0.10000000149011612, 0.0), 0.0));
            }
         }

         if (this.lastPosition != null && this.lastPosition.distSqr(this.blockPosition()) > 2.5 && Math.abs(distToFloor) < 0.5) {
            this.playSound(AMSoundRegistry.UNDERMINER_STEP.get(), 1.0F, 0.75F + this.random.nextFloat() * 0.25F);
            this.lastPosition = this.blockPosition();
            if (this.random.nextFloat() < 0.015F && !this.level().canSeeSky(this.lastPosition)) {
               this.playSound((SoundEvent)SoundEvents.AMBIENT_CAVE.value(), 3.0F, 0.75F + this.random.nextFloat() * 0.25F);
            }
         }

         Player player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), AMConfig.underminerDisappearDistance, true);
         if (player == null || this.lastGivenStack != null || this.getTarget() != null && this.getTarget().isAlive()) {
            this.setHiding(false);
         } else {
            this.setHiding(true);
            this.lookAt(player, 360.0F, 360.0F);
         }
      }

      this.setYBodyRot(this.getYRot());
      if (this.mineCooldown > 0) {
         this.mineCooldown--;
      }

      if (this.resetStackTime > 0) {
         this.resetStackTime--;
         if (this.resetStackTime == 0) {
            this.lastGivenStack = null;
         }
      }

      if ((Boolean)this.entityData.get(VISUALLY_MINING)) {
         this.swing(InteractionHand.MAIN_HAND);
      }
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canPickUpLoot() {
      return true;
   }

   public boolean wantsToPickUp(ItemStack stack) {
      return stack.is(AMTagRegistry.UNDERMINER_ORES);
   }

   protected void pickUpItem(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      if (itemstack.is(AMTagRegistry.UNDERMINER_ORES)) {
         this.onItemPickup(itemEntity);
         this.take(itemEntity, itemstack.getCount());
         itemEntity.discard();
         this.mineAIFlag = this.lastGivenStack == null || !ItemStack.isSameItem(this.lastGivenStack, itemEntity.getItem());
         this.lastGivenStack = itemEntity.getItem();
         this.resetStackTime = 2000 + this.random.nextInt(1200);
         this.mineCooldown = 0;
      } else {
         super.pickUpItem(itemEntity);
      }
   }

   public void jumpFromGround() {
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean isExtraSpooky() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return AlexsMobs.isAprilFools() || AlexsMobs.isHalloween() || s != null && s.toLowerCase().contains("herobrine");
   }

   private boolean isActuallyInAWall() {
      float f = AMCompat.width(this.getDimensions(this.getPose())) * 0.1F;
      AABB aabb = AABB.ofSize(this.getEyePosition(), f, 1.0E-6, f);
      return BlockPos.betweenClosedStream(aabb)
         .anyMatch(
            p_201942_ -> {
               BlockState blockstate = this.level().getBlockState(p_201942_);
               return !blockstate.isAir()
                  && blockstate.isSuffocating(this.level(), p_201942_)
                  && Shapes.joinIsNotEmpty(
                     blockstate.getCollisionShape(this.level(), p_201942_).move(p_201942_.getX(), p_201942_.getY(), p_201942_.getZ()),
                     Shapes.create(aabb),
                     BooleanOp.AND
                  );
            }
         );
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public float getBrightness() {
      return 1.0F;
   }

   public float getMiningProgress() {
      return (Float)this.entityData.get(MINING_PROGRESS);
   }

   public void setMiningProgress(float f) {
      this.entityData.set(MINING_PROGRESS, f);
   }

   private List<BlockPos> getNearbyObscuredOres(int range, int maxOres) {
      List<BlockPos> obscuredBlocks = new ArrayList<>();
      BlockPos blockpos = this.blockPosition();
      int half = range / 2;

      for (int i = 0; i <= half && i >= -half; i = (i <= 0 ? 1 : 0) - i) {
         for (int j = 0; j <= range && j >= -range; j = (j <= 0 ? 1 : 0) - j) {
            for (int k = 0; k <= range && k >= -range; k = (k <= 0 ? 1 : 0) - k) {
               BlockPos offset = blockpos.offset(j, i, k);
               BlockState state = this.level().getBlockState(offset);
               if (this.isValidMiningBlock(state)) {
                  if (obscuredBlocks.size() >= maxOres) {
                     break;
                  }

                  BlockPos obscured = this.getObscuringBlockOf(offset);
                  if (obscured != null) {
                     obscuredBlocks.add(obscured);
                  }
               }
            }
         }
      }

      return obscuredBlocks;
   }

   private boolean isValidMiningBlock(BlockState state) {
      return this.lastGivenStack != null ? this.lastGivenStack.getItem() == state.getBlock().asItem() : state.is(Blocks.ORES);
   }

   public void aiStep() {
      this.updateSwingTime();
      super.aiStep();
   }

   public boolean isAttackable() {
      return !this.isFullyHidden() && super.isAttackable();
   }

   public boolean skipAttackInteraction(Entity entity) {
      return this.isFullyHidden() || super.skipAttackInteraction(entity);
   }

   private BlockPos getObscuringBlockOf(BlockPos target) {
      Vec3 eyes = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      HitResult hitResult = this.level().clip(new ClipContext(eyes, Vec3.atCenterOf(target), Block.COLLIDER, Fluid.NONE, this));
      if (hitResult instanceof BlockHitResult && !((BlockHitResult)hitResult).getBlockPos().equals(target)) {
         BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
         return pos.distSqr(target) > 4.0 ? null : pos;
      } else {
         return null;
      }
   }

   private boolean hasPick() {
      return this.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.GHOSTLY_PICKAXE.get());
   }

   private class MineGoal extends Goal {
      private BlockPos minePretendPos = null;
      private BlockState minePretendStartState = null;
      private int mineTime = 0;

      public MineGoal() {
         this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
      }

      public boolean canUse() {
         if (EntityUnderminer.this.mineCooldown == 0
            && EntityUnderminer.this.hasPick()
            && !EntityUnderminer.this.isHiding()
            && !EntityUnderminer.this.isActuallyInAWall()
            && EntityUnderminer.this.getRandom().nextInt(30) == 0) {
            List<BlockPos> obscuredOres = EntityUnderminer.this.getNearbyObscuredOres(16, 8);
            BlockPos nearest = null;
            double nearestDist = 1.7976931348623157E308;
            if (!obscuredOres.isEmpty()) {
               for (BlockPos obscuredPos : obscuredOres) {
                  double dist = EntityUnderminer.this.position().distanceTo(Vec3.atCenterOf(obscuredPos));
                  if (nearestDist > dist) {
                     nearest = obscuredPos;
                     nearestDist = dist;
                  }
               }
            }

            EntityUnderminer.this.mineAIFlag = false;
            this.minePretendPos = nearest;
            return this.minePretendPos != null;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.minePretendPos != null
            && EntityUnderminer.this.hasPick()
            && !EntityUnderminer.this.isHiding()
            && !EntityUnderminer.this.mineAIFlag
            && this.minePretendStartState != null
            && this.minePretendStartState.equals(EntityUnderminer.this.level().getBlockState(this.minePretendPos))
            && this.mineTime < 200;
      }

      public void start() {
         if (this.minePretendPos != null) {
            this.minePretendStartState = EntityUnderminer.this.level().getBlockState(this.minePretendPos);
         }
      }

      public void stop() {
         if (this.minePretendPos != null
            && this.minePretendStartState != null
            && !this.minePretendStartState.equals(EntityUnderminer.this.level().getBlockState(this.minePretendPos))) {
            for (ServerPlayer serverplayerentity : EntityUnderminer.this.level()
               .getEntitiesOfClass(ServerPlayer.class, EntityUnderminer.this.getBoundingBox().inflate(12.0, 12.0, 12.0))) {
               AMAdvancementTriggerRegistry.UNDERMINE_UNDERMINER.trigger(serverplayerentity);
            }
         }

         this.minePretendPos = null;
         this.minePretendStartState = null;
         this.mineTime = 0;
         EntityUnderminer.this.entityData.set(EntityUnderminer.VISUALLY_MINING, false);
         EntityUnderminer.this.setMiningPos(null);
         EntityUnderminer.this.setMiningProgress(0.0F);
         if (EntityUnderminer.this.resetStackTime > 0) {
            EntityUnderminer.this.mineCooldown = 40;
         } else {
            EntityUnderminer.this.mineCooldown = 200 + EntityUnderminer.this.random.nextInt(200);
         }
      }

      public void tick() {
         if (this.minePretendPos != null && this.minePretendStartState != null) {
            this.mineTime++;
            double distSqr = EntityUnderminer.this.distanceToSqr(
               this.minePretendPos.getX() + 0.5F, this.minePretendPos.getY() + 0.5F, this.minePretendPos.getZ() + 0.5F
            );
            if (distSqr < 6.5) {
               EntityUnderminer.this.getNavigation().stop();
               if (EntityUnderminer.this.getNavigation().isDone()) {
                  EntityUnderminer.this.setMiningPos(this.minePretendPos);
                  EntityUnderminer.this.setMiningProgress((1.0F + (float)Math.cos(this.mineTime * 0.1F + 3.141592653589793)) * 0.5F);
                  double d1 = this.minePretendPos.getZ() + 0.5F - EntityUnderminer.this.getZ();
                  double d3 = this.minePretendPos.getY() + 0.5F - EntityUnderminer.this.getY();
                  double d2 = this.minePretendPos.getX() + 0.5F - EntityUnderminer.this.getX();
                  float f = Mth.sqrt((float)(d2 * d2 + d1 * d1));
                  EntityUnderminer.this.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
                  EntityUnderminer.this.setXRot((float)(Mth.atan2(d3, f) * 57.2957763671875) + (float)Math.sin(EntityUnderminer.this.tickCount * 0.1F));
                  EntityUnderminer.this.entityData.set(EntityUnderminer.VISUALLY_MINING, true);
                  if (this.mineTime % 10 == 0) {
                     SoundType soundType = this.minePretendStartState
                        .getBlock()
                        .getSoundType(this.minePretendStartState, EntityUnderminer.this.level(), this.minePretendPos, EntityUnderminer.this);
                     EntityUnderminer.this.playSound(soundType.getHitSound());
                  }
               }
            } else {
               EntityUnderminer.this.entityData.set(EntityUnderminer.VISUALLY_MINING, false);
               EntityUnderminer.this.setMiningPos(null);
               EntityUnderminer.this.getNavigation()
                  .moveTo(this.minePretendPos.getX() + 0.5F, this.minePretendPos.getY() + 0.5F, this.minePretendPos.getZ() + 0.5F, 1.0);
            }
         }
      }
   }

   private class PathNavigator extends GroundPathNavigation {
      public PathNavigator(EntityUnderminer underminer, Level level) {
         super(underminer, EntityUnderminer.this.level());
      }

      protected boolean canUpdatePath() {
         return !this.mob.isPassenger();
      }

      protected Vec3 getTempMobPos() {
         return this.mob.position();
      }
   }
}
