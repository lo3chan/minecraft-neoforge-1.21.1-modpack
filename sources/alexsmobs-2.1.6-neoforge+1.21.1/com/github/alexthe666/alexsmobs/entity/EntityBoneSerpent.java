package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentAIFindLava;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentAIJump;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentAIMeleeJump;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.LavaAndWaterAIRandomSwimming;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class EntityBoneSerpent extends Monster {
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityBoneSerpent.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final Predicate<LivingEntity> NOT_RIDING_STRADDLEBOARD_FRIENDLY = entity -> entity.isAlive()
      && (
         entity.getVehicle() == null
            || !(entity.getVehicle() instanceof EntityStraddleboard)
            || !((EntityStraddleboard)entity.getVehicle()).shouldSerpentFriend()
      );
   private static final Predicate<EntityStraddleboard> STRADDLEBOARD_FRIENDLY = entity -> entity.isVehicle() && entity.shouldSerpentFriend();
   public int jumpCooldown = 0;
   private boolean isLandNavigator;
   private int boardCheckCooldown = 0;
   private EntityStraddleboard boardToBoast = null;

   protected EntityBoneSerpent(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.switchNavigator(false);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.boneSeprentSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.BONE_SERPENT_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.BONE_SERPENT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.BONE_SERPENT_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 25.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 1.4500000476837158);
   }

   public int getMaxFallDistance() {
      return 256;
   }

   public boolean canBeAffected(MobEffectInstance potioneffectIn) {
      return potioneffectIn.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(potioneffectIn);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      if (!worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER) && !worldIn.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) {
         return this.isInLava() ? -1.0F / 0.0F : 0.0F;
      } else {
         return 10.0F;
      }
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeLeashed(Player player) {
      return true;
   }

   public boolean checkSpawnObstruction(LevelReader worldIn) {
      return worldIn.isUnobstructed(this);
   }

   public static boolean canBoneSerpentSpawn(
      EntityType<EntityBoneSerpent> p_234314_0_, LevelAccessor p_234314_1_, MobSpawnType p_234314_2_, BlockPos p_234314_3_, RandomSource p_234314_4_
   ) {
      MutableBlockPos blockpos$mutable = p_234314_3_.mutable();

      do {
         blockpos$mutable.move(Direction.UP);
      } while (p_234314_1_.getFluidState(blockpos$mutable).is(FluidTags.LAVA));

      return p_234314_1_.getBlockState(blockpos$mutable).isAir();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(0, new BoneSerpentAIFindLava(this));
      this.goalSelector.addGoal(1, new BoneSerpentAIMeleeJump(this));
      this.goalSelector.addGoal(2, new BoneSerpentAIJump(this, 10));
      this.goalSelector.addGoal(3, new LavaAndWaterAIRandomSwimming(this, 1.0, 8));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      if (!AMConfig.neutralBoneSerpents) {
         this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
         this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillager.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
      }

      this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, WitherSkeleton.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
      this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, EntitySoulVulture.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
   }

   public void travel(Vec3 travelVector) {
      boolean liquid = this.isInLava() || this.isInWater();
      if (this.isEffectiveAi() && liquid) {
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

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = this.createNavigation(this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new EntityBoneSerpent.BoneSerpentMoveController(this);
         this.navigation = new BoneSerpentPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      entities.stream().filter(entity -> !(entity instanceof EntityBoneSerpentPart) && entity.isPushable()).forEach(entity -> entity.push(this));
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.FALL)
         || source.is(DamageTypes.DROWN)
         || source.is(DamageTypes.IN_WALL)
         || source.is(DamageTypes.LAVA)
         || source.is(DamageTypeTags.IS_FIRE)
         || super.isInvulnerableTo(source);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHILD_UUID, Optional.empty());
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void tick() {
      super.tick();
      this.portalProcess = null;
      boolean ground = !this.isInLava() && !this.isInWater() && this.onGround();
      if (this.jumpCooldown > 0) {
         this.jumpCooldown--;
         float f2 = -((float)this.getDeltaMovement().y * 57.295776F);
         this.setXRot(f2);
      }

      if (ground) {
         if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }
      } else if (this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (!this.level().isClientSide()) {
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity partParent = this;
            int segments = 7 + this.getRandom().nextInt(8);

            for (int i = 0; i < segments; i++) {
               EntityBoneSerpentPart part = new EntityBoneSerpentPart(AMEntityRegistry.BONE_SERPENT_PART.get(), partParent, 0.9F, 180.0F, 0.0F);
               part.setParent(partParent);
               part.setBodyIndex(i);
               if (partParent == this) {
                  this.setChildId(part.getUUID());
               }

               part.setInitialPartPos(this);
               partParent = part;
               if (i == segments - 1) {
                  part.setTail(true);
               }

               this.level().addFreshEntity(part);
            }
         }

         if (this.boardCheckCooldown > 0) {
            this.boardCheckCooldown--;
         } else {
            this.boardCheckCooldown = 100 + this.random.nextInt(150);
            List<EntityStraddleboard> list = this.level()
               .getEntitiesOfClass(EntityStraddleboard.class, this.getBoundingBox().inflate(100.0, 15.0, 100.0), STRADDLEBOARD_FRIENDLY);
            EntityStraddleboard closestBoard = null;

            for (EntityStraddleboard board : list) {
               if (closestBoard == null || this.distanceTo(closestBoard) > this.distanceTo(board)) {
                  closestBoard = board;
               }
            }

            this.boardToBoast = closestBoard;
         }

         if (this.boardToBoast != null) {
            if (this.distanceTo(this.boardToBoast) > 200.0F) {
               this.boardToBoast = null;
            } else {
               if (this.jumpCooldown == 0 && (this.isInLava() || this.isInWater()) && this.distanceTo(this.boardToBoast) < 15.0F) {
                  float up = 0.7F + this.getRandom().nextFloat() * 0.8F;
                  Vec3 vector3d1 = this.getLookAngle();
                  this.setDeltaMovement(this.getDeltaMovement().add(vector3d1.x() * 0.6, up, vector3d1.y() * 0.6));
                  this.getNavigation().stop();
                  this.jumpCooldown = this.getRandom().nextInt(300) + 100;
               }

               if (this.distanceTo(this.boardToBoast) > 5.0F) {
                  this.getNavigation().moveTo(this.boardToBoast, 1.5);
               } else {
                  this.getNavigation().stop();
               }
            }
         }
      }
   }

   static class BoneSerpentMoveController extends MoveControl {
      private final EntityBoneSerpent dolphin;

      public BoneSerpentMoveController(EntityBoneSerpent dolphinIn) {
         super(dolphinIn);
         this.dolphin = dolphinIn;
      }

      public void tick() {
         if (this.dolphin.isInWater() || this.dolphin.isInLava()) {
            this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0, 0.005, 0.0));
         }

         if (this.operation == Operation.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
            double d0 = this.wantedX - this.dolphin.getX();
            double d1 = this.wantedY - this.dolphin.getY();
            double d2 = this.wantedZ - this.dolphin.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < 2.500000277905201E-7) {
               this.mob.setZza(0.0F);
            } else {
               float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
               this.dolphin.setYRot(this.rotlerp(this.dolphin.getYRot(), f, 10.0F));
               this.dolphin.yBodyRot = this.dolphin.getYRot();
               this.dolphin.yHeadRot = this.dolphin.getYRot();
               float f1 = (float)(this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
               if (!this.dolphin.isInWater() && !this.dolphin.isInLava()) {
                  this.dolphin.setSpeed(f1 * 0.1F);
               } else {
                  this.dolphin.setSpeed(f1 * 0.02F);
                  float f2 = -((float)(Mth.atan2(d1, Mth.sqrt((float)(d0 * d0 + d2 * d2))) * 57.2957763671875));
                  f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
                  this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0, this.dolphin.getSpeed() * d1 * 0.6, 0.0));
                  this.dolphin.setXRot(this.rotlerp(this.dolphin.getXRot(), f2, 1.0F));
                  float f3 = Mth.cos(this.dolphin.getXRot() * 0.017453292F);
                  float f4 = Mth.sin(this.dolphin.getXRot() * 0.017453292F);
                  this.dolphin.zza = f3 * f1;
                  this.dolphin.yya = -f4 * f1;
               }
            }
         } else {
            this.dolphin.setSpeed(0.0F);
            this.dolphin.setXxa(0.0F);
            this.dolphin.setYya(0.0F);
            this.dolphin.setZza(0.0F);
         }
      }
   }
}
