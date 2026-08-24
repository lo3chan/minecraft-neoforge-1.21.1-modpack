package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityCrimsonMosquito extends Monster {
   public static final ResourceLocation FULL_LOOT = AMCompat.rl("alexsmobs", "entities/crimson_mosquito_full");
   public static final ResourceLocation FROM_FLY_LOOT = AMCompat.rl("alexsmobs", "entities/crimson_mosquito_fly");
   public static final ResourceLocation FROM_FLY_FULL_LOOT = AMCompat.rl("alexsmobs", "entities/crimson_mosquito_fly_full");
   protected static final EntityDimensions FLIGHT_SIZE = EntityDimensions.fixed(1.2F, 1.8F);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> BLOOD_LEVEL = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SHRINKING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_FLY = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> MOSQUITO_SCALE = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> SICK = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> LURING_LAVIATHAN = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FLEEING_ENTITY = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
   private static final Predicate<LivingEntity> REPELLENT = mob -> mob.hasEffect(AMCompat.effect(AMEffectRegistry.MOSQUITO_REPELLENT.get()))
      || mob instanceof EntityTriops;
   private static final Predicate<LivingEntity> NO_REPELLENT = mob -> !mob.hasEffect(AMCompat.effect(AMEffectRegistry.MOSQUITO_REPELLENT.get()));
   public float prevFlyProgress;
   public float flyProgress;
   public float prevShootProgress;
   public float shootProgress;
   public int shootingTicks;
   public int randomWingFlapTick = 0;
   private int flightTicks = 0;
   private int sickTicks = 0;
   private boolean prevFlying = false;
   private int spitCooldown = 0;
   private int loopSoundTick = 0;
   private int drinkTime = 0;
   public float prevMosquitoScale = 1.0F;
   private int repellentCheckTime = 0;
   private Vec3 fleePos = null;

   protected EntityCrimsonMosquito(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.moveControl = new EntityCrimsonMosquito.MoveHelperController(this);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
   }

   public boolean hasLuringLaviathan() {
      return (Integer)this.entityData.get(LURING_LAVIATHAN) != -1;
   }

   public void onSpawnFromFly() {
      this.prevMosquitoScale = 0.2F;
      this.setShrink(false);
      this.setMosquitoScale(0.2F);
      this.setFromFly(true);

      for (int j = 0; j < 4; j++) {
         int red = (int)((this.random.nextDouble() * 0.5 + 0.5) * 255.0);
         this.level()
            .addParticle(
               ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF000000 | red << 16),
               this.getX() + this.random.nextDouble() / 2.0,
               this.getY(0.5),
               this.getZ() + this.random.nextDouble() / 2.0,
               0.0,
               0.0,
               0.0
            );
      }
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MOSQUITO_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MOSQUITO_DIE.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.crimsonMosquitoSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      if (this.getBloodLevel() > 0) {
         return this.isFromFly() ? AMCompat.lootKey(FROM_FLY_FULL_LOOT) : AMCompat.lootKey(FULL_LOOT);
      } else {
         return this.isFromFly() ? AMCompat.lootKey(FROM_FLY_LOOT) : super.getDefaultLootTable();
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyTowardsTarget(this));
      this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyAwayFromTarget(this));
      this.goalSelector.addGoal(3, new EntityCrimsonMosquito.RandomFlyGoal(this));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{EntityCrimsonMosquito.class, EntityWarpedMosco.class}));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 20, true, false, NO_REPELLENT));
      this.targetSelector
         .addGoal(
            2,
            new EntityAINearestTarget3D<LivingEntity>(
               this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)
            )
         );
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, EntityTriops.class, 16.0F, 1.3, 1.0));
   }

   public static boolean canMosquitoSpawn(
      EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn
   ) {
      BlockPos blockpos = pos.below();
      boolean spawnBlock = worldIn.getBlockState(blockpos).canOcclude();
      return reason == MobSpawnType.SPAWNER
         || spawnBlock
            && worldIn.getBlockState(blockpos).isValidSpawn(worldIn, blockpos, typeIn)
            && isDarkEnoughToSpawn(worldIn, pos, randomIn)
            && checkMobSpawnRules(AMEntityRegistry.CRIMSON_MOSQUITO.get(), worldIn, reason, pos, randomIn);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("FlightTicks", this.flightTicks);
      compound.putInt("SickTicks", this.sickTicks);
      compound.putFloat("MosquitoScale", this.getMosquitoScale());
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("Shrinking", this.isShrinking());
      compound.putBoolean("IsFromFly", this.isFromFly());
      compound.putBoolean("Sick", this.isSick());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.flightTicks = AMCompat.getInt(compound, "FlightTicks");
      this.sickTicks = AMCompat.getInt(compound, "SickTicks");
      this.setMosquitoScale(AMCompat.getFloat(compound, "MosquitoScale"));
      this.setFlying(AMCompat.getBoolean(compound, "Flying"));
      this.setShrink(AMCompat.getBoolean(compound, "Shrinking"));
      this.setFromFly(AMCompat.getBoolean(compound, "IsFromFly"));
      this.setSick(AMCompat.getBoolean(compound, "Sick"));
   }

   private void spit(LivingEntity target) {
      if (!this.isSick()) {
         EntityMosquitoSpit llamaspitentity = new EntityMosquitoSpit(this.level(), this);
         double d0 = target.getX() - this.getX();
         double d1 = target.getY(0.3333333333333333) - llamaspitentity.getY();
         double d2 = target.getZ() - this.getZ();
         float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.2F;
         llamaspitentity.shoot(d0, d1 + f, d2, 1.5F, 10.0F);
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

         if (this.getBloodLevel() > 0) {
            this.setBloodLevel(this.getBloodLevel() - 1);
         }

         this.level().addFreshEntity(llamaspitentity);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.FALL)
         || source.is(DamageTypes.DROWN)
         || source.is(DamageTypes.IN_WALL)
         || source.is(DamageTypes.LAVA)
         || source.is(DamageTypeTags.IS_FIRE)
         || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.getEntity() != null && this.getRootVehicle() == source.getEntity().getRootVehicle()) {
         return super.hurt(source, amount * 0.333F);
      } else {
         if (this.flightTicks < 0) {
            this.flightTicks = 0;
         }

         return super.hurt(source, amount);
      }
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.stopRiding();
      } else {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         if (this.isPassenger()) {
            Entity mount = this.getVehicle();
            if (mount instanceof LivingEntity livingEntity) {
               this.yBodyRot = livingEntity.yBodyRot;
               this.setYRot(livingEntity.getYRot());
               this.yHeadRot = livingEntity.yHeadRot;
               this.yRotO = livingEntity.yHeadRot;
               float radius = 1.0F;
               float angle = 0.017453292F * livingEntity.yBodyRot;
               double extraX = 1.0F * Mth.sin(3.1415927F + angle);
               double extraZ = 1.0F * Mth.cos(angle);
               this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
               if (!mount.isAlive() || mount instanceof Player && ((Player)mount).isCreative()) {
                  this.removeVehicle();
               }

               if (!this.level().isClientSide()) {
                  if (this.drinkTime % 20 == 0 && this.isAlive()) {
                     boolean mungus = AMConfig.warpedMoscoTransformation && mount instanceof EntityMungus && ((EntityMungus)mount).isWarpedMoscoReady();
                     if (AMCompat.hurt(mount, this.damageSources().mobAttack(this), mungus ? 7.0F : 2.0F)) {
                        if (mungus) {
                           ((EntityMungus)mount).disableExplosion();
                        }

                        boolean sick = this.isNonMungusWarpedTrigger(mount);
                        if (sick || mungus) {
                           if (!this.isSick()) {
                              for (ServerPlayer serverplayerentity : this.level()
                                 .getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(40.0, 25.0, 40.0))) {
                                 AMAdvancementTriggerRegistry.MOSQUITO_SICK.trigger(serverplayerentity);
                              }
                           }

                           this.setSick(true);
                           this.setFlying(false);
                           this.flightTicks = -150 - this.random.nextInt(200);
                        }

                        this.gameEvent(GameEvent.EAT);
                        this.playSound(SoundEvents.HONEY_DRINK, this.getSoundVolume(), this.getVoicePitch());
                        this.setBloodLevel(this.getBloodLevel() + 1);
                        if (this.getBloodLevel() > 3) {
                           this.removeVehicle();
                           AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
                           this.setFlying(false);
                           this.flightTicks = -15;
                        }
                     }
                  }

                  if (this.drinkTime > 81) {
                     this.drinkTime = -20 - this.random.nextInt(20);
                     this.removeVehicle();
                     AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
                     this.setFlying(false);
                     this.flightTicks = -15;
                  }
               }
            }
         }
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(SHOOTING, false);
      builder.define(SICK, false);
      builder.define(BLOOD_LEVEL, 0);
      builder.define(SHRINKING, false);
      builder.define(FROM_FLY, false);
      builder.define(MOSQUITO_SCALE, 1.0F);
      builder.define(LURING_LAVIATHAN, -1);
      builder.define(FLEEING_ENTITY, -1);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   public void setupShooting() {
      this.entityData.set(SHOOTING, true);
      this.shootingTicks = 5;
   }

   public int getLuringLaviathan() {
      return (Integer)this.entityData.get(LURING_LAVIATHAN);
   }

   public void setLuringLaviathan(int lure) {
      this.entityData.set(LURING_LAVIATHAN, lure);
   }

   public int getFleeingEntityId() {
      return (Integer)this.entityData.get(FLEEING_ENTITY);
   }

   public void setFleeingEntityId(int lure) {
      this.entityData.set(FLEEING_ENTITY, lure);
   }

   public int getBloodLevel() {
      return Math.min((Integer)this.entityData.get(BLOOD_LEVEL), 4);
   }

   public void setBloodLevel(int bloodLevel) {
      this.entityData.set(BLOOD_LEVEL, bloodLevel);
   }

   public boolean isShrinking() {
      return (Boolean)this.entityData.get(SHRINKING);
   }

   public boolean isFromFly() {
      return (Boolean)this.entityData.get(FROM_FLY);
   }

   public void setShrink(boolean shrink) {
      this.entityData.set(SHRINKING, shrink);
   }

   public void setFromFly(boolean fromFly) {
      this.entityData.set(FROM_FLY, fromFly);
   }

   public float getMosquitoScale() {
      return (Float)this.entityData.get(MOSQUITO_SCALE);
   }

   public void setMosquitoScale(float scale) {
      this.entityData.set(MOSQUITO_SCALE, scale);
   }

   public boolean isSick() {
      return (Boolean)this.entityData.get(SICK);
   }

   public void setSick(boolean shrink) {
      this.entityData.set(SICK, shrink);
   }

   public void tick() {
      super.tick();
      boolean shooting = (Boolean)this.entityData.get(SHOOTING);
      if (this.prevFlying != this.isFlying()) {
         this.refreshDimensions();
      }

      if (shooting) {
         if (this.shootProgress < 5.0F) {
            this.shootProgress++;
         }
      } else if (this.shootProgress > 0.0F) {
         this.shootProgress--;
      }

      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.isPassenger()) {
            this.setFlying(false);
         }

         if (this.isFlying()) {
            this.setNoGravity(true);
         } else {
            this.setNoGravity(false);
         }

         LivingEntity target = this.getTarget();
         if (this.getFleeingEntityId() != -1) {
            Entity fleeing = this.level().getEntity(this.getFleeingEntityId());
            if (fleeing instanceof LivingEntity living && REPELLENT.test(living) && this.distanceTo(living) < 20.0F) {
               this.setTarget(null);
               this.setLastHurtByMob(null);
               if (this.isPassenger()) {
                  this.stopRiding();
               }

               if (this.fleePos != null && !(this.fleePos.distanceTo(this.position()) < 3.0) && this.random.nextInt(40) != 0) {
                  this.setFlying(true);
                  this.moveControl.setWantedPosition(this.fleePos.x, this.fleePos.y + 1.0, this.fleePos.z, 1.2000000476837158);
               } else {
                  Vec3 vec = LandRandomPos.getPosAway(this, 8, 4, fleeing.position());
                  if (vec != null) {
                     this.fleePos = vec;
                  }
               }
            } else {
               this.setFleeingEntityId(-1);
            }
         } else {
            if (target == null && this.tickCount - this.repellentCheckTime > 50) {
               this.repellentCheckTime = this.tickCount;
               LivingEntity closestRepel = null;

               for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(30.0), REPELLENT)) {
                  if (closestRepel == null || entity.distanceTo(this) < closestRepel.distanceTo(this)) {
                     closestRepel = entity;
                  }
               }

               if (closestRepel != null) {
                  this.setFleeingEntityId(closestRepel.getId());
               }
            }

            if (target != null && REPELLENT.test(target) && this.distanceTo(target) < 20.0F) {
               this.setFleeingEntityId(target.getId());
            }
         }

         if (this.hasLuringLaviathan()) {
            this.setTarget(null);
            this.setLastHurtByMob(null);
            Entity entityx = this.level().getEntity(this.getLuringLaviathan());
            if (entityx instanceof EntityLaviathan && ((EntityLaviathan)entityx).isChilling()) {
               Vec3 vec = ((EntityLaviathan)entityx).getLureMosquitoPos();
               this.setFlying(true);
               this.lookAt(entityx, 10.0F, 10.0F);
               this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 0.699999988079071);
            } else {
               this.setLuringLaviathan(-1);
            }
         }
      }

      if (this.flyProgress == 0.0F && this.random.nextInt(200) == 0) {
         this.randomWingFlapTick = 5 + this.random.nextInt(15);
      }

      if (this.randomWingFlapTick > 0) {
         this.randomWingFlapTick--;
      }

      if (!this.level().isClientSide()
         && this.onGround()
         && !this.isFlying()
         && (this.flightTicks >= 0 && this.random.nextInt(5) == 0 || this.getTarget() != null)) {
         this.setFlying(true);
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
         this.setOnGround(false);
         this.hasImpulse = true;
      }

      if (this.flightTicks < 0) {
         this.flightTicks++;
      }

      if (!this.level().isClientSide() && this.isFlying()) {
         this.flightTicks++;
         if (this.flightTicks > 200 && (this.getTarget() == null || !this.getTarget().isAlive())) {
            BlockPos above = this.getGroundPosition(this.blockPosition().above());
            if (this.level().getFluidState(above).isEmpty() && !this.level().getBlockState(above).isAir()) {
               this.getDeltaMovement().add(0.0, -0.2, 0.0);
               if (this.onGround()) {
                  this.setFlying(false);
                  this.flightTicks = -150 - this.random.nextInt(200);
               }
            }
         }
      }

      this.prevMosquitoScale = this.getMosquitoScale();
      if (this.isShrinking()) {
         if (this.getMosquitoScale() > 0.4F) {
            this.setMosquitoScale(this.getMosquitoScale() - 0.1F);
         }
      } else if (this.getMosquitoScale() < 1.0F && !this.isSick()) {
         this.setMosquitoScale(this.getMosquitoScale() + 0.05F);
      }

      if (!this.level().isClientSide() && this.shootingTicks > 0) {
         this.shootingTicks--;
         if (this.shootingTicks == 0) {
            if (this.getTarget() != null && this.getBloodLevel() > 0) {
               this.spit(this.getTarget());
            }

            this.entityData.set(SHOOTING, false);
         }
      }

      if (this.isFlying()) {
         if (this.loopSoundTick == 0) {
            this.gameEvent(AMPlatform.ENTITY_ACTION);
            this.playSound(AMSoundRegistry.MOSQUITO_LOOP.get(), this.getSoundVolume(), this.getVoicePitch());
         }

         this.loopSoundTick++;
         if (this.loopSoundTick > 100) {
            this.loopSoundTick = 0;
         }
      }

      if (this.isPassenger()) {
         if (this.drinkTime < 0) {
            this.drinkTime = 0;
         }

         this.drinkTime++;
      } else {
         this.drinkTime = 0;
      }

      this.prevFlyProgress = this.flyProgress;
      this.prevShootProgress = this.shootProgress;
      this.prevFlying = this.isFlying();
      if (this.isSick()) {
         this.sickTicks++;
         if (this.getTarget() != null && !this.isPassenger()) {
            this.setTarget(null);
         }

         if (this.sickTicks > 100) {
            this.setShrink(false);
            this.setMosquitoScale(this.getMosquitoScale() + 0.015F);
            if (this.sickTicks > 160) {
               EntityWarpedMosco mosco = AMCompat.create(AMEntityRegistry.WARPED_MOSCO.get(), this.level());
               mosco.copyPosition(this);
               if (!this.level().isClientSide()) {
                  mosco.finalizeSpawn(
                     (ServerLevelAccessor)this.level(), AMCompat.difficultyAt(this.level(), this.blockPosition()), MobSpawnType.CONVERSION, null
                  );
               }

               if (!this.level().isClientSide()) {
                  this.level().broadcastEntityEvent(this, (byte)79);
                  this.level().addFreshEntity(mosco);
               }

               this.remove(RemovalReason.DISCARDED);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 79) {
         for (int i = 0; i < 27; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(ParticleTypes.EXPLOSION, this.getRandomX(1.6), this.getY() + this.random.nextFloat() * 3.4F, this.getRandomZ(1.6), d0, d1, d2);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isFlying() ? FLIGHT_SIZE : super.getDefaultDimensions(poseIn);
   }

   public void travel(Vec3 vec3d) {
      if (this.onGround() && !this.isFlying()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (item == AMItemRegistry.WARPED_MIXTURE.get() && !this.isSick()) {
         AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(itemstack));
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         this.setSick(true);
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   private BlockPos getGroundPosition(BlockPos radialPos) {
      while (radialPos.getY() > 1 && this.level().isEmptyBlock(radialPos)) {
         radialPos = radialPos.below();
      }

      return radialPos;
   }

   public boolean isNonMungusWarpedTrigger(Entity entity) {
      ResourceLocation mobtype = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
      return mobtype != null && !AMConfig.warpedMoscoMobTriggers.isEmpty() && AMConfig.warpedMoscoMobTriggers.contains(mobtype.toString());
   }

   public static class FlyAwayFromTarget extends Goal {
      private final EntityCrimsonMosquito parentEntity;
      private int spitCooldown = 0;
      private BlockPos shootPos = null;

      public FlyAwayFromTarget(EntityCrimsonMosquito mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (!this.parentEntity.isFlying()
            || this.parentEntity.getBloodLevel() <= 0 && this.parentEntity.drinkTime >= 0
            || this.parentEntity.getFleeingEntityId() != -1) {
            return false;
         } else if (!this.parentEntity.isPassenger() && this.parentEntity.getTarget() != null) {
            this.shootPos = this.getBlockInTargetsViewMosquito(this.parentEntity.getTarget());
            return true;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.parentEntity.getTarget() != null
            && (this.parentEntity.getBloodLevel() > 0 || this.parentEntity.drinkTime < 0)
            && this.parentEntity.isFlying()
            && !this.parentEntity.horizontalCollision;
      }

      public void stop() {
         this.spitCooldown = 20;
      }

      public void tick() {
         if (this.spitCooldown > 0) {
            this.spitCooldown--;
         }

         if (this.parentEntity.getTarget() != null) {
            if (this.shootPos == null) {
               this.shootPos = this.getBlockInTargetsViewMosquito(this.parentEntity.getTarget());
            } else {
               this.parentEntity.getMoveControl().setWantedPosition(this.shootPos.getX() + 0.5, this.shootPos.getY() + 0.5, this.shootPos.getZ() + 0.5, 1.0);
               this.parentEntity.lookAt(this.parentEntity.getTarget(), 30.0F, 30.0F);
               if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.shootPos)) < 2.5) {
                  if (this.spitCooldown == 0 && this.parentEntity.getBloodLevel() > 0) {
                     this.parentEntity.setupShooting();
                     this.spitCooldown = 20;
                  }

                  this.shootPos = null;
               }
            }
         }
      }

      public BlockPos getBlockInTargetsViewMosquito(LivingEntity target) {
         float radius = 4 + this.parentEntity.getRandom().nextInt(5);
         float angle = 0.017453292F * (target.yHeadRot + 90.0F + this.parentEntity.getRandom().nextInt(180));
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos ground = AMBlockPos.fromCoords(target.getX() + extraX, target.getY() + 1.0, target.getZ() + extraZ);
         return this.parentEntity.distanceToSqr(Vec3.atCenterOf(ground)) > 30.0
               && !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(ground))
               && this.parentEntity.distanceToSqr(Vec3.atCenterOf(ground)) > 6.0
            ? ground
            : this.parentEntity.blockPosition();
      }
   }

   public static class FlyTowardsTarget extends Goal {
      private final EntityCrimsonMosquito parentEntity;

      public FlyTowardsTarget(EntityCrimsonMosquito mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.parentEntity.isFlying()
               && this.parentEntity.getBloodLevel() <= 0
               && this.parentEntity.drinkTime >= 0
               && this.parentEntity.getFleeingEntityId() == -1
            ? !this.parentEntity.isPassenger() && this.parentEntity.getTarget() != null && !this.isBittenByMosquito(this.parentEntity.getTarget())
            : false;
      }

      public boolean canContinueToUse() {
         return this.parentEntity.drinkTime >= 0
            && this.parentEntity.getFleeingEntityId() == -1
            && this.parentEntity.getTarget() != null
            && !this.isBittenByMosquito(this.parentEntity.getTarget())
            && !this.parentEntity.horizontalCollision
            && this.parentEntity.getBloodLevel() == 0
            && this.parentEntity.isFlying()
            && this.parentEntity.getMoveControl().hasWanted();
      }

      public boolean isBittenByMosquito(Entity entity) {
         for (Entity e : entity.getPassengers()) {
            if (e instanceof EntityCrimsonMosquito) {
               return true;
            }
         }

         return false;
      }

      public void stop() {
      }

      public void tick() {
         if (this.parentEntity.getTarget() != null) {
            this.parentEntity
               .getMoveControl()
               .setWantedPosition(this.parentEntity.getTarget().getX(), this.parentEntity.getTarget().getY(), this.parentEntity.getTarget().getZ(), 1.0);
            if (this.parentEntity
                  .getBoundingBox()
                  .inflate(0.30000001192092896, 0.30000001192092896, 0.30000001192092896)
                  .intersects(this.parentEntity.getTarget().getBoundingBox())
               && !this.isBittenByMosquito(this.parentEntity.getTarget())
               && this.parentEntity.drinkTime == 0) {
               AMCompat.startRiding(this.parentEntity, this.parentEntity.getTarget(), true);
               if (!this.parentEntity.level().isClientSide()) {
                  AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.parentEntity.getId(), this.parentEntity.getTarget().getId()));
               }
            }
         }
      }
   }

   static class MoveHelperController extends MoveControl {
      private final EntityCrimsonMosquito parentEntity;

      public MoveHelperController(EntityCrimsonMosquito sunbird) {
         super(sunbird);
         this.parentEntity = sunbird;
      }

      public void tick() {
         if (this.speedModifier >= 1.0 && this.parentEntity.isSick()) {
            this.speedModifier = 0.35;
         }

         if (this.parentEntity.isFlying()) {
            if (this.operation == Operation.STRAFE) {
               Vec3 vector3d = new Vec3(
                  this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ()
               );
               double d0 = vector3d.length();
               this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(0.0, vector3d.scale(this.speedModifier * 0.05 / d0).y(), 0.0));
               float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
               float f1 = (float)this.speedModifier * f;
               this.strafeForwards = 1.0F;
               this.strafeRight = 0.0F;
               this.mob.setSpeed(f1);
               this.mob.setZza(this.strafeForwards);
               this.mob.setXxa(this.strafeRight);
               this.operation = Operation.WAIT;
            } else if (this.operation == Operation.MOVE_TO) {
               Vec3 vector3d = new Vec3(
                  this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ()
               );
               double d0 = vector3d.length();
               if (d0 < this.parentEntity.getBoundingBox().getSize()) {
                  this.operation = Operation.WAIT;
                  this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
               } else {
                  this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05 / d0)));
                  if (this.parentEntity.getTarget() == null) {
                     Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
                     this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
                     this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                  } else {
                     double d2 = this.parentEntity.getTarget().getX() - this.parentEntity.getX();
                     double d1 = this.parentEntity.getTarget().getZ() - this.parentEntity.getZ();
                     this.parentEntity.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
                     this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                  }
               }
            }
         } else {
            this.operation = Operation.WAIT;
            this.mob.setSpeed(0.0F);
            this.mob.setZza(0.0F);
            this.mob.setXxa(0.0F);
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

   static class RandomFlyGoal extends Goal {
      private final EntityCrimsonMosquito parentEntity;
      private BlockPos target = null;

      public RandomFlyGoal(EntityCrimsonMosquito mosquito) {
         this.parentEntity = mosquito;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl movementcontroller = this.parentEntity.getMoveControl();
         if (!this.parentEntity.isFlying()
            || this.parentEntity.getTarget() != null
            || this.parentEntity.hasLuringLaviathan()
            || this.parentEntity.getFleeingEntityId() != -1) {
            return false;
         } else if (movementcontroller.hasWanted() && this.target != null) {
            return false;
         } else {
            this.target = this.getBlockInViewMosquito();
            if (this.target != null) {
               this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            }

            return true;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null
            && this.parentEntity.isFlying()
            && this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4
            && this.parentEntity.getMoveControl().hasWanted()
            && !this.parentEntity.horizontalCollision;
      }

      public void stop() {
         this.target = null;
      }

      public void tick() {
         if (this.target == null) {
            this.target = this.getBlockInViewMosquito();
         }

         if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5) {
               this.target = null;
            }
         }
      }

      public BlockPos getBlockInViewMosquito() {
         float radius = 1 + this.parentEntity.getRandom().nextInt(5);
         float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.yBodyRot;
         float angle = 0.017453292F * renderYawOffset + 3.15F + this.parentEntity.getRandom().nextFloat() * neg;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, this.parentEntity.getY() + 2.0, this.parentEntity.getZ() + extraZ);
         BlockPos ground = this.parentEntity.getGroundPosition(radialPos);
         int up = this.parentEntity.isSick() ? 2 : 6;
         BlockPos newPos = ground.above(1 + this.parentEntity.getRandom().nextInt(up));
         return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
      }
   }
}
