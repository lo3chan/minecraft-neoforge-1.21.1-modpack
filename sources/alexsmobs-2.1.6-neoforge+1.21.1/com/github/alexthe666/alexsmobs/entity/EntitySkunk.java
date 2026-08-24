package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntitySkunk extends Animal {
   public float prevSprayProgress;
   public float sprayProgress;
   private int prevSprayTime = 0;
   private int harassedTime;
   private int sprayCooldown;
   private Vec3 sprayAt;
   private static final EntityDataAccessor<Integer> SPRAY_TIME = SynchedEntityData.defineId(EntitySkunk.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> SPRAY_YAW = SynchedEntityData.defineId(EntitySkunk.class, EntityDataSerializers.FLOAT);

   protected EntitySkunk(EntityType<? extends Animal> type, Level level) {
      super(type, level);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPRAY_YAW, 0.0F);
      builder.define(SPRAY_TIME, 0);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntitySkunk.SprayGoal());
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.5) {
         public void tick() {
            super.tick();
            EntitySkunk.this.harassedTime += 10;
         }
      });
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.SKUNK_BREEDABLES), false));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.goalSelector
         .addGoal(
            3,
            new AvoidEntityGoal(
               this,
               LivingEntity.class,
               AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.SKUNK_FEARS),
               10.0F,
               1.3,
               1.1,
               EntitySelector.NO_CREATIVE_OR_SPECTATOR
            ) {
               public boolean canUse() {
                  return super.canUse() && EntitySkunk.this.getSprayTime() <= 0;
               }

               public boolean canContinueToUse() {
                  return super.canContinueToUse() && EntitySkunk.this.getSprayTime() <= 0;
               }

               public void tick() {
                  super.tick();
                  if (this.toAvoid != null) {
                     EntitySkunk.this.sprayAt = this.toAvoid.position();
                  }

                  EntitySkunk.this.harassedTime += 4;
               }
            }
         );
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.skunkSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.SKUNK_BREEDABLES);
   }

   public float getSprayYaw() {
      return (Float)this.entityData.get(SPRAY_YAW);
   }

   public void setSprayYaw(float yaw) {
      this.entityData.set(SPRAY_YAW, yaw);
   }

   public int getSprayTime() {
      return (Integer)this.entityData.get(SPRAY_TIME);
   }

   public void setSprayTime(int time) {
      this.entityData.set(SPRAY_TIME, time);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SKUNK_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SKUNK_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SKUNK_HURT.get();
   }

   public void tick() {
      super.tick();
      this.prevSprayProgress = this.sprayProgress;
      if (this.getSprayTime() > 0) {
         if (this.sprayProgress < 5.0F) {
            this.sprayProgress++;
         }

         this.setSprayTime(this.getSprayTime() - 1);
         if (this.getSprayTime() == 0) {
            this.spawnLingeringCloud();
         } else if (this.getSprayTime() % 6 == 0) {
            this.playSound(AMSoundRegistry.SKUNK_SPRAY.get());
         }

         this.yBodyRot = this.getYRot();
         this.setYRot(this.approachRotation(this.getSprayYaw(), this.getYRot() + 10.0F, 15.0F));
      }

      if (this.getSprayTime() <= 0 && this.sprayProgress > 0.0F) {
         this.sprayProgress--;
      }

      if (!this.level().isClientSide()) {
         if (this.harassedTime > 200 && this.sprayCooldown == 0 && !this.isBaby()) {
            this.harassedTime = 0;
            this.sprayCooldown = 200 + this.random.nextInt(200);
            this.setSprayTime(60 + this.random.nextInt(60));
         }

         if (this.harassedTime > 0) {
            this.harassedTime--;
         }

         if (this.sprayCooldown > 0) {
            this.sprayCooldown--;
         }

         Entity lastHurt = this.getLastHurtByMob();
         if (lastHurt != null) {
            this.sprayAt = lastHurt.position();
         }
      }

      this.prevSprayTime = this.getSprayTime();
   }

   private void spawnLingeringCloud() {
      Collection<MobEffectInstance> collection = this.getActiveEffects();
      if (!collection.isEmpty()) {
         float fartDistance = 2.5F;
         Vec3 modelBack = new Vec3(0.0, 0.4000000059604645, -2.5).xRot(-this.getXRot() * 0.017453292F).yRot(-this.getYRot() * 0.017453292F);
         Vec3 fartAt = this.position().add(modelBack);
         AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), fartAt.x, fartAt.y, fartAt.z);
         areaeffectcloud.setRadius(2.5F);
         areaeffectcloud.setRadiusOnUse(-0.25F);
         areaeffectcloud.setWaitTime(20);
         areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
         areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / areaeffectcloud.getDuration());

         for (MobEffectInstance mobeffectinstance : collection) {
            areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
         }

         this.level().addFreshEntity(areaeffectcloud);
      }
   }

   public void handleEntityEvent(byte id) {
      if (id == 48) {
         Vec3 modelBack = new Vec3(0.0, 0.4000000059604645, -0.4000000059604645).xRot(-this.getXRot() * 0.017453292F).yRot(-this.getYRot() * 0.017453292F);
         Vec3 particleFrom = this.position().add(modelBack);
         float scale = this.random.nextFloat() * 0.5F + 1.0F;
         Vec3 particleTo = modelBack.multiply(scale, 1.0, scale);

         for (int i = 0; i < 3; i++) {
            double d0 = this.random.nextGaussian() * 0.1;
            double d1 = this.random.nextGaussian() * 0.1;
            double d2 = this.random.nextGaussian() * 0.1;
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.SMELLY.get(),
                  particleFrom.x,
                  particleFrom.y,
                  particleFrom.z,
                  particleTo.x + d0,
                  particleTo.y - 0.4000000059604645 + d1,
                  particleTo.z + d2
               );
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   private float approachRotation(float current, float target, float max) {
      float f = Mth.wrapDegrees(target - current);
      if (f > max) {
         f = max;
      }

      if (f < -max) {
         f = -max;
      }

      return Mth.wrapDegrees(current + f);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
      return AMCompat.create(AMEntityRegistry.SKUNK.get(), this.level());
   }

   private class SprayGoal extends Goal {
      private int actualSprayTime = 0;

      public SprayGoal() {
         this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
      }

      public boolean canUse() {
         return EntitySkunk.this.getSprayTime() > 0;
      }

      public void stop() {
         this.actualSprayTime = 0;
      }

      public void tick() {
         EntitySkunk.this.getNavigation().stop();
         Vec3 sprayAt = this.getSprayAt();
         double d0 = EntitySkunk.this.getX() - sprayAt.x;
         double d2 = EntitySkunk.this.getZ() - sprayAt.z;
         float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
         EntitySkunk.this.setSprayYaw(f);
         if (EntitySkunk.this.sprayProgress >= 5.0F) {
            EntitySkunk.this.level().broadcastEntityEvent(EntitySkunk.this, (byte)48);
            if (this.actualSprayTime > 10 && EntitySkunk.this.random.nextInt(2) == 0) {
               Vec3 skunkPos = new Vec3(EntitySkunk.this.getX(), EntitySkunk.this.getEyeY(), EntitySkunk.this.getZ());
               float xAdd = EntitySkunk.this.random.nextFloat() * 20.0F - 10.0F;
               float yAdd = EntitySkunk.this.random.nextFloat() * 20.0F - 10.0F;
               float maxSprayDist = 5.0F;
               Vec3 modelBack = new Vec3(0.0, 0.0, -5.0)
                  .xRot((xAdd - EntitySkunk.this.getXRot()) * 0.017453292F)
                  .yRot((yAdd - EntitySkunk.this.getYRot()) * 0.017453292F);
               HitResult hitResult = EntitySkunk.this.level()
                  .clip(new ClipContext(skunkPos, skunkPos.add(modelBack), Block.COLLIDER, Fluid.NONE, EntitySkunk.this));
               if (hitResult != null) {
                  BlockPos pos;
                  Direction dir;
                  if (hitResult instanceof BlockHitResult block) {
                     pos = block.getBlockPos().relative(block.getDirection());
                     dir = block.getDirection().getOpposite();
                  } else {
                     pos = AMBlockPos.fromVec3(hitResult.getLocation());
                     dir = Direction.UP;
                  }

                  BlockState currentState = EntitySkunk.this.level().getBlockState(pos);
                  BlockState sprayState = ((MultifaceBlock)AMBlockRegistry.SKUNK_SPRAY.get())
                     .getStateForPlacement(EntitySkunk.this.level().getBlockState(pos), EntitySkunk.this.level(), pos, dir);
                  if ((currentState.isAir() || currentState.canBeReplaced()) && sprayState != null && sprayState.is(AMBlockRegistry.SKUNK_SPRAY.get())) {
                     EntitySkunk.this.level().setBlockAndUpdate(pos, sprayState);
                  }

                  double sprayDist = hitResult.getLocation().subtract(skunkPos).length() / 5.0;
                  AABB poisonBox = new AABB(skunkPos, skunkPos.add(modelBack.scale(sprayDist)).add(0.0, 1.5, 0.0)).inflate(1.0);
                  Collection<MobEffectInstance> collection = EntitySkunk.this.getActiveEffects();

                  for (LivingEntity entity : EntitySkunk.this.level().getEntitiesOfClass(LivingEntity.class, poisonBox)) {
                     if (!(entity instanceof EntitySkunk)) {
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300));
                        if (entity instanceof ServerPlayer serverPlayer) {
                           AMAdvancementTriggerRegistry.SKUNK_SPRAY.trigger(serverPlayer);
                        }

                        for (MobEffectInstance mobeffectinstance : collection) {
                           entity.addEffect(new MobEffectInstance(mobeffectinstance));
                        }
                     }
                  }
               }
            }

            this.actualSprayTime++;
         }
      }

      private Vec3 getSprayAt() {
         Entity last = EntitySkunk.this.getLastHurtByMob();
         if (EntitySkunk.this.sprayAt != null) {
            return EntitySkunk.this.sprayAt;
         } else if (last != null) {
            return last.position();
         } else {
            Vec3 modelBack = new Vec3(0.0, 0.4000000059604645, -1.0)
               .xRot(-EntitySkunk.this.getXRot() * 0.017453292F)
               .yRot(-EntitySkunk.this.getYRot() * 0.017453292F);
            return EntitySkunk.this.position().add(modelBack);
         }
      }
   }
}
