package net.mcreator.undeadrevamp.entity;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.NecroconProcedure;
import net.mcreator.undeadrevamp.procedures.NecrothOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.NeocrorinesNaturalEntitySpawningConditionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.fluids.FluidType;

public class NeocrorinesEntity extends Monster {
   public NeocrorinesEntity(EntityType<NeocrorinesEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.moveControl = new FlyingMoveControl(this, 10, true);
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector
         .addGoal(
            1,
            new MeleeAttackGoal(this, 1.2, false) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }

               public boolean canUse() {
                  double x = NeocrorinesEntity.this.getX();
                  double y = NeocrorinesEntity.this.getY();
                  double z = NeocrorinesEntity.this.getZ();
                  Entity entity = NeocrorinesEntity.this;
                  Level world = NeocrorinesEntity.this.level();
                  return super.canUse() && NecroconProcedure.execute(entity);
               }

               public boolean canContinueToUse() {
                  double x = NeocrorinesEntity.this.getX();
                  double y = NeocrorinesEntity.this.getY();
                  double z = NeocrorinesEntity.this.getZ();
                  Entity entity = NeocrorinesEntity.this;
                  Level world = NeocrorinesEntity.this.level();
                  return super.canContinueToUse() && NecroconProcedure.execute(entity);
               }
            }
         );
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
      this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8, 20) {
         protected Vec3 getPosition() {
            RandomSource random = NeocrorinesEntity.this.getRandom();
            double dir_x = NeocrorinesEntity.this.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_y = NeocrorinesEntity.this.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dir_z = NeocrorinesEntity.this.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            return new Vec3(dir_x, dir_y, dir_z);
         }

         public boolean canUse() {
            double x = NeocrorinesEntity.this.getX();
            double y = NeocrorinesEntity.this.getY();
            double z = NeocrorinesEntity.this.getZ();
            Entity entity = NeocrorinesEntity.this;
            Level world = NeocrorinesEntity.this.level();
            return super.canUse() && NecroconProcedure.execute(entity);
         }

         public boolean canContinueToUse() {
            double x = NeocrorinesEntity.this.getX();
            double y = NeocrorinesEntity.this.getY();
            double z = NeocrorinesEntity.this.getZ();
            Entity entity = NeocrorinesEntity.this;
            Level world = NeocrorinesEntity.this.level();
            return super.canContinueToUse() && NecroconProcedure.execute(entity);
         }
      });
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:necrorinesambt"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.ambient")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.hurt"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:deathdying"));
   }

   public boolean causeFallDamage(float l, float d, DamageSource source) {
      return false;
   }

   public boolean hurt(DamageSource damagesource, float amount) {
      if (damagesource.is(DamageTypes.IN_FIRE)) {
         return false;
      } else if (damagesource.is(DamageTypes.FALL)) {
         return false;
      } else if (damagesource.is(DamageTypes.CACTUS)) {
         return false;
      } else if (damagesource.is(DamageTypes.LIGHTNING_BOLT)) {
         return false;
      } else {
         return damagesource.is(DamageTypes.DRAGON_BREATH) ? false : super.hurt(damagesource, amount);
      }
   }

   public boolean fireImmune() {
      return true;
   }

   public void baseTick() {
      super.baseTick();
      NecrothOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
   }

   public boolean canDrownInFluidType(FluidType type) {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      Level world = this.level();
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void setNoGravity(boolean ignored) {
      super.setNoGravity(true);
   }

   public void aiStep() {
      super.aiStep();
      this.setNoGravity(true);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)UndeadRevamp2ModEntities.NEOCRORINES.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return NeocrorinesNaturalEntitySpawningConditionProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static Builder createAttributes() {
      Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 8.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 0.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 25.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 100.0);
      return builder.add(Attributes.FLYING_SPEED, 0.3);
   }
}
