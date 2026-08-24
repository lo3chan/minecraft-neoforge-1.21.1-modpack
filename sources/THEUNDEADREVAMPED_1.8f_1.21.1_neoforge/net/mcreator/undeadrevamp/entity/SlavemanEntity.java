package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.procedures.SlavemanEntityDiesProcedure;
import net.mcreator.undeadrevamp.procedures.SlavemanEntityIsHurtProcedure;
import net.mcreator.undeadrevamp.procedures.SlavemanOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.SlavemanOnInitialEntitySpawnProcedure;
import net.mcreator.undeadrevamp.procedures.ThebeartamerNaturalEntitySpawningConditionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

public class SlavemanEntity extends Monster {
   public SlavemanEntity(EntityType<SlavemanEntity> type, Level world) {
      super(type, world);
      this.xpReward = 5;
      this.setNoAi(false);
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_HELMET.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_CHESTPLATE.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_BOOTS.get()));
   }

   protected void registerGoals() {
      super.registerGoals();
      this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);
      this.goalSelector
         .addGoal(
            1,
            new MeleeAttackGoal(this, 1.2, true) {
               protected boolean canPerformAttack(LivingEntity entity) {
                  return this.isTimeToAttack()
                     && this.mob.distanceToSqr(entity) < this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()
                     && this.mob.getSensing().hasLineOfSight(entity);
               }
            }
         );
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.5F));
      this.goalSelector.addGoal(4, new FollowMobGoal(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
      this.goalSelector.addGoal(6, new OpenDoorGoal(this, false));
      this.goalSelector.addGoal(7, new PanicGoal(this, 1.2));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(9, new NearestAttackableTargetGoal(this, Pillager.class, false, false));
      this.targetSelector.addGoal(10, new NearestAttackableTargetGoal(this, Evoker.class, false, false));
      this.targetSelector.addGoal(11, new NearestAttackableTargetGoal(this, Vindicator.class, false, false));
      this.targetSelector.addGoal(12, new NearestAttackableTargetGoal(this, Ravager.class, false, false));
      this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, Villager.class, false, false));
      this.targetSelector.addGoal(15, new NearestAttackableTargetGoal(this, IronGolem.class, false, false));
   }

   public Vec3 getPassengerRidingPosition(Entity entity) {
      return super.getPassengerRidingPosition(entity).add(0.0, -0.3499999940395355, 0.0);
   }

   public SoundEvent getAmbientSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.husk.ambient"));
   }

   public void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.husk.step")), 0.15F, 1.0F);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:royalhurts"));
   }

   public SoundEvent getDeathSound() {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:royaldies"));
   }

   public boolean hurt(DamageSource damagesource, float amount) {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      Level world = this.level();
      Entity sourceentity = damagesource.getEntity();
      Entity immediatesourceentity = damagesource.getDirectEntity();
      SlavemanEntityIsHurtProcedure.execute(world, x, y, z);
      if (damagesource.getDirectEntity() instanceof AbstractArrow) {
         return false;
      } else {
         return damagesource.is(DamageTypes.FALL) ? false : super.hurt(damagesource, amount);
      }
   }

   public void die(DamageSource source) {
      super.die(source);
      SlavemanEntityDiesProcedure.execute(this, source.getEntity());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      SlavemanOnInitialEntitySpawnProcedure.execute(world, this.getX(), this.getY(), this.getZ());
      return retval;
   }

   public void baseTick() {
      super.baseTick();
      SlavemanOnEntityTickUpdateProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
   }

   public boolean canCollideWith(Entity entity) {
      return true;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)UndeadRevamp2ModEntities.SLAVEMAN.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, world, reason, pos, random) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return ThebeartamerNaturalEntitySpawningConditionProcedure.execute(world, x, y, z);
         },
         Operation.REPLACE
      );
   }

   public static Builder createAttributes() {
      Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
      builder = builder.add(Attributes.MAX_HEALTH, 26.0);
      builder = builder.add(Attributes.ARMOR, 15.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 128.0);
      builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
      return builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
   }
}
