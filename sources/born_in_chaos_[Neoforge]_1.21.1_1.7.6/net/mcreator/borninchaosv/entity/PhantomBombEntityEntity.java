package net.mcreator.borninchaosv.entity;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.procedures.PhantomBombEntityPriNachalnomPrizyvieSushchnostiProcedure;
import net.mcreator.borninchaosv.procedures.PhantomBombEntityPriObnovlieniiTikaSushchnostiProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class PhantomBombEntityEntity extends Monster {
   public PhantomBombEntityEntity(EntityType<PhantomBombEntityEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new FloatGoal(this));
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:fly_move"));
   }

   public boolean hurt(DamageSource damagesource, float amount) {
      if (damagesource.is(DamageTypes.IN_FIRE)) {
         return false;
      } else if (damagesource.getDirectEntity() instanceof AbstractArrow) {
         return false;
      } else if (damagesource.getDirectEntity() instanceof Player) {
         return false;
      } else if (damagesource.getDirectEntity() instanceof ThrownPotion
         || damagesource.getDirectEntity() instanceof AreaEffectCloud
         || damagesource.typeHolder().is(NeoForgeMod.POISON_DAMAGE)) {
         return false;
      } else if (damagesource.is(DamageTypes.FALL)) {
         return false;
      } else if (damagesource.is(DamageTypes.CACTUS)) {
         return false;
      } else if (damagesource.is(DamageTypes.DROWN)) {
         return false;
      } else if (damagesource.is(DamageTypes.LIGHTNING_BOLT)) {
         return false;
      } else if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION)) {
         return false;
      } else if (damagesource.is(DamageTypes.TRIDENT)) {
         return false;
      } else if (damagesource.is(DamageTypes.FALLING_ANVIL)) {
         return false;
      } else if (damagesource.is(DamageTypes.DRAGON_BREATH)) {
         return false;
      } else {
         return !damagesource.is(DamageTypes.WITHER) && !damagesource.is(DamageTypes.WITHER_SKULL) ? super.hurt(damagesource, amount) : false;
      }
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }

   public boolean fireImmune() {
      return true;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
      SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
      PhantomBombEntityPriNachalnomPrizyvieSushchnostiProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
      return retval;
   }

   public void baseTick() {
      super.baseTick();
      PhantomBombEntityPriObnovlieniiTikaSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.1);
      builder = builder.add(Attributes.MAX_HEALTH, 15.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 1.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 5.0);
      return builder.add(Attributes.STEP_HEIGHT, 0.1);
   }
}
