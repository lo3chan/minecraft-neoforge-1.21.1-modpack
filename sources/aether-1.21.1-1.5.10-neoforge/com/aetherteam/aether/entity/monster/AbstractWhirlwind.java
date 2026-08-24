package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractWhirlwind extends Mob {
   public static final EntityDataAccessor<Integer> DATA_COLOR_ID = SynchedEntityData.defineId(AbstractWhirlwind.class, EntityDataSerializers.INT);
   private int lifeLeft;
   private int dropsTimer;
   private int stuckTick;
   private float movementAngle;
   private float movementCurve;
   private boolean isEvil = false;

   public AbstractWhirlwind(EntityType<? extends AbstractWhirlwind> type, Level level) {
      super(type, level);
      if (level.isClientSide()) {
         this.movementAngle = this.getRandom().nextFloat() * 360.0F;
         this.movementCurve = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.1F;
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new AbstractWhirlwind.MoveGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.025).add(Attributes.FOLLOW_RANGE, 16.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_COLOR_ID, this.getDefaultColor());
   }

   public static boolean checkWhirlwindSpawnRules(
      EntityType<? extends AbstractWhirlwind> whirlwind, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return Mob.checkMobSpawnRules(whirlwind, level, reason, pos, random)
         && level.getRawBrightness(pos, 0) > 12
         && level.getDifficulty() != Difficulty.PEACEFUL;
   }

   public void tick() {
      super.tick();
      this.lifeLeft--;
      if (!this.level().isClientSide() && (this.lifeLeft <= 0 || this.isInFluidType())) {
         this.discard();
      }
   }

   public void aiStep() {
      if (!this.level().isClientSide()) {
         if (this.verticalCollision && !this.verticalCollisionBelow) {
            this.stuckTick += 4;
         } else if (this.stuckTick > 0) {
            this.stuckTick--;
         }

         if (this.getTarget() != null) {
            this.dropsTimer++;
         }

         if (this.dropsTimer >= 128) {
            this.spawnDrops();
            this.dropsTimer = 0;
         }
      } else {
         this.spawnParticles();
      }

      super.aiStep();

      for (Entity entity : this.level()
         .getEntities(this, this.getBoundingBox().expandTowards(2.5, 2.5, 2.5))
         .stream()
         .filter(entityx -> !entityx.getType().is(AetherTags.Entities.WHIRLWIND_UNAFFECTED))
         .toList()) {
         double x = (float)entity.getX();
         double y = (float)entity.getY() - entity.getPassengerRidingPosition(this).y() * 0.6000000238418579;
         double z = (float)entity.getZ();
         double distance = this.distanceTo(entity);
         double d1 = y - this.getY();
         if (distance <= 1.5 + d1) {
            entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.15, entity.getDeltaMovement().z());
            entity.resetFallDistance();
            if (d1 > 1.5) {
               entity.setDeltaMovement(entity.getDeltaMovement().x(), -0.45 + d1 * 0.35, entity.getDeltaMovement().z());
               distance += d1 * 1.5;
            } else {
               entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.125, entity.getDeltaMovement().z());
            }

            double d2 = Math.atan2(this.getX() - x, this.getZ() - z) / 0.0175;
            d2 += 160.0;
            entity.setDeltaMovement(
               -Math.cos(0.0175 * d2) * (distance + 0.25) * 0.1, entity.getDeltaMovement().y, Math.sin(0.0175 * d2) * (distance + 0.25) * 0.1
            );
            if (entity instanceof AbstractWhirlwind) {
               entity.discard();
            }
         } else {
            double d3 = Math.atan2(this.getX() - x, this.getZ() - z) / 0.0175;
            entity.setDeltaMovement(entity.getDeltaMovement().add(Math.sin(0.0175 * d3) * 0.01, entity.getDeltaMovement().y, Math.cos(0.0175 * d3) * 0.01));
         }

         if (!this.level().isEmptyBlock(this.blockPosition())) {
            this.lifeLeft -= 50;
         }
      }

      if (this.stuckTick > 40) {
         this.lifeLeft = 0;
      }
   }

   protected void spawnDrops() {
      if (this.level() instanceof ServerLevel serverLevel && this.getRandom().nextInt(4) == 0) {
         LootParams parameters = new net.minecraft.world.level.storage.loot.LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, this.position())
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .create(LootContextParamSets.SELECTOR);
         LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(this.getLootLocation());

         for (ItemStack itemstack : lootTable.getRandomItems(parameters)) {
            serverLevel.playSound(null, this.blockPosition(), (SoundEvent)AetherSoundEvents.ENTITY_WHIRLWIND_DROP.get(), SoundSource.HOSTILE, 0.5F, 1.0F);
            this.spawnAtLocation(itemstack, 1.0F);
         }
      }
   }

   public boolean hurt(DamageSource source, float damage) {
      return false;
   }

   public void kill() {
      this.remove(RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public abstract void spawnParticles();

   public abstract ResourceKey<LootTable> getLootLocation();

   public int getColorData() {
      return (Integer)this.getEntityData().get(DATA_COLOR_ID);
   }

   public void setColorData(int color) {
      this.getEntityData().set(DATA_COLOR_ID, color);
   }

   public int getLifeLeft() {
      return this.lifeLeft;
   }

   public void setLifeLeft(int lifeLeft) {
      this.lifeLeft = lifeLeft;
   }

   public boolean isEvil() {
      return this.isEvil;
   }

   public void setEvil(boolean evil) {
      this.isEvil = evil;
   }

   public abstract int getDefaultColor();

   public boolean onClimbable() {
      return this.horizontalCollision;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected boolean canRide(Entity vehicle) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("Movement Angle", this.movementAngle);
      tag.putFloat("Movement Curve", this.movementCurve);
      tag.putInt("Life Left", this.lifeLeft);
      tag.putInt("Color", this.getColorData());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Movement Angle")) {
         this.movementAngle = tag.getFloat("Movement Angle");
      }

      if (tag.contains("Movement Curve")) {
         this.movementCurve = tag.getFloat("Movement Curve");
      }

      if (tag.contains("Life Left")) {
         this.lifeLeft = tag.getInt("Life Left");
      }

      if (tag.contains("Color")) {
         this.setColorData(tag.getInt("Color"));
      }
   }

   protected static class MoveGoal extends Goal {
      private final AbstractWhirlwind whirlwind;
      protected float movementAngle;
      protected float movementCurve;

      public MoveGoal(AbstractWhirlwind entity) {
         this.whirlwind = entity;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if (this.movementAngle == 0.0F) {
            this.movementAngle = this.whirlwind.movementAngle;
            this.movementCurve = this.whirlwind.movementCurve;
         }

         if (this.whirlwind.isEvil && this.whirlwind.getTarget() != null) {
            this.whirlwind.setDeltaMovement(Vec3.ZERO);
         } else {
            BlockPos offset = BlockPos.containing(this.whirlwind.position().add(this.whirlwind.getDeltaMovement()));
            if (this.whirlwind.level().getHeight(Types.WORLD_SURFACE, offset.getX(), offset.getZ()) < offset.getY() - this.whirlwind.getMaxFallDistance()) {
               this.movementAngle += 180.0F;
            } else {
               this.movementAngle = this.movementAngle + this.movementCurve;
            }

            double modifier = 1.0;
            AttributeInstance speed = this.whirlwind.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
               modifier = speed.getValue();
            }

            this.whirlwind
               .setDeltaMovement(
                  Math.cos(this.movementAngle * 0.017453292F) * modifier,
                  this.whirlwind.getDeltaMovement().y,
                  Math.sin(this.movementAngle * 0.017453292F) * modifier
               );
         }
      }
   }
}
