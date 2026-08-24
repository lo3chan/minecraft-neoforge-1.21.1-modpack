package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.procedures.SmokesmitterOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.SmokesmitterOnInitialEntitySpawnProcedure;
import net.minecraft.world.DifficultyInstance;
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

public class SmokesmitterEntity extends PathfinderMob {
   public SmokesmitterEntity(EntityType<SmokesmitterEntity> type, Level world) {
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

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      SmokesmitterOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
      return retval;
   }

   public void baseTick() {
      super.baseTick();
      SmokesmitterOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
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
