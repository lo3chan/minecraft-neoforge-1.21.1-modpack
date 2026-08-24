package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.procedures.FlameOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.FlameOnInitialEntitySpawnProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class FlameEntity extends PathfinderMob {
   public FlameEntity(EntityType<FlameEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public Vec3 getPassengerRidingPosition(Entity entity) {
      return super.getPassengerRidingPosition(entity).add(0.0, -0.3499999940395355, 0.0);
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.ambient"));
   }

   public boolean hurt(DamageSource damagesource, float amount) {
      return damagesource.is(DamageTypes.IN_FIRE) ? false : super.hurt(damagesource, amount);
   }

   public boolean fireImmune() {
      return true;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      FlameOnInitialEntitySpawnProcedure.execute(world, this);
      return retval;
   }

   public void baseTick() {
      super.baseTick();
      FlameOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entityIn) {
   }

   protected void pushEntities() {
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 100.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 16.0);
      return builder.add(Attributes.STEP_HEIGHT, 0.6);
   }
}
